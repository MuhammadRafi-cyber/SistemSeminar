-- =========================================================
-- DATA DUMMY untuk testing — jalankan SETELAH schema.sql
-- =========================================================
USE db_pengelolaan_seminar;

-- 2 Panitia, 3 Peserta
INSERT INTO user (nama, email, password, role, no_telepon) VALUES
('Budi Santoso',   'budi.panitia@kampus.ac.id',  'panitia123', 'PANITIA', '081200000001'),
('Sari Wulandari', 'sari.panitia@kampus.ac.id',  'panitia123', 'PANITIA', '081200000002'),
('Andi Pratama',   'andi.peserta@kampus.ac.id',  'peserta123', 'PESERTA', '081300000001'),
('Dewi Lestari',   'dewi.peserta@kampus.ac.id',  'peserta123', 'PESERTA', '081300000002'),
('Fajar Nugraha',  'fajar.peserta@kampus.ac.id', 'peserta123', 'PESERTA', '081300000003');

INSERT INTO seminar (judul, deskripsi, tanggal_pelaksanaan, waktu_mulai, waktu_selesai, lokasi, kuota, id_panitia) VALUES
('Seminar Pemrograman Berorientasi Objek', 'Membahas konsep dasar OOP dalam Java', '2026-07-10', '09:00:00', '12:00:00', 'Aula A Kampus', 50, 1),
('Workshop Basis Data dan JDBC',           'Praktik langsung koneksi Java ke MySQL', '2026-07-17', '13:00:00', '16:00:00', 'Lab Komputer 2', 30, 2);

INSERT INTO pendaftaran (id_user, id_seminar) VALUES
(3, 1), (4, 1), (5, 2), (4, 2);

UPDATE seminar SET kuota_terisi = 2 WHERE id_seminar = 1;
UPDATE seminar SET kuota_terisi = 2 WHERE id_seminar = 2;

INSERT INTO presensi (id_pendaftaran, status_hadir, waktu_presensi) VALUES
(1, 'HADIR', '2026-07-10 09:05:00'),
(2, 'TIDAK_HADIR', NULL);

INSERT INTO sertifikat (id_pendaftaran, kode_sertifikat) VALUES
(1, 'CERT-2026-OOP-0001');

INSERT INTO pembayaran (id_pendaftaran, jumlah, metode, status_pembayaran, tanggal_bayar) VALUES
(1, 50000, 'Transfer Bank', 'BERHASIL', '2026-07-01 10:00:00'),
(2, 50000, 'Transfer Bank', 'PENDING', NULL);
