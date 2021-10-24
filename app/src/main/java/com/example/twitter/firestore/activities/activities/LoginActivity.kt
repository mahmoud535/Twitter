package com.example.twitter.firestore.activities.activities
import kotlinx.android.synthetic.main.activity_login.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager

import com.example.twitter.firestore.FirestoreClass
import com.example.twitter.firestore.models.User
import com.example.mypet.utils.Constants
import com.example.twitter.R
import com.google.firebase.auth.FirebaseAuth

//import kotlinx.android.synthetic.main.MainActivity.*

class LoginActivity : BaseActivity(),View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            setContentView(R.layout.activity_login)



        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

           //Click event assigned to Forgot Password text.
        tv_forgot_password.setOnClickListener(this)
        //Click event assigned to login button.
         btn_login.setOnClickListener(this)
        //Click event assigned to Register text.
        tv_register.setOnClickListener(this)


    }
//للدخول من الLogin الي ال main activity
    fun userLoggedInSuccess(user: User?){

        //hide the progress dialog
        hideProgressDialog()

        //print the user details in the log as of now.
//    if (user != null) {
//        Log.i("First Name:",user.firstName)
//    }
//    if (user != null) {
//        Log.i("Last Name:",user.lastName)
//    }
//    if (user != null) {
//        Log.i("Email:",user.email)
//    }

    if (user != null) {
        if (user.profileCompleted==0){
            //if the user profile is incomplete then launch the UserProfileActivity.
            val intent=Intent(this@LoginActivity,
                UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }else{
            //Redirect the user to Main Screen after log in.
            startActivity(Intent(this@LoginActivity,
                MainActivity::class.java))
        }
    }

        finish()
    }


    //In Login screen the clickable components are login button ,ForgotPassword text and Register Text.
    override fun onClick(view: View?) {
        if (view !=null)
        {
            when(view.id)
            {
                R.id.tv_forgot_password->{
                    val intent=Intent(this@LoginActivity,
                        ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login->{
                    logInRegisteredUser()

                }

                R.id.tv_register->{
                    //Launch the register screen when the user click on the text.
                    val intent=Intent(this@LoginActivity,
                        RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

private fun validateLoginDetails():Boolean{
    return when{
        TextUtils.isEmpty(et_email.text.toString().trim(){it<=' '})->{
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
          false
        }
        TextUtils.isEmpty(et_password.text.toString().trim(){it<=' '})->{
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
            false
        }
        else -> {

            true
        }
    }
  }
    //عند تسجيل الدخول في الlogin
 private fun logInRegisteredUser(){
     if(validateLoginDetails()){
         //show the progress dialog.
         showProgressDialog(resources.getString(R.string.please_wait))

         //Get the text from editText trim the space
         val email=et_email.text.toString().trim(){it<=' '}
         val password=et_password.text.toString().trim(){it<=' '}

         //Login Using FirebaseAuth
         FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                 .addOnCompleteListener { task ->

                     if (task.isSuccessful){

                         FirestoreClass().getUserDetails(this@LoginActivity)
                     }else
                     {
                         hideProgressDialog()
                         showErrorSnackBar(task.exception!!.message.toString(),true)
                     }
                 }
     }
 }
}