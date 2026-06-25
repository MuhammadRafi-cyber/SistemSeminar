package controller;

import exception.*;
import model.Sertifikat;
import service.SertifikatService;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * SertifikatController — Bridge View ↔ SertifikatService.
 * Input: id_pendaftaran (sesuai DB tim, FK ke pendaftaran).
 */
public class SertifikatController {

    private final SertifikatService sertifikatService;

    public SertifikatController(SertifikatService sertifikatService) {
        this.sertifikatService = sertifikatService;
    }

    public String generate(int idPendaftaran) {
        try {
            Sertifikat s = sertifikatService.generate(idPendaftaran);
            return "SUKSES|Sertifikat berhasil diterbitkan!\n"
                 + "  Kode Sertifikat : " + s.getKodeSertifikat();
        } catch (SertifikatTidakTersediaException e) {
            return "ERROR|" + e.getMessage();
        } catch (DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal menerbitkan sertifikat. Cek koneksi database.";
        }
    }

    // E2: List Object[]{kodeSertifikat, tanggalTerbit, judulSeminar}
    public List<Object[]> getDaftarSertifikat(int idUser) {
        try { return sertifikatService.getSertifikatPeserta(idUser); }
        catch (SQLException e) { return Collections.emptyList(); }
    }

    public int hitungSertifikat(int idSeminar) {
        try { return sertifikatService.hitungSertifikat(idSeminar); }
        catch (SQLException e) { return 0; }
    }
}
