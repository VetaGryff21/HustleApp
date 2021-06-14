package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.myapp.model.Dancer;
import com.example.myapp.webservices.NetworkService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchByCodeActivity extends AppCompatActivity {

    private ArrayList<DancerUnit> dancerList;
    private ListView dancerView;
    private EditText inputDancer;
    private Button searchDancer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchbycode_page);
        inputDancer = findViewById(R.id.search_input);
        dancerView = (ListView) findViewById(R.id.dancer_list);
        searchDancer = (Button) findViewById(R.id.btn_searching);
        searchDancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tmpCode = inputDancer.getText().toString();
                if(tmpCode.length() != 0) {
                    searchDancer.setClickable(false);
                    NetworkService.getInstance()
                            .getJSONApi()
                            .getDancerByCode(tmpCode)
                            .enqueue(new Callback<List<Dancer>>() {
                                @Override
                                public void onResponse(@NonNull Call<List<Dancer>> call, @NonNull Response<List<Dancer>> response) {

                                    if (response.code() == 404) {
                                        FragmentManager manager = getSupportFragmentManager();
                                        MyDialogFragment myDialogFragment = new MyDialogFragment();
                                        myDialogFragment.show(manager, "myDialog");
                                        return;
                                    }

                                    List<Dancer> dancerList = response.body();
                                    if (!dancerList.isEmpty()) {
                                        DancerUnitAdapter dancerUnitAdapter = new DancerUnitAdapter(v.getContext(), dancerList);
                                        dancerView.setAdapter(dancerUnitAdapter);
                                        dancerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Dancer dancer = dancerList.get(position);
                                                Intent intent = new Intent(SearchByCodeActivity.this, ProfileDancerActivity.class);
                                                String s = (new Gson().toJson(dancer));
                                                intent.putExtra("DancerUnit", s);
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                    searchDancer.setClickable(true);
                                }

                                @Override
                                public void onFailure(Call<List<Dancer>> call, Throwable t) {
                                    FragmentManager manager = getSupportFragmentManager();
                                    MyDialogFragment myDialogFragment = new MyDialogFragment();
                                    myDialogFragment.show(manager, "myDialog");
                                    searchDancer.setClickable(true);
                                    return;
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity() {
        try {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            System.out.println("Какая-то ошибочка");
        }
    }
}