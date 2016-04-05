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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {

    /* Handler for hints and checkboxes for main menu */
    public void openHints(View view) {

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox1);
        Intent mapIntent = new Intent(this, MapsActivity.class);
        Intent hintIntent = new Intent(this, HintsActivity.class);
        String message = "string not initialized";
        String message1 = "string not initialized";
        String message2 = "string not initialized";
        String message3 = "string not initialized";
        String message4 = "string not initialized";
        String message5 = "string not initialized";
        Bundle extras = new Bundle();

        switch (view.getId()) {
            case R.id.button1:
                checkBox = (CheckBox) findViewById(R.id.checkbox1);
                message = getResources().getString(R.string.thechief);
                message1 = getResources().getString(R.string.thechief_clue_1);
                message2 = getResources().getString(R.string.thechief_clue_2);
                message3 = getResources().getString(R.string.thechief_clue_3);
                message4 = getResources().getString(R.string.thechief_clue_4);
                message5 = getResources().getString(R.string.thechief_clue_5);
                break;
            case R.id.button2:
                checkBox = (CheckBox) findViewById(R.id.checkbox2);
                message = getResources().getString(R.string.brandy);
                message1 = getResources().getString(R.string.brandy_clue_1);
                message2 = getResources().getString(R.string.brandy_clue_2);
                message3 = getResources().getString(R.string.brandy_clue_3);
                message4 = getResources().getString(R.string.brandy_clue_4);
                message5 = getResources().getString(R.string.brandy_clue_5);
                break;
            case R.id.button3:
                checkBox = (CheckBox) findViewById(R.id.checkbox3);
                message = getResources().getString(R.string.park);
                message1 = getResources().getString(R.string.park_clue_1);
                message2 = getResources().getString(R.string.park_clue_2);
                message3 = getResources().getString(R.string.park_clue_3);
                message4 = getResources().getString(R.string.park_clue_4);
                message5 = getResources().getString(R.string.park_clue_5);
                break;
            case R.id.button4:
                checkBox = (CheckBox) findViewById(R.id.checkbox4);
                message = getResources().getString(R.string.mcld);
                message1 = getResources().getString(R.string.mcld_clue_1);
                message2 = getResources().getString(R.string.mcld_clue_2);
                message3 = getResources().getString(R.string.mcld_clue_3);
                message4 = getResources().getString(R.string.mcld_clue_4);
                message5 = getResources().getString(R.string.mcld_clue_5);
                break;
            default:
                break;
        }

        extras.putString("title_key", message);
        extras.putString("clue1_key", message1);
        extras.putString("clue2_key", message2);
        extras.putString("clue3_key", message3);
        extras.putString("clue4_key", message4);
        extras.putString("clue5_key", message5);

        if(checkBox.isChecked()) {
            startActivity(mapIntent);
        }
        else {
            hintIntent.putExtras(extras);
            startActivity(hintIntent);
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