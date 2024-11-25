package Package_UI;
import Package_BL.Customer;

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
import javax.swing.border.Border;

public class CustomerDashBoard {

    public void openCustomerDashboard(Customer cst) {
        JFrame customerFrame = new JFrame("Customer Dashboard");
        customerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        customerFrame.setBounds(350, 100, 1200, 800);
        customerFrame.setLayout(new BorderLayout());
        customerFrame.setResizable(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new CardLayout());
        contentPanel.add(C_HomePanel.createHomePanel(cst));
        
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

            // ActionListener for the buttons
            btn[index].addActionListener(e -> {
                CardLayout cl = (CardLayout) (contentPanel.getLayout());
                if (options[index].equals("Home")) {
                    contentPanel.add(C_HomePanel.createHomePanel(cst), "Home");
                    cl.show(contentPanel, "Home");
                }else if (options[index].equals("View Bill")) {
                    contentPanel.add(C_ViewBillPanel.createViewBillPanel(cst), "ViewBill");
                    cl.show(contentPanel, "ViewBill");
                } else if (options[index].equals("Update CNIC Expiry Date")) {
                    contentPanel.add(C_UpdateCNICPanel.createUpdateCNICPanel(cst), "UpdateCNIC");
                    cl.show(contentPanel, "UpdateCNIC");
                } else if (options[index].equals("Logout")) {
                    customerFrame.dispose();
                    new Lesco();
                }
            });
        }

        customerFrame.add(buttonPanel, BorderLayout.NORTH);
        customerFrame.add(contentPanel, BorderLayout.CENTER);

        customerFrame.setVisible(true);
    }


}
