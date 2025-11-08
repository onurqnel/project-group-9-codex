package com.example.otams.data;

import androidx.annotation.NonNull;

import com.example.otams.model.RegistrationRequest;
import com.example.otams.model.RequestStatus;
import com.example.otams.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository encapsulating Firestore interactions for registration requests.
 */
public class RegistrationRequestRepository {

    private static final String COLLECTION_REQUESTS = "requests";
    private static final String COLLECTION_USERS = "users";

    private final FirebaseFirestore firestore;

    public RegistrationRequestRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public interface RequestsCallback {
        void onSuccess(List<RegistrationRequest> requests);
        void onError(Exception exception);
    }

    public interface CompletionCallback {
        void onSuccess();
        void onError(Exception exception);
    }

    public void fetchRequestsByStatus(RequestStatus status, RequestsCallback callback) {
        Query query = firestore.collection(COLLECTION_REQUESTS);
        if (status != null) {
            query = query.whereEqualTo("status", status.getFirestoreValue());
        }
        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<RegistrationRequest> results = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        RegistrationRequest request = map(document);
                        if (request != null) {
                            results.add(request);
                        }
                    }
                    callback.onSuccess(results);
                })
                .addOnFailureListener(callback::onError);
    }

    public void approveRequest(RegistrationRequest request, CompletionCallback callback) {
        User user = UserFactory.fromRequest(request);
        if (user == null) {
            callback.onError(new IllegalArgumentException("Invalid role for approval"));
            return;
        }

        firestore.collection(COLLECTION_USERS)
                .document(request.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> updateStatus(request.getRequestId(), RequestStatus.APPROVED, callback))
                .addOnFailureListener(callback::onError);
    }

    public void rejectRequest(RegistrationRequest request, CompletionCallback callback) {
        updateStatus(request.getRequestId(), RequestStatus.REJECTED, callback);
    }

    private void updateStatus(String requestId, RequestStatus status, CompletionCallback callback) {
        firestore.collection(COLLECTION_REQUESTS)
                .document(requestId)
                .update("status", status.getFirestoreValue())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    private RegistrationRequest map(@NonNull DocumentSnapshot document) {
        RegistrationRequest request = document.toObject(RegistrationRequest.class);
        if (request == null) {
            return null;
        }
        if (request.getRequestId() == null) {
            request.setRequestId(document.getId());
        }
        return request;
    }
}
