package org.techdown.photogallery

import android.content.Context
import android.preference.PreferenceManager

private const val PREF_SEARCH_QUERY="searchQuery"
private const val PREF_LAST_RESULT_ID="lastResultId"
private const val PREF_IS_POLLING="isPolling"

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
    fun getLastResultId(context:Context):String{
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_RESULT_ID,"")!!
    }
    fun setLastResultId(context:Context,lastResultId:String){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_LAST_RESULT_ID,lastResultId).apply()
    }
    fun isPolling(context:Context):Boolean{
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_IS_POLLING,false)
    }
    fun setPolling(context: Context,isOn:Boolean){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(PREF_IS_POLLING,isOn)
            .apply()
    }

}