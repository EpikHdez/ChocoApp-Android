package com.example.ximena.nomnom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.model.Restaurant;
import com.example.ximena.nomnom.services.HerokuService;
import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoriteRestaurantsActivity extends AppCompatActivity implements IAPICaller {
    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    ArrayList<Restaurant> dataModels;
    ListView listView;
    private static FavoriteRestaurantAdapter adapter;
    private static final int FAVS_USER_CODE = 400;
    private static final String RELATIVE_API = "my/favorite_place";
    ManagerUser managerUser;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_restaurants);
        managerUser=ManagerUser.getInstance();
        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView)findViewById(R.id.galleryView);
        setupDrawer();
        HerokuService.get(RELATIVE_API,  FAVS_USER_CODE, this);

        listView=(ListView)findViewById(R.id.list_fav_resta);



    }
    public void openRestaurant(){
        Intent activity = new Intent(this, RestaurantActivity.class);
        startActivity(activity);
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
    public ArrayList<Restaurant> convertToRestaurants(JSONArray jsonArray){
        ArrayList<Restaurant> restaurants_JSON= new ArrayList<>();
        try{
            for(int i=0;i<jsonArray.length();i++){
                JSONObject rest1=jsonArray.getJSONObject(i);
                JSONObject rest=rest1.getJSONObject("place");
                int id=rest.getInt("id");
                String name=rest.get("name").toString();
                JSONObject address=rest.getJSONObject("address");
                float latitude=Float.valueOf(Double.toString(address.getDouble("latitude")));
                float longitude=Float.valueOf(Double.toString(address.getDouble("longitude")));
                JSONObject address_type=address.getJSONObject("address_type");
                String type=address_type.getString("name");
                JSONArray picturesJSON=rest.getJSONArray("pictures");
                HashMap<String, String> pictures=new HashMap<>();
                for(int j=0;j<picturesJSON.length();j++) {
                    JSONObject picture=picturesJSON.getJSONObject(j);
                    String url=picture.getString("url");
                    pictures.put(name+(j+1), url);
                }
                Restaurant restaurant=new Restaurant(id,name,latitude,longitude,type,pictures);
                restaurants_JSON.add(restaurant);

            }
        }catch (Exception e){}
        return  restaurants_JSON;

    }
    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        Log.d("Si llamo","si");
        try {
            switch (requestCode) {
                case FAVS_USER_CODE:
                    Log.d("response",response.toString());
                    JSONArray places = response.getJSONArray("favorite_places");
                    Log.d("places", places.toString());

                    dataModels =  convertToRestaurants(places);
                    Log.d("dataModels",dataModels.toString());
                    adapter = new FavoriteRestaurantAdapter(dataModels, getApplicationContext());

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
        }catch(Exception e){

        }
    }

    @Override
    public void onFailure(int requestCode, String error) {

    }
}
