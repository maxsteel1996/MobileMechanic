package com.example.yusuf.mobilemechanic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

public class DriverLocation extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    Button button;
    private GoogleMap mMap;
    LocationManager locationManager;
    String provider;
    Location mLastLocation;
    Requests request;
    String vechile="";
    String problem="";
    TextView infoTextView;
    Button requestUberButton;
    static Boolean requestActive=false;
    String driverName="";
    GeoPoint driverLocation = new GeoPoint(0,0);
    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(DriverLocation.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
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

    public void updateLocation(Location location){
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause( "requesterUsername='"+Backendless.UserService.CurrentUser()+"'" );
        if(requestActive==false){

            Backendless.Data.of(Requests.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Requests>>() {
                @Override
                public void handleResponse(BackendlessCollection<Requests> response) {
                    Iterator<Requests> iterator = response.getCurrentPage().iterator();

                    while( iterator.hasNext() )
                    {
                        Requests req = iterator.next();
                        requestActive=true;
                        infoTextView.setText("Finding Mechanic nearby...");
                        requestUberButton.setText("Cancel Request");
                        if((req.getMechanicUsername() != null) || (req.getMechanicUsername() != "")){
                            driverName=req.getMechanicUsername();
                            infoTextView.setText(req.getMechanicUsername());
                            requestUberButton.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(getApplicationContext(),fault.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (driverName.equals("")) {

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));

            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your Location"));

        }

        if(requestActive==true){

            Backendless.Data.of(Requests.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Requests>>() {
                @Override
                public void handleResponse(BackendlessCollection<Requests> response) {
                    Iterator<Requests> iterator = response.getCurrentPage().iterator();

                    while( iterator.hasNext() )
                    {
                        Requests req = iterator.next();

                        if((req.getMechanicUsername() != null) || (req.getMechanicUsername() != "")){
                            driverName=req.getMechanicUsername();
                            infoTextView.setText(req.getMechanicUsername());
                            requestUberButton.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                }
            });

            if(!driverName.equals("")){
                Backendless.Data.of(BackendlessUser.class).find(new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<BackendlessUser> backendlessUserBackendlessCollection) {
                        if (backendlessUserBackendlessCollection != null) {
                            Iterator<BackendlessUser> iterator = backendlessUserBackendlessCollection.getCurrentPage().iterator();

                            while (iterator.hasNext()) {
                                BackendlessUser user = iterator.next();
                                if(user.getEmail().equals(driverName)){
                                    GeoPoint geoPoint = (GeoPoint)user.getProperty("location");
                                    driverLocation.setLatitude(geoPoint.getLatitudeE6()/1E6);
                                    driverLocation.setLatitude(geoPoint.getLongitudeE6()/1E6);
                                }

                            }
                        }
                    }   @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.i("Error", "Failed Retrieval");
                    }
                });
                if (driverLocation.getLatitude() != 0 && driverLocation.getLongitude() != 0) {

                    float distance = getDistanceInMiles(driverLocation, new GeoPoint(location.getAltitude(),location.getLongitude()));
//                    float distance = getDistanceInMiles(, driverLocation);
//                    Double distanceInMiles = driverLocation.distanceInMilesTo(new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
//
                    Double distanceOneDP = (double) Math.round(distance * 10) / 10;

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
        }

    }

    public void requestMechanic(View view){
        button.setEnabled(false);
        request = new Requests();
        request.setMechanicUsername("");
        request.setRequesterUsername(Backendless.UserService.CurrentUser().getEmail());
        request.setVechile(vechile);
        request.setProblem(problem);
        request.setMylocation(new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        if (requestActive == false) {

            Backendless.Persistence.of(Requests.class).save(request, new AsyncCallback<Requests>() {
                @Override
                public void handleResponse(Requests response) {
                    button.setEnabled(true);
                    infoTextView.setText("Finding Mechanic nearby...");
                    requestUberButton.setText("Cancel Request");
                    requestActive = true;
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                button.setEnabled(true);
                }
            });


        } else
        {




            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause( "requesterUsername = '"+Backendless.UserService.CurrentUser().getEmail()+"'" );





            Backendless.Data.of( Requests.class ).find(dataQuery, new AsyncCallback<BackendlessCollection<Requests>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<Requests> response) {
                            Iterator<Requests> iterator = response.getCurrentPage().iterator();
//                pDialog.dismiss();

                            while( iterator.hasNext() )
                            {
                                Requests person = iterator.next();
//                                Toast.makeText(DriverLocation.this,person.getObjectId(), Toast.LENGTH_SHORT).show();
                                Backendless.Persistence.of(Requests.class).remove(person, new AsyncCallback<Long>() {
                                    @Override
                                    public void handleResponse(Long response) {

                                        infoTextView.setText("Request Cancelled");
                                        requestUberButton.setText("Request Mechanic");
                                        requestActive = false;
                                        button.setEnabled(true);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        button.setEnabled(true);
                                        Toast.makeText(DriverLocation.this,fault.getMessage().toString(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            button.setEnabled(true);
                            Toast.makeText(DriverLocation.this,fault.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        infoTextView=(TextView)findViewById(R.id.infoTextView);
        requestUberButton=(Button)findViewById(R.id.requestM);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        button=(Button)findViewById(R.id.requestM);
        Intent intent=getIntent();
        problem=intent.getStringExtra("p");
        vechile=intent.getStringExtra("v");
        provider = locationManager.getBestProvider(new Criteria(), false);
        checkPermission();
        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkPermission();
        Location location = locationManager.getLastKnownLocation(provider);
        mLastLocation=location;
        if (location != null) {

            updateLocation(location);
        }
        // Add a marker in Sydney and move the camera
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkPermission();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        mMap.clear();
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }
    public float getDistanceInMiles(GeoPoint p1, GeoPoint p2) {
        double lat1 = ((double)p1.getLatitudeE6()) / 1e6;
        double lng1 = ((double)p1.getLongitudeE6()) / 1e6;
        double lat2 = ((double)p2.getLatitudeE6()) / 1e6;
        double lng2 = ((double)p2.getLongitudeE6()) / 1e6;
        float [] dist = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, dist);
        return dist[0] * 0.000621371192f;
    }
    @Override
    public void onProviderDisabled(String s) {

    }
}
