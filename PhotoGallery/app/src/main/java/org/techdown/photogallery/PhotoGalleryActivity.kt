package org.techdown.photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)


        val isFragmentContainerEmpty= (savedInstanceState==null)
        //장치 변경 등이 있을시에 다시 생성하지 않고 보존.
        if(isFragmentContainerEmpty){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer,PhotoGalleryFragment.newInstance())
                .commit()
        }

    }
}