package co.mz.noteApp.adapter


import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.mz.noteApp.data.Job
import co.mz.noteApp.databinding.RecyclerViewJobBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import kotlin.coroutines.coroutineContext

class JobAdapter(var jobList: MutableList<Job>,
                 private val listener: (Job) -> Unit):RecyclerView.Adapter<JobAdapter.ViewHolder>(), Filterable {

    private var jobs = mutableListOf<Job>()

    inner class ViewHolder(val binding: RecyclerViewJobBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerViewJobBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.textViewName.text = jobs[position].name
            holder.binding.textViewCompany.text = jobs[position].company
            holder.binding.textViewLocation.text = jobs[position].location
            if(jobs[position].company?.length ?: jobs[position].company != -1){
                holder.binding.roundIcon.text = jobs[position].company.toString().substring(0, 1)
            }

            val timestamp = jobs[position].createdAt as Date
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)
            val createdDate = Date(timestamp.time)
            holder.binding.textViewDate.text = sdf.format(createdDate).toString()

            val timestampExpiry = jobs[position].expiry as Date
            val netDateExpiry = Date(timestampExpiry.time)

            val now = Date()
            val nowExpiry = Date(now.time)
            val diff = netDateExpiry.time - nowExpiry.time

            if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt() > 0) {
                holder.binding.textViewExpiry.text = "Expira em " +
                        TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toString() + " dia(s)"
            } else if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt() == 0) {
                holder.binding.textViewExpiry.text = "Expira hoje"
            } else {
                holder.binding.textViewExpiry.setTextColor(Color.parseColor("#bdbdbd"))
                holder.binding.textViewExpiry.text = "Expirado"
            }
            holder.binding.textViewFavorite.setOnClickListener {
                Log.v("Favourite", jobs[position].toString() +" clicked")
            }
            holder.itemView.setOnClickListener {
                listener(jobs[position])
            }
        
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                jobs = if(charSearch == null || charSearch.isEmpty()){
                    jobList
                }else{
                    val resultList = mutableListOf<Job>()
                    resultList.clear()
                    for(row in jobs){
                        if(row.name?.toLowerCase()?.contains(constraint.toString().toLowerCase()) == true){
                            resultList.add(row)
                        }
                    }
                    resultList
                }

                val filterResults = FilterResults()
                filterResults.values = jobs
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
               // jobs.clear()
                jobs = results?.values as MutableList<Job>
                notifyDataSetChanged()
            }

        }
    }


    fun addJobs(job:Job){
        if(!jobs.contains(job)){
            jobs.add(job)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
      //  return  jobs.size + (jobs.size % perAds)
        return jobs.size
    }
}
