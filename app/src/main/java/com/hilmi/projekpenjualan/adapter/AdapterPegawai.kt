package com.hilmi.projekpenjualan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.ModelPegawai
import com.google.android.material.chip.Chip

class AdapterPegawai(private val pegawaiList: List<ModelPegawai>) :
    RecyclerView.Adapter<AdapterPegawai.PegawaiViewHolder>() {

    lateinit var appContext: Context

    interface OnItemClickListener {
        fun onItemClick(pegawai: ModelPegawai)
        fun onStatusClick(pegawai: ModelPegawai)
        fun onItemLongClick(pegawai: ModelPegawai)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PegawaiViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_pegawai, parent, false)
        appContext = parent.context
        return PegawaiViewHolder(view)
    }

    override fun onBindViewHolder(holder: PegawaiViewHolder, position: Int) {
        val pegawai = pegawaiList[position]
        holder.bind(pegawai)
    }

    override fun getItemCount(): Int = pegawaiList.size

    inner class PegawaiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaPegawai: TextView = itemView.findViewById(R.id.tvItemPegawai)
        val chipStatus: Chip = itemView.findViewById(R.id.chipAktif)

        fun bind(pegawai: ModelPegawai) {
            tvNamaPegawai.text = pegawai.namaPegawai
            chipStatus.text = pegawai.statusPegawai

            // Mengatur icon berdasarkan status
            if (pegawai.statusPegawai == "Aktif") {
                chipStatus.setChipIconResource(R.drawable.lingkaran_online)
            } else {
                chipStatus.setChipIconResource(R.drawable.lingkaran_offline)
            }

            itemView.setOnClickListener {
                listener?.onItemClick(pegawai)
            }

            itemView.setOnLongClickListener {
                listener?.onItemLongClick(pegawai)
                true
            }

            chipStatus.setOnClickListener {
                listener?.onStatusClick(pegawai)
            }
        }
    }
}