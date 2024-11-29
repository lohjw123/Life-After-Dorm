package com.example.lifeafterdorm

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeafterdorm.data.RentalRoom
import com.example.lifeafterdorm.dataAdapter.ListAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageRentalFragment : Fragment(), ListAdapter.OnDeleteClickListener {

    private lateinit var myToolbar: Toolbar
    private lateinit var rentalRoomList : ArrayList<RentalRoom>
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbRef : DatabaseReference
    private lateinit var listAdapter: ListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Manage Rental"
        val userId = arguments?.getString("userId")
        val view = inflater.inflate(R.layout.fragment_manage_rental, container, false)

        recyclerView = view.findViewById(R.id.rvRentalRoom)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        rentalRoomList = arrayListOf<RentalRoom>()

        listAdapter = ListAdapter(requireContext(), rentalRoomList)
        recyclerView.adapter = listAdapter
        fetchData(userId.toString())

        return view
    }

    private fun fetchData(currentUserId:String) {
        dbRef = FirebaseDatabase.getInstance().getReference("RentalRoom")
        val query = dbRef.orderByChild("userId").equalTo(currentUserId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rentalRoomList.clear()
                for (roomSnapshot in snapshot.children) {
                    val rentalRoom = roomSnapshot.getValue(RentalRoom::class.java)
                    rentalRoom?.let {
                        rentalRoomList.add(it)
                    }
                }
                listAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter.setOnDeleteClickListener(this)

    }

    override fun onDeleteClick(position: Int) {
        if (position >= 0 && position < rentalRoomList.size) {
            val postIdToDelete = rentalRoomList[position].roomId!!
            val databaseReference = FirebaseDatabase.getInstance().getReference("RentalRoom").child(postIdToDelete)
            val userId = arguments?.getString("userId")
            databaseReference.removeValue()
                .addOnSuccessListener {
                    rentalRoomList.removeAt(position - 1) // Remove the item at the clicked position
                    listAdapter.notifyItemRemoved(position) // Notify the adapter about the removed item
                    Toast.makeText(context, "Post Deleted Successfully", Toast.LENGTH_SHORT).show()
                    fetchData(userId.toString())
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Invalid item position", Toast.LENGTH_SHORT).show()
        }
    }



}