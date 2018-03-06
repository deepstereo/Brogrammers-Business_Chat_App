package com.centennialcollege.brogrammers.businesschatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ToDo If user isn't authorized go to login screen
        startActivity(new Intent(this, LoginActivity.class));
    }
}
