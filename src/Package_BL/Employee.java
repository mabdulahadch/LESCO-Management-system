package Package_BL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import javax.swing.table.DefaultTableModel;

public class Employee {

    private String userName;
    private String password;

    public Employee() {
        userName = "dummy";
        password = "123";
    }

    public Employee(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toFileFormat() {
        return userName + "," + password;
    }

    public void writeEmployeeDataInFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectTxtFiles.EmployeesFile, true))) {
            writer.write(this.toFileFormat());
            writer.newLine();
            System.out.println("Employee added successfully.");
        } catch (IOException e) {
            System.out.println("Error while writing to the file.");
        }
    }

    public boolean isValidPass(String currentPass) {
        return this.getPassword().equals(currentPass);
    }

    public void updateEmpPassword(String newPass) {

        this.setPassword(newPass);

        ArrayList<String> fileContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.EmployeesFile))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] employeeData = currentLine.split(",");
                String existingUserName = employeeData[0];

                if (existingUserName.equals(this.userName)) {
                    fileContent.add(this.toFileFormat()); // Add updated password
                } else {
                    fileContent.add(currentLine); // Keep the original line
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectTxtFiles.EmployeesFile))) {
            for (String line : fileContent) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to the file: " + e.getMessage());
        }

        System.out.println("Password updated successfully.");

    }

    public String addCustomerDetails(String FILE_NAME, String cnic, String name, String address, String phoneNumber, String customerType, String meterType) {

        while (true) {

            if (cnic.length() != 13 || !cnic.matches("\\d+")) {
                return "Invalid CNIC number!";
            } else if (!NADRA.isCNICValidInNADRADB(cnic)) {
                return "CNIC not found in NADRA database.";
            } else {
                break;
            }
        }

        if (Customer.getMeterCountForCNIC(cnic, FILE_NAME) >= 3) {
            return "Not Allowed! Maximum 3 meters allowed per CNIC.";
        }

        Customer customer = new Customer(cnic, name, address, phoneNumber, customerType, meterType);

        writeCustomerDataInFile(customer, FILE_NAME);
        return "true";
    }

    private void writeCustomerDataInFile(Customer customer, String FILE_NAME) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(customer.toFileFormat());
            writer.newLine();
            //System.out.println("Customer added successfully. Customer ID: " + customer.getCustomerId());
        } catch (IOException e) {
            System.out.println("Error while writing to the file.");
        }
    }

    public Object[][] CNICExpiresIn30days() {
        return NADRA.getCNICExpiresIn30days();
    }

    public boolean addBillingInfo(String customerID, int currentRegularUnits, int currentPeakUnits) {
        BillManagment billManagment = new BillManagment();
        return billManagment.addBillingInfoInFile(customerID, currentRegularUnits, currentPeakUnits);
    }

    public boolean saveChangesToTariffTaxDB(DefaultTableModel tableModel) {
        return TariffTax.saveChangesToTariffTaxDB(tableModel);
    }
    public boolean saveChangesToBillingDB(DefaultTableModel tableModel, String latestEditableMonth) {
        return BillManagment.saveChangesToBillingDB(tableModel,latestEditableMonth);
    }
    
    public boolean saveChangesToCustomerDB(DefaultTableModel tableModel) {
        return Customer.saveChangesToCustomerDB(tableModel);
    }

    public Object[][] viewPaid_UnpaidBillReport() {
        return BillManagment.calulateAmountUpdainANDPaidBills();
    }

    public void changeBillStatus(Scanner input) {
        BillManagment.changeBillStatus(input);
    }

    public Object[][] readDataFromCustomerDB() {
        return Customer.readDataFromCustomerDB();
    }

    public Object[][] readDataFromFileToDisplayBill() {
        return BillManagment.readDataFromFileToDisplayBillToEmp();
    }

    public static ArrayList<String> getAllcustomerIdsWithoutBill() {

        ArrayList<String> customerIdsWithoutBill = new ArrayList<>();
        ArrayList<String> billedCustomerIds = new ArrayList<>(); // Using ArrayList instead of Set

        try (BufferedReader billingReader = new BufferedReader(new FileReader(projectTxtFiles.BillingFile))) {
            String line;
            while ((line = billingReader.readLine()) != null) {
                String[] billingData = line.split(",");
                String customerId = billingData[0];
                String billingMonthYear = billingData[1];

                if (billingMonthYear.equals(getCurrentMonth())) {
                    billedCustomerIds.add(customerId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader customerReader = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile))) {
            String line;
            while ((line = customerReader.readLine()) != null) {
                String[] customerData = line.split(",");
                String customerId = customerData[0];

                if (!billedCustomerIds.contains(customerId)) {
                    customerIdsWithoutBill.add(customerId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return customerIdsWithoutBill;
    }

    private static String getCurrentMonth() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
        return formatter.format(new Date());
    }
    
        public static boolean isSinglePhase(String customerID){
            return Customer.singlePhaseCheck(customerID);
        }
    

}
