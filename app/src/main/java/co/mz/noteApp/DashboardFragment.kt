package co.mz.noteApp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import co.mz.noteApp.adapter.JobAdapter
import co.mz.noteApp.data.Job
import co.mz.noteApp.databinding.FragmentDashboardBinding
import co.mz.noteApp.viewmodel.CategoryViewModel
import co.mz.noteApp.viewmodel.JobsViewModel


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    val jobList = mutableListOf<Job>()


    private val adapter = JobAdapter(jobList){job ->
        val intent = Intent(requireActivity(), DetailsActivity::class.java)
        intent.putExtra("job", job)
        requireActivity().startActivity(intent)
    }

    private lateinit var viewModel: JobsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = activity?.run {
            ViewModelProvider(this).get(JobsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_item, menu)
        val search = menu.findItem(R.id.toolbar)
        val searchView : SearchView = search?.actionView as SearchView
        searchView.isIconifiedByDefault = false
        searchView.queryHint = "Pesquisar"

        val searchFrameId =
            searchView.context.resources.getIdentifier("android:id/search_edit_frame", null, null)
        val searchFrame: View = searchView.findViewById(searchFrameId)
        searchFrame.setBackgroundResource(R.drawable.bg_rounded)

        val searchPlateId =
            searchView.context.resources.getIdentifier("android:id/search_plate", null, null)
        val searchPlate: View = searchView.findViewById(searchPlateId)
        searchPlate.setBackgroundResource(R.drawable.bg_rounded)

        val searchBarId =
            searchView.context.resources.getIdentifier("android:id/search_bar", null, null)
        val searchBar: View = searchView.findViewById(searchBarId)
        searchBar.setBackgroundResource(R.drawable.bg_rounded)


        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()


        binding.recyclerViewJobs.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.v("scroll", "onScrollStateChanged newState $newState")
            }
        })

        binding.addButton.setOnClickListener{
            AddJobDialogFragment().show(childFragmentManager, "")
        }
    }



    private fun init(){
        binding.recyclerViewJobs.adapter = adapter
        binding.recyclerViewJobs.setHasFixedSize(true)
      //  viewModel.getAllJobs()
        //job LiveData from ModelView
        viewModel.savedJobs.observe(viewLifecycleOwner, {

            if (it != null) {
                jobList.clear()
                binding.loading.visibility = View.GONE
                for(jobSaved in it){
                    adapter.addJobs(jobSaved)
                    jobList.add(jobSaved)
                   // Toast.makeText(requireContext(), jobSaved.toString(), Toast.LENGTH_SHORT).show()
                }
            }else{
                binding.loading.visibility = View.VISIBLE
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}