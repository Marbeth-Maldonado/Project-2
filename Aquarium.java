import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;
import java.util.ArrayList;

/**
 * Fighting-game style idle animation for my Robot character
 * Sequence (loops every ~9.5 seconds):
 *  0.0 - 2.2s   Idle bob + subtle breathing
 *  2.2 - 3.3s   Raises arms to activate cannons (body leans in)
 *  3.3 - 4.3s   Cannons charge (glow + sparks)
 *  4.3 - 5.1s   Accidental fire + strong recoil
 *  5.1 - 6.0s   Freezes in surprise, quickly shuts cannons off
 *  6.0 - 7.8s   Looks left & right (body shifts, shoulders drop)
 *  7.8 - 9.5s   Returns to normal idle
 * Emotions are shown mainly through body language.
 * Eyes still change (widen, shift) but the body carries the feeling.
 */
public class Aquarium extends JPanel {

    // Colors matched closely to my original drawing
   // Exact colors from your character sheet
    private static final Color CYAN       = new Color(130, 200, 220);
    private static final Color LIGHT_CYAN = new Color(170, 220, 235);
    private static final Color DARK_CYAN  = new Color(90, 160, 185);
    private static final Color FACE       = new Color(250, 248, 245);
    private static final Color BLACK      = new Color(30, 30, 35);
    private static final Color WHITE      = new Color(245, 245, 240);
    private static final Color RED        = new Color(230, 50, 55);
    private static final Color ORANGE     = new Color(255, 130, 30);
    private static final Color YELLOW     = new Color(255, 220, 70);

   private final Timer timer;
    private long startTime;
    private final Random rng = new Random();
    private final ArrayList<Spark> sparks = new ArrayList<>();
    public Aquarium() {
        setPreferredSize (new Dimension(900, 720));
        setBackground(new Color(252, 250, 245));
        starttime = System.currentTimeMillis();
        timer = new Timer(16, e -> {
            updateSparks();
            repaint();
        });
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Aquarium");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new Aquarium());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        }
        @Override 
        protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        double t = ((System.currentTimeMillis() - startTime) % 9500) / 1000.0;
        // body language