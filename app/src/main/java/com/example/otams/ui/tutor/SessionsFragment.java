package com.example.otams.ui.tutor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.otams.R;
import com.example.otams.model.Session;
import com.example.otams.model.SessionStatus;
import com.example.otams.model.Student;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SessionsFragment extends Fragment implements SessionAdapter.SessionActionListener {

    private SessionAdapter upcomingAdapter;
    private SessionAdapter pastAdapter;
    private TextView upcomingEmptyText;
    private TextView pastEmptyText;

    private ListenerRegistration registration;
    private CollectionReference sessionsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sessions, container, false);

        RecyclerView upcomingRecyclerView = view.findViewById(R.id.upcomingSessionsRecyclerView);
        RecyclerView pastRecyclerView = view.findViewById(R.id.pastSessionsRecyclerView);
        upcomingEmptyText = view.findViewById(R.id.upcomingEmptyText);
        pastEmptyText = view.findViewById(R.id.pastEmptyText);

        upcomingAdapter = new SessionAdapter(this);
        pastAdapter = new SessionAdapter(this);

        upcomingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        upcomingRecyclerView.setAdapter(upcomingAdapter);

        pastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pastRecyclerView.setAdapter(pastAdapter);

        sessionsRef = FirebaseFirestore.getInstance().collection("sessions");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeToSessions();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    private void subscribeToSessions() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }

        String tutorId = getCurrentTutorId();
        if (tutorId == null) {
            Toast.makeText(getContext(), R.string.sessions_no_tutor, Toast.LENGTH_SHORT).show();
            upcomingAdapter.submitList(new ArrayList<>());
            pastAdapter.submitList(new ArrayList<>());
            updateEmptyState();
            return;
        }

        registration = sessionsRef.whereEqualTo("tutorId", tutorId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), R.string.sessions_load_error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Session> upcoming = new ArrayList<>();
                    List<Session> past = new ArrayList<>();
                    long now = System.currentTimeMillis();

                    if (snapshots != null) {
                        for (DocumentSnapshot document : snapshots) {
                            Session session = document.toObject(Session.class);
                            if (session == null) {
                                continue;
                            }
                            session.setId(document.getId());

                            if (session.getEndTimeMillis() >= now) {
                                upcoming.add(session);
                            } else {
                                past.add(session);
                            }
                        }
                    }

                    Comparator<Session> comparator = Comparator.comparingLong(Session::getStartTimeMillis);
                    Collections.sort(upcoming, comparator);
                    Collections.sort(past, comparator.reversed());

                    upcomingAdapter.submitList(new ArrayList<>(upcoming));
                    pastAdapter.submitList(new ArrayList<>(past));
                    updateEmptyState();
                });
    }

    private void updateEmptyState() {
        boolean hasUpcoming = upcomingAdapter.getItemCount() > 0;
        boolean hasPast = pastAdapter.getItemCount() > 0;

        upcomingEmptyText.setVisibility(hasUpcoming ? View.GONE : View.VISIBLE);
        pastEmptyText.setVisibility(hasPast ? View.GONE : View.VISIBLE);
    }

    @Nullable
    private String getCurrentTutorId() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onSessionClicked(@NonNull Session session) {
        Student student = session.getStudent();
        StringBuilder messageBuilder = new StringBuilder();

        if (student != null) {
            if (student.getEmail() != null && !student.getEmail().isEmpty()) {
                messageBuilder.append(getString(R.string.sessions_detail_email, student.getEmail())).append('\n');
            }
            if (student.getPhoneNumber() != null && !student.getPhoneNumber().isEmpty()) {
                messageBuilder.append(getString(R.string.sessions_detail_phone, student.getPhoneNumber())).append('\n');
            }
            if (student.getProgram() != null && !student.getProgram().isEmpty()) {
                messageBuilder.append(getString(R.string.sessions_detail_program, student.getProgram())).append('\n');
            }
        }

        if (session.getCourseCode() != null && !session.getCourseCode().isEmpty()) {
            messageBuilder.append(getString(R.string.sessions_detail_course, session.getCourseCode())).append('\n');
        }

        if (messageBuilder.length() == 0) {
            messageBuilder.append(getString(R.string.sessions_detail_no_info));
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.sessions_detail_title)
                .setMessage(messageBuilder.toString().trim())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onApprove(@NonNull Session session) {
        updateSessionStatus(session, SessionStatus.APPROVED, R.string.sessions_approved_toast);
    }

    @Override
    public void onReject(@NonNull Session session) {
        updateSessionStatus(session, SessionStatus.REJECTED, R.string.sessions_rejected_toast);
    }

    @Override
    public void onCancel(@NonNull Session session) {
        updateSessionStatus(session, SessionStatus.CANCELLED, R.string.sessions_cancelled_toast);
    }

    private void updateSessionStatus(Session session, SessionStatus status, int successMessage) {
        String id = session.getId();
        if (id == null) {
            Toast.makeText(getContext(), R.string.sessions_update_error, Toast.LENGTH_SHORT).show();
            return;
        }

        sessionsRef.document(id)
                .update("status", status.getFirestoreValue())
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), R.string.sessions_update_error, Toast.LENGTH_SHORT).show());
    }
}
