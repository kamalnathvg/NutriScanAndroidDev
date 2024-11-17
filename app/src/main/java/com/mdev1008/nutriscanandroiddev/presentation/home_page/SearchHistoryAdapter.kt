package com.mdev1008.nutriscanandroiddev.presentation.home_page

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
import com.mdev1008.nutriscanandroiddev.data.model.HealthCategory
import com.mdev1008.nutriscanandroiddev.domain.model.SearchHistoryItemForView
import com.mdev1008.nutriscanandroiddev.utils.getDurationTillNow
import com.mdev1008.nutriscanandroiddev.utils.loadFromUrlOrGone
import com.mdev1008.nutriscanandroiddev.utils.toReadableString

class SearchHistoryAdapter(
    private var searchHistoryItems: List<SearchHistoryItemForView>,
    private val callback: (productId: String) -> Unit
) : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>() {
    inner class SearchHistoryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val ivSearchHistoryImage : ImageView = itemView.findViewById(R.id.iv_hp_recommended_image)
        val tvSearchHistoryName : TextView = itemView.findViewById(R.id.tv_hp_recommended_name)
        val tvSearchHistoryBrand : TextView = itemView.findViewById(R.id.tv_hp_recommended_brand)
        val tvTimeStamp : TextView = itemView.findViewById(R.id.tv_hp_search_history_timestamp)
        val ivSearchHistoryHealthCategory : ImageView = itemView.findViewById(R.id.iv_hp_recommended_health_category)
        val cvListItem: CardView = itemView.findViewById(R.id.cv_list_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.component_hp_recommended_item, parent,false)
        return  SearchHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchHistoryItems.size
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        val item = searchHistoryItems[position]
        val (healthCategoryIcon, _) = getHealthCategoryIcon(context = holder.ivSearchHistoryHealthCategory.context, item.healthCategory)
        holder.apply {
            tvSearchHistoryName.text = item.productName
            tvSearchHistoryBrand.text = item.productBrand
            tvTimeStamp.text = item.lastScanned.getDurationTillNow().toReadableString()
            ivSearchHistoryImage.loadFromUrlOrGone(item.imageUrl)
            ivSearchHistoryHealthCategory.setImageResource(healthCategoryIcon)
            cvListItem.setOnClickListener {
                callback(item.productId)
            }
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
    fun updateList(newList: List<SearchHistoryItemForView>){
        this.searchHistoryItems = newList
        notifyDataSetChanged()
    }

}