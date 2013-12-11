package com.cs160.fall13.MeatUp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;

public class NearbyFriendsFragment extends Fragment {
    private ListView friendsList;
    private Menu menu;
    private boolean initialized = false;
    private MenuItem.OnMenuItemClickListener createInvitationClickListener;

    private final int INVITE_SENT = 1234;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        friendsList = (ListView) root.findViewById(R.id.friends_list);
        friendsList.setAdapter(new NearbyFriendsAdapter(friends));
        friendsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        createInvitationClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SparseBooleanArray checkedItems = friendsList.getCheckedItemPositions();
                // Send list of checked friends to invite creation activity. Also uncheck friends
                ArrayList<String> invitedFriends = new ArrayList<String>();
                NearbyFriend temp;
                for (int i = 0; i < friendsList.getAdapter().getCount(); i++) {
                    if (checkedItems.get(i)) {
                        friendsList.setItemChecked(i, false); // unchack all checked friends
                        temp = (NearbyFriend) friendsList.getItemAtPosition(i);
                        invitedFriends.add(temp.getName());
                    }
                }
                // Return action bar to normal
                showDefaultMenu();
                // Launch invite creation activity
                Intent newLunchIntent = new Intent(getActivity(), NewLunchActivity.class);
                newLunchIntent.putExtra("invitedFriendsArray", invitedFriends);
                startActivityForResult(newLunchIntent, INVITE_SENT);
                return true;
            }
        };

        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateMenu();
            }
        });
        initialized = true;
        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (initialized) {
            if (isVisibleToUser) {
                updateMenu();
            } else {
                showDefaultMenu();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        menu.findItem(R.id.action_create_invitation).setOnMenuItemClickListener(createInvitationClickListener);
        updateMenu();
    }

    private void updateMenu() {
        if (getCheckedItemCount(friendsList) > 0) {
            showContextualMenu();
        } else {
            showDefaultMenu();
        }
    }

    private void showDefaultMenu() {
        ActionBarActivity activity = (ActionBarActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setBackgroundDrawable(null);
        }
        if (menu != null) {
            menu.findItem(R.id.action_add_friend).setVisible(true);
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.action_help).setVisible(true);
            menu.findItem(R.id.action_create_invitation).setVisible(false);
        }
    }

    private void showContextualMenu() {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        Drawable background = getResources().getDrawable(R.drawable.abc_cab_background_top_holo_dark);
        actionBar.setBackgroundDrawable(background);
        if (menu != null) {
            menu.findItem(R.id.action_add_friend).setVisible(false);
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.action_help).setVisible(false);
            menu.findItem(R.id.action_create_invitation).setVisible(true);
        }
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
                    Calendar time = (Calendar) data.getSerializableExtra("lunch_time");
                    ArrayList<String> attendees = data.getStringArrayListExtra("invited_friends");
                    int id = data.getIntExtra("lunch_id", 1);
                    Lunch lunch = new Lunch(time, location, id);
                    lunch.setAttendees(attendees);
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
