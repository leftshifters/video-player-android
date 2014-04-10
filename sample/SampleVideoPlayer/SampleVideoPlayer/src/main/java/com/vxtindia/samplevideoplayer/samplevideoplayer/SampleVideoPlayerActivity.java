package com.vxtindia.samplevideoplayer.samplevideoplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.vxtindia.androidvideolibrary.app.VideoPlayerActivity;

public class SampleVideoPlayerActivity extends Activity {

    private EditText videoUrl;
    private EditText videoTitle;
    private RadioGroup radioGroupOrienatation;
    private Button buttonPlay;

    private String strVideoUrl;
    private String strVideoTitle;
    private String strOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_video_player);

        videoUrl = (EditText) findViewById(R.id.editTextVideoUrl);
        videoTitle = (EditText) findViewById(R.id.editTextVideoTitle);
        buttonPlay = (Button) findViewById(R.id.buttonPlay);
        radioGroupOrienatation = (RadioGroup) findViewById(R.id.radioGroupOrientation);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strVideoUrl = videoUrl.getText().toString();
                strVideoTitle = videoTitle.getText().toString();

                RadioButton orientation = (RadioButton) findViewById(radioGroupOrienatation.getCheckedRadioButtonId());
                strOrientation = orientation.getText().toString();

               // Toast.makeText(SampleVideoPlayerActivity.this,strVideoUrl + "\n" + strVideoTitle + "\n" + strOrientation, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(SampleVideoPlayerActivity.this, VideoPlayerActivity.class);
                intent.putExtra(VideoPlayerActivity.KEY_URL_STRING, strVideoUrl);
                intent.putExtra(VideoPlayerActivity.KEY_VIDEO_TITLE, strVideoTitle);
                if(strOrientation.equals("Landscape")){
                    intent.putExtra(VideoPlayerActivity.KEY_SCREEN_ORIENTATION, VideoPlayerActivity.ORIENTATION_LANDSCAPE);
                }else if(strOrientation.equals("Portrait")){
                    intent.putExtra(VideoPlayerActivity.KEY_SCREEN_ORIENTATION, VideoPlayerActivity.ORIENTATION_PORTRAIT);
                }

                startActivity(intent);

            }
        });
    }

}
