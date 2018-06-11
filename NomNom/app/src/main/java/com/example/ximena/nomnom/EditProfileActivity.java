package com.example.ximena.nomnom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.services.CloudinaryService;
import com.example.ximena.nomnom.services.HerokuService;
import com.mindorks.placeholderview.PlaceHolderView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class EditProfileActivity extends AppCompatActivity implements IAPICaller {
    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private PlaceHolderView mGalleryView;
    private static final int UPLOAD_IMAGE_CODE = 100;
    private static final int OPEN_IMAGE_CODE = 200;
    private static final int UPDATE_ADDRESS_USER_CODE = 300;
    private static final int ADDRESS_USER_CODE = 500;
    private static final int ADD_ADDRESS_USER_CODE = 600;
    private static final int CHANGE_USER_CODE = 700;
    private ProgressDialog progressDialog;
    Uri pictureUri;
    ManagerUser managerUser;
    int flag_activity;
    private static final String RELATIVE_API = "my/user_address";
    private static final String RELATIVE_API1 = "user/";
    private static final String RELATIVE_API2 ="my/user_address/";
    boolean addresses_null;

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
        progressDialog = new ProgressDialog(this);


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
    public void onViewClicked(View view) throws JSONException {
        switch (view.getId()) {
            case R.id.home:
                flag_activity=0;
                openMap();
                break;

            case R.id.job:
                flag_activity=1;
                openMap();
                break;
            case R.id.image:
                onRImageViewClicked();
                break;

            case R.id.change:
                onRbtnChangeClicked();
                break;

            default:
                return;
        }
    }

    private void onRImageViewClicked() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), OPEN_IMAGE_CODE);
    }

    private void onRbtnChangeClicked() {
        TextView txtname = findViewById(R.id.name);
        TextView txtemail = findViewById(R.id.email);
        TextView txthome = findViewById(R.id.home);
        TextView txtjob = findViewById(R.id.job);
        ImageView imageView = findViewById(R.id.image);
        String[] name=txtname.getText().toString().split(" ");
        String email=txtemail.getText().toString();

        if (name[0].isEmpty() || name[1].isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setTitle("Registering user");
        progressDialog.show();

        if (pictureUri != null) {
            new CloudinaryService(UPLOAD_IMAGE_CODE, this).execute(pictureUri);
        } else {
            uploadUser(null);
        }
        progressDialog.cancel();
    }
    public void uploadUser(JSONArray images){
        Log.d("IMAGENES", String.valueOf(images));

        if(images==null){
            JSONObject user = new JSONObject();
            TextView txtname = findViewById(R.id.name);
            TextView txtemail = findViewById(R.id.email);
            TextView txthome = findViewById(R.id.home);
            TextView txtjob = findViewById(R.id.job);
            ImageView imageView = findViewById(R.id.image);
            String[] name=txtname.getText().toString().split(" ");
            String email=txtemail.getText().toString();
            try {
                user.put("name", name[0]);
                user.put("last_name", name[1]);
                user.put("email", email);

                user.put("picture", managerUser.getPicture());

                HerokuService.put(RELATIVE_API1, user, CHANGE_USER_CODE, this);
                //HerokuService.post(RELATIVE_API, user, ADD_ADDRESS_USER_CODE, this);
            }catch(Exception e){

            }

        }else{
            JSONObject user = new JSONObject();
            TextView txtname = findViewById(R.id.name);
            TextView txtemail = findViewById(R.id.email);
            TextView txthome = findViewById(R.id.home);
            TextView txtjob = findViewById(R.id.job);
            ImageView imageView = findViewById(R.id.image);
            String[] name=txtname.getText().toString().split(" ");
            String email=txtemail.getText().toString();
            String image= null;
            try {
                image = images.get(0).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try{
            user.put("name", name[0]);
            user.put("last_name", name[1]);
            user.put("email", email);

            user.put("picture",image);

            HerokuService.put(RELATIVE_API1, user, CHANGE_USER_CODE, this);
            }catch(Exception e){}
            //HerokuService.post(RELATIVE_API, user, ADD_ADDRESS_USER_CODE, this);

        }


    }
    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        TextView txtname = findViewById(R.id.name);
        TextView txtemail = findViewById(R.id.email);
        TextView txthome = findViewById(R.id.home);
        TextView txtjob = findViewById(R.id.job);
        try {
            switch (requestCode) {
                case UPLOAD_IMAGE_CODE:
                    uploadUser(response.optJSONArray("pictures_urls"));
                    break;

                case ADDRESS_USER_CODE:

                    Log.d("response", response.toString());

                    JSONArray addresses = response.getJSONArray("addresses");

                    ImageView imageView = findViewById(R.id.image);

                    txtname.setText(managerUser.getName() + " " + managerUser.getLastname());
                    txtemail.setText(managerUser.getEmail());
                    Picasso.with(this).load(managerUser.getPicture()).into(imageView);
                    if(addresses.length()==0) {
                        addresses_null=true;
                        txthome.setText("Casa:0,0");
                        txtjob.setText("Trabajo:0,0");
                    }else{
                        addresses_null=false;
                        for(int i=0;i<addresses.length();i++){
                            JSONObject jsonObject=addresses.getJSONObject(i);
                            if(jsonObject.getInt("address_type_id")==1){

                                double longitude=jsonObject.getDouble("longitude");
                                double latitude=jsonObject.getDouble("latitude");
                                txtjob.setText("Trabajo:"+latitude+","+longitude);

                            }else if(jsonObject.getInt("address_type_id")==2){
                                double longitude=jsonObject.getDouble("longitude");
                                double latitude=jsonObject.getDouble("latitude");
                                txthome.setText("Casa:"+latitude+","+longitude);
                            }

                        }
                    }
                    break;

                case CHANGE_USER_CODE:
                    JSONObject job = new JSONObject();

                    String[] shome=txthome.getText().toString().split(":")[1].split(",");

                    String[] sjob=txtjob.getText().toString().split(":")[1].split(",");

                    job.put("longitude", Float.parseFloat(sjob[0]));
                    job.put("latitude", Float.parseFloat(sjob[1]));
                    job.put("address_type", 2);
                    JSONObject house = new JSONObject();
                    Log.d("response", response.toString());
                    house.put("latitude", Float.parseFloat(shome[0]));
                    house.put("logitude", Float.parseFloat(shome[1]));
                    house.put("address_type", 1);

                    if(addresses_null) {
                        HerokuService.post(RELATIVE_API, job, ADD_ADDRESS_USER_CODE, this);
                        HerokuService.post(RELATIVE_API, house, ADD_ADDRESS_USER_CODE, this);
                    }else{
                        HerokuService.put(RELATIVE_API2+1, job, UPDATE_ADDRESS_USER_CODE, this);
                        HerokuService.put(RELATIVE_API2+2, house, UPDATE_ADDRESS_USER_CODE, this);
                    }

                    break;
                case ADD_ADDRESS_USER_CODE:
                    Log.d("response", response.toString());
                    break;

                case UPDATE_ADDRESS_USER_CODE:
                    Log.d("response", response.toString());
                    break;
                default:
                    break;
            }
        }catch (Exception e){}

    }

    @Override
    public void onFailure(int requestCode, String error) {
        try{
            switch (requestCode){
                case CHANGE_USER_CODE:
                    Log.d("ERROR", error);
                    break;
            }
        }catch (Exception e){}

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                ImageView imageView = findViewById(R.id.image);
                pictureUri = data.getData();
                Picasso.with(this).load(pictureUri).into(imageView);
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
