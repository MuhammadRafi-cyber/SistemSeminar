package controller;

import exception.*;
import model.Sertifikat;
import service.SertifikatService;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class SertifikatController {
    private final SertifikatService sertifikatService;

    public SertifikatController(SertifikatService sertifikatService) {
        this.sertifikatService = sertifikatService;
    }

    public String generate(String kodeBooking) {
        try {
            Sertifikat s = sertifikatService.generate(kodeBooking);
            return "SUKSES|Sertifikat berhasil diterbitkan!\n"
                 + "  Nomor Sertifikat : " + s.getNomorSertifikat() + "\n"
                 + "  Versi            : " + s.getVersi() + "\n"
                 + "  File Path        : " + s.getFilePath();
        } catch (InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (KodeBookingTidakValidException e) {
            return "ERROR|Kode booking tidak valid: " + e.getMessage();
        } catch (SertifikatTidakTersediaException e) {
            return "ERROR|" + e.getMessage()
                 + "\n  [INFO] Pastikan peserta sudah tercatat HADIR terlebih dahulu.";
        } catch (SQLException e) {
            return "ERROR|Gagal menerbitkan sertifikat: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    /** E2: [{nomorSertifikat, tanggalTerbit, versi, filePath, judulSeminar, namaPeserta}] */
    public List<Object[]> getDaftarSertifikat(int idUser) {
        try { return sertifikatService.getSertifikatPemesan(idUser); }
        catch (SQLException e) { System.err.println("[ERROR] " + e.getMessage()); return Collections.emptyList(); }
    }

    public int hitungSertifikat(int idSeminar) {
        try { return sertifikatService.hitungSertifikat(idSeminar); }
        catch (SQLException e) { System.err.println("[ERROR] " + e.getMessage()); return 0; }
    }
}
