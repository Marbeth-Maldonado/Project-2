import java.awt.Color;

import edu.cymbal.graphics.CanvasAPI;
import edu.cymbal.graphics.Graphics;
import edu.cymbal.graphics.PainterAPI;

public class Aquarium {
    public static void main(String[] args) {
        CanvasAPI canvas = Graphics.createCanvas(800, 600, "My Robot animation");

        PainterAPI painter = canvas.getPainter();
        painter.setSpeed(10); // Lower number means faster, zero means as fast as possible.
        painter.forward(100);
        //Draw the starting idle position of the robot
        drawRobot(painter);
    }

    private static void drawRobot(PainterAPI painter) {
        // Start drawing the robot body
        painter.setPenColor(Color.CYAN);
        painter.moveTo(-40, -30);
        painter.lowerPen();
        //Draw the body outline
        for (int i = 0; i < 2; i++) {
            painter.forward(80);
            painter.turnRight(90);
            painter.forward(60);
            painter.turnRight(90);
        }
        // lift the pen to move to the next part of the robot
        painter.liftPen();
    }
}