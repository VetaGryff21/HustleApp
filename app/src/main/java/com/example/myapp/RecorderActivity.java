package com.example.myapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class RecorderActivity extends AppCompatActivity {

    final String TAG = "myLogs";

    int myBufferSize = 8192;
    int myRate = 11025;
    short myBTT = 16;
//    AudioRecord audioRecord = null;
    boolean isReading = false;
    boolean isRecording = false;
    AudioRecord audioRecorder = null;
    AudioTrack track = null;
    String filename = "";
    short[][]  buffers  = new short[256][160];
    int ix = 0;
    File myfile;
    MediaPlayer mediaPlayer;


    private final static int[] sampleRates = {44100, 22050, 16000, 11025, 8000};
    protected int usedSampleRate;

    public RecorderActivity() throws FileNotFoundException {
    }

    private class AudioThread extends Thread {
        int numberOfReadBytes   = 0;
        byte audioBuffer[]      = new  byte[myBufferSize];
        boolean recording       = false;
        float tempFloatBuffer[] = new float[3];
        int tempIndex           = 0;
        int totalReadBytes      = 0;
        byte totalByteBuffer[]  = new byte[60 * 44100 * 2];


        private final static String TAG = "AudioThread";

        /**
         * Give the thread high priority so that it's not canceled unexpectedly, and start it
         */

        @Override
        public void run() {
            Log.i(TAG, "Running Audio Thread");
            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }

                float totalAbsValue = 0.0f;
                short sample        = 0;

                numberOfReadBytes = audioRecorder.read( audioBuffer, 0, myBufferSize );

                // Analyze Sound.
                for( int i=0; i<myBufferSize; i+=2 )
                {
                    sample = (short)( (audioBuffer[i]) | audioBuffer[i + 1] << 8 );
                    if(numberOfReadBytes != 0) {
                        totalAbsValue += Math.abs(sample) / (numberOfReadBytes / 2);
                    }
                }

                // Analyze temp buffer.
                tempFloatBuffer[tempIndex%3] = totalAbsValue;
                float temp                   = 0.0f;
                for( int i=0; i<3; ++i )
                    temp += tempFloatBuffer[i];

                if( (temp >=0 && temp <= 350) && recording == false )
                {
                    Log.i("TAG", "1");
                    tempIndex++;
                }

                if( temp > 350 && recording == false )
                {
                    Log.i("TAG", "2");
                    recording = true;
                }

                if( (temp >= 0 && temp <= 350) && recording == true )
                {
                    Log.i("TAG", "Save audio to file.");

                    // Save audio to file.
                    String filepath = Environment.getExternalStorageDirectory().getPath();
                    File file = new File(filepath,"AudioRecorder");
                    if( !file.exists() )
                        file.mkdirs();

                    String fn = file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".wav";
                    filename = fn;

                    long totalAudioLen  = 0;
                    long totalDataLen   = totalAudioLen + 36;
                    long longSampleRate = myRate;
                    int channels        = 1;
                    long byteRate       = myBTT * myRate * channels/8;
                    totalAudioLen       = totalReadBytes;
                    totalDataLen        = totalAudioLen + 36;
                    byte finalBuffer[]  = new byte[totalReadBytes + 44];

                    finalBuffer[0] = 'R';  // RIFF/WAVE header
                    finalBuffer[1] = 'I';
                    finalBuffer[2] = 'F';
                    finalBuffer[3] = 'F';
                    finalBuffer[4] = (byte) (totalDataLen & 0xff);
                    finalBuffer[5] = (byte) ((totalDataLen >> 8) & 0xff);
                    finalBuffer[6] = (byte) ((totalDataLen >> 16) & 0xff);
                    finalBuffer[7] = (byte) ((totalDataLen >> 24) & 0xff);
                    finalBuffer[8] = 'W';
                    finalBuffer[9] = 'A';
                    finalBuffer[10] = 'V';
                    finalBuffer[11] = 'E';
                    finalBuffer[12] = 'f';  // 'fmt ' chunk
                    finalBuffer[13] = 'm';
                    finalBuffer[14] = 't';
                    finalBuffer[15] = ' ';
                    finalBuffer[16] = 16;  // 4 bytes: size of 'fmt ' chunk
                    finalBuffer[17] = 0;
                    finalBuffer[18] = 0;
                    finalBuffer[19] = 0;
                    finalBuffer[20] = 1;  // format = 1
                    finalBuffer[21] = 0;
                    finalBuffer[22] = (byte) channels;
                    finalBuffer[23] = 0;
                    finalBuffer[24] = (byte) (longSampleRate & 0xff);
                    finalBuffer[25] = (byte) ((longSampleRate >> 8) & 0xff);
                    finalBuffer[26] = (byte) ((longSampleRate >> 16) & 0xff);
                    finalBuffer[27] = (byte) ((longSampleRate >> 24) & 0xff);
                    finalBuffer[28] = (byte) (byteRate & 0xff);
                    finalBuffer[29] = (byte) ((byteRate >> 8) & 0xff);
                    finalBuffer[30] = (byte) ((byteRate >> 16) & 0xff);
                    finalBuffer[31] = (byte) ((byteRate >> 24) & 0xff);
                    finalBuffer[32] = (byte) (2 * 16 / 8);  // block align
                    finalBuffer[33] = 0;
                    finalBuffer[34] = 16;  // bits per sample
                    finalBuffer[35] = 0;
                    finalBuffer[36] = 'd';
                    finalBuffer[37] = 'a';
                    finalBuffer[38] = 't';
                    finalBuffer[39] = 'a';
                    finalBuffer[40] = (byte) (totalAudioLen & 0xff);
                    finalBuffer[41] = (byte) ((totalAudioLen >> 8) & 0xff);
                    finalBuffer[42] = (byte) ((totalAudioLen >> 16) & 0xff);
                    finalBuffer[43] = (byte) ((totalAudioLen >> 24) & 0xff);

                    for( int i=0; i<totalReadBytes; ++i )
                        finalBuffer[44+i] = totalByteBuffer[i];

                    FileOutputStream out;
                    try {
                        out = new FileOutputStream(fn);
                        try {
                            out.write(finalBuffer);
                            out.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } catch (FileNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    tempIndex++;
                }

                // -> Recording sound here.
                Log.i( "TAG", "Recording Sound." );
                for( int i=0; i<numberOfReadBytes; i++ )
                    totalByteBuffer[totalReadBytes + i] = audioBuffer[i];
                totalReadBytes += numberOfReadBytes;

                tempIndex++;
                // audioRecorder became null since it was canceled
            Looper.loop();
        }
    }


//    FileOutputStream fOut = openFileOutput("samplefile", MODE_PRIVATE);
//    OutputStreamWriter osw = new OutputStreamWriter(fOut);


    private AudioThread audioThread;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder_page);

        requestRecordAudioPermission();
        //if(!targetFile.exists()) {
//            try {
//                audioThread = new AudioThread();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        //}


//        createAudioRecorder();

//        Log.d(TAG, "init state = " + audioRecord.getState());
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Activity", "Granted!");
                } else {
                    Log.d("Activity", "Denied!");
                    finish();
                }
                return;
            }
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
//            audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, channelConfig, audioFormat, bufferSize);
//
//            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED){
//
//                Log.d(TAG, "--------------!");
//
//            }
//            else {
//                Log.e(TAG, "Initialisation failed !");
//            }

        } catch (Exception e) {
            Log.i("AudioRecording", "Error in Audio Record");
        }
    }

    public void recordStart(View v) {
        Log.d(TAG, "record start");
        isRecording = true;
        audioRecorder = findAudioRecord();
        audioThread = new AudioThread();
        audioThread.run();

        // While data come from microphone.

        //audioThread.run();

//        audioRecord = findAudioRecord();
//        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED){
//            audioRecord.startRecording();
//            isRecording = true;
////            try
////            {
////                int N = myBufferSize;
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                    track = new AudioTrack(
////                            new AudioAttributes.Builder()
////                                    .setUsage(AudioAttributes.USAGE_MEDIA)
////                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
////                                    .build(),
////                            new AudioFormat.Builder()
////                                    .setSampleRate(myRate)
////                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
////                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build(),
////                            myBufferSize,
////                            AudioTrack.MODE_STREAM,
////                            AudioManager.AUDIO_SESSION_ID_GENERATE);
////                } else {
////                    //support for Android KitKat
////                    track = new AudioTrack(AudioManager.STREAM_MUSIC,
////                            myRate,
////                            AudioFormat.CHANNEL_OUT_MONO,
////                            AudioFormat.ENCODING_PCM_16BIT,
////                            myBufferSize,
////                            AudioTrack.MODE_STREAM);
////                }
////
////                track.play();
////                /*
////                 * Loops until something outside of this thread stops it.
////                 * Reads the data from the recorder and writes it to the audio track for playback.
////                 */
////                    Log.i("Map", "Writing new data to buffer");
////                    short[] buffer = buffers[ix++ % buffers.length];
////                    N = audioRecord.read(buffer,0,buffer.length);
////                    track.write(buffer, 0, buffer.length);
////            }
////            catch(Throwable x)
////            {
////                Log.w("Audio", "Error reading voice audio", x);
////            }
////            /*
////             * Frees the thread's resources after the loop completes so that it can be run again
////             */
////            finally
////            {
////                audioRecord.stop();
////                audioRecord.release();
////                track.stop();
////                track.release();
////            }
//        }
//
//        else{
//            Log.e("SoundMeter", "ERROR, could not create audio recorder");
//        }

        //       int recordingState = audioRecord.getRecordingState();
        //       Log.d(TAG, "recordingState = " + recordingState);
    }


    public void recordStop(View v) {
        Log.d(TAG, "record stop");
        isRecording = false;
        if(audioThread.isAlive()) audioThread.interrupt();
//        audioRecord.stop();
//        audioRecord.release();
//        isRecording = false;
//        track.play();
////        copyWaveFile(getTempFilename(),getFilename());
////        deleteTempFile();
    }
//
//    private void deleteTempFile() {
//        File file = new File(getTempFilename());
//        file.delete();
//    }


    public void readStart(View v) {
        Log.d(TAG, "read start");
        isReading = true;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            track = new AudioTrack(
//                    new AudioAttributes.Builder()
//                            .setUsage(AudioAttributes.USAGE_MEDIA)
//                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                            .build(),
//                    new AudioFormat.Builder()
//                            .setSampleRate(myRate)
//                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
//                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build(),
//                    myBufferSize,
//                    AudioTrack.MODE_STREAM,
//                    AudioManager.AUDIO_SESSION_ID_GENERATE);
//        } else {
//
//            track = new AudioTrack(AudioManager.STREAM_MUSIC,
//                    myRate,
//                    AudioFormat.CHANNEL_IN_STEREO,
//                    AudioFormat.ENCODING_PCM_16BIT,
//                    myBufferSize,
//                    AudioTrack.MODE_STREAM);
//        }
        mediaPlayer = new MediaPlayer();
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
        }

        try {
            mediaPlayer.reset();
            AssetFileDescriptor afd;
            afd = getAssets().openFd(filename);
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (audioRecord == null)
//                    return;
//
//                byte[] myBuffer = new byte[myBufferSize];
//                int readCount = 0;
//                int totalCount = 0;
//                while (isReading) {
//                    readCount = audioRecord.read(myBuffer, 0, myBufferSize);
//                    totalCount += readCount;
//                    Log.d(TAG, "readCount = " + readCount + ", totalCount = "
//                            + totalCount);
//                    track.write(myBuffer, 0, myBuffer.length);
//                }
//            }
//        }).start();


    }


    public void readStop(View v) {
        Log.d(TAG, "read stop");
//        track.play();
//        isReading = false;
////        Wave wave = new Wave(myRate,NUM_CHANNELS,outputSignal,0,outputSignal.length-1);
////        if(wave.wroteToFile("your_filename.wav"))
////            log("Click write successful.");
////        else log("Click write failed.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        isReading = false;
//        if (audioRecord != null) {
//            audioRecord.release();
//        }
    }
}

