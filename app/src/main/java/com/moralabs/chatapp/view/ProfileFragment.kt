package com.moralabs.chatapp.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.moralabs.chatapp.R
import com.moralabs.chatapp.databinding.FragmentProfileBinding
import com.moralabs.chatapp.model.User
import com.moralabs.chatapp.util.FirebaseUtil
import com.moralabs.chatapp.util.FirebaseUtil.changeProfilePicture
import com.moralabs.chatapp.util.FirebaseUtil.mUser
import com.moralabs.chatapp.util.NotifyMessage
import com.moralabs.chatapp.util.Singleton
import java.io.ByteArrayOutputStream
import java.io.File

class ProfileFragment(val userId: String): Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var v : View



    private val imageRequestCode = 100
    private var imageUri: Uri? = null
    private lateinit var imgSource : ImageDecoder.Source
    private lateinit var selectedBitmap : Bitmap
    private lateinit var outputStream : ByteArrayOutputStream
    private lateinit var imgBytes : ByteArray
    private lateinit var savePath : String
    private lateinit var imgType : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view

        binding.profileFragmentBtnChangeProfilePicture.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,imageRequestCode)
        }

        FirebaseUtil.getUserDataListener(userId, object : NotifyMessage {
            override fun onSuccess(msg: String) {
                println(msg)
            }

            override fun onFailure(msg: String?) {
                msg?.let {
                    println(it)
                }
            }
        }, getUserDataListenerOnComplete = {userData ->
            userData?.let {
                mUser = it
                setData(it)
            }
        })

        binding.profileFragmentBtnEditProfile.setOnClickListener{
            Singleton.showUserInfoDialog(v.context, mUser)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == imageRequestCode) {
            imageUri = data?.data
            imageUri?.let {
               compressImage(imageUri!!)
            }
        }
    }

    private fun compressImage(imgUri: Uri){
        if(Build.VERSION.SDK_INT >= 28){
            imgSource = ImageDecoder.createSource(v.context.contentResolver, imgUri!!)
            selectedBitmap = ImageDecoder.decodeBitmap(imgSource)
        }else{
            selectedBitmap = MediaStore.Images.Media.getBitmap(v.context.contentResolver,imageUri!!)
        }

        outputStream = ByteArrayOutputStream()
        selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        imgBytes = outputStream.toByteArray()
        savePath = "Users/${userId}/userPicture"
        uploadImage(imgBytes, savePath)
    }

    private fun uploadImage(imgBytes : ByteArray, savePath : String){
        FirebaseUtil.getImageDownloadUrl(imgBytes, savePath, object : NotifyMessage{
            override fun onSuccess(msg: String) {
                Singleton.showToast(v.context,msg)
            }

            override fun onFailure(msg: String?) {
                msg?.let {
                    Singleton.showToast(v.context,it)
                }
            }

        }, getImageDownloadUrlOnComplete ={ imgDownloadUrl ->
                imgDownloadUrl?.let {
                    changeProfilePicture(userId, imgDownloadUrl.toString())
                }

        })
    }

    private fun setData(userData : User){
        binding.profileFragmentTextUserName.text = userData.userName
        binding.profileFragmentTextUserNickName.text = userData.userNickname
        binding.profileFragmentTextUserSurname.text = userData.userSurname

        if(!userData.userPicture.equals("Default")){
            Glide.with(v.context)
                .load(userData.userPicture)
                .into(binding.profileFragmentImgProfile)
        }
    }
}