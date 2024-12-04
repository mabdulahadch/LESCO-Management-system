package Package_BL;

import Font.LoadFont;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class LescoServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            LoadFont.loadCustomFont();
            System.out.println("LESCO Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept a new client
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectIn = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error initializing streams: " + e.getMessage());
            
        }
    }

    @Override
    public void run() {
        try {
            Customer loggedInCustomer = null;
            Employee loggedInEmployee = null;

            while (true) {

                String command = (String) objectIn.readObject();
                //System.out.println("Received command: " + command);
                if (command == null)
                    break;



                // customer
                if ("LOGINASCUSTOMER".equalsIgnoreCase(command)) {

                    String userID = (String) objectIn.readObject();
                    String userCNIC = (String) objectIn.readObject();
                    System.out.println("Received UserID: " + userID + ", UserCNIC: " + userCNIC);

                    loggedInCustomer = custLogin(userID, userCNIC); // Log the user in
                    if (loggedInCustomer != null) {
                        objectOut.writeObject("SUCCESS"); // Send login success to client
                        System.out.println("Login successful for UserID: " + userID);
                    } else {
                        objectOut.writeObject("FAILURE"); // Send login failure to client
                        System.out.println("Login failed for UserID: " + userID);
                    }
                } else if ("getName".equalsIgnoreCase(command)) {
                    if (loggedInCustomer != null) {
                        objectOut.writeObject(loggedInCustomer.getName()); // Send name to client
                        System.out.println("Sent Name: " + loggedInCustomer.getName());
                    } else {
                        objectOut.writeObject("ERROR: Not logged in"); // Send error if not logged in
                        System.out.println("Client requested name but is not logged in.");
                    }
                } else if ("getCustomerBill".equalsIgnoreCase(command)) {
                    if (loggedInCustomer != null) {
                        try {
                            Object[][] billData = loggedInCustomer.readDataFromBillingDB(); // Fetch bill data
                            objectOut.writeObject(billData); // Send bill data to client
                            System.out.println("Sent bill data object to the client.");
                        } catch (Exception e) {
                            objectOut.writeObject("ERROR: Unable to fetch bill data.");
                            e.printStackTrace();

                        }
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                    }
                } else if ("getCustomerCNIC".equalsIgnoreCase(command)) {
                    try {
                        // Fetch CNIC details
                        Object[][] cnicData = loggedInCustomer.displayCNICDetail(loggedInCustomer.getCustomerId());
                        objectOut.writeObject(cnicData); // Send CNIC data to client
                        System.out.println("Sent CNIC data object to the client.");
                    } catch (Exception e) {
                        objectOut.writeObject("ERROR: Unable to fetch CNIC data.");
                        e.printStackTrace();
                    }
                } else if ("saveCNICChanges".equalsIgnoreCase(command)) {
                    System.out.println("saveCNICChanges");
                    try {
                        String[] columnNames = { "Consumer ID", "CNIC #", "Issue Date", "Expiry Date" };
                        Object[][] updatedCNICData = (Object[][]) objectIn.readObject();
                        DefaultTableModel updatedModel = new DefaultTableModel(updatedCNICData, columnNames);
                        boolean updateSuccessful = loggedInCustomer.saveChangesToNADRADB(updatedModel);
                        System.out.println(updateSuccessful);

                        if (updateSuccessful) {
                            objectOut.writeObject("success");
                            objectOut.flush();
                            System.out.println("CNIC data updated successfully.");
                        } else {
                            objectOut.writeObject("failure");
                            System.out.println("Failed to update CNIC data.");
                        }
                    } catch (Exception e) {
                        objectOut.writeObject("ERROR: Unable to update CNIC data.");
                        e.printStackTrace();
                    }
                }

                // employee
                 if ("LOGINASEMPLOYEE".equalsIgnoreCase(command)) {
                    String userName = (String) objectIn.readObject();
                    String password = (String) objectIn.readObject();
                    System.out.println("Received UserID: " + userName + ", Password: " + password);

                    // Call employee login logic
                    loggedInEmployee = empLogin(userName, password);

                    if (loggedInEmployee != null) {
                        objectOut.writeObject("SUCCESS");
                        System.out.println("Login successful for UserID: " + userName);
                    } else {
                        objectOut.writeObject("FAILURE");
                        System.out.println("Login failed for UserID: " + userName);
                    }
                } else if ("getUserName".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        objectOut.writeObject(loggedInEmployee.getUserName());
                        System.out.println("Sent Name: " + loggedInEmployee.getUserName());
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                    }
                }

                // Add New Customer (Employee Action)
                else if ("AddNewCustomer".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            String cnic = (String) objectIn.readObject();
                            String name = (String) objectIn.readObject();
                            String address = (String) objectIn.readObject();
                            String phoneNumber = (String) objectIn.readObject();
                            String customerType = (String) objectIn.readObject();
                            String meterType = (String) objectIn.readObject();
                
                            System.out.println("Received new customer details: " +
                                    "\nCNIC: " + cnic +
                                    "\nName: " + name +
                                    "\nAddress: " + address +
                                    "\nPhone Number: " + phoneNumber +
                                    "\nCustomer Type: " + customerType +
                                    "\nMeter Type: " + meterType);
                
                                    String filepath="src/Package_DB/CustomerInfo.txt";
                            String result = loggedInEmployee.addCustomerDetails(filepath, cnic, name, address, phoneNumber, customerType, meterType);
                            objectOut.writeObject("SUCCESS".equalsIgnoreCase(result) ? "SUCCESS" : "FAILURE: " + result);
                            System.out.println(("SUCCESS".equalsIgnoreCase(result) ? "Customer added successfully: " : "Failed to add customer: ") + name);
                        } catch (Exception e) {
                            objectOut.writeObject("ERROR: Unable to add customer.");
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                    }
                } 
                else if ("ADD_BILLING_INFO".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            // Reading individual values from the client
                            String customerId = (String) objectIn.readObject();
                            int regularUnits = objectIn.readInt();
                            int peakUnits = objectIn.readInt();
                
                            System.out.println("Received billing info: " +
                                    "\nCustomer ID: " + customerId +
                                    "\nRegular Units: " + regularUnits +
                                    "\nPeak Units: " + peakUnits);
                
                            boolean result = loggedInEmployee.addBillingInfo(customerId, regularUnits, peakUnits);
                
                            // Send result to the client
                            objectOut.writeObject(result ? "SUCCESS" : "FAILURE: Unable to add billing info.");
                            System.out.println((result ? "Billing info added successfully for Customer ID: " : "Failed to add billing info for Customer ID: ") + customerId);
                        } catch (Exception e) {
                            objectOut.writeObject("ERROR: Unable to add billing info.");
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                    }
                } else if ("GET_CUSTOMER_IDS".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            ArrayList<String> customerIds = Employee.getAllcustomerIdsWithoutBill();
                            objectOut.writeObject(customerIds);
                            System.out.println("Customer IDs without bill sent to client.");
                        } catch (Exception e) {
                            objectOut.writeObject(new ArrayList<>());
                            System.out.println("Error retrieving customer IDs without bill.");
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject(new ArrayList<>());
                        System.out.println("Error: Not logged in.");
                    }
                } else if ("CHECK_SINGLE_PHASE".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            String customerId = (String) objectIn.readObject();
                            boolean isSinglePhase = Employee.isSinglePhase(customerId);
                            objectOut.writeObject(isSinglePhase);
                            System.out.println("Single-phase status sent for Customer ID: " + customerId);
                        } catch (Exception e) {
                            objectOut.writeObject(false);
                            System.out.println("Error checking single-phase status for Customer ID.");
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject(false);
                        System.out.println("Error: Not logged in.");
                    }
                }
                else if ("fetchTariffData".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                         
                            // Fetch tariff data from the database (assuming this method exists in your system)
                            Object[][] tariffData = loggedInEmployee.readDataFromTariffDB();
                            objectOut.writeObject(tariffData); // Send tariff data to client
                            System.out.println("Tariff tax data sent to client.");
                        } catch (Exception e) {
                            objectOut.writeObject(new Object[0][0]); // Send empty data in case of error
                            System.out.println("Error retrieving tariff tax data.");
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject(new Object[0][0]); // Not logged in
                        System.out.println("Error: Not logged in.");
                    }
                }else if ("saveTariffChanges".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {

                            String[] columnNames = {"Consumer Type", "Meter Type", "Regular Units", "Peak Units", "Tax %", "Fixed Tax"};
                            Object[][] updatedCNICData = (Object[][]) objectIn.readObject();
                            DefaultTableModel updatedModel = new DefaultTableModel(updatedCNICData, columnNames);
                           
                            boolean result = loggedInEmployee.saveChangesToTariffTaxDB(updatedModel);
                            
                            // Check if the data was saved successfully
                            if (result) {
                                objectOut.writeObject("success"); // Send success message to client
                                System.out.println("Tariff tax data updated successfully.");
                            } else {
                                objectOut.writeObject("failure"); // Send failure message to client
                                System.out.println("Failed to update tariff tax data.");
                            }
                        } catch (IOException ioEx) {
                            // Handle IO exceptions like reading/writing issues
                            objectOut.writeObject("error: IO Exception"); // Send error message to client
                            System.out.println("Error while saving tariff tax data: " + ioEx.getMessage());
                            ioEx.printStackTrace();
                        } catch (ClassNotFoundException cnfEx) {
                            // Handle class cast or object deserialization issues
                            objectOut.writeObject("error: Class Not Found Exception"); // Send error message to client
                            System.out.println("Error while deserializing the data: " + cnfEx.getMessage());
                            cnfEx.printStackTrace();
                        } catch (Exception e) {
                            // Generic error handling for any unexpected issues
                            objectOut.writeObject("error: General Exception"); // Send error message to client
                            System.out.println("Error saving tariff tax data: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject("error: Not logged in"); // Not logged in message
                        System.out.println("Error: Employee is not logged in.");
                    }
                }                
                
                else if ("readDataFromCustomerDB".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            // Fetch customer data
                            Object[][] custData = loggedInEmployee.readDataFromCustomerDB();
                
                            // Send data to client
                            objectOut.writeObject(custData);
                            objectOut.flush(); // Ensure data is sent immediately
                            System.out.println("Sent customer data object to the client.");
                        } catch (Exception e) {
                            // Log error and send response
                            objectOut.writeObject("ERROR: Unable to fetch customer data. Details: " + e.getMessage());
                            objectOut.flush();
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                        objectOut.flush();
                        System.out.println("Error: Employee is not logged in.");
                    }
                } 
                else if ("saveChangesToCustomerDB".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            Object[][] updatedCustomerData = (Object[][]) objectIn.readObject();
                
                            String[] columnNames = {"ID", "CNIC", "Name", "Address", "Phone", "Customer Type", "Meter Type", "Date", "RUC", "PHUC"};
                            DefaultTableModel updatedModel = new DefaultTableModel(updatedCustomerData, columnNames);
                
                            boolean result = loggedInEmployee.saveChangesToCustomerDB(updatedModel);
                
                            if (result) {
                                objectOut.writeObject("success");
                                System.out.println("Customer data updated successfully.");
                            } else {
                                objectOut.writeObject("FAILURE: Unable to update customer data.");
                                System.out.println("Failed to update customer data.");
                            }
                            objectOut.flush(); // Ensure data is sent
                        } catch (IOException | ClassNotFoundException ex) {
                            objectOut.writeObject("ERROR: Unable to process customer data. Details: " + ex.getMessage());
                            objectOut.flush();
                            ex.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                        objectOut.flush();
                        System.out.println("Error: Employee is not logged in.");
                    }
                }
                else if ("readBillingInfo".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            // Fetch customer data
                            Object[][] custData = loggedInEmployee.readDataFromFileToDisplayBill();
                
                            // Send data to client
                            objectOut.writeObject(custData);
                            objectOut.flush(); // Ensure data is sent immediately
                            System.out.println("Sent customer data object to the client.");
                        } catch (Exception e) {
                            // Log error and send response
                            objectOut.writeObject("ERROR: Unable to fetch customer data. Details: " + e.getMessage());
                            objectOut.flush();
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                        objectOut.flush();
                        System.out.println("Error: Employee is not logged in.");
                    }
                } 
                else if ("saveBillingInfo".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            // Receive the updated billing data as Object[][] from the client
                            Object[][] updatedBillingData = (Object[][]) objectIn.readObject();
                            
                            // Define the column names for the table
                            String[] columnNames = {"ID", "Month", "Regular", "Peak", "Cost of Electricity", "SalesTax", "Fixed $", "Total Bill", "Reading Date", "DueDate", "Bill Status"};
                            Object[][] data = loggedInEmployee.readDataFromFileToDisplayBill();

                            // Determine the most recent month
                          
                           
                                String latestMonth = "";
                        
                                for (int i = 0; i < data.length; i++) {
                                    String billMonth = data[i][1].toString(); // Month is in the 2nd column (index 1)
                                    //System.out.println("Bill"+billMonth);
                                    if (latestMonth.compareTo(billMonth) < 0) {
                                        latestMonth = billMonth;
                                        // System.out.println("Latest"+latestMonth);
                                    }
                                }
                                String latestEditableMonth = latestMonth;
                            
                            // Create a DefaultTableModel with the received data and column names
                            DefaultTableModel updatedModel = new DefaultTableModel(updatedBillingData, columnNames);
                            
                            // Call the saveChangesToBillingDB method with the updated model and the latest editable month
                            boolean result = loggedInEmployee.saveChangesToBillingDB(updatedModel, latestEditableMonth);
                            
                            // Respond to the client based on the result of the save operation
                            if (result) {
                                objectOut.writeObject("success");
                                System.out.println("Billing information updated successfully.");
                            } else {
                                objectOut.writeObject("error: Unable to update billing information.");
                                System.out.println("Failed to update billing information.");
                            }
                            objectOut.flush(); // Ensure the data is sent
                            
                        } catch (IOException | ClassNotFoundException ex) {
                            // Handle any exceptions that occur during the process
                            try {
                                objectOut.writeObject("error: Unable to process billing data. Details: " + ex.getMessage());
                                objectOut.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ex.printStackTrace(); // Log the exception
                        }
                    } else {
                        // Handle the case where no employee is logged in
                        try {
                            objectOut.writeObject("error: Not logged in");
                            objectOut.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Error: Employee is not logged in.");
                    }
                }
                else if ("readBillReport".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            // Fetch customer data
                            Object[][] custData = loggedInEmployee.viewPaid_UnpaidBillReport();
                            // Send data to client
                            objectOut.writeObject(custData);
                            objectOut.flush(); // Ensure data is sent immediately
                            System.out.println("Sent customer data object to the client.");
                        } catch (Exception e) {
                            // Log error and send response
                            objectOut.writeObject("ERROR: Unable to fetch customer data. Details: " + e.getMessage());
                            objectOut.flush();
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                        objectOut.flush();
                        System.out.println("Error: Employee is not logged in.");
                    }
                }  
                else if ("CNICreport".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            // Fetch customer data
                            Object[][] custData = loggedInEmployee.CNICExpiresIn30days();
                            // Send data to client
                            objectOut.writeObject(custData);
                            objectOut.flush(); // Ensure data is sent immediately
                            System.out.println("Sent customer data object to the client.");
                        } catch (Exception e) {
                            // Log error and send response
                            objectOut.writeObject("ERROR: Unable to fetch customer data. Details: " + e.getMessage());
                            objectOut.flush();
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                        objectOut.flush();
                        System.out.println("Error: Employee is not logged in.");
                    }
                }
                else if ("ValidateCurrentPass".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            // Read the current password sent by the client
                            String currentPassword = (String) objectIn.readObject();
                            boolean isValid = loggedInEmployee.isValidPass(currentPassword);
                
                            // Send response to the client (true if valid, false if not)
                            objectOut.writeObject(isValid);
                            objectOut.flush();
                            System.out.println("Password validation response sent to client.");
                        } catch (Exception e) {
                            objectOut.writeObject(false); // Send false if error occurs
                            objectOut.flush();
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                        objectOut.flush();
                        System.out.println("Error: Employee is not logged in.");
                    }
                } else if ("UpdatePass".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        try {
                            // Read the new password from the client
                            String newPassword = (String) objectIn.readObject();
                            loggedInEmployee.updateEmpPassword(newPassword);
                
                            // Send success message to the client
                            objectOut.writeObject("Password Updated Successfully");
                            objectOut.flush();
                            System.out.println("Password updated successfully.");
                        } catch (Exception e) {
                            objectOut.writeObject("ERROR: Failed to update password");
                            objectOut.flush();
                            e.printStackTrace();
                        }
                    } else {
                        objectOut.writeObject("ERROR: Not logged in");
                        objectOut.flush();
                        System.out.println("Error: Employee is not logged in.");
                    }
                }
                  
            }
        } catch (Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
        // } finally {
        //     try {
        //         if (objectIn != null) {
        //             objectIn.close();
        //         }
        //         if (objectOut != null) {
        //             objectOut.close();
        //         }
        //         if (clientSocket != null && !clientSocket.isClosed()) {
        //             clientSocket.close();
        //         }
        //     } catch (IOException e) {
        //         System.out.println("Error closing client resources: " + e.getMessage());
        //     }
        //}
    }

    public static Customer custLogin(String userID, String userCNIC) {

        File file = new File(projectTxtFiles.CustomerFile);

        if (file.length() == 0) {
            System.out.println("Nothing in the file! No records found.");
            return null;
        }

        if (validateCustLogin(userID, userCNIC)) {
            // System.out.println("Login successful!");
            return new Customer(userID, userCNIC);
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
                if (custData[0].equals(userId) && custData[4].equals(cnic)) {
                    System.out.println("Logged In as : " + custData[1]);
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reasding file");
        }
        return false;
    }

    public static Employee empLogin(String username, String password) {
        File file = new File(projectTxtFiles.EmployeesFile);

        if (file.length() == 0) {
            System.out.println("Nothing in the file! No records found.");
            return null;
        }

        if (validateEmpLogin(username, password)) {
            System.out.println("Login successful!");
            return new Employee(username, password);
        } else {
            System.out.println("Invalid username or password. Enter Again!");
            return null;
        }
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

}
