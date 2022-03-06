package org.techdown.photogallery

import android.content.Context
import android.preference.PreferenceManager

private const val PREF_SEARCH_QUERY="searchQuery"

object QueryPreferences {

    fun getStoreQuery(context:Context):String{
        val prefs= PreferenceManager.getDefaultSharedPreferences(context) // 기본이름과 퍼미션을 갖는 기본 shared preferences 인스턴스 반환
        return prefs.getString(PREF_SEARCH_QUERY,"")!!
    }
    fun setStoreQuery(context:Context,query:String){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SEARCH_QUERY,query)
            .apply()
    }
}