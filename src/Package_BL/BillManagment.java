package Package_BL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class BillManagment {

    public boolean addBillingInfoInFile(String customerID, int currentRegularUnits, int currentPeakUnits) {

        File file = new File(projectTxtFiles.BillingFile);
        boolean flag = false;

        ArrayList<String> previousBillingLines = null;

        if (file.exists() && file.length() != 0) {
            flag = true;
            previousBillingLines = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.BillingFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    previousBillingLines.add(line);
                }
            } catch (IOException e) {
                System.out.println("Error while reading file");
            }
        }

        ArrayList<String> newBillingLines = new ArrayList<>();

        try (
                BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile)); BufferedWriter writer = new BufferedWriter(new FileWriter(projectTxtFiles.BillingFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] customerData = line.split(",");

                if (customerData[0].equals(customerID)) {

                    boolean customerBillExists = false;
                    if (previousBillingLines != null) {
                        for (String billingLine : previousBillingLines) {
                            String[] billingData = billingLine.split(",");
                            if (billingData[0].equals(customerData[0]) && billingData[1].equals(getCurrentMonth())) {
                                customerBillExists = true;
                                break;
                            }
                        }
                    }

                    if (customerBillExists) {
                        System.out.println("This Month's Bill is already added for Customer: " + customerID);
                        break;
                    }

                    int previousRegularUnits = Integer.parseInt(customerData[8]);
                    if (currentRegularUnits < previousRegularUnits) {
                        System.out.println("Enter Correct (Current Regular Units cannot be smaller than Previous Consumed Units)");
                        return false;
                    }

                    int calculatedRegUnits = currentRegularUnits - previousRegularUnits;

                    int calculatedPeakUnits = 0;

                    if (customerData[6].equals("Three")) {
                        //System.out.println("2. Enter Current Meter Reading of (Peak Units) " + " Previous Reading = " + customerData[9]);

                        int previousPeakUnits = Integer.parseInt(customerData[9]);
                        if (currentPeakUnits < previousPeakUnits) {
                            System.out.println("Enter Correct (Current Peak Units cannot be smaller than Previous Consumed Units)");
                            return false;
                        }

                        calculatedPeakUnits = currentPeakUnits - previousPeakUnits;
                    }

                    double costOfElectricity = calculateElectricityCost(customerData[5], customerData[6], calculatedRegUnits, calculatedPeakUnits);
                    double salesTax = calculateSalesTax(costOfElectricity, customerData[5], customerData[6]);
                    double fixedCharges = getFixedCharges(customerData[5], customerData[6]);

//                    System.out.println("Cost"+ costOfElectricity);
//                    System.out.println("Tax"+salesTax);
//                    System.out.println("Fixed"+fixedCharges);
                    String dueDate = getDateAfter7Days(7);
                    String billPaidStatus = "Unpaid";
                    String readingEntryDate = getCurrentDate();
                    String billingMonth = getCurrentMonth();

                    double totalBillingAmount = costOfElectricity + salesTax + fixedCharges;

                    String newBillingLine = customerData[0] + "," + billingMonth + "," + currentRegularUnits + "," + currentPeakUnits + ","
                            + costOfElectricity + "," + salesTax + "," + fixedCharges + ","
                            + totalBillingAmount + "," + readingEntryDate + "," + dueDate + "," + billPaidStatus;

                    newBillingLines.add(newBillingLine);

                    System.out.println("Successfully Added Billing Info");
                }
            }

            if (flag) {
                newBillingLines.addAll(previousBillingLines);
            }

            for (String billingData : newBillingLines) {
                writer.write(billingData);
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error while reading file");
        }
        return true;
    }

    private static String getDateAfter7Days(int daysToAdd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.now().plusDays(daysToAdd).format(formatter);
    }

    private static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date());
    }

    private static String getCurrentMonth() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
        return formatter.format(new Date());
    }

    public static double getFixedCharges(String customerType, String meterType) {
        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.TariffFile))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] lineData = line.split(",");

                // Check the conditions based on meterType and customerType
                if (meterType.equals("Single") && customerType.equals("Domestic") && lineNumber == 1) {
                    return Double.parseDouble(lineData[4]); // Line 1
                } else if (meterType.equals("Single") && customerType.equals("Commercial") && lineNumber == 2) {
                    return Double.parseDouble(lineData[4]); // Line 2
                } else if (meterType.equals("Three") && customerType.equals("Domestic") && lineNumber == 3) {
                    return Double.parseDouble(lineData[4]); // Line 3
                } else if (meterType.equals("Three") && customerType.equals("Commercial") && lineNumber == 4) {
                    return Double.parseDouble(lineData[4]); // Line 4
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return 0;
    }

    public static double calculateSalesTax(double costOfElectricity, String customerType, String meterType) {

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.TariffFile))) {
            String line;
            int lineNumber = 0;
            Double result = 0.0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] lineData = line.split(",");

                if (meterType.equals("Single") && customerType.equals("Domestic") && lineNumber == 1) {
                    result = (Double.parseDouble(lineData[3]) / 100) * costOfElectricity;
                    return result; // Line 1 for 1Phase, dom
                } else if (meterType.equals("Single") && customerType.equals("Commercial") && lineNumber == 2) {
                    result = (Double.parseDouble(lineData[3]) / 100) * costOfElectricity;
                    return result; // Line 2 for 1Phase, com
                } else if (meterType.equals("Three") && customerType.equals("Domestic") && lineNumber == 3) {
                    result = (Double.parseDouble(lineData[3]) / 100) * costOfElectricity;
                    return result; // Line 3 for 3Phase, dom
                } else if (meterType.equals("Three") && customerType.equals("Commercial") && lineNumber == 4) {
                    result = (Double.parseDouble(lineData[3]) / 100) * costOfElectricity;
                    return result; // Line 4 for 3Phase, com
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return 0;
    }

    public static double calculateElectricityCost(String customerType, String meterType, int regularUnits, int peakUnits) {

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.TariffFile))) {
            String line;
            int lineNumber = 0;
            Double result = 0.0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] lineData = line.split(",");

                if (meterType.equals("Single") && customerType.equals("Domestic") && lineNumber == 1) {
                    result = Double.parseDouble(lineData[1]) * regularUnits;
                    return result; // Line 1 for 1Phase, dom
                } else if (meterType.equals("Single") && customerType.equals("Commercial") && lineNumber == 2) {
                    result = Double.parseDouble(lineData[1]) * regularUnits;
                    return result; // Line 2 for 1Phase, com
                } else if (meterType.equals("Three") && customerType.equals("Domestic") && lineNumber == 3) {
                    result = (Double.parseDouble(lineData[1]) * regularUnits) + (Double.parseDouble(lineData[2]) * peakUnits);
                    return result; // Line 3 for 3Phase, dom
                } else if (meterType.equals("Three") && customerType.equals("Commercial") && lineNumber == 4) {
                    //System.out.println("In calculate Bill "+ (Double.parseDouble(lineData[2]) * peakUnits));
                    result = (Double.parseDouble(lineData[1]) * regularUnits) + (Double.parseDouble(lineData[2]) * peakUnits);
                    //System.out.println("Reslut"+result);
                    return result; // Line 4 for 3Phase, com
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // If no match is found, return 0 or handle it appropriately
        return 0;
    }

    public static void displayUserBill(String userId) {

        File file = new File(projectTxtFiles.BillingFile);
        boolean flag = false;

        if (!file.exists() || file.length() == 0) {
            System.out.println("No Billing Info present ! ");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] customerData = line.split(",");

                if (customerData[0].equals(userId)) {

                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Error while reading file");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile))) {
            String line;
            boolean flag1 = true;
            while ((line = reader.readLine()) != null) {
                String[] customerData = line.split(",");

                if (customerData[0].equals(userId)) {

                    System.out.println("Customer ID   : " + customerData[0]);
                    System.out.println("Customer Name : " + customerData[2]);
                    System.out.println("Address       : " + customerData[3]);
                    System.out.println("Phone No.     : " + customerData[4]);
                    System.out.println("Customer Type : " + (customerData[5].equals("dom") ? "Domestic" : "Commercial"));
                    System.out.println("Meter Type    : " + (customerData[6].equals("3") ? "3 Phase" : "1 Phase"));
                    flag1 = false;
                    break;
                }

            }
            if (flag1 == true) {
                System.out.println("No user found with this ID !");
                return;
            }
        } catch (IOException e) {
            System.out.println("Error while reading file");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.BillingFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] billingData = line.split(",");

                if (billingData[0].equals(userId)) {

                    System.out.println("\033[34m-----> " + billingData[1] + " <-----\033[0m");
                    System.out.println("Total Electricity cost   : " + billingData[4]);
                    System.out.println("Total Sales Tax          : " + billingData[5]);
                    System.out.println("Fixed Charges            : " + billingData[6]);

                    if (billingData[10].equals("Unpaid")) {
                        System.out.println("Due Date                 : " + billingData[9]);
                        System.out.println("\033[1;31mTotal Amount Due         : " + billingData[7] + "\033[0m");
                    } else {
                        System.out.println("\033[32mTotal Paid Amount        : " + billingData[7] + "\033[0m");
                    }

                    System.out.println("Bill Status              : " + billingData[10]);
                }

            }
        } catch (IOException e) {
            System.out.println("Error while reading file");
        }
    }

    public static Object[][] calulateAmountUpdainANDPaidBills() {

        ArrayList<Object[]> dataList = new ArrayList<>();
        File file = new File(projectTxtFiles.BillingFile);

        if (!file.exists() || file.length() == 0) {
            System.out.println("No Billing Info present ! ");
            return null;
        }

        Double UnpaidBillSum = 0.0;
        Double PaidBillSum = 0.0;
        int unpaidCount = 0;
        int paidCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.BillingFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] billData = line.split(",");

                if (billData[10].equals("Unpaid")) {
                    UnpaidBillSum += Double.parseDouble(billData[7]);
                    unpaidCount++;

                } else {
                    PaidBillSum += Double.parseDouble(billData[7]);
                    paidCount++;
                }
            }

            //System.out.println("Total Paid Bills = " + paidCount + " Amount  = " + PaidBillSum);
            // System.out.println("Total Unpaid Bills = " + unpaidCount + " Amount  = " + UnpaidBillSum);
        } catch (IOException e) {
            System.out.println("Error while reading file");
        }

        dataList.add(new Object[]{paidCount, PaidBillSum, "Paid"});
        dataList.add(new Object[]{unpaidCount, UnpaidBillSum, "Unpaid"});

        Object[][] data = new Object[dataList.size()][3];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }
        return data;
    }

    public static Object[][] readDataFromFileToDisplayBillToEmp() {
        ArrayList<Object[]> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(projectTxtFiles.BillingFile)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                String customerId = values[0];
                String billingMonth = values[1];
                String currentRegularUnits = values[2];
                String currentPeakUnits = values[3];
                String costOfElectricity = values[4];
                String salesTax = values[5];
                String fixedCharges = values[6];
                String totalBillingAmount = values[7];
                String readingEntryDate = values[8];
                String dueDate = values[9];
                String billPaidStatus = values[10];

                dataList.add(new Object[]{customerId, billingMonth, currentRegularUnits, currentPeakUnits, costOfElectricity, salesTax, fixedCharges, totalBillingAmount, readingEntryDate, dueDate, billPaidStatus});
            }
        } catch (IOException e) {
            e.getMessage();
        }

        Object[][] data = new Object[dataList.size()][11];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }
        return data;
    }

    public static Object[][] readDataFromFileToDisplayBillToUser(String CustomerID) {
        ArrayList<Object[]> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(projectTxtFiles.BillingFile)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (CustomerID.equals(values[0])) {

                    String customerId = values[0];
                    String billingMonth = values[1];
                    String currentRegularUnits = values[2];
                    String currentPeakUnits = values[3];
                    String costOfElectricity = values[4];
                    String salesTax = values[5];
                    String fixedCharges = values[6];
                    String totalBillingAmount = values[7];
                    String readingEntryDate = values[8];
                    String dueDate = values[9];
                    String billPaidStatus = values[10];

                    dataList.add(new Object[]{customerId, billingMonth, currentRegularUnits, currentPeakUnits, costOfElectricity, salesTax, fixedCharges, totalBillingAmount, readingEntryDate, dueDate, billPaidStatus});
                }
            }
        } catch (IOException e) {
            e.getMessage();
        }

        Object[][] data = new Object[dataList.size()][11];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }
        return data;
    }

    public static boolean saveChangesToBillingDB(DefaultTableModel tableModel, String latestEditableMonth) {
        ArrayList<String> updatedData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.BillingFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                updatedData.add(line);
            }
        } catch (FileNotFoundException ex) {
            ex.getMessage();
        } catch (IOException ex) {
            ex.getMessage();
        }
        updatedData.clear();

        for (int i = 0; i < tableModel.getRowCount(); i++) {

            String col0 = tableModel.getValueAt(i, 0).toString();
            String col1 = tableModel.getValueAt(i, 1).toString();
            String col2 = tableModel.getValueAt(i, 2).toString();
            String col3 = tableModel.getValueAt(i, 3).toString();
            String col4 = tableModel.getValueAt(i, 4).toString();
            String col5 = tableModel.getValueAt(i, 5).toString();
            String col6 = tableModel.getValueAt(i, 6).toString();
            String col7 = tableModel.getValueAt(i, 7).toString();
            String col8 = tableModel.getValueAt(i, 8).toString();
            String col9 = tableModel.getValueAt(i, 9).toString();
            String col10 = tableModel.getValueAt(i, 10).toString();

            String updatedLine = String.join(",", col0, col1, col2, col3, col4, col5, col6, col7, col8, col9, col10);

            updatedData.add(updatedLine);

            if (col10.equals("Paid")) {
                int currentRegularUnits = Integer.parseInt(col2);
                int currentPeakUnits = Integer.parseInt(col3);
                updateUnitsConsumedByCustomer(col0, currentRegularUnits, currentPeakUnits);
            }

        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectTxtFiles.BillingFile,false))) {
            for (String updatedLine : updatedData) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(NADRA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static void changeBillStatus(Scanner input) {
        ArrayList<String> fileContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.BillingFile))) {
            String line;
            String status;
            boolean flag = true;

            while ((line = reader.readLine()) != null) {
                String[] billFileData = line.split(",");

                if (billFileData[10].equals("Unpaid") && flag) {
                    System.out.println("Press (p) to update Bill status of: (" + billFileData[0] + ") for " + billFileData[1] + " OR press (x) to EXIT");

                    // Prompt for input until valid input is received
                    while (true) {
                        status = input.nextLine().trim().toUpperCase(); // Normalize input
                        if ("P".equals(status)) {
                            // Update status to "Paid"
                            line = billFileData[0] + "," + billFileData[1] + "," + billFileData[2] + "," + billFileData[3] + "," + billFileData[4] + "," + billFileData[5] + "," + billFileData[6] + "," + billFileData[7] + "," + billFileData[8] + "," + billFileData[9] + "," + "Paid";
                            break; // Exit the loop on valid input
                        } else if ("X".equals(status)) {
                            flag = false; // Set flag to false to exit the outer loop
                            fileContent.add(line); // Save current line and exit
                            break; // Exit the loop on exit command
                        } else {
                            System.out.println("Invalid input. Please enter 'P' to change status to paid or 'x' to exit.");
                        }
                    }

                    int currentRegularUnits = Integer.parseInt(billFileData[2]);
                    int currentPeakUnits = Integer.parseInt(billFileData[3]);
                    updateUnitsConsumedByCustomer(billFileData[0], currentRegularUnits, currentPeakUnits);
                }

                fileContent.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error while reading Billing file: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectTxtFiles.BillingFile))) {
            for (String contentLine : fileContent) {
                writer.write(contentLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error while writing to Billing file: " + e.getMessage());
        }

        System.out.println("Status updated successfully.");
    }

    public static void updateUnitsConsumedByCustomer(String UserId, int currentRegularUnits, int currentPeakUnits) {

        ArrayList<String> customerFileContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile))) {
            String line;
            while ((line = reader.readLine()) != null) {

                String[] customerData = line.split(",");

                if (customerData[0].equals(UserId)) {
                    line = customerData[0] + "," + customerData[1] + "," + customerData[2] + "," + customerData[3]
                            + "," + customerData[4] + "," + customerData[5] + "," + customerData[6] + "," + customerData[7]
                            + "," + currentRegularUnits + "," + currentPeakUnits;
                }
                customerFileContent.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error while reading Customer File" + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectTxtFiles.CustomerFile))) {

            for (int i = 0; i < customerFileContent.size(); i++) {
                writer.write(customerFileContent.get(i));
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error while writing to Customer file: " + e.getMessage());
        }
        System.out.println("Total consumed Regular/Peak updated successfully.");
    }

}

//
//0001,09/2024,234,11,2004.0,340.68,150.0,2494.68,17/09/2024,24/09/2024,Unpaid
//0002,09/2024,234,0,3510.0,702.0,250.0,4462.0,17/09/2024,24/09/2024,Unpaid
//0003,09/2024,545,43,4876.0,828.9200000000001,150.0,5854.92,17/09/2024,24/09/2024,Unpaid
//0004,09/2024,665,0,3325.0,565.25,150.0,4040.25,17/09/2024,24/09/2024,Unpaid
//0005,09/2024,335,366,15180.0,3036.0,250.0,18466.0,17/09/2024,24/09/2024,Unpaid
//0006,09/2024,993,0,4965.0,844.0500000000001,150.0,5959.05,17/09/2024,24/09/2024,Unpaid
//0007,09/2024,30,54,1890.0,378.0,250.0,2518.0,17/09/2024,24/09/2024,Unpaid
//0008,09/2024,65,0,975.0,195.0,250.0,1420.0,17/09/2024,24/09/2024,Unpaid
