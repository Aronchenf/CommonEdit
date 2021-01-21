package com.test.edit

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.test.edit.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding:ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBinding=DataBindingUtil.setContentView(this,R.layout.activity_main)
//        edit.setDrawableWidthAndHeight(80,80)
        edit.setErrorStringWithResource("手机格式错误","^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}\$",R.color.colorPrimary)
//        edit.setInputPaddingRight(500)

        mBinding.edit.setOnClickListener {
            Log.e("AppcompatEditText", "dwadasdwad" )
        }
        mBinding.btn.setOnClickListener {
            try {
                Log.e("AppcompatEditText", mBinding.phone!! )
                Log.e("CommonEdit", mBinding.name!!)
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }
}