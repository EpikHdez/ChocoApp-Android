package com.example.ximena.nomnom;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.services.HerokuService;
import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigActivity extends AppCompatActivity implements IAPICaller {
    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    private static final int LOCAL_USER_CODE = 400;
    private static final int HOME_USER_CODE = 400;
    private static final int JOB_USER_CODE = 400;
    private static final int ADDRESS_USER_CODE = 500;
    private static final String RELATIVE_API1 = "my/user_address";
    private double latHome, longHome, latJob, longJob;
    ManagerUser managerUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        managerUser= ManagerUser.getInstance();
        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView)findViewById(R.id.galleryView);
        setupDrawer();
        HerokuService.get(RELATIVE_API1, ADDRESS_USER_CODE, this);

    }
    public void localPosition(View view){
        managerUser.setTempLongitude(0);
        managerUser.setTempLatitud(0);
        Intent activity = new Intent(this, PointsActivity.class);
        startActivity(activity);

    }
    public void homePosition(View view){
        managerUser.setTempLongitude((float) longHome);
        managerUser.setTempLatitud((float) latHome);
        Intent activity = new Intent(this, PointsActivity.class);
        startActivity(activity);

    }
    public void jobPosition(View view){
        managerUser.setTempLongitude((float) longJob);
        managerUser.setTempLatitud((float) latJob);
        Intent activity = new Intent(this, PointsActivity.class);
        startActivity(activity);

    }
    public void openHelp(View view) {
        Intent activity = new Intent(this, HelpActivity.class);
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

    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        try {
            switch (requestCode) {
                case ADDRESS_USER_CODE:
                    JSONArray jsonObject=response.getJSONArray("addresses");
                    Log.d("REPONSE", response.toString());
                    JSONObject home=jsonObject.getJSONObject(0);
                    JSONObject job=jsonObject.getJSONObject(1);
                    latHome=home.getDouble("latitude");
                    latJob=job.getDouble("latitude");
                    longHome=home.getDouble("longitude");
                    longJob=job.getDouble("longitude");
                    break;

            }
        }catch (Exception e){

        }
    }

    @Override
    public void onFailure(int requestCode, String error) {

    }
}
