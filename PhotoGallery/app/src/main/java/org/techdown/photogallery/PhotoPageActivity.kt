package org.techdown.photogallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

private lateinit var fragment : PhotoPageFragment

class PhotoPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_page)

        val fm =supportFragmentManager
        val currentFragment=fm.findFragmentById(R.id.fragment_container)

        if(currentFragment==null){
            fragment=PhotoPageFragment.newInstance(intent.data!!)
            fm.beginTransaction()
                .add(R.id.fragment_container,fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        if(fragment==null){
            super.onBackPressed()
        }else if(!fragment.getBackinfoOfWebView()){
            super.onBackPressed()
        }else{
            fragment.goBackWebView()
        }

    }
    
    companion object{
        fun newIntent(context: Context,photoPageUri: Uri): Intent {
            return Intent(context, PhotoPageActivity::class.java).apply{
                data=photoPageUri
            }
        }
    }
}