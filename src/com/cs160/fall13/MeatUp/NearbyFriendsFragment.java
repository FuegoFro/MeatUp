package com.cs160.fall13.MeatUp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class NearbyFriendsFragment extends Fragment {

    private final int INVITE_SENT = 1234;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Hardcoded list of nearby friends for now
        NearbyFriend[] friends = new NearbyFriend[]{
                new NearbyFriend("Colorado", "< 5 mi"),
                new NearbyFriend("Avi", "< 5 mi"),
                new NearbyFriend("Danny", "5 - 10 mi"),
                new NearbyFriend("Lexi", "5 - 10 mi"),
                new NearbyFriend("Daniel", "10 - 15 mi"),
        };

        View root = inflater.inflate(R.layout.nearby_friends, container, false);
        final ListView friendsList = (ListView) root.findViewById(R.id.friends_list);
        friendsList.setAdapter(new NearbyFriendsAdapter(friends));
        friendsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        final Button inviteButton = (Button) root.findViewById(R.id.create_invite_button);

        inviteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SparseBooleanArray checkedItems = friendsList.getCheckedItemPositions();
                ArrayList<String> invitedFriends = new ArrayList<String>();
                NearbyFriend temp;
                for (int i = 0; i < friendsList.getAdapter().getCount(); i++) {
                    if (checkedItems.get(i)) {
                        temp = (NearbyFriend) friendsList.getItemAtPosition(i);
                        invitedFriends.add(temp.getName());
                    }
                }
                Intent newLunchIntent = new Intent(getActivity(), NewLunchActivity.class);
                newLunchIntent.putExtra("invitedFriendsArray", invitedFriends);
                startActivityForResult(newLunchIntent, INVITE_SENT);
            }
        });

        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getCheckedItemCount(friendsList) > 0) {
                    inviteButton.setVisibility(View.VISIBLE);
                } else {
                    inviteButton.setVisibility(View.GONE);
                }
            }
        });

        return root;
    }

    private class NearbyFriend {
        private String name;
        private String distance;

        private NearbyFriend(String name, String distance) {
            this.name = name;
            this.distance = distance;
        }

        private String getName() {
            return name;
        }

        private String getDistance() {
            return distance;
        }
    }

    private class NearbyFriendsAdapter extends ArrayAdapter<NearbyFriend> {

        private final LayoutInflater inflater;
        private static final int RESOURCE = R.layout.nearby_friends_list_item;

        public NearbyFriendsAdapter(NearbyFriend[] objects) {
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

            TextView name = (TextView) view.findViewById(R.id.friend_name);
            TextView dist = (TextView) view.findViewById(R.id.friend_distance);
            NearbyFriend friend = getItem(position);
            name.setText(friend.getName());
            dist.setText(friend.getDistance());

            return view;
        }
    }

    public static int getCheckedItemCount(ListView listView) {
        // Compatibility for gingerbread
        if (Build.VERSION.SDK_INT >= 11) return listView.getCheckedItemCount();
        else {
            int count = 0;
            for (int i = listView.getCount() - 1; i >= 0; i--)
                if (listView.isItemChecked(i)) count++;
            return count;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (INVITE_SENT): {
                if (resultCode == Activity.RESULT_OK) {
                    String location = data.getStringExtra("location");
                    int hour = data.getIntExtra("hour", 1);
                    int minute = data.getIntExtra("minute", 1);
                    String day = data.getStringExtra("day");
                    Lunch lunch = new Lunch(hour, minute, day, location);
                    ActionBarActivity activity = (ActionBarActivity) getActivity();
                    LunchesFragment lunchesFragment = null;
                    for (Fragment fragment : activity.getSupportFragmentManager().getFragments()) {
                        if (fragment instanceof LunchesFragment) {
                            lunchesFragment = (LunchesFragment) fragment;
                        }
                    }
                    if (lunchesFragment != null) {
                        ActionBar actionBar = activity.getSupportActionBar();
                        actionBar.getTabAt(1).select();
                        lunchesFragment.addLunch(lunch);
                    }

                    Toast.makeText(getActivity(), "Invite Sent!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}
