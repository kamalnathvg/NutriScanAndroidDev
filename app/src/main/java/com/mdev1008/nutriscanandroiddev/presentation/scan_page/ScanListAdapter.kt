package com.mdev1008.nutriscanandroiddev.presentation.scan_page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.domain.model.ScanItemForView
import com.mdev1008.nutriscanandroiddev.utils.loadFromUrlOrGone

class ScanListAdapter(private var scanList: List<ScanItemForView>,private val callback: (productId: String) -> Unit) :
    RecyclerView.Adapter<ScanListAdapter.ScanListViewHolder>() {
    inner class ScanListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductImage: ImageView = itemView.findViewById(R.id.iv_scan_item_image)
        val tvProductName: TextView = itemView.findViewById(R.id.tv_scan_item_name)
        val tvProductBrand: TextView = itemView.findViewById(R.id.tv_scan_item_brand)
        val ivPalmOilStatus: ImageView = itemView.findViewById(R.id.iv_scan_item_palm_oil_status)
        val ivVeganStatus: ImageView = itemView.findViewById(R.id.iv_scan_item_vegan_status)
        val ivVegetarianStatus: ImageView = itemView.findViewById(R.id.iv_scan_item_vegetarian_status)
        val tvAllergenCount: TextView = itemView.findViewById(R.id.tv_scan_item_allergen_count)
        val tvAdditivesCount: TextView = itemView.findViewById(R.id.tv_scan_item_additives_count)
        val cvScanItem: CardView = itemView.findViewById(R.id.cv_scan_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.component_scan_item, parent, false)
        return ScanListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return scanList.size
    }

    override fun onBindViewHolder(holder: ScanListViewHolder, position: Int) {
        val item = scanList[position]
        holder.apply {
            ivProductImage.loadFromUrlOrGone(item.imageUrl, R.mipmap.app_icon)
            //TODO: Fix Dietary Restriction Icons
            ivVeganStatus.setImageResource(R.mipmap.additives)
            ivVegetarianStatus.setImageResource(R.mipmap.additives)
            ivPalmOilStatus.setImageResource(R.mipmap.additives)
            tvProductName.text = item.productName
            tvProductBrand.text = item.productBrand
            tvAllergenCount.text = item.allergenCount.toString()
            tvAdditivesCount.text = item.additivesCount.toString()
            cvScanItem.setOnClickListener {
                callback(item.productId)
            }

        }
    }

    fun addItem(item: ScanItemForView) {
        this.scanList.toMutableList().add(item)
        notifyItemInserted(this.scanList.size)
    }
    fun updateList(newList: List<ScanItemForView>){
        this.scanList = newList
        notifyDataSetChanged()
    }
}