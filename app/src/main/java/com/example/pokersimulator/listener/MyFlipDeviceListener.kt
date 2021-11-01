package com.example.pokersimulator.listener

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log

class MyFlipDeviceListener: SensorEventListener {
    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            Log.d("TAG", "onSensorChanged: ${sensorEvent.values}")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}