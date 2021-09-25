package com.ika.airpsollution.ui.gallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ika.airpsollution.R;
import com.ika.airpsollution.rest.MeasuringStation;
import com.ika.airpsollution.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class StatisticsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private BarChart PM10barChart;
    private BarChart PM25barChart;
    private BarChart SO2barChart;
    private LineChart NO2lineChart;
    private LineChart COlineChart;

    private ArrayList<BarEntry> PM10barEntry;
    private ArrayList<BarEntry> PM25barEntry;
    private ArrayList<BarEntry> SO2barEntry;
    private ArrayList<Entry> NO2lineEntry;
    private ArrayList<Entry> COlineEntry;

    private BarData barDataPM10;
    private BarData barDataPM25;
    private BarData barDataSO2;
    private LineData lineDataNO2;
    private LineData lineDataCO;

    private double[] pm10Avg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] pm25Avg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] so2Avg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] no2Avg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] coAvg = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner dropdown = view.findViewById(R.id.spinner1);


        List<MeasuringStation> msList = HomeViewModel.getStationList();

        String[] items = new String[msList.size() + 1];
        items[0] = "Statistika";
        for (int i = 1; i < items.length; i++) {
            items[i] = msList.get(i - 1).name;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, items);
        dropdown.setAdapter(adapter);


        getStatistics();

        PM10barChart = view.findViewById(R.id.chartPM10);
        PM25barChart = view.findViewById(R.id.chartPM25);
        SO2barChart = view.findViewById(R.id.chartSO2);
        NO2lineChart = view.findViewById(R.id.chartNO2);
        COlineChart = view.findViewById(R.id.chartCO);

        PM10barEntry = new ArrayList<>();
        PM25barEntry = new ArrayList<>();
        SO2barEntry = new ArrayList<>();
        NO2lineEntry = new ArrayList<>();
        COlineEntry = new ArrayList<>();

        BarDataSet barDataSet = new BarDataSet(PM10barEntry, "PM10");

        LineDataSet lineDataSet = new LineDataSet(NO2lineEntry, "NO2");

        barDataPM10 = new BarData(barDataSet);
        barDataPM25 = new BarData(barDataSet);
        barDataSO2 = new BarData(barDataSet);
        lineDataNO2 = new LineData(lineDataSet);
        lineDataCO = new LineData(lineDataSet);


        LimitLine ll = new LimitLine(60, "Opasno po zdravlje");
        PM10barChart.getAxisLeft().addLimitLine(ll);

        LimitLine ll2 = new LimitLine(30, "Opasno po zdravlje");
        PM25barChart.getAxisLeft().addLimitLine(ll);

        LimitLine ll3 = new LimitLine(350, "Opasno po zdravlje");
        SO2barChart.getAxisLeft().addLimitLine(ll);

        LimitLine ll4 = new LimitLine(150, "Opasno po zdravlje");
        NO2lineChart.getAxisLeft().addLimitLine(ll);

        LimitLine ll5 = new LimitLine(100, "Opasno po zdravlje");
        COlineChart.getAxisLeft().addLimitLine(ll);

        setGraphData(barDataPM10, PM10barChart,PM10barEntry, pm10Avg, "PM10 [μg/m3]", "");
        setGraphData(barDataPM25, PM25barChart,PM25barEntry, pm25Avg, "PM25 [μg/m3]", "");
        setGraphData(barDataSO2, SO2barChart,SO2barEntry, so2Avg, "SO2 [μg/m3]", "");
        setLineCharthData(lineDataNO2, NO2lineChart,NO2lineEntry, no2Avg, "NO2 [μg/m3]", "");
        setLineCharthData(lineDataCO, COlineChart,COlineEntry, coAvg, "CO [μg/m3]", "");

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(id != 0){
                    double[] PM10_values = HomeViewModel.getMeasuringStation(position - 1).getPM10Array();
                    double[] PM25_values = HomeViewModel.getMeasuringStation(position - 1).getPM25Array();
                    double[] SO2_values = HomeViewModel.getMeasuringStation(position - 1).getSO2Array();
                    double[] NO2_values = HomeViewModel.getMeasuringStation(position - 1).getNO2Array();
                    double[] CO_values = HomeViewModel.getMeasuringStation(position - 1).getCOArray();

                    setGraphData(barDataPM10, PM10barChart,PM10barEntry, PM10_values, "PM10 [μg/m3]", "");
                    setGraphData(barDataPM25, PM25barChart,PM25barEntry, PM25_values, "PM25 [μg/m3]", "");
                    setGraphData(barDataSO2, SO2barChart,SO2barEntry, SO2_values, "SO2 [μg/m3]", "");
                    setLineCharthData(lineDataNO2, NO2lineChart,NO2lineEntry, NO2_values, "NO2 [μg/m3]", "");
                    setLineCharthData(lineDataCO, COlineChart,COlineEntry, CO_values, "CO [μg/m3]", "");

                }
                else{

                    getStatistics();

                    setGraphData(barDataPM10, PM10barChart,PM10barEntry, pm10Avg, "PM10 [μg/m3]", "");
                    setGraphData(barDataPM25, PM25barChart,PM25barEntry, pm25Avg, "PM25 [μg/m3]", "");
                    setGraphData(barDataSO2, SO2barChart,SO2barEntry, so2Avg, "SO2 [μg/m3]", "");
                    setLineCharthData(lineDataNO2, NO2lineChart,NO2lineEntry, no2Avg, "NO2 [μg/m3]", "");
                    setLineCharthData(lineDataCO, COlineChart,COlineEntry, coAvg, "CO [μg/m3]", "");
                }

                Log.d("#SPINER", "Position: " + position + " Id: " + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setGraphData(BarData barData, BarChart barChart,ArrayList<BarEntry> barEntry, double[] values, String barLabel, String description){

        barData.removeDataSet(0);

        barEntry.clear();

        for (int i = 0; i < values.length; i++) {
            barEntry.add(new BarEntry(i, (int) values[i]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntry, barLabel);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barData.addDataSet(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText(description);
        barChart.animateY(2000);

        barData.notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    private void setLineCharthData(LineData lineData, LineChart lineChart,ArrayList<Entry> lineEntry, double[] values, String barLabel, String description){

        lineData.removeDataSet(0);

        lineEntry.clear();

        for (int i = 0; i < values.length; i++) {
            lineEntry.add(new BarEntry(i, (int) values[i]));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntry, barLabel);
        lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(16f);
        lineData.addDataSet(lineDataSet);

        lineChart.setData(lineData);
        lineChart.getDescription().setText(description);
        lineChart.animateY(2000);

        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void getStatistics() {

        for (int j = 0; j < 10; j++) {
            pm10Avg[j] = 0;
            pm25Avg[j] = 0;
            so2Avg[j] = 0;
            no2Avg[j] = 0;
            coAvg[j] = 0;
        }

        List<MeasuringStation> msList = HomeViewModel.getStationList();

        for (int i = 0; i < msList.size(); i++) {
            double[] pm10Values = msList.get(i).getPM10Array();
            double[] pm25Values = msList.get(i).getPM25Array();
            double[] so2Values = msList.get(i).getSO2Array();
            double[] no2Values = msList.get(i).getNO2Array();
            double[] coValues = msList.get(i).getCOArray();

            for (int j = 0; j < 10; j++) {
                pm10Avg[j] += pm10Values[j];
                pm25Avg[j] += pm25Values[j];
                so2Avg[j] += so2Values[j];
                no2Avg[j] += no2Values[j];
                coAvg[j] += coValues[j];
            }

        }

        for (int j = 0; j < 10; j++) {
            pm10Avg[j] = pm10Avg[j] / msList.size();
            pm25Avg[j] = pm25Avg[j] / msList.size();
            so2Avg[j] = so2Avg[j] / msList.size();
            no2Avg[j] = no2Avg[j] / msList.size();
            coAvg[j] = coAvg[j] / msList.size();
        }

    }

}