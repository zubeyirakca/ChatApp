package com.moralabs.chatapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moralabs.chatapp.R
import com.moralabs.chatapp.adapter.MessageUserAdapter
import com.moralabs.chatapp.adapter.UserInfoAdapter
import com.moralabs.chatapp.databinding.FragmentHomeBinding
import com.moralabs.chatapp.model.User
import com.moralabs.chatapp.util.FirebaseUtil
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment(val userId: String) : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var v : View
    private var mUserList : ArrayList<User> = ArrayList()
    private lateinit var userInfoAdapter : UserInfoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        FirebaseUtil.getAllUserInfo(userId,getAllUserInfoComplete = { userList ->
            if(!userList.isEmpty()){
                mUserList = userList
                binding.homeFragmentRecyclerView.setHasFixedSize(true)

                binding.homeFragmentRecyclerView.layoutManager = GridLayoutManager(
                    v.context,
                    2
                )
                userInfoAdapter = UserInfoAdapter(mUserList, v.context)
                binding.homeFragmentRecyclerView.adapter = userInfoAdapter
            }
        })
    }
}