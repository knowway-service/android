package com.knowway.adapter

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.knowway.R
import com.knowway.data.model.admin.AdminRecord
import com.knowway.databinding.ItemAdminRecordBinding

class AdminRecordAdapter(
    private val records: List<AdminRecord>,
    private val context: Context,
    private val isInSelectionTab: Boolean
) : RecyclerView.Adapter<AdminRecordAdapter.RecordViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition = -1
    private val handler = Handler(Looper.getMainLooper())
    private var updateSeekBarTask: Runnable? = null

    inner class RecordViewHolder(val binding: ItemAdminRecordBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // 레코드 제목 클릭 시 확장/축소 토글
            binding.recordTitle.setOnClickListener {
                val record = records[adapterPosition]
                record.isExpanded = !record.isExpanded
                notifyItemChanged(adapterPosition)
            }

            binding.btnPlayPause.setOnClickListener {
                val record = records[adapterPosition]
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    binding.btnPlayPause.setImageResource(R.drawable.ic_record_play)
                } else {
                    playMp3(record.audioFileUrl)
                    binding.btnPlayPause.setImageResource(R.drawable.ic_record_pause)
                }
            }

            // SeekBar 변경 시 재생 위치 변경
            binding.musicSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            binding.btnPrevious.setOnClickListener {
                mediaPlayer?.let {
                    val newPosition = it.currentPosition - 15000
                    it.seekTo(if (newPosition > 0) newPosition else 0)
                }
            }

            binding.btnNext.setOnClickListener {
                mediaPlayer?.let {
                    val newPosition = it.currentPosition + 15000
                    it.seekTo(if (newPosition < it.duration) newPosition else it.duration)
                }
            }

            binding.actionText.setOnClickListener {
                val record = records[adapterPosition]
                if (isInSelectionTab) {
                    Toast.makeText(context, "선정 취소: ${record.title}", Toast.LENGTH_SHORT).show()
                    Log.d("Action", "선정 취소: ${record.title}")
                } else {
                    Toast.makeText(context, "선정: ${record.title}", Toast.LENGTH_SHORT).show()
                    Log.d("Action", "선정: ${record.title}")
                }
            }
        }

        fun bind(record: AdminRecord) {
            binding.recordTitle.text = record.title
            binding.musicControlLayout.visibility = if (record.isExpanded) View.VISIBLE else View.GONE
            binding.btnPlayPause.setImageResource(R.drawable.ic_record_play)

            // 배경 변경
            if (record.isExpanded) {
                binding.root.setBackgroundResource(R.drawable.record_item_background_expanded)
            } else {
                binding.root.setBackgroundResource(R.drawable.record_item_background)
            }

            // 텍스트 변경
            if (isInSelectionTab) {
                binding.actionText.text = "선정 취소"
                binding.actionText.setTextColor(ContextCompat.getColor(context, R.color.dark_blue))
            } else {
                binding.actionText.text = "선정"
                binding.actionText.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
            binding.actionText.visibility = View.VISIBLE

            if (mediaPlayer?.isPlaying == true && adapterPosition == currentPlayingPosition) {
                binding.musicSeekbar.max = mediaPlayer!!.duration
                binding.musicSeekbar.progress = mediaPlayer!!.currentPosition
                binding.btnPlayPause.setImageResource(R.drawable.ic_record_pause)
                startSeekBarUpdate()
            } else {
                stopSeekBarUpdate()
            }
        }

        private fun playMp3(audioFileUrl: String) {
            if (mediaPlayer != null && currentPlayingPosition != adapterPosition) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }

            currentPlayingPosition = adapterPosition

            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer?.apply {
                        setDataSource(audioFileUrl)
                        prepare()
                        start()
                        setVolume(1.0f, 1.0f) // 최대 볼륨으로 설정
                        setOnCompletionListener {
                            binding.btnPlayPause.setImageResource(R.drawable.ic_record_play)
                            binding.musicSeekbar.progress = 0
                            currentPlayingPosition = -1
                            stopSeekBarUpdate()
                        }
                    }
                    binding.musicSeekbar.max = mediaPlayer!!.duration
                    startSeekBarUpdate()
                } catch (e: Exception) {
                    Log.e("MediaPlayer", "Error playing audio file: $audioFileUrl", e)
                }
            } else {
                mediaPlayer?.start()
                mediaPlayer?.setVolume(1.0f, 1.0f) // 최대 볼륨으로 설정
                startSeekBarUpdate()
            }
        }

        private fun startSeekBarUpdate() {
            updateSeekBarTask = object : Runnable {
                override fun run() {
                    if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                        binding.musicSeekbar.progress = mediaPlayer!!.currentPosition
                        handler.postDelayed(this, 1000)
                    }
                }
            }
            handler.post(updateSeekBarTask!!)
        }

        fun stopSeekBarUpdate() {
            updateSeekBarTask?.let { handler.removeCallbacks(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemAdminRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount(): Int = records.size

    override fun onViewRecycled(holder: RecordViewHolder) {
        super.onViewRecycled(holder)
        if (holder.adapterPosition == currentPlayingPosition) {
            holder.binding.musicSeekbar.progress = 0
            mediaPlayer?.release()
            mediaPlayer = null
            currentPlayingPosition = -1
            holder.stopSeekBarUpdate()
        }
    }
}
