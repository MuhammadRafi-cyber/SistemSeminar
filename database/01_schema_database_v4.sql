-- =========================================================
-- SCHEMA DATABASE: Sistem Informasi Pengelolaan Seminar (Eventix)
-- Dibuat oleh: Rafi (Database Engineer & Integration Support)
--
-- Revisi 4: Disinkronkan PERSIS dengan gambar "ERD Konseptual
-- Eventix" dari dokumen PRD. Tabel `kategori` DIHAPUS karena
-- tidak ada di diagram tersebut -- `institusi` terhubung langsung
-- ke `seminar`, bukan lewat kategori.
--
-- Penanda di komentar setiap tabel:
--   [ERD]      = kolom/tabel ini ADA di gambar ERD konseptual
--   [TAMBAHAN] = kolom ini TIDAK digambar di ERD konseptual, tapi
--                ditambahkan sebagai kebutuhan implementasi fisik
--                yang wajar (timestamp, kolom pendukung praktis).
--                Kalau tidak setuju, kolom ini aman dihapus.
-- =========================================================
-- Cara pakai:
--   1. Buka MySQL Workbench / phpMyAdmin / terminal mysql
--   2. Jalankan SELURUH isi file ini sekali jalan (Run Script)
--
-- CATATAN: kalau database db_pengelolaan_seminar sudah ada dari
-- revisi sebelumnya, DROP dulu supaya bersih:
--   DROP DATABASE IF EXISTS db_pengelolaan_seminar;
-- =========================================================

CREATE DATABASE IF NOT EXISTS db_pengelolaan_seminar
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE db_pengelolaan_seminar;

-- ---------------------------------------------------------
-- Tabel 1: institusi                                   [ERD]
-- Atribut ERD: nama, alamat
-- ---------------------------------------------------------
CREATE TABLE institusi (
    id_institusi INT AUTO_INCREMENT PRIMARY KEY,        -- [ERD] id_institusi
    nama         VARCHAR(150) NOT NULL,                 -- [ERD] nama
    alamat       VARCHAR(255)                           -- [ERD] alamat
);

-- ---------------------------------------------------------
-- Tabel 2: user                                        [ERD]
-- Atribut ERD: id_institusi (FK), nama, email (UNIQUE),
--              password_hash, role
-- ---------------------------------------------------------
CREATE TABLE user (
    id_user        INT AUTO_INCREMENT PRIMARY KEY,
    id_institusi   INT NULL,                            -- [ERD] FK id_institusi
    nama           VARCHAR(100) NOT NULL,                -- [ERD] nama
    email          VARCHAR(100) NOT NULL UNIQUE,          -- [ERD] email UNIQUE
    password_hash  VARCHAR(255) NOT NULL,                 -- [ERD] password_hash
    role           ENUM('PESERTA', 'PANITIA', 'ADMIN') NOT NULL, -- [ERD] role
    no_telepon     VARCHAR(20),                           -- [TAMBAHAN]
    tanggal_daftar TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    -- [TAMBAHAN]
    FOREIGN KEY (id_institusi) REFERENCES institusi(id_institusi) ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Tabel 3: seminar                                     [ERD]
-- Atribut ERD: id_institusi (FK), id_panitia (FK), judul,
--              tanggal_mulai, tanggal_selesai, kuota, harga, status
-- Relasi ERD: institusi 1:N seminar, user(panitia) 1:N seminar
-- ---------------------------------------------------------
CREATE TABLE seminar (
    id_seminar      INT AUTO_INCREMENT PRIMARY KEY,
    id_institusi    INT NOT NULL,                        -- [ERD] FK id_institusi
    id_panitia      INT NOT NULL,                         -- [ERD] FK id_panitia
    judul           VARCHAR(150) NOT NULL,                -- [ERD] judul
    deskripsi       TEXT,                                 -- [TAMBAHAN]
    tanggal_mulai   DATETIME NOT NULL,                    -- [ERD] tanggal_mulai
    tanggal_selesai DATETIME NOT NULL,                    -- [ERD] tanggal_selesai
    lokasi          VARCHAR(150),                         -- [TAMBAHAN]
    kuota           INT NOT NULL,                         -- [ERD] kuota
    kuota_terisi    INT NOT NULL DEFAULT 0,                -- [TAMBAHAN] -- hitung jumlah TIKET (detail_pendaftaran), bukan jumlah transaksi
    harga           DECIMAL(12, 2) NOT NULL DEFAULT 0,     -- [ERD] harga
    status          ENUM('DIBUKA', 'DITUTUP', 'SELESAI', 'CANCELLED') DEFAULT 'DIBUKA', -- [ERD] status
    dibuat_pada     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- [TAMBAHAN]
    FOREIGN KEY (id_institusi) REFERENCES institusi(id_institusi) ON DELETE RESTRICT,
    FOREIGN KEY (id_panitia) REFERENCES user(id_user) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 4: pendaftaran (header transaksi)               [ERD]
-- Atribut ERD: id_pemesan (FK), id_seminar (FK),
--              kode_transaksi, status, total
-- Relasi ERD: user(peserta) 1:N pendaftaran, seminar 1:N pendaftaran
-- ---------------------------------------------------------
CREATE TABLE pendaftaran (
    id_pendaftaran INT AUTO_INCREMENT PRIMARY KEY,
    id_pemesan     INT NOT NULL,                          -- [ERD] FK id_pemesan -> user
    id_seminar     INT NOT NULL,                           -- [ERD] FK id_seminar
    kode_transaksi VARCHAR(50) NOT NULL UNIQUE,             -- [ERD] kode_transaksi
    status         ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING', -- [ERD] status
    total          DECIMAL(12, 2) NOT NULL DEFAULT 0,       -- [ERD] total
    tanggal_daftar TIMESTAMP DEFAULT CURRENT_TIMESTAMP,     -- [TAMBAHAN]
    FOREIGN KEY (id_pemesan) REFERENCES user(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_seminar) REFERENCES seminar(id_seminar) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 5: detail_pendaftaran (tiket per peserta)        [ERD]
-- Atribut ERD: id_pendaftaran (FK), nama_peserta,
--              email_peserta, kode_booking (UNIQUE), qr_data
-- Relasi ERD: pendaftaran 1:1..4 detail_pendaftaran
-- (1 transaksi = 1 sampai 4 tiket; TIDAK UNIQUE di id_pendaftaran)
-- ---------------------------------------------------------
CREATE TABLE detail_pendaftaran (
    id_detail      INT AUTO_INCREMENT PRIMARY KEY,
    id_pendaftaran INT NOT NULL,                           -- [ERD] FK id_pendaftaran (1:N, bukan 1:1)
    nama_peserta   VARCHAR(150) NOT NULL,                  -- [ERD] nama_peserta
    email_peserta  VARCHAR(150) NOT NULL,                  -- [ERD] email_peserta
    no_telepon     VARCHAR(20),                            -- [TAMBAHAN]
    kode_booking   VARCHAR(50) NOT NULL UNIQUE,             -- [ERD] kode_booking UNIQUE
    qr_data        TEXT,                                   -- [ERD] qr_data
    FOREIGN KEY (id_pendaftaran) REFERENCES pendaftaran(id_pendaftaran) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 6: presensi                                     [ERD]
-- Atribut ERD: id_detail (FK UNIQUE), status, waktu
-- Relasi ERD: detail_pendaftaran 1:0..1 presensi
-- ---------------------------------------------------------
CREATE TABLE presensi (
    id_presensi INT AUTO_INCREMENT PRIMARY KEY,
    id_detail   INT NOT NULL UNIQUE,                       -- [ERD] FK id_detail (bukan id_pendaftaran)
    status      ENUM('HADIR', 'TIDAK_HADIR') NOT NULL DEFAULT 'TIDAK_HADIR', -- [ERD] status
    waktu       TIMESTAMP NULL,                            -- [ERD] waktu
    FOREIGN KEY (id_detail) REFERENCES detail_pendaftaran(id_detail) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 7: sertifikat                                   [ERD]
-- Atribut ERD: id_detail (FK UNIQUE), nomor (UNIQUE), versi, file_path
-- Relasi ERD: detail_pendaftaran 1:0..1 sertifikat
-- ---------------------------------------------------------
CREATE TABLE sertifikat (
    id_sertifikat INT AUTO_INCREMENT PRIMARY KEY,
    id_detail     INT NOT NULL UNIQUE,                     -- [ERD] FK id_detail (bukan id_pendaftaran)
    nomor         VARCHAR(50) NOT NULL UNIQUE,              -- [ERD] nomor UNIQUE
    versi         INT NOT NULL DEFAULT 1,                   -- [ERD] versi (untuk reissue sertifikat)
    file_path     VARCHAR(255),                             -- [ERD] file_path (lokasi file PDF)
    tanggal_terbit TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- [TAMBAHAN]
    FOREIGN KEY (id_detail) REFERENCES detail_pendaftaran(id_detail) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 8: pembayaran                                   [ERD]
-- Atribut ERD: id_pendaftaran (FK), metode, status, nominal
-- Relasi ERD: pendaftaran 1:0..1 pembayaran
-- (UNIQUE di id_pendaftaran -- maksimal 1 pembayaran per transaksi)
-- ---------------------------------------------------------
CREATE TABLE pembayaran (
    id_pembayaran INT AUTO_INCREMENT PRIMARY KEY,
    id_pendaftaran INT NOT NULL UNIQUE,                     -- [ERD] FK id_pendaftaran, UNIQUE sesuai relasi 1:0..1
    metode        VARCHAR(50),                              -- [ERD] metode
    status        ENUM('PENDING', 'BERHASIL', 'GAGAL') DEFAULT 'PENDING', -- [ERD] status
    nominal       DECIMAL(12, 2) NOT NULL DEFAULT 0,         -- [ERD] nominal
    tanggal_bayar TIMESTAMP NULL,                            -- [TAMBAHAN]
    FOREIGN KEY (id_pendaftaran) REFERENCES pendaftaran(id_pendaftaran) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 9: audit_log                                    [ERD]
-- Atribut ERD: id_user (FK), aksi, entitas, waktu
-- Relasi ERD: user 1:N audit_log
-- id_user NULLABLE: aksi otomatis oleh sistem tidak selalu
-- punya pelaku user spesifik.
-- ---------------------------------------------------------
CREATE TABLE audit_log (
    id_log  INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NULL,                                       -- [ERD] FK id_user
    aksi    VARCHAR(100) NOT NULL,                           -- [ERD] aksi
    entitas VARCHAR(50),                                     -- [ERD] entitas
    waktu   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,              -- [ERD] waktu
    FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Index tambahan untuk query yang sering dipakai (laporan, dashboard)
-- ---------------------------------------------------------
CREATE INDEX idx_user_institusi ON user(id_institusi);
CREATE INDEX idx_seminar_institusi ON seminar(id_institusi);
CREATE INDEX idx_seminar_panitia ON seminar(id_panitia);
CREATE INDEX idx_pendaftaran_pemesan ON pendaftaran(id_pemesan);
CREATE INDEX idx_pendaftaran_seminar ON pendaftaran(id_seminar);
CREATE INDEX idx_detail_pendaftaran ON detail_pendaftaran(id_pendaftaran);
CREATE INDEX idx_audit_log_user ON audit_log(id_user);

-- Selesai. Lanjut jalankan 02_dummy_data.sql untuk mengisi data contoh.
