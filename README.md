# Catatan Utama

Aplikasi Android mandiri untuk menyimpan catatan lokal dan membacanya langsung dari widget layar utama yang dapat digulir.

Proyek ini terpisah dari TARDIGRADE.

## Build APK

GitHub Actions membangun APK debug secara otomatis saat ada push ke `main`, atau melalui menu **Actions → Build Catatan Utama APK → Run workflow**.

Artefak hasil build bernama `CATATAN-UTAMA-DEBUG-APK`. Unduh APK dari halaman run yang selesai dengan status sukses.

Source proyek lengkap tersedia di `Catatan-Utama-Source.zip`. Workflow mengekstrak arsip tersebut dan membangun proyek Android di dalamnya.

## Fitur utama

- Beberapa catatan disimpan secara lokal di perangkat.
- Pilih catatan yang ditampilkan pada widget.
- Isi panjang ditampilkan sebagai baris-baris yang bisa digulir pada widget layar utama.
- Widget dapat diubah ukurannya.
- Catatan bisa diimpor melalui Android Share jika aplikasi asal mendukung berbagi teks.
- Tidak memerlukan izin internet dan tidak mengunggah isi catatan.

## Batas pengujian

APK masih perlu diuji langsung di perangkat dan launcher Android pengguna. Dukungan gulir widget dapat dipengaruhi launcher bawaan perangkat.
