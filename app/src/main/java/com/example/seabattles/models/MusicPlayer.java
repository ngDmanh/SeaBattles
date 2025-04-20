package com.example.seabattles.models;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.seabattles.R;

public class MusicPlayer {
    private static MusicPlayer instance;
    private MediaPlayer mediaPlayer;
    private MediaPlayer clickPlayer;
    private boolean isMusicOn = true;
    private boolean isSoundEffectOn = true;

    private MusicPlayer() {
    }

    public static synchronized MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public void init(Context context) {

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.music);
            mediaPlayer.setLooping(true);
            if (isMusicOn) {
                mediaPlayer.start();
            }
        }

        if (clickPlayer == null) {
            clickPlayer = MediaPlayer.create(context, R.raw.click);
        }
    }

    public void toggleMusic() {
        if (isMusicOn) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            isMusicOn = false;
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
            isMusicOn = true;
        }
    }

    public void toggleSoundEffect() {
        isSoundEffectOn = !isSoundEffectOn;
    }

    public boolean isMusicOn() {
        return isMusicOn;
    }

    public boolean isSoundEffectOn() {
        return isSoundEffectOn;
    }

    public void playClickSound() {
        if (isSoundEffectOn && clickPlayer != null) {
            clickPlayer.seekTo(0);
            clickPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void resume() {
        if (mediaPlayer != null && isMusicOn && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (clickPlayer != null) {
            clickPlayer.release();
            clickPlayer = null;
        }
        instance = null;
    }
}