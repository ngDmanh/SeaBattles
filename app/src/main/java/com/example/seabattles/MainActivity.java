package com.example.seabattles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seabattles.models.MusicPlayer;

public class MainActivity extends AppCompatActivity {

    private Button btnPlay;
    private Button ibSound; // Điều khiển âm thanh phím
    private Button ibMusic; // Điều khiển nhạc nền

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        // Khởi tạo MusicPlayer khi ứng dụng bắt đầu
        MusicPlayer.getInstance().init(this);
        // Cập nhật trạng thái các nút
        updateSoundButton();
        updateMusicButton();
    }

    void initView() {
        setContentView(R.layout.activity_main);
        btnPlay = findViewById(R.id.btnPlay);
        ibSound = findViewById(R.id.ibSound);
        ibMusic = findViewById(R.id.ibMusic);

        ibSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().playClickSound();
                MusicPlayer.getInstance().toggleSoundEffect();
                updateSoundButton();
            }
        });

        ibMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().playClickSound();
                MusicPlayer.getInstance().toggleMusic();
                updateMusicButton();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().playClickSound();
                Intent intent = new Intent(MainActivity.this, Difficulty.class);
                startActivity(intent);
            }
        });
    }

    private void updateSoundButton() {
        if (MusicPlayer.getInstance().isSoundEffectOn()) {
            ibSound.setText("Sound:On");
        } else {
            ibSound.setText("Sound:Off");
        }
    }


    private void updateMusicButton() {
        if (MusicPlayer.getInstance().isMusicOn()) {
            ibMusic.setText("Music:On");
        } else {
            ibMusic.setText("Music:Off");
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayer.getInstance().release();
    }
}