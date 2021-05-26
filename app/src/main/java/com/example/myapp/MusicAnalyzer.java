package com.example.myapp;

import android.util.Log;

public class MusicAnalyzer {
    private String LOG_TAG = "Analyzer logs";


    public MusicAnalyzer(){ }

    public int Algo1(int[] musicArray) {
        int size = musicArray.length;
        int fragmentsize = 500;
        int counter = 0;
        int jump = 1000;

        int max, min;
        int size2 = size - fragmentsize;

        for (int i = 0; i < size2; i = i + fragmentsize) {
            max = 0;
            min = 500000;
            for (int j = i; j < i + fragmentsize; j++) {
                if(Math.abs(musicArray[j]) > max)
                    max = Math.abs(musicArray[j]);
                if(Math.abs(musicArray[j]) < min)
                    min = Math.abs(musicArray[j]);
            }
            if(max - min > jump)
                counter++;
            Log.d(LOG_TAG, "---- " + min + "---- " + max);
        }
        return counter;
    }

    public int Algo2(int[] musicArray) {
        int size = musicArray.length;
        int fragmentsize = 1000;
        int counter = 0;
        int persent = 50;
        int peak;
        int max;
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

    public int Algo3(int[] musicArray) {
        int size = musicArray.length;
        int lenNewMusicArray = size / 2;
        int[] newMusicArray = new int[lenNewMusicArray];
        for (int i = 0, j = 0; i < size-1; i = i+2, j++) {
            newMusicArray[j] = (musicArray[i]+musicArray[i+1])/2;
        }
        int fragmentsize = 1000;
        int counter = 0;
        int persent = 30;
        int peak;
        int max;
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

    public int Algo4(int[] musicArray) {
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
