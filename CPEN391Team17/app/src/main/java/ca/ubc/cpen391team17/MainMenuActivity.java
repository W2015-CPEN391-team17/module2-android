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
import android.widget.CheckBox;

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
}
