package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class RoomMateMatchingFragment : Fragment() {

    private lateinit var myToolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Roommate Matching"

        val view = inflater.inflate(R.layout.fragment_room_mate_matching, container, false)

        val btnBack : Button = view.findViewById(R.id.btnMatchingBack)
        val btnWhatsapp : Button = view.findViewById(R.id.btnMatchingContactWhatsapp)

        btnBack.setOnClickListener() {
            val fragment = HomeFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        return view
    }

}