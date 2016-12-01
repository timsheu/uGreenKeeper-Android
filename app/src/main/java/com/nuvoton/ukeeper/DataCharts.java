package com.nuvoton.ukeeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatProperty;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DataCharts extends AppCompatActivity implements Mbedder.MbedderInterface{
    private final String TAG = "DataCharts";
    private boolean isExit = false;
    private LineChart lineChart;
    private LineDataSet lineDataSet;
    List<Entry> lineChartData = new ArrayList<>();
    List<Float> lineChartYData = new ArrayList<>();
    Mbedder mbedder;
    private int DATA_COUNT = 10;
    private String category = "Temperature";
    private String pollingText;
    private Timer pollingTimer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbedder = Mbedder.getInstance(this);
        mbedder.mbedderInterface = this;
        setContentView(R.layout.activity_data_charts);
        category = getIntent().getStringExtra("Category");
        TextView title = (TextView) findViewById(R.id.category_charts);
        String label = getResources().getString(R.string.temperature_label);
        if (category.compareTo("Humidity") == 0){
            label = getResources().getString(R.string.humidity_label);
        }else if (category.compareTo("Illuminance") == 0){
            label = getResources().getString(R.string.illuminance_label);
        }else if (category.compareTo("Activity") == 0){
            label = getResources().getString(R.string.activity_label);
        }
        title.setText(label);

        lineChart = (LineChart) findViewById(R.id.line_chart);
        initialChart();
        setData();
        sendOpenPolling();
        pollingTimer.scheduleAtFixedRate(new PollingTask(), 0, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pollingTimer.cancel();
    }

    private void initialChart(){
        if (lineChartData.size() != DATA_COUNT){
            for (int i=0; i<DATA_COUNT; i++){
                lineChartData.add(new Entry(i, 0));
                lineChartYData.add(0f);
            }
        }
    }

    private void shiftChartData(Float value){
        lineChartYData.remove(0);
        lineChartYData.add(value);
        lineChartData = new ArrayList<>();
        for (int i=0; i<DATA_COUNT; i++){
            lineChartData.add(new Entry(i, lineChartYData.get(i)));
        }
    }

    private LineData getLineData(){
        lineDataSet = new LineDataSet(lineChartData, "Value of " + category);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        return new LineData(dataSets);
    }

    private void setData(){
        lineChart.setData(getLineData());
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
    }

    private void sendOpenPolling(){
        pollingText = "/3303/0/5700";
        if (category.compareTo("Humidity") == 0){
            pollingText = "/3304/0/5700";
        }else if (category.compareTo("Illuminance") == 0){
            pollingText = "/3301/0/5700";
        }else if (category.compareTo("Activity") == 0){
            pollingText = "/3302/0/5500";
        }
    }

    private class PollingTask extends TimerTask{
        @Override
        public void run() {
            mbedder.openLongPolling(pollingText);
            mbedder.getNodeValue(pollingText);
        }
    };

    //Mbedder interface

    @Override
    public void payloadWithPollingText(Map<String, String> ret) {
        Log.d(TAG, "payloadWithPollingText: " + ret);
        String payload = ret.get("Payload");
        if (payload.compareTo("-1") != 0) {
            String returnPollingText = ret.get("Category");
            if (returnPollingText.compareTo(pollingText) == 0) {
                shiftChartData(Float.valueOf(payload));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setData();
                        lineDataSet.notifyDataSetChanged();
                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();
                    }
                });
                Log.d(TAG, "payloadWithPollingText: " + payload);
            }
        }else{
//            Toast.makeText(DataCharts.this, "Server did not reply data, waiting...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showToastMessage(String messenger) {
//        Toast.makeText(this, messenger, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isConnected(boolean option) {

    }

    @Override
    public void postAfterPut(String resource, String value) {

    }
}
