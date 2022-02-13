package com.acw.android.beatbox

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log
import java.lang.Exception

private const val TAG="BeatBox"
private const val SOUNDS_FOLDER="sample_sounds"
private const val MAX_SOUNDS=5 // 현재 시점에 재생할 수 있는 최대 음원 갯수



class BeatBox(private val assets: AssetManager) {
    val sounds:List<Sound>
    var speed=1.0f

    private val soundPool=SoundPool.Builder().setMaxStreams(MAX_SOUNDS).build() // 5개 음원이 켜지고 있을 때 6개째를 또 틀려하면 자동으로 soundpool이 제일 예전의 음원들을 중단시킨다.
    init{
        sounds=loadSounds()
    }


    //Sound의 list를 받아서 Sound 클래스
    private fun loadSounds():List<Sound>{
        val soundNames:Array<String>
        try{
             soundNames=assets.list(SOUNDS_FOLDER)!!

        }
        catch (e:Exception){
            Log.e(TAG,"Could not list asset",e)
            return emptyList()
        }
        val sounds= mutableListOf<Sound>()
        soundNames.forEach { filename->
            val assetPath="$SOUNDS_FOLDER/$filename"
            val sound=Sound(assetPath)
            try{
                load(sound)
                sounds.add(sound)
            }
            catch (ioe:Exception){
                Log.e(TAG,"Could not load sound $filename",ioe)
            }
        }
        return sounds
    }
    private fun load(sound:Sound){
        val afd:AssetFileDescriptor=assets.openFd(sound.assetPath)
        val soundId=soundPool.load(afd,1) // 재생할 음원을 soundpool에 load한다. load는 정수 id를 return 한다.
        sound.soundId=soundId
    }
    fun play(sound:Sound){
        sound.soundId?.let{
            soundPool.play(it,1.0f,1.0f,1,0,speed)
            //음원id, 왼쪽볼륨, 오른쪽 볼륨, 스트림 우선순위(0부터),반복재생 여부,재생속도
        }
    }
    fun release(){
        soundPool.release()
    }

}