package com.moralabs.chatapp.util

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.moralabs.chatapp.model.ChatChannel
import com.moralabs.chatapp.model.Message
import com.moralabs.chatapp.model.User
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object FirebaseUtil {
    val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    val mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()


    lateinit var mUser : User
    private var fUser : FirebaseUser? = null
    lateinit var docRef : DocumentReference
    lateinit var mChatChannel: ChatChannel
    lateinit var mQuery: Query
    lateinit var mMessage : Message

    var mStorageRef: StorageReference= FirebaseStorage.getInstance().reference
    private lateinit var rRef:StorageReference
    private lateinit var sRef: StorageReference

    fun signUpUser(userEmail : String, userPassword: String, notifyMessage: NotifyMessage,
                   signUpUserOnComplete:(fUser:FirebaseUser?, signState: Boolean) -> Unit){

        mAuth.createUserWithEmailAndPassword(userEmail,userPassword)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    fUser=it.result.user
                    if (fUser!=null){
                        fUser!!.sendEmailVerification()
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    notifyMessage.onSuccess("Kullanıcı başarıyla kayıt oldu")
                                    signUpUserOnComplete(fUser,true)
                                }else{
                                    notifyMessage.onFailure(it.exception?.message)
                                    signUpUserOnComplete(fUser, false)
                                }
                            }
                    }else{
                        notifyMessage.onFailure("Kullanıcı datasına ulaşılamadı")
                        signUpUserOnComplete(fUser,false)
                    }
                }else{
                    notifyMessage.onFailure(it.exception?.message)
                    signUpUserOnComplete(fUser,false)
                }
            }
    }

    fun saveUserData(userData: User, notifyMessage: NotifyMessage,saveUserDataOnComplete:(saveState:Boolean) -> Unit){
        mFirestore.collection("Users").document(userData.userId)
            .set(userData)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    notifyMessage.onSuccess("Lütfen email adresinize gelen linki doğrulayınız")
                    saveUserDataOnComplete(true)
                }else{
                    notifyMessage.onFailure(it.exception?.message)
                    saveUserDataOnComplete(false)
                }
            }
    }

    fun signInUser(userEmail : String, userPassword : String, notifyMessage: NotifyMessage,
                   signInUserOnComplete : (fUser : FirebaseUser?, signState : Boolean) -> Unit){
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    fUser =it.result.user
                    if(fUser != null){
                        if(fUser!!.isEmailVerified){
                            notifyMessage.onSuccess("Başarıyla Giriş Yaptınız")
                            signInUserOnComplete(fUser, true)
                        }else{
                            notifyMessage.onFailure("Lütfen Email Adresinizi Onaylayınız")
                            signInUserOnComplete(fUser, false)
                        }
                    }else{
                        notifyMessage.onFailure("Kullanıcı Datası Boş")
                        signInUserOnComplete(fUser, false)
                    }
                }else{
                    notifyMessage.onFailure(it.exception?.message)
                    signInUserOnComplete(fUser, false)
                }
            }
    }

    fun signInControl(notifyMessage: NotifyMessage,
                      singInControlOnComplete : (fUser : FirebaseUser?, signState : Boolean) -> Unit){
        fUser = mAuth.currentUser
        if(fUser != null){
            if(fUser!!.isEmailVerified){
                singInControlOnComplete(fUser, true)

            }else{
                notifyMessage.onFailure("Lütfen mail adresinizi onaylayınız")
                singInControlOnComplete(fUser, false)
            }
        }else{
            singInControlOnComplete(fUser, false)
        }
    }

    fun getUserDataListener(userId : String, notifyMessage: NotifyMessage,
                            getUserDataListenerOnComplete : (userData : User?) -> Unit){
        docRef = mFirestore.collection("Users").document(userId)
        docRef.addSnapshotListener { value, error ->
            if(error != null){
                notifyMessage.onFailure(error.message)
                getUserDataListenerOnComplete(mUser)
                return@addSnapshotListener
            }
            if (value != null){
                if(value.exists()){
                    mUser = value.toObject(User::class.java)!!
                    notifyMessage.onSuccess("User verileri başarıyla alındı")
                    getUserDataListenerOnComplete(mUser)
                }else{
                    notifyMessage.onFailure("User datası boş")
                }
            }else{
                notifyMessage.onFailure("Data boş")
                getUserDataListenerOnComplete(mUser)
            }

        }
    }

    fun changeProfilePicture(userId : String, uri : String ){
        mFirestore.collection("Users").document(userId).update("userPicture", uri)
    }

    fun updateUserInfo(userId : String,userNickName : String,userName : String, userSurname : String){
        mFirestore.collection("Users").document(userId).update("userNickname" , userNickName
            , "userName", userName, "userSurname", userSurname)
    }

    fun getAllUserInfo(userId : String , getAllUserInfoComplete : (userList : ArrayList<User>) -> Unit){
        var userList : ArrayList<User>? = null
        mFirestore.collection("Users")
            .whereNotEqualTo("userId", userId)
            .get()
            .addOnSuccessListener {
                if (it.documents.size > 0){
                    userList = ArrayList()
                    var userData : User
                    for (d in 0 until it.documents.size){
                        if (it.documents.get(d).exists()){
                            userData = it.documents.get(d).toObject(User::class.java)!!
                            userList!!.add(userData)
                            if (d == it.documents.size -1){
                                getAllUserInfoComplete(userList!!)
                            }
                        }
                    }
                }
            }.addOnFailureListener {
                Log.w(TAG, "Error getting documents: ", it)
            }
    }

    fun getImageDownloadUrl(imgBytes: ByteArray, savePath: String, notifyMessage: NotifyMessage, getImageDownloadUrlOnComplete:(imgDownloadUrl:String?)-> Unit)
    {
        var imgUrl:String?= null
        rRef = mStorageRef.child(savePath)
        rRef.putBytes(imgBytes)
            .addOnSuccessListener {
                sRef=FirebaseStorage.getInstance().getReference(savePath)
                sRef.downloadUrl
                    .addOnSuccessListener {
                        imgUrl=it.toString()
                        notifyMessage.onSuccess("Image link succesfully received")
                        getImageDownloadUrlOnComplete(imgUrl)
                    }.addOnFailureListener {
                        notifyMessage.onFailure("Something went wrong: ${it.message}")
                        getImageDownloadUrlOnComplete(imgUrl)
                    }
            }.addOnFailureListener {
                notifyMessage.onFailure("Something went wrong: ${it.message}")
                getImageDownloadUrlOnComplete(imgUrl)
            }
    }

   /* fun getChatId(senderId : String , receiverId : String){
        val users = arrayOf(senderId, receiverId)
        mFirestore.collection("Chats")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    println(document.id)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

    }*/

    fun saveMessageData(senderId : String, receiverId : String, message: String, chatId :String){
        var chatChannel = ChatChannel(receiverId,chatId)
        mFirestore.collection("Users").document(senderId).collection("Chats").
        document(receiverId).set(chatChannel)
            .addOnSuccessListener {
                println("Chat ID si kaydedildi")
                chatChannel = ChatChannel(senderId,chatId)
                mFirestore.collection("Users").document(receiverId).collection("Chats")
                    .document(senderId).set(chatChannel)
                    .addOnSuccessListener {
                        val msgdoc : HashMap<String, Any> = HashMap()
                        msgdoc.put("chatId", chatId)
                        msgdoc.put("message", message)
                        msgdoc.put("messageTime", FieldValue.serverTimestamp())
                        msgdoc.put("sentBy", senderId)
                        mFirestore.collection("Messages").document().set(msgdoc).addOnSuccessListener {
                            println("Message Gönderildi")
                        }
                    }
            }.addOnFailureListener{
                println("Chat ID si kayıt edilemedi")
            }
    }

    fun sendMessage(senderId : String, receiverId : String, message: String){
        checkChannel(senderId,receiverId, checkChannelOnComplete = {checkState, chnlId ->
            if(checkState == true){
                saveMessageData(senderId,receiverId,message,chnlId)
            }
            else {
                val randomID: String = UUID.randomUUID().toString()
                createChatChannel(
                    randomID,
                    senderId,
                    receiverId,
                    createChatChannelOnComplete = { chnlId, dState ->
                        if (dState) {
                            saveMessageData(senderId, receiverId, message, chnlId)
                        }
                    })
            }
        })
    }

    fun getChannels(userId : String , getOldMessagesComplete : (chatChannels : ArrayList<ChatChannel>) -> Unit){
        var mchannelList : ArrayList<ChatChannel> = ArrayList()
        mFirestore.collection("Users").document(userId).collection("Chats")
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    for (d in 0 until it.documents.size){
                        if (it.documents.get(d).exists()){
                            mChatChannel = it.documents.get(d).toObject(ChatChannel::class.java)!!
                            mchannelList.add(mChatChannel)

                            if (d == it.documents.size -1)
                                getOldMessagesComplete(mchannelList)
                        }else{
                            if (d == it.documents.size -1)
                                getOldMessagesComplete(mchannelList)
                        }
                    }
                }else
                    println("Boş")
            }.addOnFailureListener {
                println("Hatalı")
            }
    }

    fun checkChannel(userId: String, targetId: String, checkChannelOnComplete: (checkState: Boolean, chnlId: String) -> Unit){
        docRef = mFirestore.collection("Users").document(userId)
            .collection("Chats").document(targetId)
        docRef.get()
            .addOnSuccessListener {
                if (it.exists()){
                    mChatChannel = it.toObject(ChatChannel::class.java)!!
                    checkChannelOnComplete(true, mChatChannel.channelId)
                }else
                    checkChannelOnComplete(false, "")
            }.addOnFailureListener {
                checkChannelOnComplete(false, "")
            }
    }

    fun createChatChannel(channelId: String, userId: String, targetId: String, createChatChannelOnComplete: (chnlId: String, dState: Boolean) -> Unit){
        mChatChannel = ChatChannel(userId, channelId)

        mFirestore.collection("Users").document(targetId)
            .collection("Chats").document(userId)
            .set(mChatChannel)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    mChatChannel = ChatChannel(targetId, channelId)

                    mFirestore.collection("Users").document(userId)
                        .collection("Chats").document(targetId)
                        .set(mChatChannel)
                        .addOnCompleteListener {
                            if (it.isSuccessful)
                                createChatChannelOnComplete(channelId, true)
                            else
                                createChatChannelOnComplete("", false)
                        }
                }else
                    createChatChannelOnComplete("", false)
            }
    }

    fun getAllMessageFromChannel(senderId: String, receiverId: String,
                                 getAllMessageFromChannelOnComplete : (mList : ArrayList<Message>) -> Unit){
        val messageList : ArrayList<Message> = ArrayList()
        checkChannel(senderId, receiverId, checkChannelOnComplete = {checkState, chnlId ->
            if(checkState && !chnlId.isEmpty()){
                mFirestore.collection("Messages").orderBy("messageTime",Query.Direction.ASCENDING)
                    .addSnapshotListener { value, error ->
                        if(error != null){
                            messageList.clear()
                            getAllMessageFromChannelOnComplete(messageList)
                        }else{
                            messageList.clear()
                            for(d in value!!.documents){
                                if(d.get("chatId") == chnlId){
                                    mMessage = d.toObject(Message::class.java)!!
                                    messageList.add(mMessage)
                                }
                            }
                            getAllMessageFromChannelOnComplete(messageList)
                        }
                    }
            }else{
                val randomID: String = UUID.randomUUID().toString()
                createChatChannel(
                    randomID,
                    senderId,
                    receiverId,
                    createChatChannelOnComplete = { chnlId, dState ->
                        if(dState == true){
                            mFirestore.collection("Messages").orderBy("messageTime",Query.Direction.ASCENDING)
                                .addSnapshotListener { value, error ->
                                    if(error != null){
                                        messageList.clear()
                                        getAllMessageFromChannelOnComplete(messageList)
                                    }else{
                                        messageList.clear()
                                        for(d in value!!.documents){
                                            if(d.get("chatId") == chnlId){
                                                mMessage = d.toObject(Message::class.java)!!
                                                messageList.add(mMessage)
                                            }
                                        }
                                        getAllMessageFromChannelOnComplete(messageList)
                                    }
                                }
                        }
                    })
            }
        })
    }
    fun getLastMessage(channelId : String, getLastMessageOnComplete: (lastMessage : String) -> Unit){
        mFirestore.collection("Messages")
            .whereEqualTo("chatId",channelId)
            .orderBy("messageTime", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { value, error ->
                if(error != null){
                   getLastMessageOnComplete("")
                }else{
                    for (d in value!!.documents){
                        mMessage = d.toObject(Message::class.java)!!
                        getLastMessageOnComplete(mMessage.message)
                    }
                }

            }
    }
    fun firebaseLogout(){
        FirebaseAuth.getInstance().signOut()
    }

}