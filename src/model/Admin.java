package model;

import enums.Role;

/** Admin — extends Panitia (multi-level inheritance). */
public class Admin extends Panitia {
    public Admin(int idUser, Integer idInstitusi, String nama, String username,
                 String email, String passwordHash, String noTelepon, String tanggalDaftar) {
        super(idUser, idInstitusi, nama, username, email, passwordHash, noTelepon, tanggalDaftar);
        setRole(Role.ADMIN);
    }

    @Override
    public String generateLaporan() {
        return "=== LAPORAN GLOBAL ADMIN ===\n"
             + "Admin    : " + getNama() + " (akses penuh ke semua institusi)\n"
             + super.generateLaporan();
    }
}
