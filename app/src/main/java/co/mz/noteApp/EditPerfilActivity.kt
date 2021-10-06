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
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import co.mz.noteApp.model.User
import co.mz.noteApp.databinding.ActivityEditPerfilBinding
import co.mz.noteApp.util.NODE_USERS
import co.mz.noteApp.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private var storageReference: StorageReference? = null
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

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        binding.displayName.setText(user?.displayName.toString())
        binding.email.setText(user?.email.toString())
        Glide.with(this /* context */).load(user?.photoUrl).diskCacheStrategy(
            DiskCacheStrategy.ALL).into(binding.imagePreview).waitForLayout()
        init()
        loading(false)

        user?.let { userViewModel.post(it) }

        binding.imagePreview.setOnClickListener {
            loading(true)
            launchGallery()
        }

        binding.addButton.setOnClickListener{
            loading(true)
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

            saveFirestore(displayName!!, user?.photoUrl!!)
            loading(false)
        }
    }

    private fun loading( loading : Boolean){
        if(loading){
            binding.loading.visibility = View.VISIBLE
            binding.layout.visibility = View.GONE
        }else{
            binding.loading.visibility = View.GONE
            binding.layout.visibility = View.VISIBLE
        }
    }


    private fun init(){
        userViewModel.user.observe(this, {
            Log.v("Init Function", "Loading")
            if (it != null) {
                user = it
                loading(false)
            }else{
                Log.v("Init Function", "Failed load user")
                loading(false)
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
                    saveProfile(downloadUri.toString())
                    loading(false)
                }
            }?.addOnFailureListener{
                Toast.makeText(this, "Falha ao Gravar", Toast.LENGTH_SHORT).show()
                loading(false)
            }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
            loading(false)
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            filePath = data?.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.imagePreview.setImageBitmap(bitmap)
                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        userViewModel.initiate()
        user?.let { userViewModel.post(it) }
    }

    private fun saveProfile(uri: String){
        val tempUser = User()
        tempUser.photoUri = uri

        user?.email?.let { it ->
            firebaseFirestore.collection(NODE_USERS)
                .document(it)
                .update("photoUri", tempUser.photoUri.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "Gravado com sucesso.", Toast.LENGTH_LONG).show()
                    user?.let { userViewModel.post(it) }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving to DB $e", Toast.LENGTH_LONG).show()
                }
        }

        val userProfile = User()
        userProfile.displayName = displayName
        userProfile.photoUri = uri
        updateProfilePhoto(userProfile)
    }

    private fun saveFirestore(name : String, uri: Uri){
        val tempUser = User()
        tempUser.userId = user?.uid.toString()
        tempUser.displayName = name
        tempUser.photoUri = uri.toString()
        tempUser.email = user?.email.toString()

        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Actualizado com sucesso", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

        user?.email?.let {
            firebaseFirestore.collection(NODE_USERS)
                .document(it)
                .set(tempUser, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "Gravado com sucesso.", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving to DB $e", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun clearTextField(){
        binding.inputLayoutName.error = null
        binding.inputLayoutEmail.error = null
    }

    private fun updateProfilePhoto(userProfile: User){
        val profileUpdates = userProfileChangeRequest {
            photoUri = Uri.parse(userProfile.photoUri)
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    finish()
                }
            }
            .addOnFailureListener { task->
                Toast.makeText(this, task.message.toString(), Toast.LENGTH_SHORT).show()
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