import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

// All file work is done with Java NIO (Path, Paths, Files)
public class FileHelper {

    private static final Path TREATED_FILE = Paths.get("treated.txt");
    private static final Path LOG_FILE = Paths.get("log.txt");

    // Reads lines like  Arun,45,C  from a file and makes patient objects
    public static List<Patient> readPatients(String fileName) throws IOException {
        List<Patient> patients = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);

        for (String line : lines) {
            if (line.isBlank()) continue;
            try {
                String[] parts = line.split(",");
                String name = parts[0].trim();
                int age = Integer.parseInt(parts[1].trim());
                String type = parts[2].trim();
                patients.add(Main.createPatient(name, age, type));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException | InvalidPatientException e) {
                System.out.println("Skipped bad line: " + line + "  (" + e.getMessage() + ")");
            }
        }
        return patients;
    }

    // Writes the treated patients to treated.txt (replaces the old file)
    public static void saveTreated(List<Patient> treated) throws IOException {
        List<String> lines = new ArrayList<>();
        synchronized (treated) {
            for (Patient p : treated) {
                lines.add(p.getType() + "," + p.getName() + "," + p.getAge() + "," + p.getTreatedBy());
            }
        }
        Files.write(TREATED_FILE, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // Prints the saved file back on screen
    public static void showSaved() throws IOException {
        for (String line : Files.readAllLines(TREATED_FILE, StandardCharsets.UTF_8)) {
            System.out.println(line);
        }
    }

    // Adds one line at the end of log.txt
    public static void log(String message) {
        try {
            Files.writeString(LOG_FILE, message + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Could not write log: " + e.getMessage());
        }
    }
}
