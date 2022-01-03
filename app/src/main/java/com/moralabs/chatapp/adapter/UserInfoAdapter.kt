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
import com.moralabs.chatapp.model.User
import com.moralabs.chatapp.view.ChatActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.ArrayList

class UserInfoAdapter(val userList:ArrayList<User>, val mContext: Context):
    RecyclerView.Adapter<UserInfoAdapter.UserInfoHolder>() {
    private lateinit var v : View
    private lateinit var listener: OnItemClickListener
    private var aPos: Int=0
    private lateinit var chatIntent : Intent

    inner class UserInfoHolder(v:View) : RecyclerView.ViewHolder(v){
        var imgProfile :CircleImageView= v.findViewById(R.id.homeDesignImage)
        var txtNickname : TextView=v.findViewById(R.id.homeDesignNicknameTxt)
        var cardView: CardView=v.findViewById(R.id.homeDesignCardview)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoHolder {
        v= LayoutInflater.from(mContext).inflate(R.layout.home_design, parent, false)
        return UserInfoHolder(v)
    }

    override fun onBindViewHolder(holder: UserInfoHolder, position: Int) {
        holder.bind(userList.get(position))
        holder.cardView.setOnClickListener {
            aPos=holder.adapterPosition
            if (aPos!=RecyclerView.NO_POSITION)
            {
                chatIntent  = Intent(mContext,ChatActivity::class.java)
                chatIntent.putExtra("receiverId", userList.get(aPos).userId)
                mContext.startActivity(chatIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}