package com.example.ximena.nomnom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void openHome(View view) {
        Intent activity = new Intent(this, HomeActivity.class);
        startActivity(activity);
    }
    public void openRegister(View view) {
        Intent activity = new Intent(this, RegisterActivity.class);
        startActivity(activity);
    }
}
