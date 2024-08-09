package com.knowway.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.knowway.R
import com.knowway.databinding.ActivityOnboardingBinding
import com.knowway.ui.activity.user.LoginActivity

/**
 * 온보딩 페이지 Activity
 *
 * @author 이주현
 * @since 2024.07.28
 * @version 1.0
 */


class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButtonLayout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
