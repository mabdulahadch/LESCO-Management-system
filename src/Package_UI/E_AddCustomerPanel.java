package Package_UI;

import Font.LoadFont;
import Package_BL.Employee;
import Package_BL.projectTxtFiles;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class E_AddCustomerPanel {

    private static JTextField nameField, cnicField, addressField,phoneNumberField;

    public static JPanel createAddCustomerPanel(Employee emp) {

        JPanel addCustomerPanel = new JPanel();
        addCustomerPanel.setBackground(Color.WHITE);
        addCustomerPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control of positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        Dimension textFieldSize = new Dimension(220, 30);

        JLabel cnicLabel = new JLabel("CNIC:");
        cnicLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        cnicField = new JTextField();
        cnicField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        cnicField.setPreferredSize(textFieldSize);
        addCustomerPanel.add(cnicLabel, gbc);

        cnicField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.requestFocusInWindow();
            }
        });

        gbc.gridx = 1;
        addCustomerPanel.add(cnicField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        nameField = new JTextField();
        nameField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        nameField.setPreferredSize(textFieldSize);
        addCustomerPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        addCustomerPanel.add(nameField, gbc);

        nameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addressField.requestFocusInWindow();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        addressField = new JTextField();
        addressField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        addressField.setPreferredSize(textFieldSize);
        addCustomerPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        addCustomerPanel.add(addressField, gbc);

        addressField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                phoneNumberField.requestFocusInWindow();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        phoneNumberField = new JTextField();
        phoneNumberField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        phoneNumberField.setPreferredSize(textFieldSize);
        addCustomerPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        addCustomerPanel.add(phoneNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel customerTypeLabel = new JLabel("Customer Type:");
        customerTypeLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        JComboBox<String> customerTypeDropdown = new JComboBox<>(new String[]{"Domestic", "Commercial"});
        customerTypeDropdown.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        addCustomerPanel.add(customerTypeLabel, gbc);

        gbc.gridx = 1;
        addCustomerPanel.add(customerTypeDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;

        JLabel meterTypeLabel = new JLabel("Meter Type:");
        meterTypeLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));

        JPanel meterTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        meterTypePanel.setBackground(Color.WHITE);

        JCheckBox singlePhaseCheckbox = new JCheckBox("Single Phase");
        JCheckBox threePhaseCheckbox = new JCheckBox("Three Phase");

        singlePhaseCheckbox.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        threePhaseCheckbox.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        singlePhaseCheckbox.setBackground(Color.WHITE);
        threePhaseCheckbox.setBackground(Color.WHITE);

        ButtonGroup meterTypeGroup = new ButtonGroup();
        meterTypeGroup.add(singlePhaseCheckbox);
        meterTypeGroup.add(threePhaseCheckbox);

        meterTypePanel.add(singlePhaseCheckbox);
        meterTypePanel.add(threePhaseCheckbox);

        singlePhaseCheckbox.setFocusPainted(false);
        threePhaseCheckbox.setFocusPainted(false);

        addCustomerPanel.add(meterTypeLabel, gbc);

        gbc.gridx = 1;
        addCustomerPanel.add(meterTypePanel, gbc);

        // Enter Button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton enterButton = new JButton("Enter");
        enterButton.setPreferredSize(new Dimension(150, 40));
        enterButton.setBackground(new Color(127, 192, 20));
        enterButton.setForeground(Color.WHITE);
        enterButton.setFocusPainted(false);
        enterButton.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));

        enterButton.setBorder(BorderFactory.createEmptyBorder());

        enterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                enterButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Show black border on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                enterButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not hovering
            }
        });

        enterButton.addActionListener(evt -> {
            String cnic = cnicField.getText();
            String name = nameField.getText();
            String address = addressField.getText();
            String phoneNumber = phoneNumberField.getText();
            String customerType = (String) customerTypeDropdown.getSelectedItem();
            String meterType = singlePhaseCheckbox.isSelected() ? "Single" : "Three";

            // System.out.println(cnic+" "+name+" "+address+" "+phoneNumber+" "+customerType+" "+meterType);
            if (cnic.isEmpty() || name.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fill out the fields"); // Display error message
                return; // Exit the action listener if validation fails
            }

            String response = emp.addCustomerDetails(projectTxtFiles.CustomerFile, cnic, name, address, phoneNumber, customerType, meterType);

            if ("true".equals(response)) {
                // Further action upon success
                JOptionPane.showMessageDialog(null, "Successfully Added Customer !"); // Show error message
                cnicField.setText(" ");
                nameField.setText(" ");
                addressField.setText(" ");
                phoneNumberField.setText(" ");
                singlePhaseCheckbox.setSelected(false);
                threePhaseCheckbox.setSelected(false);

            } else {
                JOptionPane.showMessageDialog(null, response); // Show error message
            }
        });

        addCustomerPanel.add(enterButton, gbc);

        return addCustomerPanel;
    }

}
