-- =========================================================
-- KUMPULAN QUERY CRUD
-- File ini BUKAN untuk dijalankan langsung sekaligus.
-- Ini adalah referensi query yang akan dipanggil dari Java
-- lewat PreparedStatement (tanda "?" = parameter yang diisi dari kode Java).
-- Diserahkan ke Putra (Backend) untuk dipakai di DAO/Service.
-- =========================================================

-- =========== A. REGISTRASI & LOGIN ===========

-- A1. Registrasi user baru (Peserta/Panitia)
INSERT INTO user (nama, email, password, role, no_telepon)
VALUES (?, ?, ?, ?, ?);

-- A2. Cek email sudah terdaftar atau belum (sebelum registrasi)
SELECT id_user FROM user WHERE email = ?;

-- A3. Login (cocokkan email + password, ambil role untuk redirect dashboard)
SELECT id_user, nama, email, role
FROM user
WHERE email = ? AND password = ?;

-- =========== B. CRUD SEMINAR (oleh Panitia) ===========

-- B1. Menampilkan semua seminar (untuk Dashboard Peserta & Panitia)
SELECT id_seminar, judul, deskripsi, tanggal_pelaksanaan, waktu_mulai,
       waktu_selesai, lokasi, kuota, kuota_terisi, status, id_panitia
FROM seminar
ORDER BY tanggal_pelaksanaan ASC;

-- B2. Menampilkan seminar milik panitia tertentu
SELECT * FROM seminar WHERE id_panitia = ?;

-- B3. Menambahkan seminar baru
INSERT INTO seminar
    (judul, deskripsi, tanggal_pelaksanaan, waktu_mulai, waktu_selesai, lokasi, kuota, id_panitia)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

-- B4. Mengubah data seminar
UPDATE seminar
SET judul = ?, deskripsi = ?, tanggal_pelaksanaan = ?, waktu_mulai = ?,
    waktu_selesai = ?, lokasi = ?, kuota = ?, status = ?
WHERE id_seminar = ?;

-- B5. Menghapus seminar
DELETE FROM seminar WHERE id_seminar = ?;

-- =========== C. PENDAFTARAN SEMINAR (oleh Peserta) ===========

-- C1. Cek apakah peserta sudah pernah daftar seminar ini (cegah duplikat)
-- KuotaPenuhException / PendaftaranDuplikatException dilempar di sisi Java
-- berdasarkan hasil query ini dan query B2 (cek kuota_terisi vs kuota)
SELECT id_pendaftaran FROM pendaftaran
WHERE id_user = ? AND id_seminar = ?;

-- C2. Menyimpan pendaftaran baru
INSERT INTO pendaftaran (id_user, id_seminar) VALUES (?, ?);

-- C3. Menambah kuota_terisi setelah pendaftaran berhasil
UPDATE seminar SET kuota_terisi = kuota_terisi + 1 WHERE id_seminar = ?;

-- C4. Menampilkan seluruh peserta dari satu seminar (untuk Panitia)
SELECT p.id_pendaftaran, u.nama, u.email, p.tanggal_daftar, p.status_pendaftaran
FROM pendaftaran p
JOIN user u ON p.id_user = u.id_user
WHERE p.id_seminar = ?;

-- C5. Menampilkan riwayat pendaftaran seorang peserta (untuk Dashboard Peserta)
SELECT s.judul, s.tanggal_pelaksanaan, p.status_pendaftaran, p.id_pendaftaran
FROM pendaftaran p
JOIN seminar s ON p.id_seminar = s.id_seminar
WHERE p.id_user = ?;

-- =========== D. PRESENSI ===========

-- D1. Menyimpan/mengubah presensi peserta
INSERT INTO presensi (id_pendaftaran, status_hadir, waktu_presensi)
VALUES (?, ?, NOW())
ON DUPLICATE KEY UPDATE status_hadir = ?, waktu_presensi = NOW();

-- D2. Cek status kehadiran (dipakai sebelum terbitkan sertifikat)
SELECT status_hadir FROM presensi WHERE id_pendaftaran = ?;

-- =========== E. SERTIFIKAT ===========

-- E1. Membuat data sertifikat (hanya jika status_hadir = 'HADIR', dicek di Java)
INSERT INTO sertifikat (id_pendaftaran, kode_sertifikat) VALUES (?, ?);

-- E2. Menampilkan sertifikat milik seorang peserta
SELECT st.kode_sertifikat, st.tanggal_terbit, se.judul
FROM sertifikat st
JOIN pendaftaran pd ON st.id_pendaftaran = pd.id_pendaftaran
JOIN seminar se ON pd.id_seminar = se.id_seminar
WHERE pd.id_user = ?;

-- =========== F. LAPORAN ===========

-- F1. Laporan untuk Peserta: riwayat seminar, kehadiran, sertifikat
SELECT se.judul, se.tanggal_pelaksanaan, pr.status_hadir, st.kode_sertifikat
FROM pendaftaran pd
JOIN seminar se ON pd.id_seminar = se.id_seminar
LEFT JOIN presensi pr ON pd.id_pendaftaran = pr.id_pendaftaran
LEFT JOIN sertifikat st ON pd.id_pendaftaran = st.id_pendaftaran
WHERE pd.id_user = ?;

-- F2. Laporan untuk Panitia: jumlah pendaftar, kehadiran, sertifikat per seminar
SELECT
    se.judul,
    COUNT(pd.id_pendaftaran) AS jumlah_pendaftar,
    SUM(CASE WHEN pr.status_hadir = 'HADIR' THEN 1 ELSE 0 END) AS jumlah_hadir,
    COUNT(st.id_sertifikat) AS jumlah_sertifikat
FROM seminar se
LEFT JOIN pendaftaran pd ON se.id_seminar = pd.id_seminar
LEFT JOIN presensi pr ON pd.id_pendaftaran = pr.id_pendaftaran
LEFT JOIN sertifikat st ON pd.id_pendaftaran = st.id_pendaftaran
WHERE se.id_panitia = ?
GROUP BY se.id_seminar, se.judul;
