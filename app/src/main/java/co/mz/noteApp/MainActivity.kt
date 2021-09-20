package co.mz.noteApp

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import co.mz.noteApp.adapter.CategoryAdapter
import co.mz.noteApp.databinding.ActivityMainBinding
import co.mz.noteApp.viewmodel.CategoryViewModel
import co.mz.noteApp.viewmodel.JobsViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.size
import androidx.navigation.fragment.FragmentNavigator


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var auth = FirebaseAuth.getInstance()
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var jobViewModel: JobsViewModel

    private var categoryAdapter = CategoryAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAnalytics= Firebase.analytics
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        jobViewModel = ViewModelProvider(this).get(JobsViewModel::class.java)
        signInAnonymously()

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Dashboard")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        MobileAds.initialize(this) {}
        val testDeviceIds = listOf("5767EF128BA51CDEAC69887928180F7E")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.
        bottomNavigationView)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when ((destination as FragmentNavigator.Destination).className) {
                // show categories in dashboard fragment
                DashboardFragment::class.qualifiedName -> {
                    binding.recyclerViewCategories.visibility = View.VISIBLE
                    binding.toolbar.visibility = View.VISIBLE
                }
                // hide on other fragments
                else -> {
                    binding.recyclerViewCategories.visibility = View.GONE
                    binding.toolbar.visibility = View.GONE
                }
            }
        }

        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment,
                R.id.perfilFragment,
                R.id.definicoesFragment
            )
        )

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title =""
        checkTheme()
        setupActionBarWithNavController(navController, appBarConfig)


    }

    private fun initCategory(){
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategories.adapter = categoryAdapter
        binding.recyclerViewCategories.layoutManager = layoutManager
        binding.recyclerViewCategories.setHasFixedSize(true)
        categoryViewModel.getAllCategory()
        categoryViewModel.savedCategory.observe(this,{
            if (it != null) {
                for (category in it){
                    // Log.v("Category Main Page: ", category.name.toString())
                    categoryAdapter.addCategory(category)
                    Log.e(ContentValues.TAG, "Category : ${category.name.toString()}")
                }
            }


        })
    }

    fun signInAnonymously() {
        // [START signin_anonymously]
        auth.signInAnonymously()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInAnonymously:success")
                    initCategory()
                    jobViewModel.getAllJobs()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInAnonymously:failure", task.exception)


                }
            }

    }

    private fun checkTheme() {
        when (Preferences(this).darkMode?.toInt()) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
        }
    }
}