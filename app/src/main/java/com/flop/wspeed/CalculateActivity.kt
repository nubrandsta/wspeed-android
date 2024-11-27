package com.flop.wspeed

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flop.wspeed.databinding.ActivityCalculateBinding
import com.flop.wspeed.databinding.ActivityHomeBinding

class CalculateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rpm = intent.getIntExtra("rpm",0)
        val rpm_text = rpm.toString() + " RPM"
        val rpm_formula = "2 π x " + rpm.toString()

        binding.textRpm.text = rpm_text
        binding.textFormula2B.text = rpm_formula

        val angularSpeed = (2 * Math.PI * rpm) / 60

        val angular_speed_text = String.format("ω = %.3f rad/s", angularSpeed)
        val result_text = String.format("%.3f rad/s", angularSpeed)

        binding.textFormula3A.text = angular_speed_text
        binding.textWspeed.text = result_text
    }
}