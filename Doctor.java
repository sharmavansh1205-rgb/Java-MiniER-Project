import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

// A doctor is a Runnable, so every doctor works on its own thread
public class Doctor implements Runnable {

    private final String name;
    private final PriorityBlockingQueue<Patient> waiting;
    private final List<Patient> treated;

    public Doctor(String name, PriorityBlockingQueue<Patient> waiting, List<Patient> treated) {
        this.name = name;
        this.waiting = waiting;
        this.treated = treated;
    }

    @Override
    public void run() {
        Patient patient;
        // poll() takes the most urgent patient, or gives null when the queue is empty
        while ((patient = waiting.poll()) != null) {
            patient.treat(name);
            treated.add(patient);
        }
        System.out.println(name + " has no more patients and goes home.");
    }
}
