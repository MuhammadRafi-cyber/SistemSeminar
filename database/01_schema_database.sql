-- =========================================================
-- SCHEMA DATABASE: Sistem Informasi Pengelolaan Seminar
-- Dibuat oleh: Rafi (Database Engineer & Integration Support)
-- =========================================================
-- Cara pakai:
--   1. Buka MySQL Workbench / phpMyAdmin / terminal mysql
--   2. Jalankan SELURUH isi file ini sekali jalan (Run Script)
-- =========================================================

CREATE DATABASE IF NOT EXISTS db_pengelolaan_seminar
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE db_pengelolaan_seminar;

-- ---------------------------------------------------------
-- Tabel 1: user
-- Menyimpan data Peserta dan Panitia (dibedakan lewat kolom role)
-- Ini representasi tabel dari abstract class "User" di Class Diagram
-- ---------------------------------------------------------
CREATE TABLE user (
    id_user        INT AUTO_INCREMENT PRIMARY KEY,
    nama           VARCHAR(100) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    role           ENUM('PESERTA', 'PANITIA') NOT NULL,
    no_telepon     VARCHAR(20),
    tanggal_daftar TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- Tabel 2: seminar
-- Dikelola oleh user dengan role PANITIA (id_panitia)
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
    status              ENUM('DIBUKA', 'DITUTUP', 'SELESAI') DEFAULT 'DIBUKA',
    id_panitia          INT NOT NULL,
    dibuat_pada         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_panitia) REFERENCES user(id_user) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 3: pendaftaran
-- Satu peserta hanya bisa daftar SATU KALI per seminar
-- (dijaga dengan UNIQUE KEY id_user + id_seminar)
-- ---------------------------------------------------------
CREATE TABLE pendaftaran (
    id_pendaftaran     INT AUTO_INCREMENT PRIMARY KEY,
    id_user            INT NOT NULL,
    id_seminar         INT NOT NULL,
    tanggal_daftar     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status_pendaftaran ENUM('TERDAFTAR', 'DIBATALKAN') DEFAULT 'TERDAFTAR',
    UNIQUE KEY unik_pendaftaran (id_user, id_seminar),
    FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_seminar) REFERENCES seminar(id_seminar) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 4: presensi
-- Maksimal SATU presensi per pendaftaran (UNIQUE id_pendaftaran)
-- ---------------------------------------------------------
CREATE TABLE presensi (
    id_presensi    INT AUTO_INCREMENT PRIMARY KEY,
    id_pendaftaran INT NOT NULL UNIQUE,
    status_hadir   ENUM('HADIR', 'TIDAK_HADIR') NOT NULL DEFAULT 'TIDAK_HADIR',
    waktu_presensi TIMESTAMP NULL,
    FOREIGN KEY (id_pendaftaran) REFERENCES pendaftaran(id_pendaftaran) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 5: sertifikat
-- Diterbitkan berdasarkan pendaftaran yang sudah HADIR
-- ---------------------------------------------------------
CREATE TABLE sertifikat (
    id_sertifikat   INT AUTO_INCREMENT PRIMARY KEY,
    id_pendaftaran  INT NOT NULL UNIQUE,
    kode_sertifikat VARCHAR(50) NOT NULL UNIQUE,
    tanggal_terbit  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pendaftaran) REFERENCES pendaftaran(id_pendaftaran) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 6: pembayaran (opsional, untuk simulasi pembayaran)
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
-- Index tambahan untuk query yang sering dipakai (laporan, dashboard)
-- ---------------------------------------------------------
CREATE INDEX idx_seminar_panitia ON seminar(id_panitia);
CREATE INDEX idx_pendaftaran_user ON pendaftaran(id_user);
CREATE INDEX idx_pendaftaran_seminar ON pendaftaran(id_seminar);

-- Selesai. Lanjut jalankan 02_dummy_data.sql untuk mengisi data contoh.
