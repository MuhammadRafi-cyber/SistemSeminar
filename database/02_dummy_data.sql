-- =========================================================
-- DATA DUMMY untuk testing
-- Jalankan SETELAH 01_schema_database.sql
-- Password di bawah ditulis plain text HANYA untuk latihan/testing.
-- Untuk aplikasi sungguhan, password wajib di-hash (misal pakai BCrypt)
-- sebelum disimpan -- itu bisa didiskusikan dengan Putra di bagian backend.
-- =========================================================

USE db_pengelolaan_seminar;

-- ---------------------------------------------------------
-- Data institusi (3 contoh: kampus, sekolah, perusahaan)
-- ---------------------------------------------------------
INSERT INTO institusi (nama_institusi, jenis_institusi, kota) VALUES
('Universitas Pembangunan Nasional Veteran Jakarta', 'UNIVERSITAS', 'Jakarta'),
('SMA Negeri 1 Depok', 'SEKOLAH', 'Depok'),
('PT Teknologi Nusantara', 'PERUSAHAAN', 'Jakarta');

-- ---------------------------------------------------------
-- Data kategori seminar
-- ---------------------------------------------------------
INSERT INTO kategori (nama_kategori, deskripsi) VALUES
('Teknologi & Pemrograman', 'Seminar dan workshop seputar pengembangan perangkat lunak'),
('Pengembangan Diri & Soft Skill', 'Seminar seputar komunikasi, kepemimpinan, dan karier');

-- ---------------------------------------------------------
-- Data user: 2 Panitia, 3 Peserta (id_institusi mengacu ke data di atas)
-- ---------------------------------------------------------
INSERT INTO user (nama, email, password, role, no_telepon, id_institusi) VALUES
('Budi Santoso',   'budi.panitia@kampus.ac.id',  'panitia123', 'PANITIA', '081200000001', 1),
('Sari Wulandari', 'sari.panitia@kampus.ac.id',  'panitia123', 'PANITIA', '081200000002', 1),
('Andi Pratama',   'andi.peserta@kampus.ac.id',  'peserta123', 'PESERTA', '081300000001', 1),
('Dewi Lestari',   'dewi.peserta@kampus.ac.id',  'peserta123', 'PESERTA', '081300000002', 2),
('Fajar Nugraha',  'fajar.peserta@kampus.ac.id', 'peserta123', 'PESERTA', '081300000003', 3);

-- ---------------------------------------------------------
-- Data seminar (id_panitia & id_kategori mengacu ke data di atas)
-- ---------------------------------------------------------
INSERT INTO seminar (judul, deskripsi, tanggal_pelaksanaan, waktu_mulai, waktu_selesai, lokasi, kuota, id_panitia, id_kategori) VALUES
('Seminar Pemrograman Berorientasi Objek', 'Membahas konsep dasar OOP dalam Java', '2026-07-10', '09:00:00', '12:00:00', 'Aula A Kampus', 50, 1, 1),
('Workshop Basis Data dan JDBC',           'Praktik langsung koneksi Java ke MySQL', '2026-07-17', '13:00:00', '16:00:00', 'Lab Komputer 2', 30, 2, 1),
('Workshop Public Speaking untuk Mahasiswa', 'Melatih kepercayaan diri berbicara di depan umum', '2026-07-24', '09:00:00', '11:30:00', 'Aula B Kampus', 40, 1, 2);

-- ---------------------------------------------------------
-- Data pendaftaran (id_user dan id_seminar mengacu ke data di atas)
-- ---------------------------------------------------------
INSERT INTO pendaftaran (id_user, id_seminar) VALUES
(3, 1),  -- Andi daftar Seminar OOP (id_pendaftaran: 1)
(4, 1),  -- Dewi daftar Seminar OOP (id_pendaftaran: 2)
(5, 2),  -- Fajar daftar Workshop JDBC (id_pendaftaran: 3)
(4, 2);  -- Dewi daftar Workshop JDBC juga (id_pendaftaran: 4)

-- Update kuota_terisi sesuai jumlah pendaftaran (sementara manual,
-- nantinya bisa dihitung otomatis lewat service di backend)
UPDATE seminar SET kuota_terisi = 2 WHERE id_seminar = 1;
UPDATE seminar SET kuota_terisi = 2 WHERE id_seminar = 2;

-- ---------------------------------------------------------
-- Data detail_pendaftaran (Wujud Fisik Tiket)
-- PERBAIKAN: Menggunakan kolom nama, email, dan kode_booking unik
-- ---------------------------------------------------------
INSERT INTO detail_pendaftaran (id_pendaftaran, nama_peserta, email_peserta, no_telepon, kode_booking, qr_data) VALUES
(1, 'Andi Pratama', 'andi.peserta@kampus.ac.id', '081300000001', 'BOOK-OOP-0001', 'QR-OOP-0001'),
(2, 'Dewi Lestari', 'dewi.peserta@kampus.ac.id', '081300000002', 'BOOK-OOP-0002', 'QR-OOP-0002'),
(3, 'Fajar Nugraha', 'fajar.peserta@kampus.ac.id', '081300000003', 'BOOK-JDBC-0001', 'QR-JDBC-0001'),
(4, 'Dewi Lestari', 'dewi.peserta@kampus.ac.id', '081300000002', 'BOOK-JDBC-0002', 'QR-JDBC-0002');

-- ---------------------------------------------------------
-- Data presensi (contoh: sebagian sudah hadir)
-- PERBAIKAN: Relasi menggunakan id_detail, BUKAN id_pendaftaran
-- ---------------------------------------------------------
INSERT INTO presensi (id_detail, status_hadir, waktu_presensi) VALUES
(1, 'HADIR', '2026-07-10 09:05:00'),       -- Tiket Andi hadir di Seminar OOP
(2, 'TIDAK_HADIR', NULL);                  -- Tiket Dewi belum presensi

-- ---------------------------------------------------------
-- Data sertifikat (hanya untuk yang sudah HADIR)
-- PERBAIKAN: Relasi menggunakan id_detail, BUKAN id_pendaftaran
-- ---------------------------------------------------------
INSERT INTO sertifikat (id_detail, kode_sertifikat) VALUES
(1, 'CERT-2026-OOP-0001');  -- Sertifikat untuk Tiket Andi (id_detail: 1)

-- ---------------------------------------------------------
-- Data pembayaran (contoh simulasi, opsional)
-- ---------------------------------------------------------
INSERT INTO pembayaran (id_pendaftaran, jumlah, metode, status_pembayaran, tanggal_bayar) VALUES
(1, 50000, 'Transfer Bank', 'BERHASIL', '2026-07-01 10:00:00'),
(2, 50000, 'Transfer Bank', 'PENDING', NULL);

-- ---------------------------------------------------------
-- Data audit_log (contoh pencatatan aktivitas sistem)
-- id_user NULL pada baris ke-4 = aksi otomatis oleh sistem
-- ---------------------------------------------------------
INSERT INTO audit_log (id_user, aksi, tabel_terdampak, id_data_terdampak, keterangan) VALUES
(1, 'TAMBAH_SEMINAR', 'seminar', 1, 'Menambahkan seminar baru: Seminar Pemrograman Berorientasi Objek'),
(2, 'TAMBAH_SEMINAR', 'seminar', 2, 'Menambahkan seminar baru: Workshop Basis Data dan JDBC'),
(1, 'UBAH_PRESENSI', 'presensi', 1, 'Mencatat kehadiran peserta Andi Pratama sebagai HADIR'),
(NULL, 'TERBITKAN_SERTIFIKAT', 'sertifikat', 1, 'Sistem otomatis menerbitkan sertifikat untuk tiket #1');
