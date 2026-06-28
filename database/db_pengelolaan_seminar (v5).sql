-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 28, 2026 at 03:44 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_pengelolaan_seminar`
--

-- --------------------------------------------------------

--
-- Table structure for table `audit_log`
--

CREATE TABLE `audit_log` (
  `id_log` int(11) NOT NULL,
  `id_user` int(11) DEFAULT NULL,
  `aksi` varchar(100) NOT NULL,
  `entitas` varchar(50) DEFAULT NULL,
  `id_entitas` int(11) DEFAULT NULL,
  `keterangan` text DEFAULT NULL,
  `waktu` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `audit_log`
--

INSERT INTO `audit_log` (`id_log`, `id_user`, `aksi`, `entitas`, `id_entitas`, `keterangan`, `waktu`) VALUES
(1, 1, 'REGISTRASI', 'user', 1, 'Registrasi baru: budi.panitia@kampus.ac.id', '2026-06-28 13:31:22'),
(2, 2, 'REGISTRASI', 'user', 2, 'Registrasi baru: sari.panitia@kampus.ac.id', '2026-06-28 13:31:57'),
(3, 3, 'REGISTRASI', 'user', 3, 'Registrasi baru: andi.peserta@kampus.ac.id', '2026-06-28 13:32:32'),
(4, 4, 'REGISTRASI', 'user', 4, 'Registrasi baru: dewi.peserta@sekolah.ac.id', '2026-06-28 13:33:24'),
(5, 5, 'REGISTRASI', 'user', 5, 'Registrasi baru: fajar.peserta@kampus.ac.id', '2026-06-28 13:34:24'),
(6, 6, 'REGISTRASI', 'user', 6, 'Registrasi baru: admin@eventix.com', '2026-06-28 13:35:00'),
(7, 5, 'LOGIN', 'user', 5, 'Login: fajar.peserta@kampus.ac.id', '2026-06-28 13:35:27'),
(8, 5, 'UPDATE_PROFIL', 'user', 5, 'Update profil', '2026-06-28 13:35:35'),
(9, 1, 'TAMBAH_SEMINAR', 'seminar', 1, 'Membuat seminar OOP', '2026-06-28 13:43:46'),
(10, 2, 'TAMBAH_SEMINAR', 'seminar', 2, 'Membuat seminar JDBC', '2026-06-28 13:43:46'),
(11, 1, 'CATAT_PRESENSI', 'presensi', 1, 'Presensi HADIR untuk tiket BOOK-OOP-0001', '2026-06-28 13:43:46'),
(12, 1, 'CATAT_PRESENSI', 'presensi', 2, 'Presensi HADIR untuk tiket BOOK-OOP-0002', '2026-06-28 13:43:46'),
(13, NULL, 'TERBITKAN_SERTIFIKAT', 'sertifikat', 1, 'Sertifikat diterbitkan untuk tiket BOOK-OOP-0001', '2026-06-28 13:43:46'),
(14, NULL, 'TERBITKAN_SERTIFIKAT', 'sertifikat', 2, 'Sertifikat diterbitkan untuk tiket BOOK-OOP-0002', '2026-06-28 13:43:46');

-- --------------------------------------------------------

--
-- Table structure for table `detail_pendaftaran`
--

CREATE TABLE `detail_pendaftaran` (
  `id_detail` int(11) NOT NULL,
  `id_pendaftaran` int(11) NOT NULL,
  `nama_peserta` varchar(150) NOT NULL,
  `email_peserta` varchar(150) NOT NULL,
  `no_telepon` varchar(20) DEFAULT NULL,
  `kode_booking` varchar(50) NOT NULL,
  `qr_data` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `detail_pendaftaran`
--

INSERT INTO `detail_pendaftaran` (`id_detail`, `id_pendaftaran`, `nama_peserta`, `email_peserta`, `no_telepon`, `kode_booking`, `qr_data`) VALUES
(1, 1, 'Andi Pratama', 'andi.peserta@kampus.ac.id', '081300000001', 'BOOK-OOP-0001', 'QR-OOP-0001'),
(2, 1, 'Rina Permata', 'rina.teman.andi@gmail.com', '081300000099', 'BOOK-OOP-0002', 'QR-OOP-0002'),
(3, 2, 'Dewi Lestari', 'dewi.peserta@kampus.ac.id', '081300000002', 'BOOK-OOP-0003', 'QR-OOP-0003'),
(4, 3, 'Fajar Nugraha', 'fajar.peserta@kampus.ac.id', '081300000003', 'BOOK-JDBC-001', 'QR-JDBC-001');

-- --------------------------------------------------------

--
-- Table structure for table `institusi`
--

CREATE TABLE `institusi` (
  `id_institusi` int(11) NOT NULL,
  `nama` varchar(150) NOT NULL,
  `alamat` varchar(255) DEFAULT NULL,
  `logo_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `institusi`
--

INSERT INTO `institusi` (`id_institusi`, `nama`, `alamat`, `logo_path`) VALUES
(1, 'Universitas Pembangunan Nasional Veteran Jakarta', 'Jl. RS Fatmawati, Pondok Labu, Jakarta Selatan', NULL),
(2, 'SMA Negeri 1 Depok', 'Jl. Nusantara Raya, Depok', NULL),
(3, 'PT Teknologi Nusantara', 'Jl. Sudirman Kav. 25, Jakarta Pusat', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `kategori`
--

CREATE TABLE `kategori` (
  `id_kategori` int(11) NOT NULL,
  `nama_kategori` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `kategori`
--

INSERT INTO `kategori` (`id_kategori`, `nama_kategori`) VALUES
(3, 'Bisnis & Kewirausahaan'),
(2, 'Pengembangan Diri & Soft Skill'),
(1, 'Teknologi & Pemrograman');

-- --------------------------------------------------------

--
-- Table structure for table `pembayaran`
--

CREATE TABLE `pembayaran` (
  `id_pembayaran` int(11) NOT NULL,
  `id_pendaftaran` int(11) NOT NULL,
  `metode` varchar(50) DEFAULT NULL,
  `status` enum('PENDING','BERHASIL','GAGAL') DEFAULT 'PENDING',
  `nominal` decimal(12,2) NOT NULL DEFAULT 0.00,
  `waktu_bayar` timestamp NULL DEFAULT NULL,
  `status_refund` enum('TIDAK_ADA','DIMINTA','DIPROSES','SELESAI') DEFAULT 'TIDAK_ADA'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pembayaran`
--

INSERT INTO `pembayaran` (`id_pembayaran`, `id_pendaftaran`, `metode`, `status`, `nominal`, `waktu_bayar`, `status_refund`) VALUES
(1, 1, 'Transfer Bank', 'BERHASIL', 100000.00, '2026-07-01 03:00:00', 'TIDAK_ADA'),
(2, 2, 'Transfer Bank', 'PENDING', 50000.00, NULL, 'TIDAK_ADA'),
(3, 3, 'E-Wallet', 'BERHASIL', 75000.00, '2026-07-02 04:30:00', 'TIDAK_ADA');

-- --------------------------------------------------------

--
-- Table structure for table `pendaftaran`
--

CREATE TABLE `pendaftaran` (
  `id_pendaftaran` int(11) NOT NULL,
  `id_pemesan` int(11) NOT NULL,
  `id_seminar` int(11) NOT NULL,
  `kode_transaksi` varchar(50) NOT NULL,
  `status` enum('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
  `total` decimal(12,2) NOT NULL DEFAULT 0.00,
  `tanggal_daftar` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pendaftaran`
--

INSERT INTO `pendaftaran` (`id_pendaftaran`, `id_pemesan`, `id_seminar`, `kode_transaksi`, `status`, `total`, `tanggal_daftar`) VALUES
(1, 3, 1, 'TRX-20260701-OOP01', 'CONFIRMED', 100000.00, '2026-06-28 13:43:46'),
(2, 4, 1, 'TRX-20260701-OOP02', 'PENDING', 50000.00, '2026-06-28 13:43:46'),
(3, 5, 2, 'TRX-20260702-JDBC1', 'CONFIRMED', 75000.00, '2026-06-28 13:43:46');

-- --------------------------------------------------------

--
-- Table structure for table `presensi`
--

CREATE TABLE `presensi` (
  `id_presensi` int(11) NOT NULL,
  `id_detail` int(11) NOT NULL,
  `status` enum('HADIR','TIDAK_HADIR') NOT NULL DEFAULT 'TIDAK_HADIR',
  `waktu` timestamp NULL DEFAULT NULL,
  `dicatat_oleh` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `presensi`
--

INSERT INTO `presensi` (`id_presensi`, `id_detail`, `status`, `waktu`, `dicatat_oleh`) VALUES
(1, 1, 'HADIR', '2026-07-10 01:50:00', 1),
(2, 2, 'HADIR', '2026-07-10 01:52:00', 1),
(3, 3, 'TIDAK_HADIR', NULL, NULL),
(4, 4, 'TIDAK_HADIR', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `seminar`
--

CREATE TABLE `seminar` (
  `id_seminar` int(11) NOT NULL,
  `id_institusi` int(11) NOT NULL,
  `id_panitia` int(11) NOT NULL,
  `id_kategori` int(11) DEFAULT NULL,
  `judul` varchar(150) NOT NULL,
  `deskripsi` text DEFAULT NULL,
  `pembicara` varchar(150) DEFAULT NULL,
  `tanggal_mulai` datetime NOT NULL,
  `tanggal_selesai` datetime NOT NULL,
  `mode` enum('ONLINE','OFFLINE') DEFAULT 'OFFLINE',
  `lokasi` varchar(255) DEFAULT NULL,
  `kuota` int(11) NOT NULL,
  `kuota_terisi` int(11) NOT NULL DEFAULT 0,
  `harga` decimal(12,2) NOT NULL DEFAULT 0.00,
  `status` enum('DIBUKA','DITUTUP','SELESAI','CANCELLED') DEFAULT 'DIBUKA',
  `banner_path` varchar(255) DEFAULT NULL,
  `dibuat_pada` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `seminar`
--

INSERT INTO `seminar` (`id_seminar`, `id_institusi`, `id_panitia`, `id_kategori`, `judul`, `deskripsi`, `pembicara`, `tanggal_mulai`, `tanggal_selesai`, `mode`, `lokasi`, `kuota`, `kuota_terisi`, `harga`, `status`, `banner_path`, `dibuat_pada`) VALUES
(1, 1, 1, 1, 'Seminar Pemrograman Berorientasi Objek', 'Membahas konsep dasar OOP dalam Java', 'Dr. Budi Santoso', '2026-07-10 09:00:00', '2026-07-10 12:00:00', 'OFFLINE', 'Aula A Kampus', 50, 3, 50000.00, 'DIBUKA', NULL, '2026-06-28 13:43:46'),
(2, 1, 2, 1, 'Workshop Basis Data dan JDBC', 'Praktik langsung koneksi Java ke MySQL', 'Sari Wulandari, M.Kom.', '2026-07-17 13:00:00', '2026-07-17 16:00:00', 'OFFLINE', 'Lab Komputer 2', 30, 1, 75000.00, 'DIBUKA', NULL, '2026-06-28 13:43:46'),
(3, 1, 1, 2, 'Workshop Public Speaking untuk Mahasiswa', 'Melatih kepercayaan diri berbicara di depan umum', 'Motivator Berpengalaman', '2026-07-24 09:00:00', '2026-07-24 11:30:00', 'OFFLINE', 'Aula B Kampus', 40, 0, 60000.00, 'DIBUKA', NULL, '2026-06-28 13:43:46');

-- --------------------------------------------------------

--
-- Table structure for table `sertifikat`
--

CREATE TABLE `sertifikat` (
  `id_sertifikat` int(11) NOT NULL,
  `id_detail` int(11) NOT NULL,
  `nomor_sertifikat` varchar(50) NOT NULL,
  `tanggal_terbit` timestamp NOT NULL DEFAULT current_timestamp(),
  `versi` int(11) NOT NULL DEFAULT 1,
  `file_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `sertifikat`
--

INSERT INTO `sertifikat` (`id_sertifikat`, `id_detail`, `nomor_sertifikat`, `tanggal_terbit`, `versi`, `file_path`) VALUES
(1, 1, 'CERT-2026-OOP-0001', '2026-06-28 13:43:46', 1, '/sertifikat/CERT-2026-OOP-0001.pdf'),
(2, 2, 'CERT-2026-OOP-0002', '2026-06-28 13:43:46', 1, '/sertifikat/CERT-2026-OOP-0002.pdf');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int(11) NOT NULL,
  `id_institusi` int(11) DEFAULT NULL,
  `nama` varchar(100) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('PESERTA','PANITIA','ADMIN') NOT NULL,
  `no_telepon` varchar(20) DEFAULT NULL,
  `tanggal_daftar` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `id_institusi`, `nama`, `username`, `email`, `password_hash`, `role`, `no_telepon`, `tanggal_daftar`) VALUES
(1, 1, 'Budi Santoso', 'budi_pan', 'budi.panitia@kampus.ac.id', 'NkBbsXm9+El24YmYpLgKzA==:5a08a2ab3b745ccc2f3bb9e71efa880dc1de68cb7d9e0f68cea48b7426e5ff3e', 'PESERTA', '081200000001', '2026-06-28 13:31:22'),
(2, 1, 'Sari Wulandari', 'sari_pan', 'sari.panitia@kampus.ac.id', 'U3zERWQdcgzpwnuLzyPsiA==:69575e06f4464b3f81f520e131c34e7f56aacd7599d5ea15e5cf0a65bcbf2cf8', 'PESERTA', '081200000002', '2026-06-28 13:31:57'),
(3, 1, 'Andi Pratama', 'andi_pes', 'andi.peserta@kampus.ac.id', 'UmLG1M+v2pF868hKvDSFuw==:4590ac62a44919f67c492b50172d1921c283add2cdc7030bd8a31ef5cd57b094', 'PESERTA', '081300000001', '2026-06-28 13:32:32'),
(4, 2, 'Dewi Lestari', 'dewi_pes', 'dewi.peserta@sekolah.ac.id', 'c9EY7EMF5PSQ57Apq6Pxhw==:03d8dc6c8ed5a7ec99d9e342bee6fab04e1ef40c42060dbb060f744a5090a3cc', 'PESERTA', '081300000002', '2026-06-28 13:33:24'),
(5, 3, 'Fajar Nugraha', 'fajar_pes', 'fajar.peserta@kampus.ac.id', '0mvFFD8Yfviw+MtQ0ypNVg==:e43558dfafb4fc06bc7aed8b1a449bcedaa6b32ec62715ed87c27d463c9e1221', 'PESERTA', '081300000003', '2026-06-28 13:34:24'),
(6, NULL, 'Super Admin', 'superadmin', 'admin@eventix.com', 'cwSY/JLRrpceOE2Igpy+/A==:ee18ff4d58ac8dcedf919e373854658971591dd7ee50c22eb864db7fe8575298', 'PESERTA', '089900000000', '2026-06-28 13:35:00');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD PRIMARY KEY (`id_log`),
  ADD KEY `idx_audit_log_user` (`id_user`),
  ADD KEY `idx_audit_log_entitas` (`entitas`,`id_entitas`);

--
-- Indexes for table `detail_pendaftaran`
--
ALTER TABLE `detail_pendaftaran`
  ADD PRIMARY KEY (`id_detail`),
  ADD UNIQUE KEY `kode_booking` (`kode_booking`),
  ADD KEY `idx_detail_pendaftaran` (`id_pendaftaran`);

--
-- Indexes for table `institusi`
--
ALTER TABLE `institusi`
  ADD PRIMARY KEY (`id_institusi`);

--
-- Indexes for table `kategori`
--
ALTER TABLE `kategori`
  ADD PRIMARY KEY (`id_kategori`),
  ADD UNIQUE KEY `nama_kategori` (`nama_kategori`);

--
-- Indexes for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD PRIMARY KEY (`id_pembayaran`),
  ADD UNIQUE KEY `id_pendaftaran` (`id_pendaftaran`);

--
-- Indexes for table `pendaftaran`
--
ALTER TABLE `pendaftaran`
  ADD PRIMARY KEY (`id_pendaftaran`),
  ADD UNIQUE KEY `kode_transaksi` (`kode_transaksi`),
  ADD KEY `idx_pendaftaran_pemesan` (`id_pemesan`),
  ADD KEY `idx_pendaftaran_seminar` (`id_seminar`);

--
-- Indexes for table `presensi`
--
ALTER TABLE `presensi`
  ADD PRIMARY KEY (`id_presensi`),
  ADD UNIQUE KEY `id_detail` (`id_detail`),
  ADD KEY `dicatat_oleh` (`dicatat_oleh`);

--
-- Indexes for table `seminar`
--
ALTER TABLE `seminar`
  ADD PRIMARY KEY (`id_seminar`),
  ADD KEY `idx_seminar_institusi` (`id_institusi`),
  ADD KEY `idx_seminar_panitia` (`id_panitia`),
  ADD KEY `idx_seminar_kategori` (`id_kategori`);

--
-- Indexes for table `sertifikat`
--
ALTER TABLE `sertifikat`
  ADD PRIMARY KEY (`id_sertifikat`),
  ADD UNIQUE KEY `id_detail` (`id_detail`),
  ADD UNIQUE KEY `nomor_sertifikat` (`nomor_sertifikat`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `idx_user_institusi` (`id_institusi`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `audit_log`
--
ALTER TABLE `audit_log`
  MODIFY `id_log` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `detail_pendaftaran`
--
ALTER TABLE `detail_pendaftaran`
  MODIFY `id_detail` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `institusi`
--
ALTER TABLE `institusi`
  MODIFY `id_institusi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `kategori`
--
ALTER TABLE `kategori`
  MODIFY `id_kategori` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `pembayaran`
--
ALTER TABLE `pembayaran`
  MODIFY `id_pembayaran` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `pendaftaran`
--
ALTER TABLE `pendaftaran`
  MODIFY `id_pendaftaran` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `presensi`
--
ALTER TABLE `presensi`
  MODIFY `id_presensi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `seminar`
--
ALTER TABLE `seminar`
  MODIFY `id_seminar` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `sertifikat`
--
ALTER TABLE `sertifikat`
  MODIFY `id_sertifikat` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD CONSTRAINT `audit_log_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE SET NULL;

--
-- Constraints for table `detail_pendaftaran`
--
ALTER TABLE `detail_pendaftaran`
  ADD CONSTRAINT `detail_pendaftaran_ibfk_1` FOREIGN KEY (`id_pendaftaran`) REFERENCES `pendaftaran` (`id_pendaftaran`) ON DELETE CASCADE;

--
-- Constraints for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD CONSTRAINT `pembayaran_ibfk_1` FOREIGN KEY (`id_pendaftaran`) REFERENCES `pendaftaran` (`id_pendaftaran`) ON DELETE CASCADE;

--
-- Constraints for table `pendaftaran`
--
ALTER TABLE `pendaftaran`
  ADD CONSTRAINT `pendaftaran_ibfk_1` FOREIGN KEY (`id_pemesan`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `pendaftaran_ibfk_2` FOREIGN KEY (`id_seminar`) REFERENCES `seminar` (`id_seminar`) ON DELETE CASCADE;

--
-- Constraints for table `presensi`
--
ALTER TABLE `presensi`
  ADD CONSTRAINT `presensi_ibfk_1` FOREIGN KEY (`id_detail`) REFERENCES `detail_pendaftaran` (`id_detail`) ON DELETE CASCADE,
  ADD CONSTRAINT `presensi_ibfk_2` FOREIGN KEY (`dicatat_oleh`) REFERENCES `user` (`id_user`) ON DELETE SET NULL;

--
-- Constraints for table `seminar`
--
ALTER TABLE `seminar`
  ADD CONSTRAINT `seminar_ibfk_1` FOREIGN KEY (`id_institusi`) REFERENCES `institusi` (`id_institusi`),
  ADD CONSTRAINT `seminar_ibfk_2` FOREIGN KEY (`id_panitia`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `seminar_ibfk_3` FOREIGN KEY (`id_kategori`) REFERENCES `kategori` (`id_kategori`) ON DELETE SET NULL;

--
-- Constraints for table `sertifikat`
--
ALTER TABLE `sertifikat`
  ADD CONSTRAINT `sertifikat_ibfk_1` FOREIGN KEY (`id_detail`) REFERENCES `detail_pendaftaran` (`id_detail`) ON DELETE CASCADE;

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `user_ibfk_1` FOREIGN KEY (`id_institusi`) REFERENCES `institusi` (`id_institusi`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
