package co.mz.noteApp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import co.mz.noteApp.data.User
import co.mz.noteApp.databinding.ActivityEditPerfilBinding
import co.mz.noteApp.viewmodel.UserViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.util.*

class EditPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPerfilBinding
    private var user = Firebase.auth.currentUser

    private lateinit var userViewModel: UserViewModel

    private val PICKIMAGEREQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var uploadImage: ImageView
    private var displayName: String? = null
    private var email: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back, null)
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, Color.WHITE)
        supportActionBar?.setHomeAsUpIndicator(drawable)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        init()

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        binding.displayName.setText(user?.displayName.toString())
        binding.email.setText(user?.email.toString())

        uploadImage = binding.imagePreview

        binding.btnChooseImage.setOnClickListener {
            launchGallery()
        }

        binding.addButton.setOnClickListener{
            clearTextField()
            displayName = binding.displayName.text.toString().trim()
            email = binding.email.text.toString().trim()
            if(displayName!!.isEmpty()){
                binding.inputLayoutName.error = "O nome nao pode ser nulo"
                return@setOnClickListener
            }
            if(email!!.isEmpty()){
                binding.inputLayoutEmail.error = "O email nao pode ser nulo"
                return@setOnClickListener
            }
            uploadImage()



        }
    }


    private fun init(){
        userViewModel.user.observe(this, {
            if (it != null) {
                user = it
            }
        })
    }

    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.v("URI", "uri : ${downloadUri.toString()}")
                    addUploadRecordToDb(downloadUri!!)

                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKIMAGEREQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                uploadImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICKIMAGEREQUEST)
    }

    private fun addUploadRecordToDb(uri: Uri){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        db.collection("users")
            .add(data)
            .addOnSuccessListener {
               // imageUri = uri
                val userProfile = User()
                userProfile.displayName = displayName
                userProfile.photoUri = uri
                updateProfile(userProfile)
                Toast.makeText(this, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving to DB $e", Toast.LENGTH_LONG).show()
            }
    }

    private fun clearTextField(){
        binding.inputLayoutName.error = null
        binding.inputLayoutEmail.error = null
    }

    private fun updateProfile(userProfile: User){
        val profileUpdates = userProfileChangeRequest {
            displayName = userProfile.displayName
            photoUri = userProfile.photoUri
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Actualizado com sucesso", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_perfil_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


}