package com.example.prova3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.prova3.utils.Event
import com.google.firebase.database.FirebaseDatabase

class EventAdapter(private var eventList: MutableList<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.eventName)
        val eventTime: TextView = itemView.findViewById(R.id.eventTime)
        val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        val eventDescription: TextView = itemView.findViewById(R.id.eventDescription)
        val eventPlace: TextView = itemView.findViewById(R.id.eventPlace)
        val eventImage: ImageView = itemView.findViewById(R.id.eventImage)
        val buyTicketsButton: Button = itemView.findViewById(R.id.buyTicketsButton)
        val ticketsNumber: TextView = itemView.findViewById(R.id.ticketsNumber)
        val eventPrice: TextView = itemView.findViewById(R.id.ticketsPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]

        // Set event details
        holder.eventName.text = event.name
        holder.eventTime.text = event.time
        holder.eventDate.text = event.date
        holder.eventDescription.text = event.description
        holder.eventPlace.text = event.place
        holder.ticketsNumber.text = "Available Tickets: ${event.ticketsAvailable}"
        holder.eventPrice.text = "Price: ${event.ticketsPrice}"
        holder.eventImage.setImageResource(getRandomDrawable())

        // Handle ticket purchase
        holder.buyTicketsButton.setOnClickListener {
            handleTicketPurchase(holder, event, position)
        }
    }

    override fun getItemCount(): Int = eventList.size

    private fun handleTicketPurchase(holder: EventViewHolder, event: Event, position: Int) {
        if (event.ticketsAvailable > 0) {
            event.ticketsAvailable--
            event.userInterested = true
            notifyItemChanged(position)

            // Update event in Firebase
            val databaseRef = FirebaseDatabase.getInstance().getReference("Events")
            databaseRef.child(event.id).setValue(event)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            holder.itemView.context,
                            "Ticket purchased successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            holder.itemView.context,
                            "Failed to update tickets: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                holder.itemView.context,
                "No tickets available!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Utility function to get a random drawable for event images
    private fun getRandomDrawable(): Int {
        val drawableList = listOf(
            R.drawable.event1,
            R.drawable.event2,
            R.drawable.event3,
            R.drawable.event4,
            R.drawable.event5,
            R.drawable.event6
        )
        return drawableList.random()
    }

    // Update the entire event list
    fun updateEvents(newEventList: List<Event>) {
        eventList.clear()
        eventList.addAll(newEventList)
        notifyDataSetChanged()
    }

    // Update specific event details
    fun updateEventDetails(eventId: String, tickets: Int, price: String) {
        val event = eventList.find { it.id == eventId }
        event?.let {
            it.ticketsAvailable = tickets
            it.ticketsPrice = price
            notifyDataSetChanged()
        }
    }
}
