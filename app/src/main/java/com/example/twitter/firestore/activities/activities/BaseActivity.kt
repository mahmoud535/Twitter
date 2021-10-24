package com.example.twitter.firestore.activities.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.example.twitter.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce=false

    private lateinit var mProgressDialog: Dialog
    // عند بدء التسجيل في ال registerActivity ونسيان ملء خانه من خانات التسجيل (تظهر رساله في الاسفل للتحزير)

    //ولكي نفعل زلك نكتب اسم الكلاسBaseActivity بدلا منAppCompatActivity في كلاس ال registerActivity ونجعل كلاس الBaseActivity تكون  open
    fun showErrorSnackBar(message:String,errorMessage:Boolean){
        val snackBar=
                Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackBarView=snackBar.view

        if (errorMessage){
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this@BaseActivity,
                            R.color.colorSnackBarError
                    )
            )
        }else
        {
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this@BaseActivity,
                            R.color.colorSnackBarSuccess
                    )
            )
        }
        snackBar.show()

    }
//الكود الخاص بكلاس الdialog progress
    fun showProgressDialog(text:String){
        mProgressDialog=Dialog(this)
        /*
        Set the screen content from a layout resource.
        the resource will be defined, adding all top-level views to the screen.
         */

        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.tv_progress_text.text=text
        mProgressDialog.setCanceledOnTouchOutside(false)

        //start the dialog and display it on screen.
        mProgressDialog.show()

    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
    /////////////////////////////
    /////////////////////////
    //عند الضغط علي زر الرجوع الخاص بالهاتف مرتين في واجهه الMainActivity  يخرج من التطبيق
    fun doubleBackToExit(){

        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce=true

        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_to_exit),
            Toast.LENGTH_SHORT
        ).show()
        @Suppress("DEPRECATION")
        android.os.Handler().postDelayed({doubleBackToExitPressedOnce=false},2000)

    }
    /////////////////////////////////////////////
}