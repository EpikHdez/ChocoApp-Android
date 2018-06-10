package com.example.ximena.nomnom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Util;
import com.example.ximena.nomnom.interfaces.IAPICaller;
import com.example.ximena.nomnom.services.HerokuService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements IAPICaller {
    CallbackManager callbackManager;
    private static final int LOGIN_USER_CODE = 200;
    private static final int REGISTER_USER_CODE = 300;
    private static final String RELATIVE_API = "auth/signin";
    private static final String RELATIVE_API2 = "auth/signup";
    private String name, lastName, email, password;
    ManagerUser manager;
    int flag=0;
    JSONObject temp;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
               "public_profile", "email", "user_birthday", "user_friends"));
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        try {
                                            Log.d("object",object.toString());
                                            String id=object.getString("id");

                                            String name = object.getString("name");
                                            String email = object.getString("email");

                                            Log.d("object1",object.toString());
                                            JSONObject picture = object.getJSONObject("picture");// 01/31/1980 format
                                            Log.d("picture",picture.toString());
                                            JSONObject data = picture.getJSONObject("data");// 01/31/1980 format
                                            Log.d("data",data.toString());
                                            String url = data.getString("url");

                                            Log.d("object",object.toString());
                                            flag=1;
                                            JSONObject user = new JSONObject();


                                            String new_email=  email;
                                            String new_pass=id;
                                            email=new_email;
                                            password=new_pass;


                                            user.put("email", email);
                                            user.put("password", password);
                                            temp=new JSONObject();
                                            temp.put("name", name);
                                            temp.put("last_name", "");
                                            temp.put("email", email);
                                            temp.put("password", password);
                                            temp.put("password_confirmation", password);
                                            temp.put("picture", url);

                                            login(user);




                                        }catch(Exception e){}
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday,picture");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        manager=ManagerUser.getInstance();

    }
    public void login(JSONObject user){
        Log.d("holi","Si lo hice");
        HerokuService.post(RELATIVE_API, user, LOGIN_USER_CODE, this);
        Log.d("holi","Si lo hice");
    }
    public void facebookLogin(View view){


        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void openHome(View view) {
        JSONObject user = new JSONObject();
        TextView txtemail=findViewById(R.id.email);
        TextView txtpass=findViewById(R.id.pass);

        String new_email=  txtemail.getText().toString();
        String new_pass=txtpass.getText().toString();
        email=new_email;
        password=new_pass;
        try {

            user.put("email", email);
            user.put("password", password);


            HerokuService.post(RELATIVE_API, user, LOGIN_USER_CODE, this);

        }catch (Exception e){

        }
    }
    public void openHomenotView() {
        Intent activity = new Intent(this, HomeActivity.class);
        startActivity(activity);
    }
    public void openRegister(View view) {
        Intent activity = new Intent(this, RegisterActivity.class);
        startActivity(activity);
    }

    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        switch (flag) {
            case 0:
                Log.d("RESPONSE", String.valueOf(response));
                openHomenotView();
                try {
                    JSONObject user = (JSONObject) response.get("user");
                    int id = user.getInt("id");
                    String name = user.getString("name");
                    String lastname = user.getString("last_name");
                    String email = user.getString("email");
                    String picture = user.getString("picture");
                    manager.setIdUser(id);
                    manager.setName(name);
                    manager.setLastname(lastname);
                    manager.setEmail(email);
                    manager.setPicture(picture);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 1:

                JSONObject user = new JSONObject();

                try {
                    String new_email = temp.getString("email");
                    String new_pass = temp.getString("password");


                    user.put("email", new_email);
                    user.put("password", new_pass);
                    HerokuService.post(RELATIVE_API, user, LOGIN_USER_CODE, this);
                    flag = 0;
                }catch (Exception e){}
                break;

        }
    }

    @Override
    public void onFailure(int requestCode, String error) {
        switch (flag){
            case 0:
                break;
            case 1:

                JSONObject user = new JSONObject();



                HerokuService.post(RELATIVE_API2, temp, REGISTER_USER_CODE, this);
                flag=1;
                break;
        }

    }

}
