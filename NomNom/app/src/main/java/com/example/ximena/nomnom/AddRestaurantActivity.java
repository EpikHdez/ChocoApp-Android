package com.example.ximena.nomnom;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.services.CloudinaryService;
import com.example.ximena.nomnom.services.HerokuService;
import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class AddRestaurantActivity extends AppCompatActivity
        implements BaseSliderView.OnSliderClickListener,
                   ViewPagerEx.OnPageChangeListener,
                    IAPICaller {
    private static final int SELECT_PLACE_TYPES = 100;
    private static final int SAVE_PLACE = 300;
    private static final int UPLOAD_IMAGES = 400;
    private static final String PLACE_TYPES_PATH = "place_category";
    private static final String SAVE_PLACE_PATH = "place";

    private int imageCount;

    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    private Spinner placeTypesSpinner;
    private EditText placeNameEditText;

    private LinkedList<Integer> placeTypesIds;
    private int selectedPlaceTypeId;
    private String placeName;
    private LinkedList<Uri> picturesUris;
    private JSONArray picturesUrls;

    SliderLayout sliderLayout;
    HashMap<String,String> Hash_file_maps ;
    ManagerUser managerUser;
    String imageEncoded;
    List<String> imagesEncodedList;
    private static final int OPEN_IMAGE_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        managerUser=ManagerUser.getInstance();
        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mGalleryView = (PlaceHolderView)findViewById(R.id.galleryView);
        placeTypesSpinner = findViewById(R.id.APPlaceTypesSpinner);
        placeNameEditText = findViewById(R.id.APNameEditText);

        setupDrawer();
        Hash_file_maps = new HashMap<String, String>();

        sliderLayout = (SliderLayout)findViewById(R.id.slider);

        Hash_file_maps.put("Android CupCake", "http://androidblog.esy.es/images/cupcake-1.png");
        Hash_file_maps.put("Android Donut", "http://androidblog.esy.es/images/donut-2.png");
        Hash_file_maps.put("Android Eclair", "http://androidblog.esy.es/images/eclair-3.png");
        Hash_file_maps.put("Android Froyo", "http://androidblog.esy.es/images/froyo-4.png");
        Hash_file_maps.put("Android GingerBread", "http://androidblog.esy.es/images/gingerbread-5.png");

        for(String name : Hash_file_maps.keySet()){

            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .description(name)
                    .image(Hash_file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
        sliderLayout.addOnPageChangeListener(this);

        TextView txtposition=findViewById(R.id.position);
        txtposition.setText(managerUser.getCurrentLatitud()+", "+managerUser.getCurrentLongitude());

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
    protected void onStart() {
        super.onStart();

        HerokuService.get(PLACE_TYPES_PATH, SELECT_PLACE_TYPES, this);
    }

    @Override
    protected void onStop() {

        sliderLayout.stopAutoCycle();

        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
        onRImageViewClicked();

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
    private void onRImageViewClicked() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), OPEN_IMAGE_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == OPEN_IMAGE_CODE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                Hash_file_maps = new HashMap<>();
                sliderLayout.removeAllSliders();
                picturesUris = new LinkedList<>();

                if(data.getData()!=null){

                    Uri mImageUri=data.getData();
                    picturesUris.add(mImageUri);

                    Hash_file_maps.put("imagen0", mImageUri.toString());
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        sliderLayout.removeAllSliders();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            picturesUris.add(uri);
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();
                            Hash_file_maps.put("imagen"+i, uri.toString());


                        }
                    }
                }

                for(String name : Hash_file_maps.keySet()){

                    TextSliderView textSliderView = new TextSliderView(this);
                    textSliderView
                            .description(name)
                            .image(Hash_file_maps.get(name))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(this);
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra",name);
                    sliderLayout.addSlider(textSliderView);
                }
                sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
                sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                sliderLayout.setCustomAnimation(new DescriptionAnimation());
                sliderLayout.setDuration(3000);
                sliderLayout.addOnPageChangeListener(this);
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onAPBtnSaveOnClick(View view) {
        placeName = placeNameEditText.getText().toString();

        if(placeName.isEmpty()) {
            Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        picturesUrls = new JSONArray();
        imageCount = picturesUris.size();

        for(Uri uri : picturesUris) {
            new CloudinaryService(UPLOAD_IMAGES, this).execute(uri);
        }
    }

    private void populatePlaceTypes(JSONObject response) {
        JSONArray arrayPlaceTypes = response.optJSONArray("place_categories");
        placeTypesIds = new LinkedList<>();
        LinkedList<String> localPlaceTypes = new LinkedList<>();
        JSONObject item;

        for(int index = 0; index < arrayPlaceTypes.length(); index++) {
            item = arrayPlaceTypes.optJSONObject(index);
            localPlaceTypes.add(item.optString("name"));
            placeTypesIds.add(item.optInt("id"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, localPlaceTypes);

        placeTypesSpinner.setAdapter(adapter);
        placeTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPlaceTypeId = placeTypesIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getPicturesUrls(JSONObject response) {
        JSONArray responseUrls = response.optJSONArray("pictures_urls");

        for(int index = 0; index < responseUrls.length(); index++) {
            picturesUrls.put(responseUrls.optString(index));
        }
    }

    private void savePlace() {
        JSONObject data = new JSONObject();
        JSONObject address = new JSONObject();

        try {
            address.put("address", "asgfdfhgdgffsdsa");
            address.put("latitude", managerUser.getCurrentLatitud());
            address.put("longitude", managerUser.getCurrentLongitude());
            address.put("address_type", 1);

            data.put("address", address);
            data.put("name", placeName);
            data.put("pictures", picturesUrls);
            data.put("place_types", selectedPlaceTypeId);

            HerokuService.post(SAVE_PLACE_PATH, data, SAVE_PLACE, this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        switch (requestCode) {
            case SELECT_PLACE_TYPES:
                populatePlaceTypes(response);
                break;

            case UPLOAD_IMAGES:
                getPicturesUrls(response);

                if (--imageCount > 0) {
                    return;
                }

                savePlace();
                break;

            case SAVE_PLACE:
                openHome();

        }
    }

    @Override
    public void onFailure(int requestCode, String error) {

    }
}
