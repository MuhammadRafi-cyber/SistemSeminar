-- =========================================================
-- DATA DUMMY untuk testing
-- Jalankan SETELAH 01_schema_database.sql
-- Password di bawah ditulis plain text HANYA untuk latihan/testing.
-- Untuk aplikasi sungguhan, password wajib di-hash (misal pakai BCrypt)
-- sebelum disimpan -- itu bisa didiskusikan dengan Putra di bagian backend.
-- =========================================================

USE db_pengelolaan_seminar;

-- ---------------------------------------------------------
-- Data user: 2 Panitia, 3 Peserta
-- ---------------------------------------------------------
INSERT INTO user (nama, email, password, role, no_telepon) VALUES
('Budi Santoso',  'budi.panitia@kampus.ac.id',   'panitia123',  'PANITIA', '081200000001'),
('Sari Wulandari', 'sari.panitia@kampus.ac.id',  'panitia123',  'PANITIA', '081200000002'),
('Andi Pratama',  'andi.peserta@kampus.ac.id',   'peserta123',  'PESERTA', '081300000001'),
('Dewi Lestari',  'dewi.peserta@kampus.ac.id',   'peserta123',  'PESERTA', '081300000002'),
('Fajar Nugraha', 'fajar.peserta@kampus.ac.id',  'peserta123',  'PESERTA', '081300000003');

-- ---------------------------------------------------------
-- Data seminar (id_panitia mengacu ke id_user di atas)
-- ---------------------------------------------------------
INSERT INTO seminar (judul, deskripsi, tanggal_pelaksanaan, waktu_mulai, waktu_selesai, lokasi, kuota, id_panitia) VALUES
('Seminar Pemrograman Berorientasi Objek', 'Membahas konsep dasar OOP dalam Java', '2026-07-10', '09:00:00', '12:00:00', 'Aula A Kampus', 50, 1),
('Workshop Basis Data dan JDBC',           'Praktik langsung koneksi Java ke MySQL', '2026-07-17', '13:00:00', '16:00:00', 'Lab Komputer 2', 30, 2);

-- ---------------------------------------------------------
-- Data pendaftaran (id_user dan id_seminar mengacu ke data di atas)
-- ---------------------------------------------------------
INSERT INTO pendaftaran (id_user, id_seminar) VALUES
(3, 1),  -- Andi daftar Seminar OOP
(4, 1),  -- Dewi daftar Seminar OOP
(5, 2),  -- Fajar daftar Workshop JDBC
(4, 2);  -- Dewi daftar Workshop JDBC juga

-- Update kuota_terisi sesuai jumlah pendaftaran (sementara manual,
-- nantinya bisa dihitung otomatis lewat service di backend)
UPDATE seminar SET kuota_terisi = 2 WHERE id_seminar = 1;
UPDATE seminar SET kuota_terisi = 2 WHERE id_seminar = 2;

-- ---------------------------------------------------------
-- Data presensi (contoh: sebagian sudah hadir)
-- ---------------------------------------------------------
INSERT INTO presensi (id_pendaftaran, status_hadir, waktu_presensi) VALUES
(1, 'HADIR', '2026-07-10 09:05:00'),       -- Andi hadir di Seminar OOP
(2, 'TIDAK_HADIR', NULL);                  -- Dewi belum presensi

-- ---------------------------------------------------------
-- Data sertifikat (hanya untuk yang sudah HADIR)
-- ---------------------------------------------------------
INSERT INTO sertifikat (id_pendaftaran, kode_sertifikat) VALUES
(1, 'CERT-2026-OOP-0001');  -- sertifikat untuk Andi

-- ---------------------------------------------------------
-- Data pembayaran (contoh simulasi, opsional)
-- ---------------------------------------------------------
INSERT INTO pembayaran (id_pendaftaran, jumlah, metode, status_pembayaran, tanggal_bayar) VALUES
(1, 50000, 'Transfer Bank', 'BERHASIL', '2026-07-01 10:00:00'),
(2, 50000, 'Transfer Bank', 'PENDING', NULL);

-- Selesai. Cek dengan: SELECT * FROM user; dst.
