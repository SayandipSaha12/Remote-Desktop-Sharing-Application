import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.imageio.ImageIO;


public class RemoteDesktopServer {
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private Robot robot;
    private Rectangle screenRect;
    private JFrame statusFrame;
    private JLabel statusLabel;
    private JTextArea logArea;
    private List<ClientHandler> clients = new ArrayList<>();
    private ExecutorService executor = Executors.newCachedThreadPool();
    
    public RemoteDesktopServer() {
        try {
            robot = new Robot();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screenRect = new Rectangle(screenSize);
            setupUI();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    
    private void setupUI() {
        statusFrame = new JFrame("Remote Desktop Server");
        statusFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        statusFrame.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout());
        statusLabel = new JLabel("Server Stopped");
        JButton startBtn = new JButton("Start Server");
        JButton stopBtn = new JButton("Stop Server");
        
        startBtn.addActionListener(e -> startServer());
        stopBtn.addActionListener(e -> stopServer());
        
        topPanel.add(statusLabel);
        topPanel.add(startBtn);
        topPanel.add(stopBtn);
        
        logArea = new JTextArea(15, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        statusFrame.add(topPanel, BorderLayout.NORTH);
        statusFrame.add(scrollPane, BorderLayout.CENTER);
        statusFrame.pack();
        statusFrame.setVisible(true);
    }
    
    public void startServer() {
        if (running) return;
        
        try {
            serverSocket = new ServerSocket(5000);
            running = true;
            statusLabel.setText("Server Running on port 5000");
            log("Server started on port 5000");
            
            executor.submit(() -> {
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        ClientHandler handler = new ClientHandler(clientSocket);
                        clients.add(handler);
                        executor.submit(handler);
                        log("Client connected: " + clientSocket.getInetAddress());
                    } catch (IOException e) {
                        if (running) {
                            log("Error accepting client: " + e.getMessage());
                        }
                    }
                }
            });
            
        } catch (IOException e) {
            log("Failed to start server: " + e.getMessage());
        }
    }
    
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            for (ClientHandler client : clients) {
                client.disconnect();
            }
            clients.clear();
            statusLabel.setText("Server Stopped");
            log("Server stopped");
        } catch (IOException e) {
            log("Error stopping server: " + e.getMessage());
        }
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(new Date() + ": " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private volatile boolean connected = true;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                
           
                executor.submit(this::shareScreen);
                
            
                while (connected) {
                    try {
                        RemoteCommand command = (RemoteCommand) in.readObject();
                        handleCommand(command);
                    } catch (ClassNotFoundException | IOException e) {
                        break;
                    }
                }
            } catch (IOException e) {
                log("Client handler error: " + e.getMessage());
            } finally {
                disconnect();
            }
        }
        
        private void shareScreen() {
            while (connected) {
                try {
                    BufferedImage screenshot = robot.createScreenCapture(screenRect);
                    
                  
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(screenshot, "jpg", baos);
                    byte[] imageData = baos.toByteArray();
                    
                    ScreenData screenData = new ScreenData(imageData, screenRect.width, screenRect.height);
                    out.writeObject(screenData);
                    out.flush();
                    
                    Thread.sleep(100); // 10 FPS
                } catch (IOException | InterruptedException e) {
                    break;
                }
            }
        }
        
        private void handleCommand(RemoteCommand command) {
            switch (command.getType()) {
                case MOUSE_CLICK:
                    robot.mouseMove(command.getX(), command.getY());
                    robot.mousePress(command.getButton());
                    robot.mouseRelease(command.getButton());
                    break;
                case MOUSE_MOVE:
                    robot.mouseMove(command.getX(), command.getY());
                    break;
                case KEY_PRESS:
                    robot.keyPress(command.getKeyCode());
                    robot.keyRelease(command.getKeyCode());
                    break;
                case MOUSE_DRAG:
                    robot.mouseMove(command.getX(), command.getY());
                    break;
            }
        }
        
        public void disconnect() {
            connected = false;
            try {
                if (socket != null) socket.close();
                clients.remove(this);
                log("Client disconnected");
            } catch (IOException e) {
            
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RemoteDesktopServer());
    }
}