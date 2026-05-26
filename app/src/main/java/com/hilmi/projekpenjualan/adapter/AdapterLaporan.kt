package com.hilmi.projekpenjualan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.firebase.database.FirebaseDatabase
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.ModelLaporan
import java.text.NumberFormat
import java.util.Locale

class AdapterLaporan(private var laporanList: List<ModelLaporan>) :
    RecyclerView.Adapter<AdapterLaporan.LaporanViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(laporan: ModelLaporan)
        fun onItemLongClick(laporan: ModelLaporan)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun updateData(newList: List<ModelLaporan>) {
        laporanList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_laporan, parent, false)
        return LaporanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        holder.bind(laporanList[position])
    }

    override fun getItemCount(): Int = laporanList.size

    inner class LaporanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNama: TextView = itemView.findViewById(R.id.tvItemProduk)
        private val tvHarga: TextView = itemView.findViewById(R.id.tvHargaProduk)
        private val chipStatus: Chip = itemView.findViewById(R.id.chipAktif)

        fun bind(laporan: ModelLaporan) {
            tvNama.text = laporan.namaPemesan ?: "-"
            
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            tvHarga.text = formatRupiah.format(laporan.totalHarga ?: 0)

            val currentStatus = laporan.status ?: "Dikerjakan"
            updateChipUI(currentStatus)

            chipStatus.setOnClickListener {
                val newStatus = if (currentStatus == "Dikerjakan") "Selesai" else "Dikerjakan"
                updateStatusInFirebase(laporan.idLaporan, newStatus)
            }

            itemView.setOnClickListener {
                listener?.onItemClick(laporan)
            }

            itemView.setOnLongClickListener {
                listener?.onItemLongClick(laporan)
                true
            }
        }

        private fun updateChipUI(status: String) {
            chipStatus.text = status
            if (status == "Selesai") {
                chipStatus.setChipIconResource(R.drawable.lingkaran_online)
            } else {
                chipStatus.text = "Dikerjakan"
                chipStatus.setChipIconResource(R.drawable.lingkaran_dikerjakan)
            }
        }

        private fun updateStatusInFirebase(idLaporan: String?, newStatus: String) {
            if (idLaporan == null) return
            val ref = FirebaseDatabase.getInstance().getReference("laporan").child(idLaporan)
            ref.child("status").setValue(newStatus)
        }
    }
}
