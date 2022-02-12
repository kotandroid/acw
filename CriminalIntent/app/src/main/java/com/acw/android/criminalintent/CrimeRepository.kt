package com.acw.android.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import com.acw.android.criminalintent.database.CrimeDatabase
import com.acw.android.criminalintent.database.migration_1_2
import java.io.File
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

//repository pattern 이용
//repository에서 저장소에 접근하는 방법을 결정
//ui는 repository의 fun을 call할 뿐 내부 logic은 모른다.




private const val DATABASE_NAME="crime-database"
class CrimeRepository private constructor(context:Context){


    //database initialize
    private val database:CrimeDatabase= Room.databaseBuilder(
        context.applicationContext, //database가 사용될 context(application의 context)
        CrimeDatabase::class.java, // room으로 생성하고자하는 database class
        DATABASE_NAME // db이름
    ).addMigrations(migration_1_2).build()

    private val crimeDao=database.crimeDao()
    private val executor= Executors.newSingleThreadExecutor() // 독립된 실행공간을 보장받는 thread 생성
    private val filesDir=context.applicationContext.filesDir


    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID):LiveData<Crime?> = crimeDao.getCrime(id)
    fun updateCrime(crime:Crime){
        executor.execute{
            crimeDao.updateCrime(crime)
        }
    }
    fun addCrime(crime:Crime){
        executor.execute{
            crimeDao.addCrime(crime)
        }
    }

    fun getPhotoFile(crime : Crime):File=File(filesDir,crime.photoFileName)
    //photoFileName이 참조하는 파일의 경로를 return 한다.

    companion object{// singleton pattern 으로 Repository 초기화하기
        private var INSTANCE:CrimeRepository?=null

        //singleton 패턴 사용하여 repostory는 app내에서 하나의 instance만 유지시킨다.
        fun initialize(context:Context){
            if(INSTANCE==null){
                INSTANCE= CrimeRepository(context);
            }

        }
        fun get():CrimeRepository{
            return INSTANCE?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}