package com.example.pokersimulator

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.WindowManager
import androidx.activity.viewModels
import com.example.pokersimulator.common.MyYesNoDialog
import com.example.pokersimulator.databinding.ActivityMainBinding
import com.example.pokersimulator.listener.MyFlipDeviceListener

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private var flipSensor: Sensor? = null
    private val myFlipDeviceListener = MyFlipDeviceListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        flipSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        setContentView(binding.root)
        this@MainActivity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_sorting_options, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        MyYesNoDialog(
            "Are you sure you want to go back or quit?",
            "Yes",
            "No",
            { super.onBackPressed() },
            {},
            {}
        ).show(supportFragmentManager, null)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(myFlipDeviceListener, flipSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        sensorManager.unregisterListener(myFlipDeviceListener)
        super.onPause()
    }
}