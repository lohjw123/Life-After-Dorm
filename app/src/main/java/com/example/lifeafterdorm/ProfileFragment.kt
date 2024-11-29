package com.example.lifeafterdorm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.lifeafterdorm.controller.getCountryCode
import com.example.lifeafterdorm.controller.separateCountryCodeAndNumber
import com.example.lifeafterdorm.data.Location
import com.example.lifeafterdorm.data.RoomType
import com.example.lifeafterdorm.data.User
import com.example.lifeafterdorm.data.UserPreferences
import com.example.lifeafterdorm.databinding.FragmentProfileBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding
    private lateinit var dbRef : DatabaseReference
    private lateinit var newLocation: Location
    private lateinit var national:String
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var storageRef : StorageReference
    private lateinit var imagePath:Uri
    private var imageChanged: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 2000
        }
        val userId = arguments?.getString("userId")
        val spinner = binding.spinnerProfileNational
        val nationalList = resources.getStringArray(R.array.countries_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nationalList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        if (userId != null) {
            refreshData(userId)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    national = nationalList[position]
                    val selectedCountry = parent.getItemAtPosition(position).toString()
                    val countryCode = getCountryCode(selectedCountry)
                    binding.tfProfileCode.setText(countryCode)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
            binding.btnGetNewLocation.setOnClickListener {
                getCurrentLocation()
            }

            binding.btnProfileSetImage.setOnClickListener {
                startChooseImg()
            }
            binding.btnGender.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Change Gender")
                alertDialogBuilder.setMessage("Do u sure want to change your gender?")
                val gender: String = when (binding.btnGender.text) {
                    "Male" -> "Female"
                    "Female" -> "Male"
                    else -> "No Prefer to say"
                }
                alertDialogBuilder.setNeutralButton(gender) { dialog, _ ->
                    binding.btnGender.text = gender
                }
                alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }

            binding.btnProfileSaveChanges.setOnClickListener {
                val newName = binding.tfProfileNameValue.text.toString().trim()
                val newPhone =
                    binding.tfProfileCode.text.toString().trim() + binding.tfPhone.text.toString()
                        .trim()
                val gender = binding.btnGender.text.toString()
                val genderData: String = when (gender) {
                    "Male" -> "M"
                    "Female" -> "F"
                    else -> "N"
                }
                val errorMsg = ArrayList<String>()

                val maxBudgetText = binding.tfProfileMaxBudget.text.toString().trim()
                val minBudgetText = binding.tfProfileMinBudget.text.toString().trim()
                if (maxBudgetText.isNotEmpty() && minBudgetText.isNotEmpty()) {
                    try {
                        val maxBudget = maxBudgetText.toDouble()
                        val minBudget = minBudgetText.toDouble()

                        if (maxBudget <= minBudget) {
                            errorMsg.add("Minimum budget cannot be more than maximum budget.")
                        }

                        val chkSingle = binding.chkSingle.isChecked
                        val chkMiddle = binding.chkMiddle.isChecked
                        val chkMaster = binding.chkMaster.isChecked
                        val chkStudio = binding.chkStudio.isChecked
                        val chkSoho = binding.chkSoho.isChecked
                        val chkSuite = binding.chkSuite.isChecked
                        val chkPrivate = binding.chkPrivate.isChecked
                        val chkShared = binding.chkShared.isChecked

                        if (!chkSingle && !chkMiddle && !chkMaster && !chkStudio && !chkSoho && !chkSuite && !chkPrivate && !chkShared) {
                            errorMsg.add("Please select at least one room type preference.")
                        }

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Update Profile and Preferences")
                            .setMessage("Do you want to update your profile and preferences?")
                            .setPositiveButton("Update") { dialog, which ->
                                if (::newLocation.isInitialized && national.isNotEmpty() && newName.isNotEmpty() &&
                                    newPhone.isNotEmpty() && maxBudgetText.isNotEmpty() && minBudgetText.isNotEmpty()
                                ) {
                                    val updatedUser = User(
                                        name = newName,
                                        nationality = national,
                                        gender = genderData,
                                        phoneNum = newPhone,
                                        location = newLocation
                                    )

                                    val updatePreferences = UserPreferences(
                                        id = userId,
                                        maxBudget = maxBudget,
                                        minBudget = minBudget,
                                        roomType = RoomType(
                                            singleRoom = chkSingle,
                                            middleRoom = chkMiddle,
                                            masterRoom = chkMaster,
                                            studio = chkStudio,
                                            soho = chkSoho,
                                            suite = chkSuite,
                                            privateRoom = chkPrivate,
                                            sharedRoom = chkShared
                                        )
                                    )

                                    if (errorMsg.isEmpty()) {
                                        saveUserChanges(userId, updatedUser, updatePreferences)
                                    } else {
                                        showDialog("Error", errorMsg.joinToString("\n"))
                                    }
                                } else {
                                    showDialog(
                                        "Error",
                                        "Please fill in all the fields to update your data."
                                    )
                                }
                            }
                            .setNegativeButton("Cancel") { dialog, which ->
                                dialog.dismiss()
                            }
                            .setNeutralButton("Profile Only") { dialog, which ->
                                if (::newLocation.isInitialized && national.isNotEmpty() && newName.isNotEmpty() && newPhone.isNotEmpty()) {
                                    val updatedUser = User(
                                        name = newName,
                                        nationality = national,
                                        gender = genderData,
                                        phoneNum = newPhone,
                                        location = newLocation
                                    )
                                    val up = UserPreferences(id = "") // This part needs correction
                                    saveUserChanges(userId, updatedUser, up)
                                    refreshData(userId)
                                } else {
                                    showDialog(
                                        "Error",
                                        "Please fill in all the fields to update your profile data."
                                    )
                                }
                            }
                            .show()
                    } catch (e: NumberFormatException) {
                        showDialog("Error", "Invalid budget value.")
                    }
                } else {
                    showDialog("Error", "Please fill in both maximum and minimum budget fields.")
                }
            }
        }
            return binding.root
    }

    private fun refreshData(id:String){
        binding.tfProfileNameValue.text = null
        binding.tvProfileUserName.text = null
        binding.tfProfileMinBudget.text = null
        binding.tfProfileMaxBudget.text = null
        binding.chkSingle.isChecked = false
        binding.chkMiddle.isChecked = false
        binding.chkMaster.isChecked = false
        binding.chkSoho.isChecked = false
        binding.chkStudio.isChecked = false
        binding.chkSuite.isChecked = false
        binding.chkPrivate.isChecked = false
        binding.chkShared.isChecked = false
        getUserData(id, resources.getStringArray(R.array.countries_array), binding.spinnerProfileNational)
        getProfileImage(id)
    }

    private fun saveUserChanges(id: String, user: User, userPreferences: UserPreferences) {
        dbRef = FirebaseDatabase.getInstance().getReference("User/$id")
        if (imageChanged) {
            updateImg(id, user, userPreferences)
        } else {
            updateProfileData(id, user, userPreferences)
        }
    }

    private fun saveInterest(id: String, userPreferences: UserPreferences, username: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("UserPreferences")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    var found = false
                    for (upSnap in dataSnapshot.children) {
                        val up = upSnap.getValue(UserPreferences::class.java)
                        if (up != null && up.id == id) {
                            found = true
                            updatePreferences(id, userPreferences, username)
                            break
                        }
                    }
                    if (!found) {
                        dbRef.child(id).setValue(userPreferences)
                            .addOnCompleteListener {
                                Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_LONG).show()
                                startNavDrawerActivity(username, id)
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Error ${it.toString()}", Toast.LENGTH_LONG).show()
                            }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Data failed to read", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updatePreferences(id: String, userPreferences: UserPreferences, username: String) {
        val preferencesData = HashMap<String, Any>().apply {
            put("maxBudget", userPreferences.maxBudget)
            put("minBudget", userPreferences.minBudget)
            put("roomType", mapOf(
                "singleRoom" to userPreferences.roomType.singleRoom,
                "middleRoom" to userPreferences.roomType.middleRoom,
                "masterRoom" to userPreferences.roomType.masterRoom,
                "studio" to userPreferences.roomType.studio,
                "soho" to userPreferences.roomType.soho,
                "suite" to userPreferences.roomType.suite,
                "privateRoom" to userPreferences.roomType.privateRoom,
                "sharedRoom" to userPreferences.roomType.sharedRoom
            ))
        }
        dbRef = FirebaseDatabase.getInstance().getReference("UserPreferences/$id")
        dbRef.updateChildren(preferencesData)
            .addOnSuccessListener {
                showDialogAndStartActivity("Success", "Data Updated Successfully", id, username)
            }
            .addOnFailureListener { e ->
                showDialog("Failed", "Data updated failed.")
            }
    }

    private fun updateProfileData(id: String, user: User, userPreferences: UserPreferences) {
        val userData = HashMap<String, Any>().apply {
            if (user.name.isNotEmpty()) {
                put("name", user.name)
            }
            if (user.nationality.isNotEmpty()) {
                put("nationality", user.nationality)
            }
            if (user.phoneNum.isNotEmpty()) {
                put("phoneNum", user.phoneNum)
            }
            if (user.location.latitude != "0.0" && user.location.longitude != "0.0") {
                put("location", mapOf("latitude" to user.location.latitude, "longitude" to user.location.longitude))
            }
            if (user.gender.isNotEmpty()) {
                put("gender", user.gender)
            }
        }

        dbRef.updateChildren(userData)
            .addOnSuccessListener {
                if (userPreferences.id.isNotEmpty()) {
                    saveInterest(id, userPreferences, user.name)
                } else {
                    showDialog("Success", "Profile updated successfully.")
                    startNavDrawerActivity(user.name, id)
                }
            }
            .addOnFailureListener { e ->
                showDialog("Failed", "Profile data update failed.")
            }
    }


    private fun updateImg(id: String, user: User, userPreferences: UserPreferences) {
        storageRef = FirebaseStorage.getInstance().reference
        val imgRef = storageRef.child("user_image/$id.png")
        val bitmap = (binding.ivProfile.drawable as? BitmapDrawable)?.bitmap

        bitmap?.let { imageBitmap ->
            val compressedStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, compressedStream)
            val compressedData = compressedStream.toByteArray()

            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Updating Image")
            progressDialog.setMessage("Please wait while we update your profile image...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            imgRef.putBytes(compressedData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Image updated successfully", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    updateProfileData(id, user, userPreferences)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Failed to update image: ${exception.message}", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
        } ?: run {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startNavDrawerActivity(username: String, id: String) {
        val intent = Intent(requireContext(), NavDrawerActivity::class.java).apply {
            putExtra("newName", username)
            putExtra("imageChanged", imageChanged)
            putExtra("userId", id)
        }
        activity?.setResult(Activity.RESULT_OK, intent)
        startActivity(intent)
    }

    private fun showDialogAndStartActivity(title: String, message: String, id: String,  username: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            startNavDrawerActivity(username, id)
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun getUserData(id:String, nationalList: Array<String>, spinner:Spinner){
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        val userProfileListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        val user = userSnap.getValue(User::class.java)
                        if (user != null && user.id == id) {
                            binding.tvProfileUserName.text = user.name
                            binding.tfProfileNameValue.setText(user.name)
                            val location = Location()
                            location.latitude = user.location.latitude.toString()
                            location.longitude = user.location.longitude.toString()
                            val indexOfNationality = nationalList.indexOf(user.nationality)
                            if (indexOfNationality != -1) {
                                spinner.setSelection(indexOfNationality)
                            }
                            val spanMap = SpannableString(binding.tvLinkLocation.text)
                            val clickableMap = object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    openGoogleMaps(location)
                                }
                            }
                            newLocation = location
                            spanMap.setSpan(clickableMap, 0, spanMap.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                            binding.tvLinkLocation.text = spanMap
                            binding.tvLinkLocation.movementMethod = LinkMovementMethod.getInstance()
                            val separated = separateCountryCodeAndNumber(user.phoneNum)
                            if (separated != null) {
                                val (countryCode, phoneNumber) = separated
                                binding.tfProfileCode.setText(countryCode)
                                binding.tfPhone.setText(phoneNumber)
                            }
                            val gender: String = when (user.gender) {
                                "M" -> "Male"
                                "F" -> "Female"
                                else -> "No Prefer to say"
                            }
                            binding.btnGender.text = gender
                            break
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        }
        dbRef.addValueEventListener(userProfileListener)
        dbRef = FirebaseDatabase.getInstance().getReference("UserPreferences")
        val userPreferencesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (upSnap in snapshot.children) {
                        val up = upSnap.getValue(UserPreferences::class.java)
                        if (up != null && up.id == id) {
                            binding.tfProfileMaxBudget.setText(up.maxBudget.toString())
                            binding.tfProfileMinBudget.setText(up.minBudget.toString())
                            binding.chkSingle.isChecked = up.roomType.singleRoom
                            binding.chkMiddle.isChecked = up.roomType.middleRoom
                            binding.chkMaster.isChecked = up.roomType.masterRoom
                            binding.chkStudio.isChecked = up.roomType.studio
                            binding.chkSoho.isChecked = up.roomType.soho
                            binding.chkSuite.isChecked = up.roomType.suite
                            binding.chkPrivate.isChecked = up.roomType.privateRoom
                            binding.chkShared.isChecked = up.roomType.sharedRoom
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        }
        dbRef.addValueEventListener(userPreferencesListener)
    }

    private fun getProfileImage(id:String){
        val imageName = "$id.png"
        val imageRef = FirebaseStorage.getInstance().reference.child("user_image/$imageName")
        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.ivProfile)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: $exception", Toast.LENGTH_LONG).show()
            }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openGoogleMaps(location: Location) {
        val uri = "geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${location.latitude},${location.longitude}"))
            startActivity(browserIntent)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getCurrentLocation() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Getting current location...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if (Build.VERSION.SDK_INT >= Build. VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(requireContext())
                        .requestLocationUpdates(locationRequest, object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                LocationServices.getFusedLocationProviderClient(requireContext())
                                    .removeLocationUpdates(this)
                                if (locationResult.locations.isNotEmpty()) {
                                    val index = locationResult.locations.size - 1
                                    val latitude = locationResult.locations[index].latitude.toString()
                                    val longitude = locationResult.locations[index].longitude.toString()
                                    newLocation = Location("longitude", "latitude")
                                    val spanMap = SpannableString("See My New Location")
                                    val clickableMap = object : ClickableSpan() {
                                        override fun onClick(widget: View) {
                                            openGoogleMaps(newLocation)
                                        }
                                    }
                                    spanMap.setSpan(clickableMap, 0, spanMap.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    binding.tvLinkLocation.text = spanMap
                                    binding.tvLinkLocation.movementMethod = LinkMovementMethod.getInstance()
                                    progressDialog.dismiss()
                                    showDialog("Success","Find your current location now.")
                                }
                            }
                        }, Looper.getMainLooper())

                } else {
                    progressDialog.dismiss()
                    turnOnGPS()
                }
            } else {
                progressDialog.dismiss()
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }

    private fun turnOnGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
                Toast.makeText(requireContext(), "GPS is already turned on", Toast.LENGTH_SHORT).show()
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(requireContext() as Activity, 2)
                    } catch (ex: IntentSender.SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
    }

    private fun showDialog(title:String, msg:String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun startChooseImg(){
        val i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose an image for avatar"), 111)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val contentResolver = context?.contentResolver
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null){
            imagePath = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
            binding.ivProfile.setImageBitmap(bitmap)
            imageChanged = true
        }
    }
}