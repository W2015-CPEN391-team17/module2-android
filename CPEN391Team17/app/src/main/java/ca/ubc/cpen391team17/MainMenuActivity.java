package ca.ubc.cpen391team17;

import android.content.Context;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import ca.ubc.cpen391team17.CheckboxesState;

public class MainMenuActivity extends AppCompatActivity {
    private static final String CHECKBOXES_FILENAME = "checkboxes.dat";
    private CheckboxesState checkboxesState = new CheckboxesState();

    /* Handler for hints and checkboxes for main menu */
    public void openHints(View view) {

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox1);
        Intent mapIntent = new Intent(this, MapsActivity.class);
        Intent hintIntent = new Intent(this, HintsActivity.class);
        String mapName = "string not initialized";
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
                mapName = getResources().getString(R.string.thechief);
                message = getResources().getString(R.string.thechief);
                message1 = getResources().getString(R.string.thechief_clue_1);
                message2 = getResources().getString(R.string.thechief_clue_2);
                message3 = getResources().getString(R.string.thechief_clue_3);
                message4 = getResources().getString(R.string.thechief_clue_4);
                message5 = getResources().getString(R.string.thechief_clue_5);
                break;
            case R.id.button2:
                checkBox = (CheckBox) findViewById(R.id.checkbox2);
                mapName = getResources().getString(R.string.brandy);
                message = getResources().getString(R.string.brandy);
                message1 = getResources().getString(R.string.brandy_clue_1);
                message2 = getResources().getString(R.string.brandy_clue_2);
                message3 = getResources().getString(R.string.brandy_clue_3);
                message4 = getResources().getString(R.string.brandy_clue_4);
                message5 = getResources().getString(R.string.brandy_clue_5);
                break;
            case R.id.button3:
                checkBox = (CheckBox) findViewById(R.id.checkbox3);
                mapName = getResources().getString(R.string.park);
                message = getResources().getString(R.string.park);
                message1 = getResources().getString(R.string.park_clue_1);
                message2 = getResources().getString(R.string.park_clue_2);
                message3 = getResources().getString(R.string.park_clue_3);
                message4 = getResources().getString(R.string.park_clue_4);
                message5 = getResources().getString(R.string.park_clue_5);
                break;
            case R.id.button4:
                checkBox = (CheckBox) findViewById(R.id.checkbox4);
                mapName = getResources().getString(R.string.mcld);
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
            mapIntent.putExtra("mapName", mapName);
            startActivity(mapIntent);
        }
        else {
            hintIntent.putExtras(extras);
            startActivity(hintIntent);
        }
    }

    /** Save the state of the checkboxes to internal storage */
    public void saveCheckboxesState(View view) {
        /* update checkboxesState */
        checkboxesState.checkbox1 = ((CheckBox) findViewById(R.id.checkbox1)).isChecked();
        checkboxesState.checkbox2 = ((CheckBox) findViewById(R.id.checkbox2)).isChecked();
        checkboxesState.checkbox3 = ((CheckBox) findViewById(R.id.checkbox3)).isChecked();
        checkboxesState.checkbox4 = ((CheckBox) findViewById(R.id.checkbox4)).isChecked();

        /* save checkboxesState to a file */
        try {
            File checkboxesStateFile = new File(this.getApplicationContext().getFilesDir(),
                    CHECKBOXES_FILENAME);
            FileOutputStream fileOutputStream = new FileOutputStream(checkboxesStateFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this.checkboxesState);
            objectOutputStream.close();
            //TODO fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Load the state of the checkboxes from internal storage */
    public void loadCheckboxesState() {
        File checkboxesStateFile = new File(this.getApplicationContext().getFilesDir(),
                CHECKBOXES_FILENAME);
        /* only try to read the data if the file already exists */
        if (!checkboxesStateFile.exists()) {
            return;
        }
        try {
            /* load checkboxesState from a file */
            FileInputStream fileInputStream = openFileInput(CHECKBOXES_FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            this.checkboxesState = (CheckboxesState) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

            /* update the checkboxes */
            ((CheckBox) findViewById(R.id.checkbox1)).setChecked(checkboxesState.checkbox1);
            ((CheckBox) findViewById(R.id.checkbox2)).setChecked(checkboxesState.checkbox2);
            ((CheckBox) findViewById(R.id.checkbox3)).setChecked(checkboxesState.checkbox3);
            ((CheckBox) findViewById(R.id.checkbox4)).setChecked(checkboxesState.checkbox4);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        loadCheckboxesState();
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
