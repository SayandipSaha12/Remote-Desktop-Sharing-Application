import java.io.Serializable;

public class RemoteCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum CommandType {
        MOUSE_CLICK, MOUSE_MOVE, MOUSE_DRAG, KEY_PRESS
    }
    
    private CommandType type;
    private int x, y, button, keyCode;
    
    public RemoteCommand(CommandType type, int x, int y, int button, int keyCode) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.button = button;
        this.keyCode = keyCode;
    }
    

    public CommandType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getButton() { return button; }
    public int getKeyCode() { return keyCode; }
}