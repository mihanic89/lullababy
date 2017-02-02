package com.yamilab.lullababy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String[] data_time;
    String[] data_melody;
    private Button btnStart,btnStop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("09D7B5315C60A80D280B8CDF618FD3DE")
                .build();
        adView.loadAd(adRequest);

        data_time =
                getResources().getStringArray(R.array.time_array);
        data_melody=
                getResources().getStringArray(R.array.melody_array);


        ArrayAdapter<String> adapterMelody =
                new ArrayAdapter<String>(this, R.layout.simple_spinner_item,data_melody);

        adapterMelody.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);


        ArrayAdapter<String> adapterTime =
                new ArrayAdapter<String>(this, R.layout.simple_spinner_item,data_time);

        adapterTime.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);


        Spinner spinnerMelody = (Spinner) findViewById(R.id.spinner_melody);
        spinnerMelody.setAdapter(adapterMelody);

        Spinner spinnerTime = (Spinner) findViewById(R.id.spinner_time);
        spinnerTime.setAdapter(adapterTime);

        spinnerMelody.setPrompt(getResources().getString(R.string.melody));
        spinnerTime.setPrompt(getResources().getString(R.string.time));

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);


    }

    public void onClick(View v) {

    }

}
