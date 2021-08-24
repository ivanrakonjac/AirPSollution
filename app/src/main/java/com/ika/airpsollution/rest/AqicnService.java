package com.ika.airpsollution.rest;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AqicnService{

//    private static final String API_KEY = ""; //Trenutno za AirVisual
//    private static final String BASE_URL = "http://api.airvisual.com/v2/"; //Trenutno za AirVisual

    private static final String API_KEY = "";
    private static final String BASE_URL = "https://api.waqi.info/";

    private AqicnAPI aqicnAPI;

    public AqicnService() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(loggingInterceptor);

        OkHttpClient okHttpClient = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        aqicnAPI = retrofit.create(AqicnAPI.class);
    }

    public void getAllStations(){
        Call<JsonObject> call = aqicnAPI.getAllStations("Serbia", API_KEY);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.d("#API-TESTING", response.body().toString());

//                String pageName = response.body().getAsJsonObject("pageInfo").get("pageName").getAsString();

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


    public void getAllStationss() {

        Call<Object> call = aqicnAPI.getAllStationss(API_KEY, "Pančevo Vojlovica");

        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                Log.d("#API-TESTING", response.body().toString());

//                String pageName = response.body().getAsJsonObject("pageInfo").get("pageName").getAsString();

//                JsonArray states = response.body().getAsJsonArray("data");
//                for (int i = 0; i < states.size(); i++) {
//                    String state = states.get(i).getAsJsonObject().get("state").getAsString();
//                    Log.d("#API-TESTING", state);
//                }


            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                System.out.println("Evo me");
            }
        });
    }
}
