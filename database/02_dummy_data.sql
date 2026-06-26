-- =========================================================
-- DATA DUMMY untuk testing
-- Jalankan SETELAH 01_schema_database.sql
--
-- CATATAN soal password_hash: nilai di bawah ditulis sebagai teks
-- biasa HANYA supaya mudah dibaca saat testing manual lewat SQL
-- (cek JOIN, relasi, dst). Untuk login lewat aplikasi Java Putra
-- (yang sudah pakai SHA-256 + salt di PasswordHelper), user harus
-- didaftarkan lewat fitur Register di aplikasi -- BUKAN lewat
-- INSERT manual ini -- supaya hash-nya valid dan bisa login.
-- =========================================================

USE db_pengelolaan_seminar;

-- ---------------------------------------------------------
-- 1. Data institusi (id_institusi: 1, 2, 3)
-- ---------------------------------------------------------
INSERT INTO institusi (nama, alamat) VALUES
('Universitas Pembangunan Nasional Veteran Jakarta', 'Jl. RS Fatmawati, Pondok Labu, Jakarta Selatan'),
('SMA Negeri 1 Depok', 'Jl. Nusantara Raya, Depok'),
('PT Teknologi Nusantara', 'Jl. Sudirman Kav. 25, Jakarta Pusat');

-- ---------------------------------------------------------
-- 2. Data user: 2 Panitia, 3 Peserta, 1 Admin
-- ---------------------------------------------------------
INSERT INTO user (id_institusi, nama, email, password_hash, role, no_telepon) VALUES
(1, 'Budi Santoso',   'budi.panitia@kampus.ac.id',  'panitia123', 'PANITIA', '081200000001'),
(1, 'Sari Wulandari', 'sari.panitia@kampus.ac.id',  'panitia123', 'PANITIA', '081200000002'),
(1, 'Andi Pratama',   'andi.peserta@kampus.ac.id',  'peserta123', 'PESERTA', '081300000001'),
(2, 'Dewi Lestari',   'dewi.peserta@kampus.ac.id',  'peserta123', 'PESERTA', '081300000002'),
(3, 'Fajar Nugraha',  'fajar.peserta@kampus.ac.id', 'peserta123', 'PESERTA', '081300000003'),
(NULL, 'Super Admin', 'admin@eventix.com',          'admin123',   'ADMIN',   '089900000000');
-- id_user yang ter-generate: 1=Budi, 2=Sari, 3=Andi, 4=Dewi, 5=Fajar, 6=Admin

-- ---------------------------------------------------------
-- 3. Data seminar (id_institusi & id_panitia mengacu ke data di atas)
-- ---------------------------------------------------------
INSERT INTO seminar (id_institusi, id_panitia, judul, deskripsi, tanggal_mulai, tanggal_selesai, lokasi, kuota, harga) VALUES
(1, 1, 'Seminar Pemrograman Berorientasi Objek', 'Membahas konsep dasar OOP dalam Java', '2026-07-10 09:00:00', '2026-07-10 12:00:00', 'Aula A Kampus', 50, 50000),
(1, 2, 'Workshop Basis Data dan JDBC',           'Praktik langsung koneksi Java ke MySQL', '2026-07-17 13:00:00', '2026-07-17 16:00:00', 'Lab Komputer 2', 30, 75000),
(1, 1, 'Workshop Public Speaking untuk Mahasiswa', 'Melatih kepercayaan diri berbicara di depan umum', '2026-07-24 09:00:00', '2026-07-24 11:30:00', 'Aula B Kampus', 40, 60000);
-- id_seminar yang ter-generate: 1=Seminar OOP, 2=Workshop JDBC, 3=Public Speaking

-- ---------------------------------------------------------
-- 4. Data pendaftaran (HEADER TRANSAKSI)
-- ---------------------------------------------------------
INSERT INTO pendaftaran (id_pemesan, id_seminar, kode_transaksi, status, total) VALUES
(3, 1, 'TRX-OOP-0001',  'CONFIRMED', 100000),  -- Andi pesan 2 tiket Seminar OOP (sudah bayar)
(4, 1, 'TRX-OOP-0002',  'PENDING',   50000),   -- Dewi pesan 1 tiket Seminar OOP (belum bayar)
(5, 2, 'TRX-JDBC-0001', 'CONFIRMED', 75000);   -- Fajar pesan 1 tiket Workshop JDBC (sudah bayar)
-- id_pendaftaran yang ter-generate: 1=TRX-OOP-0001, 2=TRX-OOP-0002, 3=TRX-JDBC-0001

-- ---------------------------------------------------------
-- 5. Data detail_pendaftaran (TIKET, relasi 1:N ke pendaftaran)
-- ---------------------------------------------------------
INSERT INTO detail_pendaftaran (id_pendaftaran, nama_peserta, email_peserta, no_telepon, kode_booking, qr_data) VALUES
-- Transaksi 1 (Andi): 2 tiket -- untuk dirinya & temannya
(1, 'Andi Pratama',  'andi.peserta@kampus.ac.id', '081300000001', 'BOOK-OOP-0001',  'QR-OOP-0001'),
(1, 'Rina Permata',  'rina.teman.andi@gmail.com', '081300000099', 'BOOK-OOP-0002',  'QR-OOP-0002'),
-- Transaksi 2 (Dewi): 1 tiket
(2, 'Dewi Lestari',  'dewi.peserta@kampus.ac.id', '081300000002', 'BOOK-OOP-0003',  'QR-OOP-0003'),
-- Transaksi 3 (Fajar): 1 tiket
(3, 'Fajar Nugraha',  'fajar.peserta@kampus.ac.id', '081300000003', 'BOOK-JDBC-0001', 'QR-JDBC-0001');
-- id_detail yang ter-generate: 1=Andi, 2=Rina, 3=Dewi, 4=Fajar

-- Update kuota_terisi berdasarkan jumlah TIKET (bukan jumlah transaksi)
UPDATE seminar SET kuota_terisi = 3 WHERE id_seminar = 1; -- 2 tiket Andi + 1 tiket Dewi
UPDATE seminar SET kuota_terisi = 1 WHERE id_seminar = 2; -- 1 tiket Fajar

-- ---------------------------------------------------------
-- 6. Data presensi (merujuk ke id_detail / tiket)
-- ---------------------------------------------------------
INSERT INTO presensi (id_detail, status, waktu) VALUES
(1, 'HADIR', '2026-07-10 08:50:00'),       -- Tiket Andi: hadir
(2, 'HADIR', '2026-07-10 08:52:00'),       -- Tiket Rina: hadir
(3, 'TIDAK_HADIR', NULL),                  -- Tiket Dewi: belum presensi
(4, 'TIDAK_HADIR', NULL);                  -- Tiket Fajar: belum presensi (seminar masih lama)

-- ---------------------------------------------------------
-- 7. Data sertifikat (hanya untuk tiket yang sudah HADIR)
-- ---------------------------------------------------------
INSERT INTO sertifikat (id_detail, nomor, versi, file_path) VALUES
(1, 'CERT-2026-OOP-0001', 1, '/sertifikat/CERT-2026-OOP-0001.pdf'),  -- sertifikat tiket Andi
(2, 'CERT-2026-OOP-0002', 1, '/sertifikat/CERT-2026-OOP-0002.pdf');  -- sertifikat tiket Rina

-- ---------------------------------------------------------
-- 8. Data pembayaran (maksimal 1 per pendaftaran/transaksi)
-- ---------------------------------------------------------
INSERT INTO pembayaran (id_pendaftaran, metode, status, nominal, tanggal_bayar) VALUES
(1, 'Transfer Bank', 'BERHASIL', 100000, '2026-07-01 10:00:00'),  -- pembayaran transaksi Andi
(2, 'Transfer Bank', 'PENDING',  50000,  NULL),                   -- transaksi Dewi belum bayar
(3, 'E-Wallet',      'BERHASIL', 75000,  '2026-07-02 11:30:00');  -- pembayaran transaksi Fajar

-- ---------------------------------------------------------
-- 9. Data audit_log
-- ---------------------------------------------------------
INSERT INTO audit_log (id_user, aksi, entitas, waktu) VALUES
(1, 'TAMBAH_SEMINAR', 'seminar', '2026-06-01 09:00:00'),
(2, 'TAMBAH_SEMINAR', 'seminar', '2026-06-02 10:00:00'),
(1, 'UBAH_PRESENSI', 'presensi', '2026-07-10 08:50:00'),
(NULL, 'TERBITKAN_SERTIFIKAT', 'sertifikat', '2026-07-10 09:00:00');

-- Selesai. Cek dengan: SELECT * FROM user; dst.
