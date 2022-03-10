package org.techdown.photogallery

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat


private const val TAG="NotificationReceiver"


class NotificationReciever: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG,"recieved broadcast :${intent?.action}")

        if(resultCode!= Activity.RESULT_OK){
            return
            // 백그라운드가 포그라운드에 있으면 인텐트를 취소한다.
        }

        val requestCode= intent?.getIntExtra(PollWorker.REQUEST_CODE,0)
        val notification: Notification =
            intent?.getParcelableExtra(PollWorker.NOTIFICATION)!!

        val notificationManager=NotificationManagerCompat.from(context!!)
        notificationManager.notify(requestCode!!,notification)

    }
}