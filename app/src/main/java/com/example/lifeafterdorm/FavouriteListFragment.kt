package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeafterdorm.data.RentalRoom
import com.example.lifeafterdorm.dataAdapter.FavouriteListAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavouriteListFragment : Fragment() {

    private lateinit var myToolbar: Toolbar
    private lateinit var favoriteRoomsAdapter: FavouriteListAdapter
    private lateinit var favoriteRoomsList: MutableList<RentalRoom>
    private val userId : String = "U00001"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Favourite List"

        val userId = arguments?.getString("userId")

        val view = inflater.inflate(R.layout.fragment_favourite_list, container, false)

        favoriteRoomsList = mutableListOf()
        favoriteRoomsAdapter = FavouriteListAdapter(requireContext(), favoriteRoomsList)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycleViewFavouriteList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favoriteRoomsAdapter

        fetchFavoriteRooms(userId.toString())

        return view
    }

    private fun fetchFavoriteRooms(currentUserId : String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("User").child(currentUserId).child("Favorite")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (roomSnapshot in snapshot.children) {
                    val roomId = roomSnapshot.key
                    if (roomId != null) {
                        fetchRoomDetails(roomId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun fetchRoomDetails(roomId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("RentalRoom").child(roomId)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue(RentalRoom::class.java)
                room?.let {
                    favoriteRoomsList.add(room)
                    favoriteRoomsAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}