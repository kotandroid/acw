package com.acw.android.beatbox

import android.util.Log
import android.widget.SeekBar
import androidx.databinding.Bindable
import androidx.databinding.BaseObservable
private const val TAG="MainBinding"

class MainBindingViewModel(private val beatBox: BeatBox) :BaseObservable(){



    @get:Bindable
    var playSpeed :Int?=0
    set(value){
        Log.d(TAG,"playSpeed")
        field=value
        beatBox.speed=value!!.toFloat()
        notifyPropertyChanged(BR.playSpeed)
    }



}