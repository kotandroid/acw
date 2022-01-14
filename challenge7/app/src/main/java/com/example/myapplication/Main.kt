package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.Question

private const val TAG="MainActivity"
private const val KEY_INDEX="index"

private const val CHEAT_COUNT="count_cheat"
private const val REQUEST_CODE_CHEAT=0

class Main : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton:ImageButton
    private lateinit var previousButton:ImageButton
    private lateinit var cheatButton:Button

    private lateinit var questionTextView:TextView
    private var cheatCount=3
    private val quizViewModel:QuizViewModel by lazy{
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode!= Activity.RESULT_OK){
            return
        }
        if(requestCode== REQUEST_CODE_CHEAT){
            cheatCount=data?.getIntExtra(CHEAT_COUNT,0)?:0
            Log.d(TAG,""+cheatCount)
            quizViewModel.cheatArr[quizViewModel.currentIndex]=data?.getBooleanExtra(EXTRA_ANSWER_SHOWN,false)?:false
        }

    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.d(TAG,"onsavedInstancestate")
        savedInstanceState.putInt(KEY_INDEX,quizViewModel.currentIndex)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex= savedInstanceState?.getInt(KEY_INDEX,0) ?:0

        quizViewModel.currentIndex=currentIndex

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton=findViewById(R.id.next_button)
        cheatButton=findViewById(R.id.cheat_button)
        previousButton=findViewById(R.id.previous_button)
        questionTextView=findViewById(R.id.question_text_view)



        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }
        falseButton.setOnClickListener { view: View ->
             checkAnswer(false)
        }
        nextButton.setOnClickListener{
            quizViewModel.moveToNext()
            updateQuestion()
        }
        cheatButton.setOnClickListener{
            val answerIsTrue=quizViewModel.currentQuestionAnswer
            val intent=CheatActivity.newIntent(this,answerIsTrue,cheatCount)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }
        previousButton.setOnClickListener{
            quizViewModel.moveToPrevious()
            updateQuestion()
        }
        questionTextView.setOnClickListener { view: View ->
            quizViewModel.moveToNext()
            updateQuestion()
        }
        updateQuestion()
    }


    private fun updateQuestion(){
      //  Log.d(TAG,"updating quiestion text",Exception())
        val questionTextResId=quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }
    private fun checkAnswer(userAnswer:Boolean){
        val correctAnswer=quizViewModel.currentQuestionAnswer
        val messageResId=when{
            quizViewModel.cheatArr[quizViewModel.currentIndex]!! ->R.string.judgement_toast
            userAnswer==correctAnswer->R.string.correct_toast
            else->R.string.incorrect_toast
        }
        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show()
    }
}