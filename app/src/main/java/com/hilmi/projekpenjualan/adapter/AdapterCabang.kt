package com.hilmi.projekpenjualan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.ModelCabang

class AdapterCabang(private val cabangList: List<ModelCabang>) :
    RecyclerView.Adapter<AdapterCabang.CabangViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(cabang: ModelCabang)
        fun onStatusClick(cabang: ModelCabang)
        fun onItemLongClick(cabang: ModelCabang)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CabangViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_cabang, parent, false)
        return CabangViewHolder(view)
    }

    override fun onBindViewHolder(holder: CabangViewHolder, position: Int) {
        val cabang = cabangList[position]
        holder.bind(cabang)
    }

    override fun getItemCount(): Int = cabangList.size

    inner class CabangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNama: TextView = itemView.findViewById(R.id.tvItemKategori) // Menggunakan ID yang sama dari layout
        private val chipStatus: Chip = itemView.findViewById(R.id.chipAktif)

        fun bind(cabang: ModelCabang) {
            tvNama.text = cabang.namaCabang
            chipStatus.text = cabang.statusCabang

            if (cabang.statusCabang == "Aktif") {
                chipStatus.setChipIconResource(R.drawable.lingkaran_online)
            } else {
                chipStatus.setChipIconResource(R.drawable.lingkaran_offline)
            }

            itemView.setOnClickListener {
                listener?.onItemClick(cabang)
            }

            chipStatus.setOnClickListener {
                listener?.onStatusClick(cabang)
            }

            itemView.setOnLongClickListener {
                listener?.onItemLongClick(cabang)
                true
            }
        }
    }
}
