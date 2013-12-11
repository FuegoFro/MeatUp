package com.cs160.fall13.MeatUp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.io.IOException;

public class PollService extends Service {
    public static final String TAG = "###########";
    public static final int NOTIFICATION_ID = 683518658;
    private ServerPollTask updater;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Loop in background asking server for changes and sending any updates to the app with intents
        updater = new ServerPollTask();
        updater.execute();
        Log.e(TAG, "Creating service");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updater.cancel(true);
        Log.e(TAG, "Destroying service");
    }

    private class ServerPollTask extends AsyncTask<Void, Void, Void> {
        private volatile boolean running = true;

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String phoneNumber = telephonyManager.getLine1Number();
            if (phoneNumber == null) {
                phoneNumber = "9876543210";
            }
            try {
                while (running) {
                    String update = ServerAccess.getUpdateFromServer(phoneNumber);
                    if (update != null && !update.isEmpty()) {
                        try {
                            UpdateContainer updateContainer = mapper.readValue(update, UpdateContainer.class);
                            int numNewLunches = updateContainer.lunches.length;

                            Context ctx = PollService.this;
                            for (Lunch lunch : updateContainer.lunches) {
                                LunchManager.addLunch(ctx, lunch);
                            }

                            if (numNewLunches > 0) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
                                String ending = numNewLunches == 1 ? "" : "s";
                                builder.setContentTitle("New Lunch" + ending);
                                builder.setContentText("You have " + numNewLunches + " new lunch invitation" + ending);
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
                                notificationManager.notify(NOTIFICATION_ID, builder.build());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

class UpdateContainer {
    public Lunch[] lunches;
    public Object[] friendInvites;
}