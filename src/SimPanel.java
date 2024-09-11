import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JPanel;

public class SimPanel extends JPanel implements Runnable {
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    long deltaTime;
    
    static final int GAME_WIDTH = 2500;
    static final int GAME_HEIGHT = 1200;
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);

    BigDecimal tick = new BigDecimal(0);

    BigDecimal metersPerPixel = new BigDecimal("2000000");
    BigDecimal timeMultiplier = new BigDecimal("275000");
    double amountOfFrames = 120;
    double amountOfTicks = 12000;
    boolean infinitePaths = true;
    double pathDurationInTicks = 300;

    Set<Particle> particles;
    Set<Photon> photons;
    ParticlePath path;
    PhotonPath photonPath;


    SimPanel() {
        particles = new HashSet<>();
        photons = new HashSet<>();

        newParticle("earth", metersPerPixel.multiply(new BigDecimal(GAME_WIDTH / 2.0)), metersPerPixel.multiply(new BigDecimal(GAME_HEIGHT / 2.0)), new BigDecimal(-12.7135800402), new BigDecimal(0.0), new BigDecimal(5.972E24), new BigDecimal(6378000.0), metersPerPixel);
        newParticle("moon", metersPerPixel.multiply(new BigDecimal(GAME_WIDTH / 2.0)), metersPerPixel.multiply(new BigDecimal(GAME_HEIGHT / 2.0)).add(new BigDecimal(384400000)), new BigDecimal(1033), new BigDecimal(0.0), new BigDecimal(7.35E22), new BigDecimal(1737000), metersPerPixel);
        //newParticle("moon", metersPerPixel.multiply(new BigDecimal(GAME_WIDTH / 2.0)), metersPerPixel.multiply(new BigDecimal(GAME_HEIGHT / 2.0)).add(new BigDecimal(-384400000)), new BigDecimal(-1033), new BigDecimal(0.0), new BigDecimal(7.35E22), new BigDecimal(6371000.0), metersPerPixel);
        newParticle("iss", metersPerPixel.multiply(new BigDecimal(GAME_WIDTH / 2.0)), metersPerPixel.multiply(new BigDecimal(GAME_HEIGHT / 2.0)).add(new BigDecimal(415000 + 6378000.0)), new BigDecimal(7660 - 12.71358004020), new BigDecimal(0.0), new BigDecimal(400000), new BigDecimal(1), metersPerPixel);
        
        // newParticle("Black Hole", metersPerPixel.multiply(new BigDecimal(GAME_WIDTH / 2.0)), metersPerPixel.multiply(new BigDecimal(GAME_HEIGHT / 2.0)), new BigDecimal(0), new BigDecimal(0.0), new BigDecimal(8.54E36), new BigDecimal(1), metersPerPixel);

        // newPhoton("photon1", new BigDecimal(600), new BigDecimal(GAME_HEIGHT / 2.0 + 1), new BigDecimal(1),  new BigDecimal(0));


        this.path = new ParticlePath(particles, infinitePaths, pathDurationInTicks);
        this.photonPath = new PhotonPath(photons);

        this.setFocusable(true);
        this.setPreferredSize(SCREEN_SIZE);
        this.setBackground(Color.BLACK);

        gameThread = new Thread(this);
        gameThread.start(); 
    }

    public void newParticle(String name, BigDecimal xPosition, BigDecimal yPosition, BigDecimal xVelocity, BigDecimal yVelocity, BigDecimal mass, BigDecimal radius, BigDecimal metersPerPixel) {
        Particle p = new Particle(name, xPosition, yPosition, xVelocity, yVelocity, mass, radius, metersPerPixel, timeMultiplier);
        particles.add(p);
    }

    public void newPhoton(String name, BigDecimal xPosition, BigDecimal yPosition, BigDecimal xVelocity, BigDecimal yVelocity) {
        Photon temp = new Photon(name, xPosition, yPosition, xVelocity, yVelocity, new BigDecimal("99999999999999999999"));
        BigDecimal targetXPos = new BigDecimal(0);
        BigDecimal targetYPos = new BigDecimal(0);
        for(Particle particle : particles) {
            targetXPos = particle.xPosition;
            targetYPos = particle.yPosition;
        }
        BigDecimal distance = distance(temp.xPosition.multiply(metersPerPixel), targetXPos, temp.yPosition.multiply(metersPerPixel), targetYPos);
        BigDecimal previousDistance = new BigDecimal(1E100);
        BigDecimal numberOfFrames = new BigDecimal(1);
        //System.out.println(distance.compareTo(previousDistance) < 0);
        while(distance.compareTo(previousDistance) <= 0) {
            temp.move();
            
            //System.out.println(distance);

            previousDistance = distance;
            distance = distance(temp.xPosition.multiply(metersPerPixel), targetXPos, temp.yPosition.multiply(metersPerPixel), targetYPos);
            numberOfFrames = numberOfFrames.add(new BigDecimal(1));
            
            System.out.println(numberOfFrames);
        }
        Photon p = new Photon(name, xPosition, yPosition, xVelocity, yVelocity, numberOfFrames);
        photons.add(p);
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        path.drawPath(graphics);
        photonPath.drawPath(graphics);
        g.drawImage(image, 0, 0, this);
    }

    public void draw(Graphics g) {
        for(Particle particle : particles) {
            particle.draw(g);
        }

        for(Photon photon : photons) {
            photon.draw(g);
        }
    }

    public void move() {
        for(Particle particle : particles) {
            particle.move();
        }

        for(Photon photon : photons) {
            photon.move();
            //System.out.println(photon.xVelocity);
        }
    }

    public void physics() {
        double gravConstant = 6.67430E-11;
        for(Particle particle : particles) {
            for(Particle particle2 : particles) {
                BigDecimal distance = distance(particle.xPosition, particle2.xPosition, particle.yPosition, particle2.yPosition);
                BigDecimal newXAcceleration;
                BigDecimal newYAcceleration;
                        
                if(!particle.name.equals(particle2.name)) {
                    BigDecimal gravitationalFormula;

                    if(distance.subtract(particle2.radius.add(particle.radius)).compareTo(new BigDecimal(0)) < 0) {
                        gravitationalFormula = new BigDecimal(gravConstant).multiply(distance).multiply(new BigDecimal((4.0 / 3.0) * Math.PI)).multiply(particle2.mass.divide(new BigDecimal((4 / 3) * Math.PI).multiply(particle2.radius.pow(3)), 50, RoundingMode.HALF_UP));
                    } else {
                        gravitationalFormula = new BigDecimal(gravConstant).multiply(particle2.mass.divide(distance.pow(2), 50, RoundingMode.HALF_UP));
                    }
                    // System.out.println(particle.name + "'s acceleraton: " + gravitationalFormula);
                    // System.out.println(distance);
                    BigDecimal xDifference = particle2.xPosition.subtract(particle.xPosition);
                    double angle;
                    if(xDifference.compareTo(new BigDecimal(0)) == 0) {
                        angle = Math.PI / 2;
                    } else {
                        angle = Math.atan(particle2.yPosition.subtract(particle.yPosition).multiply(new BigDecimal(-1)).divide(xDifference, 50, RoundingMode.HALF_UP).doubleValue());
                    }
                    

                    BigDecimal xProportion = gravitationalFormula.multiply(new BigDecimal(Math.cos(angle)));
                    BigDecimal yProportion = gravitationalFormula.multiply(new BigDecimal(Math.sin(angle))).abs();

                    //x calculation
                    BigDecimal specPositionDifference = particle.xPosition.subtract(particle2.xPosition);
                    BigDecimal specPositionDifferenceEquation;

                    if(specPositionDifference.abs().compareTo(new BigDecimal(0)) == 0) {
                        specPositionDifferenceEquation = new BigDecimal(0);
                    } else {
                        specPositionDifferenceEquation  = specPositionDifference.divide(specPositionDifference.abs(), 50, RoundingMode.HALF_UP);
                    }

                    newXAcceleration = specPositionDifferenceEquation.multiply(new BigDecimal(-1)).multiply(xProportion);

                    //y calculation
                    specPositionDifference = particle.yPosition.subtract(particle2.yPosition);

                    if(specPositionDifference.abs().compareTo(new BigDecimal(0)) == 0) {
                        specPositionDifferenceEquation = new BigDecimal(0);
                    } else {
                        specPositionDifferenceEquation  = specPositionDifference.divide(specPositionDifference.abs(), 50, RoundingMode.HALF_UP);
                    }

                    newYAcceleration = specPositionDifferenceEquation.multiply(new BigDecimal(-1)).multiply(yProportion);

                    //System.out.println(newYAcceleration);
                    
                    
                } else {
                    newXAcceleration = new BigDecimal(0);
                    newYAcceleration = new BigDecimal(0);
                }
                particle.applyXAcceleration(newXAcceleration);
                particle.applyYAcceleration(newYAcceleration);

            }
        }
    }

    public void photonicLensing() {
        double gravConstant = 6.67430E-11;
        BigDecimal speedOfLight = new BigDecimal(299792458);

        for(Photon photon : photons) {
            for(Particle particle : particles) {
                //System.out.println(photon.frameAtClosestPoint + "   " + frame);
                if(tick.compareTo(photon.tickAtClosestPoint) == 0) {
                    BigDecimal distance = distance(photon.xPosition.multiply(metersPerPixel), particle.xPosition, photon.yPosition.multiply(metersPerPixel), particle.yPosition);
                    Double changeInAngle = new BigDecimal(4).multiply(new BigDecimal(gravConstant)).multiply(particle.mass).divide(speedOfLight.pow(2).multiply(distance.pow(2)), 50, RoundingMode.HALF_UP).doubleValue();
                    //System.out.println(changeInAngle);
                    BigDecimal xProportion = photon.xVelocity.multiply(new BigDecimal(Math.cos(changeInAngle)));
                    BigDecimal yProportion = photon.xVelocity.multiply(new BigDecimal(Math.sin(changeInAngle))).abs();
                    photon.xVelocity = xProportion;
                    photon.yVelocity = yProportion;
                }
            }
        }

    }

    private BigDecimal distance(BigDecimal xPosition1, BigDecimal xPosition2, BigDecimal yPosition1, BigDecimal yPosition2) {
        return xPosition2.subtract(xPosition1).pow(2).add(yPosition2.subtract(yPosition1).pow(2)).sqrt(new MathContext(40));
    }


    @Override
    public void run() {
        long lastTime = System.nanoTime();

        double nsPerFrame = 1000000000 / amountOfFrames;
        double nsPerTick = 1000000000 / amountOfTicks;

        double frameCounter = 0;
        double tickCounter = 0;
        
        while(true) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime);

            frameCounter += deltaTime / nsPerFrame;
            tickCounter += deltaTime / nsPerTick;
            
            lastTime = now;

            if(tickCounter >= 1) {
                tick = tick.add(new BigDecimal(1));
                physics();
                photonicLensing();
                move();
                tickCounter--;
            }
            if(frameCounter >= 1) {
                repaint();
                frameCounter--;
            }
        }
    }
}
