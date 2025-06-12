import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;

public class RemoteDesktopClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JFrame frame;
    private JLabel imageLabel;
    private volatile boolean connected = false;
    private double scaleX = 1.0, scaleY = 1.0;
    private int remoteWidth, remoteHeight;
    
    public RemoteDesktopClient() {
        setupUI();
    }
    
    private void setupUI() {
        frame = new JFrame("Remote Desktop Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField serverField = new JTextField("localhost", 15);
        JButton connectBtn = new JButton("Connect");
        JButton disconnectBtn = new JButton("Disconnect");
        
        connectBtn.addActionListener(e -> connect(serverField.getText()));
        disconnectBtn.addActionListener(e -> disconnect());
        
        topPanel.add(new JLabel("Server:"));
        topPanel.add(serverField);
        topPanel.add(connectBtn);
        topPanel.add(disconnectBtn);
        
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
     
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (connected) {
                    int x = (int) (e.getX() / scaleX);
                    int y = (int) (e.getY() / scaleY);
                    int button = e.getButton() == MouseEvent.BUTTON1 ? InputEvent.BUTTON1_DOWN_MASK : 
                                e.getButton() == MouseEvent.BUTTON3 ? InputEvent.BUTTON3_DOWN_MASK : 
                                InputEvent.BUTTON2_DOWN_MASK;
                    sendCommand(new RemoteCommand(RemoteCommand.CommandType.MOUSE_CLICK, x, y, button, 0));
                }
            }
        });
        
        imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (connected) {
                    int x = (int) (e.getX() / scaleX);
                    int y = (int) (e.getY() / scaleY);
                    sendCommand(new RemoteCommand(RemoteCommand.CommandType.MOUSE_MOVE, x, y, 0, 0));
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (connected) {
                    int x = (int) (e.getX() / scaleX);
                    int y = (int) (e.getY() / scaleY);
                    sendCommand(new RemoteCommand(RemoteCommand.CommandType.MOUSE_DRAG, x, y, 0, 0));
                }
            }
        });
        
 
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (connected) {
                    sendCommand(new RemoteCommand(RemoteCommand.CommandType.KEY_PRESS, 0, 0, 0, e.getKeyCode()));
                }
            }
        });
        
        frame.setFocusable(true);
        
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
    
    public void connect(String serverAddress) {
        try {
            socket = new Socket(serverAddress, 5000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
            
           
            new Thread(this::receiveScreenUpdates).start();
            
            frame.setTitle("Remote Desktop Client - Connected to " + serverAddress);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to connect: " + e.getMessage());
        }
    }
    
    private void receiveScreenUpdates() {
        while (connected) {
            try {
                ScreenData screenData = (ScreenData) in.readObject();
                
             
                ByteArrayInputStream bais = new ByteArrayInputStream(screenData.getImageData());
                BufferedImage image = ImageIO.read(bais);
                
                remoteWidth = screenData.getWidth();
                remoteHeight = screenData.getHeight();
                
           
                Dimension labelSize = imageLabel.getSize();
                if (labelSize.width > 0 && labelSize.height > 0) {
                    scaleX = (double) labelSize.width / remoteWidth;
                    scaleY = (double) labelSize.height / remoteHeight;
                    double scale = Math.min(scaleX, scaleY);
                    scaleX = scaleY = scale;
                    
                    int scaledWidth = (int) (remoteWidth * scale);
                    int scaledHeight = (int) (remoteHeight * scale);
                    
                    Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_FAST);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                }
                
            } catch (IOException | ClassNotFoundException e) {
                if (connected) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(frame, "Connection lost: " + e.getMessage()));
                }
                break;
            }
        }
    }
    
    private void sendCommand(RemoteCommand command) {
        try {
            if (out != null) {
                out.writeObject(command);
                out.flush();
            }
        } catch (IOException e) {
        }
    }
    
    public void disconnect() {
        connected = false;
        try {
            if (socket != null) socket.close();
            frame.setTitle("Remote Desktop Client - Disconnected");
            imageLabel.setIcon(null);
        } catch (IOException e) {
            // Ignore
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RemoteDesktopClient());
    }
}