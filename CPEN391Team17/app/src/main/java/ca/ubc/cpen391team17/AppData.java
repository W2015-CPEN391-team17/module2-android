package ca.ubc.cpen391team17;

import android.location.Location;

import java.util.ArrayList;

/**
 * Singleton that contains non-persistent application-wide data. Use
 * this to share data across Activities.
 */
public class AppData {
    // Only one instance of this class may exist at once
    private static AppData mAppData = new AppData();

    // Path data for each maps activity
    private ArrayList<Location> mMapsActivityPathLocations = new ArrayList<>();
    private ArrayList<Location> mMaps2ActivityPathLocations = new ArrayList<>();
    private ArrayList<Location> mMaps3ActivityPathLocations = new ArrayList<>();
    private ArrayList<Location> mMaps4ActivityPathLocations = new ArrayList<>();

    // Use this getter to access the singleton instance
    public static AppData getInstance() {
        return mAppData;
    }

    // Getters and setters for the path data fields
    public ArrayList<Location> getMapsActivityPathLocations() {
        return mMapsActivityPathLocations;
    }

    public ArrayList<Location> getMaps2ActivityPathLocations() {
        return mMaps2ActivityPathLocations;
    }

    public ArrayList<Location> getMaps3ActivityPathLocations() {
        return mMaps3ActivityPathLocations;
    }

    public ArrayList<Location> getMaps4ActivityPathLocations() {
        return mMaps4ActivityPathLocations;
    }
}
