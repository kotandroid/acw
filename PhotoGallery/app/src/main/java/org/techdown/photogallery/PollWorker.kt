package org.techdown.photogallery

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG="PollWorker"

class PollWorker(val context: Context, workerParams: WorkerParameters) : Worker(context,workerParams){
    override fun doWork():Result{
        Log.i(TAG,"work request triggered")
        val query=QueryPreferences.getStoreQuery(context)
        val lastResultId=QueryPreferences.getLastResultId(context)
        val items:List<GalleryItem> = if(query.isEmpty()){
            FlickrFetchr().fetchPhotoRequest()
                .execute()
                .body()
                ?.galleryItems
        }else{
            FlickrFetchr().searchPhotoRequest(query)
                .execute()
                .body()
                ?.galleryItems
        }?:emptyList()

        if(items.isEmpty()){
            return Result.success()
        }

        val resultId=items.first().id
        if(resultId==lastResultId){
            Log.i(TAG,"Got an old result : $resultId")
        }
        else{
            Log.i(TAG,"Got a new result:$resultId")
            QueryPreferences.setLastResultId(context,resultId)

            val intent=PhotoGalleryActivity.newIntent(context)
            val pendingIntent=PendingIntent.getActivity(context,0,intent,0)

            val resources = context.resources
            val notification=NotificationCompat //안드 8이상 이전 보두 지원위해 compat사용한다.
                .Builder(context, NOTIFICATION_CHANNEL_ID) // channel id를 받아 안드 8.0이상시 id를 이용해 채널을 생성한다. 안드8이하면 무시한다.
               // .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // 사용자가 알림을 눌렀을 때 드로어에서 알림이 삭제됨
                .build()


            showBackgroundNotification(0,notification)



        }
        return Result.success()
    }//background 쓰레드에서 호출.

    private fun showBackgroundNotification(
        requestCode:Int,
        notification: Notification
    ){
        val intent=Intent(ACTION_SHOW_NOTIFICATION).apply{
            putExtra(REQUEST_CODE,requestCode)
            putExtra(NOTIFICATION,notification)
        }
        context.sendOrderedBroadcast(intent, PREM_PRIVATE)
    }

    companion object{
        const val ACTION_SHOW_NOTIFICATION=
            "org.techdown.photogallery.SHOW_NOTIFICATION"

        const val PREM_PRIVATE="org.techdown.photogallery.PRIVATE"
        const val REQUEST_CODE="REQUEST_CODE"
        const val NOTIFICATION="NOTIFICATION"
    }
}