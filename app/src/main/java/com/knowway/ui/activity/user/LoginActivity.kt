package com.knowway.ui.activity.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.knowway.R
import com.knowway.data.model.user.LoginRequest
import com.knowway.data.model.user.UserChatMemberIdResponse
import com.knowway.data.network.ApiClient
import com.knowway.data.network.user.UserApiService
import com.knowway.ui.activity.AdminDepartmentStoreSearchActivity
import com.knowway.ui.activity.SelectMenuActivity
import com.knowway.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: ImageButton
    private lateinit var signUpButton: ImageButton
    private lateinit var loginErrorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ApiClient.init(this)

        emailInput = findViewById(R.id.input_email)
        passwordInput = findViewById(R.id.input_password)
        loginButton = findViewById(R.id.login_button)
        signUpButton = findViewById(R.id.sign_up_button)
        loginErrorMessage = findViewById(R.id.login_failed_txt)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                performLogin(email, password)
            } else {
                showError("이메일과 패스워드를 모두 입력해주세요.")
            }
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(email: String, password: String) {
        val apiService = ApiClient.getClient().create(UserApiService::class.java)
        val loginCall = apiService.login(LoginRequest(email, password))

        loginCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    checkUserRole()
                } else {
                    showError("이메일 또는 비밀번호를 확인해주세요.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("error", "네트워크 오류: ${t.localizedMessage}")
            }
        })
    }

    private fun checkUserRole() {
        val apiService = ApiClient.getClient().create(UserApiService::class.java)
        val call = apiService.isUserAdmin()

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.code() == 403) {
                    getUserChatId()
                } else handleAdminRole()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showError("유저 Chat ID 조회 실패.")
            }
        })
    }


    private fun handleAdminRole() {
        startActivity(Intent(this, AdminDepartmentStoreSearchActivity::class.java))
        finish()
    }

    private fun getUserChatId() {
        val apiService = ApiClient.getClient().create(UserApiService::class.java)
        val call = apiService.getUserChatId()

        call.enqueue(object : Callback<UserChatMemberIdResponse> {
            override fun onResponse(
                call: Call<UserChatMemberIdResponse>,
                response: Response<UserChatMemberIdResponse>
            ) {
                if (response.isSuccessful) {
                    val userChatIdResponse = response.body()
                    userChatIdResponse?.let {
                        navigateToSelectMenu(it.memberChatId)
                    } ?: showError("유저의 고유 Chat number가 존재하지 않습니다.")
                } else {
                    Log.d("error", "유저 Chat ID 조회 실패.")
                }
            }

            override fun onFailure(call: Call<UserChatMemberIdResponse>, t: Throwable) {
                Log.d("error", "네트워크 오류: ${t.localizedMessage}")
            }
        })
    }

    private fun logout() {
        TokenManager.clearToken();
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToSelectMenu(memberChatId: Long) {
        Intent(this, SelectMenuActivity::class.java).apply {
            val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong("memberChatId", memberChatId) // Corrected key to "memberChatId"
            editor.apply()
            startActivity(this)
            finish()
        }
    }

    private fun showError(message: String) {
        loginErrorMessage.visibility = TextView.VISIBLE
        loginErrorMessage.text = message
    }
}
