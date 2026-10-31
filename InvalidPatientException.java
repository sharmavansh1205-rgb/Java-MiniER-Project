// Our own exception: thrown when patient data is wrong
public class InvalidPatientException extends Exception {
    public InvalidPatientException(String message) {
        super(message);
    }
}
