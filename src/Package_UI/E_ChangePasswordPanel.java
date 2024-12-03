package Package_UI;

import Font.LoadFont;
import Package_BL.Employee;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class E_ChangePasswordPanel {

    public static JPanel createUpdatePasswordPanel(Socket socket, ObjectOutputStream objectOut, ObjectInputStream objectIn) {

        JPanel addCustomerPanel = new JPanel();
        addCustomerPanel.setBackground(Color.WHITE);
        addCustomerPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control of positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        Dimension textFieldSize = new Dimension(220, 30);

        JLabel cnicLabel = new JLabel("Current Password :");
        cnicLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        JPasswordField cnicField = new JPasswordField();
        cnicField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        cnicField.setPreferredSize(textFieldSize);
        addCustomerPanel.add(cnicLabel, gbc);

        gbc.gridx = 1;
        addCustomerPanel.add(cnicField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("New Password :");
        nameLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        JPasswordField nameField = new JPasswordField();
        nameField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        nameField.setPreferredSize(textFieldSize);
        addCustomerPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        addCustomerPanel.add(nameField, gbc);

        // Enter Button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton enterButton = new JButton("Save Password");
        enterButton.setPreferredSize(new Dimension(150, 40));
        enterButton.setBackground(new Color(127, 192, 20));
        enterButton.setForeground(Color.WHITE);
        enterButton.setFocusPainted(false);
        enterButton.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));

        enterButton.setBorder(BorderFactory.createEmptyBorder());

        enterButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                enterButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Show black border on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                enterButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not hovering
            }
        });

        // Action when the current password field is filled
        cnicField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentPassword = new String(cnicField.getPassword());
                try {
                    // Send the current password to the server for validation
                    objectOut.writeObject("ValidateCurrentPass");
                    objectOut.writeObject(currentPassword);
                    objectOut.flush();

                    // Read the server response
                    boolean isValid = (boolean) objectIn.readObject();

                    if (isValid) {
                        nameField.requestFocusInWindow();
                    } else {
                        JOptionPane.showMessageDialog(null, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Action when the user enters new password
        nameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterButton.doClick();
            }
        });

        // Action when the save button is clicked
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentPassword = new String(cnicField.getPassword());
                String newPassword = new String(nameField.getPassword());

                try {
                    // First, validate the current password by sending it to the server
                    objectOut.writeObject("ValidateCurrentPass");
                    objectOut.writeObject(currentPassword);
                    objectOut.flush();

                    // Read the server response
                    boolean isValid = (boolean) objectIn.readObject();

                    if (isValid) {
                        // Send the new password to the server for update
                        objectOut.writeObject("UpdatePass");
                        objectOut.writeObject(newPassword);
                        objectOut.flush();

                        // Read the server response
                        String response = (String) objectIn.readObject();
                        JOptionPane.showMessageDialog(null, response);

                        // Clear the fields
                        cnicField.setText("");
                        nameField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occurred while updating the password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addCustomerPanel.add(enterButton, gbc);

        return addCustomerPanel;

    }
}
