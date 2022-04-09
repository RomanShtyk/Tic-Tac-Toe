package com.example.tiktactoe.ui

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tiktactoe.R
import com.example.tiktactoe.ui.list.CellAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class GameActivity : AppCompatActivity(), SensorEventListener {

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("MyTag", "onAccuracyChanged: Sensor: $sensor; accuracy: $accuracy")
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return
        // Data 1: According to official documentation, the first value of the `SensorEvent` value is the step count
        sensorEvent.values.firstOrNull()?.let {
            Log.d("MyTag", "Step count: $it ")
        }

        // Data 2: The number of nanosecond passed since the time of last boot
        val lastDeviceBootTimeInMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val sensorEventTimeInNanos =
            sensorEvent.timestamp // The number of nanosecond passed since the time of last boot
        val sensorEventTimeInMillis = sensorEventTimeInNanos / 1000_000

        val actualSensorEventTimeInMillis = lastDeviceBootTimeInMillis + sensorEventTimeInMillis
        val displayDateStr =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(actualSensorEventTimeInMillis)
        Log.d("MyTag", "Sensor event is triggered at $displayDateStr")

    }

    private val viewModel: GameViewModel by viewModels()

    private val adapter: CellAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CellAdapter(viewModel::onCellClicked)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onGranted()
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }

    private fun onGranted() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initView()
        subscribeToViewModel()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            onGranted()
            return
        }
        if (ContextCompat.checkSelfPermission(this, ACTIVITY_RECOGNITION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            requestPermissionLauncher.launch(ACTIVITY_RECOGNITION)
        }
    }

    private fun initView() {
        rvCells.layoutManager = object : GridLayoutManager(this, GAME_SIZE) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvCells.adapter = adapter

        vTouchCatcher.setOnClickListener {
            viewModel.onReset()
        }
    }

    private fun subscribeToViewModel() {
        lifecycleScope.launchWhenCreated {
            viewModel.cellsList.collect(adapter::submitList)
        }
        lifecycleScope.launchWhenCreated {
            viewModel.isGameFinished.collect(::onGameFinished)
        }
    }

    private fun onGameFinished(isFinished: Boolean) {
        vTouchCatcher.visibility = if (isFinished) View.VISIBLE else View.GONE
    }

    companion object {

        const val GAME_SIZE = 3
    }
}
