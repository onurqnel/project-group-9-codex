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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestsFragment extends Fragment {

    private FirestoreRecyclerAdapter<Session, RequestViewHolder> adapter;
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private final DateFormat dateFormatter = new SimpleDateFormat("EEE, MMM d 'at' h:mm a", Locale.getDefault());
    private final DateFormat timeFormatter = new SimpleDateFormat("h:mm a", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = view.findViewById(R.id.requestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyStateText = view.findViewById(R.id.noRequestsText);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void setupRecyclerView() {
        String tutorId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (tutorId == null) {
            if (emptyStateText != null) {
                emptyStateText.setText(R.string.requests_no_tutor);
                emptyStateText.setVisibility(View.VISIBLE);
            }
            recyclerView.setVisibility(View.GONE);
            recyclerView.setAdapter(null);
            adapter = null;
            return;
        }

        if (emptyStateText != null) {
            emptyStateText.setText(R.string.empty_pending_requests);
        }

        recyclerView.setVisibility(View.VISIBLE);

        Query query = FirebaseFirestore.getInstance().collection("sessions")
                .whereEqualTo("tutorId", tutorId)
                .whereEqualTo("status", SessionStatus.PENDING.getFirestoreValue())
                .orderBy("startTimeMillis", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>()
                .setQuery(query, Session.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Session, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Session session) {
                holder.bind(session, dateFormatter, timeFormatter);
                String sessionId = getSnapshots().getSnapshot(position).getId();
                holder.approveButton.setOnClickListener(v -> updateSessionStatus(sessionId, SessionStatus.APPROVED));
                holder.rejectButton.setOnClickListener(v -> updateSessionStatus(sessionId, SessionStatus.REJECTED));
                holder.itemView.setOnClickListener(v -> showSessionDetails(session));
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_pending_session, parent, false);
                return new RequestViewHolder(itemView);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                updateEmptyState();
            }
        };

        recyclerView.setAdapter(adapter);
        updateEmptyState();
    }

    private void updateSessionStatus(String sessionId, SessionStatus status) {
        int successMessage = status == SessionStatus.APPROVED
                ? R.string.requests_approved
                : R.string.requests_rejected;

        FirebaseFirestore.getInstance().collection("sessions")
                .document(sessionId)
                .update("status", status.getFirestoreValue())
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(),
                        successMessage,
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        R.string.requests_action_error,
                        Toast.LENGTH_SHORT).show());
    }

    private void showSessionDetails(@NonNull Session session) {
        Student student = session.getStudent();
        StringBuilder messageBuilder = new StringBuilder();

        if (student != null) {
            if (!isNullOrEmpty(student.getEmail())) {
                messageBuilder.append(getString(R.string.sessions_detail_email, student.getEmail())).append('\n');
            }
            if (!isNullOrEmpty(student.getPhoneNumber())) {
                messageBuilder.append(getString(R.string.sessions_detail_phone, student.getPhoneNumber())).append('\n');
            }
            if (!isNullOrEmpty(student.getProgram())) {
                messageBuilder.append(getString(R.string.sessions_detail_program, student.getProgram())).append('\n');
            }
        }

        if (!isNullOrEmpty(session.getCourseCode())) {
            messageBuilder.append(getString(R.string.sessions_detail_course, session.getCourseCode())).append('\n');
        }

        Date start = new Date(session.getStartTimeMillis());
        Date end = new Date(session.getEndTimeMillis());
        messageBuilder.append(getString(R.string.session_time_range,
                dateFormatter.format(start),
                timeFormatter.format(end)));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.sessions_detail_title)
                .setMessage(messageBuilder.toString().trim())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void updateEmptyState() {
        if (emptyStateText == null) {
            return;
        }
        boolean isEmpty = adapter == null || adapter.getItemCount() == 0;
        emptyStateText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (recyclerView != null) {
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {

        private final TextView textStudentName;
        private final TextView textCourseCode;
        private final TextView textTimeSlot;
        private final TextView textEmail;
        private final View approveButton;
        private final View rejectButton;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textStudentName = itemView.findViewById(R.id.textStudentName);
            textCourseCode = itemView.findViewById(R.id.textCourseCode);
            textTimeSlot = itemView.findViewById(R.id.textTimeSlot);
            textEmail = itemView.findViewById(R.id.textEmail);
            approveButton = itemView.findViewById(R.id.buttonApprove);
            rejectButton = itemView.findViewById(R.id.buttonReject);
        }

        void bind(Session session, DateFormat dateFormatter, DateFormat timeFormatter) {
            Student student = session.getStudent();
            if (student != null) {
                String firstName = safeValue(student.getFirstName());
                String lastName = safeValue(student.getLastName());
                String fullName = (firstName + " " + lastName).trim();
                textStudentName.setText(fullName.isEmpty()
                        ? itemView.getContext().getString(R.string.session_unknown_student)
                        : fullName);
                textEmail.setText(safeValue(student.getEmail()));
            } else {
                textStudentName.setText(R.string.session_unknown_student);
                textEmail.setText("");
            }

            String course = session.getCourseCode();
            textCourseCode.setText(course == null || course.trim().isEmpty()
                    ? itemView.getContext().getString(R.string.session_unknown_course)
                    : course);

            Date start = new Date(session.getStartTimeMillis());
            Date end = new Date(session.getEndTimeMillis());
            textTimeSlot.setText(itemView.getContext().getString(R.string.session_time_range,
                    dateFormatter.format(start),
                    timeFormatter.format(end)));
        }

        private String safeValue(String value) {
            return value == null ? "" : value;
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
