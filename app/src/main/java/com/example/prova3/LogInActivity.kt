package com.example.prova3

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class LogInActivity : AppCompatActivity() {
    private var editTextLogInEmail: EditText? = null
    private var editTextLogInPwd: EditText? = null
    private var authProfile: FirebaseAuth? = null
    private lateinit var homeParticipantIntent: Intent
    private lateinit var homeOrganizationIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        supportActionBar?.title = "Login"

        editTextLogInEmail = findViewById(R.id.inputEmail_login)
        editTextLogInPwd = findViewById(R.id.inputPass_login)

        authProfile = FirebaseAuth.getInstance()


        val textContinueLogIn = findViewById<TextView>(R.id.signUpText_login)
        textContinueLogIn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        homeParticipantIntent = Intent(this, HomeParticipantActivity::class.java)
        homeOrganizationIntent = Intent(this, HomeOrganizationActivity::class.java)



        val buttonLogin = findViewById<ImageView>(R.id.imageContinueMain_login)
        buttonLogin.setOnClickListener {
            val textEmail = editTextLogInEmail!!.text.toString()
            val textPwd = editTextLogInPwd!!.text.toString()

            when {
                TextUtils.isEmpty(textEmail) -> {
                    Toast.makeText(
                        this@LogInActivity, "Please enter your email!",
                        Toast.LENGTH_SHORT
                    ).show()
                    editTextLogInEmail!!.error = "Email is required"
                    editTextLogInEmail!!.requestFocus()
                }

                !Patterns.EMAIL_ADDRESS.matcher(textEmail).matches() -> {
                    Toast.makeText(
                        this@LogInActivity, "Please re-enter your email!",
                        Toast.LENGTH_SHORT
                    ).show()
                    editTextLogInEmail!!.error = "Valid email is required"
                    editTextLogInEmail!!.requestFocus()
                }

                TextUtils.isEmpty(textPwd) -> {
                    Toast.makeText(
                        this@LogInActivity, "Please enter your password!",
                        Toast.LENGTH_SHORT
                    ).show()
                    editTextLogInPwd!!.error = "Password is required"
                    editTextLogInPwd!!.requestFocus()
                }

                else -> loginUser(textEmail, textPwd)
            }
        }
    }

    private fun loginUser(email: String, pwd: String) {
        authProfile!!.signInWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(this@LogInActivity) { task ->
                if (task.isSuccessful) {
                    // Fetch the user type
                    val userId = authProfile!!.currentUser!!.uid
                    val userRef = FirebaseDatabase.getInstance().reference.child("RegisteredUsers").child(userId)
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userType = snapshot.child("type").value.toString()
                            when (userType.lowercase()) {
                                "participant" -> {
                                    Toast.makeText(this@LogInActivity, "You are logged in now!", Toast.LENGTH_SHORT).show()
                                    startActivity(homeParticipantIntent)
                                }
                                "organization" -> {
                                    Toast.makeText(this@LogInActivity, "You are logged in now!", Toast.LENGTH_SHORT).show()
                                    startActivity(homeOrganizationIntent)
                                }
                                else -> Toast.makeText(this@LogInActivity, "User type is not valid", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@LogInActivity, "Failed to fetch user type", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidUserException) {
                        editTextLogInEmail!!.error =
                            "User does not exist or is no longer valid. Please register again."
                        editTextLogInEmail!!.requestFocus()
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        editTextLogInPwd!!.error =
                            "Invalid credentials. Kindly, check and re-enter!"
                        editTextLogInPwd!!.requestFocus()
                    } catch (e: Exception) {
                        Log.e(TAG, e.message!!)
                        Toast.makeText(this@LogInActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(
                        this@LogInActivity,
                        "Something went wrong!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    companion object {
        private const val TAG = "LogInActivity"
    }
}
