package com.example.ximena.nomnom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.services.CloudinaryService;
import com.example.ximena.nomnom.services.HerokuService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class RegisterActivity extends AppCompatActivity implements IAPICaller {
    private static final int UPLOAD_IMAGE_CODE = 100;
    private static final int OPEN_IMAGE_CODE = 200;
    private static final int REGISTER_USER_CODE = 300;
    private static final String RELATIVE_API = "auth/signup";
    private static final String RELATIVE_API1 ="my/user_address/";
    private static final int ADD_ADDRESS_USER_CODE = 600;

    private ImageView pictureImageView;
    private EditText nameEditText, lastNameEditText, emailEditText, passwordEditText;
    private ProgressDialog progressDialog;

    private String name, lastName, email, password;
    private Uri pictureUri;
    ManagerUser managerUser;
    int flag_activity=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        managerUser=ManagerUser.getInstance();
        pictureImageView = findViewById(R.id.RImageView);
        nameEditText = findViewById(R.id.RtxtName);
        lastNameEditText = findViewById(R.id.RtxtLastname);
        emailEditText = findViewById(R.id.RtxtEmail);
        passwordEditText = findViewById(R.id.RtxtPassword);
        progressDialog = new ProgressDialog(this);
    }

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.RImageView:
                onRImageViewClicked();
                break;

            case R.id.RbtnRegister:
                onRbtnRegisterClicked();
                break;
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

    private void onRImageViewClicked() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), OPEN_IMAGE_CODE);
    }

    private void onRbtnRegisterClicked() {
        name = nameEditText.getText().toString();
        lastName = lastNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setTitle("Registering user");
        progressDialog.show();

        if (pictureUri != null) {
            new CloudinaryService(UPLOAD_IMAGE_CODE, this).execute(pictureUri);
        } else {
            registerUser(null);
        }
    }
    public void openMap(){
        managerUser.setFlag_map(1);
        Intent activity = new Intent(this, MapsActivity.class);
        startActivity(activity);

    }
    private void registerUser(JSONArray picturesURLs) {
        JSONObject user = new JSONObject();

        try {
            user.put("name", name);
            user.put("last_name", lastName);
            user.put("email", email);
            user.put("password", password);
            user.put("password_confirmation", password);

            if (picturesURLs != null) {
                user.put("picture", picturesURLs.get(0).toString());
            }

            HerokuService.post(RELATIVE_API, user, REGISTER_USER_CODE, this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void processError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject errors = new JSONObject(responseBody).optJSONObject("errors");
            String message = "";

            for (Iterator<String> iter = errors.keys(); iter.hasNext(); ) {
                String key = iter.next();

                message += String.format("%s %s\n", key, errors.optString(key));
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                pictureUri = data.getData();
                Picasso.with(this).load(pictureUri).into(pictureImageView);
            }
        }
    }

    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        TextView txthome = findViewById(R.id.home);
        TextView txtjob = findViewById(R.id.job);
        switch (requestCode) {
            case UPLOAD_IMAGE_CODE:
                registerUser(response.optJSONArray("pictures_urls"));
                break;

            case REGISTER_USER_CODE:
                try{
                    Log.d("responde",response.toString());
                    JSONObject user=response.getJSONObject("user");
                    managerUser.setIdUser(user.getInt("id"));
                    managerUser.setName(user.getString("name"));
                    managerUser.setLastname(user.getString("last_name"));
                    managerUser.setEmail(user.getString("email"));
                    managerUser.setPicture(user.getString("picture"));
                    JSONObject job = new JSONObject();

                    String[] shome=txthome.getText().toString().split(":")[1].split(",");

                    String[] sjob=txtjob.getText().toString().split(":")[1].split(",");

                    job.put("longitude", Float.parseFloat(sjob[0]));
                    job.put("latitude", Float.parseFloat(sjob[1]));
                    job.put("address_type", 1);
                    JSONObject house = new JSONObject();
                    Log.d("response", response.toString());
                    house.put("latitude", Float.parseFloat(shome[0]));
                    house.put("logitude", Float.parseFloat(shome[1]));
                    house.put("address_type", 2);

                    HerokuService.post(RELATIVE_API1, job, ADD_ADDRESS_USER_CODE, this);
                    HerokuService.post(RELATIVE_API1, house, ADD_ADDRESS_USER_CODE, this);
                    openHomeActivity();
                }catch (Exception e) {


                }
                break;

        }
    }

    @Override
    public void onFailure(int requestCode, String error) {
        switch (requestCode) {
            case REGISTER_USER_CODE:
                progressDialog.dismiss();
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
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

        }else if(flag_activity==1){
            TextView txtjob = findViewById(R.id.job);
            txtjob.setText("Trabajo: "+managerUser.getTempLatitud()+", "+managerUser.getTempLongitude());
        }
    }
}
