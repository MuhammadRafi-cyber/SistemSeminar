package exception;
public class DataTidakDitemukanException extends Exception {
    public DataTidakDitemukanException(String entitas) {
        super("Data " + entitas + " tidak ditemukan.");
    }
    public DataTidakDitemukanException(String entitas, int id) {
        super("Data " + entitas + " dengan ID " + id + " tidak ditemukan.");
    }
    public DataTidakDitemukanException(String entitas, String kode) {
        super("Data " + entitas + " dengan kode '" + kode + "' tidak ditemukan.");
    }
}
