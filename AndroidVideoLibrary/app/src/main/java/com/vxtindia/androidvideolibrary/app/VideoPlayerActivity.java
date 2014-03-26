package com.vxtindia.androidvideolibrary.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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

    public static final String KEY_SCREEN_ORIENTATION = "keyScreenOrientation";

    public static final int ORIENTATION_PORTRAIT = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;
    private int screenOrientation=ORIENTATION_PORTRAIT;

    private String url = "";
    private String videoTitle = "";


    private SurfaceView videoSurface;
    private MediaPlayer player;
    private VideoControllerView controller;

    private LinearLayout layoutProgressBar;

    private int bufferPosition;
    private int mCurrentPosition=-1;

    private boolean fullScreen = false;

    private ActionBarHider actionBarHider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        setupActionBar();

        actionBarHider = new ActionBarHider(this);

        layoutProgressBar = (LinearLayout) findViewById(R.id.layoutPregressBar);

        videoTitle = getIntent().getStringExtra(KEY_VIDEO_TITLE);
        url = getIntent().getStringExtra(KEY_URL_STRING);


        setTitle(videoTitle);

        screenOrientation = getIntent().getIntExtra(KEY_SCREEN_ORIENTATION, ORIENTATION_PORTRAIT);

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);

        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        controller = new VideoControllerView(this,false);


    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        fullScreen = getScreenOrientation();

        if(player==null){
            Log.d(TAG, "player created");

            setupScreenOrientation();

            player =new MediaPlayer();
            layoutProgressBar.setVisibility(View.VISIBLE);
            prepareVideo();
        }
        else{
            showActionBarAndController();
        }

    }

    private void prepareVideo(){
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnErrorListener(this);
            player.setDataSource(this, Uri.parse(url));
            player.setOnPreparedListener(this);
            player.setOnBufferingUpdateListener(this);
            player.setOnCompletionListener(this);

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

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if(player != null){
            player.release();
            player = null;
        }
        controller.setMediaPlayer(null);
    }

    private void setupScreenOrientation(){
        if(screenOrientation == ORIENTATION_LANDSCAPE){
            fullScreen = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            fullScreen = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG , "onConfigurationChanged");

        fullScreen = getScreenOrientation();
        setScreenSize();
        controller.updateFullScreen();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        showActionBarAndController();
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
        player.setScreenOnWhilePlaying(true);

        if(mCurrentPosition == -1)
            player.start();
        else{
            player.seekTo(mCurrentPosition);
        }
        showActionBarAndController();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG , "Surface Destroyed");
        mCurrentPosition = player.getCurrentPosition();
        player.pause();
    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {

        Log.d(TAG , "onPrepared");
        layoutProgressBar.setVisibility(View.GONE);
        //fullScreen = getScreenOrientation();
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        setScreenSize();

    }
    // End MediaPlayer.OnPreparedListener

    public boolean getScreenOrientation(){

        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT){
            return false;
        }

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

        if(layoutProgressBar != null && layoutProgressBar.getVisibility() == View.VISIBLE){

            layoutProgressBar.setVisibility(View.GONE);
        }

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

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            actionBarHider.hide();
        }
    };

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void showActionBarAndController(){
        actionBarHider.show();
        delayedHide(3000);
        controller.show();
    }

}