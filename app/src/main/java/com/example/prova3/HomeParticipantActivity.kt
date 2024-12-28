package com.example.prova3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova3.utils.Event
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class HomeParticipantActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var eventAdapter: EventAdapter
    private val eventList = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_participant)

        supportActionBar?.title = "Home"



        try {
            database = FirebaseDatabase.getInstance().getReference("Events")
        } catch (e: Exception) {
            Toast.makeText(this, "Firebase initialization error: ${e.message}", Toast.LENGTH_SHORT).show()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setupRecyclerView()
        fetchEventsFromDatabase()


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
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
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.main2RecyclerView)
        if (recyclerView != null) {
            eventAdapter = EventAdapter(eventList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = eventAdapter
        } else {
            Toast.makeText(this, "RecyclerView not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchEventsFromDatabase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                if (!snapshot.exists()) {
                    Toast.makeText(this@HomeParticipantActivity, "No data found", Toast.LENGTH_SHORT).show()
                }
                for (dataSnapshot in snapshot.children) {
                    val event = dataSnapshot.getValue(Event::class.java)
                    if (event != null) {
                        eventList.add(event)
                    }
                }
                eventAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@HomeParticipantActivity,
                    "Failed to load events: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
