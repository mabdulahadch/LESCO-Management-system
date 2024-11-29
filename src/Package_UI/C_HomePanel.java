package Package_UI;

import Font.LoadFont;
import Package_BL.Customer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class C_HomePanel {
    public static JPanel createHomePanel(Socket socket, ObjectOutputStream objectOut, ObjectInputStream objectIn) {
        JPanel HomePanel = new JPanel();
        ImageIcon imageIcon = new ImageIcon("src/Images/backgroundimg.png"); // Path to your image
        Image image = imageIcon.getImage().getScaledInstance(1180, 700, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(image));
        background.setLayout(new BorderLayout());

        String response = "";
        try {
            // Request the customer's name
            objectOut.writeObject("getName");
            response = (String) objectIn.readObject(); // Read the server's response
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        JLabel welcomeLabel = new JLabel("Welcome " + response);
        welcomeLabel.setFont(LoadFont.customFont1.deriveFont(Font.BOLD, 73));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER); // Center the text horizontally
        welcomeLabel.setVerticalAlignment(JLabel.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        background.add(welcomeLabel, BorderLayout.CENTER);

        HomePanel.add(background);

        return HomePanel;
    }
}
