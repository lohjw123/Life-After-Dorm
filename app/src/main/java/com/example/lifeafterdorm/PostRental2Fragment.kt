package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class PostRental2Fragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Rental Room"

        val view = inflater.inflate(R.layout.fragment_post_rental2, container, false)
        val userId = arguments?.getString("userId")
        val tvPostRental2Title : TextView = view.findViewById(R.id.tvPostRental2Title)

        val btnNext : Button = view.findViewById(R.id.btnPostRental2Next)
        val tfPostRental2TitleValue : EditText = view.findViewById(R.id.tfPostRental2TitleValue)
        val radioGroupStatus : RadioGroup = view.findViewById(R.id.radioGroupStatus)
        val radioGroupRace : RadioGroup = view.findViewById(R.id.radioGroupRace)
        val radioGroupLeaseTerm : RadioGroup = view.findViewById(R.id.radioGroupLeaseTerm)
        var selectedStatus : String=""
        var selectedRace : String=""
        var selectedLeaseTerm : String=""

        radioGroupStatus.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioGroupStatus = view.findViewById<RadioButton>(checkedId)
            selectedStatus = selectedRadioGroupStatus.text.toString()
        }

        radioGroupRace.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioGroupRace = view.findViewById<RadioButton>(checkedId)
            selectedRace = selectedRadioGroupRace.text.toString()
        }

        radioGroupLeaseTerm.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioGroupLeaseTerm = view.findViewById<RadioButton>(checkedId)
            selectedLeaseTerm = selectedRadioGroupLeaseTerm.text.toString()
        }

        btnNext.setOnClickListener{
            val fragment = PostRental3Fragment()
            val bundle = Bundle()
            bundle.putString("title", tfPostRental2TitleValue.text.toString())
            bundle.putString("status", selectedStatus)
            bundle.putString("race", selectedRace)
            bundle.putString("leaseTerm", selectedLeaseTerm)
            bundle.putString("location", arguments?.getString("location"))
            bundle.putString("roomType1", arguments?.getString("roomType1"))
            bundle.putString("roomType2", arguments?.getString("roomType2"))
            bundle.putString("contactNum", arguments?.getString("contactNum"))
            bundle.putString("monthlyRental", arguments?.getString("monthlyRental"))
            bundle.putString("maxPerson", arguments?.getString("maxPerson"))
            bundle.putString("address", arguments?.getString("address"))
            bundle.putString("userId", userId)
            fragment.arguments = bundle

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        return view
    }
}