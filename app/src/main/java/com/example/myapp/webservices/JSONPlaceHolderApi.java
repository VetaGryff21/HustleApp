package com.example.myapp.webservices;

import com.example.myapp.model.Dancer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JSONPlaceHolderApi {
    @GET("/dancers/{id}")
    Call<Dancer> getDancerByID(@Path("id") int id);

    @GET("/dancers")
    Call<Dancer> getAll();

    @GET("/dancers/code/{code}")
    Call<Dancer> getDancerByCode(@Path("code") String code);

    @GET("/dancers/fullname/{fullname}")
    Call<List<Dancer>> getDancersByFulname(@Path("fullname") String fullname);
}