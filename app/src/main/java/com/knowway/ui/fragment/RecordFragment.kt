package com.knowway.ui.fragment

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.knowway.R
import com.knowway.data.model.record.Record
import com.knowway.data.network.RecordApiService
import com.knowway.databinding.FragmentRecordModalBinding
import com.knowway.ui.activity.mainpage.MainPageActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class RecordFragment : Fragment() {
    private var _binding: FragmentRecordModalBinding? = null
    private val binding get() = _binding!!

    private var mediaRecorder: MediaRecorder? = null
    private var output: String? = null

    private lateinit var sharedPreferences: SharedPreferences

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val recordAudioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        val writeStorageGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
        if (!recordAudioGranted || !writeStorageGranted) {
            Toast.makeText(context, "권한이 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private var latitude: Double? = null
    private var longitude: Double? = null

    companion object {
        private const val ARG_LATITUDE = "latitude"
        private const val ARG_LONGITUDE = "longitude"

        fun newInstance(latitude: Double?, longitude: Double?): RecordFragment {
            val fragment = RecordFragment()
            val args = Bundle()
            args.putDouble(ARG_LATITUDE, latitude ?: 0.0)
            args.putDouble(ARG_LONGITUDE, longitude ?: 0.0)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordModalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            latitude = it.getDouble(ARG_LATITUDE)
            longitude = it.getDouble(ARG_LONGITUDE)
        }

        sharedPreferences = requireActivity().getSharedPreferences("DeptPref", Context.MODE_PRIVATE)

        Glide.with(this)
            .asGif()
            .load(R.drawable.recording_wave_gif)
            .into(binding.recordingWaveGif)

        checkPermissions()

        binding.startButtonInclude.recordingStartButton.setOnClickListener {
            handleRecordingStart()
        }

        binding.stopButtonInclude.recordingStopButton.setOnClickListener {
            handleRecordingStop()
        }

        binding.retryButtonInclude.recordingRetryButton.setOnClickListener {
            handleRecordingRetry()
        }

        binding.saveButtonInclude.recordingSaveButton.setOnClickListener {
            setConfirmUI(true, true)
            setButtonVisibility(false, false, false, false, false, false)
        }

        binding.confirmButtonInclude.confirmButton.setOnClickListener {
            saveRecording()
        }

        binding.titleText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.titleText.text.clear()
            }
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> {
                    requestPermissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
        }
    }

    private fun setButtonVisibility(startVisible: Boolean, stopVisible: Boolean, saveVisible: Boolean, retryVisible: Boolean, waveVisible: Boolean, waveGifVisible: Boolean) {
        binding.startButtonInclude.root.visibility = if (startVisible) View.VISIBLE else View.GONE
        binding.stopButtonInclude.root.visibility = if (stopVisible) View.VISIBLE else View.GONE
        binding.saveButtonInclude.root.visibility = if (saveVisible) View.VISIBLE else View.GONE
        binding.retryButtonInclude.root.visibility = if (retryVisible) View.VISIBLE else View.GONE
        binding.recordingWave.visibility = if (waveVisible) View.VISIBLE else View.GONE
        binding.recordingWaveGif.visibility = if (waveGifVisible) View.VISIBLE else View.GONE
    }

    private fun setConfirmUI(confirmVisible: Boolean, textVisible: Boolean) {
        binding.confirmButtonInclude.root.visibility = if (confirmVisible) View.VISIBLE else View.GONE
        binding.titleText.visibility = if (textVisible) View.VISIBLE else View.GONE
    }

    private fun handleRecordingStart() {
        setButtonVisibility(false, true, false, false, false, true)
        startRecording()
    }

    private fun handleRecordingStop() {
        setButtonVisibility(false, false, true, true, true, false)
        stopRecording()
    }

    private fun handleRecordingRetry() {
        setButtonVisibility(false, true, false, false, false, true)
        startRecording()
    }

    private fun startRecording() {
        output = "${requireContext().externalCacheDir?.absolutePath}/recording.mp4"
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(output)
            try {
                prepare()
                start()
                Toast.makeText(requireContext(), "Recording started", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Recording failed to start", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    private fun saveRecording() {
        val recordTitle = binding.titleText.text.toString()
        if (recordTitle.isBlank()) {
            Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val departmentStoreId = sharedPreferences.getLong("dept_id", 1)
        val departmentStoreFloorId = sharedPreferences.getLong("selected_floor_id", 1)
        output?.let { filePath ->
            val file = File(filePath)
            if (file.exists()) {
                val values = ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, "Recording_${System.currentTimeMillis()}")
                    put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4")
                    put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
                }
                val contentResolver = requireContext().contentResolver
                val uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    contentResolver.openOutputStream(it).use { outputStream ->
                        file.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream!!)
                        }
                    }
                }

                val requestFile = RequestBody.create("audio/mp4".toMediaTypeOrNull(), file)
                val body = MultipartBody.Part.createFormData("file", "$recordTitle.mp4", requestFile)

                val recordRequest = Record(
                    departmentStoreFloorId = departmentStoreFloorId,
                    departmentStoreId = departmentStoreId,
                    recordTitle = recordTitle,
                    recordLatitude = latitude.toString(),
                    recordLongitude = longitude.toString()
                )
                val recordJson = Gson().toJson(recordRequest)
                val recordBody = RequestBody.create("application/json".toMediaTypeOrNull(), recordJson)
                // Upload file using Retrofit
                val call = RecordApiService.create().uploadRecord(body, recordBody)
                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "녹음 파일이 성공적으로 저장되었습니다!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "현재 위치를 확인해주세요!", Toast.LENGTH_SHORT).show()
                        }
                        parentFragmentManager.beginTransaction().remove(this@RecordFragment).commit()
                        (activity as MainPageActivity).collapsePanel()
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(requireContext(), "요청을 불러오는데 실패했어요", Toast.LENGTH_SHORT).show()
                    }

                })

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
