package co.mz.noteApp

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.mz.noteApp.data.Job
import co.mz.noteApp.databinding.FragmentJobDetailBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.text.SimpleDateFormat
import java.util.*
import android.widget.AbsListView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat


class JobDetailFragment : Fragment() {

    private var _binding: FragmentJobDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val job = arguments?.getParcelable<Job>("job") as Job
        Log.e(ContentValues.TAG, "Fragment Job Details : ${job.name}.....")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJobDetailBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val job = arguments?.getParcelable<Job>("job") as Job
        val layout: LinearLayout = binding.descriptionsLayout

        for (text in job.description!!){
            // Create TextView programmatically.
            val textView = TextView(context)
            textView.text = text
            textView.setCompoundDrawablesWithIntrinsicBounds(context?.let { ContextCompat.getDrawable(it,R.drawable.ic_done) }, null,
                null, null)
            textView.compoundDrawablePadding = 10
            textView.textSize = 16.0F
            textView.setPadding(0, 5, 0, 5)
            val lp = AbsListView.LayoutParams(
                AbsListView.LayoutParams.WRAP_CONTENT,
                AbsListView.LayoutParams.WRAP_CONTENT
            )
            textView.layoutParams = lp
            layout.addView(textView)
        }

        binding.textViewLocation.text = job.location.toString()
        binding.textViewName.text = job.name.toString()
        val timestamp = job.createdAt as Date
       // val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val netDate = Date(timestamp.time)
        binding.textViewCreatedAt.text = sdf.format(netDate).toString()

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}