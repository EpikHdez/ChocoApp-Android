package com.example.ximena.nomnom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.ximena.nomnom.model.Restaurant;
import com.mindorks.placeholderview.PlaceHolderView;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoriteRestaurantsActivity extends AppCompatActivity {
    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    ArrayList<Restaurant> dataModels;
    ListView listView;
    private static FavoriteRestaurantAdapter adapter;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_restaurants);

        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView)findViewById(R.id.galleryView);
        setupDrawer();

        listView=(ListView)findViewById(R.id.list_fav_resta);

        dataModels= new ArrayList<>();
        for(int i=0;i<20;i++) {
            HashMap<String,String> Hash_file_maps ;
            Hash_file_maps = new HashMap<String, String>();
            Hash_file_maps.put("Android CupCake", "http://androidblog.esy.es/images/cupcake-1.png");
            Hash_file_maps.put("Android Donut", "http://androidblog.esy.es/images/donut-2.png");
            Hash_file_maps.put("Android Eclair", "http://androidblog.esy.es/images/eclair-3.png");
            Hash_file_maps.put("Android Froyo", "http://androidblog.esy.es/images/froyo-4.png");
            Hash_file_maps.put("Android GingerBread", "http://androidblog.esy.es/images/gingerbread-5.png");
            dataModels.add(new Restaurant(1, "Restaurante", 50.0f, 50.0f, "comida",Hash_file_maps));
        }

        adapter= new FavoriteRestaurantAdapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Restaurant dataModel= dataModels.get(position);
                openRestaurant();

            }
        });

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
}
