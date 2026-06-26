package exception;
public class InputKosongException extends Exception {
    public InputKosongException(String field) {
        super("Field '" + field + "' tidak boleh kosong.");
    }
}
