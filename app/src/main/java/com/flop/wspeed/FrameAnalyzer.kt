package com.flop.wspeed

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class FrameAnalyzer(
    private val onRPMCalculated: (Float) -> Unit,
    private val onIntensityCalculated: (Float) -> Unit
) : ImageAnalysis.Analyzer {

    private val rpmAnalyzer = EnhancedRPMAnalyzer()

    override fun analyze(image: ImageProxy) {
        try {
            val rgbMat = convertYUVToRGB(image) // Convert to RGB
            val grayMat = Mat()
            Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY) // Grayscale conversion

            // Define ROI
            val roi = Rect(
                grayMat.width() / 2 - 25,
                grayMat.height() / 2 - 25,
                50, 50
            )
            val roiMat = grayMat.submat(roi)

            // Calculate the mean intensity of the ROI
            val mean = Core.mean(roiMat)
            val intensity = mean.`val`[0].toFloat()

            // Pass raw intensity to the graph
            onIntensityCalculated(intensity)

            // Pass intensity to RPMAnalyzer
            val currentTime = System.currentTimeMillis()
            val rpm = rpmAnalyzer.processIntensity(intensity, currentTime)

            // Send RPM to the UI if calculated
            if (rpm != null) {
                onRPMCalculated(rpm)
            }

            // Release Mats to avoid memory leaks
            rgbMat.release()
            grayMat.release()
            roiMat.release()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            image.close() // Important: Close the ImageProxy to free resources
        }
    }

    private fun convertYUVToRGB(image: ImageProxy): Mat {
        val nv21 = yuvToNv21(image)
        val mat = Mat(image.height, image.width, CvType.CV_8UC1)
        mat.put(0, 0, nv21)

        // Convert NV21 to RGB Mat
        val rgbMat = Mat()
        Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_YUV2RGB_NV21)
        return rgbMat
    }

    private fun yuvToNv21(image: ImageProxy): ByteArray {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        val uvIndex = ySize
        for (i in 0 until uSize step 2) {
            nv21[uvIndex + i] = vBuffer[i / 2]
            nv21[uvIndex + i + 1] = uBuffer[i / 2]
        }
        return nv21
    }
}


