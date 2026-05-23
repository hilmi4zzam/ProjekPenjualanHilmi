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

class AdapterTransaksi(private var produkList: List<ModelProduk>) :
    RecyclerView.Adapter<AdapterTransaksi.TransactionViewHolder>() {

    private val quantities = mutableMapOf<String, Int>()

    interface OnQuantityChangeListener {
        fun onQuantityChanged(totalPrice: Int)
    }

    private var listener: OnQuantityChangeListener? = null

    fun setOnQuantityChangeListener(listener: OnQuantityChangeListener) {
        this.listener = listener
    }

    fun updateData(newList: List<ModelProduk>) {
        produkList = newList
        notifyDataSetChanged()
    }

    fun resetQuantities() {
        quantities.clear()
        notifyDataSetChanged()
        listener?.onQuantityChanged(0)
    }

    fun getTotalPrice(): Int {
        var total = 0
        for (produk in produkList) {
            val qty = quantities[produk.idProduk ?: ""] ?: 0
            total += (produk.hargaProduk ?: 0) * qty
        }
        return total
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
        private val btnKurang: View = itemView.findViewById(R.id.btnKurangProduk)
        private val tvJumlah: TextView = itemView.findViewById(R.id.tvJumlahProduk)

        fun bind(produk: ModelProduk) {
            tvNama.text = produk.namaProduk
            
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            tvHarga.text = formatRupiah.format(produk.hargaProduk ?: 0)
            
            tvKategori.text = produk.idKategori
            tvStok.text = (produk.stokProduk ?: 0).toString()
            tvCabang.text = produk.idCabang

            val id = produk.idProduk ?: ""
            val currentQty = quantities[id] ?: 0
            tvJumlah.text = currentQty.toString()

            btnTambah.setOnClickListener {
                val newQty = (quantities[id] ?: 0) + 1
                quantities[id] = newQty
                tvJumlah.text = newQty.toString()
                listener?.onQuantityChanged(getTotalPrice())
            }

            btnKurang.setOnClickListener {
                val current = quantities[id] ?: 0
                if (current > 0) {
                    val newQty = current - 1
                    quantities[id] = newQty
                    tvJumlah.text = newQty.toString()
                    listener?.onQuantityChanged(getTotalPrice())
                }
            }
        }
    }
}
