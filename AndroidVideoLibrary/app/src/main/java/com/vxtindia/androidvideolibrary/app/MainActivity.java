package com.vxtindia.androidvideolibrary.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {


    private String url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private String videoLabel = "Big Buck Bunny";
    ImageButton play;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = (ImageButton) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                intent.putExtra(VideoPlayerActivity.KEY_VIDEO_TITLE, videoLabel);
                intent.putExtra(VideoPlayerActivity.KEY_URL_STRING, url);
                startActivity(intent);

            }
        });
    }

}
