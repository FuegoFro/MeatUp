package com.cs160.fall13.MeatUp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.*;

public class AddFriendsDialog extends DialogFragment {

    ArrayList mSelectedItems;
    public String[] allFriendsArray = {"Danny", "Colorado", "Lexi", "Daniel", "Avi"};
    ArrayList<String> allFriends = new ArrayList<String>();

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        String[] items = allFriends.toArray(new String[allFriends.size()]);
        builder.setTitle("Invite More Friends")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(items, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        ListView friendsList = (ListView) getActivity().findViewById(R.id.invitedFriendsList);
                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) friendsList.getAdapter();
                        for (int i = 0; i < mSelectedItems.size(); i++) {
                            adapter.add(allFriends.get((Integer)mSelectedItems.get(i)));
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

    public void removeAlreadyInvited(ArrayList<String> invited) {
        if (allFriends.size() == 0) Collections.addAll(allFriends, allFriendsArray);
        for (String name : invited) {
            allFriends.remove(name);
        }
    }
}
