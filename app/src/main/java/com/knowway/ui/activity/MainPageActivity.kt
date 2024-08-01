package com.knowway.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.knowway.R
import com.knowway.ui.fragment.MapFooterFragment
import pl.droidsonroids.gif.GifDrawable


class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val questionGif = GifDrawable(resources, R.drawable.question)
        val questionImageView = findViewById<ImageView>(R.id.main_question)
        questionImageView.setImageDrawable(questionGif)

        val walkingPersonGif = GifDrawable(resources, R.drawable.walking_person)
        val walkingPersonImageView = findViewById<ImageView>(R.id.main_walking_person)
        walkingPersonImageView.setImageDrawable(walkingPersonGif)

        if (savedInstanceState == null) {
            val footerFragment = MapFooterFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.footer_container, footerFragment)
                .commit()
        }
    }
}