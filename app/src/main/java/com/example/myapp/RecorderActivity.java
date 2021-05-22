package com.example.myapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.example.myapp.Constants.RECORD_ENCODING_BITRATE_48000;
import static com.example.myapp.Constants.RECORD_SAMPLE_RATE_44100;

public class RecorderActivity extends AppCompatActivity {

    boolean isRecording = false;
    private String recordPathWav;
    static String LOG_TAG = "HustleApp logs";
    private int sampleRate;
    private int channelCount;
    File recordFile;
    int channel;
    int bufferSize;
    AudioRecord recorder;
    Thread recordingThread;
    Handler h;

    private String txtPath;

    private static final int RECORDER_BPP = 16;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder_page);

        requestRecordAudioPermission();
        h = new Handler(Looper.getMainLooper());

        recordPathWav = this.getFilesDir().getAbsolutePath() + "/micrec1.wav";
        View startPlay = findViewById(R.id.start_play);
        View stopPlay = findViewById(R.id.stop_play);
        View startRecord = findViewById(R.id.start_record);
        View stopRecord = findViewById(R.id.stop_record);
        stopRecord.setEnabled(false);

        startRecord.setOnClickListener(v -> {
            try {
                startRecording(recordPathWav, RECORD_ENCODING_BITRATE_48000);
                startRecord.setEnabled(false);
                stopRecord.setEnabled(true);

                Runnable r = () -> {
                    try {
                        startRecord.setEnabled(true);
                        stopRecord.setEnabled(false);
                        stopRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
                h.postDelayed(r, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stopRecord.setOnClickListener(v -> {
            try {
                startRecord.setEnabled(true);
                stopRecord.setEnabled(false);
                stopRecording();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void onPause() {
        h.removeCallbacksAndMessages(null);
        super.onPause();
    }

    public void startRecording(String outputFile, int bitrate) throws IOException {
        sampleRate = RECORD_SAMPLE_RATE_44100;
        channelCount = 1;
        recordFile = new File(outputFile);
        if (recordFile.createNewFile()) {
            Log.d(LOG_TAG, "File created: " + recordFile.getName());
        } else {
            Log.d(LOG_TAG, "File already exists.");
        }

        if (recordFile.exists() && recordFile.isFile()) {
            channel = channelCount == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
            try {
                bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                        channel,
                        AudioFormat.ENCODING_PCM_16BIT);
                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                            channel,
                            AudioFormat.ENCODING_PCM_16BIT);
                }
                recorder = new AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        sampleRate,
                        channel,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize
                );
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, "sampleRate = " + sampleRate + " channel = " + channel + " bufferSize = " + bufferSize);
                if (recorder != null) {
                    recorder.release();
                }
            }
            if (recorder != null && recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                recorder.startRecording();
                isRecording = true;

                recordingThread = new Thread(() -> {
                    try {
                        writeAudioDataToWavFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(LOG_TAG, "Thread Running");
                });
                recordingThread.start();
            } else {
                Log.e(LOG_TAG, "prepare() failed");
            }
        } else {
            Log.e(LOG_TAG, "file failed");
        }
    }

    private void playRec() throws IOException {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(recordPathWav);
        mediaPlayer.prepare();
        mediaPlayer.start();
        write_audio_file_to_txt();
    }

    public void write_audio_file_to_txt() {
        try {
            File f = new File(recordPathWav);
            InputStream in = new BufferedInputStream(new FileInputStream(f));
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int read;
            byte[] buff = new byte[1024];
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
            out.flush();

            byte[] audioBytes = out.toByteArray();
            txtPath = this.getFilesDir().getAbsolutePath() + "/debugdata.txt";
            File txtFile = new File(txtPath);
            if (txtFile.createNewFile()) {
                Log.i(LOG_TAG, "File created: " + txtFile.getName());
            } else {
                Log.i(LOG_TAG, "File already exists.");
            }

            FileWriter myWriter = new FileWriter(txtPath);
            for (byte audioByte : audioBytes) {
                myWriter.write(audioByte + " ");
            }
            myWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Log.i(LOG_TAG, "The audio file is ");
    }

    private void writeAudioDataToWavFile() throws IOException {
        byte[] data = new byte[bufferSize];
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(recordFile);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.toString());
            fos = null;
        }
        if (null != fos) {
            int chunksCount = 0;
            ByteBuffer shortBuffer = ByteBuffer.allocate(2);
            shortBuffer.order(ByteOrder.LITTLE_ENDIAN);
            while (isRecording) {
                chunksCount += recorder.read(data, 0, bufferSize);
                if (AudioRecord.ERROR_INVALID_OPERATION != chunksCount) {
                    long sum = 0;
                    for (int i = 0; i < bufferSize; i += 2) {
                        shortBuffer.put(data[i]);
                        shortBuffer.put(data[i + 1]);
                        sum += Math.abs(shortBuffer.getShort(0));
                        shortBuffer.clear();
                    }
                    try {
                        fos.write(data);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, e.toString());
                        stopRecording();
                    }
                }
            }
        }

        try {
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
        }
        setWaveFileHeader(recordFile, channelCount);
    }

    private void setWaveFileHeader(File file, int channels) {
        long fileSize = file.length() - 8;
        long totalSize = fileSize + 36;
        long byteRate = sampleRate * channels * (RECORDER_BPP/8); //2 byte per 1 sample for 1 channel.

        try {
            final RandomAccessFile wavFile = randomAccessFile(file);
            wavFile.seek(0); // to the beginning
            wavFile.write(generateHeader(fileSize, totalSize, sampleRate, channels, byteRate));
            wavFile.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }


    public void stopRecording() throws IOException {
        if (recorder != null) {
            isRecording = false;
            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                try {
                    recorder.stop();
                } catch (IllegalStateException e) {
                    Log.e(LOG_TAG, "stopRecording() problems");
                }
            }
            recorder.release();
            recordingThread.interrupt();
        }
        playRec();
    }


    private RandomAccessFile randomAccessFile(File file) {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return randomAccessFile;
    }

    private byte[] generateHeader(
            long totalAudioLen, long totalDataLen, long longSampleRate, int channels,
            long byteRate) {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; //16 for PCM. 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * (RECORDER_BPP/8)); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        return header;
    }

    private void requestRecordAudioPermission() {
        //check API version, do nothing if API version < 23
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String @NotNull [] permissions, int @NotNull [] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Activity", "Granted!");
            } else {
                Log.d("Activity", "Denied!");
                finish();
            }
        }
    }

    public void readStart(View v) {
    }

    public void readStop(View v) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacksAndMessages(null);
    }
}

