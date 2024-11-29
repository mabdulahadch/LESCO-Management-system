package Package_UI;

import Font.LoadFont;
import Package_BL.Employee;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class E_HomePanel {

    public static JPanel createHomePanel(Socket socket, ObjectOutputStream objectOut, ObjectInputStream objectIn) {

        JPanel HomePanel = new JPanel();
        ImageIcon imageIcon = new ImageIcon("src/Images/backgroundimg.png"); // Path to your image
        Image image = imageIcon.getImage().getScaledInstance(980, 750, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(image));
        background.setLayout(new BorderLayout());

        String response = "";
        try {

            objectOut.writeObject("getUserName");
            response = (String) objectIn.readObject(); // Read the server's response

        } catch (Exception ex) {
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
