package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.MediaController;

public class MusicController extends MediaController {

    public MusicController(Context c){
        super(c);
    }

    public OnBackPressedListener onBackPressedListener;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            super.hide();
            if(onBackPressedListener != null) {
                onBackPressedListener.onBackPressed();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void hide(){}

    interface OnBackPressedListener {
        void onBackPressed();
    }
}