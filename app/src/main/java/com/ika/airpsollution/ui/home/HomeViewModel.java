package com.ika.airpsollution.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ika.airpsollution.rest.MeasuringStation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private static ArrayList<MeasuringStation> stationsList = new  ArrayList<MeasuringStation>();
    private static MutableLiveData<ArrayList<MeasuringStation>> stationsData = new MutableLiveData<>(stationsList);

    public static void addStation(MeasuringStation ms){
        stationsList.add(ms);
        stationsData.postValue(stationsList);
    }

    public static List<MeasuringStation> getStationList(){
        return stationsData.getValue();
    }

    public static MutableLiveData<ArrayList<MeasuringStation>> getMutableLiveData(){
        return stationsData;
    }

    public static MeasuringStation getMeasuringStation(int index){
        return stationsList.get(index);
    }

    public static void setMeasurements(int stationIndex, double[] measurments){
        stationsList.get(stationIndex).setPm10(measurments);

        Log.d("#MERENJA", stationsList.get(stationIndex).name);
        stationsData.postValue(stationsList);
    }

    public static void clearStationsList(){
        stationsList.clear();
        stationsData.postValue(stationsList);
    }

}