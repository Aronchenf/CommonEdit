package com.edit.universaledit

import android.content.Context
import android.util.TypedValue
import android.view.View


/**
 * sp转px
 */
fun Context.sp2px(sp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics).toInt()

fun View.sp2px(sp: Float): Int = context.sp2px(sp)

/**
 * px转sp
 */
fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity
fun View.px2sp(px: Int): Float = context.px2sp(px)


/**
 * px转dp
 */
fun Context.px2dip(px: Int): Float = px.toFloat() / resources.displayMetrics.density

fun View.px2dip(px: Int): Float = context.px2dip(px)