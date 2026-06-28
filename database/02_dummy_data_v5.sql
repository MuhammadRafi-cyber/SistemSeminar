-- =========================================================
-- DATA DUMMY v5 — jalankan SETELAH 01_schema_database.sql
-- CATATAN: password_hash di bawah adalah placeholder.
-- Untuk login via aplikasi, daftarkan ulang lewat fitur Registrasi
-- agar hash SHA-256+salt terbentuk dengan benar.
-- =========================================================

USE db_pengelolaan_seminar;

-- 1. Institusi
INSERT INTO institusi (nama, alamat, logo_path) VALUES
('Universitas Pembangunan Nasional Veteran Jakarta', 'Jl. RS Fatmawati, Pondok Labu, Jakarta Selatan', NULL),
('SMA Negeri 1 Depok',                              'Jl. Nusantara Raya, Depok',                       NULL),
('PT Teknologi Nusantara',                          'Jl. Sudirman Kav. 25, Jakarta Pusat',             NULL);
-- id: 1=UPNVJ, 2=SMAN1Depok, 3=PTTeknologi

-- 2. Kategori
INSERT INTO kategori (nama_kategori) VALUES
('Teknologi & Pemrograman'),
('Pengembangan Diri & Soft Skill'),
('Bisnis & Kewirausahaan');
-- id: 1=Teknologi, 2=Soft Skill, 3=Bisnis

-- 3. User (2 Panitia, 3 Peserta, 1 Admin)
INSERT INTO user (id_institusi, nama, username, email, password_hash, role, no_telepon) VALUES
(1, 'Budi Santoso',   'budi_pan',   'budi.panitia@kampus.ac.id',  'PLACEHOLDER_HASH', 'PANITIA', '081200000001'),
(1, 'Sari Wulandari', 'sari_pan',   'sari.panitia@kampus.ac.id',  'PLACEHOLDER_HASH', 'PANITIA', '081200000002'),
(1, 'Andi Pratama',   'andi_pes',   'andi.peserta@kampus.ac.id',  'PLACEHOLDER_HASH', 'PESERTA', '081300000001'),
(2, 'Dewi Lestari',   'dewi_pes',   'dewi.peserta@kampus.ac.id',  'PLACEHOLDER_HASH', 'PESERTA', '081300000002'),
(3, 'Fajar Nugraha',  'fajar_pes',  'fajar.peserta@kampus.ac.id', 'PLACEHOLDER_HASH', 'PESERTA', '081300000003'),
(NULL,'Super Admin',  'superadmin', 'admin@eventix.com',          'PLACEHOLDER_HASH', 'ADMIN',   '089900000000');
-- id_user: 1=Budi(Panitia), 2=Sari(Panitia), 3=Andi, 4=Dewi, 5=Fajar, 6=Admin

-- 4. Seminar
INSERT INTO seminar (id_institusi, id_panitia, id_kategori, judul, deskripsi, pembicara,
    tanggal_mulai, tanggal_selesai, mode, lokasi, kuota, harga) VALUES
(1, 1, 1, 'Seminar Pemrograman Berorientasi Objek',
    'Membahas konsep dasar OOP dalam Java', 'Dr. Budi Santoso',
    '2026-07-10 09:00:00', '2026-07-10 12:00:00', 'OFFLINE', 'Aula A Kampus', 50, 50000),
(1, 2, 1, 'Workshop Basis Data dan JDBC',
    'Praktik langsung koneksi Java ke MySQL', 'Sari Wulandari, M.Kom.',
    '2026-07-17 13:00:00', '2026-07-17 16:00:00', 'OFFLINE', 'Lab Komputer 2', 30, 75000),
(1, 1, 2, 'Workshop Public Speaking untuk Mahasiswa',
    'Melatih kepercayaan diri berbicara di depan umum', 'Motivator Berpengalaman',
    '2026-07-24 09:00:00', '2026-07-24 11:30:00', 'OFFLINE', 'Aula B Kampus', 40, 60000);
-- id_seminar: 1=OOP, 2=JDBC, 3=PublicSpeaking

-- 5. Pendaftaran (header transaksi)
INSERT INTO pendaftaran (id_pemesan, id_seminar, kode_transaksi, status, total) VALUES
(3, 1, 'TRX-20260701-OOP01', 'CONFIRMED', 100000),  -- Andi beli 2 tiket OOP
(4, 1, 'TRX-20260701-OOP02', 'PENDING',    50000),  -- Dewi beli 1 tiket OOP (belum bayar)
(5, 2, 'TRX-20260702-JDBC1', 'CONFIRMED',  75000);  -- Fajar beli 1 tiket JDBC
-- id_pendaftaran: 1=Andi, 2=Dewi, 3=Fajar

-- 6. Detail pendaftaran (tiket)
INSERT INTO detail_pendaftaran (id_pendaftaran, nama_peserta, email_peserta, no_telepon, kode_booking, qr_data) VALUES
(1, 'Andi Pratama',  'andi.peserta@kampus.ac.id',  '081300000001', 'BOOK-OOP-0001', 'QR-OOP-0001'),
(1, 'Rina Permata',  'rina.teman.andi@gmail.com',  '081300000099', 'BOOK-OOP-0002', 'QR-OOP-0002'),
(2, 'Dewi Lestari',  'dewi.peserta@kampus.ac.id',  '081300000002', 'BOOK-OOP-0003', 'QR-OOP-0003'),
(3, 'Fajar Nugraha', 'fajar.peserta@kampus.ac.id', '081300000003', 'BOOK-JDBC-001', 'QR-JDBC-001');
-- id_detail: 1=Andi, 2=Rina, 3=Dewi, 4=Fajar

-- Update kuota_terisi (jumlah tiket, bukan jumlah transaksi)
UPDATE seminar SET kuota_terisi = 3 WHERE id_seminar = 1;
UPDATE seminar SET kuota_terisi = 1 WHERE id_seminar = 2;

-- 7. Pembayaran
INSERT INTO pembayaran (id_pendaftaran, metode, status, nominal, waktu_bayar, status_refund) VALUES
(1, 'Transfer Bank', 'BERHASIL', 100000, '2026-07-01 10:00:00', 'TIDAK_ADA'),
(2, 'Transfer Bank', 'PENDING',   50000, NULL,                  'TIDAK_ADA'),
(3, 'E-Wallet',      'BERHASIL',  75000, '2026-07-02 11:30:00', 'TIDAK_ADA');

-- 8. Presensi (dicatat_oleh = id Panitia yang scan)
INSERT INTO presensi (id_detail, status, waktu, dicatat_oleh) VALUES
(1, 'HADIR',       '2026-07-10 08:50:00', 1),
(2, 'HADIR',       '2026-07-10 08:52:00', 1),
(3, 'TIDAK_HADIR', NULL,                  NULL),
(4, 'TIDAK_HADIR', NULL,                  NULL);

-- 9. Sertifikat (hanya tiket HADIR)
INSERT INTO sertifikat (id_detail, nomor_sertifikat, versi, file_path) VALUES
(1, 'CERT-2026-OOP-0001', 1, '/sertifikat/CERT-2026-OOP-0001.pdf'),
(2, 'CERT-2026-OOP-0002', 1, '/sertifikat/CERT-2026-OOP-0002.pdf');

-- 10. Audit log
INSERT INTO audit_log (id_user, aksi, entitas, id_entitas, keterangan) VALUES
(1, 'TAMBAH_SEMINAR',      'seminar',   1, 'Membuat seminar OOP'),
(2, 'TAMBAH_SEMINAR',      'seminar',   2, 'Membuat seminar JDBC'),
(1, 'CATAT_PRESENSI',      'presensi',  1, 'Presensi HADIR untuk tiket BOOK-OOP-0001'),
(1, 'CATAT_PRESENSI',      'presensi',  2, 'Presensi HADIR untuk tiket BOOK-OOP-0002'),
(NULL,'TERBITKAN_SERTIFIKAT','sertifikat',1,'Sertifikat diterbitkan untuk tiket BOOK-OOP-0001'),
(NULL,'TERBITKAN_SERTIFIKAT','sertifikat',2,'Sertifikat diterbitkan untuk tiket BOOK-OOP-0002');
