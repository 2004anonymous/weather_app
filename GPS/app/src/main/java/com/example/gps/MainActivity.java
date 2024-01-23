package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gps.utilities.utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int REQ_LOCATION_CODE = 100;
    TextView lat_tv, long_tv, city_tv, country_tv;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button locBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lat_tv = findViewById(R.id.latitude_tv);
        long_tv = findViewById(R.id.longitude_tv);
        city_tv = findViewById(R.id.city_tv);
        country_tv = findViewById(R.id.country_tv);
        locBtn = findViewById(R.id.locBtn);

        getLocation();
        checkLocStatus();
        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

    }

    private void checkLocStatus() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!manager.isLocationEnabled()){
                Toast.makeText(this, "Your location is desibled !", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Your location is enabled !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_LOCATION_CODE);
        }else {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            try {
                            Geocoder geocoder = new Geocoder(MainActivity.this);
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            lat_tv.setText(String.valueOf(addresses.get(0).getLatitude()));
                            long_tv.setText(String.valueOf(addresses.get(0).getLongitude()));
                            city_tv.setText(String.valueOf(addresses.get(0).getLocality()));
                            country_tv.setText(String.valueOf(addresses.get(0).getCountryName()));
                            }catch (Exception e){
                                Log.d("locError",e.getMessage());
                            }
                        }
                    }
                });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION_CODE && grantResults.length > 0){
            getLocation();
        }
    }
}