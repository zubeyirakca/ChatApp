package com.moralabs.chatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moralabs.chatapp.R
import com.moralabs.chatapp.model.ChatChannel
import com.moralabs.chatapp.model.User
import com.moralabs.chatapp.util.FirebaseUtil
import com.moralabs.chatapp.util.NotifyMessage
import com.moralabs.chatapp.view.ChatActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.ArrayList

class MessageUserAdapter (val ChatChannelList :ArrayList<ChatChannel>, val mContext: Context):
    RecyclerView.Adapter<MessageUserAdapter.MessageInfoHolder>() {
    private lateinit var v : View
    private lateinit var listener: OnItemClickListener
    private var aPos: Int=0
    private lateinit var chatIntent : Intent

    inner class MessageInfoHolder(v:View) : RecyclerView.ViewHolder(v){
        var imgProfile :CircleImageView= v.findViewById(R.id.message_design_imgProfile)
        var txtNickname : TextView=v.findViewById(R.id.message_design_txtNickname)
        var cardView: CardView=v.findViewById(R.id.message_design_cardView)
        var txtLastMessage : TextView = v.findViewById(R.id.message_design_textLastMessage)
        fun setLastMessage(lMessage : String){
            txtLastMessage.setText(lMessage)
        }
        fun bind(user: User)
        {
            txtNickname.text=user.userNickname
            if(!user.userPicture.equals("Default")){
                Glide.with(v.context)
                    .load(user.userPicture)
                    .into(imgProfile)
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClick(user: User){
        }
    }

    fun setOnItemClickListener(Listener : OnItemClickListener){
        this.listener=Listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageInfoHolder {
        v= LayoutInflater.from(mContext).inflate(R.layout.message_design, parent, false)
        return MessageInfoHolder(v)
    }

    override fun onBindViewHolder(holder: MessageInfoHolder, position: Int) {
        FirebaseUtil.getUserDataListener(ChatChannelList.get(position).userId, object : NotifyMessage{
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
                holder.bind(it)

            }
        })
        FirebaseUtil.getLastMessage(ChatChannelList.get(position).channelId, getLastMessageOnComplete = {lastMessage ->
            if(!lastMessage.isEmpty()){
                holder.setLastMessage(lastMessage)
            }
        })
        holder.cardView.setOnClickListener {
            aPos=holder.adapterPosition
            if (aPos!=RecyclerView.NO_POSITION)
            {
                chatIntent = Intent(mContext,ChatActivity::class.java)
                chatIntent.putExtra("receiverId", ChatChannelList.get(aPos).userId)
                mContext.startActivity(chatIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return ChatChannelList.size
    }
}