package Package_UI;

import Font.LoadFont;
import Package_BL.Customer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

public class C_ViewBillPanel {

    public static JPanel createViewBillPanel(Socket socket, ObjectOutputStream objectOut, ObjectInputStream objectIn) {

        JPanel viewCustomerBillsPanel = new JPanel(new BorderLayout());
        viewCustomerBillsPanel.setBorder(new EmptyBorder(1, 0, 0, 0));
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

        String[] columnNames = { "ID", "Month", "Regular", "Peak", "Cost of Electricity", "SalesTax", "Fixed $",
                "Total Bill", "Reading Date", "DueDate", "Bill Status" };
        Object[][] data = null;

        try {
            objectOut.writeObject("getCustomerBill");
            objectOut.flush(); 
            data = (Object[][]) objectIn.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching data from server: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (data == null) {
            data = new Object[0][columnNames.length]; 
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
        idColumn.setPreferredWidth(20);
        TableColumn monthColumn = table.getColumnModel().getColumn(1);
        monthColumn.setPreferredWidth(40);
        TableColumn addressColumn = table.getColumnModel().getColumn(2);
        addressColumn.setPreferredWidth(40);
        TableColumn phoneColumn = table.getColumnModel().getColumn(3);
        phoneColumn.setPreferredWidth(40);
        TableColumn cnicColumn = table.getColumnModel().getColumn(4);
        cnicColumn.setPreferredWidth(100);
        TableColumn costColumn = table.getColumnModel().getColumn(10);
        costColumn.setPreferredWidth(40);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        viewCustomerBillsPanel.add(scrollPane, BorderLayout.CENTER);

        return viewCustomerBillsPanel;
    }
}
