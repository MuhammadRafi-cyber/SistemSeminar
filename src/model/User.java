package model;

import enums.Role;

/**
 * User — Abstract class (Abstraction + Encapsulation).
 * Tambahan v5: kolom username (disimpan, tidak dipakai untuk login — PRD Bagian 18).
 */
public abstract class User {
    private int     idUser;
    private Integer idInstitusi;
    private String  nama;
    private String  username;        // [TAMBAHAN v5] tidak dipakai untuk login
    private String  email;
    private String  passwordHash;
    private Role    role;
    private String  noTelepon;
    private String  tanggalDaftar;

    // Constructor dari DB (full)
    public User(int idUser, Integer idInstitusi, String nama, String username,
                String email, String passwordHash, Role role,
                String noTelepon, String tanggalDaftar) {
        this.idUser        = idUser;
        this.idInstitusi   = idInstitusi;
        this.nama          = nama;
        this.username      = username;
        this.email         = email;
        this.passwordHash  = passwordHash;
        this.role          = role;
        this.noTelepon     = noTelepon;
        this.tanggalDaftar = tanggalDaftar;
    }

    // Constructor untuk INSERT baru
    public User(Integer idInstitusi, String nama, String username, String email,
                String passwordHash, Role role, String noTelepon) {
        this.idInstitusi  = idInstitusi;
        this.nama         = nama;
        this.username     = username;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.noTelepon    = noTelepon;
    }

    /** Setiap subclass menghasilkan laporan berbeda (Polymorphism). */
    public abstract String generateLaporan();

    // Getters
    public int     getIdUser()        { return idUser; }
    public Integer getIdInstitusi()   { return idInstitusi; }
    public String  getNama()          { return nama; }
    public String  getUsername()      { return username; }
    public String  getEmail()         { return email; }
    public String  getPasswordHash()  { return passwordHash; }
    public Role    getRole()          { return role; }
    public String  getNoTelepon()     { return noTelepon; }
    public String  getTanggalDaftar() { return tanggalDaftar; }

    // Setters
    public void setIdUser(int id)             { this.idUser = id; }
    public void setIdInstitusi(Integer id)    { this.idInstitusi = id; }
    public void setNama(String nama)          { this.nama = nama; }
    public void setUsername(String un)        { this.username = un; }
    public void setEmail(String email)        { this.email = email; }
    public void setPasswordHash(String hash)  { this.passwordHash = hash; }
    public void setRole(Role role)            { this.role = role; }
    public void setNoTelepon(String tlp)      { this.noTelepon = tlp; }

    @Override
    public String toString() {
        return "[" + role + "] " + nama + " (@" + username + ") <" + email + ">";
    }
}
