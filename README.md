# Catatan Utama Stabil

Build kompatibilitas widget untuk perangkat Android dengan launcher OEM yang tidak berhasil merender collection widget.

## Cara membaca

Widget ini sengaja tidak memakai `ListView`, `RemoteViewsService`, atau `RemoteCollectionItems`. Catatan dibagi menjadi beberapa bagian dan ditampilkan memakai TextView standar. Ketuk **Berikutnya** untuk melanjutkan dan **Sebelumnya** untuk kembali. Ketuk judul untuk membuka editor.

## Privasi dan data

Catatan disimpan lokal di private app storage. Tidak ada izin internet. Build ini dipasang berdampingan dengan versi sebelumnya karena application ID berbeda.

## Build

GitHub Actions menjalankan unit tests untuk pemecahan halaman, membangun APK, dan memverifikasi tanda tangan. Pengujian launcher perangkat pengguna tetap diperlukan.
