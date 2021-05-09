package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.myapp.model.Dancer;
import com.example.myapp.webservices.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDancerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        Button searchDancer = (Button) findViewById(R.id.btn_searching);
        searchDancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final EditText searchInput = findViewById(R.id.search_input);
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

                    final String tmpCode = searchInput.getText().toString();
                    NetworkService.getInstance()
                            .getJSONApi()
                            .getDancerByCode(tmpCode)
                            .enqueue(new Callback<Dancer>() {
                                @Override
                                public void onResponse(@NonNull Call<Dancer> call, @NonNull Response<Dancer> response) {

                                    if (response.code() == 404)
                                    {
                                        FragmentManager manager = getSupportFragmentManager();
                                        MyDialogFragment myDialogFragment = new MyDialogFragment();
                                        myDialogFragment.show(manager, "myDialog");
                                        return;
                                    }

                                    Dancer post = response.body();

                                    dancerName.setText(post.getFullname());
                                    dancerClub.setText(post.getClub());
                                    dancerCode.setText(post.getCode());
                                    dancerClassE.setText(post.getE());
                                    dancerClassD.setText(post.getD());
                                    dancerClassC.setText(post.getC());
                                    dancerClassB.setText(post.getB());
                                    dancerClassA.setText(post.getA());
                                    dancerDndBg.setText(post.getBg());
                                    dancerDndRs.setText(post.getRs());
                                    dancerDndM.setText(post.getM());
                                    dancerDndS.setText(post.getS());
                                    dancerDndCh.setText(post.getCh());
                                }

                                @Override
                                public void onFailure(Call<Dancer> call, Throwable t) {
                                    FragmentManager manager = getSupportFragmentManager();
                                    MyDialogFragment myDialogFragment = new MyDialogFragment();
                                    myDialogFragment.show(manager, "myDialog");
                                    return;
                                }
                            });
                    } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        backActivity(SearchDancerActivity.this, MainActivity.class);
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