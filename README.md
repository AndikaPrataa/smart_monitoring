# EcoUnila - Smart Energy And Indoor Environmental Quality Monitoring System Based IoT

**Deskripsi**

EcoUnila merupakan sistem Smart Energy Monitoring yang dikembangkan untuk mendukung pemantauan konsumsi energi listrik dan kualitas lingkungan dalam ruangan (Indoor Environmental Quality / IEQ) secara real-time di Rumah Sakit Pendidikan Nasional Universitas Lampung (RSPTN Unila). Data monitoring diperoleh dari berbagai perangkat IoT dan sensor lingkungan yang dikirimkan ke server melalui REST API sehingga informasi dapat ditampilkan secara langsung kepada pengguna.

Sistem ini terdiri dari dua platform utama, yaitu:
1. Web Application sebagai pusat pengelolaan data monitoring, visualisasi dashboard, manajemen pengguna, serta konfigurasi sistem.
2. Android Mobile Application yang memungkinkan admin maupun teknisi memantau kondisi energi dan lingkungan secara real-time, menerima notifikasi, serta mengakses data monitoring dari mana saja.


**Fitur Utama**

EcoUnila menyediakan berbagai fitur untuk mendukung proses pemantauan energi listrik dan kualitas lingkungan dalam ruangan secara real-time. 

a. Website EcoUnila (https://eco.unila.ac.id/)
Pada platform web memiliki fitur sebagai berikut :
- Dashboard monitoring energi listrik dan lingkungan berbasis Indoor Environmental Quality (IEQ)
- Manajemen pengguna
- Riwayat monitoring
- Visualisasi data bentuk grafik
- Manajemen notifikasi

b. Aplikasi EcoUnila
Aplikasi Android dirancang untuk memberikan kemudahan bagi pengguna dalam memantau kondisi energi dan lingkungan secara langsung melalui perangkat mobile.
- Dashboard monitoring listrik dan lingkungan real-time
- Push Notification menggunakan Firebase Cloud Messaging (FCM)
- Sinkronisasi data menggunakan REST API 
