package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class PlayerInService extends Service implements View.OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private WeakReference<ImageButton> btnPlay;
    private WeakReference<ImageButton> btnStop;
    private WeakReference<Button> btnShow;
    public static WeakReference<TextView> textViewSongTime, textViewLyric;
    public static WeakReference<SeekBar> songProgressBar;
    static Handler progressBarHandler = new Handler();
    public static MediaPlayer mp;
    private boolean isPause = false;

    @Override
    public void onCreate() {
        mp = new MediaPlayer();
        mp.reset();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initUI();
        super.onStart(intent, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initUI() {
        btnPlay = new WeakReference(MainActivity.btnPlay);
        btnStop = new WeakReference<>(MainActivity.btnStop);
        btnShow = new WeakReference<>(MainActivity.btnShow);
        textViewSongTime = new WeakReference<>(MainActivity.textViewSongTime);
        textViewLyric = new WeakReference<>(MainActivity.textViewLyric);
        songProgressBar = new WeakReference<>(MainActivity.seekBar);
        songProgressBar.get().setOnSeekBarChangeListener(this);
        btnPlay.get().setOnClickListener(this);
        btnStop.get().setOnClickListener(this);
        btnShow.get().setOnClickListener(this);
        mp.setOnCompletionListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                if (mp.isPlaying()) {
                    mp.pause();
                    isPause = true;
                    progressBarHandler.removeCallbacks(mUpdateTimeTask);
                    btnPlay.get().setBackgroundResource(R.drawable.play);
                    return;
                }
                if (isPause) {
                    mp.start();
                    isPause = false;
                    updateProgressBar();
                    btnPlay.get().setBackgroundResource(R.drawable.pause);
                    return;
                }
                if (!mp.isPlaying()) {
                    playSong();
                }
                break;
            case R.id.btnStop:
                mp.stop();
                onCompletion(mp);
                textViewSongTime.get().setText("0.00/0.00");
                break;
            case R.id.btnShow:
                if (textViewLyric.get().getVisibility() == View.INVISIBLE) {
                    btnShow.get().setText("Hide lyric");
                    textViewLyric.get().setVisibility(View.VISIBLE);
                } else {
                    btnShow.get().setText("Show lyric");
                    textViewLyric.get().setVisibility(View.INVISIBLE);
                }
        }
    }


    public void updateProgressBar() {
        try {
            progressBarHandler.postDelayed(mUpdateTimeTask, 100);
        } catch (Exception e) {

        }
    }

    static Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            long totalDuration = 0;
            long currentDuration = 0;
            try {
                totalDuration = mp.getDuration();
                currentDuration = mp.getCurrentPosition();
                textViewSongTime.get().setText(Utility.millSecondsToTimer(currentDuration) + "/" + Utility.millSecondsToTimer(totalDuration));
                int progress = (int) (Utility.getProgressPercentage(currentDuration, totalDuration));
                songProgressBar.get().setProgress(progress);
                progressBarHandler.postDelayed(this, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void playSong() {
        Utility.initNotification("Playing", this);
        try {
            mp.reset();
            Uri myUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.song);
            mp.setDataSource(this, myUri);
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    try {
                        mp.start();
                        updateProgressBar();
                        btnPlay.get().setBackgroundResource(R.drawable.pause);
                    } catch (Exception e) {
                        Log.i("EXCEPTION", "" + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        songProgressBar.get().setProgress(0);
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        btnPlay.get().setBackgroundResource(R.drawable.play);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentDuration = Utility.progressToTimer(seekBar.getProgress(), totalDuration);
        mp.seekTo(currentDuration);
        updateProgressBar();
    }

}
