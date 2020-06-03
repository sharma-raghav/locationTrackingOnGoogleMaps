package com.example.locationtracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;

    private GPSLocationListener locationListener;

    private Integer valForLogging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkForPermissions();
        setContentView(R.layout.activity_main);
        String TAG = "main_activity";
        Log.d(TAG, "onCreate: main_activity started");
        valForLogging = 0;
        addLocationListener();
    }

    public void addLocationListener() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        final String provider = locationManager.getBestProvider(criteria, true);
        this.locationListener = new GPSLocationListener(this, valForLogging);
        try {
            this.locationManager.requestLocationUpdates(provider, 5, 0, locationListener);
        } catch (SecurityException e) {
            Log.e("security exception", "addLocationListener: ");
        }
    }

    public void startStopLogging(View view) {
        valForLogging = 1 - valForLogging;
        Button btn = findViewById(R.id.startLogging);
        if (valForLogging == 1) {
            locationListener.val = 1;
            Log.i("", "startStopLogging: here");
            Toast toast = Toast.makeText(getApplicationContext(), "Logging Started", Toast.LENGTH_SHORT);
            toast.show();
            Log.d("", "startStopLogging: " + getFilesDir());
            btn.setText("Stop");
        } else {
            locationListener.val = 0;
            String path = Environment.getExternalStorageDirectory() + File.separator + "locations";
            File folder = new File (path);
            folder.mkdirs();
            try {
                String fpath = Environment.getExternalStorageDirectory().getPath() + "Download";
//                File file = new File(fpath + System.currentTimeMillis() + ".csv");
                File file = new File(folder, System.currentTimeMillis() + ".csv");
//                file.getParentFile().mkdirs();
                FileWriter myOutWriter = new FileWriter(file);
                CSVWriter writer = new CSVWriter(myOutWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
                for (int i = 0; i < locationListener.longitudes.size(); i++) {
                    String entry = locationListener.latitudes.get(i) + ","
                            + locationListener.longitudes.get(i) + "," +
                            locationListener.speeds.get(i) ;
                    writer.writeNext(entry.split(","));
                }
//                file.createNewFile();
                writer.close();
                locationListener.speeds.clear();
                locationListener.longitudes.clear();
                locationListener.longitudes.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast toast = Toast.makeText(getApplicationContext(), "Logging saved at " + Environment.getExternalStorageDirectory()+File.separator+"/locations", Toast.LENGTH_LONG);
            toast.show();
            btn.setText("Start");
        }
    }

    public void select(View view) {
        fileChoser();
    }

    public void fileChoser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Choose the CSV file from locations folder"), 42);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.d("URI", "onActivityResult: " + uri.toString());
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("fileUri", uri.toString());
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Unable to load file", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    protected void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 15);
        }
    }
}
