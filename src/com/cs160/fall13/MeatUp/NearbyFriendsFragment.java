package com.cs160.fall13.MeatUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class NearbyFriendsFragment extends Fragment {
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
               startActivity(newLunchIntent);
           }
        });

        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (friendsList.getCheckedItemCount() > 0) {
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
}
