package com.acw.android.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.acw.android.criminalintent.Crime

//@Database - 이 클래스가 database를 나타낸다고 알려줌
@Database(entities=[Crime::class],version=1)
@TypeConverters(CrimeTypeConverters::class)

abstract class CrimeDatabase:RoomDatabase(){

    abstract fun crimeDao():CrimeDao

}