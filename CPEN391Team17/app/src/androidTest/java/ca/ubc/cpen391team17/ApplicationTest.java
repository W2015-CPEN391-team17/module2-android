package ca.ubc.cpen391team17;

import android.app.Application;
import android.location.Location;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void exampleTest() {
        assertEquals(true, true);
    }

    @Test
    public void emptyListGenerateLocationsStringTest() {
        // empty list of locations
        List<Location> locationsList = new ArrayList<Location>();

        // call the function and check the result
        final String expected = "#?";
        String result = BluetoothActivity.generateLocationsString(locationsList);
        assertEquals(expected, result);
    }

    @Test
    public void oneEntryGenerateLocationsStringTest() {
        // create a list of locations with one entry
        final String providerName = "";
        Location location = new Location(providerName);
        location.setLatitude(1.23456789d);
        location.setLongitude(9.87654321d);
        List<Location> locationsList = new ArrayList<Location>();
        locationsList.add(location);

        // call the function and check the result
        final String expected = "#1.23456789,9.87654321;?";
        String result = BluetoothActivity.generateLocationsString(locationsList);
        assertEquals(expected, result);
    }

    @Test
    public void threeEntriesgenerateLocationsStringTest() {
        // create three example locations
        final String providerName = "";
        Location location0 = new Location(providerName);
        Location location1 = new Location(providerName);
        Location location2 = new Location(providerName);
        location0.setLatitude(1.0001d);
        location0.setLongitude(2.0002d);
        location1.setLatitude(3.0003d);
        location1.setLongitude(4.0004d);
        location2.setLatitude(5.0005d);
        location2.setLongitude(6.0006d);

        // add them to the list
        List<Location> locationsList = new ArrayList<Location>();
        locationsList.add(location0);
        locationsList.add(location1);
        locationsList.add(location2);

        // call the function and check the result
        final String expected = "#1.0001,2.0002;3.0003,4.0004;5.0005,6.0006;?";
        String result = BluetoothActivity.generateLocationsString(locationsList);
        assertEquals(expected, result);
    }

    @Test
    public void emptyTrimLocationsTest() {
        // create list with 2*MAX_LOCATIONS_SIZE example locations
        List<Location> locationsList = new ArrayList<Location>();

        // data that the DE2 would have sent
        float lat = 1.0f;
        float lon = 1.0f;
        float latrange = 1.0f;
        float lonrange = 1.0f;

        // call the function and check the result
        List<Location> resultList = BluetoothActivity.trimLocations(locationsList, lat, lon, latrange,
                lonrange);
        assertEquals(0, resultList.size());
    }

    @Test
    public void fourEntriesKeepAllTrimLocationsTest() {
        // create four example locations
        final String providerName = "";
        Location location0 = new Location(providerName);
        Location location1 = new Location(providerName);
        Location location2 = new Location(providerName);
        Location location3 = new Location(providerName);
        location0.setLatitude(1.0001d);
        location0.setLongitude(1.0001d);
        location1.setLatitude(2.0002d);
        location1.setLongitude(2.0002d);
        location2.setLatitude(3.0003d);
        location2.setLongitude(3.0003d);
        location3.setLatitude(4.0004d);
        location3.setLongitude(4.0004d);

        // add them to the list
        List<Location> locationsList = new ArrayList<Location>();
        locationsList.add(location0);
        locationsList.add(location1);
        locationsList.add(location2);
        locationsList.add(location3);

        // data that the DE2 would have sent
        // all locations should be within range
        float lat = 3.5f;
        float lon = 3.5f;
        float latrange = 5.0f;
        float lonrange = 5.0f;

        // call the function and check the result
        List<Location> resultList = BluetoothActivity.trimLocations(locationsList, lat, lon, latrange,
                lonrange);
        List<Location> expectedList = new ArrayList<Location>();
        expectedList.add(location0);
        expectedList.add(location1);
        expectedList.add(location2);
        expectedList.add(location3);
        assertEquals(expectedList, resultList);
    }

    @Test
    public void fourEntriesKeepHalfTrimLocationsTest() {
        // create four example locations
        final String providerName = "";
        Location location0 = new Location(providerName);
        Location location1 = new Location(providerName);
        Location location2 = new Location(providerName);
        Location location3 = new Location(providerName);
        location0.setLatitude(1.0001d);
        location0.setLongitude(1.0001d);
        location1.setLatitude(2.0002d);
        location1.setLongitude(2.0002d);
        location2.setLatitude(3.0003d);
        location2.setLongitude(3.0003d);
        location3.setLatitude(4.0004d);
        location3.setLongitude(4.0004d);

        // add them to the list
        List<Location> locationsList = new ArrayList<Location>();
        locationsList.add(location0);
        locationsList.add(location1);
        locationsList.add(location2);
        locationsList.add(location3);

        // data that the DE2 would have sent
        // only locations 2 and 3 should be within range
        float lat = 3.5f;
        float lon = 3.5f;
        float latrange = 1.0f;
        float lonrange = 1.0f;

        // call the function and check the result
        List<Location> resultList = BluetoothActivity.trimLocations(locationsList, lat, lon, latrange,
                lonrange);
        List<Location> expectedList = new ArrayList<Location>();
        expectedList.add(location2);
        expectedList.add(location3);
        assertEquals(expectedList, resultList);
    }

    @Test
    public void overMaxSizeTrimLocationsTest() {
        // create list with 2*MAX_LOCATIONS_SIZE example locations
        List<Location> locationsList = new ArrayList<Location>();
        final String providerName = "";
        for(int i = 0; i < 2*BluetoothActivity.MAX_LOCATIONS_SIZE; i++) {
            Location location = new Location(providerName);
            location.setLatitude(1.0d);
            location.setLongitude(1.0d);
            locationsList.add(location);
        }

        // data that the DE2 would have sent
        // all points should be within range
        float lat = 1.0f;
        float lon = 1.0f;
        float latrange = 1.0f;
        float lonrange = 1.0f;

        // call the function and check the result
        List<Location> resultList = BluetoothActivity.trimLocations(locationsList, lat, lon, latrange,
                lonrange);
        assertEquals(BluetoothActivity.MAX_LOCATIONS_SIZE, resultList.size());
    }
}
