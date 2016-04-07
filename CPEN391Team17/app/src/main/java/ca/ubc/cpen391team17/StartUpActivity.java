package ca.ubc.cpen391team17;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

public class StartUpActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mBarometer;

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
        initializeBarometer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mBarometer, SensorManager.SENSOR_DELAY_NORMAL);
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

    public void initializeBarometer() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        mBarometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (mBarometer != null) {
            System.out.println("updateAirPressure: There *is* a barometer");
        } else {
            System.out.println("updateAirPressure: barometer not found");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double pressure = event.values[0];
        System.out.println("onSensorChanged: pressure is " + (int)pressure + " hPa");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // we do not need to do anything if sensor accuracy changes
    }
}
