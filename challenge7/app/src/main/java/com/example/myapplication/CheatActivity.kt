package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

private const val ON_SAVE_TAG="onsaveinstatance"
private const val EXTRA_ANSWER_IS_TRUE="answer_is_true"
const val EXTRA_ANSWER_SHOWN="answer_shown"
private const val CHEAT_FLAG="cheat"
private const val CHEAT_COUNT="count_cheat"

class CheatActivity : AppCompatActivity() {
    private lateinit var answerTextView:TextView
    private lateinit var showAnswerButton:Button
    private lateinit var cheatCountView:TextView

    private  var cheated=false
    private var answerIsTrue=false
    private var cheatCount=3

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        Log.d(ON_SAVE_TAG,"onsave")
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean(CHEAT_FLAG,cheated)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)


        val currentCheat=savedInstanceState?.getBoolean(CHEAT_FLAG,false)?:false
        cheated=currentCheat // SIS 복구

        val restoreCheatCount=intent.getIntExtra(CHEAT_COUNT,999)
        cheatCount=restoreCheatCount
        Log.d(ON_SAVE_TAG,""+cheatCount)

        cheatCountView=findViewById(R.id.cheat_count)
        cheatCountView.setText("남은 hint 수 :"+cheatCount)

        answerIsTrue=intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false )
        answerTextView=findViewById(R.id.answer_text_view)
        showAnswerButton=findViewById(R.id.show_answer_button)
        showAnswerButton.setOnClickListener{
            if(cheatCount==0){
                Toast.makeText(this,"hint 횟수를 모두 소진하셨습니다.",Toast.LENGTH_SHORT).show()
            }
            else{
                val answerText=when{
                    answerIsTrue->R.string.true_button
                    else->R.string.false_button
                }
                cheatCount--
                answerTextView.setText(answerText)
                cheated=true
                setAnswerShownResult(cheated)
                cheatCountView.setText("남은 hint 수 :"+cheatCount)
            }

        }
    }
    private fun setAnswerShownResult(isAnswerShown:Boolean){
        val data=Intent().apply{
            putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown)
            putExtra(CHEAT_COUNT,cheatCount)
        }
        setResult(Activity.RESULT_OK,data)
    }
    companion object{
        fun newIntent(packageContext: Context, answerIsTrue:Boolean,cheatCount:Int):Intent{
            return Intent(packageContext,CheatActivity::class.java).apply{
                putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue)
                putExtra(CHEAT_COUNT,cheatCount)
            }
        }
    }
}