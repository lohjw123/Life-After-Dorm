package com.example.lifeafterdorm

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeafterdorm.data.RentalRoom
import com.example.lifeafterdorm.dataAdapter.HomeAdapter
import com.example.lifeafterdorm.dataAdapter.HomeRecommendAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var myToolbar: Toolbar
    private lateinit var rentalRoomList : ArrayList<RentalRoom>
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewHome1: RecyclerView
    private lateinit var dbRef : DatabaseReference
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var homeRecommendAdapter: HomeRecommendAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Home"

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val imageViewHomeSearch : ImageView = view.findViewById(R.id.imageViewHomeSearch)
        dbRef = FirebaseDatabase.getInstance().getReference("RentalRoom")

        recyclerView = view.findViewById(R.id.recycleViewHomePopular)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        recyclerViewHome1 = view.findViewById(R.id.recyclerViewHome1)
        recyclerViewHome1.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerViewHome1.setHasFixedSize(true)

        rentalRoomList = arrayListOf<RentalRoom>()

        homeAdapter = HomeAdapter(requireContext(), rentalRoomList)
        recyclerView.adapter = homeAdapter

        homeRecommendAdapter = HomeRecommendAdapter(requireContext(), rentalRoomList)
        recyclerViewHome1.adapter = homeRecommendAdapter

        fetchData()

        imageViewHomeSearch.setOnClickListener{
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun fetchData(){
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (rentalRoomSnap in snapshot.children) {
                        val rentalRoom = rentalRoomSnap.getValue(RentalRoom::class.java)
                        rentalRoomList.add(rentalRoom!!)
                    }
                }
                homeAdapter.notifyDataSetChanged()
                homeRecommendAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }


}