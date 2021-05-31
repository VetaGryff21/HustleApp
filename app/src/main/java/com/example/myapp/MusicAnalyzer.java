package com.example.myapp;

import android.util.Log;

public class MusicAnalyzer {
    private String LOG_TAG = "Analyzer logs";


    public MusicAnalyzer(){ }

    public int Algo1(float[] musicArray) {
        int size = musicArray.length;
        int fragmentsize = 20;
        int counter = 0;
        int persent = 50;
        float peak;
        float max;
        int flag;
        int size2 = size - fragmentsize;

        for (int i = 0; i < size2; i = i + fragmentsize) {
            max = -1;
            for (int j = i; j < i + fragmentsize; j++) {
                if(musicArray[j] > max)
                    max = musicArray[j];
            }
            peak = max / 100 * persent;
            flag = 0;
            for (int j = i; j < i + fragmentsize; j++) {
                if(musicArray[j] > peak) {
                    flag++;
                }
            }
            Log.d(LOG_TAG, "flag == "+ flag);
            if(flag < 5)
                counter++;
        }
        return counter;
    }

    public int Algo2(float[] musicArray) {
        int size = musicArray.length;
        int fragmentsize = 30;
        int counter = 0;
        int persent = 50;
        float peak;
        float max;
        int flag;
        int size2 = size - fragmentsize;

        for (int i = 0; i < size2; i = i + fragmentsize) {
            max = -1;
            for (int j = i; j < i + fragmentsize; j++) {
                if(musicArray[j] > max)
                    max = musicArray[j];
            }
            peak = max / 100 * persent;
            flag = 0;
            for (int j = i; j < i + fragmentsize; j++) {
                if(musicArray[j] > peak) {
                    flag++;
                }
            }
            Log.d(LOG_TAG, "flag == "+ flag);
            if(flag < 5)
                counter++;
        }
        return counter;
    }

    public int Algo3(float[] musicArray) {
        int size = musicArray.length;
        int fragmentsize = 38;
        int counter = 0;
        int persent = 50;
        float peak;
        float max;
        int flag;
        int size2 = size - fragmentsize;

        for (int i = 0; i < size2; i = i + fragmentsize) {
            max = -1;
            for (int j = i; j < i + fragmentsize; j++) {
                if(musicArray[j] > max)
                    max = musicArray[j];
            }
            peak = max / 100 * persent;
            flag = 0;
            for (int j = i; j < i + fragmentsize; j++) {
                if(musicArray[j] > peak) {
                    flag++;
                }
            }
            Log.d(LOG_TAG, "flag == "+ flag);
            if(flag < 7)
                counter++;
        }
        return counter;
    }

    public int Algo4(float[] musicArray) {
        int size = musicArray.length;
        int fragmentsize = 45;
        int counter = 0;
        int persent = 50;
        float peak;
        float max;
        int flag;
        int size2 = size - fragmentsize;

        for (int i = 0; i < size2; i = i + fragmentsize) {
            max = -1;
            for (int j = i; j < i + fragmentsize; j++) {
                if(musicArray[j] > max)
                    max = musicArray[j];
            }
            peak = max / 100 * persent;
            flag = 0;
            for (int j = i; j < i + fragmentsize; j++) {
                if(musicArray[j] > peak) {
                    flag++;
                }
            }
            Log.d(LOG_TAG, "flag == "+ flag);
            if(flag < 10)
                counter++;
        }
        return counter;
    }



    public int Algo21(float[] musicArray) {
        int size = musicArray.length;
        int fragmentsize = 20;
        int counter = 0;
        int persent = 50;
        float peak;
        float max;
        int flag;
        int size2 = size - fragmentsize;

        for (int i = 0; i < size2; i = i + fragmentsize) {
            max = 0;
            for (int j = i; j < i + fragmentsize; j++) {
                if(Math.abs(musicArray[j]) > max)
                    max = Math.abs(musicArray[j]);
            }
            peak = max / 100 * persent;
            flag = 0;
            for (int j = i; j < i + fragmentsize; j++) {
                if(musicArray[j] > peak) {
                    flag++;
                }
            }
            Log.d(LOG_TAG, "flag ========= "+ flag);
            if(flag < 70)
                counter++;
        }
        return counter;
    }

    public int Algo31(float[] musicArray) {
        int size = musicArray.length;
        int lenNewMusicArray = size / 2;
        float[] newMusicArray = new float[lenNewMusicArray];
        for (int i = 0, j = 0; i < size-1; i = i+2, j++) {
            newMusicArray[j] = (musicArray[i]+musicArray[i+1])/2;
        }
        int fragmentsize = 1;
        int counter = 0;
        int persent = 30;
        float peak;
        float max;
        int flag;
        int size2 = lenNewMusicArray - fragmentsize;

        for (int i = 0; i < size2; i = i + fragmentsize) {
            max = 0;
            for (int j = i; j < i + fragmentsize; j++) {
                if(Math.abs(newMusicArray[j]) > max)
                    max = Math.abs(newMusicArray[j]);
            }
            peak = max / 100 * persent;
            flag = 0;
            for (int j = i; j < i + fragmentsize; j++) {
                if(newMusicArray[j] > peak) {
                    flag++;
                }
            }
            Log.d(LOG_TAG, "flag -- "+ flag);
            if(flag < 25)
                counter++;
        }
        return counter;
    }

    public int Algo41(float[] musicArray) {
        int size = musicArray.length;
        int fragmentsize = 1000;
        int counter = 0;
        int persent = 60;
        int peak;
        int max;
        int size2 = size - fragmentsize;

        float[] invertArray = new float[size];
        for (int i = 0; i < size; i++) {
            invertArray[i] = (float) musicArray[i];
        }



        return counter;
    }


    public int Algo(int[] musicArray) {
        int size = musicArray.length;
        int fragmentsize = 1000;
        int counter = 0;
        int persent = 60;
        int peak;
        int max;
        int size2 = size - fragmentsize;

        for (int i = 0; i < size2; i = i + fragmentsize) {
            max = 0;
            for (int j = i; j < i + fragmentsize; j++) {
                if (Math.abs(musicArray[j]) > max)
                    max = Math.abs(musicArray[j]);
            }
            peak = max / 100 * persent;
            for (int j = i; j < i + fragmentsize; j++) {
                if (musicArray[j] > peak) {
                    counter++;
                    break;
                }
            }
        }
        return counter;
    }
}
