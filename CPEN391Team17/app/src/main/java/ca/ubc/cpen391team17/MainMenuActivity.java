package ca.ubc.cpen391team17;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.CheckBox;

public class MainMenuActivity extends AppCompatActivity {

    /** Opens the hints for geocache 1 **/
    public void openHint1(View view) {
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox1);
        if(checkBox.isChecked()) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, Geocache1Activity.class);
            startActivity(intent);
        }
    }

    /** Opens the hints for geocache 2 **/
    public void openHint2(View view) {
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox2);
        if(checkBox.isChecked()) {
            Intent intent = new Intent(this, Maps2Activity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, Geocache2Activity.class);
            startActivity(intent);
        }
    }

    /** Opens the hints for geocache 3 **/
    public void openHint3(View view) {
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox3);
        if(checkBox.isChecked()) {
            Intent intent = new Intent(this, Maps3Activity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, Geocache3Activity.class);
            startActivity(intent);
        }
    }

    /** Opens the hints for geocache 4 **/
    public void openHint4(View view) {
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox4);
        if(checkBox.isChecked()) {
            Intent intent = new Intent(this, Maps4Activity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, Geocache4Activity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
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

    /**
     * Called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Quit the app since we will not show the startup screen again
        this.finishAffinity();
    }
}
