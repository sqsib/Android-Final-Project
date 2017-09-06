package com.example.sib.finalproject.Services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.sib.finalproject.MainActivity;

/**
 * Created by Shuo on 3/22/2017.
 */

public class DirectionManager implements SensorEventListener {
    MainActivity mainActivity;
    SensorManager sensorManager;
    Sensor accSensor;
    Sensor magneticSensor;
    float[] accData;
    float[] magData;

    public DirectionManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        sensorManager = (SensorManager) mainActivity.getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void register() {
        if (accSensor != null) {
            sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (magneticSensor != null) {
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.equals(accSensor)) {
            accData = sensorEvent.values;
        }
        if (sensorEvent.sensor.equals(magneticSensor)) {
            magData = sensorEvent.values;
        }

        if (accData != null && magData != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, accData, magData)) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                double angle = orientation[0] * 360 / (2 * Math.PI);
                if (angle < 0) {
                    angle += 360;
                }
                mainActivity.updateSensor(angle);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
