package com.example.myweather;


import static com.example.myweather.utils.Utility.baseUrl;
import static com.example.myweather.utils.Utility.key;
import static com.example.myweather.utils.Utility.showAlert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.example.myweather.ApiRequest.Request;
import com.example.myweather.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding bind;
    String deviceLoc, query, url;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<Address> address;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getCurrLocation();
        url = baseUrl+"forecast.json?key="+key+"&q="+query;

        bind.allForeCast.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
        bind.refreshListener.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (url != null){
                    Request.makeJSONRequest(MainActivity.this,url,bind);
                }
                bind.refreshListener.setRefreshing(false);
            }
        });

        bind.searchV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                query = str;
                url = baseUrl+"forecast.json?key="+key+"&q="+query;
                Request.makeJSONRequest(MainActivity.this,url,bind);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String txt) {
                if (Objects.equals(txt, "")){
                    url = baseUrl+"forecast.json?key="+key+"&q="+deviceLoc;
                    Request.makeJSONRequest(MainActivity.this,url,bind);
                }
                return true;
            }
        });

    }
    public void getCurrLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},201);
        }else{
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                showAlert(MainActivity.this,"It seems your gps is desibled !","Location");
            }else{
                Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location!=null){
                            Geocoder geocoder = new Geocoder(MainActivity.this);
                            try {
                                address = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                deviceLoc = address.get(0).getLocality();
                                url = baseUrl+"forecast.json?key="+key+"&q="+deviceLoc;
                                Request.makeJSONRequest(MainActivity.this,url,bind);
                            } catch (IOException e) {
                                Log.d("error101",e.getMessage());
                            }
                        }else{
                            try {
                                Request.makeJSONRequest(MainActivity.this,url,bind);
                            }catch (Exception e){
                                Toast.makeText(MainActivity.this, "No internet connection !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 201 && grantResults.length > 0){
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }
}