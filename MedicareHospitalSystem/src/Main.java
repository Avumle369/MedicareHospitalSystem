import java.util.Scanner;

public class Main {
    private static final WardManager manager = new WardManager();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== MEDICARE HOSPITAL ADMISSION SYSTEM =====");
            System.out.println("1. Register Patient");
            System.out.println("2. Search Patient");
            System.out.println("3. Update Patient");
            System.out.println("4. Delete Patient");
            System.out.println("5. Allocate Bed to Inpatient");
            System.out.println("6. Release Bed");
            System.out.println("7. View Ward Layout");
            System.out.println("8. Reports");
            System.out.println("9. Exit");
            System.out.print("Select an option (1-9): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> registerPatientUI();
                    case 2 -> searchPatientUI();
                    case 3 -> updatePatientUI();
                    case 4 -> deletePatientUI();
                    case 5 -> allocateBedUI();
                    case 6 -> releaseBedUI();
                    case 7 -> manager.displayWardLayout();
                    case 8 -> generateReportsUI();
                    case 9 -> {
                        running = false;
                        System.out.println("Exiting system. Goodbye!");
                    }
                    default -> System.out.println("Invalid option. Enter a number between 1 and 9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid numerical option.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void registerPatientUI() {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter First Name: ");
        String fName = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lName = scanner.nextLine();
        System.out.print("Enter Age: ");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter Medical Condition: ");
        String condition = scanner.nextLine();

        System.out.println("Select Category: 1. Inpatient  2. Outpatient  3. Emergency");
        int catChoice = Integer.parseInt(scanner.nextLine());

        Patient patient;
        if (catChoice == 1) {
            patient = new Inpatient(id, fName, lName, age, gender, condition, manager.getWardName(), "Unassigned");
        } else if (catChoice == 2) {
            patient = new Patient(id, fName, lName, age, gender, condition, PatientCategory.OUTPATIENT);
        } else if (catChoice == 3) {
            patient = new Patient(id, fName, lName, age, gender, condition, PatientCategory.EMERGENCY);
        } else {
            System.out.println("Invalid category selected.");
            return;
        }

        manager.registerPatient(patient);
        System.out.println("Patient registered successfully.");
    }

    private static void searchPatientUI() {
        System.out.print("Enter Patient ID to search: ");
        String id = scanner.nextLine();
        Patient p = manager.searchPatient(id);
        if (p != null) {
            p.displayDetails();
        } else {
            System.out.println("Patient not found.");
        }
    }

    private static void updatePatientUI() {
        System.out.print("Enter Patient ID to update: ");
        String id = scanner.nextLine();
        if (manager.searchPatient(id) == null) {
            System.out.println("Patient not found.");
            return;
        }
        System.out.print("New First Name: ");
        String fName = scanner.nextLine();
        System.out.print("New Last Name: ");
        String lName = scanner.nextLine();
        System.out.print("New Age: ");
        int age = Integer.parseInt(scanner.nextLine());
        System.out.print("New Gender: ");
        String gender = scanner.nextLine();
        System.out.print("New Condition: ");
        String cond = scanner.nextLine();

        manager.updatePatient(id, fName, lName, age, gender, cond);
        System.out.println("Patient details updated successfully.");
    }

    private static void deletePatientUI() {
        System.out.print("Enter Patient ID to delete: ");
        String id = scanner.nextLine();
        if (manager.deletePatient(id)) {
            System.out.println("Patient deleted successfully.");
        } else {
            System.out.println("Patient not found.");
        }
    }

    private static void allocateBedUI() {
        System.out.print("Enter Inpatient ID for bed allocation: ");
        String id = scanner.nextLine();
        String allocatedBed = manager.allocateBed(id);
        System.out.println("Bed " + allocatedBed + " allocated successfully.");
    }

    private static void releaseBedUI() {
        System.out.print("Enter Bed Code or Allocated Patient ID to release: ");
        String input = scanner.nextLine();
        if (manager.releaseBed(input)) {
            System.out.println("Bed released successfully.");
        } else {
            System.out.println("Bed allocation record not found.");
        }
    }

    private static void generateReportsUI() {
        System.out.println("\n--- WARD REPORTS ---");
        System.out.println("1. Sort patients by Last Name");
        System.out.println("2. Sort patients by Patient ID");
        System.out.print("Choose sorting: ");
        int sortChoice = Integer.parseInt(scanner.nextLine());
        if (sortChoice == 1) manager.sortPatientsByName();
        else if (sortChoice == 2) manager.sortPatientsById();

        System.out.println("\nRegistered Patients:");
        for (Patient p : manager.getPatients()) {
            p.displayDetails();
        }

        System.out.println("\nAvailable Beds: " + manager.getAvailableBeds());
        System.out.println("Occupied Beds: " + manager.getOccupiedBeds());
        System.out.println("Total Registered Patients: " + manager.getPatients().size());
        int occupiedCount = manager.getOccupiedBeds().size();
        System.out.println("Total Occupied Beds: " + occupiedCount + "/20");
        double percentage = (occupiedCount / 20.0) * 100;
        System.out.printf("Ward Occupancy Percentage: %.2f%%\n", percentage);
    }
}
