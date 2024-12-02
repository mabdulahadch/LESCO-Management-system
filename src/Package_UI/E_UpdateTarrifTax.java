package Package_UI;

import Font.LoadFont;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

public class E_UpdateTarrifTax {

    private static boolean isColumnEdited = false;
    public static JPanel createTariffTaxInfoPanel(Socket socket, ObjectOutputStream objectOut, ObjectInputStream objectIn) {
        JPanel tariffTaxInfoPanel = new JPanel(new BorderLayout());
        tariffTaxInfoPanel.setBorder(new EmptyBorder(0, 1, 0, 0));

        // Search Bar Panel
        JPanel searchBarPanel = createSearchBarPanel();
        JTextField searchField = (JTextField) searchBarPanel.getComponent(1);

        tariffTaxInfoPanel.add(searchBarPanel, BorderLayout.NORTH);

        // Fetch data from server
        Object[][] data = fetchDataFromServer(objectOut, objectIn);
        String[] columnNames = {"Consumer Type", "Meter Type", "Regular Units", "Peak Units", "Tax %", "Fixed Tax"};

        // Table setup
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3 || column == 4 || column == 5;
            }
        };

        JTable table = setupTable(tableModel);

        // Table row sorter for search functionality
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        setupSearchFilter(searchField, rowSorter);

        tableModel.addTableModelListener(e -> isColumnEdited = true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tariffTaxInfoPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel(objectOut, objectIn, tableModel, table);
        tariffTaxInfoPanel.add(buttonPanel, BorderLayout.SOUTH);

        return tariffTaxInfoPanel;
    }

    private static JPanel createSearchBarPanel() {
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

        return searchBarPanel;
    }

    /**
     * Sets up the table for the Tariff Tax panel.
     */
    private static JTable setupTable(DefaultTableModel tableModel) {
        JTable table = new JTable(tableModel);
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

        return table;
    }

    /**
     * Sets up the search functionality for the table.
     */
    private static void setupSearchFilter(JTextField searchField, TableRowSorter<DefaultTableModel> rowSorter) {
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
                if (searchText.trim().isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
            }
        });
    }

    /**
     * Creates the button panel with Save functionality.
     */
    private static JPanel createButtonPanel(ObjectOutputStream objectOut, ObjectInputStream objectIn, DefaultTableModel tableModel, JTable table) {
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
                saveButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBorder(BorderFactory.createEmptyBorder());
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a row to update the expiry date.", "No row selected", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!isColumnEdited) {
                    JOptionPane.showMessageDialog(null, "Please edit the Table column before saving.", "No column edited", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (saveChangesToServer(objectOut, objectIn, tableModel)) {
                    JOptionPane.showMessageDialog(null, "Tariff Tax updated successfully!", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                    isColumnEdited = false;
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update Tariff Tax. Please try again.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(saveButton);

        return buttonPanel;
    }

    /**
     * Fetches data from the server.
     */
    private static Object[][] fetchDataFromServer(ObjectOutputStream objectOut, ObjectInputStream objectIn) {
        try {
            objectOut.writeObject("FETCH_TARIFF_DATA");
            objectOut.flush();
            return (Object[][]) objectIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[0][0];
        }
    }

    /**
     * Saves changes to the server.
     */
    private static boolean saveChangesToServer(ObjectOutputStream objectOut, ObjectInputStream objectIn, DefaultTableModel tableModel) {
        try {
            objectOut.writeObject("SAVE_TARIFF_CHANGES");
            objectOut.writeObject(tableModel.getDataVector());
            objectOut.flush();
            return (boolean) objectIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
