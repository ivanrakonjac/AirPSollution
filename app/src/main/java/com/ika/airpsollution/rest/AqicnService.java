package com.ika.airpsollution.rest;

import android.icu.number.Precision;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ika.airpsollution.ui.home.HomeViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AqicnService{

    private static final String API_KEY = "2899ae75fe9ed379218345c2a204cb8b0cc45124";
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

    public void getAllStations() {

        Call<JsonObject> call = aqicnAPI.getAllStations(API_KEY, "Serbia");

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

                    MeasuringStation ms = new MeasuringStation(name.toString(), Double.parseDouble(lat), Double.parseDouble(lon),
                                        getMeasurments(0,200), getMeasurments(0,100), getMeasurments(0,510),
                                        getMeasurments(0,410), getMeasurments(0,550), getMeasurments(0,260));
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

    public void getInfoForStation(String stationName) {

        String url = BASE_URL + "/feed/" + stationName + "/";

        Call<JsonObject> call = aqicnAPI.getInfoForStation(url, API_KEY);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.d("#API-TESTING", response.body().toString());

                JsonObject data = response.body().getAsJsonObject("data");
                Log.d("#API-TESTING", data.toString());

                JsonObject iaqi = response.body().getAsJsonObject("data").getAsJsonObject("iaqi");
                Log.d("#API-TESTING", iaqi.toString());

                JsonObject forecast = response.body().getAsJsonObject("data").getAsJsonObject("forecast");
                JsonObject daily = forecast.getAsJsonObject("daily");

                JsonArray o3 = daily.getAsJsonArray("o3");
                JsonArray pm10 = daily.getAsJsonArray("pm10");
                JsonArray pm25 = daily.getAsJsonArray("pm25");
                JsonArray uvi = daily.getAsJsonArray("uvi");
                Log.d("#API-TESTING", o3.toString());
                Log.d("#API-TESTING", pm10.toString());
                Log.d("#API-TESTING", pm25.toString());
                Log.d("#API-TESTING", uvi.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error", call.toString());
                Log.d("Error", t.toString());
            }
        });


    }

    private double[] getMeasurments (double MIN, double MAX){

        double[] measurements = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < 10; i++) {
//            double randomNum = ThreadLocalRandom.current().nextDouble(MIN, MAX + 1);
            double randomNum = new Random().nextDouble()*MAX;
            measurements[i] = new BigDecimal(randomNum).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }

        return measurements;
    }



}
