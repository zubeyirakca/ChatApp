package com.moralabs.chatapp.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moralabs.chatapp.R
import com.moralabs.chatapp.model.Message
import com.moralabs.chatapp.util.FirebaseUtil
import kotlin.collections.ArrayList
private val VIEW_TYPE_MESSAGE_SENT = 1
private val VIEW_TYPE_MESSAGE_RECEIVED = 2
class ChatAdapter(val messageList:ArrayList<Message>, val mContext: Context):
    RecyclerView.Adapter<ChatAdapter.ChatHolder>() {

    inner class ChatHolder(v:View) : RecyclerView.ViewHolder(v){
    }

    override fun getItemViewType(position: Int): Int {
        if(messageList.get(position).sentBy == FirebaseUtil.mAuth.uid){
            return VIEW_TYPE_MESSAGE_SENT
        }else{
            return VIEW_TYPE_MESSAGE_RECEIVED
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        if(viewType == 1){
            val view = LayoutInflater.from(mContext).inflate(R.layout.message_textview_design_right, parent, false)
            return ChatHolder(view)
        }else{
            val view = LayoutInflater.from(mContext).inflate(R.layout.message_textview_design_left, parent, false)
            return ChatHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val textView : TextView = holder.itemView.findViewById(R.id.chat_recycler_textView)
        textView.text = messageList.get(position).message
    }

}