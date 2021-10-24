package com.example.twitter.firestore.activities.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.twitter.firestore.FirestoreClass
import com.example.twitter.firestore.models.User
import com.example.mypet.utils.Constants
import com.example.mypet.utils.GlideLoader
import com.example.twitter.R
import kotlinx.android.synthetic.main.activity_register.et_email
import kotlinx.android.synthetic.main.activity_register.et_first_name
import kotlinx.android.synthetic.main.activity_register.et_last_name
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity() ,View.OnClickListener{

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri:Uri?=null
    private var mUserProfileURL:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)



        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            //get user details from intent as a parcelableExtra.
            mUserDetails=intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)
        et_email.isEnabled=false
        et_email.setText(mUserDetails.email)

        if (mUserDetails.profileCompleted==0){
            tv_title.text=resources.getString(R.string.title_complete_profile)

            et_first_name.isEnabled=false


            et_last_name.isEnabled=false



        }else{
            setupActionBar()
            tv_title.text=resources.getString(R.string.title_edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image,iv_user_photo)



            if (mUserDetails.mobile != 0L){
                et_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender==Constants.MALE){
                rb_male.isChecked=true
            }else{
                rb_female.isChecked=true
            }
        }

          //عند تسجيل الدخول في الlogin للانتقال الي الuser profile سنجد تلقائيا الfirst name and last name and email مملوئين ببيانات الشخص اللزي سجل ولا يسمح بتعديلهم
//        et_first_name.isEnabled=false
//        et_first_name.setText(mUserDetails.firstName)
//
//        et_last_name.isEnabled=false
//        et_last_name.setText(mUserDetails.lastName)
//
//        et_email.isEnabled=false
//        et_email.setText(mUserDetails.email)
        ///////////////////////////////////////////////////
        iv_user_photo.setOnClickListener(this@UserProfileActivity)

        btn_submit.setOnClickListener(this@UserProfileActivity)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }



//الكود الخاص بتحميل صوره الuser profile
    override fun onClick(v: View?) {
        if(v!=null){
            when(v.id){
                R.id.iv_user_photo->{
                    //here we will click if the permission is already allowed or we need to request for it.
                    //First of all we will click the READ_Storage permission and if it is not allowed we
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) ==PackageManager.PERMISSION_GRANTED
                    ){
                        //showErrorSnackBar("You already have the storage permission.",false)
                       Constants.showImageChooser(this)
                    }else{

                        /*Request permission to be granted to this application .These permissions
                        must  be requested in your manifest ,they should not be granted to your app,
                        and they should have protection level
                         */
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit->{
                    if (validateUserProfileDetails()){
                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageFileUri !=null)
                        FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri!!)
                        else{
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

   private fun updateUserProfileDetails(){
       val userHashMap=HashMap<String,Any>()

       val firstName=et_first_name.text.toString().trim(){it<=' '}
       if (firstName != mUserDetails.firstName){
           userHashMap[Constants.FIRST_NAME]=firstName
       }

       val lastName=et_last_name.text.toString().trim(){it<=' '}
       if (lastName != mUserDetails.lastName){
           userHashMap[Constants.LAST_NAME]=lastName
       }
       val mobileNumber=et_mobile_number.text.toString().trim(){it<=' '}

       val gender=if(rb_male.isChecked)
       {
           Constants.MALE
       }else
       {
           Constants.FEMALE
       }

       if(mUserProfileURL.isNotEmpty()){
           userHashMap[Constants.IMAGE]=mUserProfileURL
       }

       if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()){
           userHashMap[Constants.MOBILE]=mobileNumber.toLong()
       }

       if (gender.isNotEmpty() && gender != mUserDetails.gender){
           userHashMap[Constants.GENDER]=gender
       }
       userHashMap[Constants.GENDER]=gender

       userHashMap[Constants.COMPLETE_PROFILE]=1

       //showProgressDialog(resources.getString(R.string.please_wait))

       FirestoreClass().updateUserProfileData(this,userHashMap)

   }

    fun useProfileUpdateSuccess(){
        hideProgressDialog()

        Toast.makeText(
                this@UserProfileActivity,
                resources.getString(R.string.msg_profile_update_success),
                Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity,
            MainActivity::class.java))
        finish()//close profile activity
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
              if (requestCode==Constants.READ_STORAGE_PERMISSION_CODE){
                  //if permission is granted
                  if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                      //showErrorSnackBar("The storage permission is granted.",false)
                        Constants.showImageChooser(this)
                  }else{
                      //Displaying another toast if permission is not granted
                      Toast.makeText(
                          this,
                          resources.getString(R.string.read_storage_permission_denied),
                          Toast.LENGTH_LONG
                      ).show()
                  }
              }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK){
            if (requestCode==Constants.PICK_IMAGE_REQUEST_CODE){
                if (data!=null){
                    try {
                        //the uri of selected image from phone storage.
                        mSelectedImageFileUri=data.data!!

                       // iv_user_photo.setImageURI(Uri.parse(selectedImageFileUri.toString()))
                       GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,iv_user_photo)
                    } catch (e:IOException){
                        e.printStackTrace()
                        Toast.makeText(
                                this@UserProfileActivity,
                                resources.getString(R.string.image_selection_failed),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    private fun validateUserProfileDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_mobile_number.text.toString().trim(){it <=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
                false
            }
            else->{
                true
            }
        }
    }

   fun imageUploadSuccess(imageURL: String){
     //hideProgressDialog()

       mUserProfileURL=imageURL
       updateUserProfileDetails()
   }
}