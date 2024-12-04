package Package_BL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;

public class NADRA {

    private String CNIC;
    private String IssuanceData;
    private String ExpiryData;

    public NADRA(String CNIC, String IssuanceData, String ExpiryData) {
        this.CNIC = CNIC;
        this.IssuanceData = IssuanceData;
        this.ExpiryData = ExpiryData;
    }

    public String getCNIC() {
        return CNIC;
    }

    public String getIssuanceData() {
        return IssuanceData;
    }

    public String getExpiryData() {
        return ExpiryData;
    }

    public static Object[][] displayCNICDetailFromNADRADB(String Id) {
        ArrayList<Object[]> dataList = new ArrayList<>();
        boolean flag = false;

        try (
                BufferedReader nadraReader = new BufferedReader(new FileReader(projectTxtFiles.NadraFile)); 
                BufferedReader customerReader = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile))) {
            ArrayList<String> customerList = new ArrayList<>();
            String customerLine;
            while ((customerLine = customerReader.readLine()) != null) {
                customerList.add(customerLine);
            }

            String nadraLine;


            while ((nadraLine = nadraReader.readLine()) != null) {
                String[] nadraData = nadraLine.split(",");

                for (String customerEntry : customerList) {
                    String[] customerData = customerEntry.split(",");

                    if (customerData[4].equals(nadraData[0]) && customerData[0].equals(Id)) {
                        //System.out.println("CNIC: " + nadraData[0] + " (Expiry Date: " + nadraData[2] + ")");

                        dataList.add(new Object[]{customerData[0], nadraData[0], nadraData[1], nadraData[2]});
                        flag = true;
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error while reading files: " + e.getMessage());
        }

        if (!flag) {
            System.out.println("No CNICs found that will expire in the next 30 days.");
        }

        Object[][] data = new Object[dataList.size()][4];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }
        return data;
    }

    public static boolean saveChangesToNADRADB(DefaultTableModel tableModel) {
        ArrayList<String> updatedData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.NadraFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                updatedData.add(line);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NADRA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NADRA.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String cnic = (String) tableModel.getValueAt(i, 1);
            String newExpiryDate = (String) tableModel.getValueAt(i, 3);

            LocalDate newExpiryDateParsed;
            LocalDate issueDateParsed;

            try {
                newExpiryDateParsed = LocalDate.parse(newExpiryDate, formatter); 
            } catch (DateTimeParseException ex) {
                System.out.println("Error: Invalid date format.");
                return false;
            }

            for (int j = 0; j < updatedData.size(); j++) {
                String[] nadraEntry = updatedData.get(j).split(",");
                if (nadraEntry[0].equals(cnic)) {

                    issueDateParsed = LocalDate.parse(nadraEntry[1], formatter);

                    if (newExpiryDateParsed.isBefore(issueDateParsed)) {
                        return false;
                    }

                    nadraEntry[2] = newExpiryDate;
                    updatedData.set(j, String.join(",", nadraEntry));
                    break;
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectTxtFiles.NadraFile))) {
            for (String updatedLine : updatedData) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(NADRA.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public static boolean isCNICValidInNADRADB(String cnic) {
        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.NadraFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] nadraData = line.split(",");
                if (nadraData[0].equals(cnic)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading NADRADB file.");
        }
        return false; // CNIC not found
    }

    public static Object[][] getCNICExpiresIn30days() {
        ArrayList<Object[]> dataList = new ArrayList<>();
        boolean flag = false;

        
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        // System.out.println("Date after 30 days"+thirtyDaysFromNow);

        try (
                BufferedReader nadraReader = new BufferedReader(new FileReader(projectTxtFiles.NadraFile)); 
                BufferedReader customerReader = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile))) {
            ArrayList<String> customerList = new ArrayList<>();
            
            String customerLine;
            while ((customerLine = customerReader.readLine()) != null) {
                customerList.add(customerLine);
            }

            String nadraLine;
            // LocalDate today = LocalDate.now();
            // LocalDate thirtyDaysFromNow = today.plusDays(30);
            // System.out.println("Date after 30 days"+thirtyDaysFromNow);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            while ((nadraLine = nadraReader.readLine()) != null) {
                String[] nadraData = nadraLine.split(",");
                LocalDate cnicExpiryDate = LocalDate.parse(nadraData[2], formatter);

                for (String customerEntry : customerList) {
                    String[] customerData = customerEntry.split(",");

                    if (customerData[4].equals(nadraData[0]) && cnicExpiryDate.isAfter(today) && cnicExpiryDate.isBefore(thirtyDaysFromNow)) {
                        // System.out.println("CNIC: " + nadraData[0] + " (Expiry Date: " + nadraData[2] + ")");

                        dataList.add(new Object[]{customerData[0], nadraData[0], nadraData[1], nadraData[2]});

                        flag = true;
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error while reading files: " + e.getMessage());
        }

        if (!flag) {
            System.out.println("No CNICs found that will expire in the next 30 days.");
        }

        Object[][] data = new Object[dataList.size()][4];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }
        return data;
    }

    private static String getDateAfter30Days(int daysToAdd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.now().plusDays(daysToAdd).format(formatter);
    }

}
