package org.techdown.photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {

    /*
Call은 retrofit의 interface이다. 기본적으로 모든 Retrofit 웹 요청은 retrofit2.call 객체를 반환한다. Call객체는 실행할 수 있는 하나의 웹 요청을
나타내며 , 이것이 실행되면 이것에 대응대는 웹 요청이 생성된다. Call의 제너릭 타입 매개변수로 지정한 타입은(여기서는 FlickrResponse)
Retrofit이 Http응답을 역직렬화하는 data type을 나타낸다. 기본적으로 Retrofit은 HTTP응답을 OkHttp.ResponseBody 타입으로 역직렬화하는데
data type을 위와 같이 지정하게 되면 HTTP응답이 지정한 type으로 역직렬화 된다.

*/

    @GET("services/rest?method=flickr.interestingness.getList")
    fun fetchPhotos(): Call<PhotoResponse>

    @GET
    fun fetchUrlBytes(@Url url:String):Call<ResponseBody>

    @GET("services/rest?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query:String):Call<PhotoResponse>
    //query annotation은 url끝에 쿼리 매개변수를 동적으로 추가할 수 있도록 한다.


}