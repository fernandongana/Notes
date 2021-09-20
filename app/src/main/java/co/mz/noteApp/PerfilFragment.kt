package co.mz.noteApp

import android.content.Context
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




class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        val navController = findNavController()
        userViewModel.user.observe(viewLifecycleOwner, { user ->
            if (user?.email != null) {
                Log.v("init", user.email.toString())
                showWelcomeMessage(user.email.toString())
                Log.v("user Profile", "Already have User......")
            } else {
                Log.v("user Profile", "Navigate to Login......")
                showWelcomeMessage("Login Failed")
                navController.navigate(R.id.loginFragment)
            }
        })
    }

    private fun showWelcomeMessage(user : String) {
        Toast.makeText(requireContext(), "User: "+ user, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = activity?.run {
            ViewModelProvider(this).get(UserViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val navController = findNavController()

        val preferences = requireActivity().getSharedPreferences("MY_APP", Context.MODE_PRIVATE)
        val retrivedToken = preferences.getString("TOKEN", null) //second parameter default value.

        if(retrivedToken.isNullOrEmpty()){
            userViewModel.initiate()
        }else{
          //  userViewModel.login("fernandongana@gmail.com","123456")
            userViewModel.link(retrivedToken)
        }




   /*     val currentBackStackEntry = navController.currentBackStackEntry!!
        val savedStateHandle = currentBackStackEntry.savedStateHandle
        savedStateHandle.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry, { success ->
                if (!success) {
                    Log.v("Login", "LOGIN_SUCCESSFUL")
                    val startDestination = navController.graph.startDestination
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }else{
                    Log.v("Login", "Login Failed")
                }
            }) */
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