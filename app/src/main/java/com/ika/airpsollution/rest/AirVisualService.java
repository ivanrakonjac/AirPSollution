package com.ika.airpsollution.rest;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AirVisualService {

    private AirVisualAPI airVisualAPI;

    private static final String API_KEY = "@string/AirVisual_KEY";
    private static final String BASE_URL = "http://api.airvisual.com/v2/"; //Trenutno za AirVisual

    public AirVisualService() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(loggingInterceptor);

        OkHttpClient okHttpClient = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        airVisualAPI = retrofit.create(AirVisualAPI.class);
    }

    public void getAllStations(){
        Call<JsonObject> call = airVisualAPI.getAllStations("Serbia", API_KEY);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.d("#API-TESTING", response.body().toString());

                JsonArray states = response.body().getAsJsonArray("data");
                for (int i = 0; i < states.size(); i++) {
                    String state = states.get(i).getAsJsonObject().get("state").getAsString();
                    Log.d("#API-TESTING", state);
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("Evo me");
            }
        });
    }

}
