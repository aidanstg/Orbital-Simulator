import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.*;

public class ParticlePath {

    Set<Particle> particles;
    Set<Integer[]> path;
    boolean infinitePaths;
    double pathDurationInTicks;

    ParticlePath(Set<Particle> particles, boolean infinitePaths, double pathDurationInTicks) {
        this.particles = particles;
        path = new HashSet<>();
        this.infinitePaths = infinitePaths;
        this.pathDurationInTicks = pathDurationInTicks;
    }

    public void drawPath(Graphics g) {
        for(Particle particle : particles) {
            Integer[] position = {particle.x, particle.y, 0};
            path.add(position);
        }
        Iterator<Integer[]> iterator = path.iterator();
        while(iterator.hasNext()) {
            Integer[] position = iterator.next();
            if(position[2] >= pathDurationInTicks && !infinitePaths) {
                iterator.remove();
            } else {
                g.setColor(Color.RED);
                g.fillRect((int) Math.round(position[0]), (int) Math.round(position[1]), 5, 5);
                
            }
            position[2] += 1;
        }
    }
}
