package com.hilmi.projekpenjualan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.ModelProduk
import java.text.NumberFormat
import java.util.Locale

class AdapterProduk(private val produkList: List<ModelProduk>) :
    RecyclerView.Adapter<AdapterProduk.ProdukViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(produk: ModelProduk)
        fun onItemLongClick(produk: ModelProduk)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_produk, parent, false)
        return ProdukViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        val produk = produkList[position]
        holder.bind(produk)
    }

    override fun getItemCount(): Int = produkList.size

    inner class ProdukViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNama: TextView = itemView.findViewById(R.id.tvItemProduk)
        private val tvHarga: TextView = itemView.findViewById(R.id.tvHargaProduk)
        private val tvKategori: TextView = itemView.findViewById(R.id.tvKategoriProduk)
        private val tvStok: TextView = itemView.findViewById(R.id.tvStokProduk)
        private val tvCabang: TextView = itemView.findViewById(R.id.tvCabangProduk)

        fun bind(produk: ModelProduk) {
            tvNama.text = produk.namaProduk
            
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            tvHarga.text = formatRupiah.format(produk.hargaProduk ?: 0)
            
            tvKategori.text = produk.idKategori
            tvStok.text = (produk.stokProduk ?: 0).toString()
            tvCabang.text = produk.idCabang

            itemView.setOnClickListener {
                listener?.onItemClick(produk)
            }

            itemView.setOnLongClickListener {
                listener?.onItemLongClick(produk)
                true
            }
        }
    }
}