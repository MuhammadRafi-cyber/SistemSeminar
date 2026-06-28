-- =========================================================
-- SCHEMA DATABASE: Sistem Informasi Pengelolaan Seminar (Eventix)
-- Versi 5 — Sinkronisasi penuh dengan PRD v1.0
-- =========================================================
-- Cara pakai:
--   DROP DATABASE IF EXISTS db_pengelolaan_seminar;
--   Lalu jalankan file ini seluruhnya.
-- =========================================================

CREATE DATABASE IF NOT EXISTS db_pengelolaan_seminar
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE db_pengelolaan_seminar;

-- ---------------------------------------------------------
-- Tabel 1: institusi
-- Tambahan v5: logo_path
-- ---------------------------------------------------------
CREATE TABLE institusi (
    id_institusi INT AUTO_INCREMENT PRIMARY KEY,
    nama         VARCHAR(150) NOT NULL,
    alamat       VARCHAR(255),
    logo_path    VARCHAR(255)                          -- [TAMBAHAN] path file logo
);

-- ---------------------------------------------------------
-- Tabel 2: kategori
-- Dikembalikan di v5 (ada di PRD tabel 9)
-- ---------------------------------------------------------
CREATE TABLE kategori (
    id_kategori   INT AUTO_INCREMENT PRIMARY KEY,
    nama_kategori VARCHAR(100) NOT NULL UNIQUE
);

-- ---------------------------------------------------------
-- Tabel 3: user
-- Tambahan v5: username (disimpan, tidak dipakai untuk login)
-- ---------------------------------------------------------
CREATE TABLE user (
    id_user        INT AUTO_INCREMENT PRIMARY KEY,
    id_institusi   INT NULL,
    nama           VARCHAR(100) NOT NULL,
    username       VARCHAR(50) UNIQUE,                 -- [TAMBAHAN v5] tidak dipakai login
    email          VARCHAR(100) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           ENUM('PESERTA','PANITIA','ADMIN') NOT NULL,
    no_telepon     VARCHAR(20),
    tanggal_daftar TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_institusi) REFERENCES institusi(id_institusi) ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Tabel 4: seminar
-- Tambahan v5: id_kategori, pembicara, mode, banner_path
-- ---------------------------------------------------------
CREATE TABLE seminar (
    id_seminar      INT AUTO_INCREMENT PRIMARY KEY,
    id_institusi    INT NOT NULL,
    id_panitia      INT NOT NULL,
    id_kategori     INT NULL,                          -- [TAMBAHAN v5] FK kategori
    judul           VARCHAR(150) NOT NULL,
    deskripsi       TEXT,
    pembicara       VARCHAR(150),                      -- [TAMBAHAN v5]
    tanggal_mulai   DATETIME NOT NULL,
    tanggal_selesai DATETIME NOT NULL,
    mode            ENUM('ONLINE','OFFLINE') DEFAULT 'OFFLINE', -- [TAMBAHAN v5]
    lokasi          VARCHAR(255),
    kuota           INT NOT NULL,
    kuota_terisi    INT NOT NULL DEFAULT 0,
    harga           DECIMAL(12,2) NOT NULL DEFAULT 0,
    status          ENUM('DIBUKA','DITUTUP','SELESAI','CANCELLED') DEFAULT 'DIBUKA',
    banner_path     VARCHAR(255),                      -- [TAMBAHAN v5]
    dibuat_pada     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_institusi) REFERENCES institusi(id_institusi) ON DELETE RESTRICT,
    FOREIGN KEY (id_panitia)   REFERENCES user(id_user)           ON DELETE CASCADE,
    FOREIGN KEY (id_kategori)  REFERENCES kategori(id_kategori)   ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Tabel 5: pendaftaran (header transaksi)
-- Tidak ada perubahan kolom, hanya ENUM status tetap 3 nilai
-- ---------------------------------------------------------
CREATE TABLE pendaftaran (
    id_pendaftaran INT AUTO_INCREMENT PRIMARY KEY,
    id_pemesan     INT NOT NULL,
    id_seminar     INT NOT NULL,
    kode_transaksi VARCHAR(50) NOT NULL UNIQUE,
    status         ENUM('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
    total          DECIMAL(12,2) NOT NULL DEFAULT 0,
    tanggal_daftar TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pemesan)  REFERENCES user(id_user)              ON DELETE CASCADE,
    FOREIGN KEY (id_seminar)  REFERENCES seminar(id_seminar)        ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 6: detail_pendaftaran (tiket per peserta)
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
-- Tabel 7: pembayaran
-- Tambahan v5: status_refund — alur refund via kolom terpisah
--   status        : PENDING → BERHASIL | GAGAL (status pembayaran itu sendiri)
--   status_refund : NULL → DIMINTA → DIPROSES → SELESAI (alur refund jika ada)
-- UNIQUE(id_pendaftaran): 1 pembayaran per transaksi
-- ---------------------------------------------------------
CREATE TABLE pembayaran (
    id_pembayaran  INT AUTO_INCREMENT PRIMARY KEY,
    id_pendaftaran INT NOT NULL UNIQUE,
    metode         VARCHAR(50),
    status         ENUM('PENDING','BERHASIL','GAGAL') DEFAULT 'PENDING',
    nominal        DECIMAL(12,2) NOT NULL DEFAULT 0,
    waktu_bayar    TIMESTAMP NULL,
    status_refund  ENUM('TIDAK_ADA','DIMINTA','DIPROSES','SELESAI') DEFAULT 'TIDAK_ADA',
    FOREIGN KEY (id_pendaftaran) REFERENCES pendaftaran(id_pendaftaran) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 8: presensi
-- Tambahan v5: dicatat_oleh (FK user → Panitia yang scan) — PRD tabel 9
-- ---------------------------------------------------------
CREATE TABLE presensi (
    id_presensi  INT AUTO_INCREMENT PRIMARY KEY,
    id_detail    INT NOT NULL UNIQUE,
    status       ENUM('HADIR','TIDAK_HADIR') NOT NULL DEFAULT 'TIDAK_HADIR',
    waktu        TIMESTAMP NULL,
    dicatat_oleh INT NULL,                            -- [TAMBAHAN v5] FK user (Panitia)
    FOREIGN KEY (id_detail)    REFERENCES detail_pendaftaran(id_detail) ON DELETE CASCADE,
    FOREIGN KEY (dicatat_oleh) REFERENCES user(id_user)                 ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Tabel 9: sertifikat
-- Tambahan v5: nomor_sertifikat (rename dari nomor agar sesuai PRD tabel 9)
-- ---------------------------------------------------------
CREATE TABLE sertifikat (
    id_sertifikat   INT AUTO_INCREMENT PRIMARY KEY,
    id_detail       INT NOT NULL UNIQUE,
    nomor_sertifikat VARCHAR(50) NOT NULL UNIQUE,     -- PRD: nomor_sertifikat
    tanggal_terbit  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    versi           INT NOT NULL DEFAULT 1,
    file_path       VARCHAR(255),
    FOREIGN KEY (id_detail) REFERENCES detail_pendaftaran(id_detail) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Tabel 10: audit_log
-- Tambahan v5: id_entitas, keterangan — NFR-13
-- ---------------------------------------------------------
CREATE TABLE audit_log (
    id_log     INT AUTO_INCREMENT PRIMARY KEY,
    id_user    INT NULL,
    aksi       VARCHAR(100) NOT NULL,
    entitas    VARCHAR(50),
    id_entitas INT NULL,                              -- [TAMBAHAN v5] ID data yang terdampak
    keterangan TEXT,                                  -- [TAMBAHAN v5] detail perubahan
    waktu      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE SET NULL
);

-- ---------------------------------------------------------
-- Index untuk query yang sering dipakai
-- ---------------------------------------------------------
CREATE INDEX idx_user_institusi         ON user(id_institusi);
CREATE INDEX idx_seminar_institusi      ON seminar(id_institusi);
CREATE INDEX idx_seminar_panitia        ON seminar(id_panitia);
CREATE INDEX idx_seminar_kategori       ON seminar(id_kategori);
CREATE INDEX idx_pendaftaran_pemesan    ON pendaftaran(id_pemesan);
CREATE INDEX idx_pendaftaran_seminar    ON pendaftaran(id_seminar);
CREATE INDEX idx_detail_pendaftaran     ON detail_pendaftaran(id_pendaftaran);
CREATE INDEX idx_audit_log_user         ON audit_log(id_user);
CREATE INDEX idx_audit_log_entitas      ON audit_log(entitas, id_entitas);
