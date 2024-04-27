import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Controls the Frame config
        JFrame frame = new JFrame("Florpy Birb");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(360, 640);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        // Controls the FlappyBird JPanel config
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        frame.setVisible(true);
    }
}
