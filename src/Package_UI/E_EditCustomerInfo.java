package Package_UI;

import Font.LoadFont;
import Package_BL.Employee;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

public class E_EditCustomerInfo {

    private static boolean isColumnEdited = false;

    public static JPanel createEditCustomerInfoPanel(Employee emp) {
        JPanel updateCustomerInfoPanel = new JPanel(new BorderLayout());
        updateCustomerInfoPanel.setBorder(new EmptyBorder(0, 1, 0, 0));

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchBarPanel.setPreferredSize(new Dimension(300, 50));  // Adjust the height of the search bar panel
        searchBarPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        JTextField searchField = new JTextField();
        searchField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 18));
        searchField.setPreferredSize(new Dimension(500, 40));

        searchBarPanel.add(searchLabel);
        searchBarPanel.add(searchField);

        updateCustomerInfoPanel.add(searchBarPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Address", "Phone #", "CNIC", "Consumer", "Meter", "Connection", "Regular", "Peak"};
        Object[][] data = emp.readDataFromCustomerDB();

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow only the "Expiry Date" column to be editable (index 3)
                return column == 1 || column == 2 || column == 3 || column == 4 || column == 5 || column == 6;
            }
        };
        JTable table = new JTable(tableModel);

        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                isColumnEdited = true;
            }
        });

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

        // Set general table properties
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

        TableColumn idColumn = table.getColumnModel().getColumn(0);
        idColumn.setPreferredWidth(50);
        TableColumn addressColumn = table.getColumnModel().getColumn(2);
        addressColumn.setPreferredWidth(100);
        TableColumn phoneColumn = table.getColumnModel().getColumn(3);
        phoneColumn.setPreferredWidth(100);
        TableColumn cnicColumn = table.getColumnModel().getColumn(4);
        cnicColumn.setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        updateCustomerInfoPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton removeButton = new JButton("Remove");
        removeButton.setPreferredSize(new Dimension(250, 50));
        removeButton.setBackground(new Color(127, 192, 20));
        removeButton.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createEmptyBorder());
        removeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                removeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Show black border on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                removeButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not hovering
            }
        });

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

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a row to update the Customer Data.", "No row selected", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!isColumnEdited) {
                    JOptionPane.showMessageDialog(null, "Please edit the Table column before saving.", "No column edited", JOptionPane.WARNING_MESSAGE);
                    isColumnEdited = false;
                    return;
                }

                if (emp.saveChangesToCustomerDB(tableModel)) {
                    JOptionPane.showMessageDialog(null, "Customer Data updated successfully!", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update Customer Data. Please try again.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected row index
                int selectedRow = table.getSelectedRow();

                // Check if a row is selected
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a row to delete the customer data.", "No row selected", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Remove the row from the tableModel (this removes it from the JTable view)
                tableModel.removeRow(selectedRow);

                // Update the underlying data in the file
                if (emp.saveChangesToCustomerDB(tableModel)) {
                    JOptionPane.showMessageDialog(null, "Customer data updated successfully!", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update customer data. Please try again.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);

        updateCustomerInfoPanel.add(buttonPanel, BorderLayout.SOUTH);

        return updateCustomerInfoPanel;
    }
}
