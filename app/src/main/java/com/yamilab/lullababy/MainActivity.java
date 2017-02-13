package com.yamilab.lullababy;


import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.yamilab.lullababy.PlayNewAudio";

    String[] data_time;
    String[] data_melody;
    private ImageButton btnStart, btnStop, btnPrevious, btnNext;

    private TextView textMelody;
    private TextView textTimer;

    private ProgressBar progressBar;

    private ObjectAnimator animation;

    private int melodyIndex, defValue = 0;

    private int timer;

    private SeekBar timerControl = null;

    CountDownTimer cTimer = null;

    SharedPreferences sPref;

    private MediaPlayerService player;
    boolean serviceBound = false;

    ArrayList<Audio> audioList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        loadAudio();
        //реклама
        // Load an ad into the AdMob banner view.

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
                .build();
        adView.loadAd(adRequest);


        sPref = getPreferences(MODE_PRIVATE);
        melodyIndex = sPref.getInt("melody", 0);
        timer = sPref.getInt("timer", 300000);



        textMelody = (TextView) findViewById(R.id.textViewMelody);
        textTimer = (TextView) findViewById(R.id.textViewTimer);


        data_melody =
                getResources().getStringArray(R.array.melody_array);

        textMelody.setText(data_melody[melodyIndex]);

        textTimer.setText(timer / 60000 + " мин.");


        timerControl = (SeekBar) findViewById(R.id.timer_bar);
        timerControl.setProgress(timer / 60000);

        timerControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 5) {
                    progress = 5;
                    textTimer.setText(progress + " мин.");
                } else textTimer.setText(progress + " мин.");
                if (progress > 175) {
                    progress = 600;
                    textTimer.setText("∞ мин.");
                } else textTimer.setText(progress + " мин.");


                timer = progress * 60000;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //анимация
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        animation = ObjectAnimator.ofInt(progressBar, "progress", 500, 0); //
        //animation.setDuration (30000); //in milliseconds
        animation.setInterpolator(new LinearInterpolator());
        //animation.start();

        btnStart = (ImageButton) findViewById(R.id.btnStart);
        if (isMyServiceRunning(MediaPlayerService.class))
            btnStart.setImageResource(android.R.drawable.ic_media_pause);
            else
            btnStart.setImageResource(android.R.drawable.ic_media_play);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning(MediaPlayerService.class)){
                    btnStart.setImageResource(android.R.drawable.ic_media_play);
                    player.stopPlayer();
                    cancelTimer();
                    animation.cancel();
                    progressBar.clearAnimation();
                    progressBar.setVisibility(View.INVISIBLE);
                }

                    else{
                    start_lullababy();
                    btnStart.setImageResource(android.R.drawable.ic_media_pause);
                }
                /*
                cancelTimer();
                animation.setDuration(timer); //in milliseconds

                sPref = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putInt("melody", melodyIndex);
                ed.putInt("timer", timer);
                ed.commit();
                playAudio(melodyIndex);
                startTimer(timer);
                animation.start();
                */
            }
        });
        /*
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stopPlayer();
                cancelTimer();
                animation.cancel();
                progressBar.clearAnimation();

            }
        });
        */
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                melodyIndex--;
                if (melodyIndex < 0) melodyIndex = 11;
                textMelody.setText(data_melody[melodyIndex]);
               // if( AudioManager.isMusicActive())start_lullababy();
            }
        });

        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                melodyIndex++;
                if (melodyIndex > 11) melodyIndex = 0;
                textMelody.setText(data_melody[melodyIndex]);
              //  if(isMyServiceRunning(MediaPlayerService.class)==true)start_lullababy();

            }
        });


    }

    private void loadAudio() {
        String [] melody;
        Resources res = getResources();
        melody = res.getStringArray(R.array.melody_array);
        audioList = new ArrayList<>();

        audioList.add(new Audio(R.raw.music0,  getString(R.string.app_name),  melody[0]));
        audioList.add(new Audio(R.raw.music1,  getString(R.string.app_name),  melody[1]));
        audioList.add(new Audio(R.raw.music2,  getString(R.string.app_name),  melody[2]));
        audioList.add(new Audio(R.raw.music3,  getString(R.string.app_name),  melody[3]));
        audioList.add(new Audio(R.raw.music4,  getString(R.string.app_name),  melody[4]));
        audioList.add(new Audio(R.raw.music5,  getString(R.string.app_name),  melody[5]));
        audioList.add(new Audio(R.raw.music6,  getString(R.string.app_name),  melody[6]));
        audioList.add(new Audio(R.raw.music7,  getString(R.string.app_name),  melody[7]));
        audioList.add(new Audio(R.raw.music8,  getString(R.string.app_name),  melody[8]));
        audioList.add(new Audio(R.raw.music9,  getString(R.string.app_name),  melody[9]));
        audioList.add(new Audio(R.raw.music10,  getString(R.string.app_name),  melody[10]));
        audioList.add(new Audio(R.raw.music11,  getString(R.string.app_name),  melody[11]));
    }

    void start_lullababy(){
        progressBar.setVisibility(View.VISIBLE);
        cancelTimer();
        animation.setDuration(timer); //in milliseconds

        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("melody", melodyIndex);
        ed.putInt("timer", timer);
        ed.commit();
        playAudio(melodyIndex);
        startTimer(timer);
        animation.start();
    }

    void startTimer(int time) {

        cTimer = new CountDownTimer(time, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                //textTimer.setText("seconds remaining: " + millisUntilFinished / 1000);
                // Toast.makeText(MainActivity.this,"seconds remaining: " + millisUntilFinished / 1000,
                //        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                //  mTextField.setText("done!");
            }

        };
        cTimer.start();
        animation.start();
    }

    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();


    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            //Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;

    }

    public void setBtnPlay(){
        btnStart.setImageResource(android.R.drawable.ic_media_play);

    }

    public void setBtnPause(){
        btnStart.setImageResource(android.R.drawable.ic_media_pause);
    }
}
