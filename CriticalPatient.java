// Child class 1: serious cases, treated first
public class CriticalPatient extends Patient {

    public CriticalPatient(String name, int age) throws InvalidPatientException {
        super(name, age);
    }

    @Override public int getPriority() { return 1; }
    @Override public String getType() { return "CRITICAL"; }
    @Override public int getTreatTimeMs() { return 1000; }
}
