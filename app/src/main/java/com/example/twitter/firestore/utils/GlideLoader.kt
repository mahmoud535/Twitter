package com.example.mypet.utils

import kotlinx.android.synthetic.main.activity_user_profile.*
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.twitter.R

import java.io.IOException

class GlideLoader (val context: Context){
    //Any
    fun loadUserPicture(image:Any, imageView: ImageView){
        try {
            //Load the user image in the imageView.
            Glide
                .with(context)
                .load(image)//URI of the image
                .centerCrop()//Scale type pf the image.
                .placeholder(R.drawable.ic_user_placeholder)//A default place holder if image is filed to load.
                .into(imageView)//the view in which the image will be loaded.
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
}