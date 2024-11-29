package com.example.lifeafterdorm

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeafterdorm.data.RentalRoom
import com.example.lifeafterdorm.dataAdapter.SearchAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {

    private lateinit var dataList: ArrayList<RentalRoom>
    private lateinit var adapter: SearchAdapter
    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val userId = intent.getStringExtra("userId")
        val pageTitleTextView = findViewById<TextView>(R.id.pageTitle)
        if (pageTitleTextView != null) {
            pageTitleTextView.text = "Room Details"
        }

        findViewById<ImageView>(R.id.viewback).setOnClickListener {
            val intent = Intent(this, NavDrawerActivity::class.java).apply {
                putExtra("userId", userId)
            }
            startActivity(intent)
        }



        val gridLayoutManager = GridLayoutManager(this, 1)
        findViewById<RecyclerView>(R.id.recyclerView).layoutManager = gridLayoutManager
        val builder = AlertDialog.Builder(this)

        dataList = ArrayList()
        adapter = SearchAdapter(this, dataList)
        this.findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("RentalRoom")

        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(RentalRoom::class.java)
                    if (dataClass != null) {
                        dataList.add(dataClass)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                dataList.clear()
            }

        })

        searchView = findViewById<SearchView>(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })
    }

    fun searchList(text: String) {
        val searchList = ArrayList<RentalRoom>()
        for (dataClass in dataList) {
            if (dataClass.title.lowercase().contains(text.lowercase())) {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }
}