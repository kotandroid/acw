package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView


private const val EXTRA_ANSWER_IS_TRUE="answer_is_true"
const val EXTRA_ANSWER_SHOWN="answer_shown"
private const val CHEAT_FLAG="cheat"

class CheatActivity : AppCompatActivity() {
    private lateinit var answerTextView:TextView
    private lateinit var showAnswerButton:Button
    private  var cheated=false
    private var answerIsTrue=false

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean(CHEAT_FLAG,cheated)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        val currentCheat=savedInstanceState?.getBoolean(CHEAT_FLAG,false)?:false
        cheated=currentCheat

        answerIsTrue=intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false )
        answerTextView=findViewById(R.id.answer_text_view)
        showAnswerButton=findViewById(R.id.show_answer_button)
        showAnswerButton.setOnClickListener{
            val answerText=when{
                answerIsTrue->R.string.true_button
                else->R.string.false_button
            }
            answerTextView.setText(answerText)
            cheated=true
            setAnswerShownResult(cheated)
        }
    }
    private fun setAnswerShownResult(isAnswerShown:Boolean){
        val data=Intent().apply{
            putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown)
        }
        setResult(Activity.RESULT_OK,data)
    }
    companion object{
        fun newIntent(packageContext: Context, answerIsTrue:Boolean):Intent{
            return Intent(packageContext,CheatActivity::class.java).apply{
                putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue)
            }
        }
    }
}