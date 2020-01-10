package com.android.goodlife.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.android.goodlife.DoctorActivity.MainActivityHome
import com.android.goodlife.Model.ChatMessage
import com.android.goodlife.ParseFirebaseData

import com.android.goodlife.R
import com.android.goodlife.Untilty.Const
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



@RequiresApi(Build.VERSION_CODES.LOLLIPOP)


class NotificationService : JobService() {
    //Todo: scheduler stops on force exit of app or reboot
    override fun onStartJob(params: JobParameters?): Boolean {
        val ref = FirebaseDatabase.getInstance().getReference(Const.MESSAGE_CHILD)
        val parseFirebaseData: ParseFirebaseData = ParseFirebaseData(this)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                Log.d(Const.LOG_TAG, "Data changed from service")
                for (oneChat: ChatMessage in parseFirebaseData.getAllUnreadReceivedMessages(p0)) {
//                    Log.e(Constants.LOG_TAG, oneChat.text + "\n")
                   showNotification(oneChat.senderName.toString(),oneChat.text.toString())
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    fun showNotification(title: String, content: String) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("default", "Fchat-Message", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "New message notification for fchat"
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(applicationContext, "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true) // clear notification after click
        val intent = Intent(applicationContext, MainActivityHome::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }

}