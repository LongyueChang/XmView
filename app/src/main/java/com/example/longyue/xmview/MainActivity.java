package com.example.longyue.xmview;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

import XmViews.ChaosCompassView;
import XmViews.XmView;

public class MainActivity extends AppCompatActivity {
    private XmView xmView;
    private SensorEventListener mListener;
    private float value;
    private SensorManager severManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xmView= (XmView) findViewById(R.id.main_xmview);

        severManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                value = event.values[0];

                xmView.setValue(value);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };


        severManager.registerListener(mListener, severManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

    }

}
