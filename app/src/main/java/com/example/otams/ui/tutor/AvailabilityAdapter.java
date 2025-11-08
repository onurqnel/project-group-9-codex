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
import com.example.otams.model.Slot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter rendering the tutor's availability slots.
 */
class AvailabilityAdapter extends ListAdapter<Slot, AvailabilityAdapter.AvailabilityViewHolder> {

    interface SlotActionListener {
        void onDeleteSlot(@NonNull Slot slot);
    }

    private static final DiffUtil.ItemCallback<Slot> DIFF_CALLBACK = new DiffUtil.ItemCallback<Slot>() {
        @Override
        public boolean areItemsTheSame(@NonNull Slot oldItem, @NonNull Slot newItem) {
            String oldId = oldItem.getId();
            String newId = newItem.getId();
            if (oldId != null && newId != null) {
                return oldId.equals(newId);
            }
            return oldItem.getStartTimeMillis() == newItem.getStartTimeMillis()
                    && oldItem.getEndTimeMillis() == newItem.getEndTimeMillis();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Slot oldItem, @NonNull Slot newItem) {
            return oldItem.getStartTimeMillis() == newItem.getStartTimeMillis()
                    && oldItem.getEndTimeMillis() == newItem.getEndTimeMillis()
                    && oldItem.isManualApprovalRequired() == newItem.isManualApprovalRequired();
        }
    };

    private final SlotActionListener listener;
    private final DateFormat dateFormatter = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
    private final DateFormat timeFormatter = new SimpleDateFormat("h:mm a", Locale.getDefault());

    AvailabilityAdapter(@NonNull SlotActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AvailabilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_availability_slot, parent, false);
        return new AvailabilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailabilityViewHolder holder, int position) {
        Slot slot = getItem(position);
        holder.bind(slot, listener, dateFormatter, timeFormatter);
    }

    static class AvailabilityViewHolder extends RecyclerView.ViewHolder {

        private final TextView textDate;
        private final TextView textTimeRange;
        private final TextView textStatus;
        private final TextView textAutoApprove;
        private final Button deleteButton;

        AvailabilityViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textTimeRange = itemView.findViewById(R.id.textTimeRange);
            textStatus = itemView.findViewById(R.id.textStatus);
            textAutoApprove = itemView.findViewById(R.id.textAutoApprove);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }

        void bind(Slot slot,
                  SlotActionListener listener,
                  DateFormat dateFormatter,
                  DateFormat timeFormatter) {
            Date startDate = new Date(slot.getStartTimeMillis());
            Date endDate = new Date(slot.getEndTimeMillis());

            textDate.setText(dateFormatter.format(startDate));
            textTimeRange.setText(itemView.getContext().getString(
                    R.string.availability_time_range,
                    timeFormatter.format(startDate),
                    timeFormatter.format(endDate)));

            long now = System.currentTimeMillis();
            if (slot.getEndTimeMillis() < now) {
                textStatus.setText(R.string.availability_status_expired);
            } else {
                textStatus.setText(R.string.availability_status_open);
            }

            boolean manualApproval = slot.isManualApprovalRequired();
            textAutoApprove.setText(itemView.getContext().getString(
                    manualApproval ? R.string.availability_manual_approval
                            : R.string.availability_auto_approval));

            deleteButton.setOnClickListener(v -> listener.onDeleteSlot(slot));
        }
    }
}
