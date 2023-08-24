package viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Alarm
import model.AlarmRepository
import javax.inject.Inject

@HiltViewModel
class AddAlarmViewModel @Inject constructor(private val repository: AlarmRepository) : ViewModel() {
    // 추가: 요일 반복 주기를 나타내는 상수
    companion object {
        const val REPEAT_NONE = 0
        const val REPEAT_EVERY_WEEK = 1
        const val REPEAT_EVERY_OTHER_WEEK = 2
        const val REPEAT_EVERY_THIRD_WEEK = 3
        const val REPEAT_EVERY_FOURTH_WEEK = 4
    }


    private val _vibrateEnabled = MutableLiveData<Boolean>(false)
    val vibrateEnabled: LiveData<Boolean> = _vibrateEnabled

    fun setVibrateEnabled(enabled: Boolean) {
        _vibrateEnabled.value = enabled
    }




    private val _repeatType = MutableLiveData<Int>(REPEAT_NONE)

    private val _ringtoneUri = MutableLiveData<Uri?>(null)
    val ringtoneUri: LiveData<Uri?> = _ringtoneUri

    fun setRingtoneUri(uri: Uri?) {
        _ringtoneUri.value = uri
    }


    val repeatType: LiveData<Int> = _repeatType

    // 추가: 요일 반복 주기 설정 함수
    fun setRepeatType(type: Int) {
        _repeatType.value = type
    }

    suspend fun insert(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(alarm)
        }.join()
    }




}
