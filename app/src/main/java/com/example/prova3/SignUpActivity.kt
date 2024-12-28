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
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(textEmail, textPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val writeUserDetails = ReadWriteUserDetails(textFullName, textType, textMobile)

                    val referenceProfile = FirebaseDatabase.getInstance().getReference("RegisteredUsers")
                    firebaseUser?.let {
                        referenceProfile.child(it.uid).setValue(writeUserDetails)
                            .addOnCompleteListener { saveTask ->
                                if (saveTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "User registered successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = when (textType.lowercase()) {
                                        "participant" -> Intent(this, HomeParticipantActivity::class.java)
                                        "organization" -> Intent(this, HomeOrganizationActivity::class.java)
                                        else -> {
                                            Toast.makeText(this, "Unknown user type!", Toast.LENGTH_SHORT).show()
                                            null
                                        }
                                    }

                                    if (intent != null) {
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }

                                } else {
                                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    handleRegistrationError(task.exception)
                }
            }
    }

    private fun handleRegistrationError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                editTextRegisterPwd.error = "Weak password!"
                editTextRegisterPwd.requestFocus()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                editTextRegisterEmail.error = "Invalid email!"
                editTextRegisterEmail.requestFocus()
            }
            is FirebaseAuthUserCollisionException -> {
                editTextRegisterEmail.error = "Email already registered!"
                editTextRegisterEmail.requestFocus()
            }
            else -> {
                Log.e(TAG, exception?.message.orEmpty())
                Toast.makeText(this, exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
