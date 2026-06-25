package util;

import exception.*;

import java.time.LocalDate;

/**
 * Validator — kumpulan method validasi input yang dipakai di seluruh Service.
 */
public class Validator {

    /** Memastikan string tidak null dan tidak kosong */
    public static void cekTidakKosong(String nilai, String namaField) throws InputKosongException {
        if (nilai == null || nilai.trim().isEmpty()) {
            throw new InputKosongException(namaField);
        }
    }

    /** Format email: harus ada '@' dan '.' setelah '@' */
    public static void cekEmail(String email) throws EmailTidakValidException, InputKosongException {
        cekTidakKosong(email, "Email");
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            throw new EmailTidakValidException();
        }
    }

    /** Password minimal 8 karakter, ada huruf dan angka */
    public static void cekPassword(String password) throws PasswordTidakValidException, InputKosongException {
        cekTidakKosong(password, "Password");
        if (password.length() < 8) throw new PasswordTidakValidException();
        boolean adaHuruf = password.matches(".*[a-zA-Z].*");
        boolean adaAngka = password.matches(".*[0-9].*");
        if (!adaHuruf || !adaAngka) throw new PasswordTidakValidException();
    }

    /** Kuota harus positif */
    public static void cekKuota(int kuota) throws KuotaTidakValidException {
        if (kuota <= 0) throw new KuotaTidakValidException();
    }

    /** Tanggal mulai tidak boleh di masa lalu, tanggal selesai harus setelah mulai */
    public static void cekTanggal(LocalDate tanggalMulai, LocalDate tanggalSelesai)
            throws TanggalTidakValidException {
        if (tanggalMulai == null || tanggalSelesai == null) {
            throw new TanggalTidakValidException("Tanggal mulai dan tanggal selesai wajib diisi.");
        }
        if (tanggalMulai.isBefore(LocalDate.now())) {
            throw new TanggalTidakValidException("Tanggal pelaksanaan tidak boleh di masa lalu.");
        }
        if (!tanggalSelesai.isAfter(tanggalMulai) && !tanggalSelesai.isEqual(tanggalMulai)) {
            throw new TanggalTidakValidException("Tanggal selesai harus sama dengan atau setelah tanggal mulai.");
        }
    }
}
