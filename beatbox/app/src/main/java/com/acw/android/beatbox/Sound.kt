package com.acw.android.beatbox
private const val WAV=".wav"


class Sound(val assetPath:String, var soundId:Int?=null) {
    val name =assetPath.split("/").last().removeSuffix(WAV)
    //split.last 를 이용해서 경로 맨끝의 파일이름만 추출하고, removeSuffix로 확장자인 .wav를 제거한다.

}