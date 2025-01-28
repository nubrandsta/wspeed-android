package com.flop.wspeed

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flop.wspeed.databinding.ActivityMateriBinding

class MateriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMateriBinding

    private var index_location : Int = 0
    //map title and content
    val materi_titles : List<Int> = listOf(
        R.string.rpm_title,
        R.string.wspeed_title,
        R.string.tachometer_title,
        R.string.frequency_title
    )

    val materi_contents : List<Int> = listOf(
        R.string.rpm_body,
        R.string.wspeed_body,
        R.string.tachometer_body,
        R.string.frequency_body
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMateriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.fabPrev.visibility = View.GONE
        binding.fabNext.visibility = View.GONE

        showMateri()

        binding.fabPrev.setOnClickListener {
            index_location--
            showMateri()
        }

        binding.fabNext.setOnClickListener {
            index_location++
            showMateri()
        }


    }

    private fun showMateri(){
        binding.titleText.text = getString(materi_titles[index_location])
        binding.rpmBodyText.text = getString(materi_contents[index_location])

        if(index_location == 0){
            binding.fabPrev.visibility = View.GONE
        }else{
            binding.fabPrev.visibility = View.VISIBLE
            }
        if(index_location == materi_titles.size - 1){
            binding.fabNext.visibility = View.GONE
        }else{
            binding.fabNext.visibility = View.VISIBLE
        }
    }
}