package com.cs160.fall13.MeatUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainTabsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_tabs_layout);

        TabSetup.setupTabs(
                new Class[]{NearbyFriendsFragment.class, LunchesFragment.class},
                new String[]{"Nearby Friends", "Lunches"},
                this
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create add friend button
        getMenuInflater().inflate(R.menu.main_tabs_actions, menu);
        menu.findItem(R.id.action_add_friend).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplicationContext(), AddFriendsActivity.class);
                startActivity(intent);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
