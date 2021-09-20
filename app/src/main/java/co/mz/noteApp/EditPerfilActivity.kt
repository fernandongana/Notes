package co.mz.noteApp

import android.content.ContentValues.TAG
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import co.mz.noteApp.data.User
import co.mz.noteApp.databinding.ActivityEditPerfilBinding
import co.mz.noteApp.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class EditPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPerfilBinding
    private var user = Firebase.auth.currentUser

    private lateinit var userViewModel: UserViewModel

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

        binding.displayName.setText(user?.displayName.toString())
        binding.email.setText(user?.email.toString())

        binding.addButton.setOnClickListener{
            clearTextField()
            val displayName = binding.displayName.text.toString().trim()
            val email = binding.email.text.toString().trim()
            if(displayName.isEmpty()){
                binding.inputLayoutName.error = "O nome nao pode ser nulo"
                return@setOnClickListener
            }
            if(email.isEmpty()){
                binding.inputLayoutEmail.error = "O email nao pode ser nulo"
                return@setOnClickListener
            }
            val userProfile = User()

            userProfile.displayName = displayName
            userProfile.photoUri = "www.test.com"
            updateProfile(userProfile)

        }
    }


    private fun init(){
        userViewModel.user.observe(this, {
            if (it != null) {
                user = it
            }
        })
    }

    fun clearTextField(){
        binding.inputLayoutName.error = null
        binding.inputLayoutEmail.error = null
    }

    private fun updateProfile(userProfile: User){
        val profileUpdates = userProfileChangeRequest {
            displayName = userProfile.displayName
            photoUri = Uri.parse(userProfile.photoUri)
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_perfil_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}