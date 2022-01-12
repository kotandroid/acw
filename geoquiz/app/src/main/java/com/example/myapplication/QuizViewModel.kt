package com.example.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.lang.reflect.Array.get


private const val TAG="QuizViewModel"

class QuizViewModel: ViewModel() {
    var currentIndex=0

    private val questionBank=listOf(
        Question(R.string.question_australia,true),
        Question(R.string.question_oceans,true),
        Question(R.string.question_mideast,false),
        Question(R.string.question_africa,false),
        Question(R.string.question_americas,true),
        Question(R.string.question_asia,true)
    )
    val cheatArr=Array<Boolean>(questionBank.size){false}

    val currentQuestionAnswer:Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText:Int
        get() =questionBank[currentIndex].textResId

    fun moveToNext(){
        currentIndex=(currentIndex+1)% questionBank.size
    }
    fun moveToPrevious(){
        currentIndex=if(currentIndex==0){questionBank.size-1} else{(currentIndex-1)%questionBank.size}
    }
}