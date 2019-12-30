package com.android.goodlife.Data
import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.android.goodlife.R

import java.text.SimpleDateFormat
import java.util.*


class Tools {
    companion object {
        private fun getAPIVerison(): Float {

            var f: Float? = null
            try {
                f = java.lang.Float.valueOf(Build.VERSION.RELEASE.substring(0, 1))
            } catch (e: NumberFormatException) {
            }

            return f!!.toFloat()
        }

        fun formatTime(time: Long): String {
            // income time
            val date = Calendar.getInstance()
            date.timeInMillis = time

            // current time
            val curDate = Calendar.getInstance()
            curDate.timeInMillis = System.currentTimeMillis()

            var dateFormat: SimpleDateFormat? = null
            if (date.get(Calendar.YEAR) == curDate.get(Calendar.YEAR)) {
                if (date.get(Calendar.DAY_OF_YEAR) == curDate.get(Calendar.DAY_OF_YEAR)) {
                    dateFormat = SimpleDateFormat("h:mm a", Locale.US)
                } else {
                    dateFormat = SimpleDateFormat("MMM d", Locale.US)
                }
            } else {
                dateFormat = SimpleDateFormat("MMM yyyy", Locale.US)
            }
            return dateFormat.format(time)
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        fun systemBarMarshmellow(act: Activity) {
            if (getAPIVerison() >= 6.0) {
                val window = act.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = act.resources.getColor(R.color.colorPrimaryDark)
            }
        }
    }


}