package com.example.ximena.nomnom;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.model.New;
import com.example.ximena.nomnom.model.Restaurant;
import com.example.ximena.nomnom.services.HerokuService;
import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements IAPICaller {
    private static final int GET_NEWS_CODE = 100;

    private static final String RELATIVE_ENDPOINT = "news";

    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    private ProgressDialog progressDialog;

    ArrayList<New> dataModels;
    ListView listView;
    private NewsAdapter adapter;
    ManagerUser managerUser;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView)findViewById(R.id.galleryView);
        setupDrawer();
        managerUser=ManagerUser.getInstance();
        managerUser.setCurrentContext(this);
        listView=(ListView)findViewById(R.id.list_news);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Obteniendo noticias");

        dataModels= new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.show();
        HerokuService.get(RELATIVE_ENDPOINT, GET_NEWS_CODE, this);
    }

    private void displayNews(JSONObject response) {
        JSONArray allNews = response.optJSONArray("news");
        JSONObject item;

        for(int index = 0; index < allNews.length(); index++) {
            item = allNews.optJSONObject(index);
            dataModels.add(new New(item.optString("title"), item.optString("body")));
        }

        adapter = new NewsAdapter(dataModels, this);
        listView.setAdapter(adapter);
        progressDialog.dismiss();
    }

    public void openMap() {
        Intent activity = new Intent(this, MapsActivity.class);
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
        switch (requestCode) {
            case GET_NEWS_CODE:
                displayNews(response);
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, String error) {
        progressDialog.dismiss();
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
