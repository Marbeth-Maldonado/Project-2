import javax.swing.*;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * BlueSpark and Sherry - Splash Art
 *
 * BlueSpark (foreground) accidentally fires her arm cannon while
 * Sherry (background) recoils from the blast.
 *
 * Both characters are drawn in their OWN local coordinate space and then
 * moved/scaled into the scene, so you can resize or reposition either
 * one by changing a single translate/scale line in paintComponent.
 */
public class Aquarium extends JPanel {

    private static final int WIDTH = 1100;
    private static final int HEIGHT = 700;

    // Where the cannon muzzle is and where the blast lands (scene coordinates).
    private static final double MUZZLE_X = 548, MUZZLE_Y = 235;
    private static final double BLAST_X = 728, BLAST_Y = 165;

    // ---------- BlueSpark palette (from the character sheet) ----------
    private static final Color ICE        = new Color(158, 222, 236);
    private static final Color ICE_LIGHT  = new Color(206, 242, 250);
    private static final Color ICE_SHADE  = new Color(105, 186, 220);
    private static final Color B_OUTLINE  = new Color(58, 112, 158);
    private static final Color BLACK      = new Color(16, 18, 30);
    private static final Color PAPER      = new Color(248, 250, 250);

    // ---------- Sherry palette ----------
    private static final Color SKIN       = new Color(172, 92, 38);
    private static final Color SKIN_LIGHT = new Color(206, 124, 50);
    private static final Color SKIN_DARK  = new Color(128, 62, 28);
    private static final Color HAIR       = new Color(58, 30, 28);
    private static final Color HAIR_RED   = new Color(120, 46, 40);
    private static final Color TURQ       = new Color(64, 194, 218);
    private static final Color TURQ_LIGHT = new Color(120, 222, 236);
    private static final Color TURQ_DARK  = new Color(24, 140, 170);
    private static final Color NAVY       = new Color(20, 28, 78);
    private static final Color NAVY_LIGHT = new Color(32, 64, 168);
    private static final Color S_OUTLINE  = new Color(22, 40, 70);
    private static final Color GOLD       = new Color(250, 196, 44);

    // ---------- Fire palette (from the sketch) ----------
    private static final Color FIRE_RED    = new Color(226, 28, 44);
    private static final Color FIRE_ORANGE = new Color(255, 120, 20);
    private static final Color FIRE_YELLOW = new Color(255, 212, 20);
    private static final Color FIRE_CORE   = new Color(255, 250, 200);

    public static void main(String[] args) throws Exception {
        // Optional: "java Aquarium --png out.png" saves an image and exits.
        if (args.length == 2 && args[0].equals("--png")) {
            BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            Aquarium a = new Aquarium();
            a.setSize(WIDTH, HEIGHT);
            a.paint(g);
            g.dispose();
            javax.imageio.ImageIO.write(img, "png", new File(args[1]));
            return;
        }

        JFrame frame = new JFrame("BlueSpark and Sherry - Splash Art");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Aquarium art = new Aquarium();
        art.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.add(art);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // =====================================================================
    //  SCENE
    // =====================================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        drawBackground(g2);

        // Sherry is in the BACK, so she is drawn first and a little smaller.
        Graphics2D s = (Graphics2D) g2.create();
        s.translate(762, 112);
        s.scale(0.86, 0.86);
        s.rotate(Math.toRadians(7), 150, 600);   // leaning away from the blast
        drawSherry(s);
        s.dispose();

        // Fire goes between the two characters.
        drawBlast(g2);

        // BlueSpark is in the FRONT.
        Graphics2D b = (Graphics2D) g2.create();
        b.translate(60, 50);
        drawBlueSpark(b);
        b.dispose();

        drawForegroundFx(g2);
        g2.dispose();
    }

    // =====================================================================
    //  BACKGROUND + EFFECTS
    // =====================================================================
    private void drawBackground(Graphics2D g) {
        g.setPaint(new GradientPaint(0, 0, new Color(35, 8, 74),
                                     WIDTH, HEIGHT, new Color(0, 18, 100)));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Big soft shapes.
        g.setColor(new Color(30, 70, 255, 55));
        g.fillOval(40, 100, 610, 560);
        g.setColor(new Color(100, 35, 255, 55));
        g.fillOval(490, 60, 560, 560);

        // Warm glow around the explosion (the fire lights the scene).
        RadialGradientPaint glow = new RadialGradientPaint(
            new Point2D.Double(BLAST_X, BLAST_Y), 430,
            new float[]{0f, 0.45f, 1f},
            new Color[]{new Color(255, 150, 40, 170), new Color(255, 70, 90, 70), new Color(255, 70, 90, 0)});
        g.setPaint(glow);
        g.fillOval((int) BLAST_X - 430, (int) BLAST_Y - 430, 860, 860);

        // Speed lines radiating from the blast.
        Random r = new Random(5);
        for (int i = 0; i < 46; i++) {
            double a = r.nextDouble() * Math.PI * 2;
            double d1 = 150 + r.nextDouble() * 120;
            double d2 = d1 + 120 + r.nextDouble() * 330;
            g.setStroke(new BasicStroke(1.5f + r.nextFloat() * 3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(255, 200, 120, 40 + r.nextInt(50)));
            g.draw(new Line2D.Double(BLAST_X + Math.cos(a) * d1, BLAST_Y + Math.sin(a) * d1,
                                     BLAST_X + Math.cos(a) * d2, BLAST_Y + Math.sin(a) * d2));
        }

        // Stars (seeded locally so they don't change on every repaint).
        Random rs = new Random(12);
        for (int i = 0; i < 100; i++) {
            int x = rs.nextInt(WIDTH), y = rs.nextInt(HEIGHT), size = 2 + rs.nextInt(5);
            if (i % 3 == 0)      g.setColor(new Color(255, 80, 190, 200));
            else if (i % 3 == 1) g.setColor(new Color(0, 225, 255, 210));
            else                 g.setColor(new Color(255, 255, 255, 190));
            g.fillOval(x, y, size, size);
        }

        // Long diagonal action streaks.
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(255, 55, 170));
        g.drawLine(20, 330, 190, 240);
        g.drawLine(300, 675, 500, 590);
        g.setColor(new Color(0, 220, 255));
        g.drawLine(20, 575, 190, 495);
        g.drawLine(760, 685, 1030, 585);
    }

    /** Fire jet from the muzzle plus the big explosion (shapes copied from the sketch). */
    private void drawBlast(Graphics2D g) {
        double ang = Math.atan2(BLAST_Y - MUZZLE_Y, BLAST_X - MUZZLE_X);
        double len = Math.hypot(BLAST_X - MUZZLE_X, BLAST_Y - MUZZLE_Y);

        // Jet: a tapered flame from the muzzle toward the explosion.
        Color[] cols = {FIRE_RED, FIRE_ORANGE, FIRE_YELLOW, FIRE_CORE};
        double[] widths = {58, 44, 30, 14};
        for (int i = 0; i < 4; i++) {
            Path2D jet = new Path2D.Double();
            jet.moveTo(0, -widths[i] * 0.35);
            jet.curveTo(len * 0.3, -widths[i] * 0.6, len * 0.7, -widths[i] * 0.9, len, -widths[i] * 0.5);
            jet.lineTo(len, widths[i] * 0.5);
            jet.curveTo(len * 0.7, widths[i] * 0.9, len * 0.3, widths[i] * 0.6, 0, widths[i] * 0.35);
            jet.closePath();
            AffineTransform t = new AffineTransform();
            t.translate(MUZZLE_X, MUZZLE_Y);
            t.rotate(ang);
            g.setColor(cols[i]);
            g.fill(t.createTransformedShape(jet));
        }

        // Explosion burst: same jagged shape drawn 4 times, shrinking.
        double[] sc = {1.0, 0.82, 0.58, 0.32};
        for (int i = 0; i < 4; i++) {
            g.setColor(cols[i]);
            g.fill(spiky(BLAST_X, BLAST_Y, 150 * sc[i], 118 * sc[i], 11, ang - 0.4, 77, 0.55));
        }

        // Dripping fire bits, like the sketch.
        drip(g, BLAST_X + 70, BLAST_Y + 105, 16, 52);
        drip(g, BLAST_X - 95, BLAST_Y + 70, 12, 38);

        // Sparks.
        Random r = new Random(9);
        g.setColor(FIRE_YELLOW);
        for (int i = 0; i < 26; i++) {
            double a = r.nextDouble() * Math.PI * 2;
            double d = 150 + r.nextDouble() * 130;
            double sz = 3 + r.nextDouble() * 6;
            g.fill(new Ellipse2D.Double(BLAST_X + Math.cos(a) * d, BLAST_Y + Math.sin(a) * d, sz, sz));
        }
    }

    private void drawForegroundFx(Graphics2D g) {
        // Yellow star from the sketch.
        drawStar(g, 62, 72, 42, 18, -12, GOLD, new Color(150, 100, 0));
        drawStar(g, 150, 30, 18, 8, 15, GOLD, new Color(150, 100, 0));
        drawStar(g, 30, 410, 16, 7, 0, new Color(0, 225, 255), new Color(0, 90, 150));

        

        // Little "!" marks by Sherry for surprise.
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Color.WHITE);
        g.drawLine(846, 118, 836, 85);
        g.drawLine(879, 108, 879, 72);
        g.drawLine(912, 118, 924, 85);
    }

    // =====================================================================
    //  BLUESPARK  (local space: ~400 wide x 610 tall, head centre x = 200)
    // =====================================================================
    private void drawBlueSpark(Graphics2D g) {
        // Slight tilt for a dynamic pose.
        g.rotate(Math.toRadians(-3), 200, 600);

        // Ground shadow.
        g.setColor(new Color(0, 0, 15, 110));
        g.fill(new Ellipse2D.Double(60, 585, 290, 34));

        // ---- Feet (black, under the boots) ----
        fillOut(g, new Ellipse2D.Double(98, 555, 100, 50), BLACK, B_OUTLINE, 2.5f);
        fillOut(g, new Ellipse2D.Double(206, 555, 100, 50), BLACK, B_OUTLINE, 2.5f);

        // ---- Thin white thighs ----
        fillOut(g, new RoundRectangle2D.Double(142, 392, 38, 62, 16, 16), PAPER, B_OUTLINE, 2.5f);
        fillOut(g, new RoundRectangle2D.Double(222, 392, 38, 62, 16, 16), PAPER, B_OUTLINE, 2.5f);

        // ---- Big blocky boots ----
        drawBoot(g, 106, 440);
        drawBoot(g, 208, 440);

        // ---- White abdomen + black V panel ----
        fillOut(g, new Rectangle2D.Double(152, 305, 98, 62), PAPER, B_OUTLINE, 2.5f);
        Path2D v = poly(158, 312, 244, 312, 201, 378);
        g.setColor(BLACK);
        g.fill(v);
        g.setColor(PAPER);
        g.setStroke(new BasicStroke(2));
        g.drawLine(188, 322, 190, 360);
        g.drawLine(201, 322, 201, 366);
        g.drawLine(214, 322, 212, 360);

        // ---- Chevron hip plate ----
        fillOut(g, poly(104, 352, 201, 372, 298, 352, 304, 398, 201, 424, 98, 398), ICE, B_OUTLINE, 2.5f);
        g.setColor(ICE_SHADE);
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(201, 372, 201, 424);

        // ---- Puffy shoulders (behind the chest) ----
        drawShoulder(g, 112, 266);
        drawShoulder(g, 290, 266);

        // ---- Cropped chest plate with a V notch ----
        fillOut(g, poly(122, 238, 170, 238, 201, 262, 232, 238, 280, 238, 280, 318, 122, 318),
                ICE, B_OUTLINE, 2.5f);
        g.setColor(ICE_LIGHT);
        g.fill(new RoundRectangle2D.Double(130, 266, 60, 14, 8, 8));

        // White collar.
        fillOut(g, poly(160, 214, 242, 214, 234, 244, 201, 262, 168, 244), PAPER, B_OUTLINE, 2.5f);

        // ---- Raised left arm: big gauntlet ----
        drawPeaceArm(g);

        // ---- Head ----
        drawBlueSparkHead(g);

        // ---- Peace-sign hand (drawn after the head so it stays visible) ----
        drawPeaceHand(g);

        // ---- Right arm cannon (front-most, pointing at the blast) ----
        Graphics2D c = (Graphics2D) g.create();
        c.translate(290, 266);
        c.rotate(Math.toRadians(-22));

        fillOut(c, new RoundRectangle2D.Double(0, -10, 70, 20, 14, 14), PAPER, B_OUTLINE, 2.5f);  // thin arm
        fillOut(c, new RoundRectangle2D.Double(46, -50, 148, 100, 40, 40), ICE, B_OUTLINE, 3f);   // gauntlet
        c.setColor(ICE_LIGHT);
        c.fill(new RoundRectangle2D.Double(58, -42, 100, 30, 24, 24));
        c.setColor(ICE_SHADE);
        c.setStroke(new BasicStroke(2.5f));
        c.drawLine(80, 8, 160, 8);
        fillOut(c, new RoundRectangle2D.Double(176, -40, 36, 80, 14, 14), ICE_SHADE, B_OUTLINE, 3f); // muzzle ring
        fillOut(c, new Ellipse2D.Double(198, -30, 24, 60), BLACK, B_OUTLINE, 2f);                    // opening
        c.setColor(FIRE_ORANGE);
        c.fill(new Ellipse2D.Double(206, -18, 12, 36));
        c.setColor(FIRE_YELLOW);
        c.fill(new Ellipse2D.Double(209, -9, 7, 18));
        c.dispose();
    }

    private void drawBoot(Graphics2D g, double x, double y) {
        fillOut(g, new RoundRectangle2D.Double(x, y + 10, 88, 128, 34, 34), ICE, B_OUTLINE, 3f);
        // Notched cuff at the top.
        fillOut(g, poly(x + 4, y + 22, x + 22, y + 4, x + 38, y + 12, x + 54, y, x + 84, y + 22, x + 84, y + 40, x + 4, y + 40),
                ICE_LIGHT, B_OUTLINE, 2.5f);
        g.setColor(ICE_SHADE);
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine((int) (x + 44), (int) (y + 44), (int) (x + 44), (int) (y + 118));
    }

    private void drawShoulder(Graphics2D g, double cx, double cy) {
        fillOut(g, new Ellipse2D.Double(cx - 34, cy - 32, 68, 64), ICE, B_OUTLINE, 3f);
        g.setColor(ICE_LIGHT);
        g.fill(new Ellipse2D.Double(cx - 22, cy - 24, 28, 18));
        // Paint "drips" under the shoulder, like the sheet.
        fillOut(g, new Ellipse2D.Double(cx - 22, cy + 22, 14, 24), ICE, B_OUTLINE, 2f);
        fillOut(g, new Ellipse2D.Double(cx + 8, cy + 22, 12, 18), ICE, B_OUTLINE, 2f);
    }

    private void drawPeaceArm(Graphics2D g) {
        // Thin arm from the shoulder up to the hand.
        Path2D arm = new Path2D.Double();
        arm.moveTo(100, 280);
        arm.lineTo(66, 262);
        strokeOut(g, arm, 18, PAPER, B_OUTLINE);

        // Big gauntlet.
        Graphics2D a = (Graphics2D) g.create();
        a.rotate(Math.toRadians(-12), 58, 250);
        fillOut(a, new RoundRectangle2D.Double(14, 206, 84, 100, 34, 34), ICE, B_OUTLINE, 3f);
        a.setColor(ICE_LIGHT);
        a.fill(new RoundRectangle2D.Double(24, 214, 50, 26, 18, 18));
        a.dispose();

    }

    private void drawPeaceHand(Graphics2D g) {
        // Black mitten hand making a peace sign next to her face.
        Graphics2D h = (Graphics2D) g.create();
        h.translate(30, 186);
        h.rotate(Math.toRadians(-8));
        // two raised fingers
        fillOut(h, new RoundRectangle2D.Double(-26, -66, 17, 66, 16, 16), BLACK, B_OUTLINE, 2.5f);
        fillOut(h, new RoundRectangle2D.Double(-4, -72, 17, 72, 16, 16), BLACK, B_OUTLINE, 2.5f);
        // palm + folded fingers
        fillOut(h, new RoundRectangle2D.Double(-34, -22, 62, 50, 24, 24), BLACK, B_OUTLINE, 2.5f);
        fillOut(h, new RoundRectangle2D.Double(10, -2, 26, 22, 12, 12), BLACK, B_OUTLINE, 2.5f);  // thumb
        h.setColor(B_OUTLINE);
        h.setStroke(new BasicStroke(2));
        h.drawLine(-14, 12, 10, 12);
        h.drawLine(-12, 22, 8, 22);
        h.dispose();
    }

    private void drawBlueSparkHead(Graphics2D g) {
        // Face (blank white oval, like the sheet).
        fillOut(g, new Ellipse2D.Double(122, 92, 160, 128), PAPER, B_OUTLINE, 3f);

        // Wide "oops!" eyes: light-blue discs with a tiny pupil.
        for (double ex : new double[]{167, 237}) {
            fillOut(g, new Ellipse2D.Double(ex - 20, 140, 40, 42), ICE_LIGHT, B_OUTLINE, 2.5f);
            g.setColor(ICE_SHADE);
            g.fill(new Ellipse2D.Double(ex - 12, 150, 24, 26));
            g.setColor(BLACK);
            g.fill(new Ellipse2D.Double(ex - 5, 158, 10, 11));
            g.setColor(Color.WHITE);
            g.fill(new Ellipse2D.Double(ex - 4, 158, 4, 4));
        }

        // Small, nervous mouth.
        fillOut(g, new Ellipse2D.Double(193, 191, 16, 15), new Color(70, 110, 150), B_OUTLINE, 2f);

        // Sweat drop.
        fillOut(g, poly(285, 118, 296, 140, 291, 150, 279, 150, 274, 140), new Color(150, 220, 255), B_OUTLINE, 2f);

        // Helmet: big dome, side flaps and a V-shaped forehead dip.
        Path2D helmet = new Path2D.Double();
        helmet.moveTo(66, 176);
        helmet.curveTo(30, 90, 105, 2, 201, 2);
        helmet.curveTo(297, 2, 372, 90, 336, 176);
        helmet.curveTo(330, 200, 296, 196, 288, 168);
        helmet.lineTo(280, 112);
        helmet.curveTo(250, 108, 222, 124, 201, 138);   // V dip
        helmet.curveTo(180, 124, 152, 108, 122, 112);
        helmet.lineTo(114, 168);
        helmet.curveTo(106, 196, 72, 200, 66, 176);
        helmet.closePath();
        fillOut(g, helmet, ICE, B_OUTLINE, 3.5f);

        // Inner-helmet shading + highlight.
        Path2D shade = new Path2D.Double();
        shade.moveTo(116, 108);
        shade.curveTo(150, 84, 178, 104, 201, 132);
        shade.curveTo(224, 104, 252, 84, 286, 108);
        shade.lineTo(286, 90);
        shade.curveTo(250, 60, 152, 60, 116, 90);
        shade.closePath();
        g.setColor(new Color(130, 200, 228));
        g.fill(shade);

        g.setColor(ICE_LIGHT);
        g.fill(new Ellipse2D.Double(112, 22, 90, 40));
        g.fill(new Ellipse2D.Double(76, 130, 26, 40));
    }

    // =====================================================================
    //  SHERRY  (local space: ~300 wide x 620 tall, head centre x = 150)
    // =====================================================================
    private void drawSherry(Graphics2D g) {
        // Shadow.
        g.setColor(new Color(0, 0, 15, 100));
        g.fill(new Ellipse2D.Double(10, 590, 280, 34));

        // ---- Hair behind everything (two big afro puffs) ----
        g.setColor(HAIR);
        g.fill(new Ellipse2D.Double(6, 14, 124, 124));
        g.fill(new Ellipse2D.Double(170, 14, 124, 124));
        g.fill(new Ellipse2D.Double(76, 2, 148, 120));
        scatterCurls(g, 68, 76, 58, 38, 1);
        scatterCurls(g, 232, 76, 58, 38, 2);
        scatterCurls(g, 150, 50, 66, 22, 3);

        // ---- Legs (navy leggings, legs kicked apart) ----
        Path2D lLeg = poly(108, 296, 152, 296, 128, 432, 76, 432);
        Path2D rLeg = poly(148, 296, 192, 296, 224, 432, 172, 432);
        fillOut(g, lLeg, NAVY, S_OUTLINE, 2.5f);
        fillOut(g, rLeg, NAVY, S_OUTLINE, 2.5f);
        g.setColor(NAVY_LIGHT);
        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(124, 316, 104, 410);
        g.drawLine(176, 316, 196, 410);

        // ---- Boots (tall turquoise greaves with blue knee joints) ----
        drawSherryBoot(g, 56, 426);
        drawSherryBoot(g, 160, 426);

        // ---- Torso ----
        Path2D lowerTorso = poly(110, 262, 190, 262, 198, 304, 102, 304);
        fillOut(g, lowerTorso, NAVY, S_OUTLINE, 2.5f);
        // Hip armour.
        fillOut(g, poly(100, 292, 200, 292, 206, 322, 150, 336, 94, 322), TURQ_DARK, S_OUTLINE, 2.5f);

        // Arms thrown up in panic: navy sleeves + big teal gauntlet fists.
        drawRaisedArm(g, 92, 208, 36, 184, 20, 128);
        drawRaisedArm(g, 208, 208, 264, 184, 280, 128);

        // Puffy shoulders.
        fillOut(g, new Ellipse2D.Double(58, 176, 68, 64), TURQ, S_OUTLINE, 2.5f);
        fillOut(g, new Ellipse2D.Double(174, 176, 68, 64), TURQ, S_OUTLINE, 2.5f);
        g.setColor(TURQ_LIGHT);
        g.fill(new Ellipse2D.Double(68, 184, 26, 16));
        g.fill(new Ellipse2D.Double(184, 184, 26, 16));

        // Chest plate + light centre panel + yellow trim.
        fillOut(g, poly(104, 188, 196, 188, 192, 264, 150, 284, 108, 264), TURQ, S_OUTLINE, 2.5f);
        g.setColor(TURQ_LIGHT);
        g.fill(poly(122, 198, 178, 198, 176, 254, 150, 268, 124, 254));
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(141, 214, 146, 262);
        g.drawLine(159, 214, 154, 262);
        fillOut(g, new RoundRectangle2D.Double(138, 190, 24, 12, 4, 4), new Color(100, 108, 112), S_OUTLINE, 1.5f);

        // Fluffy white collar.
        for (double[] c : new double[][]{{112, 178}, {134, 186}, {166, 186}, {188, 178}, {150, 190}, {124, 168}, {176, 168}}) {
            fillOut(g, new Ellipse2D.Double(c[0] - 19, c[1] - 17, 38, 34), PAPER, new Color(160, 170, 180), 1.5f);
        }

        // Neck.
        fillOut(g, new RoundRectangle2D.Double(130, 140, 40, 40, 14, 14), SKIN_DARK, SKIN_DARK, 1f);
        // Re-draw collar centre in front of the neck.
        fillOut(g, new Ellipse2D.Double(124, 168, 52, 30), PAPER, new Color(160, 170, 180), 1.5f);

        // ---- Face ----
        fillOut(g, new Ellipse2D.Double(92, 86, 16, 36), SKIN, SKIN_DARK, 2f);   // ears
        fillOut(g, new Ellipse2D.Double(192, 86, 16, 36), SKIN, SKIN_DARK, 2f);
        fillOut(g, new Ellipse2D.Double(100, 38, 100, 132), SKIN, SKIN_DARK, 2.5f);

        // Warm cheeks + freckles.
        g.setColor(SKIN_LIGHT);
        g.fill(new Ellipse2D.Double(106, 112, 26, 18));
        g.fill(new Ellipse2D.Double(168, 112, 26, 18));
        g.setColor(SKIN_DARK);
        for (int i = 0; i < 5; i++) {
            g.fill(new Ellipse2D.Double(114 + i * 5, 120 + (i % 2) * 4, 2.5, 2.5));
            g.fill(new Ellipse2D.Double(172 + i * 5, 120 + (i % 2) * 4, 2.5, 2.5));
        }

        // Thick eyebrows, raised in alarm.
        g.setColor(HAIR);
        g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new QuadCurve2D.Double(108, 78, 124, 56, 143, 66));
        g.draw(new QuadCurve2D.Double(157, 66, 176, 56, 192, 78));

        // Wide-open olive-green eyes.
        for (double ex : new double[]{125, 175}) {
            fillOut(g, new Ellipse2D.Double(ex - 17, 80, 34, 32), Color.WHITE, SKIN_DARK, 2f);
            g.setColor(new Color(132, 142, 40));
            g.fill(new Ellipse2D.Double(ex - 9, 85, 18, 22));
            g.setColor(new Color(30, 38, 20));
            g.fill(new Ellipse2D.Double(ex - 4, 91, 8, 12));
            g.setColor(Color.WHITE);
            g.fill(new Ellipse2D.Double(ex - 3, 92, 4, 4));
        }

        // Nose.
        g.setColor(SKIN_DARK);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new QuadCurve2D.Double(142, 118, 150, 126, 158, 118));
        g.fill(new Ellipse2D.Double(140, 116, 5, 5));
        g.fill(new Ellipse2D.Double(155, 116, 5, 5));

        // Open mouth with her signature gap tooth.
        fillOut(g, new Ellipse2D.Double(133, 134, 34, 28), new Color(88, 30, 34), new Color(120, 50, 40), 3f);
        g.setColor(Color.WHITE);
        g.fill(new Rectangle2D.Double(138, 136, 10, 9));
        g.fill(new Rectangle2D.Double(152, 136, 10, 9));
        g.setColor(new Color(214, 84, 96));
        g.fill(new Ellipse2D.Double(140, 150, 20, 10));

        // Forehead hair + curly tendrils framing the face.
        g.setColor(HAIR);
        g.fill(new Ellipse2D.Double(104, 30, 92, 40));
        for (double[] c : new double[][]{{98, 70}, {94, 100}, {98, 130}, {202, 70}, {206, 100}, {202, 130}}) {
            g.fill(new Ellipse2D.Double(c[0] - 9, c[1] - 9, 18, 18));
        }
        g.setColor(HAIR_RED);
        g.fill(new Ellipse2D.Double(116, 36, 22, 10));
        g.fill(new Ellipse2D.Double(92, 122, 8, 8));
        g.fill(new Ellipse2D.Double(200, 122, 8, 8));
    }

    private void drawSherryBoot(Graphics2D g, double x, double y) {
        fillOut(g, new RoundRectangle2D.Double(x, y + 22, 84, 140, 38, 38), TURQ, S_OUTLINE, 3f);
        // Foot.
        fillOut(g, new Ellipse2D.Double(x - 8, y + 148, 100, 38), TURQ_DARK, S_OUTLINE, 3f);
        // Blue knee joint.
        fillOut(g, new Ellipse2D.Double(x + 18, y, 48, 38), NAVY_LIGHT, S_OUTLINE, 3f);
        g.setColor(new Color(60, 120, 230));
        g.fill(new Ellipse2D.Double(x + 28, y + 8, 28, 20));
        // Armour seam.
        g.setColor(TURQ_DARK);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new QuadCurve2D.Double(x + 14, y + 70, x + 44, y + 52, x + 70, y + 76));
        g.setColor(TURQ_LIGHT);
        g.fill(new Ellipse2D.Double(x + 14, y + 40, 20, 40));
    }

    private void drawRaisedArm(Graphics2D g, double sx, double sy, double ex, double ey, double fx, double fy) {
        Path2D arm = new Path2D.Double();
        arm.moveTo(sx, sy);
        arm.lineTo(ex, ey);
        arm.lineTo(fx, fy + 20);
        strokeOut(g, arm, 24, NAVY, S_OUTLINE);
        // Teal gauntlet fist.
        fillOut(g, new Ellipse2D.Double(fx - 26, fy - 24, 52, 52), TURQ_DARK, S_OUTLINE, 3f);
        g.setColor(TURQ);
        g.fill(new Ellipse2D.Double(fx - 18, fy - 18, 24, 20));
        g.setColor(S_OUTLINE);
        g.setStroke(new BasicStroke(2));
        g.drawLine((int) fx - 10, (int) fy + 4, (int) fx + 14, (int) fy + 4);
    }

    /** Scatters little dark-red curls inside a hair puff so it reads as curly texture. */
    private void scatterCurls(Graphics2D g, double cx, double cy, double rx, double ry, long seed) {
        Random r = new Random(seed);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 26; i++) {
            double a = r.nextDouble() * Math.PI * 2;
            double d = Math.sqrt(r.nextDouble());
            double x = cx + Math.cos(a) * rx * d, y = cy + Math.sin(a) * ry * d;
            g.setColor(i % 2 == 0 ? HAIR_RED : new Color(30, 14, 16));
            g.draw(new Arc2D.Double(x - 7, y - 7, 14, 14, r.nextInt(360), 200, Arc2D.OPEN));
        }
    }

    // =====================================================================
    //  HELPERS
    // =====================================================================
    /** Fill a shape and give it a clean outline (like marker linework). */
    private void fillOut(Graphics2D g, Shape s, Color fill, Color line, float width) {
        g.setColor(fill);
        g.fill(s);
        g.setColor(line);
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(s);
    }

    /** Draw a thick outlined line/arm along a path. */
    private void strokeOut(Graphics2D g, Shape path, float width, Color fill, Color line) {
        g.setColor(line);
        g.fill(new BasicStroke(width + 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND).createStrokedShape(path));
        g.setColor(fill);
        g.fill(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND).createStrokedShape(path));
    }

    /** Build a polygon from x,y pairs. */
    private Path2D poly(double... p) {
        Path2D path = new Path2D.Double();
        path.moveTo(p[0], p[1]);
        for (int i = 2; i < p.length; i += 2) path.lineTo(p[i], p[i + 1]);
        path.closePath();
        return path;
    }

    /** Jagged explosion shape. Same seed = same shape, so layers nest neatly. */
    private Path2D spiky(double cx, double cy, double rx, double ry, int spikes, double rot, long seed, double inner) {
        Random r = new Random(seed);
        Path2D p = new Path2D.Double();
        for (int i = 0; i < spikes * 2; i++) {
            double a = rot + i * Math.PI / spikes;
            double f = (i % 2 == 0) ? 1.0 - 0.3 * r.nextDouble() : inner + 0.12 * r.nextDouble();
            double x = cx + Math.cos(a) * rx * f, y = cy + Math.sin(a) * ry * f;
            if (i == 0) p.moveTo(x, y); else p.lineTo(x, y);
        }
        p.closePath();
        return p;
    }

    private void drip(Graphics2D g, double x, double y, double w, double h) {
        Path2D d = new Path2D.Double();
        d.moveTo(x, y);
        d.curveTo(x + w, y + h * 0.3, x + w * 0.5, y + h * 0.8, x, y + h);
        d.curveTo(x - w * 0.5, y + h * 0.8, x - w, y + h * 0.3, x, y);
        g.setColor(FIRE_RED);
        g.fill(d);
    }

    private void drawStar(Graphics2D g, double cx, double cy, double outer, double inner,
                          double rotDeg, Color fill, Color line) {
        Path2D star = new Path2D.Double();
        for (int i = 0; i < 10; i++) {
            double a = Math.toRadians(rotDeg - 90 + i * 36);
            double r = (i % 2 == 0) ? outer : inner;
            double x = cx + Math.cos(a) * r, y = cy + Math.sin(a) * r;
            if (i == 0) star.moveTo(x, y); else star.lineTo(x, y);
        }
        star.closePath();
        fillOut(g, star, fill, line, 3f);
    }
}