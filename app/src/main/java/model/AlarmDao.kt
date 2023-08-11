package model

import androidx.room.*

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm)

    @Query("SELECT * from alarm_table ORDER BY id ASC") // tra ve 1 list bao thuc dc sap xep theo thoi gian tao
    suspend fun getAlarms(): List<Alarm>

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarm_table WHERE id=:id")
    fun getAlarmWithId(id: Long): Alarm
}