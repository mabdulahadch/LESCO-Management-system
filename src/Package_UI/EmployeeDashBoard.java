package Package_UI;

import Font.LoadFont;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import Package_BL.Employee;

public class EmployeeDashBoard {

    public void openEmployeeDashboard(Employee emp,Socket socket, ObjectOutputStream objectOut, ObjectInputStream objectIn) {

        JFrame employeeFrame = new JFrame("Employee Dashboard");
        employeeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        employeeFrame.setBounds(350, 100, 1200, 800);
        employeeFrame.setResizable(false);
        employeeFrame.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JPanel contentPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        contentPanel.add(E_HomePanel.createHomePanel(socket, objectOut, objectIn), "Home");
         contentPanel.add(E_AddCustomerPanel.createAddCustomerPanel(socket, objectOut, objectIn), "AddCustomerPanel");//Done
         contentPanel.add(E_AddBillPanel.createBillingInfoPanel(socket, objectOut, objectIn), "BillingInfoPanel");//Done
         contentPanel.add(E_UpdateTarrifTax.createTariffTaxInfoPanel(socket, objectOut, objectIn), "TariffTaxInfoPanel"); //Done
         contentPanel.add(E_EditCustomerInfo.createEditCustomerInfoPanel(socket, objectOut, objectIn), "EditCustomerInfoPanel");//done
         contentPanel.add(E_EditBillInfo.createViewCustomerBillsPanel(socket, objectOut, objectIn), "EditCustomerBillsPanel"); //done
         contentPanel.add(E_BillReportPanel.createViewReportsOfBillPanel(socket, objectOut, objectIn), "ViewReportsOfBillPanel");//Done
         contentPanel.add(E_CNICReportPanel.createViewCNICReportsPanel(socket, objectOut, objectIn), "ViewCNICReportsPanel"); // Done
         contentPanel.add(E_ChangePasswordPanel.createUpdatePasswordPanel(socket, objectOut, objectIn), "UpdatePasswordPanel");//done

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

                switch (option) {
                    case "Home" -> {  // Done
                        cardLayout.show(contentPanel, "Home");
                    }
                    case "Add New Customer" -> {  // Done
                       
                        cardLayout.show(contentPanel, "AddCustomerPanel");
                    }
                    case "Add New Billing Info" -> { // Done
                        cardLayout.show(contentPanel, "BillingInfoPanel");
                    }
                    case "Edit Tariff Tax Info" -> { //Done
                        cardLayout.show(contentPanel, "TariffTaxInfoPanel");
                    }
                    case "Edit Customer Info" -> { //Done
                        // contentPanel.add(E_EditCustomerInfo.createEditCustomerInfoPanel(emp), "EditCustomerInfoPanel");
                        cardLayout.show(contentPanel, "EditCustomerInfoPanel");
                    }
                    case "Edit Customer Bills" -> {   // Done
                        cardLayout.show(contentPanel, "EditCustomerBillsPanel");
                    }
                    case "View Bill Reports" -> { // Done
                        cardLayout.show(contentPanel, "ViewReportsOfBillPanel");
                    }
                    case "View CNIC Reports" -> {  // Done 
                        cardLayout.show(contentPanel, "ViewCNICReportsPanel");
                    }
                    case "Update Password" -> {  // Done
                        cardLayout.show(contentPanel, "UpdatePasswordPanel");
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
