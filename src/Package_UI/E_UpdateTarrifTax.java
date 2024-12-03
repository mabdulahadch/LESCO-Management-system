package Package_UI;

import Font.LoadFont;
import Package_BL.Employee;
import Package_BL.TariffTax;
import Package_BL.projectTxtFiles;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class E_UpdateTarrifTax {

    private static boolean isColumnEdited = false;

    public static JPanel createTariffTaxInfoPanel(Socket clientSocket, ObjectOutputStream objectOut, ObjectInputStream objectIn) {
        JPanel tariffTaxInfoPanel = new JPanel(new BorderLayout());
        tariffTaxInfoPanel.setBorder(new EmptyBorder(0, 1, 0, 0));

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchBarPanel.setPreferredSize(new Dimension(300, 50)); // Adjust height of the search bar panel
        searchBarPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        JTextField searchField = new JTextField();
        searchField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 18));
        searchField.setPreferredSize(new Dimension(500, 40));

        searchBarPanel.add(searchLabel);
        searchBarPanel.add(searchField);

        tariffTaxInfoPanel.add(searchBarPanel, BorderLayout.NORTH);

        // Define column names for the table
        String[] columnNames = {"Consumer Type", "Meter Type", "Regular Units", "Peak Units", "Tax %", "Fixed Tax"};

        // Initialize table model and data
        Object[][] data = new Object[0][columnNames.length]; // Default empty data
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3 || column == 4 || column == 5;
            }
        };

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        tariffTaxInfoPanel.add(scrollPane, BorderLayout.CENTER);

        // Fetch data from the server
        try {
            objectOut.writeObject("fetchTariffData");
            objectOut.flush();
            Object[][] fetchedData = (Object[][]) objectIn.readObject();
            tableModel.setDataVector(fetchedData, columnNames);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching tariff data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Set table properties
        table.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        table.setRowHeight(50);
        table.setShowGrid(true);
        table.setSelectionBackground(Color.LIGHT_GRAY);
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        header.setBackground(new Color(127, 192, 20));
        header.setForeground(Color.WHITE);

        // Add search functionality
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                String searchText = searchField.getText();
                if (searchText.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
            }
        });

        tableModel.addTableModelListener(e -> isColumnEdited = true);

        // Add Save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton saveButton = new JButton("Save Changes");
        saveButton.setPreferredSize(new Dimension(250, 50));
        saveButton.setBackground(new Color(127, 192, 20));
        saveButton.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder());

        saveButton.addActionListener(e -> {
            if (!isColumnEdited) {
                JOptionPane.showMessageDialog(null, "No changes detected in the table.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                // Collect updated data from the table
                Object[][] updatedData = new Object[table.getRowCount()][table.getColumnCount()];
                for (int row = 0; row < table.getRowCount(); row++) {
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        updatedData[row][col] = table.getValueAt(row, col); // Populate the updated data array
                    }
                }
            
                // Send updated data to the server
                objectOut.writeObject("saveTariffChanges"); // Send command to the server to save changes
                objectOut.flush();  // Ensure the command is sent
                objectOut.writeObject(updatedData); // Send the updated data array
                objectOut.flush();  // Ensure the updated data is sent
            
                // Wait for server response
                String response = (String) objectIn.readObject();  // Read the response from the server
            
                // Handle the server response
                if ("success".equalsIgnoreCase(response)) {
                    JOptionPane.showMessageDialog(null, "Changes saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to save changes.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                // Handle exceptions if something goes wrong
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving changes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }            
        });

        buttonPanel.add(saveButton);
        tariffTaxInfoPanel.add(buttonPanel, BorderLayout.SOUTH);

        return tariffTaxInfoPanel;
    }
}
