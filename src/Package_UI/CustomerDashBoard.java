package Package_UI;

import Package_BL.Customer;

import Font.LoadFont;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class CustomerDashBoard {

    public void openCustomerDashboard(Socket socket, ObjectOutputStream objectOut, ObjectInputStream objectIn) {

        try {
            JFrame customerFrame = new JFrame("Customer Dashboard");
            customerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            customerFrame.setBounds(350, 100, 1200, 800);
            customerFrame.setLayout(new BorderLayout());
            customerFrame.setResizable(false);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

            JPanel contentPanel = new JPanel();
            CardLayout cardLayout = new CardLayout();
            contentPanel.setLayout(cardLayout);

            contentPanel.add(C_HomePanel.createHomePanel(socket, objectOut, objectIn), "Home");
            contentPanel.add(C_ViewBillPanel.createViewBillPanel(socket, objectOut, objectIn), "ViewBill");
            contentPanel.add(C_UpdateCNICPanel.createUpdateCNICPanel(socket, objectOut, objectIn), "UpdateCNIC");

            String[] options = {
                    "Home",
                    "View Bill",
                    "Update CNIC Expiry Date",
                    "Logout"
            };

            Dimension buttonSize = new Dimension(700, 50);
            final JButton[] btn = new JButton[4];

            for (int i = 0; i < 4; i++) {
                int index = i;

                btn[index] = new JButton(options[index]);
                btn[index].setPreferredSize(buttonSize);
                btn[index].setMaximumSize(buttonSize);
                btn[index].setBackground(new Color(127, 192, 20));
                btn[index].setFocusPainted(false);
                btn[index].setForeground(Color.WHITE);
                btn[index].setFont(LoadFont.customFont.deriveFont(Font.BOLD, 15));

                if (index == 0) {
                    Border leftBorder1 = BorderFactory.createMatteBorder(0, 3, 0, 0, Color.WHITE);
                    btn[index].setBorder(leftBorder1);
                } else {
                    btn[index].setBorder(BorderFactory.createEmptyBorder());
                }

                btn[index].addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        btn[index].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        if (index == 0) {
                            btn[index].setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createMatteBorder(0, 3, 0, 0, Color.WHITE),
                                    BorderFactory.createEmptyBorder()));
                        } else {
                            btn[index].setBorder(BorderFactory.createEmptyBorder());
                        }
                    }
                });

                buttonPanel.add(btn[index]);
                buttonPanel.add(Box.createRigidArea(new Dimension(3, 0)));

                btn[index].addActionListener(e -> {
                    
                    if (options[index].equals("Home")) {
                        cardLayout.show(contentPanel, "Home");
                    } else if (options[index].equals("View Bill")) {
                        cardLayout.show(contentPanel, "ViewBill");
                    } else if (options[index].equals("Update CNIC Expiry Date")) {
                        cardLayout.show(contentPanel, "UpdateCNIC");
                    } else if (options[index].equals("Logout")) {
                        customerFrame.dispose();
                        try {
                            if (socket != null && !socket.isClosed()) {
                                socket.close();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }

            customerFrame.add(buttonPanel, BorderLayout.NORTH);
            customerFrame.add(contentPanel, BorderLayout.CENTER);

            customerFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
