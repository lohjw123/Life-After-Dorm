package com.example.lifeafterdorm

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.lifeafterdorm.controller.separateCountryCodeAndNumber
import com.example.lifeafterdorm.data.CommunityPost
import com.example.lifeafterdorm.data.Location
import com.example.lifeafterdorm.data.Rating
import com.example.lifeafterdorm.data.RentalRoom
import com.example.lifeafterdorm.data.User
import com.example.lifeafterdorm.databinding.ActivityRoomDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar

class RoomDetailsActivity : AppCompatActivity() {

    private lateinit var roomId: String
    private lateinit var roomUserId: String
    private lateinit var contactNum: String
    private lateinit var binding: ActivityRoomDetailsBinding
    private lateinit var dbRef : DatabaseReference
    private var lastRecordId : String=""
    private lateinit var rentalRoom : RentalRoom
    private lateinit var btnActivityRoomDetailAddToFavourite: Button
    private val matchedUsers: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getStringExtra("userId")
        if(userId != null){
        val pageTitleTextView = findViewById<TextView>(R.id.pageTitle)
        if (pageTitleTextView != null) {
            pageTitleTextView.text = "Room Details"
        }

        findViewById<ImageView>(R.id.viewback).setOnClickListener {
            val intent = Intent(this, NavDrawerActivity::class.java)
            startActivity(intent)
        }

        val bundle = intent.extras
        if (bundle != null) {
            roomId = bundle.getString("roomId") ?: ""
            roomUserId = bundle.getString("roomUserId") ?: ""
            contactNum = bundle.getString("contactNum") ?: ""
            fetchData(roomId)
        } else {
            Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show()
            finish()
        }

        checkRoomFavouriteStatus(userId)

        val btnActivityRoomDetailContactOwner: Button = findViewById(R.id.btnActivityRoomDetailContactOwner)
        val btnActivityRoomDetailFindRoommate: Button = findViewById(R.id.btnActivityRoomDetailFindRoommate)
        btnActivityRoomDetailAddToFavourite = findViewById(R.id.btnActivityRoomDetailAddToFavourite)

        btnActivityRoomDetailContactOwner.setOnClickListener {
            buttomDialog(userId)
        }

        btnActivityRoomDetailFindRoommate.setOnClickListener {
            buttomDialogFindRoommate(userId)
        }

        btnActivityRoomDetailAddToFavourite.setOnClickListener {
            addToFavorites(userId)
        }

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("CommunityPost")
        val query = reference.orderByKey().limitToLast(1)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val lastRecord = childSnapshot.getValue(CommunityPost::class.java)
                        lastRecordId = lastRecord!!.postId
                    }
                } else {
                    println("No data available.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Failed to retrieve data: ${databaseError.message}")
            }
        })

            val masterActivityRoomUserRating : RatingBar = findViewById(R.id.masterActivityRoomUserRating)
            val btnRate = findViewById<Button>(R.id.btnRate)
            btnRate?.setOnClickListener {
                val userRating = masterActivityRoomUserRating.rating.toString()
                Toast.makeText(this, "Rated: " + userRating, Toast.LENGTH_SHORT).show()
                saveRatingData(userRating, userId)
                calculateRoomRating()
            }
        }
    }

    private fun buttomDialog(userid: String) {
        val dialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(R.layout.room_details_bottom_sheet_dialog, null)

        val btnClose: LinearLayout = view.findViewById(R.id.layoutUpload)
        val btnContactInWhatsapp : LinearLayout = view.findViewById(R.id.layoutContactWhatsapp)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnContactInWhatsapp.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Updating Image")
            progressDialog.setMessage("Please wait while we update your profile image...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            dbRef = FirebaseDatabase.getInstance().getReference("User")
            val phoneListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnap in snapshot.children) {
                            val user = userSnap.getValue(User::class.java)
                            if (user != null && user.id == userid) {
                                sendWhatsAppMessage(user.phoneNum)
                                progressDialog.dismiss()
                                break
                            }
                        }
                    }
                    dismissLoadingDialog()
                }

                override fun onCancelled(error: DatabaseError) {
                    dismissLoadingDialog()
                    progressDialog.dismiss()
                    Toast.makeText(this@RoomDetailsActivity, "Failed to fetch phone number", Toast.LENGTH_SHORT).show()
                }
            }
            dbRef.addValueEventListener(phoneListener)
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private var progressDialog: ProgressDialog? = null

    private fun showLoadingDialog() {
        dismissLoadingDialog()
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun dismissLoadingDialog() {
        progressDialog?.dismiss()
    }

    private fun sendWhatsAppMessage(phoneNumber: String) {
        val message = "Hello! I'm interested in renting your property. Could you please provide me with more information?"
        val uri = Uri.parse("https://wa.me/$phoneNumber/?text=${URLEncoder.encode(message, "UTF-8")}")
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)

        try {
            startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            val appPackageName = "com.whatsapp"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }
    }
    private fun buttomDialogFindRoommate(userid:String) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.room_details_findroommate_buttom_sheet_dialog, null)
        val btnClose: LinearLayout = view.findViewById(R.id.layoutFindRoommate)
        val btnPostToCommunity : LinearLayout = view.findViewById(R.id.layoutPostToCommunity)
        val btnRoommateMatching : LinearLayout = view.findViewById(R.id.layoutRoommateMatching)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnPostToCommunity.setOnClickListener {
            addPostToCommunity(userid)
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }

        btnRoommateMatching.setOnClickListener {
            val text = binding.tvActivityStatusValue.text.toString()
            val currentUserGender = when (text.toLowerCase()) {
                "Male" -> "M"
                "Female" -> "F"
                else -> {
                    ""
                }
            }
            searchSameGenderUsers(currentUserGender)
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun addPostToCommunity(userid:String) {
        dbRef = FirebaseDatabase.getInstance().getReference("CommunityPost")
        val postId = dbRef.push().key ?: ""
        saveDataToDatabase(postId, userid)
    }

    private fun saveDataToDatabase(id: String, currentUserID:String) {
        val postTime = getCurrentTime()
        val postContent = "Want to find Roommate for Room "
        val communityPost = CommunityPost(id, currentUserID, roomId, postTime.toString(), postContent)
        dbRef = FirebaseDatabase.getInstance().getReference("CommunityPost")

        dbRef.child(id).setValue(communityPost)
            .addOnCompleteListener{
                Toast.makeText(this, "Data saved", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Error ${it.toString()}", Toast.LENGTH_LONG).show()
            }
    }

    private fun postIdIncrement() {

    }

    private fun fetchData(roomId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("RentalRoom").child(roomId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val rentalRoomResult = snapshot.getValue(RentalRoom::class.java)
                    if (rentalRoomResult != null) {
                        rentalRoom = rentalRoomResult
                        updateUi()
                    }
                } else {
                    Toast.makeText(this@RoomDetailsActivity, "Error", Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RoomDetailsActivity, "Error " + "{$error}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateUi() {
        binding.tvActivityRoomDetailsName.text = rentalRoom.roomType1
        binding.tvActivityRoomDetailsPrice.text = "RM" + rentalRoom.monthlyRental
        binding.tvActivityRoomDetailAddressValue.text = rentalRoom.address
        binding.tvActivityRoomDetailDescValue.text = rentalRoom.editTextTextMultiLineDesc
        binding.tvActivityTitle.text = rentalRoom.title
        binding.tvActivityMaxPersonValue.text = rentalRoom.maxPerson
        binding.tvActivityStatusValue.text = rentalRoom.status
        binding.tvActivityRaceValue.text = rentalRoom.race
        binding.tvAmenitiesUtilitiesDetailsValue.text = rentalRoom.checkedValues
        binding.tvActivityLeaseTermValue.text = rentalRoom.leaseTerm
        binding.tvRoomRating.text = rentalRoom.roomRating + "/5.0"
        Glide.with(this).load(rentalRoom.imageUri).into(binding.imageView3Activity)
    }

    private fun saveRatingData(userRating :String, userid: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Rating").child(roomId).child(userid)
        val ratingTime = getCurrentTime()

        val rating = Rating(userid, roomId, userRating, ratingTime)
        dbRef.setValue(rating)
            .addOnSuccessListener {
                Toast.makeText(this, "Rating saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save rating: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun calculateRoomRating() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Rating").child(roomId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalRating = 0.0
                var numRatings = 0
                for (ratingSnapshot in snapshot.children) {
                    val ratingValue = ratingSnapshot.child("ratingValue").getValue(String::class.java)
                    ratingValue?.let {
                        val rating = it.toDoubleOrNull()
                        rating?.let {
                            totalRating += it
                            numRatings = rentalRoom.numOfRating.toInt() + 1
                        }
                    }
                }
                val averageRating = totalRating / numRatings
                updateRoomRatingInFirebase(averageRating, numRatings)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RoomDetailsActivity, "Failed to fetch ratings: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRoomRatingInFirebase(averageRating: Double, numOfRating: Int) {
        val dbRef = FirebaseDatabase.getInstance().getReference("RentalRoom").child(roomId)

        dbRef.child("roomRating").setValue(averageRating.toString())
            .addOnSuccessListener {
                dbRef.child("numOfRating").setValue(numOfRating.toString())
                    .addOnSuccessListener {
//                        Toast.makeText(this, "Room rating and numOfRating updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update numOfRating: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update room rating: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addToFavorites(userid: String) {
        val userId = userid
        val dbRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("Favorite").child(roomId)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    dbRef.removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this@RoomDetailsActivity, "Room removed from favorites", Toast.LENGTH_SHORT).show()
                            btnActivityRoomDetailAddToFavourite.text = "Add to Favorites"
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@RoomDetailsActivity, "Failed to remove room from favorites: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    dbRef.setValue(true)
                        .addOnSuccessListener {
                            Toast.makeText(this@RoomDetailsActivity, "Room added to favorites", Toast.LENGTH_SHORT).show()
                            btnActivityRoomDetailAddToFavourite.text = "Remove from Favorites"
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@RoomDetailsActivity, "Failed to add room to favorites: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RoomDetailsActivity, "Failed to check favorites: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkRoomFavouriteStatus(userid: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("User").child(userid).child("Favorite").child(roomId)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    btnActivityRoomDetailAddToFavourite.text = "Remove from Favorites"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RoomDetailsActivity, "Failed to check favorites: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCurrentTime() : String{
        val currentTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val postTime = formatter.format(currentTime)
        return postTime.toString()
    }

    private fun displayUserInfo(user: User) {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        dialogBuilder.setTitle("User Information")
        dialogBuilder.setMessage("User ID: ${user.id}\nName: ${user.name}\nEmail: ${user.email}\nGender: ${user.gender}")
        dialogBuilder.setPositiveButton("Contact") { _, _ ->
            contactUserOnWhatsApp(user)
        }
        dialogBuilder.setNegativeButton("Search Same Gender") { _, _ ->
            searchSameGenderUsers(user.gender)
        }
        dialogBuilder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    // 联络用户 WhatsApp
    private fun contactUserOnWhatsApp(user: User) {
        val phoneNumber = user.phoneNum
        if (phoneNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phoneNumber"))
            startActivity(intent)
        } else {
            Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchSameGenderUsers(gender: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("User")
        val query = dbRef.orderByChild("gender").equalTo(gender)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                matchedUsers.clear()
                for (childSnapshot in snapshot.children) {
                    val user = childSnapshot.getValue(User::class.java)
                    user?.let { matchedUsers.add(it) }
                }
                if (matchedUsers.isNotEmpty()) {
                    val randomUser = matchedUsers.random()
                    displayUserInfo(randomUser)
                } else {
                    Toast.makeText(this@RoomDetailsActivity, "No matching users found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error: ${error.message}")
            }
        })
    }
}