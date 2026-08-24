import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WardManager {
    private final List<Patient> patients = new ArrayList<>();
    private final String[][] bedGrid = new String[4][5]; // 4x5 layout (20 beds: B01 to B20)
    private final String wardName = "General Ward";

    public WardManager() {
        int count = 1;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                bedGrid[r][c] = String.format("B%02d", count++);
            }
        }
    }

    // Patient Management
    public boolean registerPatient(Patient p) {
        for (Patient existing : patients) {
            if (existing.getPatientId().equalsIgnoreCase(p.getPatientId())) {
                throw new IllegalArgumentException("Patient ID already exists.");
            }
        }
        return patients.add(p);
    }

    public Patient searchPatient(String patientId) {
        for (Patient p : patients) {
            if (p.getPatientId().equalsIgnoreCase(patientId)) {
                return p;
            }
        }
        return null;
    }

    public boolean updatePatient(String patientId, String firstName, String lastName, int age, String gender, String condition) {
        Patient p = searchPatient(patientId);
        if (p != null) {
            p.setFirstName(firstName);
            p.setLastName(lastName);
            p.setAge(age);
            p.setGender(gender);
            p.setMedicalCondition(condition);
            return true;
        }
        return false;
    }

    public boolean deletePatient(String patientId) {
        Patient p = searchPatient(patientId);
        if (p != null) {
            if (p instanceof Inpatient) {
                releaseBedByPatientId(patientId);
            }
            return patients.remove(p);
        }
        return false;
    }

    // Bed Management
    public String allocateBed(String patientId) {
        Patient p = searchPatient(patientId);
        if (p == null) throw new IllegalArgumentException("Patient not found.");
        if (p.getCategory() != PatientCategory.INPATIENT) {
            throw new IllegalArgumentException("Only Inpatients can be allocated a bed.");
        }
        if (isBedAllocatedToPatient(patientId)) {
            throw new IllegalStateException("Patient already has a bed assigned.");
        }

        String availableBed = getNextAvailableBedCode();
        if (availableBed == null) {
            throw new IllegalStateException("No beds available in the ward.");
        }

        // Replace bed code with patient ID in 2D array
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (bedGrid[r][c].equals(availableBed)) {
                    bedGrid[r][c] = "[" + patientId + "]";
                    if (p instanceof Inpatient) {
                        ((Inpatient) p).setBedNumber(availableBed);
                    }
                    return availableBed;
                }
            }
        }
        return null;
    }

    public boolean releaseBed(String bedCode) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                int bedNum = (r * 5) + (c + 1);
                String defaultCode = String.format("B%02d", bedNum);

                if (!bedGrid[r][c].equals(defaultCode) && (bedGrid[r][c].equalsIgnoreCase(bedCode) || bedGrid[r][c].equalsIgnoreCase("[" + bedCode + "]"))) {
                    String assignedPatientId = bedGrid[r][c].replace("[", "").replace("]", "");
                    Patient p = searchPatient(assignedPatientId);
                    if (p instanceof Inpatient) {
                        ((Inpatient) p).setBedNumber("None");
                    }
                    bedGrid[r][c] = defaultCode;
                    return true;
                }
            }
        }
        return false;
    }

    private void releaseBedByPatientId(String patientId) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (bedGrid[r][c].equals("[" + patientId + "]")) {
                    int bedNum = (r * 5) + (c + 1);
                    bedGrid[r][c] = String.format("B%02d", bedNum);
                }
            }
        }
    }

    private boolean isBedAllocatedToPatient(String patientId) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (bedGrid[r][c].equals("[" + patientId + "]")) return true;
            }
        }
        return false;
    }

    public String getNextAvailableBedCode() {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (!bedGrid[r][c].startsWith("[")) {
                    return bedGrid[r][c];
                }
            }
        }
        return null;
    }

    // Display & Reports
    public void displayWardLayout() {
        System.out.println("\n--- Ward Bed Layout (4x5) ---");
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                System.out.printf("%-8s", bedGrid[r][c]);
            }
            System.out.println();
        }
    }

    public List<String> getAvailableBeds() {
        List<String> list = new ArrayList<>();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (!bedGrid[r][c].startsWith("[")) list.add(bedGrid[r][c]);
            }
        }
        return list;
    }

    public List<String> getOccupiedBeds() {
        List<String> list = new ArrayList<>();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 5; c++) {
                if (bedGrid[r][c].startsWith("[")) list.add(bedGrid[r][c]);
            }
        }
        return list;
    }

    public void sortPatientsByName() {
        Collections.sort(patients);
    }

    public void sortPatientsById() {
        patients.sort(Comparator.comparing(Patient::getPatientId));
    }

    public List<Patient> getPatients() { return patients; }
    public String getWardName() { return wardName; }
}