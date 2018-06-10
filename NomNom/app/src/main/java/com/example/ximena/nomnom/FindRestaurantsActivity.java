package com.example.ximena.nomnom;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.model.Restaurant;
import com.example.ximena.nomnom.services.HerokuService;
import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FindRestaurantsActivity extends AppCompatActivity implements IAPICaller {
    private static final int SEARCH_PLACES_CODE = 100;
    private static final String RELATIVE_PATH = "search";

    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    private SearchView searchView;
    private GridView placesList;

    private ArrayList<Restaurant> searchResults;
    private ManagerUser managerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_restaurants);

        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView)findViewById(R.id.galleryView);
        setupDrawer();

        searchView = findViewById(R.id.SearchPlace);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        placesList = findViewById(R.id.list_disc_resta);
        managerUser = ManagerUser.getInstance();
    }
    private void setupDrawer(){
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

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer){
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

    private void search(String query) {
        String endPoint = String.format("%s/%s", RELATIVE_PATH, query);
        HerokuService.find(endPoint, SEARCH_PLACES_CODE, this);
    }

    private void displayPlaces(JSONObject response) {
        JSONArray places = response.optJSONArray("places");
        searchResults = new ArrayList<>();

        for(int index = 0; index < places.length(); index++) {
            JSONObject rest = places.optJSONObject(index);
            int id=rest.optInt("id");
            String name=rest.opt("name").toString();
            JSONObject address=rest.optJSONObject("address");
            float latitude=Float.valueOf(Double.toString(address.optDouble("latitude")));
            float longitude=Float.valueOf(Double.toString(address.optDouble("longitude")));
            JSONObject address_type=address.optJSONObject("address_type");
            String type=address_type.optString("name");
            JSONArray picturesJSON=rest.optJSONArray("pictures");
            HashMap<String, String> pictures=new HashMap<>();
            for(int j=0;j<picturesJSON.length();j++) {
                JSONObject picture=picturesJSON.optJSONObject(j);
                String url=picture.optString("url");
                pictures.put(name+(j+1), url);
            }
            Restaurant restaurant=new Restaurant(id,name,latitude,longitude,type,pictures);
            searchResults.add(restaurant);
        }

        RestaurantAdapter adapter = new RestaurantAdapter(searchResults, getApplicationContext());

        placesList.setAdapter(adapter);
        placesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Restaurant dataModel = searchResults.get(position);
                managerUser.setCurrentRestaurant(dataModel);
                openRestaurant();
            }
        });
    }

    public void openRestaurant(){
        Intent activity = new Intent(this, RestaurantActivity.class);
        startActivity(activity);
    }

    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        switch (requestCode) {
            case SEARCH_PLACES_CODE:
                displayPlaces(response);
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, String error) {

    }
}
