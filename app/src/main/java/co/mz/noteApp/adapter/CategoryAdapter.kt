package co.mz.noteApp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.mz.noteApp.model.Category
import co.mz.noteApp.databinding.RecyclerViewCategoryBinding


class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){

    private var categories = mutableListOf<Category>()

    inner class ViewHolder(val binding: RecyclerViewCategoryBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        return ViewHolder(RecyclerViewCategoryBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.categoryButton.text = categories[position].name
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun addCategory(category: Category){
        if(!categories.contains(category)){
            categories.add(category)
          //  Log.v("categ: ", category.name.toString() +" added...!")
        }
        notifyDataSetChanged()
    }
}