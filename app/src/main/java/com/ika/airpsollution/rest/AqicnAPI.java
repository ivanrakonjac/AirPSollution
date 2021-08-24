package com.ika.airpsollution.rest;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AqicnAPI {

    @GET("states")
    Call<JsonObject> getAllStations(@Query("country") String country, @Query("key") String key);

    @GET("search/")
    Call<Object> getAllStationss(@Query("token") String token, @Query("keyword") String keyword);

}
