package com.nuvoton.ukeeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Credits extends AppCompatActivity {
    private String creditsText = "UKeeper Credits:\n" +
            "ARCA: https://github.com/ACRA/acra\n" +
            "DavidWebb: https://github.com/hgoebl/DavidWebb\n" +
            "MPAndroidChart: https://github.com/PhilJay/MPAndroidChart";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        TextView textView = (TextView) findViewById(R.id.creditsTextView);
        textView.setText(creditsText);
    }
}
