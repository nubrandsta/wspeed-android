package com.flop.wspeed

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flop.wspeed.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSpeed.setOnClickListener{
            showDialogWSpeed()

        }

        binding.btnTach.setOnClickListener{
            showDialogRPM()

        }

        binding.btnFreq.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

    }
    private fun showDialogRPM() {
        // Inflate the dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_blades, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Get references to views in the dialog
        val inputNumber: EditText = dialogView.findViewById(R.id.et_blades)
        val okButton: Button = dialogView.findViewById(R.id.nextButton)

        okButton.setOnClickListener {
            // Retrieve the number input
            val number = inputNumber.text.toString().toIntOrNull()

            if (number != null) {
                // Close the dialog
                dialog.dismiss()

                // Start the new activity and pass the integer
                val intent = Intent(this, RPMActivity::class.java).apply {
                    putExtra("number", number)
                }
                startActivity(intent)
            } else {
                inputNumber.error = "Please enter a valid number"
            }
        }

        dialog.show()
    }

    private fun showDialogWSpeed() {
        // Inflate the dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_blades, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Get references to views in the dialog
        val inputNumber: EditText = dialogView.findViewById(R.id.et_blades)
        val okButton: Button = dialogView.findViewById(R.id.nextButton)

        okButton.setOnClickListener {
            // Retrieve the number input
            val number = inputNumber.text.toString().toIntOrNull()

            if (number != null) {
                // Close the dialog
                dialog.dismiss()

                // Start the new activity and pass the integer
                val intent = Intent(this, WSpeedActivity::class.java).apply {
                    putExtra("number", number)
                }
                startActivity(intent)
            } else {
                inputNumber.error = "Please enter a valid number"
            }
        }

        dialog.show()
    }
}