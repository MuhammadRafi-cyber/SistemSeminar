package enums;

/**
 * StatusRefund — status proses refund pada tabel pembayaran.
 * Kolom DB: status_refund ENUM('TIDAK_ADA','DIMINTA','DIPROSES','SELESAI')
 *
 * Alur refund:
 *   TIDAK_ADA → DIMINTA (peserta minta refund saat batalkan pendaftaran)
 *             → DIPROSES (sistem memproses refund dummy)
 *             → SELESAI  (refund dummy selesai)
 */
public enum StatusRefund { TIDAK_ADA, DIMINTA, DIPROSES, SELESAI }
