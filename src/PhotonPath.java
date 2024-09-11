import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.*;

public class PhotonPath {

    Set<Photon> particles;
    Set<Integer[]> path;

    PhotonPath(Set<Photon> particles) {
        this.particles = particles;
        path = new HashSet<>();
    }

    public void drawPath(Graphics g) {
        for(Photon particle : particles) {
            Integer[] position = {particle.x, particle.y, 0};
            path.add(position);
        }
        Iterator<Integer[]> iterator = path.iterator();
        while(iterator.hasNext()) {
            Integer[] position = iterator.next();
            if(position[2] >= 1200 && false) {
                iterator.remove();
            } else {
                g.setColor(Color.RED);
                g.fillRect((int) Math.round(position[0]), (int) Math.round(position[1]), 1, 1);
                
            }
            position[2] += 1;
        }
    }
}
