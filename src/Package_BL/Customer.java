package Package_BL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class Customer {

    private String customerId;
    private String cnic;
    private String name;
    private String address;
    private String phoneNumber;
    private String customerType;
    private String meterType;
    private String connectionDate;
    private int regularUnitsConsumed;
    private int peakHourUnitsConsumed;

    public Customer(String customerId, String cnic) {
        this.customerId = customerId;
        this.cnic = cnic;
    }

    public Customer(String cnic, String name, String address, String phoneNumber, String customerType, String meterType) {
        this.customerId = generateCustomerId();
        this.cnic = cnic;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.customerType = customerType;
        this.meterType = meterType;
        this.connectionDate = getCurrentDate();
        this.regularUnitsConsumed = 0;
        this.peakHourUnitsConsumed = 0;
    }

    public static String generateCustomerId() {
        File file = new File(projectTxtFiles.CustomerFile);
        if (file.length() == 0) {
            //System.out.println("Nothing in the file! No records found.");
            return "0001";
        }

        int index = getFirstIndexFromLastLine(projectTxtFiles.CustomerFile);

        index++;

        return String.format("%04d", index);
    }

    public static int getFirstIndexFromLastLine(String FILE_NAME) {
        String firstIndexofLastLine = null;
        try (
                BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            String lastLine = null;

            // Read through the entire file to get the last line
            while ((line = br.readLine()) != null) {
                lastLine = line;  // Keep updating the lastLine variable
            }

            // Now split the last line by commas and get the first index
            if (lastLine != null) {
                String[] parts = lastLine.split(",");
                if (parts.length > 0) {
                    firstIndexofLastLine = parts[0];  // Get the first part (index)
                }
            }
        } catch (IOException e) {
            System.out.println("Kuch bhi");
        }

        return Integer.parseInt(firstIndexofLastLine);
    }

    String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date());
    }

    public String toFileFormat() {
        return customerId + "," + name + "," + address + "," + phoneNumber + "," + cnic + "," + customerType + "," + meterType + "," + connectionDate + "," + regularUnitsConsumed + "," + peakHourUnitsConsumed;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCnic() {
        return cnic;
    }

    public String getCustomerName(){
        return this.name;
    }
    public String getName() {
        try (
                BufferedReader br = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile))) {
            String line;
            String[] customerData;

            while ((line = br.readLine()) != null) {

                customerData = line.split(",");
                if (customerData[0].equals(this.getCustomerId())) {
                    return customerData[1];
                }
            }

        } catch (IOException e) {
            System.out.println("Kuch bhi");
        }

        return "Customer";
    }

    public static int getMeterCountForCNIC(String cnic, String FILE_NAME) {
        int meterCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] customerData = line.split(",");
                if (customerData[1].equals(cnic)) {
                    meterCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading CustomerInfo file.");
        }
        return meterCount;
    }

    public static Object[][] readDataFromCustomerDB() {
        ArrayList<Object[]> dataList = new ArrayList<>();
    
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(projectTxtFiles.CustomerFile)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
    
                // Validate the line has exactly 10 columns
                if (values.length == 10) {
                    String customerId = values[0];
                    String customerName = values[1];
                    String customerAddress = values[2];
                    String customerPhone = values[3];
                    String customerCNIC = values[4];
                    String customerType = values[5];
                    String meterType = values[6];
                    String meterInstallationDate = values[7];
                    String regularUnits = values[8];
                    String peakUnits = values[9];
    
                    // Add the data to the list
                    dataList.add(new Object[]{customerId, customerName, customerAddress, customerPhone, customerCNIC, customerType, meterType, meterInstallationDate, regularUnits, peakUnits});
                } else {
                    // Log or handle rows with missing or extra data
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Convert ArrayList to Object[][]
        Object[][] data = new Object[dataList.size()][10];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }
        return data;
    }
    
    // Customer Functionallities
    public Object[][] displayCNICDetail(String id) {
        return NADRA.displayCNICDetailFromNADRADB(id);
    }

    public boolean saveChangesToNADRADB(DefaultTableModel tableModel) {
        return NADRA.saveChangesToNADRADB(tableModel);
    }

    public Object[][] readDataFromBillingDB() {
        return BillManagment.readDataFromFileToDisplayBillToUser(getCustomerId());
    }
  
    public static boolean saveChangesToCustomerDB(Object tableModelObject) {
        if (tableModelObject instanceof DefaultTableModel) {
            DefaultTableModel tableModel = (DefaultTableModel) tableModelObject; // Cast to DefaultTableModel
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectTxtFiles.CustomerFile, false))) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    StringBuilder updatedLine = new StringBuilder();
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object cellValue = tableModel.getValueAt(i, j);
                        updatedLine.append(cellValue != null ? cellValue.toString() : "");
                        if (j < tableModel.getColumnCount() - 1) {
                            updatedLine.append(",");
                        }
                    }
                    writer.write(updatedLine.toString());
                    writer.newLine();
                }
                return true;
            } catch (IOException ex) {
                System.err.println("Error saving changes to customer database: " + ex.getMessage());
                return false;
            }
        } else {
            System.err.println("Provided object is not an instance of DefaultTableModel");
            return false;
        }
    }
    

    // Override isCellEditable to make the cell editable based on the value
    // public boolean isCellEditable(int rowIndex, int columnIndex) {
    //     // Get value of the cell and check if it's not null or empty
    //     Object value = super.getValueAt(rowIndex, columnIndex);
    //     return value != null && !value.toString().isEmpty(); // Return true if not null and not empty
    // }

    // // Override getValueAt to return an empty string if the value is null
    // public Object getValueAt(int rowIndex, int columnIndex) {
    //     // Get value from the super class (DefaultTableModel)
    //     Object value = super.getValueAt(rowIndex, columnIndex);
    //     return value != null ? value : ""; // Return empty string if value is null
    // }
        
    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getMeterType() {
        return meterType;
    }

    public int getRegularUnitsConsumed() {
        return regularUnitsConsumed;
    }

    public int getPeakHourUnitsConsumed() {
        return peakHourUnitsConsumed;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    public void setConnectionDate(String connectionDate) {
        this.connectionDate = connectionDate;
    }

    public void setRegularUnitsConsumed(int regularUnitsConsumed) {
        this.regularUnitsConsumed = regularUnitsConsumed;
    }

    public void setPeakHourUnitsConsumed(int peakHourUnitsConsumed) {
        this.peakHourUnitsConsumed = peakHourUnitsConsumed;
    }

    public String getConnectionDate() {
        return connectionDate;
    }

    public static boolean singlePhaseCheck(String customerId) {
        try (BufferedReader customerReader = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile))) {
            String line;
            while ((line = customerReader.readLine()) != null) {
                String[] customerData = line.split(",");
                String currentCustomerId = customerData[0];

                if (currentCustomerId.equals(customerId)) {
                    String meterType = customerData[6];
                    return meterType.equals("Single");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
