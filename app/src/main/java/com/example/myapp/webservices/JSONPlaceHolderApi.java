package com.example.myapp.webservices;

import com.example.myapp.model.Dancer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JSONPlaceHolderApi {
    @GET("/dancers/{id}")
    public Call<Dancer> getDancerByID(@Path("id") int id);

    @GET("/dancers")
    public Call<Dancer> getAll();

    @GET("/dancers/code/{code}")
    public Call<Dancer> getDancerByCode(@Path("code") String code);
}