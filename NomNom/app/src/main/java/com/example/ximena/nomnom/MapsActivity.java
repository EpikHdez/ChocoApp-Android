package com.example.ximena.nomnom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.cloudinary.android.Logger;
import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.services.HerokuService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mindorks.placeholderview.PlaceHolderView;

import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, IAPICaller {
    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;

    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    ManagerUser manager;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<MarkerOptions> markers;
    private Location loc;
    int flag_map = 0;
    private static final int NEARBY_USER_CODE = 400;
    private static final String RELATIVE_API = "nearby";
    int flag = NEARBY_USER_CODE;
    Location locat;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("asdfasdfasdS", String.format("%f, %f", location.getLatitude(), location.getLongitude()));
                drawMarker(location);
                mLocationManager.removeUpdates(mLocationListener);
                loc = location;
            } else {
                Log.d("asdfasdff", "Location is null");
            }
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
    };

    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        manager = ManagerUser.getInstance();
        flag_map = manager.getFlag_map();
        mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView) findViewById(R.id.drawerView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView) findViewById(R.id.galleryView);
        markers = new ArrayList<MarkerOptions>();
        setupDrawer();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initMap();
        getCurrentLocation();


    }

    private void setupDrawer() {
        mDrawerView
                .addView(new DrawerHeader())
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_HOME))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_PROFILE))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_FAVORITES))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_FIND))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SEARCH))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_MAP))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_CONFIG))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_LOGOUT));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (flag_map==0) {
            restaurantsMap();

        }else{
            profileMap();
        }
    }

    public void restaurantsMap() {
        markerAddRestaurant();
        putPositions();
        onMapClickAddRestaurant();
    }

    public void profileMap() {
        returnToProfile(mMap);
        changeMarkerPosition(mMap);
    }

    private void initMap() {
        int googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (googlePlayStatus != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, this, -1).show();
            finish();
        } else {
            if (mMap != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setAllGesturesEnabled(true);
            }
        }
    }

    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled))
            Toast.makeText(this, "asdfajdkgadkfg", Toast.LENGTH_SHORT).show();
        else {
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null) {
            Log.d("asjfhajkdgsdfgs",String.format("getCurrentLocation(%f, %f)", location.getLatitude(),
                    location.getLongitude()));
            drawMarker(location);

        }
        locat=location;


    }

    private void drawMarker(Location location) {
        if (mMap != null) {
            mMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("Current Position"));
            markers.add(new MarkerOptions()
                    .position(gps)
                    .title("Current Position"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 17));
            try {
                JSONObject location1 = new JSONObject();
                location1.put("latitude", locat.getLatitude());
                location1.put("longitude", locat.getLongitude());
                location1.put("radius", 50.0f);
                HerokuService.post(RELATIVE_API, location1, NEARBY_USER_CODE, this);
            }catch (Exception e){}
        }

    }

    public void returnToProfile( GoogleMap map){
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                manager.setTempLatitud(Float.valueOf(String.valueOf(marker.getPosition().latitude)));
                manager.setTempLongitude(Float.valueOf(String.valueOf(marker.getPosition().longitude)));
                finish();

                return false;
            }
        });

    }

    public void changeMarkerPosition(GoogleMap map){
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude)).title("New Marker");
                mMap.addMarker(marker);

            }
        });
    }

    public void showNearby(JSONArray jsonArray){
        try{
        for(int i=0; i<jsonArray.length();i++) {
            JSONObject rest= (JSONObject) jsonArray.get(i);
            String name=rest.get("name").toString();
            Log.d("name",name);
            JSONObject address=rest.getJSONObject("address");
            double longitude= address.getDouble("longitude");
            double latitude= address.getDouble("latitude");
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(name);
            markers.add(marker);
            mMap.addMarker(marker);
            Log.d("SIP","Si se agrego");
        }}catch (Exception e){}
    }
    public void putPositions(){
        for (MarkerOptions m : markers) {
            mMap.addMarker(m);
        }
    }

    public void markerAddRestaurant(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                manager.setCurrentLatitud(Float.valueOf(String.valueOf(marker.getPosition().latitude)));
                manager.setCurrentLongitude(Float.valueOf(String.valueOf(marker.getPosition().longitude)));
                //finish();
                if(marker.getTitle().equals("Current Position")||marker.getTitle().equals("New Marker")){
                    openAddRestaurant();
               }else{
                    opeRestaurant();
                }
                return false;
            }
        });
    }

    public void onMapClickAddRestaurant(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //mMap.clear();
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude)).title("New Marker");
                mMap.addMarker(marker);
                markers.add(marker);
                manager.setCurrentLatitud(Float.valueOf(String.valueOf(point.latitude)));
                manager.setCurrentLongitude(Float.valueOf(String.valueOf(point.longitude)));

                openAddRestaurant();

                System.out.println(point.latitude + "---" + point.longitude);
            }
        });
    }

    public void openAddRestaurant(){
        Intent activity = new Intent(this, AddRestaurantActivity.class);
        startActivity(activity);

    }

    public void opeRestaurant(){
        Intent activity = new Intent(this, RestaurantActivity.class);
        startActivity(activity);

    }
    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        Log.d("Si llamo","si");
        try {
            switch (requestCode) {
                case NEARBY_USER_CODE:
                    Log.d("nearby", response.toString());
                    JSONArray places = response.getJSONArray("places");
                    showNearby(places);
                    break;
            }
        }catch(Exception e){

        }

    }

    @Override
    public void onFailure(int requestCode, String error) {

    }
}
