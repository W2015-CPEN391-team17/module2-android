package ca.ubc.cpen391team17;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

public class StartUpActivity extends AppCompatActivity {
    private SensorManager mSensorManager;


    /**
     *
     */
    public void updateAirPressure() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        Sensor barometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (barometer != null) {
            System.out.println("updateAirPressure: There *is* a barometer");
        } else {
            System.out.println("updateAirPressure: barometer not found");
        }
    }

    public void mainMenu(View view){
        Intent intent = new Intent(this, MainMenuActivity.class);
        this.finish();
        startActivity(intent);
    }

    private void checkGooglePlayServices() {
        // Check if the user has the latest latest Google Play Services, and if not, prompt the
        // user to update it.
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, resultCode, 0);
            if (dialog != null) {
                //This dialog will help the user update to the latest GooglePlayServices
                dialog.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        checkGooglePlayServices();
    }

    /**
     * Called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Quit the app
        this.finishAffinity();
    }
}
