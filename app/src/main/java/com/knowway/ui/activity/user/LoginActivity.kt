package com.knowway.ui.activity.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.knowway.R
import com.knowway.data.model.user.LoginRequest
import com.knowway.data.model.user.LoginResponse
import com.knowway.data.model.user.UserChatMemberIdResponse
import com.knowway.data.model.user.UserRole
import com.knowway.data.network.ApiClient
import com.knowway.data.network.user.UserApiService
import com.knowway.ui.activity.SelectMenuActivity
import com.knowway.ui.activity.AdminDepartmentStoreSearchActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var emailInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginButton: ImageButton
    lateinit var loginErrorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.input_email)
        passwordInput = findViewById(R.id.input_password)
        loginButton = findViewById(R.id.login_button)
        loginErrorMessage = findViewById(R.id.login_failed_txt)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (!isValidEmail(email)) {
                    showError("유효한 이메일을 입력해주세요.")
                } else {
                    performLogin(email, password)
                }
            } else {
                showError("이메일과 패스워드를 입력해주세요.")
            }
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    fun performLogin(email: String, password: String) {
        val apiService = ApiClient.getClient().create(UserApiService::class.java)
        val loginCall = apiService.login(LoginRequest(email, password))

        loginCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        // After a successful login, get the user chat ID
                        getUserChatId(loginResponse.role)
                    } else {
                        showError("로그인 실패. 서버 응답이 잘못되었습니다.")
                    }
                } else {
                    showError("로그인 실패. 이메일 또는 비밀번호를 확인하세요.")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showError("네트워크 오류: ${t.localizedMessage}")
            }
        })
    }

    fun getUserChatId(userRole: UserRole) {
        val apiService = ApiClient.getClient().create(UserApiService::class.java)
        val call = apiService.getUserChatId()

        call.enqueue(object : Callback<UserChatMemberIdResponse> {
            override fun onResponse(
                call: Call<UserChatMemberIdResponse>,
                response: Response<UserChatMemberIdResponse>
            ) {
                if (response.isSuccessful) {
                    val userChatIdResponse = response.body()
                    if (userChatIdResponse != null) {
                        val memberChatId = userChatIdResponse.memberChatId
                        navigateToNextActivity(userRole, memberChatId)
                    } else {
                        showError("유저의 고유 Chat number가 존재하지 않습니다.")
                    }
                } else {
                    showError("유저 Chat ID 조회 실패.")
                }
            }

            override fun onFailure(call: Call<UserChatMemberIdResponse>, t: Throwable) {
                showError("네트워크 오류: ${t.localizedMessage}")
            }
        })
    }

    private fun navigateToNextActivity(userRole: UserRole, memberChatId: Long) {
        val intent = when (userRole) {
            UserRole.ADMIN -> Intent(this, AdminDepartmentStoreSearchActivity::class.java).apply {
            }
            else -> {
                Intent(this, SelectMenuActivity::class.java).apply {
                    val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putLong("memberChatId", memberChatId)
                    editor.apply()
                }
            }
        }

        startActivity(intent)
        finish()
    }

    fun showError(message: String) {
        loginErrorMessage.visibility = TextView.VISIBLE
        loginErrorMessage.text = message
    }
}
