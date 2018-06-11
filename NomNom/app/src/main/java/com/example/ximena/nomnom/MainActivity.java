package com.example.ximena.nomnom;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements IAPICaller {
    CallbackManager callbackManager;
    private static final int LOGIN_USER_CODE = 200;
    private static final int REGISTER_USER_CODE = 300;
    private static final String RELATIVE_API = "auth/signin";
    private static final String RELATIVE_API2 = "auth/signup";
    private String email, password;
    ManagerUser manager;
    int flag=0;
    JSONObject temp;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());
        String projectToken = "1821b8b7903673aecfd72b7780ef3461";
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);
        mixpanel.track("Video play");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
               "public_profile", "email", "user_birthday", "user_friends"));
        callbackManager = CallbackManager.Factory.create();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String email=pref.getString("email", null); // getting String
        String pass=pref.getString("pass", null); // getting String
        if(email!=null && pass!=null){
            try{
                Log.d("email",email);
                Log.d("pass",pass);
                JSONObject test=new JSONObject();
                test.put("email", email);
                test.put("password", pass);
                login(test);
            }
            catch (Exception e){}
        }
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
        if(isLoggedIn){
            Log.d("accessToken",accessToken.toString());
            //login(user);

        }
        manager=ManagerUser.getInstance();

    }
    public void login(JSONObject user){
        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            String email = user.getString("email");
            String pass = user.getString("password");
            editor.putString("email", email); // Storing string
            editor.putString("pass", pass); // Storing string
            editor.commit(); // commit changes
            HerokuService.post(RELATIVE_API, user, LOGIN_USER_CODE, this);
        }catch (Exception e){}

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
                    finish();
                    openHomenotView();


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
