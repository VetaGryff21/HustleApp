package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
    }

    @Override
    public void onBackPressed() {
        backActivity(ProfileActivity.this, MainActivity.class);
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