package com.example.lifeafterdorm

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.lifeafterdorm.controller.getCountryCode
import com.example.lifeafterdorm.data.User
import com.example.lifeafterdorm.databinding.FragmentPostRental1Binding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostRental1Fragment : Fragment() {

    private lateinit var dbRef : DatabaseReference
    private lateinit var binding:FragmentPostRental1Binding
    private lateinit var maxPerson:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostRental1Binding.inflate(inflater, container,false)
        val userId = arguments?.getString("userId")
        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Rental Room"
        val view = binding.root
        val spinnerLocation : Spinner = view.findViewById(R.id.spinnerLocation)
        val spinnerRoomType1 : Spinner = view.findViewById(R.id.spinnerRoomType1)
        val spinnerRoomType2 : Spinner = view.findViewById(R.id.spinnerRoomType2)
        val tfContactNum : EditText = view.findViewById(R.id.tfPostRentalContactNum)
        val tfMonthRental : EditText = view.findViewById(R.id.tfPostRentalMonthRental)

        val tfAddress : EditText = view.findViewById(R.id.tfPostRentalAddress)
        val btnNext : Button = view.findViewById(R.id.btnPostRentalNext)
        val locationArray = resources.getStringArray(R.array.LocationArray)
        val RoomType1Array = resources.getStringArray(R.array.RoomTypeArray)
        val RoomType2Array = resources.getStringArray(R.array.RoomType2Array)
        val locationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locationArray)
        val RoomType1Adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, RoomType1Array)
        val RoomType2Adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, RoomType2Array)

        val spinnerPerson = binding.spinnerPerson
        val maxPersonList = resources.getStringArray(R.array.maxPerson)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, maxPersonList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPerson.adapter = adapter
        spinnerPerson.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                maxPerson = maxPersonList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        spinnerLocation.adapter = locationAdapter
        spinnerRoomType1.adapter = RoomType1Adapter
        spinnerRoomType2.adapter = RoomType2Adapter
        getPhoneNumber(userId.toString())
        btnNext.setOnClickListener{
            if(tfMonthRental.text.toString().trim().isNotEmpty() && tfAddress.text.toString().trim().isNotEmpty()){
                val fragment = PostRental2Fragment()
                val bundle = Bundle()
                bundle.putString("location", spinnerLocation.selectedItem.toString())
                bundle.putString("roomType1", spinnerRoomType1.selectedItem.toString())
                bundle.putString("roomType2", spinnerRoomType2.selectedItem.toString())
                bundle.putString("contactNum", tfContactNum.text.toString())
                bundle.putString("monthlyRental", tfMonthRental.text.toString())
                bundle.putString("maxPerson", maxPerson)
                bundle.putString("address", tfAddress.text.toString())
                bundle.putString("userId", userId)

                fragment.arguments = bundle
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.fragmentContainer, fragment)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }else{
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Error")
                alertDialogBuilder.setMessage("Please fill in all text field.")
                alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }
        return view
    }

    private fun getPhoneNumber(userid:String){
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        val phoneListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        val user = userSnap.getValue(User::class.java)
                        if (user != null && user.id == userid) {
                            binding.tfPostRentalContactNum.setText(user.phoneNum)//failed to get data
                            break
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        dbRef.addValueEventListener(phoneListener)
    }

}