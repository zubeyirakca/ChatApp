package com.moralabs.chatapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moralabs.chatapp.databinding.FragmentSignInBinding
import com.moralabs.chatapp.util.FirebaseUtil
import com.moralabs.chatapp.util.NotifyMessage
import com.moralabs.chatapp.util.Singleton

class SignInFragment : Fragment(), View.OnClickListener{
    private lateinit var v : View
    private lateinit var binding : FragmentSignInBinding
    private lateinit var mainIntent : Intent


    private lateinit var txtUserEmail : String
    private lateinit var txtUserPassword : String

    private fun init(){
        txtUserEmail = binding.signInFragmentEditEmail.text.toString()
        txtUserPassword = binding.signInFragmentEditPassword.text.toString()
        binding.signInFragmentBtnSignIn.setOnClickListener(this)
        binding.signInFragmentBtnSignUp.setOnClickListener(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v=view
        init()
        FirebaseUtil.signInControl(object : NotifyMessage{
            override fun onSuccess(msg: String) {
                TODO("Not yet implemented")
            }

            override fun onFailure(msg: String?) {
                msg?.let {
                    Singleton.showSnack(v,it)
                }
            }

        }, singInControlOnComplete = {fUser, signState ->  
            if(signState){
                fUser?.let {
                    goToMainPage(it.uid)
                }
            }
        })
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when(it.id){
                binding.signInFragmentBtnSignIn.id -> signInToUser()
                binding.signInFragmentBtnSignUp.id -> goToSignUpPage()
            }
        }
    }
    private fun signInToUser(){
        init()
        if(!txtUserEmail.isEmpty()){
            if(!txtUserPassword.isEmpty()){
                FirebaseUtil.signInUser(txtUserEmail, txtUserPassword, object : NotifyMessage{
                    override fun onSuccess(msg: String) {
                        Singleton.showSnack(v, msg)
                    }
                    override fun onFailure(msg: String?) {
                        msg?.let {
                            Singleton.showSnack(v, msg)
                        }
                    }

                }, signInUserOnComplete = { fUser, signState ->
                    if(signState){
                        fUser?.let {
                            goToMainPage(it.uid)
                        }
                    }

                })
            }else{
                Singleton.showSnack(v,"Lütfen Şifrenizi Giriniz")
            }
        }else{
            Singleton.showSnack(v, "Lütfen e-mail adresinizi giriniz")
        }

    }
    private fun goToSignUpPage(){
        Singleton.setPage(1)
    }
    private fun goToMainPage(userId : String){
        mainIntent = Intent(v.context,MainActivity::class.java)
        mainIntent.putExtra("userId", userId)
        startActivity(mainIntent)
        (v.context as Activity).finish()
    }

}