package exception;
public class HargaTidakValidException extends Exception {
    public HargaTidakValidException() {
        super("Harga seminar tidak boleh bernilai negatif.");
    }
}
