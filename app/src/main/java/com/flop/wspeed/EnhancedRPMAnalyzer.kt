package com.flop.wspeed

import kotlin.math.pow

class EnhancedRPMAnalyzer {
    private val intensityValues = mutableListOf<Float>()
    private val timestamps = mutableListOf<Long>()

    private val smoothedIntensityValues = mutableListOf<Float>()
    private val smoothingWindowSize = 5 // Smoothing window for moving average

    fun processIntensity(intensity: Float, timestamp: Long): Float? {
        // Update intensity and timestamp history
        intensityValues.add(intensity)
        timestamps.add(timestamp)

        // Maintain sliding window for smoothing
        if (intensityValues.size > 100) {
            intensityValues.removeAt(0)
            timestamps.removeAt(0)
        }

        // Smooth the data using moving average
        val smoothedValue = calculateMovingAverage(intensity)
        smoothedIntensityValues.add(smoothedValue)

        // Maintain sliding window for smoothed values
        if (smoothedIntensityValues.size > 100) {
            smoothedIntensityValues.removeAt(0)
        }

        if (smoothedIntensityValues.size < 3) return null // Not enough data to analyze

        // Detect extrema (peaks and valleys)
        val extremaIndices = detectExtrema(smoothedIntensityValues)

        // Calculate intervals between extrema
        val intervals = extremaIndices.zipWithNext { a, b -> timestamps[b] - timestamps[a] }
        if (intervals.isEmpty()) return null

        // Determine the average interval
        val meanInterval = intervals.average().toFloat()
        val frequency = if (meanInterval > 0) 1000f / meanInterval else 0f // Frequency in Hz

        return frequency * 60f // Convert to RPM
    }

    private fun calculateMovingAverage(value: Float): Float {
        val values = intensityValues.takeLast(smoothingWindowSize)
        return if (values.isNotEmpty()) values.average().toFloat() else value
    }

    private fun detectExtrema(values: List<Float>): List<Int> {
        val extremaIndices = mutableListOf<Int>()
        for (i in 1 until values.size - 1) {
            val prev = values[i - 1]
            val curr = values[i]
            val next = values[i + 1]

            if (curr > prev && curr > next) extremaIndices.add(i) // Peak
            if (curr < prev && curr < next) extremaIndices.add(i) // Valley
        }
        return extremaIndices
    }
}





