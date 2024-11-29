package com.example.lifeafterdorm

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.example.lifeafterdorm.data.RentalRoom
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar

class PostRental4Fragment : Fragment() {

    private lateinit var myToolbar: Toolbar
    private lateinit var imgUri : Uri
    private lateinit var imageViewUpload : ImageView
    private lateinit var dbRef : DatabaseReference
    private lateinit var storageRef : StorageReference
    private var imageURL : String=""
    private lateinit var editTextTextMultiLineDesc : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Rental Room"

        val view = inflater.inflate(R.layout.fragment_post_rental4, container, false)
        val userId = arguments?.getString("userId")
        val tvPostRental4Desc : TextView = view.findViewById(R.id.tvPostRental4Desc)
        editTextTextMultiLineDesc = view.findViewById(R.id.editTextTextMultiLineDesc)
        val btnPostRentalUploadImage : Button = view.findViewById(R.id.btnPostRentalUploadImage)
        val btnPostRental4Submit : Button = view.findViewById(R.id.btnPostRental4Submit)
        imageViewUpload = view.findViewById(R.id.imageViewUpload)

        dbRef = FirebaseDatabase.getInstance().getReference("RentalRoom")
        val roomId = dbRef.push().key ?: ""


        btnPostRentalUploadImage.setOnClickListener() {
            startForResult.launch("image/*")
        }

        imageViewUpload.setOnClickListener() {
            startForResult.launch("image/*")
        }

//        tvPostRental4Desc.text = arguments?.getString("checkedValues").toString()

        btnPostRental4Submit.setOnClickListener() {
            val fragment = RentalMainFragment()

            if (userId != null) {
                uploadImageToFirebase(roomId, userId)
            }

//            val rentalPost = RentalRoom(roomId, userId, arguments?.getString("location").toString(), arguments?.getString("roomType1").toString(), arguments?.getString("roomType2").toString(), arguments?.getString("contactNum").toString(), arguments?.getString("monthlyRental").toString(), arguments?.getString("maxPerson").toString(), arguments?.getString("address").toString(), arguments?.getString("title").toString(), arguments?.getString("status").toString(), arguments?.getString("race").toString(), arguments?.getString("leaseTerm").toString(), arguments?.getString("checkedValues").toString(), editTextTextMultiLineDesc.text.toString(), imgUri.toString())
//            dbRef.child(rentalPost.roomId).setValue(rentalPost)
//                .addOnCompleteListener{
//                    Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_LONG).show()
//
//                }
//                .addOnFailureListener{
//                    Toast.makeText(requireContext(), "Error ${it.toString()}", Toast.LENGTH_LONG).show()
//                }
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }

    //error when empty
    private fun uploadImageToFirebase(id:String, userId: String) {
        storageRef = FirebaseStorage.getInstance().reference
        val imgRef = storageRef.child("room_img/${id}.png")
        imgRef.putFile(imgUri)
            .addOnSuccessListener { taskSnapshot ->
                imgRef.downloadUrl.addOnSuccessListener { uri ->
                    imageURL = uri.toString()
                    Toast.makeText(requireContext(), "Image upload successful", Toast.LENGTH_LONG).show()
                    saveDataToDatabase(id, userId)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to get image URL", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveDataToDatabase(id: String, userId:String) {
        val postDateTime = getCurrentTime()
        val rentalPost = RentalRoom(id, userId, arguments?.getString("location").toString(), arguments?.getString("roomType1").toString(), arguments?.getString("contactNum").toString(), arguments?.getString("monthlyRental").toString(), arguments?.getString("maxPerson").toString(), arguments?.getString("address").toString(), arguments?.getString("title").toString(), arguments?.getString("status").toString(), arguments?.getString("race").toString(), arguments?.getString("leaseTerm").toString(), arguments?.getString("checkedValues").toString(), editTextTextMultiLineDesc.text.toString(), imageURL, "0.0", "0", postDateTime)

        dbRef.child(id).setValue(rentalPost)
            .addOnCompleteListener{
                Toast.makeText(requireContext(), "Post Uploaded", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(), "Error ${it.toString()}", Toast.LENGTH_LONG).show()
            }
    }

    private fun getCurrentTime() : String{
        val currentTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val postTime = formatter.format(currentTime)
        return postTime.toString()
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.GetContent()){
        imgUri = it!!
        imageViewUpload.setImageURI(imgUri)

    }
}