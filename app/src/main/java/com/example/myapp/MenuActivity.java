package com.example.myapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
                final String[] options = {
                        "по имени", "по коду", "по названию клуба"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Поиск танцора:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                public void onClick(DialogInterface dialog, int which) {
                    if ("по имени".equals(options[which])) {
                        try {
                            Intent intent = new Intent(MenuActivity.this, SearchDancerActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) { }
                    } else if ("по коду".equals(options[which])) {
                        try {
                            Intent intent = new Intent(MenuActivity.this, SearchByCodeActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) { }
                    } else if ("по названию клуба".equals(options[which])) {
                        try {
                            Intent intent = new Intent(MenuActivity.this, SearchByClubActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) { }
                    }
                    // the user clicked on colors[which]
                }
                });
                builder.show();
//                try {
//                    Intent intent = new Intent(MenuActivity.this, SearchDancerActivity.class);
//                    startActivity(intent);
//                    finish();
//                } catch (Exception e) { }
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

        Button bpmdetectButton = (Button) findViewById(R.id.btn_bpmdetect);
        bpmdetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MenuActivity.this, RecorderActivity.class);
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
                    Intent intent = new Intent(MenuActivity.this, PlayerActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) { }
            }
        });
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            System.out.println("Какая-то ошибочка");
        }
    }
}