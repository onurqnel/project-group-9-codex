package com.example.otams.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.otams.R;
import com.example.otams.data.RegistrationRequestRepository;
import com.example.otams.model.RegistrationRequest;
import com.example.otams.model.RequestStatus;
import com.example.otams.ui.auth.LoginPage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdministratorPage extends AppCompatActivity implements RequestsAdapter.OnRequestActionListener {

    private final List<RegistrationRequest> requests = new ArrayList<>();
    private RequestStatus currentStatus = RequestStatus.PENDING;

    private RegistrationRequestRepository repository;
    private RequestsAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private Button pendingButton;
    private Button rejectedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_home);

        repository = new RegistrationRequestRepository(FirebaseFirestore.getInstance());

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(R.string.admin_welcome);

        recyclerView = findViewById(R.id.requestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RequestsAdapter(this, requests, this);
        recyclerView.setAdapter(adapter);

        emptyStateText = findViewById(R.id.emptyStateText);
        pendingButton = findViewById(R.id.Move2);
        rejectedButton = findViewById(R.id.button3);
        Button logoutButton = findViewById(R.id.logoutButton);

        pendingButton.setOnClickListener(v -> loadRequests(RequestStatus.PENDING));
        rejectedButton.setOnClickListener(v -> loadRequests(RequestStatus.REJECTED));
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(this, R.string.admin_logged_out, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginPage.class));
            finish();
        });

        loadRequests(RequestStatus.PENDING);
    }

    private void loadRequests(RequestStatus status) {
        currentStatus = status;
        repository.fetchRequestsByStatus(status, new RegistrationRequestRepository.RequestsCallback() {
            @Override
            public void onSuccess(List<RegistrationRequest> items) {
                requests.clear();
                requests.addAll(items);
                adapter.notifyDataSetChanged();
                updateEmptyState();
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(AdministratorPage.this,
                        getString(R.string.error_loading_requests, exception.getMessage()),
                        Toast.LENGTH_LONG).show();
            }
        });
        updateButtonState(status);
    }

    private void updateEmptyState() {
        if (requests.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText(getEmptyStateText(currentStatus));
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @StringRes
    private int getEmptyStateText(RequestStatus status) {
        if (status == RequestStatus.REJECTED) {
            return R.string.empty_rejected_requests;
        }
        return R.string.empty_pending_requests;
    }

    private void updateButtonState(RequestStatus status) {
        pendingButton.setEnabled(status != RequestStatus.PENDING);
        rejectedButton.setEnabled(status != RequestStatus.REJECTED);
    }

    @Override
    public void onApprove(RegistrationRequest request) {
        repository.approveRequest(request, new RepositoryCompletionCallback(R.string.request_approved));
    }

    @Override
    public void onReject(RegistrationRequest request) {
        repository.rejectRequest(request, new RepositoryCompletionCallback(R.string.request_rejected));
    }

    private class RepositoryCompletionCallback implements RegistrationRequestRepository.CompletionCallback {

        private final int successMessageResId;

        RepositoryCompletionCallback(@StringRes int successMessageResId) {
            this.successMessageResId = successMessageResId;
        }

        @Override
        public void onSuccess() {
            Toast.makeText(AdministratorPage.this, successMessageResId, Toast.LENGTH_SHORT).show();
            loadRequests(currentStatus);
        }

        @Override
        public void onError(Exception exception) {
            Toast.makeText(AdministratorPage.this,
                    getString(R.string.error_request_action, exception.getMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }
}
