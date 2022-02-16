package com.acw.android.beatbox

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG="MainViewModel"
class MainViewModel: ViewModel(){

        lateinit var beatBox: BeatBox


    override fun onCleared() {

        Log.d(TAG,"mainViewModel Clear")
        beatBox.release()
    }


}