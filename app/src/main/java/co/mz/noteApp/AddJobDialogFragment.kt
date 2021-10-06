package co.mz.noteApp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import co.mz.noteApp.model.Job
import co.mz.noteApp.databinding.FragmentAddJobDialogBinding
import co.mz.noteApp.viewmodel.JobsViewModel
import java.util.*


class AddJobDialogFragment : DialogFragment() {

    private var _binding: FragmentAddJobDialogBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: JobsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddJobDialogBinding.inflate(inflater,container,false)
        init()

        binding.addButton.setOnClickListener{
            clearTextField()

            val name = binding.jobName.text.toString().trim()
            val company = binding.jobCompany.text.toString().trim()
           // val category = binding.menu.editText.toString()

            if(name.isEmpty()){
                binding.inputLayoutName.error = "O nome da vaga nao pode ser nulo"
                return@setOnClickListener
            }

            if(company.isEmpty()){
                binding.inputLayoutCompany.error = "O nome da empresa nao pode ser nulo"
                return@setOnClickListener
            }

            val job = Job()
            job.createdAt = Date()
            job.name = name
            job.company = company

            viewModel.addJob(job)
        }
        return  binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun clearTextField(){
        binding.inputLayoutName.error = null
        binding.inputLayoutCompany.error = null
    }


    fun init(){
        viewModel = ViewModelProvider(this).get(JobsViewModel::class.java)
        viewModel.result.observe(viewLifecycleOwner, {
            val message = if(it == null){
                getString(R.string.added_job)
            }else{
                getString(R.string.error, it.message)
            }

            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            dismiss()
        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)


    }


}