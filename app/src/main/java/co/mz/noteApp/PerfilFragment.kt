package co.mz.noteApp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import co.mz.noteApp.databinding.FragmentPerfilBinding
import co.mz.noteApp.viewmodel.UserViewModel
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        binding.loading.visibility = View.VISIBLE
        binding.layout.visibility = View.GONE
        userViewModel.user.observe(viewLifecycleOwner, { user ->
            if (user?.email != null) {
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
        Toast.makeText(requireContext(), "User: "+ user, Toast.LENGTH_SHORT).show()
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