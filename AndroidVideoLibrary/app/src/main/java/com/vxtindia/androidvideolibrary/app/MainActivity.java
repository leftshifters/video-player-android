package com.vxtindia.androidvideolibrary.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    public static final String KEY_VIDEO_LABEL = "videoLable";
    public static final String KEY_URL_STRING = "urlString";

    private String url="http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
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
                intent.putExtra(KEY_VIDEO_LABEL, "Big Buck Bunny");
                intent.putExtra(KEY_URL_STRING, url);
                startActivity(intent);

            }
        });
    }

}
