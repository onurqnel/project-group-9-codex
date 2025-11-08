package com.example.otams.ui.registration;

import com.example.otams.R;
import com.example.otams.ui.common.BaseStatusActivity;

public class RejectedPage extends BaseStatusActivity {

    @Override
    protected int getTitleTextResId() {
        return R.string.status_rejected_title;
    }

    @Override
    protected int getTitleColorResId() {
        return R.color.status_rejected;
    }

    @Override
    protected int getDescriptionTextResId() {
        return R.string.status_rejected_message;
    }
}
