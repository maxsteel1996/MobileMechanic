package com.example.yusuf.mobilemechanic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewRequests extends AppCompatActivity {

    ListView listView;
//    ArrayList<String> listViewContent;
//    ArrayList<String> usernames;
//    ArrayList<Double> latitudes;
//    ArrayList<Double> longitudes;
    ArrayAdapter arrayAdapter;
//
//    Location location;
//
//    LocationManager locationManager;
//    String provider;
String[] types={"25 miles away","30 miles away"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        listView = (ListView) findViewById(R.id.listView);
//        listViewContent = new ArrayList<String>();
//        usernames = new ArrayList<String>();
//        latitudes = new ArrayList<Double>();
//        longitudes = new ArrayList<Double>();
//
//        listViewContent.add("Finding nearby requests..
        getSupportActionBar().setTitle("User Requests");
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types);

        listView.setAdapter(arrayAdapter);
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
    }}


//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        provider = locationManager.getBestProvider(new Criteria(), false);
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(provider, 400, 1, this);
//
//        location = locationManager.getLastKnownLocation(provider);
//
//        if (location != null) {
//
//
//            updateLocation();
//
//        }
//
//
//
//
//    }
//
//
//    public void updateLocation() {
//
//        final GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

//        ParseUser.getCurrentUser().put("location", userLocation);
//        ParseUser.getCurrentUser().saveInBackground();
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Requests");
//
//        query.whereNear("requesterLocation", userLocation);
//        query.setLimit(100);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//
//                if (e == null) {
//
//                    Log.i("MyApp", objects.toString());
//
//                    if (objects.size() > 0) {
//
//                        listViewContent.clear();
//                        usernames.clear();
//                        latitudes.clear();
//                        longitudes.clear();
//
//                        for (ParseObject object : objects) {
//
//                            if (object.get("driverUsername") == null) {
//
//                                Log.i("MyApp", object.toString());
//
//                                Double distanceInMiles = userLocation.distanceInMilesTo((ParseGeoPoint) object.get("requesterLocation"));
//
//                                Double distanceOneDP = (double) Math.round(distanceInMiles * 10) / 10;
//
//                                listViewContent.add(distanceOneDP.toString() + " miles");
//
//                                usernames.add(object.getString("requesterUsername"));
//                                latitudes.add(object.getParseGeoPoint("requesterLocation").getLatitude());
//                                longitudes.add(object.getParseGeoPoint("requesterLocation").getLongitude());
//
//                            }
//
//                        }
//
//                        arrayAdapter.notifyDataSetChanged();
//
//
//                    }
//
//
//                }
//
//            }
////        });
//
//    }




//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        // locationManager.removeUpdates(this);
//
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//
//
//        updateLocation();
//
//
//    }

//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }
//}
