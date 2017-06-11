package com.corral.androidshoppinglist;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Context esto = this;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent i = new Intent(esto, ActivityListaList.class);
                startActivity(i);
                finish();
            }
        }, 3000);
        ((TextView) findViewById(R.id.tituloView)).startAnimation((Animation) AnimationUtils.loadAnimation(StartActivity.this,R.anim.translate));
        MediaPlayer mp = MediaPlayer.create(this, R.raw.efectoaudio);
        mp.start();
    }
}
