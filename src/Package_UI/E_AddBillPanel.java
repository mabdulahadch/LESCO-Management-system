package Package_UI;

import Font.LoadFont;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class E_AddBillPanel {

    private static JLabel customerIdsLabel, regUnitsLabel, peakUnitsLabel;
    private static JTextField regUnitsField, peakUnitsField;
    private static JComboBox<String> customerIdDropdown;
    private static JButton generateBillButton;

    public static JPanel createBillingInfoPanel(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in) {

        JPanel addBillPanel = new JPanel();
        addBillPanel.setBackground(Color.WHITE);
        addBillPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Dimension textFieldSize = new Dimension(220, 30);

        gbc.gridx = 0;
        gbc.gridy = 0;
        customerIdsLabel = new JLabel("Customer IDs :");
        customerIdsLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        addBillPanel.add(customerIdsLabel, gbc);

        ArrayList<String> customerIds = getCustomerIdsFromServer(out, in);
        customerIdDropdown = new JComboBox<>(customerIds.toArray(new String[0]));
        customerIdDropdown.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));

        gbc.gridx = 1;
        addBillPanel.add(customerIdDropdown, gbc);

        customerIdDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCustomerId = (String) customerIdDropdown.getSelectedItem();
                boolean isSinglePhase = checkIfSinglePhaseFromServer(selectedCustomerId, out, in);
                if (isSinglePhase) {
                    peakUnitsLabel.setVisible(false);
                    peakUnitsField.setVisible(false);
                    peakUnitsField.setText("0");
                } else {
                    peakUnitsLabel.setVisible(true);
                    peakUnitsField.setVisible(true);
                }
                regUnitsField.requestFocusInWindow();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;

        regUnitsLabel = new JLabel("Current Regular Units :");
        regUnitsLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        addBillPanel.add(regUnitsLabel, gbc);

        regUnitsField = new JTextField();
        regUnitsField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        regUnitsField.setPreferredSize(textFieldSize);

        gbc.gridx = 1;
        addBillPanel.add(regUnitsField, gbc);

        regUnitsField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                peakUnitsField.requestFocusInWindow();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;

        peakUnitsLabel = new JLabel("Current Peak Unit :");
        peakUnitsLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        addBillPanel.add(peakUnitsLabel, gbc);

        peakUnitsField = new JTextField();
        peakUnitsField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        peakUnitsField.setPreferredSize(textFieldSize);

        gbc.gridx = 1;
        addBillPanel.add(peakUnitsField, gbc);

        peakUnitsField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBillButton.doClick();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        generateBillButton = new JButton("Generate Bill");
        generateBillButton.setPreferredSize(new Dimension(150, 40));
        generateBillButton.setBackground(new Color(127, 192, 20));
        generateBillButton.setForeground(Color.WHITE);
        generateBillButton.setFocusPainted(false);
        generateBillButton.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        generateBillButton.setBorder(BorderFactory.createEmptyBorder());

        generateBillButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                generateBillButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Show black border on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                generateBillButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not hovering
            }
        });

        generateBillButton.addActionListener(evt -> {

            String selectedCustomerId = (String) customerIdDropdown.getSelectedItem();
            String regularUnitsStr = regUnitsField.getText();
            String peakUnitsStr = peakUnitsField.getText();

            if (regularUnitsStr.isEmpty() || peakUnitsStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both regular and peak units.");
                return;
            }

            try {
                int regularUnits = Integer.parseInt(regularUnitsStr);
                int peakUnits = Integer.parseInt(peakUnitsStr);

                if (sendBillingInfoToServer(selectedCustomerId, regularUnits, peakUnits, out, in)) {
                    JOptionPane.showMessageDialog(null, "Bill added successfully for Customer ID: " + selectedCustomerId);
                    reloadBillingPanel(addBillPanel, clientSocket, out, in);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add bill for Customer ID: " + selectedCustomerId);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter valid numeric values for the units.");
            }
        });

        addBillPanel.add(generateBillButton, gbc);
        return addBillPanel;
    }

    private static ArrayList<String> getCustomerIdsFromServer(ObjectOutputStream out, ObjectInputStream in) {
        try {
            out.writeObject("GET_CUSTOMER_IDS");
            out.flush();
            
            // Receiving response from the server
            return (ArrayList<String>) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    

    private static boolean checkIfSinglePhaseFromServer(String customerId, ObjectOutputStream out, ObjectInputStream in) {
        try {
            out.writeObject("CHECK_SINGLE_PHASE");
            out.writeObject(customerId);
            out.flush();
            
            // Receiving response from the server
            return (boolean) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    

    private static boolean sendBillingInfoToServer(String customerId, int regularUnits, int peakUnits, ObjectOutputStream out, ObjectInputStream in) {
        try {
            // Sending data to the server
            out.writeObject("ADD_BILLING_INFO");
            out.writeObject(customerId);  // Sending customer ID
            out.writeInt(regularUnits);  // Sending regular units
            out.writeInt(peakUnits);     // Sending peak units
            out.flush();
    
            // Receiving response from the server
            String response = (String) in.readObject();
            System.out.println("Server Response: " + response);
            return "SUCCESS".equalsIgnoreCase(response);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    

    private static void reloadBillingPanel(JPanel billingPanel, Socket clientSocket, ObjectOutputStream out, ObjectInputStream in) {
        billingPanel.removeAll();
        JPanel newBillingPanel = createBillingInfoPanel(clientSocket, out, in);
        billingPanel.add(newBillingPanel);
        billingPanel.revalidate();
        billingPanel.repaint();
    }
}
