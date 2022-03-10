package org.techdown.photogallery

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class GalleryItem (
    var title:String="",
    var id:String="",
    var url:String="",
    var owner:String=""
){
    val photoPageUri:Uri
    get(){
        return Uri.parse("https://www.flickr.com/photos/")
            .buildUpon()
            .appendPath(owner)
            .appendPath(id)
            .build()
    }
}
