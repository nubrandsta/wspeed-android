package com.flop.wspeed

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flop.wspeed.databinding.ActivityQuizTestBinding
import com.flop.wspeed.quiz.quizList

class QuizTestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityQuizTestBinding
    val quizlist = quizList
    var index = 0

    var correct = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityQuizTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnNext.visibility = android.view.View.GONE
        binding.answerTitle.visibility = android.view.View.GONE
        binding.titleText.visibility = android.view.View.GONE

        correct = 0

        loadQuestion()

    }

    private fun loadQuestion(){
        binding.questionNumText.text = "Soal No. ${index + 1}"
        binding.questionTitleText.text = quizlist[index].question

        binding.btnOption1.text = quizlist[index].option1
        binding.btnOption2.text = quizlist[index].option2
        binding.btnOption3.text = quizlist[index].option3
        binding.btnOption4.text = quizlist[index].option4

        binding.btnOption1.setOnClickListener {
            checkAnswer(1)
        }
        binding.btnOption2.setOnClickListener {
            checkAnswer(2)
        }
        binding.btnOption3.setOnClickListener {
            checkAnswer(3)
        }
        binding.btnOption4.setOnClickListener {
            checkAnswer(4)
        }

        binding.btnOption1.isEnabled = true
        binding.btnOption2.isEnabled = true
        binding.btnOption3.isEnabled = true
        binding.btnOption4.isEnabled = true

        binding.btnOption1.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.button_secondary, null))
        binding.btnOption1.setTextColor(resources.getColor(R.color.primary_blue))
        binding.btnOption2.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.button_secondary, null))
        binding.btnOption2.setTextColor(resources.getColor(R.color.primary_blue))
        binding.btnOption3.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.button_secondary, null))
        binding.btnOption3.setTextColor(resources.getColor(R.color.primary_blue))
        binding.btnOption4.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.button_secondary, null))
        binding.btnOption4.setTextColor(resources.getColor(R.color.primary_blue))

        binding.answerText.visibility = android.view.View.GONE
        binding.answerTitle.visibility = android.view.View.GONE
        binding.btnNext.visibility = android.view.View.GONE


    }

    private fun checkAnswer(answer: Int){

        binding.btnOption1.isEnabled = false
        binding.btnOption2.isEnabled = false
        binding.btnOption3.isEnabled = false
        binding.btnOption4.isEnabled = false

        when(answer){
            1 -> {
                binding.btnOption1.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.button_primary, null))
                binding.btnOption1.setTextColor(resources.getColor(R.color.white))
            }
            2 -> {
                binding.btnOption2.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.button_primary, null))
                binding.btnOption2.setTextColor(resources.getColor(R.color.white))
            }
            3 -> {
                binding.btnOption3.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.button_primary, null))
                binding.btnOption3.setTextColor(resources.getColor(R.color.white))
            }
            4 -> {
                binding.btnOption4.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.button_primary, null))
                binding.btnOption4.setTextColor(resources.getColor(R.color.white))
            }
        }

        if(answer == quizlist[index].correctAnswer){
            binding.answerTitle.text = "Jawaban Benar!"
            binding.answerTitle.setTextColor(resources.getColor(R.color.green))
            binding.answerText.visibility = android.view.View.GONE

            correct++

        } else {
            binding.answerTitle.text = "Jawaban Salah!"
            binding.answerTitle.setTextColor(resources.getColor(R.color.red))
            binding.answerText.visibility = android.view.View.VISIBLE
            when(quizList[index].correctAnswer){
                1 -> binding.answerText.text = "Jawaban Benar: " + quizlist[index].option1
                2 -> binding.answerText.text = "Jawaban Benar: " + quizlist[index].option2
                3 -> binding.answerText.text = "Jawaban Benar: " + quizlist[index].option3
                4 -> binding.answerText.text = "Jawaban Benar: " + quizlist[index].option4
            }
        }

        binding.answerTitle.visibility = android.view.View.VISIBLE
        binding.btnNext.visibility = android.view.View.VISIBLE


        binding.btnNext.setOnClickListener{

            if(index+1 >= quizlist.size){
                index = 0
                showFinish()
                Toast.makeText(this, "Anda telah menyelesaikan quiz!", Toast.LENGTH_SHORT).show()

            }
            else{
                index++
                loadQuestion()
            }

        }
    }

    private fun showFinish() {

        val totalQuestions = quizlist.size
        val correctAnswers = correct
        val correctText = "$correctAnswers dari $totalQuestions"

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_quiz)
        dialog.findViewById<TextView>(R.id.text_content).text = correctText

        dialog.findViewById<Button>(R.id.btn_ok).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        dialog.show()

    }
}