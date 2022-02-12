package com.acw.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class CrimeDetailViewModel(): ViewModel() {
    private val crimeRepository=CrimeRepository.get()
    private val crimeIdLiveData= MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?>?=
            Transformations.switchMap(crimeIdLiveData) {
                crimeId->crimeRepository.getCrime(crimeId)
            }
            // crime fragment가 생성될 때 load Crime을 하는데 이 때 crimeIdLiveData가 설정되는 것을 볼 수 있다.
            // crimeIdLiveData를 이용해 범죄 데이터를 db로부터 가져온 뒤 Crime객체를 갖는 LiveData로 반환한다.

    fun loadCrime(crimeId:UUID){
        crimeIdLiveData.value=crimeId
    }
    fun saveCrime(crime:Crime){
        crimeRepository.updateCrime(crime)
    }
    fun getPhotoFile(crime:Crime): File {
        return crimeRepository.getPhotoFile(crime)
    }
}