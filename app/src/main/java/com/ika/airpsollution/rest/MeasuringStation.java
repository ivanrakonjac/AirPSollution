package com.ika.airpsollution.rest;

import androidx.annotation.NonNull;

        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

        import java.util.List;

public class MeasuringStation {

    public MeasuringStation(String name, double lat, double lon, double[] pm10, double[] pm25, double[] so2, double[] no2, double[] co, double[] o3) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.so2 = so2;
        this.no2 = no2;
        this.co = co;
        this.o3 = o3;
    }

    public double lat;
    public double lon;
    public String name;

    public double[] pm10;
    public double[] pm25;
    public double[] so2;
    public double[] no2;
    public double[] co;
    public double[] o3;

    public void setPm10(double[] pm10) {
        this.pm10 = pm10;
    }


    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPm10(int index) {
        return pm10[index];
    }

    public double getPm25(int index) {
        return pm25[index];
    }

    public void setPm25(double[] pm25) {
        this.pm25 = pm25;
    }

    public double getSo2(int index) {
        return so2[index];
    }

    public void setSo2(double[] so2) {
        this.so2 = so2;
    }

    public double getNo2(int index) {
        return no2[index];
    }

    public void setNo2(double[] no2) {
        this.no2 = no2;
    }

    public double getCo(int index) {
        return co[index];
    }

    public void setCo(double[] co) {
        this.co = co;
    }

    public double getO3(int index) {
        return o3[index];
    }

    public void setO3(double[] o3) {
        this.o3 = o3;
    }
}

