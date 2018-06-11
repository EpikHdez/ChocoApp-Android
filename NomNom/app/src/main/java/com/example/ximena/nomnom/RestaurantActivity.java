package com.example.ximena.nomnom;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.model.Restaurant;
import com.example.ximena.nomnom.services.HerokuService;
import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONObject;

import java.util.HashMap;

public class  RestaurantActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, IAPICaller {
    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    SliderLayout sliderLayout;
    HashMap<String,String> Hash_file_maps ;
    ManagerUser managerUser;
    private static final int FAVORITE_CODE = 100;
    private static final int SHOW_FAVORITE_CODE = 200;
    private static final int DELETE_FAVORITE_CODE = 300;
    private static final String RELATIVE_API = "my/favorite_place";

    boolean exist=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        managerUser=ManagerUser.getInstance();
        Restaurant restaurant=managerUser.getCurrentRestaurant();
        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView)findViewById(R.id.galleryView);
        setupDrawer();
        try{
            HerokuService.get(RELATIVE_API + "/" + restaurant.getId(), SHOW_FAVORITE_CODE, this);
            Hash_file_maps = restaurant.getPictures();
            if (Hash_file_maps.size() == 0) {
                Hash_file_maps.put("Android CupCake", "http://androidblog.esy.es/images/cupcake-1.png");
            }
            sliderLayout = (SliderLayout) findViewById(R.id.slider);


            for (String name : Hash_file_maps.keySet()) {

                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView
                        .description(name)
                        .image(Hash_file_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", name);
                sliderLayout.addSlider(textSliderView);
            }
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sliderLayout.setCustomAnimation(new DescriptionAnimation());
            sliderLayout.setDuration(3000);
            sliderLayout.addOnPageChangeListener(this);

            TextView location = findViewById(R.id.location);
            TextView name = findViewById(R.id.name);
            TextView type = findViewById(R.id.type);
            location.setText(restaurant.getLatitude() + ", " + restaurant.getLongitude());
            name.setText("Nombre: " + restaurant.getName());
            type.setText("Tipo: " + restaurant.getType());
        }catch (Exception e){}

    }
    public void openProducts(View view){
        Intent activity = new Intent(this, ProductsActivity.class);
        startActivity(activity);
    }
    public void changeFav(View view){
        try {
            JSONObject place = new JSONObject();
            place.put("place_id", managerUser.getCurrentRestaurant().getId());
            if(!exist) {
                HerokuService.post(RELATIVE_API, place, FAVORITE_CODE, this);
            }else {

                HerokuService.delete(RELATIVE_API + "/" + managerUser.getCurrentRestaurant().getId(), DELETE_FAVORITE_CODE, this);
            }
        }catch (Exception e){

        }
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
    protected void onStop() {

        sliderLayout.stopAutoCycle();

        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        Button favbtn=findViewById(R.id.addFav);
        try {
            switch (requestCode) {
                case FAVORITE_CODE:
                    favbtn.setBackgroundResource((R.drawable.lov3));
                    exist=true;
                    break;
                case DELETE_FAVORITE_CODE:
                    exist=false;
                    favbtn.setBackgroundResource((R.drawable.lov2));

                    break;
                case SHOW_FAVORITE_CODE:
                    Log.d("RESPONSE A", response.toString());
                    if(response.has("favorite_place")){
                        exist=true;
                        favbtn.setBackgroundResource((R.drawable.lov3));

                    }else{
                        favbtn.setBackgroundResource((R.drawable.lov2));
                        exist=false;
                    }
                    break;
            }
        }catch (Exception e){}
    }

    @Override
    public void onFailure(int requestCode, String error) {
        Button favbtn = findViewById(R.id.addFav);
        try {
            switch (requestCode) {
                case FAVORITE_CODE:
                    favbtn.setBackgroundResource((R.drawable.lov3));
                    exist = true;
                    break;
                case DELETE_FAVORITE_CODE:
                    exist = false;
                    favbtn.setBackgroundResource((R.drawable.lov2));

                    break;


            }
        } catch (Exception e) {
        }
    }



}
