package com.knowway.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.knowway.R
import com.knowway.ui.fragment.RecordFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class RecordActivity : AppCompatActivity() {
    private lateinit var recordFragment: RecordFragment
    private lateinit var slidingUpPanelLayout: SlidingUpPanelLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        slidingUpPanelLayout = findViewById(R.id.main_frame)
        slidingUpPanelLayout.addPanelSlideListener(PanelEventListener())

        val recordingBtn: ImageView = findViewById(R.id.recordingBtn)
        recordingBtn.setOnClickListener {
            slidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            replaceFragment()
        }

        if (savedInstanceState == null) {
            initializeFragment()
        } else {
            recordFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as RecordFragment
        }
    }

    private fun initializeFragment() {
        recordFragment = RecordFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, recordFragment)
            .commit()
    }

    private fun replaceFragment() {
        recordFragment = RecordFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, recordFragment)
            .commit()
    }

    inner class PanelEventListener : SlidingUpPanelLayout.PanelSlideListener {
        override fun onPanelSlide(panel: View?, slideOffset: Float) {
            // Do something when the panel is sliding
        }

        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
            // Do something when the panel state changes
        }
    }
}
