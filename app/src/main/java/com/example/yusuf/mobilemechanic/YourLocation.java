package com.example.yusuf.mobilemechanic;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

public class YourLocation extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    String provider;
    Requests request;
    TextView infoTextView;
    Button requestUberButton;
    Location mLastLocation;
    Boolean requestActive = false;
    String driverUsername = "";
    GeoPoint driverLocation = new GeoPoint(0, 0);
    GoogleApiClient mGoogleApiClient = null;

    Handler handler = new Handler();

    public void requestUber(View view) {

        if (requestActive == false) {

            request = new Requests();
            request.setDriverUsername("");
            request.setRequesterUsername(Backendless.UserService.CurrentUser().getEmail());
            request.setMylocation(new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            Backendless.Persistence.of(Requests.class).save(request, new AsyncCallback<Requests>() {
                @Override
                public void handleResponse(Requests response) {
                    infoTextView.setText("Finding Uber driver...");
                    requestUberButton.setText("Cancel Uber");
                    requestActive = true;
                }

                @Override
                public void handleFault(BackendlessFault fault) {

                }
            });


        } else {

            infoTextView.setText("Uber Cancelled.");
            requestUberButton.setText("Request Uber");
            requestActive = false;

            Backendless.Persistence.of(Requests.class).remove(request);


        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        infoTextView = (TextView) findViewById(R.id.infoTextView);
        requestUberButton = (Button) findViewById(R.id.requestUber);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {


            updateLocation(location);

        }


    }






    public void updateLocation(final Location location) {

        mMap.clear();

        if (requestActive == false) {

            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause("requesterusername'" + Backendless.UserService.CurrentUser().getEmail() + "'");
            Backendless.Data.of(Requests.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Requests>>() {
                @Override
                public void handleResponse(BackendlessCollection<Requests> requestsBackendlessCollection) {
                    Log.i("Query", "Successful");
                    if(requestsBackendlessCollection != null){
                        Log.i("Collection", "Not Zero");
                        Iterator<Requests> iterator = requestsBackendlessCollection.getCurrentPage().iterator();

                        while (iterator.hasNext()) {
                            Requests requests = iterator.next();
//                            email = requests.driverUsername;
                            requestActive = true;
                            infoTextView.setText("Finding Uber driver...");
                            requestUberButton.setText("Cancel Uber");
                            if (requests.getDriverUsername() != null||!(requests.getDriverUsername().equals(""))) {

                                driverUsername =requests.getDriverUsername();
                                infoTextView.setText("Your driver is on their way!");
                                requestUberButton.setVisibility(View.INVISIBLE);

                                Log.i("AppInfo", driverUsername);


                            }
                        }

                    }else{
                        Log.i("Collection", "Zero");
                    }
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                }
            });





        }

        if (driverUsername.equals("")) {

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));

            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location"));

        }

        if (requestActive == true) {

            if (!driverUsername.equals("")) {

                Backendless.Data.of(BackendlessUser.class).find(new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<BackendlessUser> backendlessUserBackendlessCollection) {
                        if (backendlessUserBackendlessCollection != null) {
                            Iterator<BackendlessUser> iterator = backendlessUserBackendlessCollection.getCurrentPage().iterator();

                            while (iterator.hasNext()) {
                                BackendlessUser user = iterator.next();
                                if(user.getEmail().equals(driverUsername)){
                                    driverLocation = (GeoPoint)user.getProperty("location");

                                }

                            }
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.i("Error", "Failed Retrieval");
                    }
                });



                if (driverLocation.getLatitude() != 0 && driverLocation.getLongitude() != 0) {

                    Log.i("AppInfo", driverLocation.toString());

                    Double distanceInMiles = driverLocation.getDistance( );

                    Double distanceOneDP = (double) Math.round(distanceInMiles * 10) / 10;

                    infoTextView.setText("Your driver is " + distanceOneDP.toString() + " miles away ");


                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    ArrayList<Marker> markers = new ArrayList<Marker>();

                    markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Driver Location")));

                    markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location")));

                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }

                    LatLngBounds bounds = builder.build();

                    int padding = 100;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                    mMap.animateCamera(cu);
                }

            }


            final GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());


            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause("requesterusername'" + Backendless.UserService.CurrentUser().getEmail() + "'");
            Backendless.Data.of(Requests.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Requests>>() {
                @Override
                public void handleResponse(BackendlessCollection<Requests> requestsBackendlessCollection) {
                    Log.i("Query", "Successful");
                    if(requestsBackendlessCollection != null){
                        Log.i("Collection", "Not Zero");
                        Iterator<Requests> iterator = requestsBackendlessCollection.getCurrentPage().iterator();

                        while (iterator.hasNext()) {
                            Requests requests = iterator.next();
                            requests.setMylocation(userLocation);
                            Backendless.Persistence.of(Requests.class).save(requests);


                        }

                    }else{
                        Log.i("Collection", "Zero");
                    }
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                }
            });









        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateLocation(location);
            }
        }, 5000);


    }



    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(YourLocation.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }







    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermission();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null){
            Log.i("Google Api", String.valueOf(mLastLocation.getLatitude()));
            Log.i("Google Api", String.valueOf(mLastLocation.getLongitude()));
            onLocationChanged(mLastLocation);

        }else{
            Log.i("Google Api", "Babaji ka thulu");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
