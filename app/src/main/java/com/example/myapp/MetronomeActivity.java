package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.Nullable;

public class MetronomeActivity extends AppCompatActivity {

    private final short MIN_BPM = 1;
    private final short MAX_BPM =  300;

    private short bpm = 1;
    private short noteValue = 4;
    private short beats = 4;
    private double beatSound = 2440;
    private double sound = 6440;
    private AudioManager audio;
    private MetronomeAsyncTask metroTask;

    //private Button plusButton;
    //private Button minusButton;
    //private TextView currentBeat;


    //private Toolbar toolbar;
    private boolean playing;
    private FloatingActionButton playStopFAB;
    private Button addOneButton;
    private Button addFiveButton;
    private Button minusOneButton;
    private Button minusFiveButton;
    private SeekBar seekBarTempo;
    private TextView seekBarTextView;
//    private GestureDetectorCompat gestureObject;
    private ImageButton tunerImageButton;
    private ImageButton bpmImageButton;

    private Beats beat;
    private Handler handler;

    // have in mind that: http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
    // in this case we should be fine as no delayed messages are queued
    private Handler getHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                String message = (String)msg.obj;
//                if(message.equals("1"))
//                    currentBeat.setTextColor(Color.GREEN);
//                else
//                    currentBeat.setTextColor(getResources().getColor(R.color.yellow));
//                currentBeat.setText(message);
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metronome_page);

//        gestureObject = new GestureDetectorCompat(this, new LearnGesture());

        metroTask = new MetronomeAsyncTask();

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(R.string.toolbar_metronome);
        playStopFAB = (FloatingActionButton) findViewById(R.id.playStopFAB);
        addOneButton = (Button) findViewById(R.id.addOneButton);
        addFiveButton = (Button) findViewById(R.id.addFiveButton);
        minusOneButton = (Button) findViewById(R.id.minusOneButton);
        minusFiveButton = (Button) findViewById(R.id.minusFiveButton);
        seekBarTempo = (SeekBar) findViewById(R.id.seekBarTempo);
        seekBarTextView = (TextView) findViewById(R.id.tempoTextView);

        seekBarTempo.setMax(300);

        Spinner beatSpinner = (Spinner) findViewById(R.id.beatspinner);
        ArrayAdapter<Beats> arrayBeats =
                new ArrayAdapter<Beats>(this,
                        android.R.layout.simple_spinner_item, Beats.values());
        beatSpinner.setAdapter(arrayBeats);
        beatSpinner.setSelection(Beats.four.ordinal());
        arrayBeats.setDropDownViewResource(R.layout.spinner_dropdown);
        beatSpinner.setOnItemSelectedListener(beatsSpinnerListener);

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        playing = false;
        playStopFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!playing) {
                    //Log.i("setbeatfab", beat.getNum() + "");
                    metroTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                    playing = true;
                    playStopFAB.setImageResource(R.drawable.ic_stop_white_36dp);
                }
                else {
                    metroTask.stop();
                    metroTask = new MetronomeAsyncTask();
                    Runtime.getRuntime().gc();
                    playing = false;
                    playStopFAB.setImageResource(R.drawable.ic_play_arrow_white_36dp);
                }
            }
        });

        seekBarTempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bpm = (short) (progress);
                seekBarTextView.setText("" + bpm);

                if(bpm == 0) {
                    playStopFAB.setEnabled(false);
                } else {
                    playStopFAB.setEnabled(true);
                    metroTask.setBpm(bpm);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        addOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = (bpm >= MAX_BPM) ? MAX_BPM : (++bpm);
                seekBarTextView.setText("" + bpm);
                metroTask.setBpm(bpm);
                seekBarTempo.setProgress(bpm);
            }
        });


        addFiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = (bpm + 5 > MAX_BPM) ? MAX_BPM : (bpm += 5);
                seekBarTempo.setProgress(bpm);
                seekBarTextView.setText("" + bpm);
                metroTask.setBpm(bpm);
            }
        });

        minusOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = (bpm <= MIN_BPM) ? MIN_BPM : (--bpm);
                seekBarTextView.setText("" + bpm);
                metroTask.setBpm(bpm);
                seekBarTempo.setProgress(bpm);
            }
        });

        minusFiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = (bpm - 5 < MIN_BPM) ? MIN_BPM : (bpm -= 5);
                seekBarTextView.setText("" + bpm);
                metroTask.setBpm(bpm);
                seekBarTempo.setProgress(bpm);
            }
        });

//        bpmImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MetronomeActivity.this, BpmActivity.class);
//                finish();
//                startActivity(intent);
//            }
//        });
//
//        tunerImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MetronomeActivity.this, GuitarTunerActivity.class);
//                finish();
//                startActivity(intent);
//            }
//        });
    }

    private AdapterView.OnItemSelectedListener beatsSpinnerListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            beat = (Beats) arg0.getItemAtPosition(arg2);
//            Log.i("setbeatadapt", beat.getNum() + "");

            metroTask.setBeat(beat.getNum());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    };

    private class MetronomeAsyncTask extends AsyncTask<Void,Void,String> {
        Metronome metronome;

        MetronomeAsyncTask() {
            handler = getHandler();
            metronome = new Metronome(handler);
        }

        protected String doInBackground(Void... params) {
            //Log.i("background", beats + "");

            metronome.setBeat(beat.getNum());
            metronome.setNoteValue(noteValue);
            metronome.setBpm(bpm);
            metronome.setBeatSound(beatSound);
            metronome.setSound(sound);

            metronome.play();

            return null;
        }

        public void stop() {
            metronome.stop();
            metronome = null;
        }

        public void setBpm(short bpm) {
            metronome.setBpm(bpm);
            metronome.calcSilence();
        }

        public void setBeat(short beat) {
            if(metronome != null)
                metronome.setBeat(beat);
        }

    }

    @Override
    public void onBackPressed() {
        backActivity(MetronomeActivity.this, MainActivity.class);
    }

    private void backActivity(Object fromActivity, Object toActivity) {
        try {
            Intent intent = new Intent((Context) fromActivity, (Class<?>) toActivity);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            System.out.println("Какая-то ошибочка");
        }
    }

    //responsible for swipe
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        this.gestureObject.onTouchEvent(event);
//        return super.onTouchEvent(event);
//    }
//
//    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//
//            if(e2.getX() > e1.getX()) {
//
//                Intent intent = new Intent(MetronomeActivity.this, BpmActivity.class);
//                finish();
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_left_right, R.anim.slide_out_left_right);
//
//            } else
//            if(e2.getX() < e1.getX()) {
//                //Intent intent = new Intent(MetronomeActivity.this, TunerActivity.class);
//                Intent intent = new Intent(MetronomeActivity.this, GuitarTunerActivity.class);
//                finish();
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right_left, R.anim.slide_out_right_left);
//            }
//
//            return true;
//        }
//    }
}
