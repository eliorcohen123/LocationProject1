package com.eliorcohen12345.locationproject.MapsDataPackage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.eliorcohen12345.locationproject.MainAndOtherPackage.EmailPasswordValidator
import com.eliorcohen12345.locationproject.MainAndOtherPackage.MainActivity
import com.eliorcohen12345.locationproject.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edittext_email
import kotlinx.android.synthetic.main.activity_login.edittext_password

class LoginActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    var email: String? = null
    var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setViewListeners()
    }

    override fun onStart() {
        super.onStart()

        checkUser()
    }

    private fun setViewListeners() {
        button_login.setOnClickListener { submit() }
        textview_forgot.setOnClickListener { launchForgot() }
        textview_register.setOnClickListener { launchRegister() }
    }

    private fun submit() {
        button_login.isEnabled = false

        email = edittext_email.text.toString()
        password = edittext_password.text.toString()

        if (validate()) {
            login()
        } else {
            showErrorMessage()
        }
    }

    private fun validate(): Boolean {
        return !email.isNullOrEmpty()
                && !password.isNullOrEmpty()
                && EmailPasswordValidator.getInstance().isValidEmail(email!!)
                && EmailPasswordValidator.getInstance().isValidPassword(password!!)
    }

    private fun login() {
        auth.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        launchMain()
                    } else {
                        showErrorMessage()
                    }
                }
    }

    private fun launchMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun launchRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun launchForgot() {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

    private fun showErrorMessage() {
        textview_error_login.visibility = View.VISIBLE
        button_login.isEnabled = true
    }

    private fun checkUser() {
        if (auth.currentUser != null)
            launchMain()
    }

}
