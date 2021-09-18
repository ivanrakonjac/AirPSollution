package com.ika.airpsollution.rest;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ika.airpsollution.ui.home.HomeViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AqicnService{

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

    public void getAllStationss() {

        Call<JsonObject> call = aqicnAPI.getAllStationss(API_KEY, "Serbia");

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.d("#API-TESTING", response.body().toString());

                JsonArray data = response.body().getAsJsonArray("data");
                Log.d("#API-TESTING", data.toString());

                for (JsonElement obj: data) {
//                    Log.d("#API-TESTING", obj.toString());
                    JsonObject  jobject = obj.getAsJsonObject();
                    jobject = jobject.getAsJsonObject("station");
                    JsonElement name = jobject.get("name");
                    JsonElement geo = jobject.get("geo");
                    String lat = geo.getAsJsonArray().get(0).toString();
                    String lon = geo.getAsJsonArray().get(1).toString();
                    Log.d("#API-TESTING", jobject.toString());
                    Log.d("#API-TESTING", name.toString());
                    Log.d("#API-TESTING", lat + " | " + lon);

                    MeasuringStation ms = new MeasuringStation(name.toString(), Double.parseDouble(lat), Double.parseDouble(lon));
                    HomeViewModel.addStation(ms);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error", call.toString());
                Log.d("Error", t.toString());
            }
        });
    }
}
