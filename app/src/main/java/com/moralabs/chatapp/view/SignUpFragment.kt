package com.moralabs.chatapp.view

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moralabs.chatapp.databinding.FragmentSignUpBinding
import com.moralabs.chatapp.model.User
import com.moralabs.chatapp.util.FirebaseUtil
import com.moralabs.chatapp.util.NotifyMessage
import com.moralabs.chatapp.util.Singleton


class SignUpFragment : Fragment(), View.OnClickListener {
    private lateinit var v : View
    private lateinit var binding : FragmentSignUpBinding

    private lateinit var txtUserEmail: String
    private lateinit var txtUserNick:String
    private lateinit var txtUserName: String
    private lateinit var txtUserSurname: String
    private lateinit var txtUserPassword:String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun init() {
        txtUserEmail = binding.signUpFragmentEditEmail.text.toString()
        txtUserName = binding.signUpFragmentEditName.text.toString()
        txtUserNick=binding.signUpFragmentEditNickname.text.toString()
        txtUserSurname=binding.signUpFragmentEditSurname.text.toString()
        txtUserPassword=binding.signUpFragmentEditUserPassword.text.toString()

        binding.signUpFragmentBtnSignUp.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when(it.id){
                binding.signUpFragmentBtnSignUp.id -> signUpToUser()
            }
        }
    }

    private fun signUpToUser(){
        init()
        if (!txtUserEmail.isEmpty()){
            if (!txtUserName.isEmpty()){
                if (!txtUserNick.isEmpty()){
                    if (!txtUserPassword.isEmpty()){
                        if (!txtUserSurname.isEmpty()){
                            FirebaseUtil.signUpUser(txtUserEmail,txtUserPassword, object : NotifyMessage{
                                override fun onSuccess(msg: String) {
                                    Singleton.showSnack(v,msg)
                                }
                                override fun onFailure(msg: String?) {
                                    msg?.let {
                                        Singleton.showSnack(v,msg)
                                    }
                                }
                            }, signUpUserOnComplete = {fUser, signState ->
                                if (signState){
                                    fUser?.let {
                                        FirebaseUtil.mUser= User(
                                            it.uid,
                                            txtUserName,
                                            txtUserNick,
                                            txtUserSurname,
                                            txtUserEmail,
                                            "Default"
                                        )
                                        FirebaseUtil.saveUserData(FirebaseUtil.mUser, object :NotifyMessage{
                                            override fun onSuccess(msg: String) {
                                                Singleton.showSnack(v,msg)
                                            }

                                            override fun onFailure(msg: String?) {
                                                msg?.let {
                                                    Singleton.showSnack(v,msg)
                                                }
                                            }
                                        },saveUserDataOnComplete = {
                                                saveState ->
                                            if (saveState){
                                                clearAllEditText()
                                                backToPage()
                                            }
                                        })
                                    }
                                }
                            })

                        }else{
                            Singleton.showSnack(v,"Lütfen geçerli bir soy isim giriniz")
                        }
                    }else{
                        Singleton.showSnack(v,"Lütfen geçerli bir şifre giriniz")
                    }
                }else{
                    Singleton.showSnack(v,"Lütfen geçerli bir nickname giriniz")
                }
            }else{
                Singleton.showSnack(v,"Lütfen geçerli bir isim giriniz")
            }
        }else{
            Singleton.showSnack(v,"Lütfen geçerli bir e-mail adresi giriniz")
        }
    }

    private fun clearAllEditText(){
        binding.signUpFragmentEditEmail.setText("")
        binding.signUpFragmentEditName.setText("")
        binding.signUpFragmentEditNickname.setText("")
        binding.signUpFragmentEditSurname.setText("")
        binding.signUpFragmentEditUserPassword.setText("")
    }

    private fun backToPage(){
        Singleton.setPage(0)
    }

}