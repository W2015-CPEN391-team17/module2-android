package ca.ubc.cpen391team17;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A serializable structure to store the lat/lon info for a location list
 */
public class LocationListState implements Serializable {
    private ArrayList<Double> latList;
    private ArrayList<Double> lonList;
    private int size;

    public LocationListState() {
        this.latList = new ArrayList<Double>();
        this.lonList = new ArrayList<Double>();
        this.size = 0;
    }

    public void add(Location location) {
        this.latList.add(location.getLatitude());
        this.lonList.add(location.getLongitude());
        this.size = this.latList.size();
    }

    public Location remove() {
        Location location = new Location("");
        double lat = this.latList.remove(this.latList.size()-1);
        double lon = this.lonList.remove(this.lonList.size() - 1);
        this.size = this.latList.size();
        location.setLatitude(lat);
        location.setLongitude(lon);
        return location;
    }

    public int size() {
        return this.size;
    }
}
