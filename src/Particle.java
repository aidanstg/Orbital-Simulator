import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Particle extends Rectangle {
    final String name;
    BigDecimal xPosition, yPosition, xVelocity, yVelocity, mass, radius, metersPerPixel, timeMultiplier;

    Particle(String name, BigDecimal xPosition, BigDecimal yPosition, BigDecimal xVelocity, BigDecimal yVelocity, BigDecimal mass, BigDecimal radius, BigDecimal metersPerPixel, BigDecimal timeMultiplier) {
        this.name = name;
        this.xPosition = xPosition;
        x = (int) xPosition.divide(metersPerPixel, 50, RoundingMode.HALF_UP).doubleValue();
        this.yPosition = yPosition;
        y = (int) yPosition.divide(metersPerPixel, 50, RoundingMode.HALF_UP).doubleValue();
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.mass = mass;
        this.radius = radius;
        this.metersPerPixel = metersPerPixel;
        this.timeMultiplier = timeMultiplier;

    }

    public void move() {
        xPosition = xPosition.add(xVelocity.multiply(timeMultiplier));
        
        x = (int) xPosition.divide(metersPerPixel, 50, RoundingMode.HALF_UP).doubleValue();
        
        //System.err.println("Velocity: " + yVelocity + ", timeMultiplier: " + timeMultiplier + ", Position: " + yPosition);
        
        yPosition = yPosition.add(yVelocity.multiply(timeMultiplier));
        //System.out.println(yPosition);

        y = (int) yPosition.divide(metersPerPixel, 50, RoundingMode.HALF_UP).doubleValue();
    }
    
    public void applyXAcceleration(BigDecimal xAcceleration) {
        this.xVelocity = xVelocity.add(xAcceleration.multiply(timeMultiplier));
    }
    
    public void applyYAcceleration(BigDecimal yAcceleration) {
        //System.out.println(yAcceleration);
        this.yVelocity = yVelocity.add(yAcceleration.multiply(timeMultiplier));
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        int dimensions = radius.multiply(new BigDecimal(2)).divide(metersPerPixel, 50, RoundingMode.HALF_UP).intValue();
        if(dimensions <= 1) {
            dimensions = 1;
        }
        g.fillOval(x - (dimensions / 2), y - (dimensions / 2), dimensions, dimensions);
    }

    @Override
    public int hashCode() {
        int t = (int) System.nanoTime();
        return t;
    }
}
