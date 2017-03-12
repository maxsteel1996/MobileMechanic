package com.example.yusuf.mobilemechanic;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.Iterator;

public class ViewRequests extends AppCompatActivity implements LocationListener {
    EditText editMile;
    Button set;
    int mi=200;
    ListView listView;
    ArrayList<String> listViewContent;
//    ArrayList<String> usernames;
//    ArrayList<Double> latitudes;
//    ArrayList<Double> longitudes;
    ArrayAdapter arrayAdapter;
    LocationManager locationManager;
    String provider;
//
//    Location location;
//
//    LocationManager locationManager;
//    String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);
        editMile=(EditText)findViewById(R.id.setMile);
        set=(Button)findViewById(R.id.set);

        listView = (ListView) findViewById(R.id.listView);
        listViewContent = new ArrayList<String>();
        listViewContent.add("Finding nearby requests.....");
//        usernames = new ArrayList<String>();
//        latitudes = new ArrayList<Double>();
//        longitudes = new ArrayList<Double>();
//
//        listViewContent.add("Finding nearby requests..

        getSupportActionBar().setTitle("User Requests");
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listViewContent);

        listView.setAdapter(arrayAdapter);
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
        final Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {

            updateLocation(location);
        }

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editMile.getText().toString()!=""){
                    mi= Integer.parseInt(editMile.getText().toString());
                    updateLocation(location);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Intent i = new Intent(getApplicationContext(), ViewRaiderLocation.class);
//                i.putExtra("username", usernames.get(position));
//                i.putExtra("latitude", latitudes.get(position));
//                i.putExtra("longitude", longitudes.get(position));
//                i.putExtra("userLatitude", location.getLatitude());
//                i.putExtra("userLongitude", location.getLongitude());
//                startActivity(i);
//
//            }}
    }

    public void updateLocation(final Location location){
        final GeoPoint userLocation= new GeoPoint(location.getLatitude(),location.getLongitude());
        double myLatitude = location.getLatitude();
        double myLongitude = location.getLongitude();
        String query = "distance( %f, %f, mylocation.latitude, mylocation.longitude ) < mi("+mi+") and  MechanicUsername=''";
        String whereClause = String.format( query, myLatitude, myLongitude );

        BackendlessDataQuery dataQuery = new BackendlessDataQuery( whereClause );
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addRelated( "mylocation" );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Data.of( Requests.class ).find( dataQuery, new AsyncCallback<BackendlessCollection<Requests>>()
        {
            @Override
            public void handleResponse( BackendlessCollection<Requests> cars )
            {
                listViewContent.clear();
                if( cars.getCurrentPage().size() == 0 ) {

                    Toast.makeText(ViewRequests.this, "Did not find any cars", Toast.LENGTH_SHORT).show();

                } else{
                    Iterator<Requests> iterator = cars.getCurrentPage().iterator();

                while( iterator.hasNext() )
                {
                    Requests car = iterator.next();
//                    System.out.println( String.format( "Found car: %s %s", car.make, car.model ) );
                    float distance = getDistanceInMiles(car.getMylocation(), userLocation);
                    
                    listViewContent.add(car.requesterUsername+"  "+Math.round(distance * 100.0) / 100.0+" miles away");
                    arrayAdapter.notifyDataSetChanged();
                }

                }
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {
                listViewContent.clear();
                listViewContent.add(backendlessFault.getMessage());
                arrayAdapter.notifyDataSetChanged();
            }
        } );
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
    public void onLocationChanged(Location location) {
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

