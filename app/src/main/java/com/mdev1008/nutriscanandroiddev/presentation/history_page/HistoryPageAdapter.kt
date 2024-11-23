package com.mdev1008.nutriscanandroiddev.presentation.history_page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.domain.model.SearchHistoryItemForView
import com.mdev1008.nutriscanandroiddev.utils.getDurationTillNow
import com.mdev1008.nutriscanandroiddev.utils.getIconAndBg
import com.mdev1008.nutriscanandroiddev.utils.loadFromUrlOrGone
import com.mdev1008.nutriscanandroiddev.utils.toReadableString

class HistoryPageAdapter(private val searchHistory: List<SearchHistoryItemForView>) :
    RecyclerView.Adapter<HistoryPageAdapter.HistoryPageViewHolder>() {
    private val filteredList: List<SearchHistoryItemForView> = searchHistory

    inner class HistoryPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductImage: ImageView = itemView.findViewById(R.id.iv_hsp_product_image)
        val tvProductName: TextView = itemView.findViewById(R.id.tv_hsp_product_name)
        val tvProductBrand: TextView = itemView.findViewById(R.id.tv_hsp_brand)
        val tvProductLastScanned: TextView = itemView.findViewById(R.id.tv_hsp_last_scanned)
        val ivHealthCategory: ImageView = itemView.findViewById(R.id.iv_hsp_health_category)
        val cvProduct: CardView = itemView.findViewById(R.id.cv_hsp_item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryPageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.component_hsp_history_item, parent, false)
        return HistoryPageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: HistoryPageViewHolder, position: Int) {
        val item = filteredList[position]
        holder.apply {
            ivProductImage.loadFromUrlOrGone(imageUrl = item.imageUrl, R.mipmap.app_icon)
            tvProductName.text = item.productName
            tvProductBrand.text = item.productBrand
            tvProductLastScanned.text = item.lastScanned.getDurationTillNow().toReadableString()
            val (icon, _) = item.healthCategory.getIconAndBg(ivHealthCategory.context)
            ivHealthCategory.setImageResource(icon)
        }
    }

}