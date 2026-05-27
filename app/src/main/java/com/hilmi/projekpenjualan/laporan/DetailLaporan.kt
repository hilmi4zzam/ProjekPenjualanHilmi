package com.hilmi.projekpenjualan.laporan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.CartItem
import com.hilmi.projekpenjualan.model.ModelLaporan
import java.text.NumberFormat
import java.util.Locale

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
        sb.append("        HILMI STORE        \n")
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
        sb.append("DIBAYAR: ${formatRupiah.format(selectedLaporan?.dibayar ?: 0L)}\n")
        sb.append("KEMBALIAN: ${formatRupiah.format(selectedLaporan?.kembalian ?: 0L)}\n")
        sb.append("TOTAL: ${formatRupiah.format(selectedLaporan?.totalHarga ?: 0)}\n")
        sb.append("==============================\n")
        sb.append("Terima kasih atas kunjungannya!\n")
        sb.append("==============================\n")
        
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
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${getString(R.string.app_name)} Document"
        
        val receiptText = generateReceiptText().replace("\n", "<br>")
        val htmlContent = "<html><body><pre style='font-family: monospace; font-size: 10pt;'>$receiptText</pre></body></html>"
        
        val webView = WebView(this)
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter(jobName)
                printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
            }
        }
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
