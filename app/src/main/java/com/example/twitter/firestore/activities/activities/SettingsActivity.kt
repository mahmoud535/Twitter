package com.example.twitter.firestore.activities.activities

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.example.twitter.firestore.FirestoreClass
import com.example.twitter.firestore.models.User
import com.example.mypet.utils.Constants
import com.example.mypet.utils.GlideLoader
import com.example.twitter.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_user_profile.*

class SettingsActivity : BaseActivity(),View.OnClickListener {

    private lateinit var mUserDetails: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()

        tv_edit.setOnClickListener(this)
        btn_logout.setOnClickListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_settings_activity)

        val actionBar=supportActionBar

        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_settings_activity.setNavigationOnClickListener {onBackPressed()}
    }

    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }



    fun userDetailsSuccess(user: User){
//        if (user != null) {
            mUserDetails= user
//        }
//
        hideProgressDialog()
//
               //لتحميل الصوره
//        if (user != null) {
            GlideLoader(this@SettingsActivity)//.loadUserPicture(user.image,iv_user_photo)
//        }
//        if (user != null) {
            tv_name.text = "${user.firstName} ${user.lastName}"
//        }
//        if (user != null) {
            tv_gender.text = user.gender
//        }
//        if (user != null) {
            tv_email.text = user.email
//        }
//        if (user != null) {
            tv_mobile_number.text = "${user.mobile}"
//        }
//
//
     }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
           //للخروج من الSettingsActivity
    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                //SettingsActivityمن زر ال tv_edit الموجود في ال
                // SettingsActivityتغيير البيانات الموجوده في صفحه ال
                R.id.tv_edit ->{
                    val intent=Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                    startActivity(intent)
                }
               ///////////////////////////////////////////

                R.id.btn_logout->{
                    FirebaseAuth.getInstance().signOut()
                    val intent= Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}