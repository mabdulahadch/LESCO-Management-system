package Package_UI;

import Font.LoadFont;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class C_UpdateCNICPanel {

    private static boolean isColumnEdited = false;

    public static JPanel createUpdateCNICPanel(Socket socket, ObjectOutputStream objectOut,
            ObjectInputStream objectIn) {

        JPanel viewCNICReportsPanel = new JPanel(new BorderLayout());
        viewCNICReportsPanel.setBorder(new EmptyBorder(1, 0, 0, 0));

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchBarPanel.setPreferredSize(new Dimension(300, 50)); // Adjust the height of the search bar panel
        searchBarPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        JTextField searchField = new JTextField();
        searchField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 18));
        searchField.setPreferredSize(new Dimension(500, 40));

        searchBarPanel.add(searchLabel);
        searchBarPanel.add(searchField);

        viewCNICReportsPanel.add(searchBarPanel, BorderLayout.NORTH);

        String[] columnNames = { "Consumer ID", "CNIC #", "Issue Date", "Expiry Date" };
        Object[][] data = null;

        try {
            // Send request to server
            objectOut.writeObject("getCustomerCNIC");

            data = (Object[][]) objectIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching data from server: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (data == null) {
            data = new Object[0][columnNames.length]; // Ensure correct column count
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                isColumnEdited = true;
            }
        });

        JTable table = new JTable(tableModel);

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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        viewCNICReportsPanel.add(scrollPane, BorderLayout.CENTER);

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
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Show black border on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not hovering
            }
        });
       
        // saveButton.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         int selectedRow = table.getSelectedRow();
        //         if (selectedRow == -1) {
        //             JOptionPane.showMessageDialog(null, "Please select a row to update the expiry date.",
        //                     "No row selected", JOptionPane.ERROR_MESSAGE);
        //             return;
        //         }

        //         if (!isColumnEdited) {
        //             JOptionPane.showMessageDialog(null, "Please edit the expiry date column before saving.",
        //                     "No column edited", JOptionPane.WARNING_MESSAGE);
        //             isColumnEdited = false;
        //             return;
        //         }
        //         try {
        //             Object[][] updatedCNICData = new Object[1][columnNames.length];
        //             System.out.println(Arrays.deepToString(updatedCNICData));
        //             updatedCNICData[0] = new Object[]{
        //                 table.getValueAt(selectedRow, 0), // Consumer ID
        //                 table.getValueAt(selectedRow, 1), // CNIC #
        //                 table.getValueAt(selectedRow, 2), // Issue Date
        //                 table.getValueAt(selectedRow, 3)  // Expiry Date
        //             };
                
        //             // Send the command and data to the server
        //             objectOut.writeObject("saveCNICChanges");
        //             objectOut.flush(); // Ensure command is sent
        //             objectOut.writeObject(updatedCNICData);
        //             objectOut.flush(); // Ensure data is sent
                
        //             // Optionally, wait for server response
        //             String response = (String) objectIn.readObject();
        //             if ("success".equalsIgnoreCase(response)) {
        //                 JOptionPane.showMessageDialog(null, "Changes saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        //             } else if ("failure".equalsIgnoreCase(response)) {
        //                 JOptionPane.showMessageDialog(null, "Failed to save changes.", "Error", JOptionPane.ERROR_MESSAGE);
        //             } else {
        //                 JOptionPane.showMessageDialog(null, response, "Server Response", JOptionPane.INFORMATION_MESSAGE);
        //             }
        //         } catch (Exception ex) {
        //             ex.printStackTrace(); // Log the error for debugging
        //             JOptionPane.showMessageDialog(null, "Error saving changes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        //         }
                
        //     }
        // });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a row to update the expiry date.",
                            "No row selected", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                if (!isColumnEdited) {
                    JOptionPane.showMessageDialog(null, "Please edit the expiry date column before saving.",
                            "No column edited", JOptionPane.WARNING_MESSAGE);
                    return;
                }
        
                try {
                    // Debug selected row and column data
                    System.out.println("Selected Row: " + selectedRow);
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        System.out.println("Column " + i + ": " + table.getValueAt(selectedRow, i));
                    }
        
                    // Prepare updated CNIC data
                    Object[][] updatedCNICData = new Object[1][columnNames.length];
                    updatedCNICData[0] = new Object[]{
                        table.getValueAt(selectedRow, 0), // Consumer ID
                        table.getValueAt(selectedRow, 1), // CNIC #
                        table.getValueAt(selectedRow, 2), // Issue Date
                        table.getValueAt(selectedRow, 3)  // Expiry Date
                    };
                    System.out.println("Sending Data: " + Arrays.deepToString(updatedCNICData));
        
                    // Ensure socket is connected
                    if (socket == null || socket.isClosed()) {
                        JOptionPane.showMessageDialog(null, "Connection to the server is closed.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                    System.out.println(1);
                       
                        return;
                    }
        
                    // Send command and data to the server
                    objectOut.writeObject("saveCNICChanges");
                    objectOut.flush();
                    objectOut.writeObject(updatedCNICData);
                    objectOut.flush();
                    
                    System.out.println(1);
                    // Wait for server response
                    String response = (String) objectIn.readObject();
                    if ("success".equalsIgnoreCase(response)) {
                        JOptionPane.showMessageDialog(null, "Changes saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else if ("failure".equalsIgnoreCase(response)) {
                        JOptionPane.showMessageDialog(null, "Failed to save changes.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, response, "Server Response", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(); // Log for debugging
                    JOptionPane.showMessageDialog(null, "Error saving changes: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        

        buttonPanel.add(saveButton);
        viewCNICReportsPanel.add(buttonPanel, BorderLayout.SOUTH);
        return viewCNICReportsPanel;

    }
}
