package Package_UI;

import Font.LoadFont;
import Package_BL.Employee;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class E_HomePanel {
    
    public static JPanel createHomePanel(Employee emp) {
        
        JPanel HomePanel = new JPanel();
        ImageIcon imageIcon = new ImageIcon("src/Images/backgroundimg.png"); // Path to your image
        Image image = imageIcon.getImage().getScaledInstance(980, 750, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(image));
        background.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome "+emp.getUserName());
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
