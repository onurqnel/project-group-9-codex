package com.example.otams.ui.tutor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.otams.R;
import com.example.otams.model.Session;
import com.example.otams.model.SessionStatus;
import com.example.otams.model.Student;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter that displays tutor sessions and exposes management callbacks.
 */
class SessionAdapter extends ListAdapter<Session, SessionAdapter.SessionViewHolder> {

    interface SessionActionListener {
        void onSessionClicked(@NonNull Session session);

        void onApprove(@NonNull Session session);

        void onReject(@NonNull Session session);

        void onCancel(@NonNull Session session);
    }

    private static final DiffUtil.ItemCallback<Session> DIFF_CALLBACK = new DiffUtil.ItemCallback<Session>() {
        @Override
        public boolean areItemsTheSame(@NonNull Session oldItem, @NonNull Session newItem) {
            String oldId = oldItem.getId();
            String newId = newItem.getId();
            if (oldId != null && newId != null) {
                return oldId.equals(newId);
            }
            return oldItem.getStartTimeMillis() == newItem.getStartTimeMillis()
                    && oldItem.getStudentId() != null
                    && oldItem.getStudentId().equals(newItem.getStudentId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Session oldItem, @NonNull Session newItem) {
            return oldItem.getStartTimeMillis() == newItem.getStartTimeMillis()
                    && oldItem.getEndTimeMillis() == newItem.getEndTimeMillis()
                    && equals(oldItem.getStatus(), newItem.getStatus());
        }

        private boolean equals(String a, String b) {
            if (a == null) {
                return b == null;
            }
            return a.equals(b);
        }
    };

    private final SessionActionListener listener;
    private final DateFormat dateFormatter = new SimpleDateFormat("EEE, MMM d 'at' h:mm a", Locale.getDefault());
    private final DateFormat timeFormatter = new SimpleDateFormat("h:mm a", Locale.getDefault());

    SessionAdapter(@NonNull SessionActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = getItem(position);
        holder.bind(session, listener, dateFormatter, timeFormatter);
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {

        private final TextView studentName;
        private final TextView courseCode;
        private final TextView timeSlot;
        private final TextView statusText;
        private final Button primaryButton;
        private final Button secondaryButton;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.textStudentName);
            courseCode = itemView.findViewById(R.id.textCourseCode);
            timeSlot = itemView.findViewById(R.id.textTimeSlot);
            statusText = itemView.findViewById(R.id.textStatus);
            primaryButton = itemView.findViewById(R.id.buttonApprove);
            secondaryButton = itemView.findViewById(R.id.buttonReject);
        }

        void bind(Session session,
                  SessionActionListener listener,
                  DateFormat dateFormatter,
                  DateFormat timeFormatter) {
            Student student = session.getStudent();
            if (student != null) {
                String name = student.getFirstName();
                String lastName = student.getLastName();
                if (name == null && lastName == null) {
                    studentName.setText(R.string.session_unknown_student);
                } else {
                    StringBuilder builder = new StringBuilder();
                    if (name != null) {
                        builder.append(name);
                    }
                    if (lastName != null) {
                        if (builder.length() > 0) {
                            builder.append(' ');
                        }
                        builder.append(lastName);
                    }
                    studentName.setText(builder.toString());
                }
            } else {
                studentName.setText(R.string.session_unknown_student);
            }

            String course = session.getCourseCode();
            courseCode.setText(course == null || course.trim().isEmpty()
                    ? itemView.getContext().getString(R.string.session_unknown_course)
                    : course);

            Date start = new Date(session.getStartTimeMillis());
            Date end = new Date(session.getEndTimeMillis());
            timeSlot.setText(itemView.getContext().getString(
                    R.string.session_time_range,
                    dateFormatter.format(start),
                    timeFormatter.format(end)));

            SessionStatus status = session.getStatusEnum();
            if (status == null) {
                status = SessionStatus.PENDING;
            }

            String statusLabel = session.getStatusLabel();
            if (statusLabel == null) {
                statusLabel = status.getDisplayName();
            }
            statusText.setText(statusLabel);

            updateButtons(session, status, listener);

            itemView.setOnClickListener(v -> listener.onSessionClicked(session));
        }

        private void updateButtons(Session session, SessionStatus status, SessionActionListener listener) {
            primaryButton.setVisibility(View.GONE);
            secondaryButton.setVisibility(View.GONE);
            primaryButton.setOnClickListener(null);
            secondaryButton.setOnClickListener(null);

            if (status == SessionStatus.PENDING) {
                primaryButton.setText(R.string.session_action_approve);
                primaryButton.setVisibility(View.VISIBLE);
                primaryButton.setOnClickListener(v -> listener.onApprove(session));

                secondaryButton.setText(R.string.session_action_reject);
                secondaryButton.setVisibility(View.VISIBLE);
                secondaryButton.setOnClickListener(v -> listener.onReject(session));
            } else if (status == SessionStatus.APPROVED) {
                secondaryButton.setText(R.string.session_action_cancel);
                secondaryButton.setVisibility(View.VISIBLE);
                secondaryButton.setOnClickListener(v -> listener.onCancel(session));
            }
        }
    }
}
