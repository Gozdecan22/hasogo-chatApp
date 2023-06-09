package com.buildapp.hasogo



import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buildapp.hasogo.messages.LatestMessagesActivity
import com.buildapp.hasogo.models.User
import com.buildapp.hasogo.registerlogin.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {
companion object {
    val TAG = "RegisterActivity"
}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        register_button_register.setOnClickListener {
          performRegister()
        }

        already_have_account_textview.setOnClickListener {
            Log.d("RegisterActivity", "Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"
            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))


            val PICK_IMAGE = 0
            startActivityForResult(chooserIntent, PICK_IMAGE);
        }
    }

    private lateinit var auth: FirebaseAuth
    var selectedPhotoUri : Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {

            Log.d("RegisterActivity", "Photo was selected")

           selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
           selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f
           // val bitmapDrawable = BitmapDrawable(bitmap)
           // selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister() {


        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Please  text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Attempting to create user with email: $email")


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if  (!it.isSuccessful) return@addOnCompleteListener
                Log.d(TAG,  "Successfully created user with uid: ${it.result.user?.uid}")

                uploadImageToFirebaseStorage()
            }

            .addOnFailureListener{
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this,"Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()

            }
    }



    private fun uploadImageToFirebaseStorage() {
        val storage = Firebase.storage
        storage.reference
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                   it.toString()
                   Log.d(TAG, "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"Failed to upload image to storage: ${it.message}")

            }
    }
    private fun saveUserToFirebaseDatabase(profileImageURL: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

           val user = User(uid, username_edittext_register.text.toString(), profileImageURL)
           ref.setValue(user)
               .addOnSuccessListener {
                   Log.d(TAG, "Finally we saved the user to Firebase Database")

                   val intent = Intent(this, LatestMessagesActivity::class.java)
                   intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                   startActivity(intent)
               }
               .addOnFailureListener {
                   Log.d(TAG, "Failed to set value to database: ${it.message}")
               }

    }


}


















