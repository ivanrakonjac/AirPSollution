package com.ika.airpsollution.rest;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface AqicnAPI {

    @GET("search/")
    Call<JsonObject> getAllStations(@Query("token") String token, @Query("keyword") String keyword);

    @GET
    Call<JsonObject> getInfoForStation(@Url String url, @Query("token") String token);

}
