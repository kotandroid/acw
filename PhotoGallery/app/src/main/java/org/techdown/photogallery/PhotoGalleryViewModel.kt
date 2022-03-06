package org.techdown.photogallery

import android.app.Application
import androidx.lifecycle.*
import java.lang.Appendable

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
    private val flickrFetchr=FlickrFetchr()
    val galleryItemLiveData :LiveData<List<GalleryItem>>
    private val mutableSearchTerm=MutableLiveData<String>()

    val searchTerm:String
        get()=mutableSearchTerm.value?:""


    init {
        mutableSearchTerm.value = QueryPreferences.getStoreQuery(app)
        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            if(searchTerm.isBlank()){
                flickrFetchr.fetchPhotos()
            }else{
                flickrFetchr.searchPhotos(searchTerm)
           }
        }
    }
    fun fetchPhotos(query:String=""){
        QueryPreferences.setStoreQuery(app,query)
        mutableSearchTerm.value=query
    }

}