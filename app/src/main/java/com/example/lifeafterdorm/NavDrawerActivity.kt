package com.example.lifeafterdorm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.lifeafterdorm.data.UserPreferences
import com.example.lifeafterdorm.databinding.NavDrawerMenuHeaderBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class NavDrawerActivity : AppCompatActivity(){

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var myToolbar: Toolbar
    private lateinit var dbRef : DatabaseReference
    private var userLoginId:String = ""
    private val PROFILE_UPDATE_REQUEST_CODE = 101
    private lateinit var menuHeaderBinding:NavDrawerMenuHeaderBinding


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_drawer)
        userLoginId = intent.getStringExtra("userId").toString()
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open,R.string.menu_close )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        dbRef = FirebaseDatabase.getInstance().reference
        myToolbar = findViewById(R.id.navDrawerToolbar)
        myToolbar.title = "Home"
        setSupportActionBar(myToolbar)
        myToolbar.setTitleTextColor(Color.WHITE)
        drawerHeaderData(userLoginId)
        menuHeaderBinding = NavDrawerMenuHeaderBinding.bind(navigationView.getHeaderView(0))
        Glide.with(this).clear(menuHeaderBinding.imgProfile)
        getProfileImage(this, userLoginId)
        findPreferences(userLoginId)

        val bundle = Bundle()
        bundle.putString("userId", userLoginId)
        navigationView.setNavigationItemSelectedListener { menuItem ->
         when (menuItem.itemId) {
         R.id.nav_profile -> {
            drawerLayout.closeDrawer(GravityCompat.START)
            replaceFragment(ProfileFragment(),  "Profile", userLoginId)
            true
            }

            R.id.nav_settings -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                replaceFragment(SettingsFragment(), "Settings", userLoginId)

                true
            }

            R.id.nav_home -> {
                drawerLayout.closeDrawer(GravityCompat.START)

                replaceFragment(HomeFragment(), "Home", userLoginId)

                true
            }

            R.id.nav_favouriteList -> {
                drawerLayout.closeDrawer(GravityCompat.START)

                replaceFragment(FavouriteListFragment(), "Favourite List", userLoginId)

                true
            }

             R.id.nav_community -> {
                 drawerLayout.closeDrawer(GravityCompat.START)
                 val intent = Intent(this, CommunityActivity::class.java)
                 startActivity(intent)
                 true
             }

             R.id.nav_rentalRoom -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                replaceFragment(RentalMainFragment(), "Rental Room", userLoginId)

                true
            }

            R.id.nav_logout -> {
                outNavMain("Log your account out")
                true
            }

            else -> {
              false
            }
        }}
    }

    private fun replaceFragment(fragment: Fragment, title: String, userId:String){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment.apply {
            arguments = Bundle().apply {
                putString("userId", userId)
            }
        })
        transaction.addToBackStack(null)
        transaction.commit()
        supportActionBar?.title = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        else {
            when(item.itemId) {
                R.id.menuHelp -> replaceFragment(HelpSupportFragment(), "Help & Support", intent.getStringExtra("userId").toString())
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_header, menu)
        return true
    }

    private fun drawerHeaderData(id:String?){
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        dbRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(
            object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val username: String? = userSnapshot.child("name").getValue(String::class.java)
                        menuHeaderBinding.tvName.text = "Welcome $username"
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    outNavMain("Data read error")
                }
            }
        )
    }


    private fun getProfileImage(context: Context, id:String?) {

        val imageName = "$id.png"
        val imageRef = FirebaseStorage.getInstance().reference.child("user_image/$imageName")
        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(context)
                    .load(uri)
                    .into(menuHeaderBinding.imgProfile)
            }
            .addOnFailureListener { exception ->
                outNavMain("Data read error")
            }
    }



    private fun outNavMain(msg:String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Log out")
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            drawerLayout.closeDrawer(GravityCompat.START)
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun findPreferences(id:String){
        dbRef = FirebaseDatabase.getInstance().getReference("UserPreferences")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(upSnap in snapshot.children){
                        val up = upSnap.getValue(UserPreferences::class.java)
                        if(up != null && up.id == id){
                            welcomeUser()
                        }else{
                            goMakePreferencesBox(id)
                        }
                    }
                }else{
                    goMakePreferencesBox(id)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun goMakePreferencesBox(id:String){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Welcome")
        alertDialogBuilder.setMessage("You haven't make your preferences yet. Want to shared your preferences to us?")
        alertDialogBuilder.setPositiveButton("Let's Go") { dialog, _ ->
            val enterAnimation = R.anim.slide_in_from_right
            val exitAnimation = R.anim.slide_out_to_left
            val fragment = ProfileFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction?.replace(R.id.fragmentContainer, fragment.apply {
                arguments = Bundle().apply {
                    putString("userId", id)
                }
            })
            transaction?.setCustomAnimations(enterAnimation, exitAnimation)
            transaction?.replace(R.id.fragmentContainer, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        alertDialogBuilder.setNegativeButton("Do it later"){ dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun welcomeUser(){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Welcome")
        alertDialogBuilder.setMessage("Welcome to Life After Dorm!")
        alertDialogBuilder.setPositiveButton("Hello") { dialog, _ ->
           dialog.dismiss()
        }
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PROFILE_UPDATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newName = data?.getStringExtra("newName")
            val imageChanged = data?.getBooleanExtra("imageChanged", false) ?: false
            val userId = data?.getStringExtra("userId").toString()

            newName?.let {
                val tvName:TextView = findViewById(R.id.tvName)
                tvName.text = it
            }

            if (imageChanged) {
                userId.let {
                    menuHeaderBinding.imgProfile.setImageDrawable(null)
                    Glide.with(this).clear(menuHeaderBinding.imgProfile)
                    getProfileImage(this, it)
                }
            }
        }
    }
}