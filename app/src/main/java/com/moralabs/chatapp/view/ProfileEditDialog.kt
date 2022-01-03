package com.moralabs.chatapp.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.moralabs.chatapp.R
import com.moralabs.chatapp.model.User
import com.moralabs.chatapp.util.FirebaseUtil
import com.moralabs.chatapp.util.Singleton

class ProfileEditDialog(val mContext : Context, val user : User) : Dialog(mContext) {
    private lateinit var editUserNickName : EditText
    private lateinit var editUserName : EditText
    private lateinit var editUserSurname : EditText
    private lateinit var btnSave : Button

    private fun init(){

        editUserNickName = findViewById(R.id.profile_edit_dialog_editUserNickName)
        editUserName = findViewById(R.id.profile_edit_dialog_editUserName)
        editUserSurname = findViewById(R.id.profile_edit_dialog_editUserSurname)
        btnSave = findViewById(R.id.profile_edit_dialog_btnSave)

        editUserName.setText(user.userName)
        editUserSurname.setText(user.userSurname)
        editUserNickName.setText(user.userNickname)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit_dialog)
        init()


        btnSave.setOnClickListener {
            if(!editUserNickName.text.isEmpty()){
                if(!editUserName.text.isEmpty()){
                    if(!editUserSurname.text.isEmpty()){
                        FirebaseUtil.updateUserInfo(user.userId, editUserNickName.text.toString(), editUserName.text.toString(),
                                    editUserSurname.text.toString())
                        if(this.isShowing)
                            this.dismiss()
                    }else{
                        Singleton.showToast(mContext, "UserSurname Boş Bırakılamaz")
                    }
                }else{
                    Singleton.showToast(mContext, "UserName Boş Bırakılamaz")
                }
            }else{
                Singleton.showToast(mContext, "NickName Boş Bırakılamaz")
            }
        }

    }

}