-- ============================================================
--  db_pengelolaan_seminar — Schema Database
--  Sesuai file 03_query_crud.sql dari repo tim (GitHub Rafi)
--  Eventix | UAS PBO | UPNVJ 2026
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_pengelolaan_seminar
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_pengelolaan_seminar;

-- ============================================================
--  TABEL USER
--  Dipakai di query A1, A2, A3
-- ============================================================
CREATE TABLE IF NOT EXISTS user (
    id_user        INT          NOT NULL AUTO_INCREMENT,
    nama           VARCHAR(150) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    role           ENUM('PESERTA','PANITIA','ADMIN') NOT NULL DEFAULT 'PESERTA',
    no_telepon     VARCHAR(20),
    tanggal_daftar TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_user)
) ENGINE=InnoDB;

-- ============================================================
--  TABEL SEMINAR
--  Dipakai di query B1–B5, C3
-- ============================================================
CREATE TABLE IF NOT EXISTS seminar (
    id_seminar          INT           NOT NULL AUTO_INCREMENT,
    judul               VARCHAR(255)  NOT NULL,
    deskripsi           TEXT,
    tanggal_pelaksanaan DATE          NOT NULL,
    waktu_mulai         TIME,
    waktu_selesai       TIME,
    lokasi              VARCHAR(255)  NOT NULL,
    kuota               INT           NOT NULL DEFAULT 0,
    kuota_terisi        INT           NOT NULL DEFAULT 0,
    status              ENUM('DIBUKA','DITUTUP','SELESAI','CANCELLED') NOT NULL DEFAULT 'DIBUKA',
    id_panitia          INT           NOT NULL,
    dibuat_pada         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_seminar),
    CONSTRAINT fk_seminar_panitia FOREIGN KEY (id_panitia) REFERENCES user(id_user)
) ENGINE=InnoDB;

-- ============================================================
--  TABEL PENDAFTARAN
--  Dipakai di query C1–C5
--  Kolom: id_user, id_seminar, tanggal_daftar, status_pendaftaran
-- ============================================================
CREATE TABLE IF NOT EXISTS pendaftaran (
    id_pendaftaran    INT       NOT NULL AUTO_INCREMENT,
    id_user           INT       NOT NULL,
    id_seminar        INT       NOT NULL,
    tanggal_daftar    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status_pendaftaran ENUM('PENDING','CONFIRMED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (id_pendaftaran),
    CONSTRAINT fk_pendaftaran_user    FOREIGN KEY (id_user)    REFERENCES user(id_user),
    CONSTRAINT fk_pendaftaran_seminar FOREIGN KEY (id_seminar) REFERENCES seminar(id_seminar)
) ENGINE=InnoDB;

-- ============================================================
--  TABEL PRESENSI
--  Dipakai di query D1, D2
--  FK ke pendaftaran.id_pendaftaran (bukan ke detail)
--  UNIQUE(id_pendaftaran) → ON DUPLICATE KEY UPDATE bisa berjalan
-- ============================================================
CREATE TABLE IF NOT EXISTS presensi (
    id_presensi    INT  NOT NULL AUTO_INCREMENT,
    id_pendaftaran INT  NOT NULL UNIQUE,
    status_hadir   ENUM('HADIR','TIDAK_HADIR') NOT NULL DEFAULT 'TIDAK_HADIR',
    waktu_presensi TIMESTAMP NULL,
    PRIMARY KEY (id_presensi),
    CONSTRAINT fk_presensi_pendaftaran FOREIGN KEY (id_pendaftaran) REFERENCES pendaftaran(id_pendaftaran)
) ENGINE=InnoDB;

-- ============================================================
--  TABEL SERTIFIKAT
--  Dipakai di query E1, E2
--  FK ke pendaftaran.id_pendaftaran
--  Kolom: kode_sertifikat (bukan nomor_sertifikat)
-- ============================================================
CREATE TABLE IF NOT EXISTS sertifikat (
    id_sertifikat  INT         NOT NULL AUTO_INCREMENT,
    id_pendaftaran INT         NOT NULL UNIQUE,
    kode_sertifikat VARCHAR(30) NOT NULL UNIQUE,
    tanggal_terbit  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_sertifikat),
    CONSTRAINT fk_sertifikat_pendaftaran FOREIGN KEY (id_pendaftaran) REFERENCES pendaftaran(id_pendaftaran)
) ENGINE=InnoDB;

-- ============================================================
--  DATA SEED — buat akun panitia untuk testing
--  Setelah import, registrasi ulang via aplikasi untuk password hash yang benar.
--  Atau: gunakan menu Registrasi di aplikasi, lalu ubah role di phpMyAdmin.
-- ============================================================
-- (Tidak ada seed data — semua akun dibuat via menu Registrasi di aplikasi)
