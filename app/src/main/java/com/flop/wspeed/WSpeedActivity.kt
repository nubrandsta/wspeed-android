package com.flop.wspeed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.opencv.android.OpenCVLoader
import java.util.concurrent.Executors

class WSpeedActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var chart: LineChart

    private lateinit var rpmTextView: TextView
    private lateinit var stableRpmTextView: TextView

    private lateinit var refreshButton: Button

    private var numberOfBlades: Int = 0
    private lateinit var bladesTextView: TextView

    private val rpmHistory = mutableListOf<Float>() // For tracking recent RPMs
    private var lastStableRpm: Int = 0 // Last stable RPM value

    private var trueRPM = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wspeed)

        previewView = findViewById(R.id.view_finder)
        chart = findViewById(R.id.chart)

        rpmTextView = findViewById(R.id.rpmTextView)
        stableRpmTextView = findViewById(R.id.stableRpmTextView)

        val blades = intent.getIntExtra("number", -1)
        numberOfBlades = blades
        val bladesText = blades.toString() + " Jari-jari"

        bladesTextView = findViewById(R.id.bladesTextView)
        bladesTextView.text = bladesText

        val buttonCalculate: Button = findViewById(R.id.calculateButton)
        buttonCalculate.setOnClickListener{
            val intent = Intent(this, CalculateActivity::class.java).apply{
                putExtra("rpm",trueRPM)
            }
            startActivity(intent)
        }


        setupChart()
        setupCamera()

        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            println("OpenCV failed to initialize!")
        } else {
            println("OpenCV successfully initialized!")
        }

        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener {
            refreshActivity()
        }
    }

    private fun refreshActivity() {
        recreate()
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
        val trueRpm = rpm / numberOfBlades // Adjust RPM by dividing by the number of blades

        rpmTextView.text = String.format("~Frekuensi: %.2f Hz", rpm)

        // Update RPM history for stability check
        updateRpmHistory(trueRpm)

        if (isStableRpm()) {
            lastStableRpm = (rpmHistory.average().toInt() / 10) * 10
            trueRPM = lastStableRpm
            stableRpmTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
        } else {
            stableRpmTextView.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        }

        stableRpmTextView.text = String.format("%d RPM", lastStableRpm)
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