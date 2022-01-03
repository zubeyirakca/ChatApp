package com.moralabs.chatapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.moralabs.chatapp.adapter.MessageUserAdapter
import com.moralabs.chatapp.databinding.FragmentMessagesBinding
import com.moralabs.chatapp.model.ChatChannel
import com.moralabs.chatapp.model.User
import com.moralabs.chatapp.util.FirebaseUtil
import com.moralabs.chatapp.util.NotifyMessage

class MessagesFragment : Fragment() {
    private lateinit var binding : FragmentMessagesBinding
    private lateinit var v : View
    private lateinit var messagesUserAdapter: MessageUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        FirebaseUtil.getChannels(FirebaseUtil.mAuth.uid!!, getOldMessagesComplete = { channels ->
            binding.messagesFragmentRecyclerView.setHasFixedSize(true)
            binding.messagesFragmentRecyclerView.layoutManager = LinearLayoutManager(
                v.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            messagesUserAdapter = MessageUserAdapter(channels, v.context)
            binding.messagesFragmentRecyclerView.adapter = messagesUserAdapter
            messagesUserAdapter.notifyDataSetChanged()
        })


    }
}