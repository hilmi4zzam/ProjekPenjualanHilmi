# SatSet - Point of Sale (POS) App

> Aplikasi kasir digital *sat-set* anti ribet! Dirancang khusus untuk mempermudah manajemen toko, pencatatan transaksi, hingga pengaturan pegawai dalam satu genggaman.

## Tech Stack yang Dipakai
* **Language:** Kotlin
* **Architecture:** MVVM (Model-View-ViewModel)
* **UI/Design:** XML Layouts
* **Backend/Database:** Firebase Realtime Database
* **Tools:** Android Studio

## Key Features
Aplikasi ini punya beberapa fitur utama buat *handle* *flow* kasir dari awal sampai akhir:
1.  **Authentication System:** Login aman buat kasir atau admin (Pegawai).
2.  **Dashboard & Saldo Toko:** *Monitoring* total pendapatan/saldo toko secara *real-time*.
3.  **Manajemen Katalog:** * Tambah, edit, dan hapus Produk.
    * Pengaturan Kategori (bisa di-set Aktif/Nonaktif).
4.  **Manajemen Pegawai:** Kontrol akses pegawai dan ubah status aktif/nonaktif.
5.  **Sistem Transaksi (POS):** * Pilih item pesanan (Makanan/Minuman/Camilan) dengan mudah.
    * Perhitungan total belanja otomatis.
6.  **Pembayaran Flexible:** *Support* pembayaran via QRIS maupun Tunai.
7.  **Detail & Struk Transaksi:** Menampilkan rincian transaksi (uang bayar, kembalian) lengkap dengan fitur Cetak dan Bagikan (Share).

---

## App Screenshots

### 1. Splash Screen & Login
Tampilan awal saat user membuka aplikasi dan *login screen* untuk masuk ke sistem.

<p align="center">
  <img src="screenshots/WhatsApp Image 2026-05-29 at 09.13.34.jpeg" width="250" alt="Splash Screen">
  <img src="screenshots/WhatsApp Image 2026-05-29 at 09.09.56.jpeg" width="250" alt="Login Screen">
</p>

### 2. Dashboard Main Menu
Halaman utama yang menampilkan *summary* saldo toko dan navigasi menu-menu krusial (Produk, Kategori, Cabang, Pegawai, Laporan, Transaksi).

<p align="center">
  <img src="screenshots/WhatsApp Image 2026-05-29 at 09.29.54.jpeg" width="250" alt="Dashboard">
</p>

### 3. POS / Menu Pesanan
Fitur utama kasir untuk *input* pesanan pelanggan secara cepat (sat-set!).

<p align="center">
  <img src="screenshots/WhatsApp Image 2026-05-29 at 09.28.54.jpeg" width="250" alt="Menu Pesanan">
</p>

### 4. Checkout Pembayaran & Struk Transaksi
*Flow* penyelesaian pesanan, mencakup pemilihan metode pembayaran (QRIS/Tunai) dan rincian kembalian yang bisa di-cetak atau di-share.

<p align="center">
  <img src="screenshots/WhatsApp Image 2026-05-29 at 09.09.53.jpeg" width="250" alt="Pembayaran">
  <img src="screenshots/WhatsApp Image 2026-05-29 at 09.24.19.jpeg" width="250" alt="Detail Transaksi">
</p>

### 5. Manajemen Kategori & Produk
Halaman admin untuk menambah variasi produk dan kategori menu jualan.

<p align="center">
  <img src="screenshots/WhatsApp Image 2026-05-29 at 09.09.55(1).jpeg" width="250" alt="Tambah Kategori">
  <img src="screenshots/WhatsApp Image 2026-05-29 at 09.09.56(3).jpeg" width="250" alt="Tambah Produk">
  <img src="screenshots/WhatsApp Image 2026-05-29 at 09.09.55.jpeg" width="250" alt="List Kategori">
</p>

### 6. Manajemen Pegawai
Pemilik toko bisa menambah pegawai baru atau menonaktifkan akun pegawai lama.

<p align="center">
  <img src="screenshots/WhatsApp Image 2026-05-29 at 09.24.20.jpeg" width="250" alt="List Pegawai">
</p>

---

## How to Run (Local Development)
1. *Clone* repository ini ke *local machine* lo.
2. Buka **Android Studio**.
3. Pilih menu `File > Open` dan cari folder project ini.
4. *Sync Project with Gradle Files* dan tunggu sampai proses selesai.
5. (Opsional) Sambungkan project dengan *Firebase Project* lu melalui `Tools > Firebase` kalau lu butuh sinkronisasi *database* sendiri.
6. Klik *Run* (`Shift + F10`) ke emulator atau *physical device* lo.
