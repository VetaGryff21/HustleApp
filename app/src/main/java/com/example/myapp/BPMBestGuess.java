package com.example.myapp;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


public class BPMBestGuess {

    private HashMap<Double, Double> bpmEntries = new HashMap<Double, Double>();

    private static double DECAY_RATE = 0.999;
    private static double DELETE_THRESHHOLD = 0.01;
    private double confidence = 0;

    public BPMBestGuess() {
    }


    public void appendBPMGuess(double bpm, double confidence) {
        Iterator<Entry<Double, Double>> it = bpmEntries.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Double, Double> e = it.next();
            e.setValue(e.getValue() * DECAY_RATE);
            if (e.getValue() < DELETE_THRESHHOLD)
                it.remove();
        }
        if (BPMDetect.isBreakdown()) {
            Log.d("", "isBreakdown");
            return;
        }
        Log.d("", "not isBreakdown");
        if (bpmEntries.containsKey(bpm))
            bpmEntries.put(bpm, bpmEntries.get(bpm) + confidence);
        else
            bpmEntries.put(bpm, confidence);
    }

    private double calculateGuess() {
        double bestGuessStart = 0;
        double bestGuessValue = 0;
        double currentGuessStart = 0;
        double currentGuessValue = 0;
        boolean rising = false;
        for (Entry<Double, Double> e : bpmEntries.entrySet()) {
//        	System.out.println(e.getKey()+"\t"+e.getValue());
            if (e.getValue() > bestGuessValue) {
                bestGuessStart = e.getKey();
                bestGuessValue = e.getValue();
            }
        }
//        System.out.println("---------------------");

        confidence = bestGuessValue;
        return bestGuessStart;
    }

    public double getConfidence() {
        return this.confidence;
    }

    public double getBPM() {
        return calculateGuess();
    }
}
