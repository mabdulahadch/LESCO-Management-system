package Package_BL;

import Font.LoadFont;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            Customer loggedInCustomer = null;
            Employee loggedInEmployee = null;

            while (true) {
                String command = in.readLine();

                if ("LOGINASCUSTOMER".equalsIgnoreCase(command)) {
                    String userID = in.readLine();
                    String userCNIC = in.readLine();
                    System.out.println("Received UserID: " + userID + ", UserCNIC: " + userCNIC);

                    loggedInCustomer = custLogin(userID, userCNIC);
                    if (loggedInCustomer != null) {
                        out.println("SUCCESS");
                        System.out.println("Login successful for UserID: " + userID);
                    } else {
                        out.println("FAILURE");
                        System.out.println("Login failed for UserID: " + userID);
                    }
                } else if ("getName".equalsIgnoreCase(command)) {
                    if (loggedInCustomer != null) {
                        out.println(loggedInCustomer.getName()); // Respond with the customer's name
                        System.out.println("Sent Name: " + loggedInCustomer.getName());
                    } else {
                        out.println("ERROR: Not logged in");
                        System.out.println("Client requested name but is not logged in.");
                    }
                } else if ("getCustomerBill".equalsIgnoreCase(command)) {

                    try (ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream())) {
                        Object[][] billData = loggedInCustomer.readDataFromBillingDB();
                        objectOut.writeObject(billData);
                        objectOut.flush();
                        System.out.println("Sent bill data object to the client.");
                    } catch (Exception e) {
                    }
                } else {
                    out.println("ERROR: Not logged in");
                    System.out.println("Client requested name but is not logged in.");
                }




                
                if ("LOGINASEMPLOYEE".equalsIgnoreCase(command)) {
                    String userName = in.readLine();
                    String password = in.readLine();
                    System.out.println("Received UserID: " + userName + ", UserCNIC: " + password);

                    loggedInEmployee = empLogin(userName, password);
                    if (loggedInEmployee != null) {
                        out.println("SUCCESS");
                        System.out.println("Login successful for UserID: " + userName);
                    } else {
                        out.println("FAILURE");
                        System.out.println("Login failed for UserID: " + userName);
                    }
                } else if ("getUserName".equalsIgnoreCase(command)) {
                    if (loggedInEmployee != null) {
                        out.println(loggedInEmployee.getUserName()); // Respond with the customer's name
                        System.out.println("Sent Name: " + loggedInEmployee.getUserName());
                    } else {
                        out.println("ERROR: Not logged in");
                        System.out.println("Client requested name but is not logged in.");
                    }
                } else if ("getCustomerBill".equalsIgnoreCase(command)) {

                }

                // ifClientIsCustomer(command, in, out, loggedInCustomer);
                // ifClientIsEmployee(command, in, out, loggedInEmployee);
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                // clientSocket.close();
            } catch (Exception e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
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
