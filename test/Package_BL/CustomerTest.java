package Package_BL;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomerTest {

    private static final String TEST_CUSTOMER_FILE = "test_customer_file.txt";

    @BeforeAll
    public void setUp() throws Exception {
        // Set up a test file for Customer operations
        File testFile = new File(TEST_CUSTOMER_FILE);
        if (!testFile.exists()) {
            testFile.createNewFile();
        }

        try (PrintWriter writer = new PrintWriter(testFile)) {
            writer.println("0001,John Doe,123 Street,1234567890,12345,Residential,Single,01/01/2023,100,50");
            writer.println("0002,Jane Doe,456 Avenue,0987654321,67890,Commercial,Three,15/03/2023,200,150");
        }

        // Set the test file path in the projectTxtFiles class
        projectTxtFiles.CustomerFile = TEST_CUSTOMER_FILE;
    }

    @Test
    public void testConstructorAndGetters() {
        Customer customer = new Customer("12345", "John Smith", "789 Road", "5555555555", "Residential", "Single");

        assertNotNull(customer.getCustomerId());
        assertEquals("12345", customer.getCnic());
        assertEquals("John Smith", customer.getCustomerName());
        assertEquals("789 Road", customer.getAddress());
        assertEquals("5555555555", customer.getPhoneNumber());
        assertEquals("Residential", customer.getCustomerType());
        assertEquals("Single", customer.getMeterType());
    }

    @Test
    public void testSetters() {
        Customer customer = new Customer("12345", "John Smith", "789 Road", "5555555555", "Residential", "Single");

        customer.setName("Jane Smith");
        customer.setAddress("456 Avenue");
        customer.setPhoneNumber("9876543210");
        customer.setCustomerType("Commercial");
        customer.setMeterType("Three");

        assertEquals("Jane Smith", customer.getCustomerName());
        assertEquals("456 Avenue", customer.getAddress());
        assertEquals("9876543210", customer.getPhoneNumber());
        assertEquals("Commercial", customer.getCustomerType());
        assertEquals("Three", customer.getMeterType());
    }

    @Test
    public void testGenerateCustomerId() {
        String generatedId = Customer.generateCustomerId();
        assertEquals("0003", generatedId);
    }

    @Test
    public void testToFileFormat() {
        Customer customer = new Customer("12345", "John Smith", "789 Road", "5555555555", "Residential", "Single");
        String fileFormat = customer.toFileFormat();

        assertTrue(fileFormat.contains("John Smith"));
        assertTrue(fileFormat.contains("Residential"));
        assertTrue(fileFormat.contains("789 Road"));
    }

    @Test
    public void testReadDataFromCustomerDB() {
        Object[][] data = Customer.readDataFromCustomerDB();
        assertEquals(2, data.length);
        assertEquals("John Doe", data[0][1]);
        assertEquals("Residential", data[0][5]);
    }

    @Test
    public void testSinglePhaseCheck() {
        boolean isSinglePhase = Customer.singlePhaseCheck("0001");
        assertTrue(isSinglePhase);

        boolean isNotSinglePhase = Customer.singlePhaseCheck("0002");
        assertFalse(isNotSinglePhase);
    }

    @Test
    public void testGetMeterCountForCNIC() {
        int meterCount = Customer.getMeterCountForCNIC("12345", TEST_CUSTOMER_FILE);
        assertEquals(0, meterCount);

        int noMeterCount = Customer.getMeterCountForCNIC("00000", TEST_CUSTOMER_FILE);
        assertEquals(0, noMeterCount);
    }

    @Test
    public void testGetFirstIndexFromLastLine() {
        int lastIndex = Customer.getFirstIndexFromLastLine(TEST_CUSTOMER_FILE);
        assertEquals(2, lastIndex);
    }

    @Test
    public void testSaveChangesToCustomerDB() {
        Customer customer = new Customer("12345", "Jane Smith", "456 Avenue", "9876543210", "Commercial", "Three");
        Object[][] initialData = Customer.readDataFromCustomerDB();

        DefaultTableModel tableModel = new DefaultTableModel(initialData, new String[]{"CustomerId", "Name", "Address", "PhoneNumber", "CNIC", "CustomerType", "MeterType", "ConnectionDate", "RegularUnits", "PeakUnits"});
        tableModel.addRow(new Object[]{customer.getCustomerId(), customer.getCustomerName(), customer.getAddress(), customer.getPhoneNumber(), customer.getCnic(), customer.getCustomerType(), customer.getMeterType(), customer.getCurrentDate(), 0, 0});

        boolean saved = Customer.saveChangesToCustomerDB(tableModel);
        assertTrue(saved);

        Object[][] updatedData = Customer.readDataFromCustomerDB();
        assertEquals(initialData.length + 1, updatedData.length);
    }

    @AfterAll
    public void tearDown() throws Exception {
        File testFile = new File(TEST_CUSTOMER_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
    }
}
