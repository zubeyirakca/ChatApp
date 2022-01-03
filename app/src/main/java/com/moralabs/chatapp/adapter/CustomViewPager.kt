package com.moralabs.chatapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class CustomViewPager(fm : FragmentManager) : FragmentPagerAdapter(fm){
    private var fragmentList : ArrayList<Fragment> = ArrayList()

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList.get(position)
    }

    fun addFragment(fragment : Fragment){
        fragmentList.add(fragment)
    }
}