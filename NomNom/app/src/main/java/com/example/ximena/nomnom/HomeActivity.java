package com.example.ximena.nomnom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mikepenz.materialdrawer.DrawerBuilder;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


    }
    public void openMap() {
        Intent activity = new Intent(this, MapActivity.class);
        startActivity(activity);
    }
}
