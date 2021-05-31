package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_page);

        Button searchButton = (Button) findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MenuActivity.this, SearchDancerActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) { }
            }
        });

        Button metronomeButton = (Button) findViewById(R.id.btn_metronome);
        metronomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MenuActivity.this, MetronomeActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) { }
            }
        });

        Button playerButton = (Button) findViewById(R.id.btn_player);
        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MenuActivity.this, RecorderActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) { }
            }
        });
    }
}