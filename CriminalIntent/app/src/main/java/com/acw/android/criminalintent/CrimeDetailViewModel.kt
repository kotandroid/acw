package com.acw.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CrimeDetailViewModel(): ViewModel() {
    private val crimeRepository=CrimeRepository.get()
    private val crimeIdLiveData= MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?>?=
            Transformations.switchMap(crimeIdLiveData) {
                crimeId->crimeRepository.getCrime(crimeId)
            }
            // crimeIdLiveData를 이용해 범죄 데이터를 db로부터 가져온 뒤 Crime객체를 갖는 LiveData로 반환한다.

    fun loadCrime(crimeId:UUID){
        crimeIdLiveData.value=crimeId
    }
    fun saveCrime(crime:Crime){
        crimeRepository.updateCrime(crime)
    }
}