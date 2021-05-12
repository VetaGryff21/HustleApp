package com.example.myapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.Nullable;

public class RecorderActivity extends AppCompatActivity {

    final String TAG = "myLogs";

    int myBufferSize = 8192;
    int myRate = 11025;
    int myBTT = 16;
    AudioRecord audioRecord = null;
    boolean isReading = false;
    boolean isRecording = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder_page);

        requestRecordAudioPermission();


        createAudioRecorder();

        Log.d(TAG, "init state = " + audioRecord.getState());
    }

    private void requestRecordAudioPermission() {
        //check API version, do nothing if API version < 23!
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Activity", "Granted!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Activity", "Denied!");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private int minSize;
    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 32000, 44100 };

    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_FLOAT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        Log.d("SoundMeter", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            Log.d("SoundMeter", "Found a supported bufferSize, attempting to instantiate");
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED){
                                minSize = bufferSize;
                                myRate = rate;
                                myBTT = audioFormat;
                                return recorder;
                            }
                            else
                                recorder.release();
                        }
                    } catch (Exception e) {
                        Log.e("SoundMeter", rate + " Exception, keep trying.", e);
                    }
                }
            }
        }
        return null;
    }


    void createAudioRecorder() {
        int sampleRate = 11025;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        try {

            int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, channelConfig, audioFormat, bufferSize);

            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED){

                Log.d(TAG, "--------------!");

            }
            else {
                Log.e(TAG, "Initialisation failed !");
            }

        } catch (Exception e) {

            Log.i("AudioRecording", "Error in Audio Record");

        }
    }

    public void recordStart(View v) {
        Log.d(TAG, "record start");
        audioRecord = findAudioRecord();
        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED){
            audioRecord.startRecording();
            isRecording = true;

        }
        else{
            Log.e("SoundMeter", "ERROR, could not create audio recorder");
        }

        int recordingState = audioRecord.getRecordingState();
        Log.d(TAG, "recordingState = " + recordingState);
    }


    public void recordStop(View v) {
        Log.d(TAG, "record stop");
        audioRecord.stop();
        audioRecord.release();
        isRecording = false;

//        copyWaveFile(getTempFilename(),getFilename());
//        deleteTempFile();
    }
//
//    private void deleteTempFile() {
//        File file = new File(getTempFilename());
//        file.delete();
//    }

    private Thread recordingThread = null;

    public void readStart(View v) {
        Log.d(TAG, "read start");
        isReading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (audioRecord == null)
                    return;

                byte[] myBuffer = new byte[myBufferSize];
                int readCount = 0;
                int totalCount = 0;
                while (isReading) {
                    readCount = audioRecord.read(myBuffer, 0, myBufferSize);
                    totalCount += readCount;
                    Log.d(TAG, "readCount = " + readCount + ", totalCount = "
                            + totalCount);
                }
            }
        }).start();
    }



    public void readStop(View v) {
        Log.d(TAG, "read stop");
        isReading = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isReading = false;
        if (audioRecord != null) {
            audioRecord.release();
        }
    }


}

