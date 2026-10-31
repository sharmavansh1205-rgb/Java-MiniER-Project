import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.PriorityBlockingQueue;

public class Main {

    // Collections used
    static PriorityBlockingQueue<Patient> waiting = new PriorityBlockingQueue<>();  // urgent first
    static List<Patient> treated = Collections.synchronizedList(new ArrayList<>()); // history

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addPatient();
                    case "2" -> importPatients();
                    case "3" -> showWaiting();
                    case "4" -> startDoctors();
                    case "5" -> showTreated();
                    case "6" -> saveToFile();
                    case "7" -> showSummary();
                    case "0" -> running = false;
                    default -> System.out.println("Wrong option, try again.");
                }
            } catch (InvalidPatientException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: please type a number for age.");
            } catch (IOException e) {
                System.out.println("File problem: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println();
        }
        System.out.println("Bye!");
    }

    static void printMenu() {
        System.out.println("===== Mini Emergency Room =====");
        System.out.println("1. Add patient");
        System.out.println("2. Import patients from patients.txt");
        System.out.println("3. Show waiting list");
        System.out.println("4. Start doctors (2 threads)");
        System.out.println("5. Show treated patients");
        System.out.println("6. Save treated patients to file");
        System.out.println("7. Show summary");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    // Decides which child class to create (polymorphism: result is used as Patient)
    static Patient createPatient(String name, int age, String type) throws InvalidPatientException {
        if (type.equalsIgnoreCase("C")) {
            return new CriticalPatient(name, age);
        } else if (type.equalsIgnoreCase("N")) {
            return new NormalPatient(name, age);
        }
        throw new InvalidPatientException("Type must be C (critical) or N (normal)");
    }

    static void addPatient() throws InvalidPatientException {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Age: ");
        int age = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Type (C = critical, N = normal): ");
        String type = sc.nextLine();

        Patient p = createPatient(name, age, type);
        waiting.add(p);
        FileHelper.log("Added " + p);
        System.out.println("Added: " + p);
    }

    static void importPatients() throws IOException {
        List<Patient> list = FileHelper.readPatients("patients.txt");
        waiting.addAll(list);
        FileHelper.log("Imported " + list.size() + " patients from file");
        System.out.println(list.size() + " patients imported.");
    }

    static void showWaiting() {
        if (waiting.isEmpty()) {
            System.out.println("Nobody is waiting.");
            return;
        }
        List<Patient> list = new ArrayList<>(waiting);
        Collections.sort(list);                       // uses compareTo()
        System.out.println("Waiting list (most urgent first):");
        for (Patient p : list) {
            System.out.println("  " + p);
        }
    }

    static void startDoctors() throws InterruptedException {
        if (waiting.isEmpty()) {
            System.out.println("Nobody is waiting.");
            return;
        }
        Thread doctor1 = new Thread(new Doctor("Dr. Rao", waiting, treated));
        Thread doctor2 = new Thread(new Doctor("Dr. Mehta", waiting, treated));
        doctor1.start();
        doctor2.start();
        doctor1.join();                               // wait until both doctors finish
        doctor2.join();
        FileHelper.log("Doctors finished. Total treated: " + treated.size());
        System.out.println("All patients treated.");
    }

    static void showTreated() {
        if (treated.isEmpty()) {
            System.out.println("Nobody treated yet.");
            return;
        }
        for (Patient p : treated) {
            System.out.println("  " + p + "  by " + p.getTreatedBy());
        }
    }

    static void saveToFile() throws IOException {
        FileHelper.saveTreated(treated);
        System.out.println("Saved to treated.txt:");
        FileHelper.showSaved();
    }

    static void showSummary() {
        Map<String, Integer> count = new HashMap<>();
        for (Patient p : treated) {
            count.merge(p.getType(), 1, Integer::sum);
        }
        System.out.println("Total treated: " + treated.size());
        for (Map.Entry<String, Integer> entry : count.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
