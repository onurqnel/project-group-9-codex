package com.example.otams.ui.tutor;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.otams.R;
import com.example.otams.model.Slot;
import com.example.otams.model.Tutor;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class AvailabilityFragment extends Fragment implements AvailabilityAdapter.SlotActionListener {

    private DatePicker datePicker;
    private TimePicker startTimePicker;
    private TimePicker endTimePicker;
    private SwitchMaterial approvalSwitch;
    private TextView errorText;
    private AvailabilityAdapter adapter;

    private ListenerRegistration registration;
    private CollectionReference availabilityRef;
    private final Tutor tutor = new Tutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_availability, container, false);

        datePicker = view.findViewById(R.id.datePicker);
        startTimePicker = view.findViewById(R.id.startTimePicker);
        endTimePicker = view.findViewById(R.id.endTimePicker);
        approvalSwitch = view.findViewById(R.id.approvalSwitch);
        errorText = view.findViewById(R.id.availabilityErrorText);

        RecyclerView recyclerView = view.findViewById(R.id.availabilityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AvailabilityAdapter(this);
        recyclerView.setAdapter(adapter);

        Button addSlotButton = view.findViewById(R.id.addSlotButton);
        addSlotButton.setOnClickListener(v -> attemptCreateSlot());

        configurePickers();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        availabilityRef = firestore.collection("availability");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeToSlots();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    private void configurePickers() {
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.HOUR_OF_DAY, 0);
        minDate.set(Calendar.MINUTE, 0);
        minDate.set(Calendar.SECOND, 0);
        minDate.set(Calendar.MILLISECOND, 0);
        datePicker.setMinDate(minDate.getTimeInMillis());
        startTimePicker.setIs24HourView(false);
        endTimePicker.setIs24HourView(false);
    }

    private void subscribeToSlots() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }

        String tutorId = getCurrentTutorId();
        if (tutorId == null) {
            showError(R.string.availability_no_tutor);
            tutor.setAvailableSlots(new ArrayList<>());
            adapter.submitList(new ArrayList<>());
            return;
        }

        registration = availabilityRef.whereEqualTo("tutorId", tutorId)
                .orderBy("startTimeMillis", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        showError(R.string.availability_load_error);
                        return;
                    }

                    List<Slot> slots = new ArrayList<>();
                    if (snapshots != null) {
                        for (DocumentSnapshot document : snapshots) {
                            Slot slot = document.toObject(Slot.class);
                            if (slot != null) {
                                slot.setId(document.getId());
                                slots.add(slot);
                            }
                        }
                    }

                    tutor.setAvailableSlots(slots);
                    adapter.submitList(new ArrayList<>(tutor.getAvailableSlots()));
                    clearError();
                });
    }

    private void attemptCreateSlot() {
        String tutorId = getCurrentTutorId();
        if (tutorId == null) {
            showError(R.string.availability_no_tutor);
            return;
        }

        Calendar startCalendar = buildCalendarFromInputs(startTimePicker);
        Calendar endCalendar = buildCalendarFromInputs(endTimePicker);

        if (startCalendar == null || endCalendar == null) {
            showError(R.string.availability_time_error);
            return;
        }

        if (startCalendar.get(Calendar.MINUTE) % 30 != 0 || endCalendar.get(Calendar.MINUTE) % 30 != 0) {
            showError(R.string.availability_increment_error);
            return;
        }

        if (!endCalendar.after(startCalendar)) {
            showError(R.string.availability_end_after_start);
            return;
        }

        long now = System.currentTimeMillis();
        if (startCalendar.getTimeInMillis() < now) {
            showError(R.string.availability_past_error);
            return;
        }

        long startMillis = startCalendar.getTimeInMillis();
        long endMillis = endCalendar.getTimeInMillis();

        boolean manualApproval = approvalSwitch.isChecked();
        Slot slotCandidate = new Slot(tutorId, startMillis, endMillis, manualApproval);

        if (!slotCandidate.isValidDuration()) {
            showError(R.string.availability_increment_error);
            return;
        }

        if (tutor.hasConflictingSlot(slotCandidate)) {
            showError(R.string.availability_overlap_error);
            return;
        }

        Slot slot = tutor.createNewSlot(slotCandidate);
        adapter.submitList(new ArrayList<>(tutor.getAvailableSlots()));

        availabilityRef.add(slot)
                .addOnSuccessListener(documentReference -> {
                    clearError();
                    Toast.makeText(getContext(),
                            R.string.availability_slot_created,
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    tutor.removeSlot(slot);
                    adapter.submitList(new ArrayList<>(tutor.getAvailableSlots()));
                    showError(R.string.availability_slot_error);
                });
    }

    @Nullable
    private Calendar buildCalendarFromInputs(TimePicker picker) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = picker.getHour();
            minute = picker.getMinute();
        } else {
            hour = picker.getCurrentHour();
            minute = picker.getCurrentMinute();
        }

        if (hour < 0 || minute < 0) {
            return null;
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Nullable
    private String getCurrentTutorId() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onDeleteSlot(@NonNull Slot slot) {
        String id = slot.getId();
        if (id == null) {
            showError(R.string.availability_slot_error);
            return;
        }

        availabilityRef.document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    tutor.removeSlot(slot);
                    adapter.submitList(new ArrayList<>(tutor.getAvailableSlots()));
                    clearError();
                    Toast.makeText(getContext(),
                            R.string.availability_slot_deleted,
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> showError(R.string.availability_slot_error));
    }

    private void showError(@StringRes int messageRes) {
        if (errorText == null) {
            return;
        }
        errorText.setText(messageRes);
        errorText.setVisibility(View.VISIBLE);
    }

    private void clearError() {
        if (errorText == null) {
            return;
        }
        errorText.setText("");
        errorText.setVisibility(View.GONE);
    }
}
