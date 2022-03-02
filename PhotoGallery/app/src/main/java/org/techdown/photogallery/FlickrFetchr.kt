package org.techdown.photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.techdown.photogallery.api.FlickrApi
import org.techdown.photogallery.api.FlickrResponse
import org.techdown.photogallery.api.PhotoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG="FlickrFetchr"

class FlickrFetchr {

    val flickrApi: FlickrApi
    init{
        val gson: Gson=GsonBuilder().registerTypeAdapter(PhotoResponse::class.java,PhotoDeserializer()).create()

        val retrofit :Retrofit=Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        flickrApi=retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        val flickrRequest: Call<PhotoResponse> = flickrApi.fetchPhotos()

        flickrRequest.enqueue(object :Callback<PhotoResponse>{

            override fun onFailure(call: Call<PhotoResponse>, t: Throwable) {
                Log.e(TAG,"Failed to fetch photo",t)
            }

            override fun onResponse(call: Call<PhotoResponse>, response: Response<PhotoResponse>) {
                //서버로부터 응답이 수신되면 실행되는 callback함수
                //Retrofit이 onResponse에 전달하는 response객체는 자신의 몸체에 결과 컨텐츠를 포함한다.
                //여기서는 API인터페이스의 fetchContents()가 Call<String>을 반환하므로
                //response.body()가 String type의 문자열을 반환한다.
                // Call객체는 요청을 초기 설정하는데 사용된 원래의 Call객체이다.
                // Call.execute()를 호출하면 요청이 실행되어 곧바로 Response객체가 반환된다.
                // 하지만 main thread에서의 network작업은 android에서 허용되지 않는다.

                Log.d(TAG,"Response recieved")
                val photoResponse:PhotoResponse?=response?.body()
                var galleryItem:List<GalleryItem> =  photoResponse?.galleryItems?: mutableListOf()
                galleryItem=galleryItem.filterNot{
                    it.url.isBlank()
                }//flickr의 이미지 중에는 urls필드 값이 없는 것도 있다. 따라서 여기서는 filterNot을 사용해서 그런 이미지 데이터를 걸러낸다.
                responseLiveData.value=galleryItem
            }
        })
        return responseLiveData
    }
    fun fetchPhotos2(): List<GalleryItem>{
        lateinit var ret:List<GalleryItem>
        val flickrRequest: Call<PhotoResponse> = flickrApi.fetchPhotos()
        flickrRequest.enqueue(object :Callback<PhotoResponse>{

            override fun onFailure(call: Call<PhotoResponse>, t: Throwable) {
                Log.e(TAG,"Failed to fetch photo",t)
            }

            override fun onResponse(call: Call<PhotoResponse>, response: Response<PhotoResponse>) {

                Log.d(TAG,"Response recieved")
                val photoResponse:PhotoResponse?=response?.body()
                var galleryItem:List<GalleryItem> =  photoResponse?.galleryItems?: mutableListOf()
                galleryItem=galleryItem.filterNot{
                    it.url.isBlank()
                }//flickr의 이미지 중에는 urls필드 값이 없는 것도 있다. 따라서 여기서는 filterNot을 사용해서 그런 이미지 데이터를 걸러낸다.
                ret=galleryItem
            }
        })
        return ret
    }




}