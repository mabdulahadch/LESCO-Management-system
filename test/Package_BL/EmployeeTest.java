package Package_BL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {
    Employee employee;
    String CUSTOMERFILE = "test/Package_BL/testCustomerData.txt";
    String EMPLOYEEFILE = "test/Package_BL/testEmployeeData.txt";

    @BeforeEach
    void setUp() {
        employee = ClientHandler.empLogin("Ahad", "123");

        try {
            File customerFile = new File(CUSTOMERFILE);
            File employeeFile = new File(EMPLOYEEFILE);

            // Create the files if they do not exist
            if (!customerFile.exists()) {
                customerFile.createNewFile();
            }
            if (!employeeFile.exists()) {
                employeeFile.createNewFile();
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

    @Test
    void addBillingInfoValid() {
        Boolean actualResult = employee.addBillingInfo("0001", 300, 100);
        Boolean expectedResult = true;

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void addBillingInfoInvalid() {
        Boolean actualResult = employee.addBillingInfo("0003", 500, 0);
        Boolean expectedResult = false;

        assertEquals(expectedResult, actualResult);
    }


}
