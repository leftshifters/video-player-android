package com.vxtindia.androidvideolibrary.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;

//using http://www.brightec.co.uk/blog/custom-android-media-controller
public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        VideoControllerView.MediaPlayerControl{

    private static final String TAG = "VideoPlayerActivity" ;
    public static final String KEY_VIDEO_TITLE = "keyVideoLabel";
    public static final String KEY_URL_STRING = "keyUrlString";
    private String url = "";
    private String videoTitle = "";


    private TextView tvVideoTitle;
    private SurfaceView videoSurface;
    private MediaPlayer player;
    private VideoControllerView controller;

    private ProgressDialog progressDialog;

    private int bufferPosition;

    private boolean fullScreen = false;
    private boolean playerCreated = false;

    private boolean videoReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoTitle = getIntent().getStringExtra(KEY_VIDEO_TITLE);
        url = getIntent().getStringExtra(KEY_URL_STRING);

        tvVideoTitle = (TextView) findViewById(R.id.videoTitle);
        tvVideoTitle.setText(videoTitle);

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);

        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        controller = new VideoControllerView(this,false);

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

        if(player == null){
            Log.d(TAG, "player created");
            player = new MediaPlayer();
            playerCreated = true;
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        fullScreen = getScreenOrientation();

        if(playerCreated){
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getResources().getString(R.string.loading_video));
            progressDialog.show();

            try {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setOnErrorListener(this);
                player.setDataSource(this, Uri.parse(url));
                player.setOnPreparedListener(this);

                videoReady = true;
                player.prepareAsync();

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
        else{
            //player.start();
            controller.show();
        }

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        if(player != null && player.isPlaying()){
            //player.release();
            player.pause();
        }
        playerCreated = false;
        videoReady = false;

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

        if(player != null && player.isPlaying()){
            //player.release();
            player.pause();
        }

        playerCreated = false;
        videoReady = false;

    }

    @Override
    protected void onDestroy() {

        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if(player != null){
            player.release();
            player = null;
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
        Log.d(TAG , "Surface created");
        player.setDisplay(holder);

        //player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG , "Surface Destroyed");
    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {

        Log.d(TAG , "onPrepared");

        progressDialog.dismiss();

        fullScreen = getScreenOrientation();


        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        player.setOnBufferingUpdateListener(this);
        player.setOnCompletionListener(this);

        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setScreenOnWhilePlaying(true);

        setScreenSize();

        if(videoReady){
            player.start();

            Log.d(TAG, "player started");
        }

    }
    // End MediaPlayer.OnPreparedListener

    public boolean getScreenOrientation(){

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            return false;
        }
        else if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }
        else
            return false;
    }

    public void setScreenSize(){

        Log.d(TAG, "setScreenSize");

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        //int screenHeight = getResources().getDisplayMetrics().heightPixels;

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
        //Log.d(TAG, "buffer Percentage=" + bufferPosition);
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

    //Implement MediaPlayer.OnErrorListener
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int framework_err, int impl_err) {

        Log.d(TAG, "Error: " + framework_err + "," + impl_err);

        //statePrepared=false;

        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        showErrorAlertDialog();
        return true;
    }
    //End MediaPlayer.OnErrorListener

    public void showErrorAlertDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.error_dialog_title)
                .setCancelable(false)
                .setMessage(R.string.error_dialog_message)
                .setPositiveButton(R.string.error_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            VideoPlayerActivity.this.finish();
                    }
                });
        AlertDialog alert=alertDialogBuilder.create();
        alert.show();

    }

}