package com.moralabs.chatapp.util

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.moralabs.chatapp.model.User
import com.moralabs.chatapp.view.ProfileEditDialog

class Singleton {
    companion object{
        var mViewPager : ViewPager? = null

        fun showSnack(v : View, msg : String){
            Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show()
        }
        fun showToast(context : Context, msg : String){
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
        fun setPage(sIn : Int){
            mViewPager?.let {
                it.currentItem = sIn}
        }

        private lateinit var profileEditDialog : ProfileEditDialog
        fun showUserInfoDialog(context: Context, user: User){
            profileEditDialog = ProfileEditDialog(context, user)
            profileEditDialog.setCancelable(true)
            profileEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            profileEditDialog.show()
        }
    }
}