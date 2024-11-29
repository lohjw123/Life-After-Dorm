package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class PostRental3Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Rental Room"
        val userId = arguments?.getString("userId")
        val view = inflater.inflate(R.layout.fragment_post_rental3, container, false)
        val tvPostRental3Title : TextView = view.findViewById(R.id.tvPostRental3Title)

        val btnPostRental3Next : Button = view.findViewById(R.id.btnPostRental3Next)
        val cbNearLRT : CheckBox = view.findViewById(R.id.cbNearLRT)
        val cbNearBusStation : CheckBox = view.findViewById(R.id.cbNearBusStation)
        val cbNearKTM : CheckBox = view.findViewById(R.id.cbNearKTM)
        val cbNearMRT : CheckBox = view.findViewById(R.id.cbNearMRT)
        val cb24hourssecurity : CheckBox = view.findViewById(R.id.cb24hourssecurity)
        val cbSportFacilities : CheckBox = view.findViewById(R.id.cbSportFacilities)
        val cbCoveredCarPark : CheckBox = view.findViewById(R.id.cbCoveredCarPark)
        val cbNearRestaurant : CheckBox = view.findViewById(R.id.cbNearRestaurant)
        val cbWiFi : CheckBox = view.findViewById(R.id.cbWiFi)
        val cbAirConditionerWithMeter : CheckBox = view.findViewById(R.id.cbAirConditionerWithMeter)
        val cbIncludeUtilitiesFees : CheckBox = view.findViewById(R.id.cbIncludeUtilitiesFees)
        val cbPrivateBathroom : CheckBox = view.findViewById(R.id.cbPrivateBathroom)
        val cbIncludeAirConditionerFee : CheckBox = view.findViewById(R.id.cbIncludeAirConditionerFee)
        val cbMiniMarket : CheckBox = view.findViewById(R.id.cbMiniMarket)

        val checkedValues = mutableListOf<String>()

        btnPostRental3Next.setOnClickListener() {
            val fragment = PostRental4Fragment()
            val bundle = Bundle()

            if (cbNearLRT.isChecked) {
                checkedValues.add(cbNearLRT.text.toString())
            }

            if (cbNearBusStation.isChecked) {
                checkedValues.add(cbNearBusStation.text.toString())
            }

            if (cbNearKTM.isChecked) {
                checkedValues.add(cbNearKTM.text.toString())
            }

            if (cbNearMRT.isChecked) {
                checkedValues.add(cbNearMRT.text.toString())
            }

            if (cb24hourssecurity.isChecked) {
                checkedValues.add(cb24hourssecurity.text.toString())
            }

            if (cbSportFacilities.isChecked) {
                checkedValues.add(cbSportFacilities.text.toString())
            }

            if (cbCoveredCarPark.isChecked) {
                checkedValues.add(cbCoveredCarPark.text.toString())
            }

            if (cbNearRestaurant.isChecked) {
                checkedValues.add(cbNearRestaurant.text.toString())
            }

            if (cbWiFi.isChecked) {
                checkedValues.add(cbWiFi.text.toString())
            }

            if (cbAirConditionerWithMeter.isChecked) {
                checkedValues.add(cbAirConditionerWithMeter.text.toString())
            }

            if (cbIncludeUtilitiesFees.isChecked) {
                checkedValues.add(cbIncludeUtilitiesFees.text.toString())
            }

            if (cbPrivateBathroom.isChecked) {
                checkedValues.add(cbPrivateBathroom.text.toString())
            }

            if (cbIncludeAirConditionerFee.isChecked) {
                checkedValues.add(cbIncludeAirConditionerFee.text.toString())
            }

            if (cbMiniMarket.isChecked) {
                checkedValues.add(cbMiniMarket.text.toString())
            }

            bundle.putString("location", arguments?.getString("location"))
            bundle.putString("roomType1", arguments?.getString("roomType1"))
            bundle.putString("roomType2", arguments?.getString("roomType2"))
            bundle.putString("contactNum", arguments?.getString("contactNum"))
            bundle.putString("monthlyRental", arguments?.getString("monthlyRental"))
            bundle.putString("maxPerson", arguments?.getString("maxPerson"))
            bundle.putString("address", arguments?.getString("address"))
            bundle.putString("title", arguments?.getString("title"))
            bundle.putString("status", arguments?.getString("status"))
            bundle.putString("race", arguments?.getString("race"))
            bundle.putString("leaseTerm", arguments?.getString("leaseTerm"))
            bundle.putString("checkedValues", checkedValues.joinToString(", "))
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