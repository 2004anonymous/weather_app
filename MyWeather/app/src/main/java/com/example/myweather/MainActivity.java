package com.example.myweather;


import static com.example.myweather.utils.NetworkConn.isConnectionAvail;
import static com.example.myweather.utils.Utility.baseUrl;
import static com.example.myweather.utils.Utility.getProviderStatus;
import static com.example.myweather.utils.Utility.getProviderValue;
import static com.example.myweather.utils.Utility.key;
import static com.example.myweather.utils.Utility.mainUrl;
import static com.example.myweather.utils.Utility.setProviderStatus;
import static com.example.myweather.utils.Utility.showAlert;
import static com.example.myweather.utils.Utility.showToast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.example.myweather.ApiRequest.Request;
import com.example.myweather.databinding.ActivityMainBinding;
import com.example.myweather.utils.NetworkConn;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding bind;
    String deviceLoc, query, url;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<Address> address;
    LocationManager manager;
    BroadcastReceiver broadcast;
    Task<Location> location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        url = baseUrl + "forecast.json?key=" + key + "&q=" + query;
        register();
        if (!isConnectionAvail(MainActivity.this)) {
            bind.netWorkErrorPage.setVisibility(View.VISIBLE);
        } else {

            boolean status = getProviderStatus(MainActivity.this);
            if (!status) {
            getCurrLocation();
            } else {
            url = mainUrl + getProviderValue(MainActivity.this);
            Request.makeJSONRequest(MainActivity.this, url, bind);
            }

            bind.allForeCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
            bind.refreshListener.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (getProviderStatus(MainActivity.this)) {
                        Request.makeJSONRequest(MainActivity.this, mainUrl + getProviderValue(MainActivity.this), bind);
                    } else {
                        getLocality();
                    }
                } else {
                    if (Objects.equals(query, null)) {
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Request.makeJSONRequest(MainActivity.this, url, bind);
                    }
                }
                bind.refreshListener.setRefreshing(false);
            }
        });

        bind.searchV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                query = str;
                url = baseUrl + "forecast.json?key=" + key + "&q=" + query;
                Request.makeJSONRequest(MainActivity.this, url, bind);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String txt) {
                if (Objects.equals(txt, "")) {
                    url = baseUrl + "forecast.json?key=" + key + "&q=" + getProviderValue(MainActivity.this);
                    Request.makeJSONRequest(MainActivity.this, url, bind);
                }
                return true;
            }
        });
    }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        };
        getOnBackPressedDispatcher().addCallback(callback);

    }

    public void getCurrLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 201);
        } else {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showAlert(MainActivity.this, "Please enable your device location !", "Location", MainActivity.this);
            } else {
                getLocality();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 201 && grantResults.length > 0) {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    public void getLocality() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 201);
        }
        location = fusedLocationProviderClient.getLastLocation();
        location.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(MainActivity.this);
                        try {
                            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            deviceLoc = address.get(0).getLocality();
                            url = baseUrl + "forecast.json?key=" + key + "&q=" + deviceLoc;
                            Request.makeJSONRequest(MainActivity.this, url, bind);
                            setProviderStatus(MainActivity.this, true, deviceLoc);
                        } catch (IOException e) {
                            Log.d("error101", e.getMessage());
                        }
                    } else {
                        try {
                            Request.makeJSONRequest(MainActivity.this, url, bind);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "No internet connection !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    }
    public void register(){
        broadcast = new NetworkConn(MainActivity.this,bind);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcast,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast);
    }
}