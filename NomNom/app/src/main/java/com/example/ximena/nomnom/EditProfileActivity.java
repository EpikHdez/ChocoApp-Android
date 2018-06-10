package com.example.ximena.nomnom;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.services.HerokuService;
import com.mindorks.placeholderview.PlaceHolderView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class EditProfileActivity extends AppCompatActivity implements IAPICaller {
    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    private static final int ADDRESS_USER_CODE = 500;
    ManagerUser managerUser;
    int flag_activity;
    private static final String RELATIVE_API = "my/user_address";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        managerUser=ManagerUser.getInstance();
        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView)findViewById(R.id.galleryView);
        setupDrawer();
        HerokuService.get(RELATIVE_API, ADDRESS_USER_CODE, this);

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

    public void openMap(){
        managerUser.setFlag_map(1);
        Intent activity = new Intent(this, MapsActivity.class);
        startActivity(activity);

    }
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home:
                flag_activity=0;
                openMap();
                break;

            case R.id.job:
                flag_activity=1;
                openMap();
                break;

            default:
                return;
        }
    }

    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        try {
            switch (requestCode) {
                case ADDRESS_USER_CODE:

                    Log.d("response", response.toString());
                    JSONArray addresses = response.getJSONArray("addresses");
                    TextView txtname = findViewById(R.id.name);
                    TextView txtemail = findViewById(R.id.email);
                    TextView txthome = findViewById(R.id.home);
                    TextView txtjob = findViewById(R.id.job);
                    ImageView imageView = findViewById(R.id.image);

                    txtname.setText(managerUser.getName() + " " + managerUser.getLastname());
                    txtemail.setText(managerUser.getEmail());
                    Picasso.with(this).load(managerUser.getPicture()).into(imageView);
                    if(addresses.length()==0) {
                        txthome.setText("No ha insertado su dirección.");
                        txtjob.setText("No ha insertado su dirección.");
                    }
                    break;
            }
        }catch (Exception e){}

    }

    @Override
    public void onFailure(int requestCode, Object error) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(flag_activity==0){
                TextView txthome = findViewById(R.id.home);

                txthome.setText(managerUser.getTempLatitud()+","+managerUser.getTempLongitude());

            }else{
                TextView txtjob = findViewById(R.id.job);
                txtjob.setText(managerUser.getTempLatitud()+","+managerUser.getTempLongitude());
            }


        }
    }
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        if(flag_activity==0){
            TextView txthome = findViewById(R.id.home);

            txthome.setText("Casa: "+managerUser.getTempLatitud()+", "+managerUser.getTempLongitude());

        }else{
            TextView txtjob = findViewById(R.id.job);
            txtjob.setText("Trabajo: "+managerUser.getTempLatitud()+", "+managerUser.getTempLongitude());
        }
    }
}
