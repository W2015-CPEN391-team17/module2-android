package com.example.timothychoy.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Geocache6Activity extends AppCompatActivity {

    /** opens the map associated with geocache 6 **/
    /*public void openMap6(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocache6);
    }

}
