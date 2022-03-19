package fastcampus.aop.part3.chapter18

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part3.chapter18.databinding.ViewholderSearchResultItemBinding
import fastcampus.aop.part3.chapter18.model.SearchResultEntity

class SearchRecyclerAdapter:RecyclerView.Adapter<SearchRecyclerAdapter.SearchResultItemViewHolder>() {

    private var searchResultList: List<SearchResultEntity> = listOf()
    private lateinit var searchResultClickListener: (SearchResultEntity) -> Unit

    inner class SearchResultItemViewHolder(private val binding: ViewholderSearchResultItemBinding, val searchResultClickListener:(SearchResultEntity) -> Unit) : RecyclerView.ViewHolder(binding.root){

        fun bind(data: SearchResultEntity) = with(binding){
            textTextView.text = data.name
            subTextTextView.text = data.fullAdress
        }

        fun bindViews(data:SearchResultEntity) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemViewHolder {
        val view = ViewholderSearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultItemViewHolder(view, searchResultClickListener)
    }

    override fun onBindViewHolder(holder: SearchResultItemViewHolder, position: Int) {
        holder.bind(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    override fun getItemCount(): Int = searchResultList.size

    fun setSearchResultList(searchResultList:List<SearchResultEntity>, searchResultClickListener: (SearchResultEntity) -> Unit){
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged()
    }
}