package com.cs160.fall13.MeatUp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class TabSetup {
    public static void setupTabs(Class<?>[] classes, String[] names, ActionBarActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPager pager = (ViewPager) activity.findViewById(R.id.pager);
        TabPageAdapter adapter = new TabPageAdapter(activity.getSupportFragmentManager(), classes);
        pager.setAdapter(adapter);

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Make it split (put actions on bottom)
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setDisplayShowTitleEnabled(false);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new NamedTabListener(names, pager, actionBar);
        for (String name : names) {
            actionBar.addTab(actionBar.newTab().setText(name).setTabListener(tabListener));
        }
    }

    private static class TabPageAdapter extends FragmentPagerAdapter {
        private final Class<?>[] classes;

        public TabPageAdapter(FragmentManager fm, Class<?>[] classes) {
            super(fm);
            this.classes = classes;
        }

        @Override
        public Fragment getItem(int i) {
            Class<?> aClass = classes[i];
            try {
                Object instance = aClass.newInstance();
                if (!(instance instanceof Fragment)) {
                    throw new RuntimeException("Class passed to tab page adapter not a fragment");
                }
                return (Fragment) instance;
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not instantiate tab");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not instantiate tab");
            }
        }

        @Override
        public int getCount() {
            return classes.length;
        }
    }

    private static class NamedTabListener implements ActionBar.TabListener {
        private String[] names;
        private ViewPager pager;

        private static final String TAG = "MeatUp.NamedTabListener";

        private NamedTabListener(String[] names, ViewPager pager, final ActionBar actionBar) {
            this.names = names;
            this.pager = pager;

            pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    actionBar.setSelectedNavigationItem(position);
                }
            });
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            for (int i = 0; i < names.length; i++) {
                if (names[i].equals(tab.getText())) {
                    pager.setCurrentItem(i);
                    return;
                }
            }
            // Should not happen
            Log.e(TAG, "Selected a non-existent tab!");
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // hides the given tab. Ignore this.
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // probably ignore this event
        }
    }
}
