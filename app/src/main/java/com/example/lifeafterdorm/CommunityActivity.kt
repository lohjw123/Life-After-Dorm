package com.example.lifeafterdorm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeafterdorm.data.CommunityPost
import com.example.lifeafterdorm.data.RentalRoom
import com.example.lifeafterdorm.data.User
import com.example.lifeafterdorm.dataAdapter.CommunityPostAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommunityActivity : AppCompatActivity() {

    private lateinit var rentalRoomList : MutableMap<String, RentalRoom>
    private lateinit var userList : MutableMap<String, User>
    private lateinit var communityPostList : ArrayList<CommunityPost>
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbRef : DatabaseReference
    private lateinit var communityPostAdapter: CommunityPostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        val userId = intent.getStringExtra("userId")
        val pageTitleTextView = findViewById<TextView>(R.id.pageTitle)
        val layoutCommunityPost : LinearLayout = findViewById(R.id.layoutCommunityPost)

        if (pageTitleTextView != null) {
            pageTitleTextView.text = "Community"
        }

        findViewById<ImageView>(R.id.viewback).setOnClickListener {
            val intent = Intent(this, NavDrawerActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recycleViewCommunnity)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val viewBack = findViewById<ImageView>(R.id.viewback)

        rentalRoomList = mutableMapOf()
        userList = mutableMapOf()
        communityPostList = arrayListOf<CommunityPost>()

        communityPostAdapter = CommunityPostAdapter(this, communityPostList, userList, rentalRoomList)
        recyclerView.adapter = communityPostAdapter

        fetchDataUserList()
        fetchDataRoomList()
        fetchDataCommunityPostList()

        layoutCommunityPost.setOnClickListener() {
            buttomDialog()
        }

    }

    private fun buttomDialog() {
        val dialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(R.layout.community_buttom_sheet_dialog, null)

        val btnClose: LinearLayout = view.findViewById(R.id.layoutCommunityClose)
        val layoutCommunitySortByDate : LinearLayout = view.findViewById(R.id.layoutCommunitySortByDate)
        val layoutCommunitySortByDateDesc : LinearLayout = view.findViewById(R.id.layoutCommunitySortByDateDesc)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        layoutCommunitySortByDate.setOnClickListener {
            fetchDataCommunityPostListByDateTimeAscending()
            dialog.dismiss()
        }

        layoutCommunitySortByDateDesc.setOnClickListener {
            fetchDataCommunityPostListByDateTimeDescending()
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun fetchDataRoomList(){
        dbRef = FirebaseDatabase.getInstance().getReference("RentalRoom")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (rentalRoomSnap in snapshot.children) {
                        val rentalRoom = rentalRoomSnap.getValue(RentalRoom::class.java)
                        rentalRoom?.let { rentalRoomList[it.roomId] = it }
                    }
                }
                communityPostAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CommunityActivity, "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchDataUserList(){
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (rentalRoomSnap in snapshot.children) {
                        val user = rentalRoomSnap.getValue(User::class.java)
                        user?.let { userList[it.id] = it }
                    }
                }
                communityPostAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CommunityActivity, "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchDataCommunityPostList(){
        dbRef = FirebaseDatabase.getInstance().getReference("CommunityPost")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (rentalRoomSnap in snapshot.children) {
                        val communityPost = rentalRoomSnap.getValue(CommunityPost::class.java)
                        communityPostList.add(communityPost!!)
                    }
                }
                communityPostAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CommunityActivity, "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchDataCommunityPostListByDateTimeAscending(){
        dbRef = FirebaseDatabase.getInstance().getReference("CommunityPost")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val tempList = mutableListOf<CommunityPost>()
                    for (postSnap in snapshot.children) {
                        val communityPost = postSnap.getValue(CommunityPost::class.java)
                        communityPost?.let { tempList.add(it) }
                    }
                    // Sort the tempList by postDateTime in ascending order
                    tempList.sortBy { it.postDateTime }
                    // Clear the existing list and add sorted data
                    communityPostList.clear()
                    communityPostList.addAll(tempList)
                    communityPostAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CommunityActivity, "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchDataCommunityPostListByDateTimeDescending(){
        dbRef = FirebaseDatabase.getInstance().getReference("CommunityPost")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val tempList = mutableListOf<CommunityPost>()
                    for (postSnap in snapshot.children) {
                        val communityPost = postSnap.getValue(CommunityPost::class.java)
                        communityPost?.let { tempList.add(it) }
                    }
                    // Sort the tempList by postDateTime in descending order
                    tempList.sortByDescending { it.postDateTime }
                    // Clear the existing list and add sorted data
                    communityPostList.clear()
                    communityPostList.addAll(tempList)
                    communityPostAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CommunityActivity, "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

}