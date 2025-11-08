package com.example.otams.ui.registration;

import com.example.otams.R;
import com.example.otams.ui.common.BaseStatusActivity;

public class ApprovedPage extends BaseStatusActivity {

    @Override
    protected int getTitleTextResId() {
        return R.string.status_approved_title;
    }

    @Override
    protected int getTitleColorResId() {
        return R.color.status_approved;
    }

    @Override
    protected int getDescriptionTextResId() {
        return R.string.status_approved_message;
    }
}
