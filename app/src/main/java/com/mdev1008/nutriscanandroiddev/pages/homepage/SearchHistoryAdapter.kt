package com.mdev1008.nutriscanandroiddev.pages.homepage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.models.data.HealthCategory
import com.mdev1008.nutriscanandroiddev.models.data.SearchHistoryItem
import com.mdev1008.nutriscanandroiddev.utils.getDurationTillNow
import com.mdev1008.nutriscanandroiddev.utils.loadFromUrlOrGone
import com.mdev1008.nutriscanandroiddev.utils.toReadableString

class SearchHistoryAdapter(
    private val viewModel: HomePageViewModel,
    private var searchHistoryItems: List<SearchHistoryItem>,
    private val onShowProgress: Unit?,
    private val onHideProgress: Unit?
) : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>() {
    inner class SearchHistoryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val ivSearchHistoryImage : ImageView = itemView.findViewById(R.id.iv_search_history_image)
        val tvSearchHistoryName : TextView = itemView.findViewById(R.id.tv_search_history_name)
        val tvSearchHistoryBrand : TextView = itemView.findViewById(R.id.tv_search_history_brand)
        val tvTimeStamp : TextView = itemView.findViewById(R.id.tv_search_history_timestamp)
        val ivSearchHistoryHealthCategory : ImageView = itemView.findViewById(R.id.iv_search_history_health_category)
        val cvListItem: CardView = itemView.findViewById(R.id.cv_list_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.component_hp_search_history_list_item, parent,false)
        return  SearchHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchHistoryItems.size
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        val item = searchHistoryItems[position]
        val (healthCategoryIcon, _) = getHealthCategoryIcon(context = holder.ivSearchHistoryHealthCategory.context, item.healthCategory)

        holder.tvSearchHistoryName.text = item.productName
        holder.tvSearchHistoryBrand.text = item.productBrand
        holder.tvTimeStamp.text = item.timeStamp.getDurationTillNow().toReadableString()
        holder.ivSearchHistoryImage.loadFromUrlOrGone(item.imageUrl)
        holder.ivSearchHistoryHealthCategory.setImageResource(healthCategoryIcon)
        holder.cvListItem.setOnClickListener{
            it.isClickable = false
            onShowProgress
            viewModel.emit(HomePageEvent.FetchProductDetails(item.productId ?: ""))
        }
    }
    private fun getHealthCategoryIcon(context : Context, healthCategory: HealthCategory) : Pair<Int, Int> {
        return when(healthCategory){
            HealthCategory.HEALTHY,
            HealthCategory.GOOD
            -> Pair(
                R.drawable.circle_good, ContextCompat.getColor(context,
                    R.color.product_category_good_bg
                ))
            HealthCategory.FAIR
            -> Pair(
                R.drawable.circle_moderate, ContextCompat.getColor(context,
                    R.color.product_category_moderate_bg
                ))
            HealthCategory.POOR,
            HealthCategory.BAD
            -> Pair(
                R.drawable.circle_bad, ContextCompat.getColor(context,
                    R.color.product_category_bad_bg
                ))
            HealthCategory.UNKNOWN -> Pair(
                R.drawable.circle_unknown, ContextCompat.getColor(context,
                    R.color.md_theme_background
                ))
        }
    }
    fun updateData(newList: List<SearchHistoryItem>){
        this.searchHistoryItems = newList
        notifyDataSetChanged()
    }

}