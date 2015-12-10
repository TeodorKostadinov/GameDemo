package com.example.fos.gamedemo.cmn;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by fos on 8.12.2015 г..
 */
public class Question {

    public Question(String[] answers, int correct, double[] location, String text) {
        this.answers = answers;
        this.correct = correct;
        this.location = location;
        this.text = text;
    }

    private String[] answers;
    private int correct;
    private double[] location;
    private String text;

    public String[] getAnswers() {
        return answers;
    }

    public int getCorrect() {
        return correct;
    }

    public LatLng getLocation() {
        return new LatLng(location[0], location[1]);
    }

    public String getText() {
        return text;
    }
}
