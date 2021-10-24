package com.example.mypet.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class MSPBUtton (context:Context,attrs:AttributeSet):AppCompatButton(context,attrs){
    //Call the function to apply the front to the components.
    init {
        applyFont()
    }

    private fun applyFont(){
        //This is used to get the file from the assets folder and set it to the textView.
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        setTypeface(typeface)
    }
}