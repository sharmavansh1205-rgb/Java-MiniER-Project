// Child class 2: normal cases, treated after critical ones
public class NormalPatient extends Patient {

    public NormalPatient(String name, int age) throws InvalidPatientException {
        super(name, age);
    }

    @Override public int getPriority() { return 2; }
    @Override public String getType() { return "NORMAL"; }
    @Override public int getTreatTimeMs() { return 500; }
}
