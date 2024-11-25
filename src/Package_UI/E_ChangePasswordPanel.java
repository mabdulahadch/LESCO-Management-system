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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class E_ChangePasswordPanel {

    public static JPanel createUpdatePasswordPanel(Employee emp) {

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

//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        
//        
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

        cnicField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentPassword = new String(cnicField.getPassword());
                if (emp.isValidPass(currentPassword)) {
                    nameField.requestFocusInWindow();
                } else {
                    JOptionPane.showMessageDialog(null, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        nameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            enterButton.doClick();
            }
        });
        

        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String currentPassword = new String(cnicField.getPassword());
                String newPassword = new String(nameField.getPassword());

                if (emp.isValidPass(currentPassword)) {

                    emp.updateEmpPassword(newPassword);
                    JOptionPane.showMessageDialog(null, "Password updated successfully!");
                    cnicField.setText("");
                    nameField.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        addCustomerPanel.add(enterButton, gbc);

        return addCustomerPanel;

    }
}
