package com.acw.android.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.acw.android.criminalintent.Crime

//@Database - 이 클래스가 database를 나타낸다고 알려줌
@Database(entities=[Crime::class],version=2)
@TypeConverters(CrimeTypeConverters::class)

abstract class CrimeDatabase:RoomDatabase(){

    abstract fun crimeDao():CrimeDao

}
val migration_1_2=object: Migration(1,2){
    override fun migrate(database:SupportSQLiteDatabase){
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT '' "
        )

    }
}