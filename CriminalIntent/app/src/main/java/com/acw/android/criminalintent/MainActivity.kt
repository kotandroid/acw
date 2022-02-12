package com.acw.android.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import java.io.File
import java.util.*

private const val TAG="MainActivity"

class MainActivity : AppCompatActivity(),CrimeListFragment.Callbacks,CrimeFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)


        if (currentFragment == null) {

            val fragment = CrimeListFragment.newInstance()
                supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
            //crimelistfragment를 생성해서 fragment_container에 transaction
        }
    }
    override fun onCrimeSelected(crimeId: UUID){
        Log.d(TAG,"MainActivity.onCrimeSelected: $crimeId")
       // val fragment=CrimeFragment.newInstance(crimeId)
        val fragment=CrimeFragment(crimeId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .addToBackStack(null) // backStack에 추가하여 crimelist를 다시 역 transaction함과 동시에 viewmodel도 유지시킬 수 있다.
            .commit()
    }

    override fun onPhotoSelected(photoFile: File) {

        val fragment=DialogFragment.newInstance(photoFile)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .addToBackStack(null)
            .commit()

    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.fragment_crime_list,menu)
        return super.onCreateOptionsMenu(menu)

    }
}