import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGamePanel extends JPanel{
    final private int appleSize = 20;
    final private int screenWidth = 320;
    final private int screenHeight = 320;
    final private Random rd = new Random();
    
    private boolean paused;
    

    // Timers
    private Timer timer;
    private int delay = 250;

    // Position of snake body and apple
    ArrayList<Sequence> snake = new ArrayList<>();
    Sequence apple;
    int eatApple;

    // x, y coordination
    private int x_pos;
    private int y_pos;

    //keep tracks of the user's key stoke
    private int facing = 0; 
    private int lastFacing = 0;

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.RED);
        for(Sequence s: snake)
            g.fillRect(s.x, s.y, 20, 20);

        g.setColor(Color.GREEN);
        g.fillRect(apple.x, apple.y, appleSize, appleSize);

        repaint();
    }

    public SnakeGamePanel(){
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setFocusable(true);
        setBackground(Color.BLUE);
        newGame();
    }

    /** The player can hit multiple and different arrow keys. And the last key overrides all previous keys. */
    private class DirectionListener implements KeyListener{
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_UP && lastFacing != KeyEvent.VK_DOWN){
                facing = key;
            }
            else if (key == KeyEvent.VK_DOWN && lastFacing != KeyEvent.VK_UP){
                facing = key;
            }
            else if (key == KeyEvent.VK_LEFT && lastFacing != KeyEvent.VK_RIGHT){
                facing = key;
            }
            else if (key == KeyEvent.VK_RIGHT && lastFacing != KeyEvent.VK_LEFT){
                facing = key;
            }
            else if (key == KeyEvent.VK_SPACE){
                facing = key;
                pauseGame();
            }
            // System.out.println(key);
        }
        @Override
        public void keyTyped(KeyEvent e) {}
        @Override
        public void keyReleased(KeyEvent e) {}
    }

    private static class Sequence{
        public int x = 0;
        public int y = 0;

        public Sequence(int x, int y){
            this.x = x;
            this.y = y;
        }

        public boolean equals(Sequence next){
            return next.x == this.x && next.y == this.y;
        }
    }

    // Update snake's direction by the last keypress from multipleKeyArrowKeys list 
    private class MovingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
                if(facing != 0 &&  facing != lastFacing ) {
                    // System.out.println("READS FACING");
                    switch (facing) {
                        // System.out.println("READS FACING22");
                        case 38: //KeyEvent.VK_UP
                            x_pos = 0;
                            y_pos = -20;
                            lastFacing = KeyEvent.VK_DOWN;
                            // System.out.println("lastFacing : " + lastFacing);
                            break;
                        case 40: //KeyEvent.VK_DOWN
                            x_pos = 0;
                            y_pos = 20;
                            lastFacing = KeyEvent.VK_UP;
                            // System.out.println("lastFacing : " + lastFacing);
                            break;
                        case 37: //KeyEvent.VK_LEFT
                            x_pos = -20;
                            y_pos = 0;
                            lastFacing = KeyEvent.VK_RIGHT;
                            // System.out.println("lastFacing : " + lastFacing);
                            break;
                        case 39: //KeyEvent.VK_RIGHT
                            x_pos = 20;
                            y_pos = 0;
                            lastFacing = KeyEvent.VK_LEFT;
                            // System.out.println("lastFacing : " + lastFacing);
                            break;
                        case 32: //KeyEvent.VK_SPACE
                            facing = 0;
                            // pauseGame();
                            break;
                    }
                }

            //add snake's body in the head
            snake.add(new Sequence(snake.get(snake.size() - 1).x + x_pos, snake.get(snake.size() - 1).y + y_pos));

            //remove we added the head, remove the tail of the snake 
            //todo  this is wrong
            if (ifSnakeOverlapsApple(apple))
                nextApple();
            else
                snake.remove(0);

            // System.out.println("snake position", x_pos, y_pos);
            if (isGameOver()){
                timer.stop();
                JOptionPane.showMessageDialog(new JFrame("Game Over"),"Game Over, New Game?");
                newGame();
                timer.restart();
            }
            repaint();
        }
    }

    /**
     * Generate a new apple if the head of the snake is overlapping the apple's position
     * @return  {@code true} if the snake ate the apple
     */
    public void pauseGame(){
        paused = !paused;
        if(paused){
            timer.stop();
            JOptionPane.showMessageDialog( new JFrame("Paused"),"Pause, continue?");
            timer.restart();
        }
    }

    public boolean ifSnakeOverlapsApple(Sequence apple){
        for (Sequence s : snake){
            if (s.equals(apple)){
                return true;
            }
        }
        return false;
    }

    private void nextApple(){
        if(snake.get(snake.size() - 1).equals(apple)) {
            eatApple += 1;
            
            do{
                apple = rdGeneration(); //make a new generation until apple doesn't overlap a
            }
            while(ifSnakeOverlapsApple(apple));

            if(eatApple % 4 == 0){
                if(delay > 50){
                    eatApple = 0;
                    delay -= 20;
                    timer.setDelay(delay);
                    // System.out.println("delay : " +  delay);
                    // System.out.println("apple + delay", apple, delay);
                }
            }
        }
    }

    /**
     * Check to see if the snake will be hitting the wall or itself
     * @return  {@code true} if game is over
     */
    private boolean isGameOver(){
        // Sequence snakeHead = new Sequence(snake.get(snake.size() - 1).x + x_pos, snake.get(snake.size() - 1).y + y_pos);
        
        Sequence snakeHead = snake.get(snake.size() - 1);
        if (snakeHead.y >= screenHeight || snakeHead.y < 0 || snakeHead.x < 0 || snakeHead.x >= screenWidth || overlapSnake()) {
            return true;
        }

        return false;
    }

    //checks if the snake hits itself 
    //returns true if the snake hits itslef
    private boolean overlapSnake(){
        for(Sequence i: snake)
            for(Sequence j: snake)
                if(i != j && i.equals(j)){
                    // {System.out.println("HJANNAH" + i + " and " + j);
                    // System.out.println(snake.size());
                    for (int s = 0; s < snake.size(); s++){
                        // System.out.println(s + "HANAHx" + snake.get(s).x);
                        // System.out.println(s + "HANAHy" + snake.get(s).y);
                        
                    }
                    
                    return true;}
        return false;
    }


    /* Resets all the variables.*/
    private void newGame(){
        // Clear previous values
        snake.clear();
        x_pos = 0;
        y_pos = 0;
        delay = 250;
        facing = 0;
        lastFacing = 0;
        eatApple = 0;
        timer = new Timer(delay, new MovingListener());

        // Initialize variables
        snake.add(rdGeneration());
        apple = rdGeneration();

        // Add listeners and start the game
        addKeyListener(new DirectionListener());
        timer.start();
    }

    private Sequence rdGeneration(){
        // problem
        return new Sequence(rd.nextInt(screenWidth/20) * 20, rd.nextInt(screenHeight/20) * 20);
    }

}