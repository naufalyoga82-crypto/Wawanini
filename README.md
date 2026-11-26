# Catatan Utama Perbaikan

Ini adalah build pemulihan widget untuk Android 12 ke atas. Aplikasi dipasang berdampingan dengan Catatan Utama lama agar data catatan lama tidak terhapus oleh perbedaan tanda tangan APK.

## Memindahkan catatan dari aplikasi lama

1. Jangan hapus aplikasi Catatan Utama lama.
2. Buka setiap catatan lama, tekan lama pada isi catatan, pilih **Pilih semua**, lalu **Bagikan**.
3. Pilih **Catatan Utama Perbaikan** pada menu berbagi Android.
4. Ulangi untuk catatan lain yang ingin dipindahkan.
5. Pastikan semua isi catatan sudah masuk dan widget versi baru dapat dibaca sebelum menghapus aplikasi lama.

## Widget

- Android 12 (API 31) ke atas menggunakan `RemoteViews.RemoteCollectionItems` untuk mengisi `ListView` secara langsung.
- Android 8–11 menggunakan `RemoteViewsService` sebagai fallback.
- Teks panjang dibagi menjadi baris yang dapat digulir.
- Perubahan catatan mengirim snapshot data terbaru ke widget.

## Verifikasi

GitHub Actions menjalankan unit test pemecah baris, build APK, dan verifikasi tanda tangan. Widget tetap perlu diuji di launcher Xiaomi/HyperOS pada perangkat pengguna.

## Catatan pembaruan

Build ini memiliki application ID `id.yoga.catatanutama.fix`, berbeda dari aplikasi lama `id.yoga.catatanutama`. Jangan menghapus aplikasi lama sebelum migrasi catatan selesai.
