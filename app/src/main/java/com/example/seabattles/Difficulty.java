package com.example.seabattles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seabattles.models.MusicPlayer;

public class Difficulty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        Button easyButton = findViewById(R.id.btnEasy);
        Button hardButton = findViewById(R.id.btnHard);
        Button normalButton = findViewById(R.id.btnNormal);
        Button backButton = findViewById(R.id.btnBack);
        Button howToPlayButton = findViewById(R.id.btnHTPlay);

        easyButton.setOnClickListener(v -> {
            Intent intent = new Intent(Difficulty.this, ShipLoading.class);
            intent.putExtra("DIFFICULTY", "EASY");
            MusicPlayer.getInstance().playClickSound();
            startActivity(intent);
        });

        normalButton.setOnClickListener(view -> {
            Intent intent = new Intent(Difficulty.this, ShipLoading.class);
            intent.putExtra("DIFFICULTY", "NORMAL");
            MusicPlayer.getInstance().playClickSound();
            startActivity(intent);
        });

        hardButton.setOnClickListener(v -> {
            Intent intent = new Intent(Difficulty.this, ShipLoading.class);
            intent.putExtra("DIFFICULTY", "HARD");
            MusicPlayer.getInstance().playClickSound();
            startActivity(intent);
        });

        howToPlayButton.setOnClickListener(v -> {
            Intent intent = new Intent(Difficulty.this, HowToPlay.class);
            MusicPlayer.getInstance().playClickSound();
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            MusicPlayer.getInstance().playClickSound();
            finish();
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