package model;

import enums.Role;

/**
 * User — Abstract class.
 * Konsep PBO: Abstraction + Encapsulation.
 * Peserta dan Panitia mewarisi class ini.
 */
public abstract class User {

    // === Atribut (private = Encapsulation) ===
    private int    idUser;
    private String nama;
    private String email;
    private String passwordHash;   // Disimpan sebagai hash, BUKAN plain text
    private Role   role;
    private String noTelepon;
    private String tanggalDaftar;

    // === Constructor ===
    public User(int idUser, String nama, String email, String passwordHash,
                Role role, String noTelepon, String tanggalDaftar) {
        this.idUser       = idUser;
        this.nama         = nama;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.noTelepon    = noTelepon;
        this.tanggalDaftar = tanggalDaftar;
    }

    // Constructor tanpa ID (untuk insert baru)
    public User(String nama, String email, String passwordHash,
                Role role, String noTelepon) {
        this.nama         = nama;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.noTelepon    = noTelepon;
    }

    // === Abstract method (Polymorphism) ===
    /**
     * Setiap subclass wajib mengimplementasikan cara menghasilkan laporan berbeda.
     * Peserta → laporan riwayat pribadi
     * Panitia → laporan seminar institusi
     */
    public abstract String generateLaporan();

    // === Getters ===
    public int    getIdUser()       { return idUser; }
    public String getNama()         { return nama; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role   getRole()         { return role; }
    public String getNoTelepon()    { return noTelepon; }
    public String getTanggalDaftar(){ return tanggalDaftar; }

    // === Setters ===
    public void setIdUser(int idUser)             { this.idUser = idUser; }
    public void setNama(String nama)               { this.nama = nama; }
    public void setEmail(String email)             { this.email = email; }
    public void setPasswordHash(String hash)       { this.passwordHash = hash; }
    public void setRole(Role role)                 { this.role = role; }
    public void setNoTelepon(String noTelepon)     { this.noTelepon = noTelepon; }
    public void setTanggalDaftar(String tgl)       { this.tanggalDaftar = tgl; }

    @Override
    public String toString() {
        return "[" + role + "] " + nama + " <" + email + ">";
    }
}
