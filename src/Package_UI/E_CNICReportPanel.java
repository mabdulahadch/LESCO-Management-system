package Package_UI;
import Package_BL.Employee;

import Font.LoadFont;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
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

public class E_CNICReportPanel {
    
    public static JPanel createViewCNICReportsPanel(Employee emp) {

        JPanel viewCNICReportsPanel = new JPanel(new BorderLayout());
        viewCNICReportsPanel.setBorder(new EmptyBorder(0, 1, 0, 0));

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

        viewCNICReportsPanel.add(searchBarPanel, BorderLayout.NORTH);

        String[] columnNames = {"Consumer ID", "CNIC #", "Issue Date", "Expiry Date"};
        Object[][] data = emp.CNICExpiresIn30days();

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

        return viewCNICReportsPanel;
    }
}
