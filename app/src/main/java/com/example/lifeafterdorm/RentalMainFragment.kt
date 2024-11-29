package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.lifeafterdorm.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RentalMainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Rental Room"
        val userId = arguments?.getString("userId")
        val view = inflater.inflate(R.layout.fragment_rental_main, container, false)

        val btnPost : Button = view.findViewById(R.id.btnPost)
        val btnManagePost : Button = view.findViewById(R.id.btnManagePost)


        btnPost.setOnClickListener() {
            val fragment = PostRental1Fragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, fragment.apply {
                arguments = Bundle().apply {
                    putString("userId", userId)
                }
            })
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        btnManagePost.setOnClickListener() {
            val fragment = ManageRentalFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, fragment)
                val bundle = Bundle()
                bundle.putString("userId", userId)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }

}