package com.nuvoton.ukeeper;

import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Map;

public class Setup extends AppCompatActivity implements Mbedder.MbedderInterface{
    private boolean isDrag = false;
    private final String TAG = "Setup";
    private String category = "Temperature";
    private String switchResource = "/3308/0/5850", seekbarResource = "/3308/0/5900";
    private SeekBar seekBar;
    private TextView valueText;
    private Switch aSwitch;
    private Mbedder mbedder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbedder = Mbedder.getInstance(this);
        mbedder.mbedderInterface = this;
        setContentView(R.layout.activity_setup);
        category = getIntent().getStringExtra("Category");
        TextView title = (TextView) findViewById(R.id.category_setup);
        valueText = (TextView) findViewById(R.id.value);
        valueText.setText(R.string.connecting);
        seekBar = (SeekBar) findViewById(R.id.slider);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                valueText.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDrag = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //send value
                if (isDrag){
                    Log.d(TAG, "onStopTrackingTouch: " + isDrag);
                    String value = String.valueOf(seekBar.getProgress());
                    mbedder.putValueToResource(seekbarResource, value);
                }
                isDrag = false;
            }
        });
        aSwitch = (Switch) findViewById(R.id.onoff);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                if (isOn){
                    mbedder.putValueToResource(switchResource, "1");
                }else{
                    if (category.compareTo("Food") == 0){
                        valueText.setText("0");
                    }
                    mbedder.putValueToResource(switchResource, "0");
                }
            }
        });

        String label = getResources().getString(R.string.temperature_label);
        if (category.compareTo("Illuminance") == 0){
            label = getResources().getString(R.string.illuminance_label);
        }else if (category.compareTo("Food") == 0){
            label = getResources().getString(R.string.food_label);
        }
        title.setText(label);
        determineResources();
        updateValue(seekbarResource);
    }

    // Mbedder interface

    @Override
    public void payloadWithPollingText(Map<String, String> ret) {
        Log.d(TAG, "payloadWithPollingText: " + ret);
        final String payload = ret.get("Payload");
        String resource = ret.get("Category");
        if (payload.compareTo("-1") == 0 || payload.compareTo("") == 0){
            updateValue(resource);
            return;
        }
        if (resource.compareTo(seekbarResource) == 0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    valueText.setText(payload);
                    seekBar.setProgress(Integer.valueOf(payload));
                }
            });
            if (switchResource.compareTo(seekbarResource) != 0) {
                updateValue(switchResource);
            }else{
                if (payload.compareTo("0") == 0){
                    setSwitch(false);
                }else{
                    setSwitch(true);
                }
            }
        }else{
            if (payload.compareTo("0") == 0){
                setSwitch(false);
            }else{
                setSwitch(true);
            }
        }
    }

    @Override
    public void showToastMessage(String messenger) {

    }

    @Override
    public void isConnected(boolean option) {

    }

    @Override
    public void postAfterPut(String resource, String value) {
        mbedder.postValueToResource(resource, value);
    }

    // utilities
    private void determineResources(){
        if (category.compareTo("Illuminance") == 0){
            switchResource = "/3311/0/5850";
            seekbarResource = "/3311/0/5851";
        }else if (category.compareTo("Food") == 0) {
            switchResource = "/3343/0/5851";
            seekbarResource = "/3343/0/5851";
        }
    }

    private void updateValue(String resource){
        mbedder.openLongPolling(resource);
        mbedder.getNodeValue(resource);
    }

    private void setSwitch(final boolean option){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                aSwitch.setChecked(option);
            }
        });
    }
}
