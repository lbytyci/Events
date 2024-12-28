package com.example.prova3

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova3.utils.Event
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*


class HomeOrganizationActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var eventAdapter: EventAdapter
    private val eventList = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_organization)

        supportActionBar?.title = "Home"


        try {
            database = FirebaseDatabase.getInstance().getReference("Events")
        } catch (e: Exception) {
            Toast.makeText(this, "Firebase initialization error: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
