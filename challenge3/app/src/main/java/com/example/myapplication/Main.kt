package com.example.myapplication

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.Question

private const val TAG="MainActivity"
class Main : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton:ImageButton
    private lateinit var previousButton:ImageButton

    private lateinit var questionTextView:TextView
    private val questionBank=listOf(
            Question(R.string.question_australia,true),
            Question(R.string.question_oceans,true),
            Question(R.string.question_mideast,false),
            Question(R.string.question_africa,false),
            Question(R.string.question_americas,true),
            Question(R.string.question_asia,true)
    )
    private val btnEnable=arrayOfNulls<Boolean>(questionBank.size)
    private val numofquiz=questionBank.size
    private var numofcorrect:Int=0

    private var currentIndex=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton=findViewById(R.id.next_button)
        previousButton=findViewById(R.id.previous_button)
        questionTextView=findViewById(R.id.question_text_view)

        var toast:Toast

        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }
        falseButton.setOnClickListener { view: View ->
             checkAnswer(false)
        }
        nextButton.setOnClickListener{
            currentIndex=(currentIndex+1)%questionBank.size
            updateQuestion()
        }
        previousButton.setOnClickListener{
            currentIndex=if(currentIndex==0){questionBank.size-1} else{(currentIndex-1)%questionBank.size}
            updateQuestion()
        }
        questionTextView.setOnClickListener { view: View ->
            currentIndex=(currentIndex+1)%questionBank.size
            updateQuestion()
        }
        updateQuestion()
    }


    private fun updateQuestion(){
        val questionTextResId=questionBank[currentIndex].textResId
        questionTextView.setText(questionTextResId)
        if(btnEnable[currentIndex]==true){
            trueButton.setEnabled(false)
            falseButton.setEnabled(false)
        }
        else{
            trueButton.setEnabled(true)
            falseButton.setEnabled(true)
        }
    }
    private fun checkAnswer(userAnswer:Boolean){
        val correctAnswer=questionBank[currentIndex].answer
        val messageResId :String
        if(userAnswer==correctAnswer){
            messageResId="correct"
            trueButton.setEnabled(false)
            falseButton.setEnabled(false)
            btnEnable[currentIndex]=true
            numofcorrect++
            Log.d(TAG,""+numofcorrect)
        }
        else{
            messageResId="wrong"
        }

        Toast.makeText(this,messageResId,Toast.LENGTH_SHORT).show()
        if(currentIndex==numofquiz-1){
            Toast.makeText(this,"정답율 : "+(numofcorrect.toFloat()/numofquiz)*100+"%",Toast.LENGTH_SHORT).show()
        }


    }
}