package com.example.ximena.nomnom;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;

import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.model.Restaurant;
import com.example.ximena.nomnom.services.HerokuService;
import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PointsActivity extends AppCompatActivity implements LocationListener,IAPICaller {
    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    ArrayList<Restaurant> dataModels;
    GridView listView;
    private static RestaurantAdapter adapter;
    LocationManager locationManager;
    String provider;
    private static final int NEARBY_USER_CODE = 400;

    private static final String RELATIVE_API = "nearby";
    int flag_location;
    ManagerUser managerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        managerUser = ManagerUser.getInstance();
        mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView) findViewById(R.id.drawerView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView) findViewById(R.id.galleryView);
        setupDrawer();
        listView = (GridView) findViewById(R.id.list_disc_resta);
        Log.d("Vamos a ver",Float.toString(managerUser.getTempLatitud()));
        if(managerUser.getTempLatitud()==0 && managerUser.getTempLongitude()==0) {
            getCurrentLocation();
            flag_location=0;
        }else{
            try {
                flag_location = 1;
                JSONObject location1 = new JSONObject();
                Log.d("latidu", String.valueOf(managerUser.getTempLatitud()));
                Log.d("latidu", String.valueOf(managerUser.getTempLongitude()));
                location1.put("latitude",Double.valueOf(String.valueOf(managerUser.getTempLatitud())));
                location1.put("longitude",Double.valueOf(String.valueOf(managerUser.getTempLongitude())));
                location1.put("radius", 50.0f);
                HerokuService.post(RELATIVE_API, location1, NEARBY_USER_CODE, this);
            }catch (Exception e){

            }
        }

    }

    public void getCurrentLocation(){
        // Getting LocationManager object
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null && !provider.equals("")) {

            // Get the location from the given provider
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
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 20000, 1, this);

            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }
    }

    public void openRestaurant() {
        Intent activity = new Intent(this, RestaurantActivity.class);
        startActivity(activity);
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

    @Override
    public void onLocationChanged(Location location) {
        try {
            if(flag_location==0) {
                Log.d("Latitude", Double.toString(location.getLatitude()));
                Log.d("Longitude", Double.toString(location.getLongitude()));
                JSONObject location1 = new JSONObject();
                location1.put("latitude", location.getLatitude());
                location1.put("longitude", location.getLongitude());
                location1.put("radius", 50.0f);
                HerokuService.post(RELATIVE_API, location1, NEARBY_USER_CODE, this);
            }else{
                Log.d("Latitude", Double.toString(location.getLatitude()));
                Log.d("Longitude", Double.toString(location.getLongitude()));
                JSONObject location1 = new JSONObject();
                location1.put("latitude", managerUser.getTempLatitud());
                location1.put("longitude", managerUser.getTempLongitude());
                location1.put("radius", 50.0f);
                HerokuService.post(RELATIVE_API, location1, NEARBY_USER_CODE, this);

            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public ArrayList<Restaurant> convertToRestaurants(JSONArray jsonArray) {
        ArrayList<Restaurant> restaurants_JSON = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rest = jsonArray.getJSONObject(i);
                int id = rest.getInt("id");
                String name = rest.get("name").toString();
                JSONObject address = rest.getJSONObject("address");
                float latitude = Float.valueOf(Double.toString(address.getDouble("latitude")));
                float longitude = Float.valueOf(Double.toString(address.getDouble("longitude")));
                JSONObject address_type = address.getJSONObject("address_type");
                String type = address_type.getString("name");
                JSONArray picturesJSON = rest.getJSONArray("pictures");
                HashMap<String, String> pictures = new HashMap<>();
                for (int j = 0; j < picturesJSON.length(); j++) {
                    JSONObject picture = picturesJSON.getJSONObject(j);
                    String url = picture.getString("url");
                    pictures.put(name + (j + 1), url);
                }
                Restaurant restaurant = new Restaurant(id, name, latitude, longitude, type, pictures);
                restaurants_JSON.add(restaurant);

            }
        } catch (Exception e) {
        }
        return restaurants_JSON;

    }

    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        Log.d("Si llamo", "si");
        try {
            switch (requestCode) {

                case NEARBY_USER_CODE:
                    Log.d("response", response.toString());
                    JSONArray places = response.getJSONArray("places");
                    Log.d("places", places.toString());
                    convertToRestaurants(places);
                    dataModels = convertToRestaurants(places);

                    adapter = new RestaurantAdapter(dataModels, getApplicationContext());

                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Restaurant dataModel = dataModels.get(position);
                            managerUser.setCurrentRestaurant(dataModel);
                            openRestaurant();

                        }
                    });

                    break;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onFailure(int requestCode, String error) {

    }
}
