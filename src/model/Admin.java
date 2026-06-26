package model;

import enums.Role;

/** Admin — extends Panitia (multi-level inheritance). */
public class Admin extends Panitia {
    public Admin(int idUser, Integer idInstitusi, String nama, String email,
                 String passwordHash, String noTelepon, String tanggalDaftar) {
        super(idUser, idInstitusi, nama, email, passwordHash, noTelepon, tanggalDaftar);
        setRole(Role.ADMIN);
    }

    @Override
    public String generateLaporan() {
        return "=== LAPORAN GLOBAL ADMIN ===\n"
             + "Admin   : " + getNama() + " (akses penuh ke semua data)\n"
             + super.generateLaporan();
    }
}
