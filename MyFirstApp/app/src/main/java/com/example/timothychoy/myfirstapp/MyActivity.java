package com.example.timothychoy.myfirstapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

public class MyActivity extends AppCompatActivity {

    /** Opens the hints for geocache 1 **/
    public void openHint1(View view) {
        Intent intent = new Intent(this, Geocache1Activity.class);
        startActivity(intent);
    }

    /** Opens the hints for geocache 2 **/
    public void openHint2(View view) {
        Intent intent = new Intent(this, Geocache2Activity.class);
        startActivity(intent);
    }

    /** Opens the hints for geocache 3 **/
    public void openHint3(View view) {
        Intent intent = new Intent(this, Geocache3Activity.class);
        startActivity(intent);
    }

    /** Opens the hints for geocache 4 **/
    public void openHint4(View view) {
        Intent intent = new Intent(this, Geocache4Activity.class);
        startActivity(intent);
    }

    /** Opens the hints for geocache 5 **/
    public void openHint5(View view) {
        Intent intent = new Intent(this, Geocache5Activity.class);
        startActivity(intent);
    }

    /** Opens the hints for geocache 6 **/
    public void openHint6(View view) {
        Intent intent = new Intent(this, Geocache6Activity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
