package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.MediaController;

public class MusicController extends MediaController {

    public MusicController(Context c){
        super(c);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            super.hide();
            ((Activity) getContext()).finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void hide(){}



}