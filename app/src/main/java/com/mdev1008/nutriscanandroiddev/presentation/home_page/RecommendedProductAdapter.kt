package com.mdev1008.nutriscanandroiddev.presentation.home_page

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.domain.model.RecommendedProductForView
import com.mdev1008.nutriscanandroiddev.utils.loadFromUrlOrGone

class RecommendedProductAdapter(
    private var recommendedProducts: List<RecommendedProductForView>,
    private val callback: (productId: String) -> Unit
): RecyclerView.Adapter<RecommendedProductAdapter.RecommendedProductViewHolder>() {

    inner class RecommendedProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ivProductImage: ImageView = itemView.findViewById(R.id.iv_hp_recommended_image)
        val tvProductName: TextView = itemView.findViewById(R.id.tv_hp_recommended_name)
        val tvProductBrand: TextView = itemView.findViewById(R.id.tv_hp_recommended_brand)
        val ivProductHealthCategory: ImageView = itemView.findViewById(R.id.iv_hp_recommended_health_category)
        val cvRecommendedItem: CardView = itemView.findViewById(R.id.cv_recommended_list_item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendedProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.component_hmp_recommended_item, parent, false)
        return RecommendedProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recommendedProducts.size
    }

    override fun onBindViewHolder(holder: RecommendedProductViewHolder, position: Int) {
        val item = recommendedProducts[position]
        holder.apply {
            ivProductImage.loadFromUrlOrGone(item.imageUrl, R.mipmap.app_icon)
            tvProductName.text = item.productName
            tvProductBrand.text = item.brand
            ivProductHealthCategory.setImageResource(R.drawable.circle_good)
            cvRecommendedItem.setOnClickListener {
                callback(item.productId)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<RecommendedProductForView>){
        this.recommendedProducts = newList
        notifyDataSetChanged()
    }

}