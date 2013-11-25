package com.cs160.fall13.MeatUp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

public class GetRecommendationActivity extends FragmentActivity {

    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_recommendations);

        mPager = (ViewPager) findViewById(R.id.allRecs);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        Restaurant[] restaurants = {
                new Restaurant( "La Val's", 37.8755322, -122.2603641, true, true, 4),
                new Restaurant("The Cheese Board", 37.8799915, -122.2694861, true, true, 4.5),
                new Restaurant("Herbivore", 37.864683,-122.266847, true, true, 3.5),
                new Restaurant("Saturn Cafe", 37.8697487,-122.2660694, true, true, 3.5),
                new Restaurant("Cafe Gratitude", 37.8759474,-122.269075, true, true, 3.5),
                new Restaurant("Udupi Palace", 37.8717547,-122.2728575, true, true, 4),
                new Restaurant("Urbann Turbann", 37.8752509,-122.2602815, true, true, 4),
                new Restaurant("Gregoire's", 37.878695,-122.268545, true, true, 4.5),
                new Restaurant("La Note", 37.8661957,-122.2673496, true, true, 4),
                new Restaurant("The Jupiter", 37.86984,-122.267491, true, true, 4),
                new Restaurant("Chez Pannisse", 37.8795938,-122.2689348, true, true, 4),
                new Restaurant("McDonald's",37.87239,-122.268728, true, true, 2.5),
                new Restaurant("Bear's Ramen House", 37.8679625,-122.2579367, true, true, 3.5),
                new Restaurant("Cafe Strada", 37.8692854,-122.2546207,true, true, 4),
                new Restaurant("Thallasa", 37.86635,-122.267166, true, true, 4)
        };
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new RecommendationFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
