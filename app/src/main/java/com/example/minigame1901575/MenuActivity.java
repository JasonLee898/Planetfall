package com.example.minigame1901575;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {

    private ImageButton playButton;
    private ImageButton soundButton;
    private Button exitButton;
    private boolean isSoundOn = true;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);



        // Initialize the play button
        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the sound button
        soundButton = findViewById(R.id.sound_button);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoundOn) {
                    // Turn off the background music
                    isSoundOn = false;
                    soundButton.setImageResource(R.drawable.ic_baseline_volume_off_24);
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                } else {
                    // Turn on the background music
                    isSoundOn = true;
                    soundButton.setImageResource(R.drawable.ic_baseline_volume_up_24);
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                }
            }
        });

        exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        // Initialize the background music
        mediaPlayer = MediaPlayer.create(this, R.raw.backgroundmusicspace);
        mediaPlayer.setLooping(true);
        if (isSoundOn) {
            mediaPlayer.start();
        }
    }

    public void onBackPressed() {
        // Stop the background music when the user presses the back button
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        super.onBackPressed();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the media player resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}