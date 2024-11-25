package Package_BL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class TariffTax {

    private static final int TOTAL_TARIFF_ROWS = 4;

    public static void addorUpdatedTraffTaxDetailsInFile(Scanner input) {
        File file = new File(projectTxtFiles.TariffFile);

        if (!file.exists() || file.length() == 0) {
            System.out.println("The file is empty. Please enter new Tariff Tax Details.");
            addTariffTaxDataInFile(file, input); // Method to take input from the user and write to the file
        } else {
            System.out.println("Tariff Tax Info already exists in the file:");
            displayOrUpdateTariffFileContents(file, input); // Method to display existing file data or employee can update
        }
    }

    public static void addTariffTaxDataInFile(File file, Scanner input) {
        //Scanner scanner = new Scanner(System.in);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < TOTAL_TARIFF_ROWS; i++) {

                System.out.println("Enter details for " + (i + 1) + " row:");
                System.out.print("Enter phase type (1 or 3): ");
                String phaseType = input.nextLine();

                System.out.print("Enter regular unit price: ");
                int regularUnitPrice = input.nextInt();

                int peakHourUnitPrice = 0;    // only for Phase 3 users
                if (phaseType.equals("3")) {
                    System.out.print("Enter peak hour unit price: ");
                    peakHourUnitPrice = input.nextInt();
                }

                System.out.print("Enter sales tax percentage: ");
                int salesTax = input.nextInt();

                System.out.print("Enter fixed charges: ");
                int fixedCharges = input.nextInt();

                input.nextLine(); // Clear the buffer

                // Write the entered data to the file in the required format
                writer.write(phaseType + "," + regularUnitPrice + "," + (peakHourUnitPrice > 0 ? peakHourUnitPrice : "") + "," + salesTax + "," + fixedCharges);
                writer.newLine();
            }

            System.out.println("Tariff Tax details successfully added to the file.");

        } catch (IOException e) {
            System.out.println("Error writing to the file: " + e.getMessage());
        }
    }

    public static void displayOrUpdateTariffFileContents(File file, Scanner input) {

        System.out.println("Tariff Tax Info in the file:");

        // Read and display file content
        ArrayList<String> fileContents = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                fileContents.add(line);                                      // Store each line in a list for later modification
                System.out.println(lineNumber + ": " + line); // Display each line with a line number
                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return;
        }

        System.out.println("Enter the line number to update (1-4):  or any other to exit");
        int lineToUpdate = input.nextInt();
        input.nextLine(); // Clear the buffer

        if (lineToUpdate < 1 || lineToUpdate > 4) {
            //System.out.println("Invalid line number. Please choose a number between 1 and 4.");
            return;
        }

        // Gather new data for the specified line
        System.out.print("Enter new phase type (1 or 3): ");
        int phaseType = input.nextInt();

        System.out.print("Enter new regular unit price: ");
        int regularUnitPrice = input.nextInt();

        int peakHourUnitPrice = 0;
        if (phaseType == 3) {
            System.out.print("Enter new peak hour unit price: ");
            peakHourUnitPrice = input.nextInt();
        }

        System.out.print("Enter new sales tax percentage: ");
        int salesTax = input.nextInt();

        System.out.print("Enter new fixed charges: ");
        int fixedCharges = input.nextInt();

        input.nextLine(); // Clear buffer

        // Store updated data
        String updatedEntry = phaseType + "," + regularUnitPrice + ","
                + (peakHourUnitPrice > 0 ? peakHourUnitPrice : "") + ","
                + salesTax + "," + fixedCharges;

        // Update the specific line in the list
        fileContents.set(lineToUpdate - 1, updatedEntry);

        // Write the updated list back to the file
        writeUpdatedContentsToFile(file, fileContents);

    }

    private static void writeUpdatedContentsToFile(File file, ArrayList<String> updatedContents) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : updatedContents) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("File updated successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to the file: " + e.getMessage());
        }
    }

    public static Object[][] readDataFromFile(String fileName) {
        ArrayList<Object[]> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                lineNumber++;

                String consumerType = "";

                if (values[0].equals("1") && lineNumber == 1 || values[0].equals("3") && lineNumber == 3) {
                    consumerType = "Domestic Type";
                } else if (values[0].equals("1") && lineNumber == 2 || values[0].equals("3") && lineNumber == 4) {
                    consumerType = "Commercial Type";
                }
                String meterType = values[0];
                String regularUnits = values[1];
                String peakUnits = values[2].isEmpty() ? " " : values[2];
                String taxPercentage = values[3];
                String fixedTax = values[4];

                dataList.add(new Object[]{consumerType, meterType, regularUnits, peakUnits, taxPercentage, fixedTax});
            }
        } catch (IOException e) {
            e.getMessage();
        }

        Object[][] data = new Object[dataList.size()][5];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }
        return data;
    }

    public static boolean saveChangesToTariffTaxDB(DefaultTableModel tableModel) {

        ArrayList<String> updatedData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.TariffFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                updatedData.add(line);
            }
        } catch (FileNotFoundException ex) {
            ex.getMessage();
        } catch (IOException ex) {
            ex.getMessage();
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {

            String col2 = tableModel.getValueAt(i, 2).toString(); 
            String col3 = tableModel.getValueAt(i, 3).toString(); 
            String col4 = tableModel.getValueAt(i, 4).toString();
            String col5 = tableModel.getValueAt(i, 5).toString();

            String[] rowData = updatedData.get(i).split(","); // Split the line by commas

            rowData[1] = col2;
            rowData[2] = col3;
            rowData[3] = col4;
            rowData[4] = col5;

            updatedData.set(i, String.join(",", rowData));
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(projectTxtFiles.TariffFile))) {
            for (String updatedLine : updatedData) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(NADRA.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

}
