package com.example.myapp;

import android.Manifest;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jtransforms.fft.FloatFFT_1D;

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

import static com.example.myapp.Constants.RECORD_SAMPLE_RATE_44100;
import static com.example.myapp.SampleHistory.FFT_BASS;
import static com.example.myapp.SampleHistory.FFT_MID;
import static com.example.myapp.SampleHistory.FFT_TREBLE;

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
    TextView countClap1;
    TextView countClap2;
    TextView countClap3;
    TextView countClap4;
    MusicAnalyzer analyzer;
    int fileCounter;
    int recordTime = 6;
    private SampleHistory sh;
    private float[] shSamples;
    FloatFFT_1D fft = new FloatFFT_1D(1024);
    private static BPMBestGuess bpm;
    public double dbpm = 0;

    private String txtPath;

    private static final int RECORDER_BPP = 16;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder_page);

        requestRecordAudioPermission();
        h = new Handler(Looper.getMainLooper());
        fileCounter = 0;
        bpm = new BPMBestGuess();

        recordPathWav = this.getFilesDir().getAbsolutePath() + "/micrec1.wav";
        View startRecord = findViewById(R.id.start_record);
        countClap1 = findViewById(R.id.textinput_1);
        countClap2 = findViewById(R.id.textinput_2);
        countClap3 = findViewById(R.id.textinput_3);
        countClap4 = findViewById(R.id.textinput_4);
        analyzer = new MusicAnalyzer();
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        int storysize = RECORD_SAMPLE_RATE_44100 * recordTime;
        shSamples = new float[storysize];
        BPMDetect.setSampleSource(sh);

        startRecord.setOnClickListener(v -> {
            try {
                startRecording(recordPathWav);
                startRecord.setEnabled(false);
                startRecord.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                countClap1.setText("Идет запись");
                countClap2.setText("");
                countClap3.setText("");
                countClap4.setText("");
                Runnable r = () -> {
                    try {
                        startRecord.setEnabled(true);
                        startRecord.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        stopRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
                h.postDelayed(r, recordTime*1000);
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

    int ind = 1;
    public void startRecording(String outputFile) throws IOException {
        countClap1.setText("");
        sampleRate = RECORD_SAMPLE_RATE_44100;
        channelCount = 1;
        ind = 1;
        recordFile = new File(outputFile);
        sh = new SampleHistory(RECORD_SAMPLE_RATE_44100, recordTime);
        if (recordFile.createNewFile()) {
            Log.d(LOG_TAG, "File created: " + recordFile.getName());
        } else {
            Log.d(LOG_TAG, "File already exists.");
        }

        if (recordFile.exists() && recordFile.isFile()) {
            channel = channelCount == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;
            try {
                bufferSize = 128;
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
                        ind = 1;
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
            txtPath = this.getFilesDir().getAbsolutePath() + "/debugdata";
//---------- to array without header and nulls
            float[] newBassArray = cutNulls(sh.getFftSamples(FFT_BASS));
            float[] newMidArray = cutNulls(sh.getFftSamples(FFT_MID));
            float[] newTrebleArray = cutNulls(sh.getFftSamples(FFT_TREBLE));
            float[] newFullArray = cutNulls(shSamples);

//---------- print float array
            //printArrayToFloatFile(newFullArray, "all");
            printArrayToFloatFile(newMidArray, "mid");
            printArrayToFloatFile(newBassArray, "bass");
            printArrayToFloatFile(newTrebleArray, "treble");

            SampleHistory sh1 = new SampleHistory(sampleRate, 5);


            dbpm = bpm.getBPM();
            dbpm = Math.round(dbpm);

            int[] bpms = new int[3];
            bpms[0] = (int) Math.round(BPMDetect.detectBPM(BPMDetect.filteredIntegral(newBassArray),
                        sampleRate, 0));
            bpms[1] = (int) Math.round(BPMDetect.detectBPM(BPMDetect.filteredIntegral(newMidArray),
                        sampleRate, 1));
            bpms[2] = (int) Math.round(BPMDetect.detectBPM(BPMDetect.filteredIntegral(newTrebleArray),
                        sampleRate, 2));

            //countClap2.setText("Алгоритм 2: " +  BPMDetect.confidence[0] + " " +  BPMDetect.confidence[1] + " " +  BPMDetect.confidence[2] +" " + bpms[getBestGuess(BPMDetect.confidence)] + " ");
            //downbeat = BPMDetect.getDownbeat(sh.getFftSamples(SampleHistory.FFT_BASS), sampleRate);

//---------- analyzer
            Log.i(LOG_TAG, "=======algo1==============");
            countClap1.setText("Bass: " + bpms[0] + " " + Math.round(BPMDetect.confidence[0]));
            countClap2.setText("Mid: " + bpms[1] + " " + Math.round(BPMDetect.confidence[1]));
            countClap3.setText("Treble: " + bpms[2] + " " + Math.round(BPMDetect.confidence[2]));
            countClap4.setText("Result: " + Math.round(bpms[getBestGuess(BPMDetect.confidence)]));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Log.i(LOG_TAG, "The audio file is ");
    }

    private void printArrayToIntFile(int[] array) throws IOException {
        FileWriter myWriter = new FileWriter(txtPath + "_int_" + fileCounter);
        for (int audioInt : array) {
            myWriter.write(audioInt + " ");
        }
        myWriter.close();
        fileCounter++;
    }

    private void printArrayToByteFile(byte[] array) throws IOException {
        FileWriter myWriter = new FileWriter(txtPath + "_byte");
        for (byte audioInt : array) {
            myWriter.write(audioInt + " ");
        }
        myWriter.close();
    }

    private void printArrayToFloatFile(float[] array, String name) throws IOException {
        FileWriter myWriter = new FileWriter(txtPath + "_" + name);
        for (float audioInt : array) {
            myWriter.write(audioInt + " ");
        }
        myWriter.close();
        fileCounter++;
    }

    private float[] cutNulls(float[] array) {
        int prefix = 0;
        int postfix = 0;

        for (int i = 0; i < array.length; i++) {
            if (prefix == 0) {
                if (array[i] != 0) {
                    prefix = i;
                    break;
                }
            }
        }
        for (int i = array.length - 1; i > 0; i--) {
             if (postfix == 0) {
                if (array[i] != 0) {
                    postfix = i;
                    break;
                }
            }
        }
        int newsize = postfix - prefix;
        float[] newArray = new float[newsize];
        System.arraycopy(array, prefix, newArray,0, newsize);
        return newArray;
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

        float sample = 0;
        float[] samples = sh.getSamples();
        int mSize = bufferSize/2;
        float[] samplesmini = new float[mSize];

        if (null != fos) {
            int chunksCount = 0;
            while (isRecording) {
                chunksCount += recorder.read(data, 0, bufferSize);
                if (AudioRecord.ERROR_INVALID_OPERATION != chunksCount) {
                    for (int i = 0; i < (data.length / 2); i++) {
                        sample = data[i * 2 + 1] << 8 | (255 & data[i * 2]);
                        samplesmini[i] = sample;
                    }
                    fos.write(data);
                    for (int i = 0; i < (1024 - mSize); i++)
                        samples[i] = samples[i + mSize];
                    for (int i = 0; i < mSize; i++)
                        samples[i + (1024 - mSize)] = samplesmini[i];

                    new Thread(new FFTer(fft, samples, sh)).start();
                    int newind = (mSize * ind - mSize);
                    for (int i = newind, j = 0; i < mSize; i++, j++) {
                        shSamples[i] = samplesmini[j];
                    }
                    ind++;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity() {
        try {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            System.out.println("Какая-то ошибочка");
        }
    }

    public static BPMBestGuess getBPM()
    {
        return bpm;
    }

    public static int getBestGuess(double[] conf)
    {
        if (conf[0] >= conf[1] && conf[0] >= conf[2])
            return 0;
        if (conf[1] >= conf[0] && conf[1] >= conf[2])
            return 1;
        if (conf[2] >= conf[0] && conf[2] >= conf[1])
            return 2;
        return -1;
    }
}

