package com.moralabs.chatapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.moralabs.chatapp.R
import com.moralabs.chatapp.databinding.ActivityMainBinding
import com.moralabs.chatapp.util.FirebaseUtil
import com.moralabs.chatapp.util.NotifyMessage


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var transaction : FragmentTransaction
    private lateinit var homeFragment: HomeFragment
    private lateinit var messagesFragment: MessagesFragment
    private lateinit var profileFragment: ProfileFragment

    private lateinit var getIntent: Intent
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntent = intent
        userId = getIntent.getStringExtra("userId")
        userId?.let {
            FirebaseUtil.getUserDataListener(userId!!, object : NotifyMessage {
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
                    FirebaseUtil.mUser = it
                }
            })

            homeFragment = HomeFragment(it)
            messagesFragment = MessagesFragment()
            profileFragment = ProfileFragment(it)

            setFragment(homeFragment)

            binding.mainActivityBottomNav.setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.bottom_menu_home ->{
                        setFragment(homeFragment)
                        true
                    }
                    R.id.bottom_menu_messages -> {
                        setFragment(messagesFragment)
                        true
                    }
                    R.id.bottom_menu_profile -> {
                        setFragment(profileFragment)
                        true
                    }
                    else -> false
                }
            }

        }

        binding.activityMainLogoutIcon.setOnClickListener {
            var builder= AlertDialog.Builder(this)
            builder.setTitle("Çıkış")
            builder.setMessage("Çıkış yapmak istediğinizden emin misiniz?")
            builder.setPositiveButton("Evet"){dialog, which ->
                val intent = Intent(this,SignActivity::class.java)
                finish()
                startActivity(intent)
                FirebaseUtil.firebaseLogout()
            }
            builder.setNegativeButton("Hayır"){dialog,which ->
            }

            val dialog:AlertDialog=builder.create()
            dialog.show()
        }

    }

    private fun setFragment(fragment: Fragment){
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.mainActivityFrameLayout.id, fragment)
        transaction.commit()
    }
}