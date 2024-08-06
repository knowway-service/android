package com.knowway.ui.activity.user

import android.content.Intent
import android.content.SharedPreferences
import com.knowway.R
import com.knowway.data.network.ApiClient

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.knowway.data.model.user.EmailDuplicationRequest
import com.knowway.data.model.user.RegisterRequest
import com.knowway.data.network.user.UserApiService
import com.knowway.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var emailValidationMessage: TextView
    private lateinit var emailDuplicateError: TextView
    private lateinit var passwordErrorMessage: TextView
    private lateinit var passwordMismatchError: TextView

    private var isEmailAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {

        ApiClient.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        emailInput = findViewById(R.id.input_email)
        passwordInput = findViewById(R.id.input_password)
        confirmPasswordInput = findViewById(R.id.input_password_confirm)
        emailValidationMessage = findViewById(R.id.email_validation_message)
        emailDuplicateError = findViewById(R.id.email_duplicate_error)
        passwordErrorMessage = findViewById(R.id.password_error_message)
        passwordMismatchError = findViewById(R.id.password_mismatch_error)

        findViewById<View>(R.id.duplication_check_button).setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isNotEmpty()) {
                if (isValidEmail(email)) {
                    checkEmailDuplication(email)
                } else {
                    showError("이메일 형식이 올바르지 않습니다.")
                }
            } else {
                showError("이메일을 입력해주세요.")
            }
        }

        findViewById<View>(R.id.sign_up_button).setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (!isValidEmail(email)) {
                    showError("이메일 형식이 올바르지 않습니다.")
                }
                if (!isValidPassword(password)) {
                    showPasswordError("한 개의 대문자, 소문자, 숫자를 포함해주세요.")
                }
                if (!isEmailAvailable) {
                    showError("중복 체크를 해주세요")
                }
                if (password != confirmPassword) {
                    passwordMismatchError.visibility = View.VISIBLE
                } else if (isEmailAvailable && isValidEmail(email) && isValidPassword(password)) {
                    performSignup(email, password)
                }
            } else {
                passwordMismatchError.text = "모든 필드를 입력해주세요."
                passwordMismatchError.visibility = View.VISIBLE
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    private fun checkEmailDuplication(email: String) {
        val apiService = ApiClient.getClient().create(UserApiService::class.java)
        val checkEmailCall = apiService.checkEmail(EmailDuplicationRequest(email))

        checkEmailCall.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    isEmailAvailable = true
                    emailValidationMessage.visibility = View.VISIBLE
                    emailDuplicateError.visibility = View.GONE
                } else {
                    isEmailAvailable = false
                    emailValidationMessage.visibility = View.GONE
                    emailDuplicateError.visibility = View.VISIBLE
                    showError("이메일이 중복되었습니다.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
            }
        })
    }

    private fun performSignup(email: String, password: String) {
        val apiService = ApiClient.getClient().create(UserApiService::class.java)
        val signupCall = apiService.registerUser(RegisterRequest(email, password))

        signupCall.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    passwordMismatchError.text = "회원가입에 실패했습니다."
                    passwordMismatchError.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
            }
        })
    }

    private fun showError(message: String) {
        emailDuplicateError.text = message
        emailDuplicateError.visibility = View.VISIBLE
    }

    private fun showPasswordError(message: String) {
        passwordErrorMessage.text = message
        passwordErrorMessage.visibility = View.VISIBLE
    }

}
