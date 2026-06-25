package exception;

public class InputKosongException extends Exception {
    public InputKosongException(String namaField) {
        super("Field '" + namaField + "' tidak boleh kosong.");
    }
}
