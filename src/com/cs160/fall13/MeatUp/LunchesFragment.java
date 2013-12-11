package com.cs160.fall13.MeatUp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.*;

public class LunchesFragment extends Fragment {

    private LunchesAdapter adapter;
    private static final int EDIT_LUNCH = 9001;
    private int indexEdited;
    private ListView plannedLunches;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.e("#######", "LunchesFragment onCreateView");
        View root = inflater.inflate(R.layout.lunches, container, false);

        plannedLunches = (ListView) root.findViewById(R.id.planned_lunches);
        adapter = new LunchesAdapter(new ArrayList<Lunch>());
        plannedLunches.setAdapter(adapter);
        plannedLunches.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        View noLunchesText = root.findViewById(R.id.no_lunches_text);
        plannedLunches.setEmptyView(noLunchesText);

        refreshLunches();

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("#######", "LunchesFragment setUserVisibleHint: " + isVisibleToUser);
        if (isVisibleToUser) {
            // Basically onResume - comes back into view
            refreshLunches();
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(PollService.NOTIFICATION_ID);
        }
    }

    private void refreshLunches() {
        // No point in doing any of this if we don't have an adapter yet
        if (adapter == null) {
            return;
        }

        // Each time, get and display sorted list of lunches
        // Remove any lunches that are in the past
        Context ctx = getActivity();
        List<Lunch> allLunches = LunchManager.getLunches(ctx);
        List<Lunch> lunches = new ArrayList<Lunch>();
        Calendar now = Calendar.getInstance();
        for (Lunch lunch : allLunches) {
            if (now.compareTo(lunch.getTime()) >= 0) {
                // The lunch is in the past
                LunchManager.removeLunch(ctx, lunch.getId());
            } else {
                lunches.add(lunch);
            }
        }

        Collections.sort(lunches, new Comparator<Lunch>() {
            @Override
            public int compare(Lunch lhs, Lunch rhs) {
                return lhs.getTime().compareTo(rhs.getTime());
            }
        });
        adapter.clear();
        for (Lunch lunch : lunches) {
            adapter.add(lunch);
        }
        adapter.notifyDataSetChanged();
    }

    public class LunchesAdapter extends ArrayAdapter<Lunch> {

        private final LayoutInflater inflater;
        private static final int RESOURCE = R.layout.lunch_list_item;

        public LunchesAdapter(ArrayList<Lunch> objects) {
            super(getActivity(), RESOURCE, objects);
            inflater = getActivity().getLayoutInflater();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = inflater.inflate(RESOURCE, parent, false);
            } else {
                view = convertView;
            }

            TextView location = (TextView) view.findViewById(R.id.friend_name);
            TextView time = (TextView) view.findViewById(R.id.friend_distance);
            // final variables declared so they can be accessed in the view's onClick
            final Lunch lunch = getItem(position);
            final int listIndex = position;
            Calendar lunchTime = lunch.getTime();
            location.setText(lunch.getLocation());

            Calendar today = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DATE, 1);
            String dateString;
            if (lunchTime.get(Calendar.YEAR) == today.get(Calendar.YEAR) && lunchTime.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                dateString = "Today";
            } else if (lunchTime.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) && lunchTime.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
                dateString = "Tomorrow";
            } else {
                dateString = new SimpleDateFormat("EEEE, MMM d").format(lunchTime.getTime());
            }
            String timeString = new SimpleDateFormat("h:mma").format(lunchTime.getTime());

            time.setText(dateString + " at " + timeString);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent editLunchIntent = new Intent(getActivity(), NewLunchActivity.class);
                    editLunchIntent.putExtra("isEdit", true);
                    editLunchIntent.putExtra("lunch", (Parcelable) lunch);
                    indexEdited = listIndex;
                    startActivityForResult(editLunchIntent, EDIT_LUNCH);
                }
            });
            return view;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (EDIT_LUNCH) : {
                if (resultCode == Activity.RESULT_OK) {
                    //update the list item with the new lunch
                    Lunch updatedLunch = data.getParcelableExtra("updated_lunch");
                    Lunch oldLunch = adapter.getItem(indexEdited);
                    adapter.remove(oldLunch);
                    adapter.insert(updatedLunch, indexEdited);
                    for (int i = 0; i < adapter.getCount(); i++) {
                        plannedLunches.setItemChecked(i, false); // uncheck selection
                    }
                    indexEdited = -5;
                }
                break;
            }
        }
    }

}
