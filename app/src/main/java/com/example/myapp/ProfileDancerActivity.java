package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.model.Dancer;
import com.google.gson.Gson;

public class ProfileDancerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dancer dancer = new Gson().fromJson(getIntent().getStringExtra("DancerUnit"), Dancer.class);
        setContentView(R.layout.profile_page);

        final TextView dancerName = findViewById(R.id.dancer_name);
        final TextView dancerClub = findViewById(R.id.dancer_club);
        final TextView dancerCode = findViewById(R.id.dancer_code);
        final TextView dancerClassE = findViewById(R.id.dancer_classic_e);
        final TextView dancerClassD = findViewById(R.id.dancer_classic_d);
        final TextView dancerClassC = findViewById(R.id.dancer_classic_c);
        final TextView dancerClassB = findViewById(R.id.dancer_classic_b);
        final TextView dancerClassA = findViewById(R.id.dancer_classic_a);
        final TextView dancerDndBg = findViewById(R.id.dancer_dnd_bg);
        final TextView dancerDndRs = findViewById(R.id.dancer_dnd_rs);
        final TextView dancerDndM = findViewById(R.id.dancer_dnd_m);
        final TextView dancerDndS = findViewById(R.id.dancer_dnd_s);
        final TextView dancerDndCh = findViewById(R.id.dancer_dnd_ch);

        dancerName.setText("" + dancer.getFullname());
        dancerClub.setText("Клуб: " + dancer.getClub());
        dancerCode.setText("Код: " + dancer.getCode().toString());
        dancerClassE.setText("E: " + dancer.getE().toString());
        dancerClassD.setText("D: " + dancer.getD().toString());
        dancerClassC.setText("C: " + dancer.getC().toString());
        dancerClassB.setText("B: " + dancer.getB().toString());
        dancerClassA.setText("A: " + dancer.getA().toString());
        dancerDndBg.setText("Bg: " + dancer.getBg().toString());
        dancerDndRs.setText("Rs: " + dancer.getRs().toString());
        dancerDndM.setText("M: " + dancer.getM().toString());
        dancerDndS.setText("S: " + dancer.getS().toString());
        dancerDndCh.setText("Ch: " + dancer.getCh().toString());
    }


    @Override
    public void onBackPressed() {
        backActivity(ProfileDancerActivity.this, MainActivity.class);
    }

    private void backActivity(Object fromActivity, Object toActivity) {
        try {
            Intent intent = new Intent((Context) fromActivity, (Class<?>) toActivity);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            System.out.println("Какая-то ошибочка");
        }
    }
}