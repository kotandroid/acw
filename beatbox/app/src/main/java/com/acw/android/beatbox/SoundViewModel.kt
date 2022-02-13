package com.acw.android.beatbox

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class SoundViewModel(private val beatBox: BeatBox):BaseObservable() {


    var sound :Sound?=null
    set(sound){
        field=sound
        notifyChange()//바인딩 속성값이 변경되었음을 바인딩클래스(ListItemSoundBinding)에 알린다.
    }

    @get:Bindable
    val title:String?
            get()=sound?.name


    fun onButtonClicked() {
        sound?.let {
            beatBox.play(it)
        }
    }
}