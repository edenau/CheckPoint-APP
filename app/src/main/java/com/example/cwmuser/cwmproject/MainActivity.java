package com.example.cwmuser.cwmproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MainActivity extends FragmentActivity {

    //Variables
    private Location Loc1Location;
    private Location Loc2Location;
    private LatLng Loc1LatLng;
    private LatLng Loc2LatLng;
    private GoogleMap map;
    private SharedPreferences SP;
    private float distance;
    private LocationManager locationManager;
    private boolean locationAvailable;
    private Button toListButton;
    private List<String> nameList;
    static final int READ_BLOCK_SIZE = 100;
    private List<String> latList;
    private List<String> lngList;
    private Button findClosestButton;
    private float closestdistance;
    private int closestposition;

    //Get Best Provider function. Used in activating location manager
    private String getBestLocationProvider() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);

        return locationManager.getBestProvider(c, true);
    }

    //Location Manager and bestProvider set-up function. Used in onResume and sends user location.
    private void activateLocationManager() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = getBestLocationProvider();
        locationAvailable = false;

        // If the bestProvider is not accessible and/or enabled
        if (!locationManager.isProviderEnabled(bestProvider)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your LOCATION provider seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }

        if (locationManager != null && bestProvider != "") {
            locationManager.requestLocationUpdates(bestProvider, 3000, 1, locationListener);
        }
    }

    //Reading .csv files produced by list
    private void readingFiles() {

        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsoluteFile() + "/MyApplication");

        //List of names
        File name = new File(directory, "nameList.csv");
        FileInputStream fIn = null;
        try {
            fIn = new FileInputStream(name);
            InputStreamReader isr = new InputStreamReader(fIn);
            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                s += readString;
                inputBuffer = new char[READ_BLOCK_SIZE];
            }

            String[] retrievedStringArray = s.split(",");
            nameList = new ArrayList<String>(Arrays.asList(retrievedStringArray));
        } catch (IOException e) {
            e.printStackTrace();
            nameList = new ArrayList<String>();
        }

        //List of latitudes
        File lat = new File(directory, "latList.csv");
        FileInputStream fIn1 = null;
        try {
            fIn1 = new FileInputStream(lat);
            InputStreamReader isr = new InputStreamReader(fIn1);
            char[] inputBuffer1 = new char[READ_BLOCK_SIZE];
            String s1 = "";
            int charRead;
            while ((charRead = isr.read(inputBuffer1)) > 0) {
                String readString = String.copyValueOf(inputBuffer1, 0, charRead);
                s1 += readString;
                inputBuffer1 = new char[READ_BLOCK_SIZE];
            }

            String[] retrievedStringArray = s1.split(",");
            latList = new ArrayList<String>(Arrays.asList(retrievedStringArray));
        } catch (IOException e) {
            e.printStackTrace();
            latList = new ArrayList<String>();
        }

        //List of longitudes
        File lng = new File(directory, "lngList.csv");
        FileInputStream fIn2 = null;
        try {
            fIn2 = new FileInputStream(lng);
            InputStreamReader isr = new InputStreamReader(fIn2);
            char[] inputBuffer2 = new char[READ_BLOCK_SIZE];
            String s2 = "";
            int charRead;
            while ((charRead = isr.read(inputBuffer2)) > 0) {
                String readString = String.copyValueOf(inputBuffer2, 0, charRead);
                s2 += readString;
                inputBuffer2 = new char[READ_BLOCK_SIZE];
            }

            String[] retrievedStringArray = s2.split(",");
            lngList = new ArrayList<String>(Arrays.asList(retrievedStringArray));
        } catch (IOException e) {
            e.printStackTrace();
            lngList = new ArrayList<String>();
        }
        if (lngList.get(0) == "") {
            lngList.remove(0);
        }
        if (latList.get(0) == "") {
            latList.remove(0);
        }
        if (nameList.get(0) == "") {
            nameList.remove(0);
        }
    }

    //Plots markers from CSV file on map
    private void plottingMarkers(){
        if (nameList.size()==0) {
        }
        else {
            for (int i = 0; i < nameList.size(); i++) {
                Loc2LatLng = new LatLng(Double.parseDouble(latList.get(i)), Double.parseDouble(lngList.get(i)));
                MarkerOptions markerSavedLocations = new MarkerOptions();
                markerSavedLocations.title(nameList.get(i));
                markerSavedLocations.position(Loc2LatLng);
                map.addMarker(markerSavedLocations);
            }
        }
    }

    //Location Listener set-up. Receives user location, displays locations, calculates distances.
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Loc1Location = location;
            locationAvailable= true;
            if (map != null && Loc1Location != null) {
                //User location as LatLng class
                Loc1LatLng = new LatLng(Loc1Location.getLatitude(), Loc1Location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(Loc1LatLng, 15));

                String bestProvider = getBestLocationProvider();

                //Displays you are here location on map
                MarkerOptions markerUserLocation = new MarkerOptions();
                map.clear();
                markerUserLocation.title("You Are Here");
                markerUserLocation.position(Loc1LatLng);
                map.addMarker(markerUserLocation);
                plottingMarkers();

                for (int p = 0; p < nameList.size(); p++) {
                    Loc2Location = new Location(bestProvider);
                    Loc2Location.setLatitude(Double.parseDouble(latList.get(p)));
                    Loc2Location.setLongitude(Double.parseDouble(lngList.get(p)));
                    distance = Loc1Location.distanceTo(Loc2Location);
                    Log.i("CHECK:", Float.toString(distance));

                    if (distance < 10) {
                        Toast.makeText(MainActivity.this, "You've reached " + nameList.get(p), Toast.LENGTH_SHORT).show();
                        latList.remove(p);
                        lngList.remove(p);
                        nameList.remove(p);
                        map.clear();
                        plottingMarkers();
                    }
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderEnabled(String s) {}

        @Override
        public void onProviderDisabled(String s) {}
    };

    public void writingFiles() {
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsoluteFile() + "/MyApplication");
        directory.mkdirs();
        File name = new File(directory, "nameList.csv");
        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(name));
            for (int p = 0; p < nameList.size(); p++) {
                osw.write(nameList.get(p));
                if (p == (nameList.size() - 1)) {
                } else {
                    osw.write(",");
                }
            }
            osw.flush();
            osw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        File lat = new File(directory, "latList.csv");
        try {
            OutputStreamWriter osw2 = new OutputStreamWriter(new FileOutputStream(lat));
            for (int p = 0; p < latList.size(); p++) {
                osw2.write(latList.get(p));
                if (p == (latList.size() - 1)) {
                } else {
                    osw2.write(",");
                }
            }
            osw2.flush();
            osw2.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        File lng = new File(directory, "lngList.csv");
        try {
            OutputStreamWriter osw3 = new OutputStreamWriter(new FileOutputStream(lng));
            for (int p = 0; p < lngList.size(); p++) {
                osw3.write(lngList.get(p));
                if (p == (lngList.size() - 1)) {
                } else {
                    osw3.write(",");
                }
            }
            osw3.flush();
            osw3.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void createFiles(){
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsoluteFile() + "/MyApplication");
        if(!directory.exists()) directory.mkdirs();

        File file1 = new File(directory, "nameList.csv");
        File file2 = new File(directory, "latList.csv");
        File file3 = new File(directory, "lngList.csv");
        try{
            if(!file1.exists()) file1.createNewFile();
            if(!file2.exists()) file2.createNewFile();
            if(!file3.exists()) file3.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Sets up map fragment. Used in onCreate.
    private void setUpMapIfNeeded() {
        if (map == null) {
            SupportMapFragment smf = null;
            smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            if (smf != null) {
                map = smf.getMap();
                if (map != null) {
                    map.setMyLocationEnabled(true);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_file);

        activateLocationManager();

        setUpMapIfNeeded();
        locationAvailable= false;

        SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        toListButton = (Button) findViewById(R.id.toListButton);

        findClosestButton = (Button) findViewById(R.id.findClosest);

        findClosestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationAvailable == true) {
                    if (nameList.size() != 0) {
                        for (int c = 0; c < nameList.size(); c++) {
                            Loc2Location.setLatitude(Double.parseDouble(latList.get(c)));
                            Loc2Location.setLongitude(Double.parseDouble(lngList.get(c)));
                            distance = Loc1Location.distanceTo(Loc2Location);
                            if (c == 0) {
                                closestdistance = distance;
                                closestposition = 0;
                            } else if (distance < closestdistance) {
                                closestdistance = distance;
                                closestposition = c;
                            }
                        }
                        int rounded = (int) closestdistance;
                        Toast toast = Toast.makeText(getApplicationContext(), "Closest Checkpoint: " + nameList.get(closestposition) + " at " +rounded + " metres", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Checkpoints Entered", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Current Location Unavailable", Toast.LENGTH_LONG).show();
                }
            }
        });

        toListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, list.class));
                writingFiles();
            }
        });

        createFiles();
        readingFiles();
        plottingMarkers();
    }

    @Override
    protected void onResume() {
        activateLocationManager();
        readingFiles();
        writingFiles();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
