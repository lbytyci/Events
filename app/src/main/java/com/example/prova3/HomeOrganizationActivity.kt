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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val addTaskBtn = findViewById<ImageView>(R.id.addTaskBtn)
        addTaskBtn?.setOnClickListener {
            showAddEventPopup()
        } ?: Toast.makeText(this, "Add button not found", Toast.LENGTH_SHORT).show()


        setupRecyclerView()
        fetchEventsFromDatabase()


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
                R.id.search -> {
                    startActivity(
                        Intent(
                            applicationContext,
                            HomeOrganizationSearchActivity::class.java
                        )
                    )
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
        val recyclerView = findViewById<RecyclerView>(R.id.mainRecyclerView)
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
                    this@HomeOrganizationActivity,
                    "Failed to load events: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun showAddEventPopup() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_add_event, null)

        val eventNameInput = dialogView.findViewById<EditText>(R.id.eventNameInput)
        val eventTimeInput = dialogView.findViewById<EditText>(R.id.eventTimeInput)
        val eventDateInput = dialogView.findViewById<EditText>(R.id.eventDateInput)
        val eventDescriptionInput = dialogView.findViewById<EditText>(R.id.eventDescriptionInput)
        val eventPlaceInput = dialogView.findViewById<EditText>(R.id.eventPlaceInput)
        val ticketsAvailableInput = dialogView.findViewById<EditText>(R.id.eventTicketsInput)
        val ticketsPriceInput = dialogView.findViewById<EditText>(R.id.eventTicketPriceInput)
        val submitButton = dialogView.findViewById<Button>(R.id.submitButton)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()


        submitButton.setOnClickListener {
            val eventName = eventNameInput.text.toString().trim()
            val eventTime = eventTimeInput.text.toString().trim()
            val eventDate = eventDateInput.text.toString().trim()
            val eventDescription = eventDescriptionInput.text.toString().trim()
            val eventPlace = eventPlaceInput.text.toString().trim()
            val ticketsAvailable = ticketsAvailableInput.text.toString().trim()
            val ticketsPrice = ticketsPriceInput.text.toString().trim()

            val eventId = (1..100).random().toString()
            if (eventName.isEmpty() || eventTime.isEmpty() || eventDate.isEmpty() ||
                eventDescription.isEmpty() || eventPlace.isEmpty() || ticketsAvailable.isEmpty() || ticketsPrice.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                addEventToDatabase(
                    eventDate,
                    eventDescription,
                    eventId,
                    eventName,
                    eventPlace,
                    eventTime,
                    ticketsAvailable.toInt(),
                    ticketsPrice
                )
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun addEventToDatabase(
        date: String,
        description: String,
        id: String,
        name: String,
        place: String,
        time: String,
        ticketsAvailable: Int,
        ticketsPrice: String
    ) {
        val eventId = database.push().key
        if (eventId != null) {
            val event = Event(
                name = name,
                time = time,
                date = date,
                description = description,
                place = place,
                id = eventId,
                ticketsAvailable = ticketsAvailable,
                ticketsPrice = ticketsPrice
            )
            database.child(eventId).setValue(event).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to add event: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "Failed to generate event ID", Toast.LENGTH_SHORT).show()
        }
    }

}
