package com.vxtindia.androidvideolibrary.app;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Created by neha on 19/03/14.
 */
public class ActionBarHider {

    private Activity mActivity;

    private boolean mShowing = true;

    private static final int sDefaultTimeout = 3000;



    public ActionBarHider(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void show(){
        show(sDefaultTimeout);
    }

    public void show(int timeout){

        WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mActivity.getWindow().setAttributes(attrs);
        mActivity.getActionBar().show();

        mShowing = true;
    }

    public void hide(){
        mActivity.getActionBar().hide();

        WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mActivity.getWindow().setAttributes(attrs);

        mShowing = false;
    }

    public boolean isShowing(){
        return mShowing;
    }



}
