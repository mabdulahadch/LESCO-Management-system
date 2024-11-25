package Package_UI;

import Font.LoadFont;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Lesco {

    JFrame frame;
    JLabel welcomeLabel;
    JButton userButton, employeeButton;

    public Lesco() {

        LoadFont.loadCustomFont();

        frame = new JFrame("Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(350, 100, 1200, 800);
        frame.setResizable(false);

        frame.setContentPane(new BackgroundPanel());

        init();
        frame.setVisible(true);
    }

    private void init() {

        frame.getContentPane().setBackground(Color.WHITE);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        welcomeLabel = new JLabel("Welcome to LESCO Billing System");
        welcomeLabel.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setForeground(new Color(127, 192, 20));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        frame.add(welcomeLabel, gbc);

        userButton = createStyledButton("USER DASHBOARD");
        employeeButton = createStyledButton("EMPLOYEE DASHBOARD");

        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserLoginPage();
            }
        });

        employeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEmpLoginPage();
            }
        });

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(userButton, gbc);

        gbc.gridy = 2;
        frame.add(employeeButton, gbc);

        userButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                userButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                userButton.setBorder(BorderFactory.createEmptyBorder());
            }
        });

        employeeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                employeeButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                employeeButton.setBorder(BorderFactory.createEmptyBorder());
            }
        });

    }

    class BackgroundPanel extends JPanel {

        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = new ImageIcon("src/Images/backgroundimg3.png").getImage();
            } catch (Exception e) {
                System.out.println("Image not found.");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);

        button.setForeground(Color.WHITE);
        button.setBackground(new Color(127, 192, 20));
        button.setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));
        button.setPreferredSize(new Dimension(250, 50));
        button.setBorder(BorderFactory.createLineBorder(new Color(127, 192, 20), 1, true));

        button.setFocusPainted(false);
        return button;
    }

    private void openUserLoginPage() {
        CustomerLogin customerLogin = new CustomerLogin();
        frame.dispose();
    }

    private void openEmpLoginPage() {
        EmployeeLogin employeeLogin = new EmployeeLogin();
        frame.dispose();
    }

    // public static void main(String[] args) {

    //     // Lesco lesco = new Lesco();
    //     CustomerLogin customerLogin = new CustomerLogin();
    //     // EmployeeLogin employeeLogin = new EmployeeLogin();
    // }

}
