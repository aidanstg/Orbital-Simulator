import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.math.BigDecimal;


public class Photon extends Rectangle{
    final String name;
    BigDecimal xPosition, yPosition, xVelocity, yVelocity, radius;
    BigDecimal tickAtClosestPoint;

    Photon(String name, BigDecimal xPosition, BigDecimal yPosition, BigDecimal xVelocity, BigDecimal yVelocity, BigDecimal tickAtClosestPoint) {
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.tickAtClosestPoint = tickAtClosestPoint;
    }

    public void move() {
        xPosition = xPosition.add(xVelocity);
        //System.out.println(xPosition.add(xVelocity));
        x = (int) xPosition.doubleValue();

        yPosition = yPosition.add(yVelocity);
        y = (int) yPosition.doubleValue();

    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        int dimensions = 100;
        g.fillOval(x - (dimensions / 2), y - (dimensions / 2), dimensions, dimensions);
    }

    @Override
    public int hashCode() {
        int t = (int) System.nanoTime();
        return t;
    }
}
