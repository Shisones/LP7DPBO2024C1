import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    private int frameWidth = 360;
    private int frameHeight = 640;

    // Image attributes
    private Image backgroundImage;
    private Image birdImage;
    private Image lowerPipeImage;
    private Image upperPipeImage;

    // Player attributes
    private int playerStartPosX = frameWidth / 8;
    private int playerStartPosY = frameHeight / 2;
    private int playerWidth = 34;
    private int playerHeight = 24;
    private Player player;

    // Pipe attributes
    private int pipeStartPosX = frameWidth;
    private int pipeStartPosY = 0;
    private int pipeWidth = 64;
    private int pipeHeight = 512;
    private ArrayList<Pipe> pipes;

    // Game logic attributes
    private JLabel gameOverLabel;
    private Timer gameLoop;
    private Timer pipesCooldown;
    private int gravity = 0;
    private boolean gameOver = false;
    private int score = 0;

    private boolean mainMenu = true;
    // Constructor
    public FlappyBird() {
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setFocusable(true);
        requestFocus();

        addKeyListener(this);

        // Make an option pane popup to start the game
        int opt = JOptionPane.showOptionDialog(null, "Florpy birb", "Florpy birb", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if(opt == JOptionPane.OK_OPTION){ game();}
        else { System.exit(0); }
    }

    public void game() {
        // Image importing
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        // Game initialization
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();
        score = 0;

        // Pipes cooldown timer
        pipesCooldown = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        pipesCooldown.start();

        // Set the gameLoop timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Drawing the BG and Player
        g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeight, null);
        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);

        if (mainMenu = true){
            drawMenu(g);
        }

        // Drawing the score
        g.setFont(new Font("Roboto", Font.BOLD, 16));
        g.setColor(Color.white);
        g.drawString("Score: " + score / 2, 15, 30);

        // Drawing the pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }
    }

    public void drawMenu(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Roboto", Font.BOLD, 35));
        g.drawString("Florpy Birb", frameWidth / 2 - g.getFontMetrics().stringWidth("Florpy Birb") / 2, frameHeight / 2 + 50);
    }

    public void move() {
        // Set the player position and velocity
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));


        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());

            if ( // Check for pipe collisions
                    player.getPosY() >= frameHeight || // Relatif frameheight, makanya kl offset 0, dia noclip
                    player.getPosX() + player.getWidth() >= pipe.getPosX() && // Outline X
                    player.getPosY() + player.getHeight() >= pipe.getPosY() && // Outline Y
                    player.getPosX() <= pipe.getPosX() + pipe.getWidth() && // Player Clipping X
                    player.getPosY() <= pipe.getPosY() + pipe.getHeight() // Player Clipping Y
            ) {
                stopGame();
            } else if (pipe.getPosX() < player.getPosX() && !pipe.isPassed()) {
                score++; pipe.setPassed(true);
            }

        }
    }

    public void placePipes() {
        // Placing the pipes on the screen
        int randomPipePosY = (int) (pipeStartPosY - pipeHeight/4 - Math.random() * (pipeHeight/2));
        int openingSpace = frameHeight/4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPipePosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPipePosY + pipeHeight + openingSpace, pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }
    private void stopGame() {
        // Set Gameover to true and stop the gameloop timer
        gameLoop.stop();
        gameOver = true;

        // Ini awalnya pengen pake main menu draw (yg florpy bird itu tulisan dibawah kanan; tapi masalahnya key eventnya ga jalan, gatau kenapa (linux windows jg gabisa)
        gameOverLabel = new JLabel();
        gameOverLabel.setFont(new Font("Roboto", Font.BOLD, 15));
        gameOverLabel.setForeground(Color.WHITE);
        gameOverLabel.setVisible(true);
        // Ini jelek banget dah harus pake html, nyari dimana mana gaada caranya selain dipisah jadi 2 JLabel berbeda
        gameOverLabel.setText("<html>Game Over, Final Score: " + score/2 + "</html>");
        gameOverLabel.setBounds(frameWidth/2 - 50, frameHeight/2, 300, 150);
        add(gameOverLabel);

//        restartGame();

    }
    private void restartGame() {
        score = 0; // Reset score

        // Reset player location
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);

        // delete gameOverLabel
        remove(gameOverLabel);

        // Reset all pipe-related things
        pipes.clear();
        pipesCooldown.restart();
        gameLoop.start(); // Restart the game
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    // INI KEY EVENT SIALAN GA KERJA RAHHHHHHHH
    // ngoding jg ngetesnya ga dimainin gamenya, ngeset manual si gravitynya sama pos si playernya
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setVelocityY(-10);
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restartGame();
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }

}
