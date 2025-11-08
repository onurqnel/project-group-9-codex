package com.example.otams.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.R;
import com.example.otams.data.RegistrationRequestRepository;
import com.example.otams.model.RegistrationRequest;
import com.example.otams.model.RequestStatus;
import com.example.otams.ui.auth.LoginPage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RejectedRequestsPage extends AppCompatActivity {

    private final List<String> rejectedUsers = new ArrayList<>();
    private RegistrationRequestRepository repository;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_rejection_request_page);

        repository = new RegistrationRequestRepository(FirebaseFirestore.getInstance());

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(R.string.rejected_requests_title);

        Button registrationRequestsButton = findViewById(R.id.Move2RegReq);
        Button rejectedRequestsButton = findViewById(R.id.Move2RejectReq);
        Button logoutButton = findViewById(R.id.logoutButton);

        ListView rejectedRequestsList = findViewById(R.id.rejectedRequestsList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rejectedUsers);
        rejectedRequestsList.setAdapter(adapter);

        registrationRequestsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AdministratorPage.class));
            finish();
        });

        rejectedRequestsButton.setOnClickListener(v -> fetchRejectedRequests());

        logoutButton.setOnClickListener(v -> {
            Toast.makeText(this, R.string.admin_logged_out, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginPage.class));
            finish();
        });

        fetchRejectedRequests();
    }

    private void fetchRejectedRequests() {
        repository.fetchRequestsByStatus(RequestStatus.REJECTED, new RegistrationRequestRepository.RequestsCallback() {
            @Override
            public void onSuccess(List<RegistrationRequest> requests) {
                populateList(requests);
            }

            @Override
            public void onError(Exception exception) {
                showError(R.string.error_loading_requests, exception);
            }
        });
    }

    private void populateList(List<RegistrationRequest> requests) {
        rejectedUsers.clear();
        if (requests.isEmpty()) {
            rejectedUsers.add(getString(R.string.empty_rejected_requests));
        } else {
            for (RegistrationRequest request : requests) {
                String role = request.getRole() != null ? request.getRole() : getString(R.string.request_role_unknown);
                rejectedUsers.add(getString(R.string.rejected_request_row,
                        request.getEmail(),
                        role));
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showError(@StringRes int messageRes, Exception exception) {
        Toast.makeText(this,
                getString(messageRes, exception.getMessage()),
                Toast.LENGTH_LONG).show();
    }
}
