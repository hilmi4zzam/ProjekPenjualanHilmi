package com.hilmi.projekpenjualan.laporan

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.CartItem
import com.hilmi.projekpenjualan.model.ModelLaporan
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

class DetailLaporan : AppCompatActivity() {

    private lateinit var llItemSummary: LinearLayout
    private lateinit var tvTotalHarga: TextView
    private lateinit var tvNamaPemesan: TextView
    private lateinit var tvNamaKasir: TextView
    private lateinit var tvDibayar: TextView
    private lateinit var tvKembalian: TextView
    private lateinit var btnCetak: MaterialButton
    private lateinit var btnBagikan: MaterialButton
    private lateinit var btnSelesai: MaterialButton
    private var selectedLaporan: ModelLaporan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nota)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.tvPembayaranBerhasil).text = "Detail Transaksi"

        llItemSummary = findViewById(R.id.llItemSummary)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        tvNamaPemesan = findViewById(R.id.tvNama)
        tvNamaKasir = findViewById(R.id.tvNamaKasir)
        tvDibayar = findViewById(R.id.tvDibayar)
        tvKembalian = findViewById(R.id.tvKembalian)
        btnCetak = findViewById(R.id.btnCetak)
        btnBagikan = findViewById(R.id.btnBagikan)
        btnSelesai = findViewById(R.id.btnSelesai)

        selectedLaporan = intent.getParcelableExtra("LAPORAN")
        
        if (selectedLaporan != null) {
            tvNamaPemesan.text = selectedLaporan?.namaPemesan
            tvNamaKasir.text = selectedLaporan?.namaKasir ?: "-"
            displayTotalPrice(selectedLaporan?.totalHarga ?: 0)
            displayPayment(selectedLaporan?.dibayar ?: 0L, selectedLaporan?.kembalian ?: 0L)
            displayItems(selectedLaporan?.items as? ArrayList<CartItem>)
        }

        btnSelesai.text = "Kembali"
        btnSelesai.setOnClickListener {
            finish()
        }

        btnCetak.setOnClickListener {
            cetakNota()
        }

        btnBagikan.setOnClickListener {
            showShareDialog()
        }
    }

    private fun generateReceiptText(): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        
        val sb = StringBuilder()
        sb.append("==============================\n")
        sb.append("        Belum Kepikiran        \n")
        sb.append("==============================\n")
        sb.append("Kasir: ${selectedLaporan?.namaKasir ?: "-"}\n")
        sb.append("Nama: ${selectedLaporan?.namaPemesan ?: "-"}\n")
        sb.append("------------------------------\n")
        
        selectedLaporan?.items?.forEach { item ->
            val pricePerItem = item.hargaProduk ?: 0
            val totalItemPrice = pricePerItem * (item.jumlah ?: 0)
            sb.append("${item.namaProduk}\n")
            sb.append("${item.jumlah} x ${formatRupiah.format(pricePerItem)} = ${formatRupiah.format(totalItemPrice)}\n")
        }
        
        sb.append("------------------------------\n")
        sb.append("Dibayar: ${formatRupiah.format(selectedLaporan?.dibayar ?: 0L)}\n")
        sb.append("Kembalian: ${formatRupiah.format(selectedLaporan?.kembalian ?: 0L)}\n")
        sb.append("TOtal: ${formatRupiah.format(selectedLaporan?.totalHarga ?: 0)}\n")
        sb.append("==============================\n")
        sb.append("Terima kasih atas kunjungannya!\n")
        
        return sb.toString()
    }

    private fun showShareDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_input_nomor, null)
        val etNomor = dialogView.findViewById<TextInputEditText>(R.id.etNomorWA)

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Bagikan ke WhatsApp")
        builder.setView(dialogView)
        builder.setPositiveButton("Kirim") { dialog, _ ->
            val nomor = etNomor.text.toString().trim()
            if (nomor.isNotEmpty()) {
                shareToWhatsApp(nomor)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Nomor tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun shareToWhatsApp(nomor: String) {
        var formattedNomor = nomor
        if (formattedNomor.startsWith("0")) {
            formattedNomor = "62" + formattedNomor.substring(1)
        } else if (!formattedNomor.startsWith("62")) {
            formattedNomor = "62" + formattedNomor
        }

        val receiptText = generateReceiptText()
        
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, receiptText)
        intent.putExtra("jid", "$formattedNomor@s.whatsapp.net")
        intent.`package` = "com.whatsapp"

        try {
            startActivity(intent)
        } catch (e: Exception) {
            try {
                val url = "https://api.whatsapp.com/send?phone=$formattedNomor&text=${java.net.URLEncoder.encode(receiptText, "UTF-8")}"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = android.net.Uri.parse(url)
                startActivity(i)
            } catch (ex: Exception) {
                Toast.makeText(this, "WhatsApp tidak terinstal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cetakNota() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 101)
                return
            }
        }
        mulaiCetakBluetooth()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mulaiCetakBluetooth()
            } else {
                Toast.makeText(this, "Izin Bluetooth diperlukan untuk mencetak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mulaiCetakBluetooth() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val bluetoothAdapter = bluetoothManager?.adapter

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Tidak ada printer terdeteksi", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && 
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val pairedDevices = bluetoothAdapter.bondedDevices
        if (pairedDevices.isNullOrEmpty()) {
            Toast.makeText(this, "Tidak ada printer terdeteksi", Toast.LENGTH_SHORT).show()
            return
        }

        val device = pairedDevices.firstOrNull {
            it.bluetoothClass?.majorDeviceClass == 1536 || 
            it.bluetoothClass?.majorDeviceClass == 7936
        } ?: pairedDevices.first()

        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        Thread {
            try {
                val socket = device.createRfcommSocketToServiceRecord(uuid)
                socket.connect()
                val outputStream = socket.outputStream
                
                outputStream.write(byteArrayOf(0x1B, 0x40))
                
                val text = generateReceiptText()
                outputStream.write(text.toByteArray())
                
                outputStream.write("\n\n\n".toByteArray())
                outputStream.flush()
                socket.close()
                
                runOnUiThread {
                    Toast.makeText(this@DetailLaporan, "Berhasil mencetak nota", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@DetailLaporan, "Gagal terhubung ke printer", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun displayItems(items: ArrayList<CartItem>?) {
        llItemSummary.removeAllViews()
        if (items.isNullOrEmpty()) return

        val inflater = LayoutInflater.from(this)
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        for (item in items) {
            val itemView = inflater.inflate(R.layout.layout_item_summary, llItemSummary, false)
            val tvItemName = itemView.findViewById<TextView>(R.id.tvItemName)
            val tvItemPrice = itemView.findViewById<TextView>(R.id.tvItemPrice)

            tvItemName.text = "${item.namaProduk} - ${item.jumlah}x"
            val pricePerItem = (item.hargaProduk ?: 0) * (item.jumlah ?: 0)
            tvItemPrice.text = formatRupiah.format(pricePerItem)

            llItemSummary.addView(itemView)
        }
    }

    private fun displayTotalPrice(total: Int) {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        tvTotalHarga.text = formatRupiah.format(total)
    }

    private fun displayPayment(dibayar: Long, kembalian: Long) {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        tvDibayar.text = formatRupiah.format(dibayar)
        tvKembalian.text = formatRupiah.format(kembalian)
    }
}
