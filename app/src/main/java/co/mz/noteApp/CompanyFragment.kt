package co.mz.noteApp

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.mz.noteApp.model.Job
import co.mz.noteApp.databinding.FragmentCompanyBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView


class CompanyFragment() : Fragment() {

    private var _binding: FragmentCompanyBinding? = null
    private val binding get() = _binding!!

    lateinit var mAdView : AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val job = arguments?.getParcelable<Job>("job") as Job
        Log.e(ContentValues.TAG, "Fragment Company Details : ${job.company}.....")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompanyBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val job = arguments?.getParcelable<Job>("job") as Job
        binding.textViewCompany.text = job.company.toString()
        binding.textViewWeb.text = job.companyWeb.toString()

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}