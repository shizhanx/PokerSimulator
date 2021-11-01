package com.example.pokersimulator.listener

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class MyFlipDeviceListener(private val callback: () -> Unit): SensorEventListener {
    private var lastY = 0f
    private var lastTimeStamp = 0L
    /** Maximum pause between movements.  */
    private val maxPauseBetweenFlip = 1000000000
    /** The minimum difference of the Y value for a 180 degree flip */
    private val minFlipDifference = 0.7

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            if (lastY == 0f) lastY = sensorEvent.values[1]
            if (lastTimeStamp == 0L) lastTimeStamp = sensorEvent.timestamp

            // Periodically check if the Y value changed drastically because of a flip
            if (sensorEvent.timestamp - lastTimeStamp < maxPauseBetweenFlip) {
                if (sensorEvent.values[1] - lastY > minFlipDifference) {
                    callback()
                    lastY = sensorEvent.values[1]
                }
            } else {
                // Initialize the periodical observing.
                lastTimeStamp = sensorEvent.timestamp
                lastY = sensorEvent.values[1]
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}