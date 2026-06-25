-- =========================================================
-- SCHEMA DATABASE: Sistem Informasi Pengelolaan Seminar
-- Dibuat oleh: Rafi (Database Engineer & Integration Support)
-- Revisi 3: Perbaikan Arsitektur Relasi 1:N pada Detail Tiket
--           dan sinkronisasi ENUM status dengan Backend.
-- =========================================================
-- Cara pakai:
--   1. Buka MySQL Workbench / phpMyAdmin / terminal mysql
--   2. Jalankan SELURUH isi file ini sekali jalan (Run Script)
--
-- CATATAN: kalau database db_pengelolaan_seminar sudah ada dari
-- sebelumnya (versi 6 tabel), sebaiknya DROP DATABASE dulu supaya
-- bersih, baru jalankan file ini dari awal.
--   DROP DATABASE IF EXISTS db_pengelolaan_seminar;
-- =========================================================

CREATE DATABASE IF NOT EXISTS db_pengelolaan_seminar
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE db_pengelolaan_seminar;

-- ---------------------------------------------------------
-- Tabel 1: institusi
-- Master data asal institusi/instansi user (kampus, sekolah,
-- perusahaan, dst). Dibuat duluan karena dirujuk oleh tabel user.
-- ---------------------------------------------------------
CREATE TABLE institusi (
    id_institusi   INT AUTO_INCREMENT PRIMARY KEY,
    nama_institusi VARCHAR(150) NOT NULL,
    jenis_institusi ENUM('UNIVERSITAS', 'SEKOLAH', 'PERUSAHAAN', 'INSTANSI_PEMERINTAH', 'LAINNYA') DEFAULT 'LAINNYA',
    kota           VARCHAR(100),
    dibuat_pada    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- Tabel 2: kategori
-- Master data kategori/topik seminar (Teknologi, Bisnis, dst).
-- Dibuat duluan karena dirujuk oleh tabel seminar.
-- ---------------------------------------------------------
CREATE TABLE kategori (
    id_kategori   INT AUTO_INCREMENT PRIMARY KEY,
    nama_kategori VARCHAR(100) NOT NULL UNIQUE,
    deskripsi     TEXT,
    dibuat_pada   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- Tabel 3: user
-- PERBAIKAN: Menambahkan role 'ADMIN'
-- ---------------------------------------------------------
CREATE TABLE user (
    id_user        INT AUTO_INCREMENT PRIMARY KEY,
    nama           VARCHAR(100) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    role           ENUM('PESERTA', 'PANITIA', 'ADMIN') NOT NULL,
    no_telepon     VARCHAR(20),
    id_institusi   INT NULL,
    tanggal_daftar TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_institusi) REFERENCES institusi(id_institusi) ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Tabel 4: seminar
-- PERBAIKAN: Menambahkan status 'CANCELLED'
-- ---------------------------------------------------------
CREATE TABLE seminar (
    id_seminar          INT AUTO_INCREMENT PRIMARY KEY,
    judul               VARCHAR(150) NOT NULL,
    deskripsi           TEXT,
    tanggal_pelaksanaan DATE NOT NULL,
    waktu_mulai         TIME,
    waktu_selesai       TIME,
    lokasi              VARCHAR(150),
    kuota               INT NOT NULL,
    kuota_terisi        INT NOT NULL DEFAULT 0,
    status              ENUM('DIBUKA', 'DITUTUP', 'SELESAI', 'CANCELLED') DEFAULT 'DIBUKA',
    id_panitia          INT NOT NULL,
    id_kategori         INT NOT NULL,
    dibuat_pada         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_panitia) REFERENCES user(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_kategori) REFERENCES kategori(id_kategori) ON DELETE RESTRICT
);

-- ---------------------------------------------------------
-- Tabel 5: pendaftaran
-- PERBAIKAN: Mengubah status_pendaftaran menjadi PENDING/CONFIRMED/CANCELLED
-- ---------------------------------------------------------
CREATE TABLE pendaftaran (
    id_pendaftaran     INT AUTO_INCREMENT PRIMARY KEY,
    id_user            INT NOT NULL,
    id_seminar         INT NOT NULL,
    tanggal_daftar     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status_pendaftaran ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',
    UNIQUE KEY unik_pendaftaran (id_user, id_seminar),
    FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_seminar) REFERENCES seminar(id_seminar) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 6: detail_pendaftaran (Wujud Fisik Tiket)
-- PERBAIKAN: Menghapus UNIQUE pada id_pendaftaran agar 1 transaksi
--            bisa menampung 1-4 tiket. Mengubah kolom agar sesuai ERD.
-- ---------------------------------------------------------
CREATE TABLE detail_pendaftaran (
    id_detail      INT AUTO_INCREMENT PRIMARY KEY,
    id_pendaftaran INT NOT NULL, 
    nama_peserta   VARCHAR(150) NOT NULL,
    email_peserta  VARCHAR(150) NOT NULL,
    no_telepon     VARCHAR(20),
    kode_booking   VARCHAR(50) NOT NULL UNIQUE,
    qr_data        TEXT,
    FOREIGN KEY (id_pendaftaran) REFERENCES pendaftaran(id_pendaftaran) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 7: presensi
-- PERBAIKAN: Mengubah Foreign Key agar merujuk ke id_detail (Tiket)
-- ---------------------------------------------------------
CREATE TABLE presensi (
    id_presensi    INT AUTO_INCREMENT PRIMARY KEY,
    id_detail      INT NOT NULL UNIQUE, 
    status_hadir   ENUM('HADIR', 'TIDAK_HADIR') NOT NULL DEFAULT 'TIDAK_HADIR',
    waktu_presensi TIMESTAMP NULL,
    FOREIGN KEY (id_detail) REFERENCES detail_pendaftaran(id_detail) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 8: sertifikat
-- PERBAIKAN: Mengubah Foreign Key agar merujuk ke id_detail (Tiket)
-- ---------------------------------------------------------
CREATE TABLE sertifikat (
    id_sertifikat   INT AUTO_INCREMENT PRIMARY KEY,
    id_detail       INT NOT NULL UNIQUE, 
    kode_sertifikat VARCHAR(50) NOT NULL UNIQUE,
    tanggal_terbit  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_detail) REFERENCES detail_pendaftaran(id_detail) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 9: pembayaran (opsional, untuk simulasi pembayaran)
-- ---------------------------------------------------------
CREATE TABLE pembayaran (
    id_pembayaran     INT AUTO_INCREMENT PRIMARY KEY,
    id_pendaftaran    INT NOT NULL,
    jumlah            DECIMAL(12, 2) NOT NULL DEFAULT 0,
    metode            VARCHAR(50),
    status_pembayaran ENUM('PENDING', 'BERHASIL', 'GAGAL') DEFAULT 'PENDING',
    tanggal_bayar     TIMESTAMP NULL,
    FOREIGN KEY (id_pendaftaran) REFERENCES pendaftaran(id_pendaftaran) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 10: audit_log
-- Mencatat aktivitas penting di sistem (siapa melakukan apa,
-- kapan) untuk keperluan audit/akuntabilitas.
-- id_user NULLABLE: aksi otomatis oleh sistem (misal penerbitan
-- sertifikat otomatis) tidak selalu punya pelaku user spesifik.
-- ---------------------------------------------------------
CREATE TABLE audit_log (
    id_log            INT AUTO_INCREMENT PRIMARY KEY,
    id_user           INT NULL,
    aksi              VARCHAR(100) NOT NULL,
    tabel_terdampak   VARCHAR(50),
    id_data_terdampak INT,
    keterangan        TEXT,
    waktu             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Index tambahan untuk query yang sering dipakai (laporan, dashboard)
-- ---------------------------------------------------------
CREATE INDEX idx_user_institusi ON user(id_institusi);
CREATE INDEX idx_seminar_panitia ON seminar(id_panitia);
CREATE INDEX idx_seminar_kategori ON seminar(id_kategori);
CREATE INDEX idx_pendaftaran_user ON pendaftaran(id_user);
CREATE INDEX idx_pendaftaran_seminar ON pendaftaran(id_seminar);
CREATE INDEX idx_audit_log_user ON audit_log(id_user);
