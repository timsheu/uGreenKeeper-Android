package com.nuvoton.ukeeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseChart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_chart);
        Button tempButton = (Button) findViewById(R.id.chart_temperature);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("Category", "Temperature");
                intent.setClass(ChooseChart.this, DataCharts.class);
                startActivity(intent);
            }
        });

        Button humidButton = (Button) findViewById(R.id.chart_humid);
        humidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("Category", "Humidity");
                intent.setClass(ChooseChart.this, DataCharts.class);
                startActivity(intent);
            }
        });

        Button luxButton = (Button) findViewById(R.id.chart_lux);
        luxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("Category", "Illuminance");
                intent.setClass(ChooseChart.this, DataCharts.class);
                startActivity(intent);
            }
        });

        Button activityButton = (Button) findViewById(R.id.chart_activity);
        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("Category", "Activity");
                intent.setClass(ChooseChart.this, DataCharts.class);
                startActivity(intent);
            }
        });
    }
}
