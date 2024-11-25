package Package_UI;

import Font.LoadFont;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import Package_BL.Customer;
import Package_BL.projectTxtFiles;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class CustomerLogin {

    JFrame loginFrame;
    private JLabel userLabel, passLabel;
    private JTextField userField, passField;
    private JButton loginButton, backButton;

    public CustomerLogin() {
        loginFrame = new JFrame("Employee Login");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setBounds(350, 100, 1200, 800);
        loginFrame.setResizable(false);

        loginFrame.setContentPane(new BackgroundPanel());

        init();

        loginFrame.setVisible(true);
    }

    private void init() {

        loginFrame.getContentPane().setBackground(Color.white);
        loginFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        userLabel = new JLabel("User CNIC:");
        userLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginFrame.add(userLabel, gbc);

        userField = new JTextField();
        userField.setPreferredSize(new Dimension(220, 30));
        userField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        gbc.gridx = 1;
        gbc.gridy = 0;
        loginFrame.add(userField, gbc);

        userField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Transfer focus to password field
                passField.requestFocusInWindow();
            }
        });

        passLabel = new JLabel("User ID:");
        passLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginFrame.add(passLabel, gbc);

        passField = new JTextField();
        passField.setPreferredSize(new Dimension(220, 30));
        passField.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 15));
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginFrame.add(passField, gbc);
        
                passField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Transfer focus to password field
               loginButton.doClick();
            }
        });

        loginButton = createStyledButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginFrame.add(loginButton, gbc);

        backButton = new JButton("Back");

        backButton.setForeground(new Color(127, 192, 20));
        backButton.setBackground(Color.WHITE);
        backButton.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        backButton.setPreferredSize(new Dimension(350, 32));
        backButton.setBorder(BorderFactory.createLineBorder(new Color(127, 192, 20), 2, true));

        backButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 3;
        loginFrame.add(backButton, gbc);

        backButton.addActionListener((ActionEvent e) -> {
            loginFrame.dispose();
            new Lesco();
        });

        loginButton.addActionListener((ActionEvent e) -> {
            String userID = passField.getText();
            String userCNIC = userField.getText();
            Customer cst = custLogin(userID, userCNIC);

            if (cst != null) {
                CustomerDashBoard cstDash = new CustomerDashBoard();
                cstDash.openCustomerDashboard(cst);
                loginFrame.dispose();
            } else {
                System.out.println("Login Not Successfull");
                JOptionPane.showMessageDialog(loginFrame, "Login Failed. Try again.");
                userField.setText("");
                passField.setText("");

            }
        });
    }

    class BackgroundPanel extends JPanel {

        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                // Load the background image
                backgroundImage = new ImageIcon("src/Images/backgroundimg3.png").getImage();
            } catch (Exception e) {
                System.out.println("Image not found.");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Draw the image scaled to the size of the panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);

        button.setForeground(Color.WHITE);
        button.setBackground(new Color(127, 192, 20));
        button.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        button.setPreferredSize(new Dimension(350, 32));
        button.setBorder(BorderFactory.createLineBorder(new Color(127, 192, 20), 2, true));

        button.setFocusPainted(false);
        return button;
    }

    public static Customer custLogin(String userID, String userCNIC) {

        File file = new File(projectTxtFiles.CustomerFile);

        if (file.length() == 0) {
            System.out.println("Nothing in the file! No records found.");
            return null;
        }

        if (validateCustLogin(userID, userCNIC)) {
            //System.out.println("Login successful!");
            return new Customer(userID, userCNIC);
        } else {
            System.out.println("Invalid userId or cnic. Enter Again!");
            return null;
        }
    }

    public static boolean validateCustLogin(String userId, String cnic) {
        try (BufferedReader reader = new BufferedReader(new FileReader(projectTxtFiles.CustomerFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] custData = line.split(",");
                if (custData[0].equals(userId) && custData[4].equals(cnic)) {
                    System.out.println("Logged In as : " + custData[1]);
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reasding file");
        }
        return false;
    }

}
