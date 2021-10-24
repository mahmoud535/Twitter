package com.example.twitter.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.twitter.firestore.activities.activities.LoginActivity
import com.example.twitter.firestore.activities.activities.RegisterActivity
import com.example.twitter.firestore.activities.activities.UserProfileActivity
import com.example.twitter.firestore.models.User
import com.example.twitter.firestore.activities.activities.SettingsActivity
import com.example.mypet.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

    class FirestoreClass {


    private val mFireStore=FirebaseFirestore.getInstance()
    fun registerUser(activity: RegisterActivity, userInfo: User){
        //the "User" is collection name.If the collection is already created then it will not create the same o
        mFireStore.collection(Constants.USERS)
        //Document ID for user fields.Here the document it is the User ID
            .document(userInfo.id)
        //Here the userInfo are Field and the SetOption is Set to merge .It is for if we wants to merge later
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                //Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    fun getCurrentUserID():String{
        //An Instance of currentUser Using FirebaseAuth
        val currentUser=FirebaseAuth.getInstance().currentUser
        //A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID=""
        if (currentUser != null){
            currentUserID=currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity){
        //here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
        //the document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName,document.toString())

                //Here we have received the document snapshot which is converted into the User Data model object.
                    val user =document.toObject(User::class.java)//*****************************

                val sharedPreferences=
                        activity.getSharedPreferences(
                                Constants.MYSHOPPAL_PREFERENCES,
                                Context.MODE_PRIVATE
                        )

                val editor:SharedPreferences.Editor=sharedPreferences.edit()
                //Key:logged_in_username
                //Value:
                if (user != null) {
                    editor.putString(
                            Constants.LOGGED_IN_USERNAME,
                            "${user.firstName} ${user.lastName}"
                    )
                }
                editor.apply()

                //Start
                when(activity){
                    is LoginActivity ->{
                        //call a function of a base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)

                    }
                    //ننادي الكلاس اللتي تدعيSettingsActivity
                    is SettingsActivity ->{

                        if (user != null) {
                            activity.userDetailsSuccess(user)
                        }

                    }
                }
                //END

            }
            .addOnFailureListener { e->
                  //Hide the progress dialog if there is any error .and print the error in log.

                when(activity){
                     is LoginActivity ->{
                         activity.hideProgressDialog()
                     }
                    is SettingsActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }

    }

    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .update(userHashMap)
                .addOnSuccessListener {
                    when(activity){
                        is UserProfileActivity ->{
                            //Hide the progress dialog if there is any error .And print the error in log.
                            activity.useProfileUpdateSuccess()
                        }
                    }

                }
                .addOnFailureListener { e->
                    when(activity){
                        is UserProfileActivity ->{
                            //Hide the progress dialog if there is any error .And print the error in log.
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while updating the user details.",
                            e
                    )
                }
    }

    fun uploadImageToCloudStorage(activity: Activity,imageFileURI: Uri?){
        val sRef:StorageReference=FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE+System.currentTimeMillis()+"."
        + imageFileURI?.let {
                Constants.getFileExtension(
                    activity,
                    it
                )
            }
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            //the image upload is success
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            //Get the downloadable url from the task snapshot
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URl",uri.toString())
                    when(activity){
                        is UserProfileActivity ->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener{exception ->
                //Hide the progress dialog if there is any error.And print the error in log.
                when(activity){
                    is UserProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }


}