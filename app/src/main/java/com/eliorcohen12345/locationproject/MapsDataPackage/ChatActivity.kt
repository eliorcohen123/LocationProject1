package com.eliorcohen12345.locationproject.MapsDataPackage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eliorcohen12345.locationproject.CustomAdapterPackage.ChatAdapter
import com.eliorcohen12345.locationproject.DataAppPackage.ChatMessage
import com.eliorcohen12345.locationproject.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_room.*

class ChatActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val firestore = FirebaseFirestore.getInstance()
    val chatMessages = ArrayList<ChatMessage>()
    var chatRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        checkUser()

        initList()
        setViewListeners()
    }

    private fun setViewListeners() {
        button_send.setOnClickListener {
            sendChatMessage()
        }
    }

    private fun initList() {
        if (user == null)
            return

        list_chat.layoutManager = LinearLayoutManager(this)
        val adapter = ChatAdapter(chatMessages, user.uid)
        list_chat.adapter = adapter
        listenForChatMessages()
    }

    private fun listenForChatMessages() {
        chatRegistration = firestore.collection("messages")
                .addSnapshotListener { messageSnapshot, exception ->

                    if (messageSnapshot == null || messageSnapshot.isEmpty)
                        return@addSnapshotListener

                    chatMessages.clear()

                    for (messageDocument in messageSnapshot.documents) {
                        chatMessages.add(
                                ChatMessage(
                                        messageDocument["text"] as String,
                                        messageDocument["email"] as String,
                                        messageDocument["user"] as String,
                                        messageDocument.getTimestamp("timestamp")!!.toDate()
                                ))
                    }

                    chatMessages.sortBy { it.timestamp }
                    list_chat.adapter?.notifyDataSetChanged()
                }
    }

    private fun sendChatMessage() {
        val message = edittext_chat.text.toString()
        edittext_chat.setText("")

        if (message != "") {
            firestore.collection("messages")
                    .add(mapOf(
                            Pair("text", message),
                            Pair("email", user?.email),
                            Pair("user", user?.uid),
                            Pair("timestamp", Timestamp.now())
                    ))
        }
    }

    private fun checkUser() {
        if (user == null)
            launchLogin()
    }

    private fun launchLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        chatRegistration?.remove()
    }

}
