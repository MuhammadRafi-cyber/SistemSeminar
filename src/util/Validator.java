package util;

import exception.*;
import java.time.LocalDateTime;

/**
 * Validator — kumpulan method validasi input yang dipakai di seluruh Service.
 */
public class Validator {

    public static void cekTidakKosong(String nilai, String namaField) throws InputKosongException {
        if (nilai == null || nilai.trim().isEmpty())
            throw new InputKosongException(namaField);
    }

    public static void cekEmail(String email) throws EmailTidakValidException, InputKosongException {
        cekTidakKosong(email, "Email");
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
            throw new EmailTidakValidException();
    }

    public static void cekPassword(String password)
            throws PasswordTidakValidException, InputKosongException {
        cekTidakKosong(password, "Password");
        if (password.length() < 8
                || !password.matches(".*[a-zA-Z].*")
                || !password.matches(".*[0-9].*"))
            throw new PasswordTidakValidException();
    }

    public static void cekKuota(int kuota) throws KuotaTidakValidException {
        if (kuota <= 0) throw new KuotaTidakValidException();
    }

    public static void cekHarga(double harga) throws HargaTidakValidException {
        if (harga < 0) throw new HargaTidakValidException();
    }

    /**
     * Validasi tanggal seminar.
     * tanggal_mulai wajib ada dan tidak boleh di masa lalu.
     * tanggal_selesai harus sama dengan atau setelah tanggal_mulai.
     */
    public static void cekTanggal(LocalDateTime tanggalMulai, LocalDateTime tanggalSelesai)
            throws TanggalTidakValidException {
        if (tanggalMulai == null)
            throw new TanggalTidakValidException("Tanggal mulai seminar wajib diisi.");
        if (tanggalMulai.isBefore(LocalDateTime.now()))
            throw new TanggalTidakValidException("Tanggal mulai tidak boleh di masa lalu.");
        if (tanggalSelesai != null && tanggalSelesai.isBefore(tanggalMulai))
            throw new TanggalTidakValidException("Tanggal selesai harus setelah tanggal mulai.");
    }

    public static void cekJumlahTiket(int jumlah) throws JumlahTiketTidakValidException {
        if (jumlah < 1 || jumlah > 4) throw new JumlahTiketTidakValidException();
    }
}
