package com.example.ximena.nomnom;

import android.content.Intent;
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
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements IAPICaller {
    CallbackManager callbackManager;
    private static final int LOGIN_USER_CODE = 200;
    private static final int REGISTER_USER_CODE = 300;
    private static final String RELATIVE_API = "auth/signin";
    private String name, lastName, email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
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
            Intent activity = new Intent(this, HomeActivity.class);
            startActivity(activity);
        }catch (Exception e){

        }
    }
    public void openRegister(View view) {
        Intent activity = new Intent(this, RegisterActivity.class);
        startActivity(activity);
    }

    @Override
    public void onSuccess(int requestCode, JSONObject response) {
        Log.d("RESPONSE", String.valueOf(response));

    }

    @Override
    public void onFailure(int requestCode, Object error) {

    }
}
