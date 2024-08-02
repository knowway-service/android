package com.knowway.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.knowway.R
import com.knowway.ui.fragment.MainBackFragment
import com.knowway.ui.fragment.MainFloorSelectFragment
import com.knowway.ui.fragment.MainMapFragment
import com.knowway.ui.fragment.MapFooterFragment


class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val floorSelectButton: ImageButton = findViewById(R.id.main_up_down)
        floorSelectButton.setOnClickListener {
            val floorSelectModal = MainFloorSelectFragment()
            floorSelectModal.show(supportFragmentManager, "층 선택 모달창")
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.button_fragment_container, MainBackFragment())
                .replace(R.id.card_fragment_container, MainMapFragment())
                .replace(R.id.footer_container, MapFooterFragment())
                .commit()
        }
    }
}