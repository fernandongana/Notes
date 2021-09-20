package co.mz.noteApp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import co.mz.noteApp.databinding.FragmentLoginBinding
import co.mz.noteApp.viewmodel.UserViewModel
import android.content.SharedPreferences
import android.util.Log


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = activity?.run {
            ViewModelProvider(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private lateinit var userViewModel: UserViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(LOGIN_SUCCESSFUL, false)

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            login(username, password)
        }
    }
    
    private fun login(email: String, password: String) {
        val login = userViewModel.login(email, password)
        login.observe(viewLifecycleOwner, { result ->
            if (result != null) {

                result.getIdToken(false).addOnCompleteListener {
                    if (it.isSuccessful) {
                        var token = it.result?.token.toString()
                        val preferences = requireActivity().getSharedPreferences("MY_APP", Context.MODE_PRIVATE)
                        preferences.edit().putString("TOKEN", token).apply()
                        preferences.edit().putString("Email", email).apply()
                        preferences.edit().putString("Password", password).apply()
                        Log.v("Login Token", token)
                        savedStateHandle.set(LOGIN_SUCCESSFUL, true)
                        findNavController().popBackStack()
                    }else{
                        Log.v("Error", it.exception.toString())
                    }
                }

            } else {
                showErrorMessage()
            }
        })
    }

    private fun showErrorMessage() {
        Toast.makeText(requireContext(), "Erro ao fazer login ", Toast.LENGTH_SHORT).show()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return  binding.root
    }
}