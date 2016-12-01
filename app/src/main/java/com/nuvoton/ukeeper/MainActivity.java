package com.nuvoton.ukeeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Map;
import java.util.logging.Handler;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements Mbedder.MbedderInterface{
    private String[] nameArray = {
            "Tim",
            "Morgan",
            "Peter"
    };
    Mbedder mbedder;
    Button chooseCharts, chooseSetup, credits;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbedder = Mbedder.getInstance(this);
        mbedder.mbedderInterface = this;
        setContentView(R.layout.activity_main);
        chooseCharts = (Button) findViewById(R.id.choose_chart_button);
        chooseCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ChooseChart.class);
                startActivity(intent);
            }
        });

        chooseSetup = (Button) findViewById(R.id.choose_setup_button);
        chooseSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ChooseSetup.class);
                startActivity(intent);
            }
        });
        chooseSetup.setEnabled(false);
        chooseCharts.setEnabled(false);

        final Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, R.string.connecting, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mbedder.getEndName();
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    connect.setEnabled(false);
                                    connect.setBackgroundResource(R.mipmap.banner_grey);
                                }
                            });
                            sleep(5000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    connect.setEnabled(true);
                                    connect.setBackgroundResource(R.mipmap.banner_blue);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, nameArray);
        Spinner keyOwner = (Spinner) findViewById(R.id.key_spinner);
        keyOwner.setAdapter(adapter);
        keyOwner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mbedder.setupKey(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        credits = (Button) findViewById(R.id.credits);
        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Credits.class);
                startActivity(intent);
            }
        });
    }

    //Mbedder interface

    @Override
    public void payloadWithPollingText(Map<String, String> ret) {

    }

    @Override
    public void showToastMessage(final String messenger) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, messenger, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void isConnected(boolean option) {
        if (option){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chooseSetup.setEnabled(true);
                    chooseSetup.setBackgroundResource(R.mipmap.banner_green);
                    chooseCharts.setEnabled(true);
                    chooseCharts.setBackgroundResource(R.mipmap.banner_orange);
                }
            });
        }
    }

    @Override
    public void postAfterPut(String resource, String value) {

    }
}
