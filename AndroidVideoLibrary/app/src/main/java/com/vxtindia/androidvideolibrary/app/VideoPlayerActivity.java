package com.vxtindia.androidvideolibrary.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;

//using http://www.brightec.co.uk/blog/custom-android-media-controller
public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        VideoControllerView.MediaPlayerControl{

    private static final String TAG = "VideoPlayerActivity" ;
    private String url="http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    private SurfaceView videoSurface;
    private MediaPlayer player;
    private VideoControllerView controller;

    private ProgressDialog progressDialog;

    private int bufferPosition;

    private boolean fullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        progressDialog = new ProgressDialog(this);

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);

        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new VideoControllerView(this,false);


        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading_video));


        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, Uri.parse(url));
            player.setOnPreparedListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("OnDestroy", "onDestroy");
        if(player != null){
            player.release();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG , "onConfigurationChanged");
        setScreenSize();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG , "Surface Changed");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG , "Surface Destroyed");
    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {

        progressDialog.dismiss();

        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        player.setOnBufferingUpdateListener(this);
        player.setOnCompletionListener(this);

        setScreenSize();

        player.start();
    }
    // End MediaPlayer.OnPreparedListener

    public void setScreenSize(){

        Log.d(TAG , "setScreenSize");

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();

        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();

        lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);

        videoSurface.setLayoutParams(lp);

    }


    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public void start() {
        player.start();
    }
    @Override
    public void pause() {
        player.pause();
    }
    @Override
    public int getDuration() {
        return player.getDuration();
    }
    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }
    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }
    @Override
    public int getBufferPercentage() {
        return bufferPosition;
    }
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }
    @Override
    public boolean canSeekForward() {
        return true;
    }
    @Override
    public boolean isFullScreen() {
        return fullScreen;
    }
    @Override
    public void toggleFullScreen() {

        fullScreen = !fullScreen;

        controller.updateFullScreen();

        if(fullScreen){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }
    //End VideoControllerView.MediaPlayerControl


    //Implement MediaPlayer.OnBufferingUpdateListener
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        //Log.d("OnBufferingUpdate",""+i);
        bufferPosition=i;
    }
    //End MediaPlayer.OnBufferingUpdateListener

    protected void setBufferPosition(int progress) {
        bufferPosition = progress;
    }

    //Implement MediaPlayer.OnCompletionListener
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "OnCompletion");
        mediaPlayer.seekTo(0);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int framework_err, int impl_err) {

        Log.d(TAG, "Error: " + framework_err + "," + impl_err);
        return true;
    }
}