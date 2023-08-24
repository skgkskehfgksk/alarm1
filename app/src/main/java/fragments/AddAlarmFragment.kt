package fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.alarmapp.R
import com.example.alarmapp.databinding.FragmentAddAlarmBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Alarm
import viewModel.AddAlarmViewModel
import viewModel.AddAlarmViewModel.Companion.REPEAT_NONE

@AndroidEntryPoint
class AddAlarmFragment : Fragment() {
    private lateinit var binding: FragmentAddAlarmBinding
    private val viewModel: AddAlarmViewModel by viewModels()

    // AddAlarmFragment 클래스 안에 있는 RINGTONE_PICKER_REQUEST_CODE를 클래스 멤버 변수로 이동
    private val RINGTONE_PICKER_REQUEST_CODE = 0





    // AlarmSoundManager 클래스 정의
    object AlarmSoundManager {
        private var mediaPlayer: MediaPlayer? = null

        fun playSound(context: Context) {
            mediaPlayer?.stop()
            mediaPlayer = MediaPlayer.create(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }

        fun stopSound() {
            mediaPlayer?.stop()
            mediaPlayer = null
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)







        binding.txtSave.setOnClickListener { // Create a new alarm
            createAlarm()
            Navigation.findNavController(view)
                .navigate(R.id.action_addAlarmFragment_to_alarmFragment)
        }



        binding.checkRecur.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.recurLayout.visibility = View.VISIBLE
            } else {
                binding.recurLayout.visibility = View.GONE
            }
        }

        // 클릭을 가로채고 다른 뷰들에 클릭 이벤트 전달하지 않음
        binding.recurLayout.setOnTouchListener { _, _ ->
            true
        }

        binding.btnDoneRecur.setOnClickListener {
            with(binding) {
                if (!checkMon.isChecked && !checkTue.isChecked && !checkWed.isChecked &&
                    !checkThu.isChecked && !checkFri.isChecked && !checkSat.isChecked &&
                    !checkSun.isChecked) {
                    checkRecur.isChecked = false
                }
            }

            val selectedRadioId = binding.radioGroup.checkedRadioButtonId
            val repeatType = when (selectedRadioId) {
                R.id.radioButton1 -> Alarm.REPEAT_WEEKLY
                R.id.radioButton2 -> Alarm.REPEAT_EVERY_OTHER_WEEK
                R.id.radioButton3 -> Alarm.REPEAT_EVERY_THIRD_WEEK
                R.id.radioButton4 -> Alarm.REPEAT_EVERY_FOURTH_WEEK
                else -> Alarm.REPEAT_NONE
            }

            viewModel.setRepeatType(repeatType)


            binding.recurLayout.visibility = View.GONE
            // 다른 뷰들에 클릭 이벤트 전달
            binding.recurLayout.setOnTouchListener(null)
//            binding.txtSave.visibility = View.VISIBLE
//            binding.txtCancel.visibility = View.VISIBLE
        }

        binding.txtCancel.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_addAlarmFragment_to_alarmFragment)
        }

        binding.switchVib.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setVibrateEnabled(isChecked)
        }

        binding.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 알림 소리 재생
                AlarmSoundManager.playSound(requireContext())
            } else {
                // 알림 소리 중지
                AlarmSoundManager.stopSound()
            }
        }

        binding.buttonBell.setOnClickListener() {
            openRingtonePicker()

        }

        // ... (기존 코드)
    }

    private fun openRingtonePicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "벨소리 선택")
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        val RINGTONE_PICKER_REQUEST_CODE = 0
        startActivityForResult(intent, RINGTONE_PICKER_REQUEST_CODE)
    }

    // 벨소리 선택 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RINGTONE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val ringtoneUri = data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            viewModel.setRingtoneUri(ringtoneUri)
        }
    }


    private fun createAlarm() {
        val alarm = Alarm(
            binding.timePicker.hour,
            binding.timePicker.minute,
            binding.checkRecur.isChecked,
            binding.checkMon.isChecked,
            binding.checkTue.isChecked,
            binding.checkWed.isChecked,
            binding.checkThu.isChecked,
            binding.checkFri.isChecked,
            binding.checkSat.isChecked,
            binding.checkSun.isChecked,
            binding.edtAlarmLabel.text.toString(),
            true,
            System.currentTimeMillis(),
            System.currentTimeMillis()
        )

        val repeatType = viewModel.repeatType.value ?: REPEAT_NONE

        alarm.setRepeatType(repeatType)

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.insert(alarm)
        }

        val hour: Int = binding.timePicker.hour
        val minute: Int = binding.timePicker.minute
        Toast.makeText(requireActivity(), "$hour : $minute", Toast.LENGTH_LONG).show()
        activity?.let { alarm.schedule(it as Context) }


        val ringtoneUri = viewModel.ringtoneUri.value

        alarm.setRingtoneUri(ringtoneUri)
    }

    // ... (기존 코드)
}



