package com.cs160.fall13.MeatUp;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class RestaurantMapFragment extends SupportMapFragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    // Global constants
        /*
         * Define a request code to send to Google Play services
         * This code is returned in Activity.onActivityResult
         */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private Restaurant restaurant = null;
    private Location restLoc;
    private LocationClient mLocationClient;
    private Location mCurrentLocation;
    private Marker rinfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.restaurant = (Restaurant) getArguments().getSerializable(RecommendationFragment.RESTAURANT_ARGUMENT_KEY);
        this.restLoc = new Location(restaurant.getTitle());
        this.restLoc.setLatitude(restaurant.getLat());
        this.restLoc.setLongitude(restaurant.getLon());
        this.mLocationClient = new LocationClient(getActivity(), this, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupMap();
        return view;
    }

    private void setupMap() {
        if (restaurant != null) {
            GoogleMap map = getMap();
            LatLng location = new LatLng(restaurant.getLat(), restaurant.getLon());

            map.setMyLocationEnabled(true);
            Location myLoc = map.getMyLocation();
            if (myLoc != null) {
                myLoc.getLongitude();
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

            String distStr = "Calculating distance...";
            if (mLocationClient.isConnected()) {
                distStr = getDistanceToRestString();
            }

            rinfo = map.addMarker(new MarkerOptions()
                    .title(restaurant.getTitle())
                    .snippet(distStr)
                    .position(location));
            rinfo.showInfoWindow();
        }
    }

    private void updateMapSnippet() {
        String distanceStr = getDistanceToRestString();
        String dispStr = distanceStr + " (rating: " + restaurant.getRating() + "/5)";
        rinfo.setSnippet(dispStr);
        rinfo.showInfoWindow();
    }

    private String getDistanceToRestString() {
        double dist = -1;
        String distMeas = "meters";

        if (mCurrentLocation != null && restLoc != null){
            dist = mCurrentLocation.distanceTo(restLoc);

            if (dist > 500){
                dist *= 6.21371e-4;     // meters to miles
                distMeas = "miles";
            }
        }
        return String.format("%.1f %s away", dist, distMeas);
    }

    /*
    * Called when the Activity becomes visible.
    */
    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();

    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                /*
                 * If the result code is Activity.RESULT_OK, try
                 * to connect again
                 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        /*
                         * Try the request again
                         */
                        break;
                }
        }
    }

    // TODO use this method
    private boolean servicesConnected(ConnectionResult connectionResult) {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Get the error code
            int errorCode = connectionResult.getErrorCode();
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode,
                    getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                //  errorFragment.show(getSupportFragmentManager(),
                //          "Location Updates");
            }
            return true;
        }
    }

    /*
    * Called by Location Services when the request to connect the
    * client finishes successfully. At this point, you can
    * request the current location or start periodic updates
    */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        // Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(getActivity(), "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Implementation of {@link com.google.android.gms.location.LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (rinfo != null && mCurrentLocation != null) {
            updateMapSnippet();
        }
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            // TODO find showErrorDialog method
        }
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
