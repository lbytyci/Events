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
