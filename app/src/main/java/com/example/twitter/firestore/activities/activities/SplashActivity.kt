package com.example.twitter.firestore.activities.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.example.twitter.R


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ////////////////////////////////////////////////////////
        //الخاص بالواجهه عند فتح التطبيق لكي يجعل الصوره ملء الشاشه
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        ///////////////////////////////////////////////////////
        //////////////////////////////////////////////////////
        //للانتقال من واجهه الsplash الي واجهه الMain تلقائيا
        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                //نجرب الDashbourdactivity::class.java
                //LoginActivity::class.javaلو زر السيتينج مشتغلش نخلينا في
                //Launch the Main Activity
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()//call this when your activity is done and should be closed.
            },
        1500
        )
        /////////////////////////////////////////////////////

//    val typeface:Typeface= Typeface.createFromAsset(assets,"Montserrat-Bold.ttf")
//    tv_app_name.typeface=typeface
    }
}