package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class LunchesFragment extends Fragment {

    private LunchesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.lunches, container, false);

        ListView plannedLunches = (ListView) root.findViewById(R.id.planned_lunches);
        adapter = new LunchesAdapter(new ArrayList<Lunch>());
        plannedLunches.setAdapter(adapter);

        View noLunchesText = root.findViewById(R.id.no_lunches_text);
        plannedLunches.setEmptyView(noLunchesText);

        return root;
    }

    public void addLunch(Lunch lunch) {
        adapter.add(lunch);
    }



    public class LunchesAdapter extends ArrayAdapter<Lunch> {

        private final LayoutInflater inflater;
        private static final int RESOURCE = R.layout.nearby_friends_list_item;

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
            Lunch lunch = getItem(position);
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
            return view;
        }
    }
}
