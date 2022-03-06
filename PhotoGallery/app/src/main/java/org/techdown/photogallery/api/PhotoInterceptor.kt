package org.techdown.photogallery.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


private const val API_KEY="b11e0665aeb862291e30d630d2f23802"
class PhotoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest : Request = chain.request() // 원래의 요청을 가져온다.

        val newUrl:HttpUrl=originalRequest.url().newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("format","json")
            .addQueryParameter("nojsoncallback","1")
            .addQueryParameter("extras","url_s")
            .addQueryParameter("safesearch","1")
            .build()
        //그리고 Request.url으로 원래의 url을 가져온 후 builder로 각종 매개변수들을 추가한다.

        val newRequest:Request=originalRequest.newBuilder()
            .url(newUrl)
            .build()
            //새롭게 변경한 url로 다시 request객체를 생성한다.


        return chain.proceed(newRequest)
        //요청을 전송하고 응답을 나타내는 response 객체를 받는다.
    }
}