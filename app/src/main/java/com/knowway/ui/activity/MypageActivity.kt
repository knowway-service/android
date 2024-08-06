package com.knowway.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.knowway.R
import com.knowway.adapter.ViewPagerAdapter
import com.knowway.data.model.user.UserProfileResponse
import com.knowway.data.model.user.UserRecord
import com.knowway.data.network.ApiClient
import com.knowway.data.network.user.UserApiService
import com.knowway.databinding.ActivityMypageBinding
import com.knowway.ui.fragment.MypageFooterFragment
import retrofit2.*

class MypageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private lateinit var apiService: UserApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        apiService = ApiClient.getClient().create(UserApiService::class.java)

        fetchUserProfile()
        setupViewPagerAndTabs()
        setupFooterFragment()
    }

    private fun fetchUserProfile() {
        apiService.getProfile()
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val userProfile = response.body()
                        userProfile?.let {
                            updateUIWithProfile(it)
                        }
                    } else {
                        Log.e("MypageActivity", "Failed to load user profile")
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Log.e("MypageActivity", "Error fetching user profile", t)
                }
            })
    }

    private fun updateUIWithProfile(userProfile: UserProfileResponse) {
        binding.emailTextView.text = userProfile.email
        binding.pointTextView.text = "${userProfile.pointTotal} 포인트"
    }

    private fun setupViewPagerAndTabs() {
        val viewPagerAdapter = ViewPagerAdapter(this, ViewPagerAdapter.TabType.MY_PAGE)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "등록한 팁"
                1 -> "선정된 팁"
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }.attach()
    }

    private fun setupFooterFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.footer_container, MypageFooterFragment())
            .commit()
    }
}
