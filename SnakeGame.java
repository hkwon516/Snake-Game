import javax.swing.*;
import java.awt.*;

public class SnakeGame{
    public static void main(String[] args){
        JFrame frame = new JFrame("Snake Game"); //create JFrame with name Snake Game
        frame.setSize(new Dimension(320,320));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        SnakeGamePanel panel = new SnakeGamePanel();
        frame.add(panel, BorderLayout.CENTER);
        
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}