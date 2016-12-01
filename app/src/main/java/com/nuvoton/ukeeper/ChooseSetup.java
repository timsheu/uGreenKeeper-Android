package com.nuvoton.ukeeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseSetup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_setup);

        Button temperatureSetup = (Button) findViewById(R.id.setup_temperature);
        temperatureSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("Category", "Temperature");
                intent.setClass(ChooseSetup.this, Setup.class);
                startActivity(intent);
            }
        });

        Button illuminanceSetup = (Button) findViewById(R.id.setup_illuminance);
        illuminanceSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("Category", "Illuminance");
                intent.setClass(ChooseSetup.this, Setup.class);
                startActivity(intent);
            }
        });

        Button foodSetup = (Button) findViewById(R.id.setup_food);
        foodSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("Category", "Food");
                intent.setClass(ChooseSetup.this, Setup.class);
                startActivity(intent);
            }
        });
    }
}
