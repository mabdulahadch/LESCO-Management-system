package Package_BL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class tempMain {
    
    
    
    // employee functions that will use before making employee object
    public static Employee empLogin() {
        Scanner input = new Scanner(System.in);
        File file = new File(projectTxtFiles.EmployeesFile);

        if (file.length() == 0) {
            System.out.println("Nothing in the file! No records found.");
            return null;
        }

        System.out.println("Enter Username:");
        String username = input.nextLine();

        System.out.println("Enter Password:");
        String password = input.nextLine();

        if (validateEmpLogin(username, password)) {
            System.out.println("Login successful!");
            return new Employee(username, password);
        } else {
            System.out.println("Invalid username or password. Enter Again!");
            return null;
        }
    }

    public static void empSignUp() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Employee UserName");
        String username = input.nextLine();

        File file = new File(projectTxtFiles.EmployeesFile);

        if (file.length() != 0) {                             // if file is not empty search the existing user
            if (usernameExists(username)) {
                System.out.println("Sorry, username already exists. Try another.");
                return;
            }
        }
        System.out.println("Enter Employee Password");
        String password = input.nextLine();

        Employee employee = new Employee(username, password);
        employee.writeEmployeeDataInFile();
    }

    public static boolean validateEmpLogin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.EmployeesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] employeeData = line.split(",");
                if (employeeData[0].equals(username) && employeeData[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reasding file");
        }
        return false;
    }

    public static boolean usernameExists(String username) {
        File file = new File(projectTxtFiles.EmployeesFile);

        if (!file.exists()) {
            System.out.println("File does not exist.");
            return false;
        }

        // File exists, proceed with reading
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] employeeData = line.split(",");
                if (employeeData[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading the file.");
        }
        return false;
    }

    // Customer functions that will use before making Customer object                                                                            
    public static Customer custLogin() {
        Scanner input = new Scanner(System.in);
        File file = new File(projectTxtFiles.CustomerFile);

        if (file.length() == 0) {
            System.out.println("Nothing in the file! No records found.");
            return null;
        }

        System.out.println("Enter UserId:");
        String userId = input.nextLine();

        System.out.println("Enter CNIC:");
        String cnic = input.nextLine();

        if (validateCustLogin(userId, cnic)) {
            //System.out.println("Login successful!");
            return new Customer(userId, cnic);
        } else {
            System.out.println("Invalid userId or cnic. Enter Again!");
            return null;
        }
    }

    public static boolean validateCustLogin(String userId, String cnic) {
        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] custData = line.split(",");
                if (custData[0].equals(userId) && custData[1].equals(cnic)) {
                    System.out.println("Logged In as : " + custData[2]);
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reasding file");
        }
        return false;
    }

    
    public static void tempp() {

        Scanner input = new Scanner(System.in);
        System.out.println("LESCO Billing System");

        System.out.println("1 for Employee Dashboard, 2 for Customer Dashboard");
        int demand = input.nextInt();
        input.nextLine();

        if (demand == 1) {
            System.out.println("1. for Login, 2. for Signup");
            int choice = input.nextInt();
            input.nextLine();

            if (choice == 1) {
                Employee emp = empLogin();        // if no employee Found it will return Null and no object made
                if (emp != null) {

                    OUTER:
                    while (true) {
                        System.out.println("1.  Add New Customer");
                        System.out.println("2.  Add New Billing Info.");
                        System.out.println("3.  Add or Update Triff Tax Info.");

                        System.out.println("4.  Update Customer Info.");
                        System.out.println("5.  Update Bill status.");
                        System.out.println("6.  Update Password.");

                        System.out.println("7.  View Customer Bills using ID.");
                        System.out.println("8.  View Reports of Paid/Unpaid bills.");
                        System.out.println("9.  View Reports of Customers CNIC");

                        System.out.println("10. Exit");

                        int featureChoice = input.nextInt();
                        input.nextLine();

                        switch (featureChoice) {
                            case 1 ->
                               // emp.addCustomerDetails();
                                System.out.println("emp.addCustomerDetails()");
                            case 2 ->
                                System.out.println("emp.addBillingInfo();");
                            case 3 ->
                                //emp.addorupdateTariff(input)
                                System.out.println("emp.addorupdateTariff(input)");
                            case 4 ->
                                System.out.println("emp.updateCustomerInfo(input);");
                            case 5 ->
                                emp.changeBillStatus(input); // also update in the CustomerInfoFile that consumer Peak Hours + comsumer regualr Hours
                            case 6 ->
                                System.out.println("emp.updatePassword(input);");
                            case 7 ->
                                System.out.println("emp.viewUserBill(input)");
                            case 8 ->
                                //emp.viewPaidANDUnpaidBillAmount();
                                 System.out.println("emp.viewPaidANDUnpaidBillAmount()");
                            case 9 ->
                               // emp.CNICExpiryCheck();
                                 System.out.println("emp.CNICExpiryCheck()");
                            case 10 -> {
                                break OUTER;
                            }
                            default -> {
                                System.out.println("Plz Enter correct choice !");
                            }
                        }
                    }

                } else {
                    System.out.println("Login not successfull !");
                }
            } else {
                empSignUp();
            }
        } else {
            System.out.println("Login as Customer.");
            Customer cust = custLogin();

            if (cust != null) {

                OUTER_1:
                while (true) {
                    System.out.println("1. update the expiry date of their own CNIC");
                    System.out.println("2. view their current bill");
                    System.out.println("3. Exit");
                    int featureChoice = input.nextInt();
                    input.nextLine();

                    switch (featureChoice) {
                        case 1 ->
                           // cust.updateExpiryDateInNADRADB(input);
                            System.out.println("cust.updateExpiryDateInNADRADB(input);");
                        case 2 ->
                            System.out.println("cust.dispalyBill();");
                        case 3 -> {
                            break OUTER_1;
                        }
                        default -> {
                            System.out.println("Plz Enter correct choice !");
                        }
                    }
                }

            } else {
                System.out.println("Login not successfull !");
            }
        }
    }
}
