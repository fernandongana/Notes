package co.mz.noteApp

import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import co.mz.noteApp.databinding.FragmentPerfilBinding
import co.mz.noteApp.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import java.io.IOException


class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var photoImage: ImageView

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        binding.loading.visibility = View.VISIBLE
        binding.layout.visibility = View.GONE
        photoImage = binding.photoUri
        userViewModel.user.observe(viewLifecycleOwner, { user ->
            if (user?.email != null) {
               // val storageReference : StorageReference = user.photoUrl

                   val img = "https://firebasestorage.googleapis.com/v0/b/noteapp-6d2fa.appspot.com/o/uploads%2FIMG_0015.JPG?alt=media"
               Glide.with(this /* context */).load(img).diskCacheStrategy(DiskCacheStrategy.ALL).into(photoImage).waitForLayout()

                Log.v("USER", "user on profile Url : ${user?.photoUrl}")

               /*  try {
                 //    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, userViewModel.user.value?.photoUrl)
                     val bitmap = when {
                         Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                             activity?.contentResolver,
                             user.photoUrl
                         )
                         else -> {
                             val source = activity?.contentResolver?.let {
                                 user.photoUrl?.let { it1 ->
                                     ImageDecoder.createSource(
                                         it,
                                         it1
                                     )
                                 }
                             }
                             ImageDecoder.decodeBitmap(source!!)
                         }
                     }
                   //  photoImage.setImageBitmap(bitmap)
                 } catch (e: IOException) {
                    e.printStackTrace()
                 } */

                binding.loading.visibility = View.GONE
                binding.layout.visibility = View.VISIBLE
                Log.v("init", user.email.toString())
                val nameTexView = binding.name
                val emailTexView = binding.email
                nameTexView.text = userViewModel.user.value?.displayName.toString()
                emailTexView.text = userViewModel.user.value?.email.toString()
                showWelcomeMessage(user.email.toString())
                Log.v("user Profile", "Already have User......")
            } else {
                Log.v("user Profile", "Navigate to Login......")
                showWelcomeMessage("Login Failed")
                navController.navigate(R.id.loginFragment)
            }
        })

        binding.editButton.setOnClickListener {
            val intent = Intent(requireActivity(), EditPerfilActivity::class.java)
            requireActivity().startActivity(intent)
        }

    }



    private fun showWelcomeMessage(user : String) {
        Toast.makeText(requireContext(), "User: $user", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = activity?.run {
            ViewModelProvider(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val preferences = requireActivity().getSharedPreferences("MY_APP", Context.MODE_PRIVATE)
        val retrivedToken = preferences.getString("TOKEN", null) //second parameter default value.
        val retrivedEmail = preferences.getString("Email", null)
        val retrivedPassword = preferences.getString("Password", null)
        if(retrivedToken.isNullOrEmpty()){
            userViewModel.initiate()
        }else{
            if(userViewModel.user.value == null){
                if (retrivedPassword != null && retrivedEmail != null) {
                    userViewModel.link(retrivedEmail, retrivedPassword)
                }
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPerfilBinding.inflate(inflater,container,false)
        return  binding.root
    }


}