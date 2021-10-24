package com.example.twitter.firestore.activities.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast

import com.example.twitter.R
import com.example.twitter.firestore.FirestoreClass
import com.example.twitter.firestore.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.et_email
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()

        tv_Login.setOnClickListener{
            //Launch the register screen when the user click on the text.

            onBackPressed()//للعوده الي الواجهه اللي قبلها

        }
        btn_register.setOnClickListener{
           registerUser()
        }

    }


    private fun setupActionBar(){

        setSupportActionBar(toolbar_register_activity)
        val actionBar=supportActionBar
        if (actionBar!=null)
        {
            actionBar .setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

        }
        toolbar_register_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     *A function to validate the entries of a new User.
     */

    private fun validateRegisterDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_first_name.text.toString().trim{it<=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name),true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim{it<=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name),true)
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim{it<=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim{it<=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim{it<=' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password),true)
                false
            }

            et_password.text.toString().trim{it <= ' '} != et_confirm_password.text.toString()
                .trim{it <=' '} ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),true)
              false
            }

            !cb_terms_and_condition.isChecked ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition),true)
             false
            }
            else->
            {
                //showErrorSnackBar(resources.getString(R.string.registery_successfull),false)
                 true
            }

        }
    }


    private fun registerUser(){

        //Click with validate function if the entries are valid or not.

        if (validateRegisterDetails()){

            showProgressDialog(resources.getString(R.string.please_wait))

            val email :String=et_email.text.toString().trim{it <=' '}
            val password :String=et_password.text.toString().trim{it <=' '}

       //create an instance and create a register a user with email and password.

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener  (
                    OnCompleteListener<AuthResult> {task ->

                        //If the registration is successfully done
                        if (task.isSuccessful)
                        {
                            //Firebase register user
                            val firebaseUser:FirebaseUser=task.result!!.user!!

                            val user= User(
                                firebaseUser.uid,
                                et_first_name.text.toString().trim{it<=' '},
                                et_last_name.text.toString().trim{it<=' '},
                                et_email.text.toString().trim{it<=' '}
                            )

                                   FirestoreClass().registerUser(this@RegisterActivity,user)

//                            FirebaseAuth.getInstance().signOut()
//                            finish()
                        }else{
                            hideProgressDialog()
                            //If the registering is not successful then show error message.

                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    })


        }
    }
    fun userRegistrationSuccess(){
        //Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()
    }
}