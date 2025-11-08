package com.example.otams.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.otams.R;
import com.example.otams.model.RegistrationRequest;
import com.example.otams.model.RequestStatus;
import com.example.otams.model.UserRole;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

    interface OnRequestActionListener {
        void onApprove(RegistrationRequest request);
        void onReject(RegistrationRequest request);
    }

    private final Context context;
    private final List<RegistrationRequest> requests;
    private final OnRequestActionListener listener;

    public RequestsAdapter(Context context, List<RegistrationRequest> requests, OnRequestActionListener listener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_registration_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RegistrationRequest request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {

        private final TextView textName;
        private final TextView textRole;
        private final TextView textEmail;
        private final TextView textPhone;
        private final TextView textProgramOrDegree;
        private final TextView textCourses;
        private final Button buttonApprove;
        private final Button buttonReject;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textRole = itemView.findViewById(R.id.textRole);
            textEmail = itemView.findViewById(R.id.textEmail);
            textPhone = itemView.findViewById(R.id.textPhone);
            textProgramOrDegree = itemView.findViewById(R.id.textProgramOrDegree);
            textCourses = itemView.findViewById(R.id.textCourses);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonReject = itemView.findViewById(R.id.buttonReject);
        }

        void bind(RegistrationRequest request) {
            textName.setText(context.getString(R.string.request_full_name_format,
                    safeValue(request.getFirstName()),
                    safeValue(request.getLastName())));

            textEmail.setText(safeValue(request.getEmail()));
            textPhone.setText(safeValue(request.getPhoneNumber()));

            UserRole role = request.getRoleEnum();
            textRole.setText(role != null ? role.getFirestoreValue() : context.getString(R.string.request_role_unknown));

            if (role == UserRole.STUDENT) {
                textProgramOrDegree.setText(context.getString(R.string.request_program_format, safeValue(request.getProgram())));
                textCourses.setVisibility(View.GONE);
            } else if (role == UserRole.TUTOR) {
                textProgramOrDegree.setText(context.getString(R.string.request_degree_format, safeValue(request.getDegree())));
                textCourses.setVisibility(View.VISIBLE);
                textCourses.setText(context.getString(R.string.request_courses_format, request.getCoursesAsString()));
            } else {
                textProgramOrDegree.setText("");
                textCourses.setVisibility(View.GONE);
            }

            buttonApprove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApprove(request);
                }
            });

            buttonReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReject(request);
                }
            });

            boolean isRejected = request.getStatusEnum() == RequestStatus.REJECTED;
            buttonReject.setVisibility(isRejected ? View.GONE : View.VISIBLE);
        }

        private String safeValue(String value) {
            return value == null ? "" : value;
        }
    }
}
