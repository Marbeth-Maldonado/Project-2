import edu.cymbal.graphics.CanvasAPI;
import edu.cymbal.graphics.Graphics;
import edu.cymbal.graphics.PainterAPI;

public class Aquarium {
    public static void main(String[] args) {
        CanvasAPI canvas = Graphics.createCanvas(800, 600, "My First Drawing");

        PainterAPI painter = canvas.getPainter();
        painter.setSpeed(10); // Lower number means faster, zero means as fast as possible.
        painter.forward(100);
    }
}