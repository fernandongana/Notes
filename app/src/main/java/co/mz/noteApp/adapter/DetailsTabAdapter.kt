package co.mz.noteApp.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import co.mz.noteApp.CompanyFragment
import co.mz.noteApp.JobDetailFragment
import co.mz.noteApp.data.Job
import android.os.Bundle




internal class DetailsTabAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int,
    val job: Job
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val args = Bundle()
        args.putParcelable("job", job)
        val jobFragment = JobDetailFragment()
        val companyFragment = CompanyFragment()
        jobFragment.arguments = args
        companyFragment.arguments = args
        return when (position) {
            0 -> {
                jobFragment
            }
            1 -> {
                companyFragment
            }
            else -> getItem(position)
        }
    }


    override fun getCount(): Int {
        return totalTabs
    }
}