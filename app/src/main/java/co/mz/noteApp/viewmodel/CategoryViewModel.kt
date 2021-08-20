package co.mz.noteApp.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.mz.noteApp.data.Category
import co.mz.noteApp.data.NODE_CATEGORY
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CategoryViewModel: ViewModel()  {

    private val store = FirebaseFirestore.getInstance()
    private val categoyCollection = store.collection(NODE_CATEGORY).orderBy("name", Query.Direction.ASCENDING)


    var savedCategory : MutableLiveData<List<Category>> = MutableLiveData()

    fun getAllCategory(): LiveData<List<Category>> {
        categoyCollection.addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@EventListener
            }
           // Log.w(ContentValues.TAG, "get All Category Loaded......." )
            val categoryListTemp : MutableList<Category> = mutableListOf()
            for (j in value!!) {
                var categ = j.toObject(Category::class.java)
                categoryListTemp.add(categ)

            }
            savedCategory.value = categoryListTemp

        })
        return savedCategory
    }
}