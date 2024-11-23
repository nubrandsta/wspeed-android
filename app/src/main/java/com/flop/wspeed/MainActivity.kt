package com.flop.wspeed

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.opencv.android.OpenCVLoader
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var chart: LineChart

    private lateinit var rpmTextView: TextView
    private lateinit var stableRpmTextView: TextView


    private val rpmHistory = mutableListOf<Float>() // For tracking recent RPMs
    private var lastStableRpm: Float = 0f // Last stable RPM value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.view_finder)
        chart = findViewById(R.id.chart)

        rpmTextView = findViewById(R.id.rpmTextView)
        stableRpmTextView = findViewById(R.id.stableRpmTextView)

        setupChart()
        setupCamera()

        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            println("OpenCV failed to initialize!")
        } else {
            println("OpenCV successfully initialized!")
        }
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analysis = androidx.camera.core.ImageAnalysis.Builder()
                .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                FrameAnalyzer(
                    onRPMCalculated = { rpm ->
                        runOnUiThread {
                            displayRPM(rpm)
                        }
                    },
                    onIntensityCalculated = { intensity ->
                        runOnUiThread {
                            addEntryToChart(intensity) // Update the graph
                        }
                    }
                )
            )

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis)
            } catch (exc: Exception) {
                println("Failed to bind use cases: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }


    private fun displayRPM(rpm: Float) {
        // Update the current RPM display
        rpmTextView.text = String.format("RPM: %.2f", rpm)

        // Update RPM history for stability check
        updateRpmHistory(rpm)

        // Check stability and update stable RPM display
        if (isStableRpm()) {
            lastStableRpm = rpmHistory.average().toFloat()
            stableRpmTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
        } else {
            stableRpmTextView.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        }

        stableRpmTextView.text = String.format("Stable RPM: %.2f", lastStableRpm)
    }

    private fun isStableRpm(): Boolean {
        if (rpmHistory.size < 5) return false // Not enough data
        val mean = rpmHistory.average()
        val variance = rpmHistory.map { (it - mean) * (it - mean) }.average()
        val standardDeviation = kotlin.math.sqrt(variance)
        return standardDeviation < 5 // Threshold for stability
    }

    private fun updateRpmHistory(rpm: Float) {
        // Maintain a fixed-size buffer of recent RPM values
        if (rpmHistory.size >= 20) {
            rpmHistory.removeAt(0)
        }
        rpmHistory.add(rpm)
    }


    private fun setupChart() {
        val dataSet = LineDataSet(mutableListOf<Entry>(), "Intensity")
        dataSet.color = ContextCompat.getColor(this, android.R.color.holo_blue_light)
        dataSet.setDrawValues(false)
        dataSet.setDrawCircles(false)

        chart.data = LineData(dataSet)
        chart.description.text = "Pixel Intensity / Time"
        chart.description.isEnabled = false
        chart.axisLeft.isEnabled = true
        chart.axisRight.isEnabled = false
        chart.xAxis.isEnabled = false
        chart.legend.isEnabled = false
    }

    private fun addEntryToChart(intensity: Float) {
        val data = chart.data ?: return
        val dataSet = data.getDataSetByIndex(0) ?: return

        dataSet.addEntry(Entry(dataSet.entryCount.toFloat(), intensity))
        data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.setVisibleXRangeMaximum(50f) // Keep recent 50 data points visible
        chart.moveViewToX(data.entryCount.toFloat())
    }
}
