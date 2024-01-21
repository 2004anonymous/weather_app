package com.example.myweather.utils;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myweather.MainActivity;

public class Utility {
    public static final String baseUrl = "https://api.weatherapi.com/v1/";
    public static final String key = "b97555c325c74f7fa4c71724232311";
    public static void showToast(Context context, String msg){
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
    }
    public static void showAlert(Context context, String msg,String title){
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
                dialogInterface.cancel();
            }
        });
        AlertDialog dBox = dialog.create();
        dBox.show();
    }
}
