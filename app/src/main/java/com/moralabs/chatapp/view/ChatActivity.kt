package com.moralabs.chatapp.view


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.moralabs.chatapp.adapter.ChatAdapter


import com.moralabs.chatapp.databinding.ActivityChatBinding
import com.moralabs.chatapp.model.Message
import com.moralabs.chatapp.model.User
import com.moralabs.chatapp.util.FirebaseUtil
import com.moralabs.chatapp.util.NotifyMessage
class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private lateinit var receiverUser : User
    private lateinit var receiverId : String
    private  var messageList : ArrayList<Message> = ArrayList()
    private lateinit var chatAdapter : ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        receiverId = intent.getStringExtra("receiverId").toString()
        getReceiverData(receiverId)
        messageList.clear()

        binding.chatActivityRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.chatActivityRecyclerView.setHasFixedSize(true)
        chatAdapter = ChatAdapter(messageList, this)
        binding.chatActivityRecyclerView.adapter = chatAdapter

        FirebaseUtil.getAllMessageFromChannel(FirebaseUtil.mAuth.uid!!, receiverId, getAllMessageFromChannelOnComplete = {mList ->
            if(mList.size > 0){
                messageList.clear()
                messageList.addAll(mList)
                chatAdapter.notifyDataSetChanged()
                binding.chatActivityRecyclerView.scrollToPosition(messageList.size-1)
            }
        })

        binding.chatActivityImgGoBack.setOnClickListener{
            backToMainActivity()
        }
        binding.chatActivityBtnSend.setOnClickListener{
            if (!binding.chatActivityEditMessage.text.equals("")){
                FirebaseUtil.sendMessage(FirebaseUtil.mAuth.uid.toString(), receiverId, binding.chatActivityEditMessage.text.toString())
                binding.chatActivityEditMessage.setText("")
                chatAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun getReceiverData(receiverId : String){
        FirebaseUtil.getUserDataListener(receiverId,object : NotifyMessage{
            override fun onSuccess(msg: String) {
                println(msg)
            }
            override fun onFailure(msg: String?) {
                msg?.let {
                    println(it)
                }
            }
        }, getUserDataListenerOnComplete = { userData ->
            userData?.let {
                receiverUser = it
                setData(receiverUser)
            }
        })
    }

    private fun setData(receiverData : User){
            binding.chatActivityTxtReceiverNickName.text = receiverData.userNickname
            if(!receiverData.userPicture.equals("Default")){
                Glide.with(this)
                    .load(receiverData.userPicture)
                    .into(binding.chatActivityImgReceiverProfile)
            }

    }
    fun backToMainActivity() {
        onBackPressed()
        finish()
    }
}
