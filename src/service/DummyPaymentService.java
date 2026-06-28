package service;

import dao.PembayaranDAO;
import enums.StatusPembayaran;
import enums.StatusRefund;
import exception.PembayaranGagalException;

import java.util.Arrays;
import java.util.List;

/**
 * DummyPaymentService — implementasi PaymentService untuk simulasi akademik.
 * Tidak memproses uang nyata. Tidak menyimpan CVV/OTP/data finansial.
 * Referensi PRD: BR-18, NFR-07, FR-012.
 */
public class DummyPaymentService implements PaymentService {

    private static final List<String> METODE_VALID =
        Arrays.asList("QRIS", "E-Wallet", "Virtual Account");

    private static final double SUCCESS_RATE = 0.95; // 95% berhasil (untuk demo exception TC-09)

    private final PembayaranDAO pembayaranDAO;

    public DummyPaymentService(PembayaranDAO pembayaranDAO) {
        this.pembayaranDAO = pembayaranDAO;
    }

    @Override
    public StatusPembayaran prosesPembayaran(int idPendaftaran, double nominal, String metode)
            throws PembayaranGagalException {

        if (!validasiPembayaran(nominal, metode)) {
            try { pembayaranDAO.updateStatus(idPendaftaran, StatusPembayaran.GAGAL); }
            catch (java.sql.SQLException e) { System.err.println("[PAYMENT] " + e.getMessage()); }
            throw new PembayaranGagalException(
                    "Metode '" + metode + "' tidak valid atau nominal negatif.");
        }

        if (Math.random() < SUCCESS_RATE) {
            try { pembayaranDAO.updateStatus(idPendaftaran, StatusPembayaran.BERHASIL); }
            catch (java.sql.SQLException e) { System.err.println("[PAYMENT] " + e.getMessage()); }
            return StatusPembayaran.BERHASIL;
        } else {
            try { pembayaranDAO.updateStatus(idPendaftaran, StatusPembayaran.GAGAL); }
            catch (java.sql.SQLException e) { System.err.println("[PAYMENT] " + e.getMessage()); }
            throw new PembayaranGagalException(
                    "Simulasi: gateway timeout. Silakan coba lagi.");
        }
    }

    @Override
    public boolean validasiPembayaran(double nominal, String metode) {
        if (nominal < 0) return false;
        if (metode == null || metode.trim().isEmpty()) return false;
        return METODE_VALID.contains(metode.trim());
    }

    @Override
    public StatusRefund prosesRefund(int idPendaftaran) {
        try {
            pembayaranDAO.updateStatusRefund(idPendaftaran, StatusRefund.DIPROSES);
            pembayaranDAO.updateStatusRefund(idPendaftaran, StatusRefund.SELESAI);
        } catch (java.sql.SQLException e) {
            System.err.println("[REFUND] Gagal update status refund: " + e.getMessage());
            return StatusRefund.DIPROSES; // refund gagal dicatat, kembalikan status terakhir yang berhasil
        }
        return StatusRefund.SELESAI;
    }
}
