package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    public static ImageButton btnPlay, btnStop;
    public static Button btnShow;
    public static TextView textViewSongTime, textViewLyric;
    private Intent playerService;
    public static SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        playerService = new Intent(MainActivity.this, PlayerInService.class);
        startService(playerService);
    }

    private void initView() {
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textViewSongTime = (TextView) findViewById(R.id.textViewSongTime);
        textViewLyric = (TextView) findViewById(R.id.lyric);
        btnShow = (Button) findViewById(R.id.btnShow);
        TextView textViewIconCopyright = (TextView) findViewById(R.id.textViewIconCopyright);
        textViewLyric.setVisibility(View.INVISIBLE);
        textViewIconCopyright.setText(Html.fromHtml(getString(R.string.quote)));
        textViewIconCopyright.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        if (!PlayerInService.mp.isPlaying()) {
            PlayerInService.mp.stop();
            startService(playerService);
        } else {
            btnPlay.setBackgroundResource(R.drawable.pause);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {

        try {
            if (!PlayerInService.mp.isPlaying()) {

                btnPlay.setBackgroundResource(R.drawable.play);

            } else {
                btnPlay.setBackgroundResource(R.drawable.pause);


            }
        } catch (Exception e) {
            Log.d("Exception", "" + e.getMessage() + e.getStackTrace() + e.getCause());
        }
        super.onResume();
    }
}
