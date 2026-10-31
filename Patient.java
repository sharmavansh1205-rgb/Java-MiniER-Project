// Parent class (abstract) for all patients
public abstract class Patient implements Treatable, Comparable<Patient> {

    private static int counter = 0;      // counts patients, gives each an arrival number

    private final String name;
    private final int age;
    private final int arrivalNo;
    private volatile String treatedBy = "-";

    public Patient(String name, int age) throws InvalidPatientException {
        if (name == null || name.isBlank()) {
            throw new InvalidPatientException("Name cannot be empty");
        }
        if (age < 0 || age > 120) {
            throw new InvalidPatientException("Age must be between 0 and 120");
        }
        this.name = name.trim();
        this.age = age;
        this.arrivalNo = ++counter;
    }

    // Child classes will OVERRIDE these three methods
    public abstract int getPriority();       // 1 = most urgent
    public abstract String getType();
    public abstract int getTreatTimeMs();

    // From the Treatable interface: the treatment is just a short sleep
    @Override
    public void treat(String doctorName) {
        treatedBy = doctorName;
        System.out.println(doctorName + " started treating " + this);
        try {
            Thread.sleep(getTreatTimeMs());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(doctorName + " finished " + name);
    }

    // From Comparable: lower priority number first, then who came first
    @Override
    public int compareTo(Patient other) {
        if (getPriority() != other.getPriority()) {
            return Integer.compare(getPriority(), other.getPriority());
        }
        return Integer.compare(arrivalNo, other.arrivalNo);
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getTreatedBy() { return treatedBy; }

    @Override
    public String toString() {
        return "[" + getType() + "] " + name + " (age " + age + ")";
    }
}
