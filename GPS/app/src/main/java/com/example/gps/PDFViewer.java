package com.example.gps;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class PDFViewer extends AppCompatActivity {


    FloatingActionButton button;
    ActivityResultLauncher<Intent> launcher;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_pdfviewer);

        button = findViewById(R.id.openFileBtn);
        pdfView = findViewById(R.id.pdfView);

        registerIntent();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("application/*");
                launcher.launch(i);
            }
        });

    }
    public void registerIntent(){
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        try {
                            Uri fileUri = result.getData().getData();
                            pdfView.fromUri(fileUri)
                                            .defaultPage(0)
                                                    .enableSwipe(true)
                                                            .enableDoubletap(true)
                                                                    .spacing(10)
                                                                            .load();
                            Toast.makeText(PDFViewer.this, "File selected !", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Log.d("error",e.getMessage());
                        }

                    }
                });
    }
}