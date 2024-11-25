package Package_UI;

import Font.LoadFont;
import Package_BL.Customer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class C_HomePanel {
    public static JPanel createHomePanel(Customer cst)
    {
         JPanel HomePanel = new JPanel();
        ImageIcon imageIcon = new ImageIcon("src/Images/backgroundimg.png"); // Path to your image
        Image image = imageIcon.getImage().getScaledInstance(1180, 700, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(image));
        background.setLayout(new BorderLayout());

        
        JLabel welcomeLabel = new JLabel("Welcome "+cst.getName());
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
