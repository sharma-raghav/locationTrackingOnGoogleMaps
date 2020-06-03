package com.example.locationtracker;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("fileUri"));
        String name = getFileName(uri);
        File file = new File(Environment.getExternalStorageDirectory()+
                File.separator + "locations/" + name);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String [] nextLine;
            String line;
            Double lat = 0.0;
            Double longtd = 0.0;
            int val = 0;
            while ((line = reader.readLine()) != null) {
                Log.d("abc", "onMapReady: worked");
                nextLine = line.split(",");
                Log.d("", "onMapReady: " + nextLine[0] + nextLine[1]);
                lat = Double.parseDouble(nextLine[0]);
                longtd = Double.parseDouble(nextLine[1]);
                createMarker(Double.parseDouble(nextLine[0]),Double.parseDouble(nextLine[1]));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longtd), 100));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        CSVReader reader =
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public Marker createMarker(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        return mMap.addMarker(new MarkerOptions().position(latLng));
    }

    public String getFileName(Uri uri) {
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c !=  null && c.moveToFirst()) {
            String displayName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            return displayName;
        }
        return null;
    }
}
