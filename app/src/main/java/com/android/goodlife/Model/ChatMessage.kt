package com.android.goodlife.Model


import com.android.goodlife.Data.Tools


class ChatMessage(var text: String?, var timestamp: String, var DoctorId: String?, var DoctorName: String?, var DoctorPhoto: String?, var senderId: String?, var senderName: String?, var senderPhoto: String?, var isRead: Boolean?) {

    val readableTime: String?
        get() {
            return try {
                Tools.formatTime(java.lang.Long.valueOf(timestamp))
            } catch (ignored: NumberFormatException) {
                null
            }

        }

    val receiver: Friend
        get() = Friend(DoctorId!!, DoctorName!!, DoctorPhoto!!)

    val sender: Friend
        get() = Friend(DoctorId!!, DoctorName!!, DoctorPhoto!!)

    val comparableTimestamp: Long
        get() = java.lang.Long.parseLong(timestamp)


}
