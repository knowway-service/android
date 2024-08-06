package com.knowway.adapter

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.knowway.R
import com.knowway.data.model.admin.AdminRecord
import com.knowway.data.repository.AdminRepository
import com.knowway.databinding.ItemAdminRecordBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminRecordAdapter(
    private var records: List<AdminRecord>,
    private val context: Context,
    private val isInSelectionTab: Boolean,
    private val refreshData: () -> Unit
) : RecyclerView.Adapter<AdminRecordAdapter.RecordViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition = -1
    private val handler = Handler(Looper.getMainLooper())
    private var updateSeekBarTask: Runnable? = null

    private val adminRepository: AdminRepository = AdminRepository()

    inner class RecordViewHolder(val binding: ItemAdminRecordBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.recordTitle.setOnClickListener {
                val record = records[bindingAdapterPosition]
                record.isExpanded = !record.isExpanded
                notifyItemChanged(bindingAdapterPosition)
            }

            binding.btnPlayPause.setOnClickListener {
                val record = records[bindingAdapterPosition]
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    binding.btnPlayPause.setImageResource(R.drawable.ic_record_play)
                } else {
                    playMp3(record.recordPath)
                    binding.btnPlayPause.setImageResource(R.drawable.ic_record_pause)
                }
            }

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
                val record = records[bindingAdapterPosition]
                val action = if (isInSelectionTab) "cancel" else "select"
                CoroutineScope(Dispatchers.IO).launch {
                    val response = adminRepository.selectRecord(record.recordId.toLong())
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            if (!isInSelectionTab) {
                                val pointsResponse = adminRepository.updatePoints(mapOf("recordId" to record.recordId.toLong()))
                                if (pointsResponse.isSuccessful) {
                                } else {
                                }
                            }
                            refreshData()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                        }
                    }
                }
            }
        }

        fun bind(record: AdminRecord) {
            binding.recordTitle.text = record.recordTitle
            binding.musicControlLayout.visibility = if (record.isExpanded) View.VISIBLE else View.GONE
            binding.btnPlayPause.setImageResource(R.drawable.ic_record_play)

            if (record.isExpanded) {
                binding.root.setBackgroundResource(R.drawable.record_item_background_expanded)
            } else {
                binding.root.setBackgroundResource(R.drawable.record_item_background)
            }

            if (isInSelectionTab) {
                binding.actionText.text = "선정 취소"
                binding.actionText.setTextColor(ContextCompat.getColor(context, R.color.dark_blue))
            } else {
                binding.actionText.text = "선정"
                binding.actionText.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
            binding.actionText.visibility = View.VISIBLE

            if (mediaPlayer?.isPlaying == true && bindingAdapterPosition == currentPlayingPosition) {
                binding.musicSeekbar.max = mediaPlayer!!.duration
                binding.musicSeekbar.progress = mediaPlayer!!.currentPosition
                binding.btnPlayPause.setImageResource(R.drawable.ic_record_pause)
                startSeekBarUpdate()
            } else {
                stopSeekBarUpdate()
            }
        }

        private fun playMp3(audioFileUrl: String) {
            if (mediaPlayer != null && currentPlayingPosition != bindingAdapterPosition) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }

            currentPlayingPosition = bindingAdapterPosition

            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer?.apply {
                        setDataSource(audioFileUrl)
                        prepare()
                        start()
                        setVolume(1.0f, 1.0f)
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
                }
            } else {
                mediaPlayer?.start()
                mediaPlayer?.setVolume(1.0f, 1.0f)
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
        if (holder.bindingAdapterPosition == currentPlayingPosition) {
            holder.binding.musicSeekbar.progress = 0
            mediaPlayer?.release()
            mediaPlayer = null
            currentPlayingPosition = -1
            holder.stopSeekBarUpdate()
        }
    }

    fun updateRecords(newRecords: List<AdminRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }
}