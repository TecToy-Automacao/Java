package br.com.example.demobnk;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button btnEntra = findViewById(R.id.btnEntra);
        ToggleButton btnOnOff = (ToggleButton) findViewById(R.id.tgbSom);

        MediaPlayer song = MediaPlayer.create(this, R.raw.musica);
        song.setLooping(true);

        if (song.isPlaying()!=true){
            song.start();
        }
        AudioManager audioManager = (AudioManager)getSystemService(this.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_VIBRATE);


        btnOnOff.setChecked(true);
        btnOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI);
                } else {
                    // The toggle is disabled
                    audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI);
                }
            }
        });

        btnEntra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                song.stop();
                finish();
            }
        });
    }
}