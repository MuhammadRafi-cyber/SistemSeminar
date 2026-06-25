package model;

import enums.Role;

/**
 * Admin — turunan Panitia (multi-level inheritance).
 * Admin bisa melakukan semua yang Panitia bisa + akses global.
 */
public class Admin extends Panitia {

    public Admin(int idUser, String nama, String email, String passwordHash,
                 String noTelepon, String tanggalDaftar) {
        super(idUser, nama, email, passwordHash, noTelepon, tanggalDaftar);
        setRole(Role.ADMIN);
    }

    @Override
    public String generateLaporan() {
        return "=== LAPORAN GLOBAL ADMIN ===\n"
             + "Admin   : " + getNama() + "\n"
             + "Akses   : Global (semua institusi)\n"
             + super.generateLaporan();
    }
}
