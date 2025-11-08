package com.example.otams.ui.home;

import com.example.otams.R;
import com.example.otams.ui.common.BaseHomeActivity;

public class TutorHome extends BaseHomeActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tutor_home;
    }

    @Override
    protected int getLogoutButtonId() {
        return R.id.logoutButton;
    }

    @Override
    protected int getLogoutMessageResId() {
        return R.string.logout_tutor_success;
    }
}
