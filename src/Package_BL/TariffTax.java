package Package_BL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class TariffTax {


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
