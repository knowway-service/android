package com.knowway.adapter.user

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.knowway.data.model.user.UserRecord
import com.knowway.databinding.ItemAdminRecordBinding
import com.knowway.R
import com.knowway.data.network.ApiClient
import com.knowway.data.network.user.UserApiService
import com.knowway.ui.fragment.DeleteRecordFragment
import com.knowway.ui.fragment.mainpage.MainRecordTipFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRecordAdapter(
    private val records: MutableList<UserRecord>,
    private val context: Context,
    private val showDeleteIcon: Boolean
) : RecyclerView.Adapter<UserRecordAdapter.RecordViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition = -1
    private val handler = Handler(Looper.getMainLooper())
    private var updateSeekBarTask: Runnable? = null

    inner class RecordViewHolder(val binding: ItemAdminRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Handle clicking on the record title
            binding.recordTitle.setOnClickListener {
                val record = records[adapterPosition]
                record.isExpanded = !record.isExpanded
                notifyItemChanged(adapterPosition)
            }

            // Handle play/pause button click
            binding.btnPlayPause.setOnClickListener {
                val record = records[adapterPosition]
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    binding.btnPlayPause.setImageResource(R.drawable.ic_record_play)
                } else {
                    playMp3(record.recordUrl)
                    binding.btnPlayPause.setImageResource(R.drawable.ic_record_pause)
                }
            }

            // Handle trash icon click to delete the record
            binding.actionIcon.setOnClickListener {
                val record = records[adapterPosition]
                if (binding.actionIcon.visibility == View.VISIBLE) {
                    showDeleteConfirmationDialog(record, adapterPosition)
                }
            }

            // Handle seekbar actions
            binding.musicSeekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            // Handle previous button click
            binding.btnPrevious.setOnClickListener {
                mediaPlayer?.let {
                    val newPosition = it.currentPosition - 15000
                    it.seekTo(if (newPosition > 0) newPosition else 0)
                }
            }

            // Handle next button click
            binding.btnNext.setOnClickListener {
                mediaPlayer?.let {
                    val newPosition = it.currentPosition + 15000
                    it.seekTo(if (newPosition < it.duration) newPosition else it.duration)
                }
            }
        }

        fun bind(record: UserRecord) {
            binding.recordTitle.text = record.recordTitle
            binding.musicControlLayout.visibility =
                if (record.isExpanded) View.VISIBLE else View.GONE
            binding.btnPlayPause.setImageResource(R.drawable.ic_record_play)

            binding.root.setBackgroundResource(
                if (record.isExpanded) R.drawable.record_item_background_expanded
                else R.drawable.record_item_background
            )

            if (record.isSelectedByAdmin) {
                binding.actionIcon.visibility = View.GONE
            } else {
                binding.actionIcon.setImageResource(R.drawable.trash)
                binding.actionIcon.visibility = View.VISIBLE
            }

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
                    Log.e("MediaPlayer", "Error playing audio file: $audioFileUrl", e)
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

        private fun showDeleteConfirmationDialog(record: UserRecord, position: Int) {
            val fragment = DeleteRecordFragment().apply {
                setOnConfirmListener {
                    ApiClient.getClient().create(UserApiService::class.java)
                        .deleteUserRecord(record.recordId)
                        .enqueue(object : Callback<Boolean> {
                            override fun onResponse(
                                call: Call<Boolean>,
                                response: Response<Boolean>
                            ) {
                                if (response.isSuccessful && response.body() == true) {
                                    records.removeAt(position)
                                    notifyItemRemoved(position)
                                    Toast.makeText(
                                        context,
                                        "삭제 완료: ${record.recordTitle}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Log.e("UserRecordAdapter", "Failed to delete user record")
                                }
                            }

                            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                Log.e("UserRecordAdapter", "Error deleting user record", t)
                            }
                        })
                }
            }

            // Show the fragment
            fragment.show(
                (context as AppCompatActivity).supportFragmentManager,
                "DeleteConfirmationDialog"
            )
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
