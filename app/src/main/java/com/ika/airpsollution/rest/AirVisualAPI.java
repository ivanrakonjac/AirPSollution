package com.ika.airpsollution.rest;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AirVisualAPI {
    @GET("states")
    Call<JsonObject> getAllStations(@Query("country") String country, @Query("key") String key);
}
