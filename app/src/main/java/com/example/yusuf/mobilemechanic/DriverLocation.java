package com.example.yusuf.mobilemechanic;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;
import java.util.Map;

public class DriverLocation extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    String provider;
    Location mLastLocation;
    Requests request;
    TextView infoTextView;
    Button requestUberButton;
    static Boolean requestActive=false;
    String driverName="";

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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("My Location"));

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
                BackendlessDataQuery dataQuery2 = new BackendlessDataQuery();
                dataQuery2.setWhereClause( "email='"+driverName+"'" );
                Backendless.Persistence.of("Users").find(dataQuery2, new AsyncCallback<BackendlessCollection<Map>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Map> response) {
                        Iterator<Map> iterator = response.getCurrentPage().iterator();
                        while( iterator.hasNext() )
                        {
                            Map req = iterator.next();
                            GeoPoint p= (GeoPoint) req.get("location");
                            Toast.makeText(getApplicationContext(),""+p.getLatitude(),Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(getApplicationContext(),""+fault.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    public void requestMechanic(View view){
        request = new Requests();
        request.setMechanicUsername("");
        request.setRequesterUsername(Backendless.UserService.CurrentUser().getEmail());
        request.setMylocation(new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        if (requestActive == false) {

            Backendless.Persistence.of(Requests.class).save(request, new AsyncCallback<Requests>() {
                @Override
                public void handleResponse(Requests response) {
                    infoTextView.setText("Finding Mechanic nearby...");
                    requestUberButton.setText("Cancel Request");
                    requestActive = true;
                }

                @Override
                public void handleFault(BackendlessFault fault) {

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
                                        Toast.makeText(DriverLocation.this,+response+"", Toast.LENGTH_SHORT).show();
                                        infoTextView.setText("Request Cancelled");
                                        requestUberButton.setText("Request Mechanic");
                                        requestActive = false;
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(DriverLocation.this,fault.getMessage().toString(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

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

    @Override
    public void onProviderDisabled(String s) {

    }
}
