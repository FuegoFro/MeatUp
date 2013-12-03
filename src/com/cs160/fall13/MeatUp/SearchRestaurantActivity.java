package com.cs160.fall13.MeatUp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// tutorial resources: http://developer.android.com/guide/topics/search/search-dialog.html
//

// TODO add to Manifest!   and adjust for google properties (See other manifest)
public class SearchRestaurantActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private GoogleMap mGoogleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_restaurant);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.search_map);
        mGoogleMap = fragment.getMap();

          // TODO add button and return restaurant
//        selectSuggestionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = "test choice";//restaurants[currentItem].getTitle();
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("restaurant_name", name);
//                setResult(Activity.RESULT_OK, resultIntent);
//                finish();
//            }
//        });

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            doSearch(intent.getStringExtra(SearchManager.QUERY));
        }else if(Intent.ACTION_VIEW.equals(intent.getAction())){
            getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        onSearchRequested();
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        /* Configure the search info and add any event listeners */
        return super.onCreateOptionsMenu(menu);
    }



    private void doSearch(String query){
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(0, data, this);
    }

    private void getPlace(String query){
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(1, data, this);
    }

//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        switch(item.getItemId()){
//            case R.id.action_search:
//                onSearchRequested();
//                break;
//        }
//        return super.onMenuItemSelected(featureId, item);
//    }


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
        CursorLoader cLoader = null;
        if(arg0==0)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{ query.getString("query") }, null);
        else if(arg0==1)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{ query.getString("query") }, null);
        return cLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        showLocations(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }

    private void showLocations(Cursor c){
        MarkerOptions markerOptions = null;
        LatLng position = null;
        mGoogleMap.clear();
        while(c.moveToNext()){
            markerOptions = new MarkerOptions();
            position = new LatLng(Double.parseDouble(c.getString(1)),Double.parseDouble(c.getString(2)));
            markerOptions.position(position);
            markerOptions.title(c.getString(0));
            mGoogleMap.addMarker(markerOptions);
        }
        if(position!=null){
            //CameraUpdate cameraPosition = ;
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(position)); // move to the restaurant
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12.0f)); // zoom into the location
        }
        // FIXME this hack(?) allows a user to search for multiple places in a row
        onSearchRequested();
    }

}
