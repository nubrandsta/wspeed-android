package com.flop.wspeed


import java.util.LinkedList
import kotlin.math.abs

class RPMAnalyzer(private val timeWindowMillis: Long = 3000) {

    private val timestamps = LinkedList<Long>() // Stores peak timestamps
    private var lastIntensity: Float? = null // Last processed intensity
    private var lastTimestamp: Long? = null // Last peak timestamp

    fun processIntensity(intensity: Float, timestamp: Long): Float? {
        // Check if it's a peak
        if (lastIntensity != null && isPeak(intensity, lastIntensity!!)) {
            // Ensure there's a minimum interval between peaks (e.g., debounce)
            if (lastTimestamp == null || timestamp - lastTimestamp!! > 100) {
                timestamps.add(timestamp)
                lastTimestamp = timestamp
            }
        }

        // Remove timestamps outside the sliding window
        while (timestamps.isNotEmpty() && timestamp - timestamps.first > timeWindowMillis) {
            timestamps.removeFirst()
        }

        // Update last intensity
        lastIntensity = intensity

        // Calculate RPM if we have peaks
        return if (timestamps.size > 1) calculateRPM() else null
    }

    private fun isPeak(current: Float, previous: Float): Boolean {
        return current > previous + 10 // Example threshold
    }

    private fun calculateRPM(): Float {
        val durationInSeconds = timeWindowMillis / 1000.0f
        return (timestamps.size.toFloat() / durationInSeconds) * 60
    }
}
