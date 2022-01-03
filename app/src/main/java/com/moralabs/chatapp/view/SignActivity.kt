package com.moralabs.chatapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.moralabs.chatapp.R
import com.moralabs.chatapp.adapter.CustomViewPager
import com.moralabs.chatapp.databinding.ActivityMainBinding
import com.moralabs.chatapp.databinding.ActivitySignBinding
import com.moralabs.chatapp.databinding.FragmentSignInBinding
import com.moralabs.chatapp.util.Singleton

class SignActivity : AppCompatActivity() {
    private lateinit var mViewPager : ViewPager
    private lateinit var pagerAdapter :CustomViewPager
    private lateinit var binding: ActivitySignBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mViewPager = binding.signActivityViewPager
        pagerAdapter = CustomViewPager(supportFragmentManager)
        pagerAdapter.addFragment(SignInFragment())
        pagerAdapter.addFragment(SignUpFragment())

        mViewPager.adapter =pagerAdapter
        Singleton.mViewPager = mViewPager

    }
}