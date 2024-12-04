package Package_BL;


import javax.swing.table.DefaultTableModel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {
    Employee employee;
    String CUSTOMERFILE = "test/Package_BL/testCustomerData.txt";
    String EMPLOYEEFILE = "test/Package_BL/testEmployeeData.txt";
    String TARIFFTAXFILE = "test/Package_BL/testTariffData.txt";

    @BeforeEach
    void setUp() {
        employee = ClientHandler.empLogin("Ahad", "123");

        try {
            File customerFile = new File(CUSTOMERFILE);
            File employeeFile = new File(EMPLOYEEFILE);
            File taxFile = new File(TARIFFTAXFILE);

            if (!customerFile.exists()) {
                customerFile.createNewFile();
            }
            if (!employeeFile.exists()) {
                employeeFile.createNewFile();
            }
            if (!taxFile.exists()) {
                taxFile.createNewFile();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterEach
    void freeSource() {
        employee = null;
    }

    @Test
    void validateEmployeeUserName() {
        String actualResult = employee.getUserName();
        String expectedResult = "Ahad";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void validateEmployeePassword() {
        String actualResult = employee.getPassword();
        String expectedResult = "123";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void isValidPassword() {
        Boolean actualResult = employee.isValidPass("123");
        Boolean expectedResult = true;

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateEmployeePassword() {
        Boolean actualResult = employee.updateEmpPassword("321");
        Boolean expectedResult = true;

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void validateEmployeeFileFormat() {
        String actualResult = employee.toFileFormat();
        String expectedResult = "Ahad,123";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void validateEmployeeInvalid() {
        Employee actualResult = ClientHandler.empLogin("XYZ", "666");
        assertNull(actualResult, "Expected invalid credentials to fail validation");
    }

    @Test
    void validateEmployeeValid() {
        Employee actualResult = ClientHandler.empLogin("Ahad", "123");
        assertNotNull(actualResult, "Expected a valid Employee object to be returned");
    }


    @Test
    void addCustomerDetailsInvalid() {
        String actualResult = employee.addCustomerDetails(CUSTOMERFILE, "123456", "Ahmad", "123 Street", "1234567890", "Domestic", "Single");
        String expectedResult = "Invalid CNIC number!";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void addCustomerDetailsValid() {
        String actualResult = employee.addCustomerDetails(CUSTOMERFILE, "3298723423437", "Ahmad", "Jubblee Town", "90785634525", "Domestic", "Single");
        String expectedResult = "true";

        assertEquals(expectedResult, actualResult);
    }

//    @Test
//    void addBillingInfoValid() {
//        Boolean actualResult = employee.addBillingInfo("0001", 300, 100);
//        Boolean expectedResult = true;
//
//        assertEquals(expectedResult, actualResult);
//    }
//
//    @Test
//    void addBillingInfoInvalid() {
//        Boolean actualResult = employee.addBillingInfo("0003", 100, 0);
//        Boolean expectedResult = false;
//
//        assertEquals(expectedResult, actualResult, "Enter Correct (Current Regular Units cannot be smaller than Previous Consumed Units)");
//    }
//
//    @Test
//    void alreadyExistBillInfoInvalid() {
//        Boolean actualResult = employee.addBillingInfo("0003", 100, 0);
//        Boolean expectedResult = true;
//
//        assertEquals(expectedResult, actualResult, "This Month's Bill is already added for Customer: 0003");
//    }


//    @Test
//    void testCNICExpiresIn30days(){
//        Object[][] data = employee.CNICExpiresIn30days();
//
//    }




    @Test
    void testReadDataFromFile(){

        Object[][] result = employee.readDataFromTariffDB();

        Object[][] expected = {
                {"Domestic Type", "1", "5", " ", "17", "150"},
                {"Commercial Type", "1", "15", " ", "20", "250"},
                {"Domestic Type", "3", "8", "12", "17", "150"},
                {"Commercial Type", "3", "18", "25", "20", "250"}
        };

        assertEquals(expected.length, result.length);

        for (int i = 0; i < result.length; i++) {
            assertArrayEquals(expected[i], result[i]);
        }
    }

    @Test
    void testSaveChangesToTariffTaxDB(){

        String[] columns = {"Type", "Meter", "Regular Units", "Peak Units", "Tax", "Fixed Tax"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        tableModel.addRow(new Object[]{"Domestic Type", "1", "5", " ", "17", "155"});
        tableModel.addRow(new Object[]{"Commercial Type", "1", "15", " ", "20", "255"});


        boolean result = employee.saveChangesToTariffTaxDB(tableModel);

        assertTrue(result, "Failed to save changes to Tariff Tax DB");

        Object[][] resultData = employee.readDataFromTariffDB();
        Object[][] expectedData = {
                {"Domestic Type", "1", "5", " ", "17", "155"},
                {"Commercial Type", "1", "15", " ", "20", "255"},
                {"Domestic Type", "3", "8", "12", "17", "150"},
                {"Commercial Type", "3", "18", "25", "20", "250"}
        };

        assertEquals(expectedData.length, resultData.length, "Mismatch in data length");
        for (int i = 0; i < resultData.length; i++) {
            assertArrayEquals(expectedData[i], resultData[i], "Mismatch in row " + i);
        }

    }




}

