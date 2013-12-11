package com.cs160.fall13.MeatUp;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.support.v4.app.*;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;

public class GetRecommendationActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int NUM_PAGES = 10;
    private ViewPager pager;
    private Restaurant[] restaurants = {
            new Restaurant("La Val's", 37.8755322, -122.2603641, true, true, 4),
            new Restaurant("The Cheese Board", 37.8799915, -122.2694861, true, true, 4.5),
            new Restaurant("Herbivore", 37.864683, -122.266847, true, true, 3.5),
            new Restaurant("Saturn Cafe", 37.8697487, -122.2660694, true, true, 3.5),
            new Restaurant("Cafe Gratitude", 37.8759474, -122.269075, true, true, 3.5),
            new Restaurant("Udupi Palace", 37.8717547, -122.2728575, true, true, 4),
            new Restaurant("Urbann Turbann", 37.8752509, -122.2602815, true, true, 4),
            new Restaurant("Gregoire's", 37.878695, -122.268545, true, true, 4.5),
            new Restaurant("La Note", 37.8661957, -122.2673496, true, true, 4),
            new Restaurant("The Jupiter", 37.86984, -122.267491, true, true, 4),
            new Restaurant("Chez Pannisse", 37.8795938, -122.2689348, true, true, 4),
            new Restaurant("McDonald's", 37.87239, -122.268728, true, true, 2.5),
            new Restaurant("Bear's Ramen House", 37.8679625, -122.2579367, true, true, 3.5),
            new Restaurant("Cafe Strada", 37.8692854, -122.2546207, true, true, 4),
            new Restaurant("Thallasa", 37.86635, -122.267166, true, true, 4)
    };
    private View previousSuggestionButton;
    private View nextSuggestionButton;
    private View selectSuggestionButton;
    private View searchMapRL;
    private View recMapRL;
    private GoogleMap searchGMap;
    private Restaurant currentRes;
    private View selectPlaceButton;
    public static final int RESTAURANT_SELECTED = 1;
    private TextView locNameTV;
    private TextView tvRating;
    private String restDistFromMe, restDistFromFriends;
    private Double curRating = 3.5;
    private ArrayList<String> friends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_recommendations);
        Intent intent = getIntent();
        friends = intent.getStringArrayListExtra("friends");

        // initiate friends


        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.search_map_rec);
        searchGMap = fragment.getMap();

        // Setting a custom info window adapter for the google map
        searchGMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = View.inflate(GetRecommendationActivity.this, R.layout.windowlayout, null);

                // Getting reference to the TextView to set my distance
                TextView tvMyDist = (TextView) v.findViewById(R.id.tv_my_distance);
                tvMyDist.setText(restDistFromMe);

                // Getting reference to the TextView to set friends distance
                TextView tvFriendDist = (TextView) v.findViewById(R.id.tv_friends_distance);
                tvFriendDist.setText(restDistFromFriends);

                // Getting reference to the TextView to set title
                TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
                String title = arg0.getTitle();
                tvTitle.setText(title);

                // Returning the view containing InfoWindow contents
                return v;

            }
        });

        // get title of search view
        locNameTV = (TextView) findViewById(R.id.locName_search);
        tvRating =  (TextView) findViewById(R.id.tv_yelp_rating_search);


        pager = (ViewPager) findViewById(R.id.all_recommendations);
        final PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        previousSuggestionButton = findViewById(R.id.previous_suggestion_button);
        nextSuggestionButton = findViewById(R.id.next_suggestion_button);
        selectSuggestionButton = findViewById(R.id.select_restaurant_button);

        // select restaurant in search mode
        selectPlaceButton = findViewById(R.id.select_restaurant_button_search);
        selectPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = currentRes.getTitle();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("restaurant_name", name);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        // Initializing at the beginning of the list of restaurants, hide the previous button
        previousSuggestionButton.setVisibility(View.INVISIBLE);

        // grab the other map object
        searchMapRL = findViewById(R.id.search_map_rl);
        recMapRL = findViewById(R.id.rec_map_rl);

        previousSuggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItem = pager.getCurrentItem() - 1;
                if (nextItem >= 0) {
                    pager.setCurrentItem(nextItem);
                }
                if (nextItem < pagerAdapter.getCount() - 1) {
                    // Re-enable next button
                    nextSuggestionButton.setVisibility(View.VISIBLE);
                }
                if (nextItem <= 0) {
                    // Disable previous button
                    previousSuggestionButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        nextSuggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = pagerAdapter.getCount();
                int nextItem = pager.getCurrentItem() + 1;
                if (nextItem < count) {
                    pager.setCurrentItem(nextItem);
                }
                if (nextItem > 0) {
                    // Re-enable previous button
                    previousSuggestionButton.setVisibility(View.VISIBLE);
                }
                if (nextItem >= count - 1) {
                    // Disable next button
                    nextSuggestionButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        selectSuggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = pager.getCurrentItem();
                //not sure how we will actually want to get this, but this works for now
                String name = restaurants[currentItem].getTitle();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("restaurant_name", name);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    previousSuggestionButton.setVisibility(View.INVISIBLE);
                } else {
                    previousSuggestionButton.setVisibility(View.VISIBLE);
                }
                if (i == pagerAdapter.getCount() - 1) {
                    nextSuggestionButton.setVisibility(View.INVISIBLE);
                } else {
                    nextSuggestionButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        handleIntent(getIntent());
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this, GetRecommendationActivity.class);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);
        searchView.setSearchableInfo(searchableInfo);
        return super.onCreateOptionsMenu(menu);
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


    private void showLocations(Cursor c){
        MarkerOptions markerOptions = null;
        LatLng position = null;
        searchGMap.clear();
        Marker rinfo;

        // access fragment methods (ugh)
        RestaurantMapFragment rfrag = new RestaurantMapFragment();
        for ( Fragment frag : this.getSupportFragmentManager().getFragments() ){
            if (frag instanceof RecommendationFragment){
                rfrag = (RestaurantMapFragment) ((RecommendationFragment) frag).getMapFragment();
                if (rfrag.getCurrentLocation() != null){
                    break;
                }
            }
        }

        while(c.moveToNext()){
        // create a restaurant
        Restaurant searchRes;
            if (c.getColumnCount() >= 4){
                searchRes = new  Restaurant(c.getString(0), Double.parseDouble(c.getString(2)), Double.parseDouble(c.getString(3)), true, false, 4.2);
            } else {
                searchRes = new  Restaurant(c.getString(0), 37.878695, -122.268545, true, false, 4.0);
            }
            currentRes = searchRes; // TODO this does not work with multiple restaurants   FIXME how to handle?
            String distString = rfrag.getDistanceToRestString(searchRes.getLocation());
            markerOptions = searchRes.getMarkerOptions();
            restDistFromMe = distString + " from your location";
            restDistFromFriends = "all invited friends are within " + distString;
            // Set the rating
            curRating = ((new Random()).nextFloat()*2.0 + 3.0);
            tvRating.setText(String.format("%.1f/5.0 on ", curRating));

            rinfo = searchGMap.addMarker(markerOptions);
            position = searchRes.getLatLng();
            locNameTV.setText(currentRes.getTitle());
            if(position != null){
                //CameraUpdate cameraPosition = ;
                searchGMap.animateCamera(CameraUpdateFactory.newLatLng(position)); // move to the restaurant
                searchGMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12.0f)); // zoom into the location
                rinfo.showInfoWindow();
            }
        }

        recMapRL.setVisibility(View.GONE);
        searchMapRL.setVisibility(View.VISIBLE);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            RecommendationFragment recommendationFragment = new RecommendationFragment();
            recommendationFragment.setRestaurant(restaurants[position]);
            Random generator = new Random();
            int i = generator.nextInt(friends.size());
            recommendationFragment.setFriendName(friends.get(i)); // TODO choose a random friend
            return recommendationFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }


    private class ButtonUpdater extends ViewPager.SimpleOnPageChangeListener {

    }


}
