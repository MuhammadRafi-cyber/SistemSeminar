package util;

import exception.*;
import java.time.LocalDateTime;

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

    public static void cekPassword(String pw) throws PasswordTidakValidException, InputKosongException {
        cekTidakKosong(pw, "Password");
        if (pw.length() < 8 || !pw.matches(".*[a-zA-Z].*") || !pw.matches(".*[0-9].*"))
            throw new PasswordTidakValidException();
    }

    public static void cekKuota(int kuota) throws KuotaTidakValidException {
        if (kuota <= 0) throw new KuotaTidakValidException();
    }

    public static void cekHarga(double harga) throws HargaTidakValidException {
        if (harga < 0) throw new HargaTidakValidException();
    }

    public static void cekTanggal(LocalDateTime mulai, LocalDateTime selesai)
            throws TanggalTidakValidException {
        if (mulai == null)
            throw new TanggalTidakValidException("Tanggal mulai seminar wajib diisi.");
        if (mulai.isBefore(LocalDateTime.now()))
            throw new TanggalTidakValidException("Tanggal mulai tidak boleh di masa lalu.");
        if (selesai == null)
            throw new TanggalTidakValidException("Tanggal selesai seminar wajib diisi.");
        if (!selesai.isAfter(mulai))
            throw new TanggalTidakValidException("Tanggal selesai harus setelah tanggal mulai.");
    }

    public static void cekJumlahTiket(int jumlah) throws JumlahTiketTidakValidException {
        if (jumlah < 1 || jumlah > 4) throw new JumlahTiketTidakValidException();
    }

    /**
     * Validasi jendela waktu presensi: T-60 menit s/d seminar selesai (BR-13).
     * @throws KodeBookingTidakValidException jika di luar jendela waktu
     */
    public static void cekJendelaPresensi(LocalDateTime tanggalMulai, LocalDateTime tanggalSelesai,
                                           String kodeBooking)
            throws exception.KodeBookingTidakValidException {
        if (tanggalMulai == null || tanggalSelesai == null)
            throw new exception.KodeBookingTidakValidException(kodeBooking, "data waktu seminar tidak lengkap");

        LocalDateTime now           = LocalDateTime.now();
        LocalDateTime batasAwal     = tanggalMulai.minusMinutes(60);  // T-60 menit
        LocalDateTime batasAkhir    = tanggalSelesai;

        if (now.isBefore(batasAwal))
            throw new exception.KodeBookingTidakValidException(kodeBooking,
                "presensi baru dibuka 60 menit sebelum seminar (mulai " + batasAwal + ")");
        if (now.isAfter(batasAkhir))
            throw new exception.KodeBookingTidakValidException(kodeBooking,
                "waktu presensi sudah ditutup (seminar berakhir " + batasAkhir + ")");
    }
}
