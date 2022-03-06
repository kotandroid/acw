package org.techdown.photogallery

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentHashMap

private const val TAG="ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD=0 // 다운로드 요청 시 메시지를 식별하는데 사용

class ThumbnailDownloader<in T>(private val responseHandler:Handler,private val onThumbnaildDownloaded :(T, Bitmap)->Unit)
    : HandlerThread(TAG) {
    private var hasQuit=false
    private lateinit var requestHandler:Handler
    private val requestMap=ConcurrentHashMap<T,String>() //핸들러의 참조를 보존
    private val flickrFetchr=FlickrFetchr()

    val fragmentLifecycleObserver:LifecycleObserver=
        object:LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun setup(){
                Log.i(TAG,"Starting background thread")
                start()
                looper//Looper instance를 참조하는 속성.
            }
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun tearDown(){
                Log.i(TAG,"Destroying background thread")
                quit()
            }
        }

    val viewLifecycleObserver:LifecycleObserver=
        object:LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun clearQueue(){
                Log.i(TAG,"Clearing all requests from queue")
                requestHandler.removeMessages(MESSAGE_DOWNLOAD)
                requestMap.clear()
            }
        }
    override fun quit():Boolean{
        hasQuit=true
        return super.quit()
    }


    fun queueThumbnail(target:T,url:String){
        Log.i(TAG,"Got a URL:$url")
        requestMap[target] =url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD,target).sendToTarget()
        //obtainMessage로 handler를 설정하고, sendToTarget을 호출하여 Looper의 메시지 큐 맨 끝에 넣는다.
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() { // onLooperPrepared는 Looper가 최초로 큐를 확인하기 전에 호출됨.
        requestHandler=object:Handler(){
            override fun handleMessage(msg: Message) {
                if(msg.what== MESSAGE_DOWNLOAD){ // message type을 확인 후
                    val target=msg.obj as T
                    Log.i(TAG,"Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }
    private fun handleRequest(target:T){
        val url = requestMap[target]?:return
        val bitmap=flickrFetchr.fetchPhoto(url)?:return

        responseHandler.post(Runnable {
            if (requestMap[target] != url || hasQuit) {
                return@Runnable
            }

            requestMap.remove(target)
            onThumbnaildDownloaded(target, bitmap)
        })
    }
}