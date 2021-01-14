package com.edit.universaledit

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.util.regex.Pattern


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun getCurrentColorAccent(context: Context):Int{
    val typedValue=TypedValue()
    context.theme.resolveAttribute(android.R.attr.colorAccent,typedValue,true)
    return typedValue.data
}

fun TextView.loadAnimation(context: Context,resId:Int){
    val animation=AnimationUtils.loadAnimation(context,resId)
    this.startAnimation(animation)
}

/**
 *
 */
fun EditText.verifyString(regulation:String):Boolean{
    return when{
        this.text.isEmpty()->false
        else->{
            val pattern=Pattern.compile(regulation)
            val matcher=pattern.matcher(this.text)
            matcher.matches()
        }
    }
}