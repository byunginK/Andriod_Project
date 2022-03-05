package fastcampus.aop.part2.chapter12.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fastcampus.aop.part2.chapter12.databinding.ItemBookBinding
import fastcampus.aop.part2.chapter12.databinding.ItemHistoryBinding
import fastcampus.aop.part2.chapter12.model.Book
import fastcampus.aop.part2.chapter12.model.History

class HistoryAdapter(val historyDeleteClickedListener: (String) -> Unit) : ListAdapter<History, HistoryAdapter.HistoryItemViewHolder>(diffUtil) {

    //파라미터인 binding: ItemBookBinding은 layout의 item_book과 매핑된다.
    //명칭은 java에서는 카멜케이스, layout은 _ 로 구분되어 매핑된다.
    //이 맵핑을 위해서는 build.gradle(app 단위)에서 viewBinding의 enalbed를 추가해줘야한다.
    inner class HistoryItemViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(historyModel: History) {
            binding.historyKeywordTextView.text = historyModel.keyWord
            binding.historyKeywordDeleteButton.setOnClickListener {
                historyDeleteClickedListener(historyModel.keyWord.orEmpty())
            }
        }
    }

    //뷰를 처음 만들때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        //처음이기 때문에 BookItemViewHolder을 생성해줘야한다.
        //LayoutInflater의 context는 viewGroup의 속성에서 가져온다.
        return HistoryItemViewHolder(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    //뷰에 그려질때 데이터를 바인드하는 함수
    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        //리스트는 미리 저장이 되어있어서 가져와서 사용
        holder.bind(currentList[position])
    }

    //리사이클뷰가 데이버를 다시 바인딩(가져올지)를 결정하는 판단의 기준
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem.keyWord == newItem.keyWord
            }

        }
    }
}