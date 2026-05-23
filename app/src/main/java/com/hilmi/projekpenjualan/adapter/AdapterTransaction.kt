package com.hilmi.projekpenjualan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.ModelProduk
import java.text.NumberFormat
import java.util.Locale

class AdapterTransaction(private var produkList: List<ModelProduk>) :
    RecyclerView.Adapter<AdapterTransaction.TransactionViewHolder>() {

    interface OnItemClickListener {
        fun onAddClick(produk: ModelProduk)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun updateData(newList: List<ModelProduk>) {
        produkList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val produk = produkList[position]
        holder.bind(produk)
    }

    override fun getItemCount(): Int = produkList.size

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivLogo: ImageView = itemView.findViewById(R.id.ivLogoProduk)
        private val tvNama: TextView = itemView.findViewById(R.id.tvItemProduk)
        private val tvHarga: TextView = itemView.findViewById(R.id.tvHargaProduk)
        private val tvKategori: TextView = itemView.findViewById(R.id.tvKategoriProduk)
        private val tvStok: TextView = itemView.findViewById(R.id.tvStokProduk)
        private val tvCabang: TextView = itemView.findViewById(R.id.tvCabangProduk)
        private val btnTambah: View = itemView.findViewById(R.id.btnTambahProduk)

        fun bind(produk: ModelProduk) {
            tvNama.text = produk.namaProduk
            
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            tvHarga.text = formatRupiah.format(produk.hargaProduk ?: 0)
            
            tvKategori.text = produk.idKategori
            tvStok.text = (produk.stokProduk ?: 0).toString()
            tvCabang.text = produk.idCabang

            // Handle Image if exists, if no Glide/Picasso, we might just use placeholder or implement custom loader
            // For now, let's keep it simple as I don't see an image loading library.
            
            btnTambah.setOnClickListener {
                listener?.onAddClick(produk)
            }
        }
    }
}
