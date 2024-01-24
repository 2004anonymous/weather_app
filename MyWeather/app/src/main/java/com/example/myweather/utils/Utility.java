package com.example.myweather.utils;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myweather.MainActivity;
import com.google.android.material.snackbar.Snackbar;

public class Utility {
    public static final String baseUrl = "https://api.weatherapi.com/v1/";
    public static final String key = "b97555c325c74f7fa4c71724232311";
    public static final String mainUrl = baseUrl+"forecast.json?key="+key+"&q=";
    public static  Activity act;
    static boolean flag;
    public static void showToast(Context context, String msg){
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
    }
    public static void showAlert(Context context, String msg,String title, Activity activity){
        act = activity;
        AlertDialog.Builder dialog =  new AlertDialog.Builder(context);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setPositiveButton("enable", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                View parentView = act.findViewById(android.R.id.content);
                Snackbar.make(context,parentView,"Location permission is required !",Snackbar.LENGTH_SHORT)
                        .show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.finish();
                    }
                },3000);
            }
        });
        AlertDialog dBox = dialog.create();
        dBox.show();
    }
    public static void setProviderStatus(Context context,boolean status,String locality){
        SharedPreferences sp = context.getSharedPreferences("gps",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("value",locality);
        editor.putBoolean("status",status);
        editor.commit();
    }
    public static String getProviderValue(Context context){
        return context.getSharedPreferences("gps",MODE_PRIVATE).getString("value",null);
    }
    public static boolean getProviderStatus(Context context){
        return context.getSharedPreferences("gps",MODE_PRIVATE).getBoolean("status",false);
    }
    public void showSnackBar(Context context,Activity act,String msg){
        View view = act.findViewById(android.R.id.content);
        Snackbar.make(context,view,msg,Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.YELLOW)
                .setAction("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();
    }
}
