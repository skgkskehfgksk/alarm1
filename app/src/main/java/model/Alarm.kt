package model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import broadcastReceiver.AlarmReceiver
import java.util.*


@Entity(tableName = "alarm_table")
data class Alarm(
    var hour: Int,
    var minute: Int,
    var recurring: Boolean,
    var monday: Boolean,
    var tuesday: Boolean,
    var wednesday: Boolean,
    var thursday: Boolean,
    var friday: Boolean,
    var saturday: Boolean,
    var sunday: Boolean,
    var label: String,
    var isOn: Boolean,
    var timeCreated: Long,
    @PrimaryKey var id: Long
) {

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("HOUR", hour)
        intent.putExtra("MINUTE", minute)
        intent.putExtra("RECURRING", recurring)
        intent.putExtra("MONDAY", monday)
        intent.putExtra("TUESDAY", tuesday)
        intent.putExtra("WEDNESDAY", wednesday)
        intent.putExtra("THURSDAY", thursday)
        intent.putExtra("FRIDAY", friday)
        intent.putExtra("SATURDAY", saturday)
        intent.putExtra("SUNDAY", sunday)
        intent.putExtra("LABEL", label)
        intent.putExtra("ID", id)
        val pendingIntent = id.let { PendingIntent.getBroadcast(context, it.toInt(), intent, 0) };

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis();
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
        }

        if (!recurring) {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            val daily: Long = 24 * 60 * 60 * 1000
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                daily,
                pendingIntent
            )
        }
        this.isOn = true
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = id.let { PendingIntent.getBroadcast(context, it.toInt(), intent, 0) }
        alarmManager.cancel(pendingIntent)
        this.isOn = false
    }

    fun getRecurringDays(): String {
        if (!recurring) {
            return ""
        }
        var recurDays = ""
        if (monday) recurDays += "Mon "
        if (tuesday) recurDays += "Tue "
        if (wednesday) recurDays += "Wed "
        if (thursday) recurDays += "Thu "
        if (friday) recurDays += "Fri "
        if (saturday) recurDays += "Sat "
        if (sunday) recurDays += "Sun "
        return recurDays
    }

    fun setRecurringValues(recurring: Boolean, mon: Boolean, tue: Boolean, wed: Boolean, thu: Boolean, fri: Boolean, sat: Boolean, sun: Boolean) {
        this.recurring = recurring
        this.monday = mon
        this.tuesday = tue
        this.wednesday = wed
        this.thursday = thu
        this.friday = fri
        this.saturday = sat
        this.sunday = sun
    }

    fun setRepeatType(repeatType: Int) {
        TODO("Not yet implemented")
    }

    fun setRingtoneUri(ringtoneUri: Uri?) {
        TODO("Not yet implemented")
    }

    // ... (기존 함수들)
    companion object {
        const val REPEAT_NONE: Int = 0
        const val REPEAT_WEEKLY = 1
        const val REPEAT_EVERY_OTHER_WEEK = 2
        const val REPEAT_EVERY_THIRD_WEEK = 3
        const val REPEAT_EVERY_FOURTH_WEEK = 4
        // 다른 반복 주기에 대한 상수도 필요한 경우 여기에 추가하세요.
    }

}

