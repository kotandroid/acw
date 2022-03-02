package org.techdown.photogallery.api

import com.google.gson.annotations.SerializedName
import org.techdown.photogallery.GalleryItem

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems:List<GalleryItem>
}