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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class E_EditBillInfo {

    private static boolean isColumnEdited = false;

    public static JPanel createViewCustomerBillsPanel(Employee emp) {
        JPanel viewCustomerBillsPanel = new JPanel(new BorderLayout());
        viewCustomerBillsPanel.setBorder(new EmptyBorder(0, 1, 0, 0));
        viewCustomerBillsPanel.add(new JLabel("View Customer Bills Panel"));

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchBarPanel.setPreferredSize(new Dimension(300, 50));
        searchBarPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        JTextField searchField = new JTextField();
        searchField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 18));
        searchField.setPreferredSize(new Dimension(500, 40));

        searchBarPanel.add(searchLabel);
        searchBarPanel.add(searchField);
        viewCustomerBillsPanel.add(searchBarPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Month", "Regular", "Peak", "Cost of Electricity", "SalesTax", "Fixed $", "Total Bill", "Reading Date", "DueDate", "Bill Status"};
        Object[][] data = emp.readDataFromFileToDisplayBill();

        // Determine the most recent month
        String latestEditableMonth = getLatestEditableMonth(data);

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                String billMonth = getValueAt(row, 1).toString();
                String billStatus = getValueAt(row, 10).toString();
                return billMonth.equals(latestEditableMonth) && billStatus.equals("Unpaid") && (column == 7 || column == 8 || column == 9 || column == 10);
            }
        };

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
        viewCustomerBillsPanel.add(scrollPane, BorderLayout.CENTER);

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

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a row to Remove Bill.", "No row selected", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String billMonth = table.getValueAt(selectedRow, 1).toString();
                String billStatus = table.getValueAt(selectedRow, 10).toString();
                if (!billMonth.equals(latestEditableMonth)) {
                    JOptionPane.showMessageDialog(null, "You can only remove the latest month's bill.", "Invalid Operation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                else if(billStatus.equals("Paid"))
                {
                    JOptionPane.showMessageDialog(null, "You can only remove the Unpaid Bill.", "Invalid Operation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                else {
                    tableModel.removeRow(selectedRow);
                    if (emp.saveChangesToBillingDB(tableModel, latestEditableMonth)) {
                        JOptionPane.showMessageDialog(null, "Bill updated successfully!", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update Bill. Please try again.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton saveButton = new JButton("Save Changes");
        saveButton.setPreferredSize(new Dimension(250, 50));
        saveButton.setBackground(new Color(127, 192, 20));
        saveButton.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder());

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a row to Update Bill.", "No row selected", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (emp.saveChangesToBillingDB(tableModel, latestEditableMonth)) {
                    JOptionPane.showMessageDialog(null, "Bill updated successfully!", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update Bill. Please try again.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(removeButton);
        viewCustomerBillsPanel.add(buttonPanel, BorderLayout.SOUTH);

        return viewCustomerBillsPanel;
    }

    private static String getLatestEditableMonth(Object[][] data) {
        String latestMonth = "";

        for (int i = 0; i < data.length; i++) {
            String billMonth = data[i][1].toString(); // Month is in the 2nd column (index 1)
            //System.out.println("Bill"+billMonth);
            if (latestMonth.compareTo(billMonth) < 0) {
                latestMonth = billMonth;
                // System.out.println("Latest"+latestMonth);
            }
        }

        return latestMonth;
    }

//    public static JPanel createViewCustomerBillsPanel(Employee emp) {
//
//        JPanel viewCustomerBillsPanel = new JPanel(new BorderLayout());
//        viewCustomerBillsPanel.setBorder(new EmptyBorder(0, 1, 0, 0));
//        viewCustomerBillsPanel.add(new JLabel("View Customer Bills Panel"));
//
//        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        searchBarPanel.setPreferredSize(new Dimension(300, 50));  // Adjust the height of the search bar panel
//        searchBarPanel.setBackground(Color.WHITE);
//
//        JLabel searchLabel = new JLabel("Search: ");
//        searchLabel.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
//        JTextField searchField = new JTextField();
//        searchField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 18));
//        searchField.setPreferredSize(new Dimension(500, 40));
//
//        searchBarPanel.add(searchLabel);
//        searchBarPanel.add(searchField);
//
//        viewCustomerBillsPanel.add(searchBarPanel, BorderLayout.NORTH);
//
//        String[] columnNames = {"ID", "Month", "Regular", "Peak", "Cost of Electricity", "SalesTax", "Fixed $", "Total Bill", "Reading Date", "DueDate", "Bill Status"};
//        Object[][] data = emp.readDataFromFileToDisplayBill();
//
//        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return column == 7 || column == 8 || column == 9 || column == 10;
//            }
//        };
//        JTable table = new JTable(tableModel);
//
//        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
//        table.setRowSorter(rowSorter);
//        tableModel.addTableModelListener(new TableModelListener() {
//            @Override
//            public void tableChanged(TableModelEvent e) {
//                isColumnEdited = true;
//            }
//        });
//        searchField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                filterTable();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                filterTable();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                filterTable();
//            }
//
//            private void filterTable() {
//                String searchText = searchField.getText();
//                if (searchText.trim().length() == 0) {
//                    rowSorter.setRowFilter(null);
//                } else {
//                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
//                }
//            }
//        });
//
//        table.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
//        table.setRowHeight(50);
//        table.setShowGrid(true);
//        table.setSelectionBackground(Color.LIGHT_GRAY);
//        table.setSelectionForeground(Color.BLACK);
//        table.setFillsViewportHeight(true);
//
//        JTableHeader header = table.getTableHeader();
//        header.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
//        header.setBackground(new Color(127, 192, 20));
//        header.setForeground(Color.WHITE);
//
//        TableColumn idColumn = table.getColumnModel().getColumn(0);
//        idColumn.setPreferredWidth(20);
//        TableColumn monthColumn = table.getColumnModel().getColumn(1);
//        monthColumn.setPreferredWidth(40);
//        TableColumn addressColumn = table.getColumnModel().getColumn(2);
//        addressColumn.setPreferredWidth(40);
//        TableColumn phoneColumn = table.getColumnModel().getColumn(3);
//        phoneColumn.setPreferredWidth(40);
//        TableColumn cnicColumn = table.getColumnModel().getColumn(4);
//        cnicColumn.setPreferredWidth(100);
//        TableColumn costColumn = table.getColumnModel().getColumn(10);
//        costColumn.setPreferredWidth(40);
//
//        JScrollPane scrollPane = new JScrollPane(table);
//        scrollPane.setBorder(BorderFactory.createEmptyBorder());
//        viewCustomerBillsPanel.add(scrollPane, BorderLayout.CENTER);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        buttonPanel.setBackground(Color.WHITE);
//        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
//
//        JButton removeButton = new JButton("Remove");
//        removeButton.setPreferredSize(new Dimension(250, 50));
//        removeButton.setBackground(new Color(127, 192, 20));
//        removeButton.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
//        removeButton.setForeground(Color.WHITE);
//        removeButton.setFocusPainted(false);
//        removeButton.setBorder(BorderFactory.createEmptyBorder());
//        removeButton.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseEntered(java.awt.event.MouseEvent evt) {
//                removeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Show black border on hover
//            }
//
//            @Override
//            public void mouseExited(java.awt.event.MouseEvent evt) {
//                removeButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not hovering
//            }
//        });
//
//        JButton saveButton = new JButton("Save Changes");
//        saveButton.setPreferredSize(new Dimension(250, 50));
//        saveButton.setBackground(new Color(127, 192, 20));
//        saveButton.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
//        saveButton.setForeground(Color.WHITE);
//        saveButton.setFocusPainted(false);
//        saveButton.setBorder(BorderFactory.createEmptyBorder());
//        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseEntered(java.awt.event.MouseEvent evt) {
//                saveButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Show black border on hover
//            }
//
//            @Override
//            public void mouseExited(java.awt.event.MouseEvent evt) {
//                saveButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not hovering
//            }
//        });
//
//        
//        
//
//        saveButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int selectedRow = table.getSelectedRow();
//
//                if (selectedRow == -1) {
//                    JOptionPane.showMessageDialog(null, "Please select a row to Update Bill.", "No row selected", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }
//                
//                if (!isColumnEdited) {
//                    JOptionPane.showMessageDialog(null, "Please Edit the Table column before saving.", "No column edited", JOptionPane.WARNING_MESSAGE);
//                    isColumnEdited = false;
//                    return;
//                }
//
//                if (emp.saveChangesToBillingDB(tableModel)) {
//                    JOptionPane.showMessageDialog(null, "Bill updated successfully!", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
//                } else {
//                    JOptionPane.showMessageDialog(null, "Failed to update Bill. Please try again.", "Update Failed", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        });
//        
//        buttonPanel.add(removeButton);
//        buttonPanel.add(saveButton);
//
//        viewCustomerBillsPanel.add(buttonPanel, BorderLayout.SOUTH);
//
//        return viewCustomerBillsPanel;
//    }
}
