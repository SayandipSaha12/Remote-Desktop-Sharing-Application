
import javax.swing.*;
import java.awt.*;

public class RemoteDesktopLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame launcher = new JFrame("Remote Desktop Application");
            launcher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            launcher.setLayout(new GridLayout(3, 1, 10, 10));
            launcher.setSize(300, 200);
            launcher.setLocationRelativeTo(null);
            
            JLabel titleLabel = new JLabel("Choose Mode:", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            
            JButton serverBtn = new JButton("Start as Server");
            JButton clientBtn = new JButton("Start as Client");
            
            serverBtn.addActionListener(e -> {
                new RemoteDesktopServer();
                launcher.dispose();
            });
            
            clientBtn.addActionListener(e -> {
                new RemoteDesktopClient();
                launcher.dispose();
            });
            
            launcher.add(titleLabel);
            launcher.add(serverBtn);
            launcher.add(clientBtn);
            launcher.setVisible(true);
        });
    }
}