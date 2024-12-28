package com.example.prova3

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var editTextRegisterFullName: EditText
    private lateinit var editTextRegisterEmail: EditText
    private lateinit var editTextRegisterMobile: EditText
    private lateinit var editTextRegisterPwd: EditText
    private lateinit var editTextRegisterConfirmPwd: EditText
    private lateinit var radioGroupRegisterType: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.title = "Register"

        Toast.makeText(this, "You can register now", Toast.LENGTH_SHORT).show()

        editTextRegisterFullName = findViewById(R.id.editText_register_name)
        editTextRegisterEmail = findViewById(R.id.editText_register_email)
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile)
        editTextRegisterPwd = findViewById(R.id.editText_register_password)
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password)
        radioGroupRegisterType = findViewById(R.id.radio_group_register_type)
        radioGroupRegisterType.clearCheck()

        val textContinueLogIn = findViewById<TextView>(R.id.textView_register_login)
        textContinueLogIn.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

        val imageRegister: ImageView = findViewById(R.id.imageView_register_continue)
        imageRegister.setOnClickListener {
            handleRegistration()
        }
    }

    private fun handleRegistration() {
        val selectedTypeId = radioGroupRegisterType.checkedRadioButtonId
        if (selectedTypeId == -1) {
            Toast.makeText(
                this,
                "Please select if you are an Organization or Participant",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val radioButtonRegisterTypeSelected: RadioButton = findViewById(selectedTypeId)
        val textFullName = editTextRegisterFullName.text.toString().trim()
        val textEmail = editTextRegisterEmail.text.toString().trim()
        val textMobile = editTextRegisterMobile.text.toString().trim()
        val textPassword = editTextRegisterPwd.text.toString().trim()
        val textConfirmPassword = editTextRegisterConfirmPwd.text.toString().trim()
        val textType = radioButtonRegisterTypeSelected.text.toString()


        when {
            textFullName.isEmpty() -> {
                editTextRegisterFullName.error = "Full name is required!"
                editTextRegisterFullName.requestFocus()
            }
            textEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(textEmail).matches() -> {
                editTextRegisterEmail.error = "Valid email is required!"
                editTextRegisterEmail.requestFocus()
            }
            textMobile.isEmpty() || textMobile.length != 11 -> {
                editTextRegisterMobile.error = "Phone number must be 11 digits!"
                editTextRegisterMobile.requestFocus()
            }
            textPassword.isEmpty() || textPassword.length < 8 -> {
                editTextRegisterPwd.error = "Password must be at least 8 characters!"
                editTextRegisterPwd.requestFocus()
            }
            textPassword != textConfirmPassword -> {
                editTextRegisterConfirmPwd.error = "Passwords must match!"
                editTextRegisterConfirmPwd.requestFocus()
            }
            else -> {
                registerUser(textFullName, textEmail, textType, textMobile, textPassword)
            }
        }
    }

    private fun registerUser(
        textFullName: String,
        textEmail: String,
        textType: String,
        textMobile: String,
        textPassword: String
    )
