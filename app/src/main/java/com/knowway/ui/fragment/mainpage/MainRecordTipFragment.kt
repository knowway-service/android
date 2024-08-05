package com.knowway.ui.fragment.mainpage

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.knowway.R
import com.knowway.ui.fragment.OnAudioCompletionListener

class MainRecordTipFragment : DialogFragment() {
    private var mediaPlayer: MediaPlayer? = null
    private var audioFileUrl: String? = null
    private var audioCompletionListener: OnAudioCompletionListener ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record_tip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmButtonContainer = view.findViewById<FrameLayout>(R.id.record_confirm)
        val cancelButtonContainer = view.findViewById<FrameLayout>(R.id.record_cancel)

        val confirmButtonView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_confirm_button, confirmButtonContainer, false)
        val cancelButtonView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_cancel_button, cancelButtonContainer, false)

        confirmButtonContainer.addView(confirmButtonView)
        cancelButtonContainer.addView(cancelButtonView)

        val confirmButton = confirmButtonView.findViewById<ImageButton>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            audioFileUrl?.let { url ->
                playAudio(url)
            }
        }

        val cancelButton = cancelButtonView.findViewById<ImageButton>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            stopAndReleaseMediaPlayer()
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.border_background)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAndReleaseMediaPlayer()
    }

    fun setAudioFileUrl(url: String) {
        audioFileUrl = url
    }

    fun setOnAudioCompletionListener(listener: OnAudioCompletionListener) {
        this.audioCompletionListener = listener
    }

    fun playAudio(url: String) {
        if (audioFileUrl == url && mediaPlayer?.isPlaying == true) {
            return
        }

        stopAndReleaseMediaPlayer()

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(url)
                prepare()
                start()
                setVolume(1.0f, 1.0f)
                setOnCompletionListener {
                    stopAndReleaseMediaPlayer()
                    audioCompletionListener?.onAudioCompleted()
                }
            } catch (e: Exception) {
                Log.e("MediaPlayer", "Error playing audio file: $audioFileUrl", e)
            }
        }
    }

    fun stopAndReleaseMediaPlayer() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
    }
}