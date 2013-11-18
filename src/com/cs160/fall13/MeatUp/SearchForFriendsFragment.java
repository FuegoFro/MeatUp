package com.cs160.fall13.MeatUp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;


// This code is taken straight from an example on the Android Docs
// http://developer.android.com/guide/components/loaders.html#example
// Minor modifications were made to make it work with on Gingerbread
public class SearchForFriendsFragment extends ListFragment
        implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter adapter;

    // If non-null, this is the current filter the user has provided.
    String curFilter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView lv = getListView();
        lv.setSelector(R.drawable.abc_list_selector_holo_light);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setFastScrollEnabled(true);
//        lv.setOnItemClickListener();
//        lv.setMultiChoiceModeListener(new ModeCallback());

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("Retrieving Contacts");

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // Create an empty adapter we will use to display the loaded data.
        adapter = new SimpleCursorAdapter(getActivity(),
//                android.R.layout.simple_list_item_2, null,
                R.layout.list_item_multi_select_2, null,
//                new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.CONTACT_STATUS},
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER},
                new int[]{android.R.id.text1, android.R.id.text2}, 0);
        setListAdapter(adapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Place an action bar item for searching.
        MenuItem item = menu.add("Search");
        item.setIcon(R.drawable.ic_action_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(getActivity());
        sv.setOnQueryTextListener(this);
        sv.setQueryHint("Search for friends");
        MenuItemCompat.setActionView(item, sv);
//        sv.setOnSearchClickListener();
    }

    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        curFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        Log.i("FragmentComplexList", "Item clicked: " + id);
    }

    // These are the Contacts rows that we will retrieve.
    static final String[] PHONE_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
    };
//    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
//            ContactsContract.Contacts._ID,
//            ContactsContract.Contacts.DISPLAY_NAME,
//            ContactsContract.Contacts.CONTACT_STATUS,
//            ContactsContract.Contacts.CONTACT_PRESENCE,
//            ContactsContract.Contacts.PHOTO_ID,
//            ContactsContract.Contacts.LOOKUP_KEY,
//    };

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri;
        if (curFilter != null) {
            baseUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                    Uri.encode(curFilter));
        } else {
            baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        }
//        if (curFilter != null) {
//            baseUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,
//                    Uri.encode(curFilter));
//        } else {
//            baseUri = ContactsContract.Contacts.CONTENT_URI;
//        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        String select = "((" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " NOTNULL) AND ("
                + ContactsContract.CommonDataKinds.Phone.NUMBER + " NOTNULL) AND ("
                + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " != '' ))";
        return new CursorLoader(getActivity(), baseUri,
                PHONE_PROJECTION, select, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
//        String select = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
//                + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
//                + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";
//        return new CursorLoader(getActivity(), baseUri,
//                CONTACTS_SUMMARY_PROJECTION, select, null,
//                ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        adapter.swapCursor(data);
        setEmptyText("No contacts found");
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        adapter.swapCursor(null);
    }
}