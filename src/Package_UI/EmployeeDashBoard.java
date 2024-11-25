package Package_UI;

import Font.LoadFont;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import Package_BL.Employee;

public class EmployeeDashBoard {

    public void openEmployeeDashboard(Employee emp) {

        JFrame employeeFrame = new JFrame("Employee Dashboard");
        employeeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        employeeFrame.setBounds(350, 100, 1200, 800);
        employeeFrame.setResizable(false);
        employeeFrame.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Align buttons vertically
        //buttonPanel

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new CardLayout());
        contentPanel.add(E_HomePanel.createHomePanel(emp));

        String[] options = {
            "Home",
            "Add New Customer", //
            "Add New Billing Info",
            "Edit Tariff Tax Info",
            "Edit Customer Info", // 
            "Edit Customer Bills",
            "View Bill Reports", //
            "View CNIC Reports", //
            "Update Password",
            "Logout"
        };

        Dimension buttonSize = new Dimension(200, 300);

        for (String option : options) {

            JButton optionButton = new JButton(option);
            optionButton.setPreferredSize(buttonSize);
            optionButton.setMaximumSize(buttonSize);
            optionButton.setBackground(new Color(127, 192, 20));
            optionButton.setFocusPainted(false);
            optionButton.setBorder(BorderFactory.createEmptyBorder());

            optionButton.setForeground(Color.WHITE);
            optionButton.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));

            optionButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    optionButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Show black border on hover
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    optionButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not hovering
                }
            });

            buttonPanel.add(optionButton);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            optionButton.addActionListener(e -> {
                CardLayout cl = (CardLayout) (contentPanel.getLayout());

                switch (option) {
                    case "Home" -> {  // Done
                        contentPanel.add(E_HomePanel.createHomePanel(emp), "HomePanel");
                        cl.show(contentPanel, "HomePanel");
                    }
                    case "Add New Customer" -> {  // Done
                        contentPanel.add(E_AddCustomerPanel.createAddCustomerPanel(emp), "AddCustomerPanel");
                        cl.show(contentPanel, "AddCustomerPanel");
                    }
                    case "Add New Billing Info" -> { // Done
                        contentPanel.add(E_AddBillPanel.createBillingInfoPanel(emp), "BillingInfoPanel");
                        cl.show(contentPanel, "BillingInfoPanel");
                    }
                    case "Edit Tariff Tax Info" -> { //Done
                        contentPanel.add(E_UpdateTarrifTax.createTariffTaxInfoPanel(emp), "TariffTaxInfoPanel");
                        cl.show(contentPanel, "TariffTaxInfoPanel");
                    }
                    case "Edit Customer Info" -> { //Done
                        contentPanel.add(E_EditCustomerInfo.createEditCustomerInfoPanel(emp), "EditCustomerInfoPanel");
                        cl.show(contentPanel, "EditCustomerInfoPanel");
                    }
                    case "Edit Customer Bills" -> {   // Done
                        contentPanel.add(E_EditBillInfo.createViewCustomerBillsPanel(emp), "EditCustomerBillsPanel");
                        cl.show(contentPanel, "EditCustomerBillsPanel");
                    }
                    case "View Bill Reports" -> { // Done
                        contentPanel.add(E_BillReportPanel.createViewReportsOfBillPanel(emp), "ViewReportsOfBillPanel");
                        cl.show(contentPanel, "ViewReportsOfBillPanel");
                    }
                    case "View CNIC Reports" -> {  // Done 
                        contentPanel.add(E_CNICReportPanel.createViewCNICReportsPanel(emp), "ViewCNICReportsPanel");
                        cl.show(contentPanel, "ViewCNICReportsPanel");
                    }
                    case "Update Password" -> {  // Done
                        contentPanel.add(E_ChangePasswordPanel.createUpdatePasswordPanel(emp), "UpdatePasswordPanel");
                        cl.show(contentPanel, "UpdatePasswordPanel");
                    }
                    case "Logout" -> {  // Done
                        employeeFrame.dispose();
                        new Lesco();
                    }
                    default -> {
                        //contentPanel.add(new JPanel(), "DefaultPanel");
                        // cl.show(contentPanel, "DefaultPanel");
                    }
                }
            });
        }

        employeeFrame.add(buttonPanel, BorderLayout.WEST);
        employeeFrame.add(contentPanel, BorderLayout.CENTER);

        employeeFrame.setVisible(true);
    }

}
