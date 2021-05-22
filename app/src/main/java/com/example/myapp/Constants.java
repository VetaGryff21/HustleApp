package com.example.myapp;

public class Constants {

    private Constants() {
    }

    public final static long RECORD_IN_TRASH_MAX_DURATION = 5184000000L; // 1000 X 60 X 60 X 24 X 60 = 60 Days
    public final static long MIN_REMAIN_RECORDING_TIME = 10000; // 1000 X 10 = 10 Seconds
    public final static long DECODE_DURATION = 7200000; // 2 X 60 X 60 X 1000 = 2 Hours
    
    public static final int PLAYBACK_SAMPLE_RATE = 44100;
    public static final int RECORD_SAMPLE_RATE_44100 = 44100;
    public static final int RECORD_SAMPLE_RATE_8000 = 8000;
    public static final int RECORD_SAMPLE_RATE_16000 = 16000;
    public static final int RECORD_SAMPLE_RATE_22050 = 22050;
    public static final int RECORD_SAMPLE_RATE_32000 = 32000;
    public static final int RECORD_SAMPLE_RATE_48000 = 48000;

    public static final int RECORD_ENCODING_BITRATE_12000 = 12000; //Bitrate for 3gp format
    public static final int RECORD_ENCODING_BITRATE_24000 = 24000;
    public static final int RECORD_ENCODING_BITRATE_48000 = 48000;
    public static final int RECORD_ENCODING_BITRATE_96000 = 96000;
    public static final int RECORD_ENCODING_BITRATE_128000 = 128000;
    public static final int RECORD_ENCODING_BITRATE_192000 = 192000;
    public static final int RECORD_ENCODING_BITRATE_256000 = 256000;

    public final static int RECORD_AUDIO_MONO = 1;
    public final static int RECORD_AUDIO_STEREO = 2;
    public final static int RECORD_MAX_DURATION = 14400000; // 240 min 4 hours
    
    public final static int RECORD_BYTES_PER_SECOND = RECORD_ENCODING_BITRATE_48000 / 8; //bits per sec converted to bytes per sec.
}
