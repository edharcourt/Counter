package com.example.ehar.counter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button start = null;
    Button reset = null;
    TextView count = null;
    Counter c = null;
    Timer t = null;
    boolean running = false;

    private SoundPool soundPool = null;
    private int clickSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.start = (Button) findViewById(R.id.start);
        this.reset = (Button) findViewById(R.id.reset);
        this.count = (TextView) findViewById(R.id.count);

        this.soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        this.clickSound = this.soundPool.load(this, R.raw.bloop, 1);

        this.count.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                animate();
                soundPool.play(clickSound, 1f, 1f, 1, 0, 1f);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        long count = getPreferences(MODE_PRIVATE).getLong("COUNT", 0);
        start.setEnabled(getPreferences(MODE_PRIVATE).getBoolean("START_ENABLED", true));

        this.c = new Counter(count);
        this.t = new Timer();

        // if the start button is not enabled then it must be running
        // when paused, so need to
        if (!start.isEnabled()) {
            t.scheduleAtFixedRate(c,0,1000);
        }

        start.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    start.setEnabled(false);
                    t.scheduleAtFixedRate(c,0,1000);
                    MainActivity.this.running = true;
                }
            }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferences(MODE_PRIVATE).edit().putLong("COUNT", c.count).apply();
        getPreferences(MODE_PRIVATE).edit().putBoolean("START_ENABLED", start.isEnabled()).apply();
//        t.cancel();
    }

    public void animate() {
        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.counter);
        anim.setTarget(this.count);
        anim.start();
    }

    /* -------------------------- */
    class Counter extends TimerTask {

        long count = 0;

        public Counter(long i ) { count = i; }

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.count.setText(Long.toString(count));
                    count++;
                }
            });
        }
    }
}
