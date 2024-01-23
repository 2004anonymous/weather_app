package com.example.gps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

public class OnBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        RelativeLayout footer = findViewById(R.id.footer);
        AppCompatButton startBtn = findViewById(R.id.startBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScaleAnimation scaleAnim = new ScaleAnimation(
                        0f, 1f,
                        0f, 1f,
                        Animation.ABSOLUTE, 0,
                        Animation.RELATIVE_TO_PARENT , 1);
                scaleAnim.setDuration(5000);
                scaleAnim.setRepeatCount(0);
                scaleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                scaleAnim.setFillAfter(true);
                scaleAnim.setFillEnabled(true);
                footer.setAnimation(scaleAnim);
            }
        });
    }
}