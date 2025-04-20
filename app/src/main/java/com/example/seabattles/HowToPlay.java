package com.example.seabattles;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seabattles.models.MusicPlayer;

public class HowToPlay extends AppCompatActivity {

    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    void initView(){
        setContentView(R.layout.activity_how_to_play);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().playClickSound();
                finish();
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        MusicPlayer.getInstance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayer.getInstance().resume();
    }
}