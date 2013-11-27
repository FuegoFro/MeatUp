package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LunchesFragment extends Fragment {

    private ArrayList<Lunch> lunches = new ArrayList<Lunch>();
    public ListView plannedLunches;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.lunches, container, false);
        //for some reason, the lunches weren't updating unless there was something in them to begin with.
        lunches.add(new Lunch(12, 45, "Today", "Bongo Burger"));
        plannedLunches = (ListView) root.findViewById(R.id.planned_lunches);
        plannedLunches.setAdapter(new LunchesAdapter(lunches));
        return root;
    }

    public void addLunch(Lunch lunch) {
        ((LunchesAdapter)plannedLunches.getAdapter()).addLunch(lunch);
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
            location.setText(lunch.getLocation());
            String minute = lunch.getMinute() == 0 ? "00" : Integer.toString(lunch.getMinute());
            time.setText(lunch.getDay() + " - " + lunch.getHour() + ":" + minute);
            return view;
        }

        public void addLunch(Lunch lunch) {
            lunches.add(lunch);
            plannedLunches.invalidate();
        }
    }
}
