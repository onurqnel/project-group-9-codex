package com.example.otams.ui.tutor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.otams.R;
import com.example.otams.model.Request;
import com.example.otams.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RequestsFragment extends Fragment {

    private FirestoreRecyclerAdapter<Request, RequestViewHolder> adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = view.findViewById(R.id.requestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView noRequestsText = view.findViewById(R.id.noRequestsText);
        setupRecyclerView(noRequestsText);
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

    private void setupRecyclerView(TextView emptyStateText) {
        String tutorId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (tutorId == null) {
            emptyStateText.setText(R.string.requests_no_tutor);
            return;
        }

        Query query = FirebaseFirestore.getInstance().collection("requests")
                .whereEqualTo("tutorId", tutorId)
                .whereEqualTo("approval", false);

        FirestoreRecyclerOptions<Request> options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Request, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Request request) {
                holder.bind(request);
                String requestId = getSnapshots().getSnapshot(position).getId();
                holder.approveButton.setOnClickListener(v -> updateApproval(requestId, true));
                holder.rejectButton.setOnClickListener(v -> updateApproval(requestId, false));
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_pending_session, parent, false);
                return new RequestViewHolder(itemView);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    private void updateApproval(String requestId, boolean approved) {
        FirebaseFirestore.getInstance().collection("requests")
                .document(requestId)
                .update("approval", approved)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(),
                        approved ? R.string.requests_approved : R.string.requests_rejected,
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        R.string.requests_action_error,
                        Toast.LENGTH_SHORT).show());
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {

        private final TextView textStudentName;
        private final TextView textEmail;
        private final Button approveButton;
        private final Button rejectButton;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textStudentName = itemView.findViewById(R.id.textStudentName);
            textEmail = itemView.findViewById(R.id.textEmail);
            approveButton = itemView.findViewById(R.id.buttonApprove);
            rejectButton = itemView.findViewById(R.id.buttonReject);
        }

        void bind(Request request) {
            User student = request.getUser();
            if (student != null) {
                String fullName = String.format("%s %s",
                        safeValue(student.getFirstName()),
                        safeValue(student.getLastName())).trim();
                textStudentName.setText(fullName);
                textEmail.setText(safeValue(student.getEmail()));
            }
        }

        private String safeValue(String value) {
            return value == null ? "" : value;
        }
    }
}
