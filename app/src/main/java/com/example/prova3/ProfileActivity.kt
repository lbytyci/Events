package com.example.prova3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ProfileActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var userNameTextView: TextView
    private lateinit var userTypeTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = "Profile"



        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userUid == null) {
            return
        }


        database = FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(userUid)


        userNameTextView = findViewById(R.id.textView5)
        userTypeTextView = findViewById(R.id.textView3)


        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userType = snapshot.child("type").getValue(String::class.java)
                    val userName = snapshot.child("fullName").getValue(String::class.java)
                    userNameTextView.text = userName ?: "User Name"
                    userTypeTextView.text = userType ?: "No type selected"
                } else {
                    userNameTextView.text = "User Name not found"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                userNameTextView.text = "Error fetching user data"
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers").child(FirebaseAuth.getInstance().uid!!)
                    databaseReference.child("type").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val type = snapshot.getValue(String::class.java)
                            if (type == "Participant") {
                                startActivity(Intent(applicationContext, HomeParticipantActivity::class.java))
                                finish()
                            } else if (type == "Organization") {
                                startActivity(Intent(applicationContext, HomeOrganizationActivity::class.java))
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, "Error fetching user type: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                    true
                }

                R.id.search -> {
                    startActivity(Intent(applicationContext, HomeOrganizationSearchActivity::class.java))
                    finish()
                    true
                }
                R.id.tickets -> {
                    startActivity(Intent(applicationContext, TicketsActivity::class.java))
                    finish()
                    true
                }
                R.id.profile -> {
                    true
                }
                else -> false
            }
        }

        val signOutTextView: TextView = findViewById(R.id.textView4)
        signOutTextView.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }
}