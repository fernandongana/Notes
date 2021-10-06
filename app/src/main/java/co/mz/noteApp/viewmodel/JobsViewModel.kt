package co.mz.noteApp.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.mz.noteApp.model.Job
import co.mz.noteApp.util.NODE_JOBS
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestoreSettings
import java.lang.Exception


class JobsViewModel:ViewModel() {

    private val store = FirebaseFirestore.getInstance()
    private val jobCollection = store.collection(NODE_JOBS).orderBy("createdAt", Query.Direction.DESCENDING)

    private val settings = firestoreSettings {
        isPersistenceEnabled = true
    }

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result

  //  private val _job = MutableLiveData<Job>()
  //  val job: LiveData<Job?> get() = _job
    var savedJobs : MutableLiveData<List<Job>> = MutableLiveData()

    fun addJob(job: Job){
        store.firestoreSettings = settings
        store.collection(NODE_JOBS).add(job).addOnSuccessListener {
            if(it.get().isSuccessful){
                _result.value = null
            }else{
                _result.value = it.get().exception
            }
        }
    }


    fun getAllJobs(): LiveData<List<Job>>{
        jobCollection.addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }
               // val jobs = value?.documentChanges
                var jobListTemp : MutableList<Job> = mutableListOf()
                for (j in value!!) {
                    var job = j.toObject(Job::class.java)
                    jobListTemp.add(job)
                 //   Log.e(TAG, "Job: ${job}")
                }
                savedJobs.value = jobListTemp

        })
         return savedJobs
    }



    override fun onCleared() {
        super.onCleared()
       // snapShotListener.remove()
    }
}