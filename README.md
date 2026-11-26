# Catatan Utama

Aplikasi Android mandiri untuk menyimpan catatan lokal dan membacanya langsung dari widget layar utama yang dapat digulir.

## Widget

- Android 12 (API 31) ke atas menggunakan `RemoteViews.RemoteCollectionItems` untuk mengisi `ListView` secara langsung.
- Android 8–11 menggunakan `RemoteViewsService` sebagai jalur kompatibilitas.
- Isi catatan dibagi menjadi baris-baris yang dapat digulir.
- Ketika catatan berubah, widget menerima snapshot data terbaru.

## Build dan pengujian

GitHub Actions menjalankan unit test pemecah baris widget, membangun APK debug, dan memverifikasi tanda tangan APK. Widget tetap perlu diuji pada launcher fisik, khususnya Xiaomi/HyperOS.

## Signing

APK debug yang dibangun di GitHub-hosted runner dapat memakai kunci debug berbeda pada run berbeda. Perbedaan sertifikat akan mencegah APK baru dipasang sebagai update di atas APK lama. Jangan menghapus aplikasi lama sebelum data catatan dicadangkan dan kompatibilitas tanda tangan diperiksa.
