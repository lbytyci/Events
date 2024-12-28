package com.example.prova3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prova3.utils.Event
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeOrganizationSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_organization_search)

        supportActionBar?.title = "Search"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.search

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> navigateToHome()
                R.id.search -> true
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


        val searchButton: Button = findViewById(R.id.search_button)
        val searchInput: EditText = findViewById(R.id.search_input)
        val recyclerView: RecyclerView = findViewById(R.id.mainRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val eventAdapter = EventAdapter(mutableListOf())
        recyclerView.adapter = eventAdapter


        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isNotBlank()) {
                searchEvents(query, eventAdapter)
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun navigateToHome(): Boolean {
        val databaseReference = FirebaseDatabase.getInstance().getReference("RegisteredUsers")
            .child(FirebaseAuth.getInstance().uid!!)
        databaseReference.child("type").get()
            .addOnSuccessListener { snapshot ->
                val type = snapshot.getValue(String::class.java)
                val targetActivity = if (type == "Participant") {
                    HomeParticipantActivity::class.java
                } else {
                    HomeOrganizationActivity::class.java
                }
                startActivity(Intent(applicationContext, targetActivity))
                finish()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error fetching user type: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        return true
    }


    private fun searchEvents(query: String, adapter: EventAdapter) {
        val database = Firebase.database
        val eventsRef = database.getReference("Events")
        val queryToSearch = query.trim()

        Log.d("SearchQuery", "Searching for: $queryToSearch")

        eventsRef.get()
            .addOnSuccessListener { snapshot ->
                val eventsList = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                val filteredEvents = eventsList.filter {
                    it.name.contains(queryToSearch, ignoreCase = true)
                }

                if (filteredEvents.isNotEmpty()) {
                    adapter.updateEvents(filteredEvents)
                } else {
                    Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseQueryError", "Error querying events: ${exception.message}")
                Toast.makeText(this, "Failed to retrieve events", Toast.LENGTH_SHORT).show()
            }
    }
}
