package com.yamilab.lullababy;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String[] data_time;
    String[] data_melody;
    private ImageButton btnStart,btnStop,btnPrevious,btnNext;

    private TextView textMelody;
    private TextView textTimer;

    private ProgressBar progressBar;

    private ObjectAnimator animation;

    private int melodyIndex=1;

    private int timer=300000;

    private SeekBar timerControl= null;

    CountDownTimer cTimer=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //реклама
        // Load an ad into the AdMob banner view.
        //AdView adView = (AdView) findViewById(adView);
        //AdRequest adRequest = new AdRequest.Builder()
        //        .addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
        //        .build();
        //adView.loadAd(adRequest);

        textMelody=(TextView) findViewById(R.id.textViewMelody);
        textTimer=(TextView) findViewById(R.id.textViewTimer);


        data_melody=
                getResources().getStringArray(R.array.melody_array);

        textMelody.setText(data_melody[melodyIndex]);
        textTimer.setText(timer/60000+ " мин.");

        timerControl = (SeekBar) findViewById(R.id.timer_bar);

        timerControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress<5)
                {progress=5;
                    textTimer.setText(progress+ " мин.");}
                else textTimer.setText(progress+ " мин.");
                if (progress>175) {progress=600;
                    textTimer.setText("∞ мин.");}
                else textTimer.setText(progress+ " мин.");


                timer=progress*60000;

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
        animation = ObjectAnimator.ofInt (progressBar, "progress", 500, 0); //
        //animation.setDuration (30000); //in milliseconds
        animation.setInterpolator (new LinearInterpolator());
        //animation.start();

       btnStart=(ImageButton) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
                animation.setDuration (timer); //in milliseconds
                startTimer(timer);
                animation.start();
            }
        });

        btnStop=(ImageButton) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
                animation.cancel();
                progressBar.clearAnimation();
            }
        });

        btnPrevious=(ImageButton) findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                melodyIndex--;
                if (melodyIndex<0) melodyIndex=11;
                textMelody.setText(data_melody[melodyIndex]);
            }
        });

        btnNext=(ImageButton) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                melodyIndex++;
                if (melodyIndex>11) melodyIndex=0;
                textMelody.setText(data_melody[melodyIndex]);
            }
        });


    }

    void startTimer(int time) {

        cTimer = new CountDownTimer(time,1000) {

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
        animation.start ();
    }

    void cancelTimer(){
        if (cTimer!=null)
            cTimer.cancel();


    }
}
