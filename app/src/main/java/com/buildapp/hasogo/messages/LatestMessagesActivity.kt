package com.buildapp.hasogo.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.buildapp.hasogo.NewMessageActivity
import com.buildapp.hasogo.R
import com.buildapp.hasogo.RegisterActivity
import com.buildapp.hasogo.models.ChatMessage
import com.buildapp.hasogo.models.User
import com.buildapp.hasogo.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)


        recyclerview_latest_messages.adapter=adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "123")
            val intent = Intent(this, ChatLogActivity::class.java)
        val row = item as LatestMessageRow


           intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        //  setupDummyRows()
        listenForLatestMessages()

        fetchCurrentUser()

        verifyUserIsLoggedIn()
    }
    val latestMessageMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessageMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }

    }
    private fun listenForLatestMessages() {
        val fromid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromid")
        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
           val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return

               latestMessageMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                 val chatMessage = snapshot.getValue(ChatMessage::class.java)?:return
                 latestMessageMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        } )
    }
    val adapter = GroupAdapter<GroupieViewHolder>()
//    private fun setupDummyRows() {
//
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//
//    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.profileImageURL}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })
    }
    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent =Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when (item.itemId) {
         R.id.menu_new_message -> {
            val intent = Intent(this, NewMessageActivity::class.java)
             startActivity(intent)

         }
           R.id.menu_sign_out -> {
               FirebaseAuth.getInstance().signOut()
               val intent =Intent(this, RegisterActivity::class.java)
               intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)
           }
       }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }



}