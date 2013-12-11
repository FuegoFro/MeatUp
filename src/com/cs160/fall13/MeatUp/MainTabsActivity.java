package com.cs160.fall13.MeatUp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainTabsActivity extends ActionBarActivity {

    public static final String SWITCH_TO_LUNCH_TAB = "switch to lunch tab";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_tabs_layout);

        TabSetup.setupTabs(
                new Class[]{NearbyFriendsFragment.class, LunchesFragment.class},
                new String[]{"Nearby Friends", "Lunches"},
                this
        );

        handleStartingIntent(getIntent());

        // Start server polling in background for 'instant' updating
        Intent intent = new Intent(this, PollService.class);
        startService(intent);
    }

    private void handleStartingIntent(Intent intent) {
        if (intent.getBooleanExtra(SWITCH_TO_LUNCH_TAB, false)) {
            getSupportActionBar().getTabAt(1).select();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, PollService.class);
        stopService(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleStartingIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create add friend button
        getMenuInflater().inflate(R.menu.main_tabs_actions, menu);
        menu.findItem(R.id.action_add_friend).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<Lunch> lunches = LunchManager.getLunches(MainTabsActivity.this);
                for (Lunch lunch : lunches) {
                    LunchManager.removeLunch(MainTabsActivity.this, lunch.getId());
                }

                Intent intent = new Intent(getApplicationContext(), AddFriendsActivity.class);
                startActivity(intent);
                return true;
            }
        });
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Context ctx = MainTabsActivity.this;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
                builder.setContentTitle("New Lunch");
                builder.setContentText("You have 1 new lunch invitation");
                builder.setSmallIcon(R.drawable.ic_launcher);

                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(ctx, MainTabsActivity.class);
                resultIntent.putExtra(MainTabsActivity.SWITCH_TO_LUNCH_TAB, true);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainTabsActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                builder.setContentIntent(resultPendingIntent);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // ID allows you to update the notification later on.
                notificationManager.notify(PollService.NOTIFICATION_ID, builder.build());

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        });
        menu.findItem(R.id.action_help).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Context ctx = MainTabsActivity.this;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
                builder.setContentTitle("Lunch Changed");
                builder.setContentText("Your lunch with David has been changed");
                builder.setSmallIcon(R.drawable.ic_launcher);

                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(ctx, MainTabsActivity.class);
                resultIntent.putExtra(MainTabsActivity.SWITCH_TO_LUNCH_TAB, true);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainTabsActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                builder.setContentIntent(resultPendingIntent);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // ID allows you to update the notification later on.
                notificationManager.notify(PollService.NOTIFICATION_ID, builder.build());
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
