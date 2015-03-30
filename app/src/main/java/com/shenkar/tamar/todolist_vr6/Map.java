package com.shenkar.tamar.todolist_vr6;


import java.io.IOException;
        import java.util.List;
        import java.util.Locale;

        import com.google.android.gms.analytics.GoogleAnalytics;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapFragment;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.model.Circle;
        import com.google.android.gms.maps.model.CircleOptions;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Color;
        import android.location.Address;
        import android.location.Criteria;
        import android.location.Geocoder;
        import android.location.Location;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Toast;

public class Map extends Activity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapPoint mySelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Get a Tracker (should auto-report)
        //((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);

        try {
            // Loading map
            initilizeMap();



        } catch (Exception e) {
            e.printStackTrace();
        }


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                googleMap.clear();
                mySelection = new MapPoint(point.latitude,point.longitude);
                // create marker
                MarkerOptions marker = new MarkerOptions().position(point).title("Chosen Point");

                googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(point.latitude,point.longitude))
                        .radius(200)
                        .fillColor(0x40ff0000)  //semi-transparent
                        .strokeColor(Color.parseColor("#6ea0fc"))
                        .strokeWidth(5));


                // adding marker
                googleMap.addMarker(marker);
            }
        });
    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.mapg)).getMap();
            googleMap.setMyLocationEnabled(true);

            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.mapg);
            mapFragment.getMapAsync(this);



        }
    }



    private String getLocationName()
    {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);


        if(mySelection !=null)
        {
            double longitude = mySelection.getLat();
            double latitude = mySelection.getLng();
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try
            {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(null!=listAddresses&&listAddresses.size()>0)
                {
                    return listAddresses.get(0).getAddressLine(0);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }

        }
        return null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    private Location getMyLocation() {
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }

        return myLocation;
    }

    public void saveLocation(View view)
    {
        Intent returnIntent = new Intent();

        //DBHelper db = new DBHelper(this);
        //long res = db.addTask(task);
        //task.setTaskId(res);
        returnIntent.putExtra("latlng",mySelection);
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // TODO Auto-generated method stub
        Location myLocation = getMyLocation();
        if(myLocation==null)
            return;
        LatLng myLatLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,16));
        // check if map is created successfully or not
        if (googleMap == null) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }


    @Override
    public void onStart(){
        super.onStart();
        //GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        //GoogleAnalytics.getInstance(this).reportActivityStop(this);

    }

}