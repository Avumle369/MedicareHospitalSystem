import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WardManagerTest {
    private WardManager manager;

    @BeforeEach
    public void setUp() {
        manager = new WardManager();
    }

    @Test
    public void testRegisterPatient() {
        Patient p = new Patient("P01", "John", "Doe", 30, "Male", "Fever", PatientCategory.OUTPATIENT);
        assertTrue(manager.registerPatient(p));
        assertEquals(1, manager.getPatients().size());
    }

    @Test
    public void testPreventDuplicatePatientId() {
        Patient p1 = new Patient("P01", "John", "Doe", 30, "Male", "Fever", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P01", "Jane", "Smith", 25, "Female", "Flu", PatientCategory.EMERGENCY);
        manager.registerPatient(p1);
        assertThrows(IllegalArgumentException.class, () -> manager.registerPatient(p2));
    }

    @Test
    public void testSearchPatient() {
        Patient p = new Patient("P01", "John", "Doe", 30, "Male", "Fever", PatientCategory.OUTPATIENT);
        manager.registerPatient(p);
        assertNotNull(manager.searchPatient("P01"));
        assertNull(manager.searchPatient("P99"));
    }

    @Test
    public void testUpdatePatientDetails() {
        Patient p = new Patient("P01", "John", "Doe", 30, "Male", "Fever", PatientCategory.OUTPATIENT);
        manager.registerPatient(p);
        assertTrue(manager.updatePatient("P01", "Johnny", "Doe", 31, "Male", "Recovered"));
        assertEquals("Johnny", manager.searchPatient("P01").getFirstName());
    }

    @Test
    public void testDeletePatient() {
        Patient p = new Patient("P01", "John", "Doe", 30, "Male", "Fever", PatientCategory.OUTPATIENT);
        manager.registerPatient(p);
        assertTrue(manager.deletePatient("P01"));
        assertNull(manager.searchPatient("P01"));
    }

    @Test
    public void testAllocateBed() {
        Inpatient inpatient = new Inpatient("P02", "Alice", "Smith", 40, "Female", "Surgery", "General Ward", "Unassigned");
        manager.registerPatient(inpatient);
        String bed = manager.allocateBed("P02");
        assertEquals("B01", bed);
        assertEquals(1, manager.getOccupiedBeds().size());
    }

    @Test
    public void testPreventAllocatingOccupiedBed() {
        Inpatient inp1 = new Inpatient("P01", "Alice", "Smith", 40, "Female", "Surgery", "General Ward", "Unassigned");
        Inpatient inp2 = new Inpatient("P02", "Bob", "Jones", 50, "Male", "Observation", "General Ward", "Unassigned");
        manager.registerPatient(inp1);
        manager.registerPatient(inp2);

        String bed1 = manager.allocateBed("P01");
        String bed2 = manager.allocateBed("P02");
        assertNotEquals(bed1, bed2);
    }

    @Test
    public void testReleaseBed() {
        Inpatient inpatient = new Inpatient("P01", "Alice", "Smith", 40, "Female", "Surgery", "General Ward", "Unassigned");
        manager.registerPatient(inpatient);
        manager.allocateBed("P01");
        assertTrue(manager.releaseBed("P01"));
        assertEquals(0, manager.getOccupiedBeds().size());
    }

    @Test
    public void testPreventBedAllocationWhenFull() {
        for (int i = 1; i <= 20; i++) {
            String id = "P" + i;
            Inpatient inpatient = new Inpatient(id, "Test", "User" + i, 20 + i, "Other", "Stable", "General Ward", "Unassigned");
            manager.registerPatient(inpatient);
            manager.allocateBed(id);
        }
        Inpatient extraInpatient = new Inpatient("P21", "Overflow", "User", 30, "Male", "Critical", "General Ward", "Unassigned");
        manager.registerPatient(extraInpatient);
        assertThrows(IllegalStateException.class, () -> manager.allocateBed("P21"));
    }

    @Test
    public void testSortPatientsBySurname() {
        Patient p1 = new Patient("P01", "John", "Zebra", 30, "Male", "Fever", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P02", "Jane", "Alpha", 25, "Female", "Flu", PatientCategory.EMERGENCY);
        manager.registerPatient(p1);
        manager.registerPatient(p2);
        manager.sortPatientsByName();
        assertEquals("Alpha", manager.getPatients().get(0).getLastName());
    }
}