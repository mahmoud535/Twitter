package com.example.mypet.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView

class MSPEditText (context: Context,attrs:AttributeSet):AppCompatEditText(context,attrs){
    //Call the function to apply the front to the components.
    init {
        applyFont()
    }

    private fun applyFont(){
        //This is used to get the file from the assets folder and set it to the textView.
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets,"Montserrat-Regular.ttf")
        setTypeface(typeface)
    }
}