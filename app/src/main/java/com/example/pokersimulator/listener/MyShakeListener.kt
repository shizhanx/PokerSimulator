package com.example.pokersimulator.listener

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import kotlin.math.abs


/**
 * Code adopted from stackOverflow forum
 * https://stackoverflow.com/questions/2317428/how-to-refresh-app-upon-shaking-the-device
 * @author Peceps https://stackoverflow.com/users/590531/peceps
 */
class MyShakeListener: SensorEventListener {

    /** Minimum movement force to consider.  */
    private val MIN_FORCE = 20

    /**
     * Minimum times in a shake gesture that the direction of movement needs to
     * change.
     */
    private val MIN_ACCELERATION_CHANGE = 2

    /** Maximum pause between movements.  */
    private val MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 1000000000

    /** Maximum allowed time for shake gesture.  */
    private val MAX_TOTAL_DURATION_OF_SHAKE = 4000000000

    /** Time when the gesture started.  */
    private var mFirstDirectionChangeTime: Long = 0

    /** Time when the last movement started.  */
    private var mLastDirectionChangeTime: Long = 0

    /** How many movements are considered so far.  */
    private var mDirectionChangeCount = 0

    /** The last x position.  */
    private var lastX = 0f

    /** The last y position.  */
    private var lastY = 0f

    /** The last z position.  */
    private var lastZ = 0f

    /** OnShakeListener that is called when shake is detected.  */
    private lateinit var mShakeListener: OnShakeListener

    /**
     * Interface for shake gesture.
     */
    interface OnShakeListener {
        /**
         * Called when shake gesture is detected.
         */
        fun onShake()
    }

    fun setOnShakeListener(listener: OnShakeListener) {
        mShakeListener = listener
    }

    override fun onSensorChanged(se: SensorEvent) {
        // get sensor data
        val x: Float = se.values[0]
        val y: Float = se.values[1]
        val z: Float = se.values[2]
        if (lastX == 0f) lastX = x
        if (lastY == 0f) lastY = y
        if (lastZ == 0f) lastZ = z

        // calculate movement
        val totalChange = abs(x + y + z - lastX - lastY - lastZ)

        if (totalChange > MIN_FORCE) {

            // get time
            val now = se.timestamp

            // store first movement time
            if (mFirstDirectionChangeTime == 0L) {
                mFirstDirectionChangeTime = now
                mLastDirectionChangeTime = now
            }

            // check if the last movement was not long ago
            val lastChangeWasAgo = now - mLastDirectionChangeTime
            if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {

                // store movement data
                mLastDirectionChangeTime = now
                mDirectionChangeCount++

                // store last sensor data
                lastX = x
                lastY = y
                lastZ = z

                // check how many movements are so far
                if (mDirectionChangeCount >= MIN_ACCELERATION_CHANGE) {
                    // check total duration
                    val totalDuration = now - mFirstDirectionChangeTime
                    if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
                        mShakeListener.onShake()
                        resetShakeParameters()
                    }
                }
            } else {
                resetShakeParameters()
            }
        }
    }

    /**
     * Resets the shake parameters to their default values.
     */
    private fun resetShakeParameters() {
        mFirstDirectionChangeTime = 0
        mDirectionChangeCount = 0
        mLastDirectionChangeTime = 0
        lastX = 0f
        lastY = 0f
        lastZ = 0f
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}