package com.example.myweather.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import com.example.myweather.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

public class NetworkConn extends BroadcastReceiver {
    Activity activity;
    static NetworkInfo netInfo;
    View sBarView;
    static ConnectivityManager cm;
    static NetworkInfo info;
    ActivityMainBinding bind;

    public NetworkConn(Activity activity, ActivityMainBinding bind){
        this.activity = activity;
        this.bind = bind;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conn.getActiveNetworkInfo();
        if (isConnectionAvail(context)){
            bind.refreshListener.setVisibility(View.VISIBLE);
            bind.netWorkErrorPage.setVisibility(View.GONE);
        }else{
            bind.searchV.clearFocus();
            bind.refreshListener.setVisibility(View.GONE);
            bind.netWorkErrorPage.setVisibility(View.VISIBLE);
            showSnackBar(context,"No internet connection !");
        }
    }
    public static boolean isConnectionAvail(Context context){
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        info = cm.getActiveNetworkInfo();
        if (info!=null){
            if (info.isConnectedOrConnecting()){
                Log.d("netInfo","Connection is secure !");
                return true;
            }else{
                return false;
            }
        }
        return false;
    }
    public void showSnackBar(Context context,String msg){
        sBarView = activity.findViewById(android.R.id.content);
        Snackbar.make(context,sBarView,msg,Snackbar.LENGTH_SHORT).show();
    }
}
