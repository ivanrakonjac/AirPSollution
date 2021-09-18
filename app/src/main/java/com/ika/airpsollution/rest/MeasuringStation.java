package com.ika.airpsollution.rest;

import androidx.annotation.NonNull;

        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

        import java.util.List;

public class MeasuringStation {

    public MeasuringStation(String name, double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
    }

    public double lat;
    public double lon;
    public String name;
}

