package com.knowway.ui.activity

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.knowway.data.model.Record
import com.knowway.data.network.RecordApiService
import com.knowway.databinding.ActivityRecordBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class RecordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRecordBinding

    private var mediaRecorder: MediaRecorder? = null
    private var output: String? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val recordAudioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        val writeStorageGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
        if (!recordAudioGranted || !writeStorageGranted) {
            Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val slidePanel = binding.mainFrame
        slidePanel.addPanelSlideListener(PanelEventListener())

        checkPermissions()

        binding.recordingIcon.setOnClickListener {
            startRecording()
        }

        binding.stopButton.setOnClickListener {
            stopRecording()
        }

        binding.saveButton.setOnClickListener {
            saveRecording()
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> {
                    requestPermissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
        }
    }
    private fun startRecording() {
        output = "${externalCacheDir?.absolutePath}/recording.mp4"
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(output)
            try {
                prepare()
                start()
                Toast.makeText(this@RecordActivity, "Recording started", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@RecordActivity, "Recording failed to start", Toast.LENGTH_SHORT).show()
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
        output?.let { filePath ->
            val file = File(filePath)
            if (file.exists()) {
                val values = ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, "Recording_${System.currentTimeMillis()}")
                    put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4")
                    put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
                }
                val contentResolver = contentResolver
                val uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    contentResolver.openOutputStream(it).use { outputStream ->
                        file.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream!!)
                        }
                    }
                }

                val requestFile = RequestBody.create("audio/mp4".toMediaTypeOrNull(), file)
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val recordRequest = Record(
                    memberId = 1,
                    departmentStoreFloorId = 2,
                    departmentStoreId = 3,
                    recordTitle = "Sample Title",
                    recordLatitude = "37.5665",
                    recordLongitude = "126.9780"
                )
                val recordJson = Gson().toJson(recordRequest)
                val recordBody = RequestBody.create("application/json".toMediaTypeOrNull(), recordJson)
                // Upload file using Retrofit
                val call = RecordApiService.create().uploadRecord(body, recordBody)
                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@RecordActivity, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@RecordActivity, "File upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(this@RecordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    // 이벤트 리스너
    inner class PanelEventListener : SlidingUpPanelLayout.PanelSlideListener {
        // 패널이 슬라이드 중일 때
        override fun onPanelSlide(panel: View?, slideOffset: Float) {
        }

        // 패널의 상태가 변했을 때
        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
//            if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                binding.btnToggle.text = "열기"
//            } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                binding.btnToggle.text = "닫기"
//            }
        }
    }
}