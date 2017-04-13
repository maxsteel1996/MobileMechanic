package com.example.yusuf.mobilemechanic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
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

public class ViewRequesterLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Intent intent;

    public void back(View v){
        Intent i=new Intent(getApplicationContext(),ViewRequests.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(getApplicationContext(),ViewRequests.class);
        startActivity(i);
    }

    public void acceptRequest(View v){
        String whereClause = "requesterUsername = '"+intent.getStringExtra("username")+"'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause( whereClause );
        Backendless.Data.of(Requests.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Requests>>() {
            @Override
            public void handleResponse(BackendlessCollection<Requests> response) {
                Iterator<Requests> iterator = response.getCurrentPage().iterator();

                while( iterator.hasNext() )
                {
                    Requests req = iterator.next();
                    Toast.makeText(ViewRequesterLocation.this,req.getObjectId(), Toast.LENGTH_SHORT).show();
                    req.setMechanicUsername(Backendless.UserService.CurrentUser().getEmail());
                    Backendless.Persistence.of(Requests.class).save(req, new AsyncCallback<Requests>() {
                        @Override
                        public void handleResponse(Requests response) {
                            Toast.makeText(ViewRequesterLocation.this,"Succesful", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?daddr=" + intent.getDoubleExtra("longitude", 0) + "," + intent.getDoubleExtra("lattitude", 0)));
                                        startActivity(i);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(ViewRequesterLocation.this,fault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(ViewRequesterLocation.this,fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requester_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intent=getIntent();

        RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.relativeLayout);


        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                ArrayList<Marker> markers = new ArrayList<Marker>();

                markers.add( mMap.addMarker(new MarkerOptions().position(new LatLng(intent.getDoubleExtra("longitude",0), intent.getDoubleExtra("lattitude",0))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(intent.getStringExtra("username"))));
                markers.add( mMap.addMarker(new MarkerOptions().position(new LatLng(intent.getDoubleExtra("userLat",0), intent.getDoubleExtra("userLon",0))).title("You")));

                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }

                LatLngBounds bounds = builder.build();

                int padding = 100;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                mMap.animateCamera(cu);



            }
        });

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
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//        ArrayList<Marker> markers = new ArrayList<Marker>();
//        markers.add( mMap.addMarker(new MarkerOptions().position(new LatLng(intent.getDoubleExtra("longitude",0), intent.getDoubleExtra("lattitude",0))).title(intent.getStringExtra("username"))));
//        markers.add( mMap.addMarker(new MarkerOptions().position(new LatLng(intent.getDoubleExtra("userLat",0), intent.getDoubleExtra("userLon",0))).title("You")));
//        for (Marker marker : markers) {
//            builder.include(marker.getPosition());
//        }
//
//        LatLngBounds bounds = builder.build();
//
//        int padding = 100;
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//
//        mMap.animateCamera(cu);



    }
}
