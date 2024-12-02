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
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Package_BL.Customer;
import Package_BL.Employee;
import Package_BL.projectTxtFiles;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class EmployeeLogin {

    JFrame loginFrame;
    private JLabel userLabel, passLabel;
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton, backButton;

    public EmployeeLogin() {

        LoadFont.loadCustomFont();
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

        userLabel = new JLabel("Username:");
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

        passLabel = new JLabel("Password:");
        passLabel.setFont(LoadFont.customFont.deriveFont(Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginFrame.add(passLabel, gbc);

        passField = new JPasswordField();
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
        backButton.setPreferredSize(new Dimension(220, 32));
        backButton.setBorder(BorderFactory.createLineBorder(new Color(127, 192, 20), 2, true));

        backButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 3;
        loginFrame.add(backButton, gbc);

        loginButton.addActionListener((ActionEvent e) -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            try {
                Socket socket = new Socket("localhost", 12345); // Connect to server
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());

                objectOut.writeObject("LOGINASEMPLOYEE");
                objectOut.writeObject(username);
                objectOut.writeObject(password);

                String response = (String) objectIn.readObject();

                if ("SUCCESS".equals(response)) {
                    EmployeeDashBoard empDash = new EmployeeDashBoard();
                    empDash.openEmployeeDashboard(null, socket, objectOut, objectIn); // Open employee dashboard on
                                                                                      // successful login
                    loginFrame.dispose(); // Close login window after successful login
                } else {
                    System.out.println("Login Not Successfull");
                    JOptionPane.showMessageDialog(loginFrame, "Login Failed. Try again.");
                    userField.setText("");
                    passField.setText("");

                }

            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(loginFrame, "Server not available. Try again later.");
                ex.printStackTrace();
            }
        });

        backButton.addActionListener((ActionEvent e) -> {
            loginFrame.dispose();
            // new Lesco();
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
        button.setPreferredSize(new Dimension(220, 32));
        button.setBorder(BorderFactory.createLineBorder(new Color(127, 192, 20), 2, true));

        button.setFocusPainted(false);
        return button;
    }

}
