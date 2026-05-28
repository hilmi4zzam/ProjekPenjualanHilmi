package com.hilmi.projekpenjualan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.ModelKategori

class AdapterCategoryFilter(private var listKategori: List<ModelKategori>) :
    RecyclerView.Adapter<AdapterCategoryFilter.ViewHolder>() {

    private var selectedPosition = 0 // "Semua" is at position 0
    private var listener: OnCategoryClickListener? = null

    interface OnCategoryClickListener {
        fun onCategoryClick(categoryName: String?)
    }

    fun setOnCategoryClickListener(listener: OnCategoryClickListener) {
        this.listener = listener
    }

    fun updateData(newList: List<ModelKategori>) {
        // We add "Semua" manually at the beginning
        val fullList = ArrayList<ModelKategori>()
        fullList.add(ModelKategori(idKategori = "semua", namaKategori = "Semua", statusKategori = "Aktif"))
        fullList.addAll(newList)
        listKategori = fullList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_category_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kategori = listKategori[position]
        holder.tvName.text = kategori.namaKategori

        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFB200"))
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#B06710"))
        }

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            
            val filterName = if (kategori.namaKategori == "Semua") null else kategori.namaKategori
            listener?.onCategoryClick(filterName)
        }
    }

    override fun getItemCount(): Int = listKategori.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cvCategory)
        val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)
    }
}
