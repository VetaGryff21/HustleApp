package com.example.myapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecorderActivity extends AppCompatActivity {

    final String TAG = "myLogs";

    int myBufferSize = 8192;
    int myRate = 11025;
    short myBTT = 16;
    AudioRecord audioRecord = null;
    boolean isReading = false;
    boolean isRecording = false;

    AudioTrack track = null;
    short[][]   buffers  = new short[256][160];
    int ix = 0;



    static String LOG_TAG = "HustleApp logs";

    private MediaRecorder recorder = null;
    private String recordFile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder_page);

        //requestRecordAudioPermission();
        //createAudioRecorder();
        //Log.d(TAG, "init state = " + audioRecord.getState());

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFile);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        recordFile = this.getFilesDir().getAbsolutePath() + "/micrec.3gp";
        View startPlay = findViewById(R.id.start_play);
        View stopPlay = findViewById(R.id.stop_play);
        View startRecord = findViewById(R.id.start_record);
        View stopRecord = findViewById(R.id.stop_record);

        startRecord.setOnClickListener(v -> startRecording());
        stopRecord.setOnClickListener(v -> stopRecording());
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFile);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    /*private void requestRecordAudioPermission() {
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
            isRecording = true;*/
//            try
//            {
//                int N = myBufferSize;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    track = new AudioTrack(
//                            new AudioAttributes.Builder()
//                                    .setUsage(AudioAttributes.USAGE_MEDIA)
//                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                                    .build(),
//                            new AudioFormat.Builder()
//                                    .setSampleRate(myRate)
//                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
//                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build(),
//                            myBufferSize,
//                            AudioTrack.MODE_STREAM,
//                            AudioManager.AUDIO_SESSION_ID_GENERATE);
//                } else {
//                    //support for Android KitKat
//                    track = new AudioTrack(AudioManager.STREAM_MUSIC,
//                            myRate,
//                            AudioFormat.CHANNEL_OUT_MONO,
//                            AudioFormat.ENCODING_PCM_16BIT,
//                            myBufferSize,
//                            AudioTrack.MODE_STREAM);
//                }
//
//                track.play();
//                /*
//                 * Loops until something outside of this thread stops it.
//                 * Reads the data from the recorder and writes it to the audio track for playback.
//                 */
//                    Log.i("Map", "Writing new data to buffer");
//                    short[] buffer = buffers[ix++ % buffers.length];
//                    N = audioRecord.read(buffer,0,buffer.length);
//                    track.write(buffer, 0, buffer.length);
//            }
//            catch(Throwable x)
//            {
//                Log.w("Audio", "Error reading voice audio", x);
//            }
//            /*
//             * Frees the thread's resources after the loop completes so that it can be run again
//             */
//            finally
//            {
//                audioRecord.stop();
//                audioRecord.release();
//                track.stop();
//                track.release();
//            }
        /*}

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
        track.play();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            track = new AudioTrack(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    new AudioFormat.Builder()
                            .setSampleRate(myRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build(),
                            myBufferSize,
                            AudioTrack.MODE_STREAM,
                            AudioManager.AUDIO_SESSION_ID_GENERATE);
                } else {
                    //support for Android KitKat
                    track = new AudioTrack(AudioManager.STREAM_MUSIC,
                            myRate,
                            AudioFormat.CHANNEL_IN_STEREO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            myBufferSize,
                            AudioTrack.MODE_STREAM);
                }
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
                    track.write(myBuffer, 0, myBuffer.length);
                }
            }
        }).start();
    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte

        String filePath = "/sdcard/voice8K16bitmono.wav";
        short sData[] = new short[myBufferSize/2];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format

            audioRecord.read(sData, 0, myBufferSize/2);
            Log.d("eray","Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, myBufferSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }


    public void readStop(View v) {
        Log.d(TAG, "read stop");
        track.play();
        isReading = false;
//        Wave wave = new Wave(myRate,NUM_CHANNELS,outputSignal,0,outputSignal.length-1);
//        if(wave.wroteToFile("your_filename.wav"))
//            log("Click write successful.");
//        else log("Click write failed.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isReading = false;
        if (audioRecord != null) {
            audioRecord.release();
        }
    }*/

}

