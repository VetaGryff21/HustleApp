package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.myapp.model.Dancer;
import com.example.myapp.webservices.NetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDancerActivity extends AppCompatActivity {

    private ArrayList<DancerUnit> dancerList;
    private ListView dancerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchdancer_page);
        final EditText inputDancer = findViewById(R.id.search_input);
        dancerView = (ListView) findViewById(R.id.dancer_list);


        Button searchDancer = (Button) findViewById(R.id.btn_searching);
        searchDancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tmpName = inputDancer.getText().toString();
                NetworkService.getInstance()
                        .getJSONApi()
                        .getDancersByFulname(tmpName)
                        .enqueue(new Callback<List<Dancer>>(){
                            @Override
                            public void onResponse(@NonNull Call<List<Dancer>> call, @NonNull Response<List<Dancer>> response) {

                                if (response.code() == 404) {
                                    FragmentManager manager = getSupportFragmentManager();
                                    MyDialogFragment myDialogFragment = new MyDialogFragment();
                                    myDialogFragment.show(manager, "myDialog");
                                    return;
                                }

                                List<Dancer> dancerList = response.body();
                                if(!dancerList.isEmpty()) {
                                    DancerUnitAdapter dancerUnitAdapter = new DancerUnitAdapter(v.getContext(), dancerList);
                                    dancerView.setAdapter(dancerUnitAdapter);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Dancer>> call, Throwable t) {
                                FragmentManager manager = getSupportFragmentManager();
                                MyDialogFragment myDialogFragment = new MyDialogFragment();
                                myDialogFragment.show(manager, "myDialog");
                                return;
                            }
                        });
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