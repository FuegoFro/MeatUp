package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class AddFriendsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_tabs_layout);

        TabSetup.setupTabs(
                new Class[]{SearchForFriendsFragment.class, PendingFriendRequestsFragment.class},
                new String[]{"Find Friends", "Pending Requests"},
                this
        );
        // Have an up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
