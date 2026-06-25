-- =========================================================
-- DATA DUMMY untuk testing
-- Jalankan SETELAH 01_schema_database.sql
-- =========================================================

USE db_pengelolaan_seminar;

-- ---------------------------------------------------------
-- 1. Data institusi (3 contoh: kampus, sekolah, perusahaan)
-- ---------------------------------------------------------
INSERT INTO institusi (nama_institusi, jenis_institusi, kota) VALUES
('Universitas Pembangunan Nasional Veteran Jakarta', 'UNIVERSITAS', 'Jakarta'),
('SMA Negeri 1 Depok', 'SEKOLAH', 'Depok'),
('PT Teknologi Nusantara', 'PERUSAHAAN', 'Jakarta');

-- ---------------------------------------------------------
-- 2. Data kategori seminar
-- ---------------------------------------------------------
INSERT INTO kategori (nama_kategori, deskripsi) VALUES
('Teknologi & Pemrograman', 'Seminar dan workshop seputar pengembangan perangkat lunak'),
('Pengembangan Diri & Soft Skill', 'Seminar seputar komunikasi, kepemimpinan, dan karier');

-- ---------------------------------------------------------
-- 3. Data user: 2 Panitia, 3 Peserta, 1 Admin
-- ---------------------------------------------------------
INSERT INTO user (nama, email, password, role, no_telepon, id_institusi) VALUES
('Budi Santoso',   'budi.panitia@kampus.ac.id',  'panitia123', 'PANITIA', '081200000001', 1),
('Sari Wulandari', 'sari.panitia@kampus.ac.id',  'panitia123', 'PANITIA', '081200000002', 1),
('Andi Pratama',   'andi.peserta@kampus.ac.id',  'peserta123', 'PESERTA', '081300000001', 1),
('Dewi Lestari',   'dewi.peserta@kampus.ac.id',  'peserta123', 'PESERTA', '081300000002', 2),
('Fajar Nugraha',  'fajar.peserta@kampus.ac.id', 'peserta123', 'PESERTA', '081300000003', 3),
('Super Admin',    'admin@eventix.com',          'admin123',   'ADMIN',   '089900000000', NULL);

-- ---------------------------------------------------------
-- 4. Data seminar
-- Harga diasumsikan Rp 50.000 untuk seminar 1, dan Rp 75.000 untuk seminar 2
-- ---------------------------------------------------------
INSERT INTO seminar (judul, deskripsi, tanggal_pelaksanaan, waktu_mulai, waktu_selesai, lokasi, kuota, id_panitia, id_kategori) VALUES
('Seminar Pemrograman Berorientasi Objek', 'Membahas konsep dasar OOP dalam Java', '2026-07-10', '09:00:00', '12:00:00', 'Aula A Kampus', 50, 1, 1),
('Workshop Basis Data dan JDBC',           'Praktik langsung koneksi Java ke MySQL', '2026-07-17', '13:00:00', '16:00:00', 'Lab Komputer 2', 30, 2, 1),
('Workshop Public Speaking untuk Mahasiswa', 'Melatih kepercayaan diri berbicara di depan umum', '2026-07-24', '09:00:00', '11:30:00', 'Aula B Kampus', 40, 1, 2);

-- ---------------------------------------------------------
-- 5. Data pendaftaran (HEADER TRANSAKSI)
-- ---------------------------------------------------------
INSERT INTO pendaftaran (id_user, id_seminar, status_pendaftaran) VALUES
(3, 1, 'CONFIRMED'),  -- id_pendaftaran: 1 (Andi daftar Seminar OOP, status sudah dibayar)
(4, 1, 'PENDING'),    -- id_pendaftaran: 2 (Dewi daftar Seminar OOP, belum bayar)
(5, 2, 'CONFIRMED');  -- id_pendaftaran: 3 (Fajar daftar Workshop JDBC, sudah dibayar)

-- ---------------------------------------------------------
-- 6. Data detail_pendaftaran (TIKET FISIK - Menguji relasi 1:N)
-- ---------------------------------------------------------
INSERT INTO detail_pendaftaran (id_pendaftaran, nama_peserta, email_peserta, no_telepon, kode_booking, qr_data) VALUES
-- TRANSAKSI 1: Andi membelikan 2 tiket (untuk dirinya dan temannya)
(1, 'Andi Pratama', 'andi.peserta@kampus.ac.id', '081300000001', 'BOOK-OOP-0001', 'QR-OOP-0001'),
(1, 'Rina Permata', 'rina.teman.andi@gmail.com', '081300000099', 'BOOK-OOP-0002', 'QR-OOP-0002'),

-- TRANSAKSI 2 & 3: Beli masing-masing 1 tiket
(2, 'Dewi Lestari', 'dewi.peserta@kampus.ac.id', '081300000002', 'BOOK-OOP-0003', 'QR-OOP-0003'),
(3, 'Fajar Nugraha', 'fajar.peserta@kampus.ac.id', '081300000003', 'BOOK-JDBC-0001', 'QR-JDBC-0001');

-- Update kuota_terisi sesuai jumlah TIKET (detail), bukan jumlah transaksi
UPDATE seminar SET kuota_terisi = 3 WHERE id_seminar = 1; -- (Andi beli 2, Dewi beli 1)
UPDATE seminar SET kuota_terisi = 1 WHERE id_seminar = 2; -- (Fajar beli 1)

-- ---------------------------------------------------------
-- 7. Data presensi (Merujuk ke id_detail / Tiket)
-- ---------------------------------------------------------
INSERT INTO presensi (id_detail, status_hadir, waktu_presensi) VALUES
(1, 'HADIR', '2026-07-10 08:50:00'),       -- Tiket Andi: Hadir
(2, 'HADIR', '2026-07-10 08:52:00'),       -- Tiket Rina (Teman Andi): Hadir
(3, 'TIDAK_HADIR', NULL),                  -- Tiket Dewi: Belum hadir
(4, 'TIDAK_HADIR', NULL);                  -- Tiket Fajar: Belum hadir (karena seminar masih lama)

-- ---------------------------------------------------------
-- 8. Data sertifikat (Hanya untuk yang statusnya HADIR)
-- ---------------------------------------------------------
INSERT INTO sertifikat (id_detail, kode_sertifikat) VALUES
(1, 'CERT-2026-OOP-0001'),  -- Sertifikat untuk Tiket Andi
(2, 'CERT-2026-OOP-0002');  -- Sertifikat untuk Tiket Rina

-- ---------------------------------------------------------
-- 9. Data pembayaran
-- ---------------------------------------------------------
INSERT INTO pembayaran (id_pendaftaran, jumlah, metode, status_pembayaran, tanggal_bayar) VALUES
(1, 100000, 'Transfer Bank', 'BERHASIL', '2026-07-01 10:00:00'), -- Transaksi Andi (2 x 50rb)
(2, 50000,  'Transfer Bank', 'PENDING',  NULL),                  -- Transaksi Dewi (1 x 50rb)
(3, 75000,  'E-Wallet',      'BERHASIL', '2026-07-02 11:30:00'); -- Transaksi Fajar (1 x 75rb)

-- ---------------------------------------------------------
-- 10. Data audit_log
-- ---------------------------------------------------------
INSERT INTO audit_log (id_user, aksi, tabel_terdampak, id_data_terdampak, keterangan) VALUES
(1, 'TAMBAH_SEMINAR', 'seminar', 1, 'Menambahkan seminar baru: Seminar Pemrograman Berorientasi Objek'),
(2, 'TAMBAH_SEMINAR', 'seminar', 2, 'Menambahkan seminar baru: Workshop Basis Data dan JDBC'),
(1, 'UBAH_PRESENSI', 'presensi', 1, 'Mencatat kehadiran peserta Andi Pratama sebagai HADIR'),
(NULL, 'TERBITKAN_SERTIFIKAT', 'sertifikat', 1, 'Sistem otomatis menerbitkan sertifikat untuk tiket Andi'),
(NULL, 'TERBITKAN_SERTIFIKAT', 'sertifikat', 2, 'Sistem otomatis menerbitkan sertifikat untuk tiket Rina');
