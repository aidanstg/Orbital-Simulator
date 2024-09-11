import javax.swing.JFrame;

import java.awt.Color;

public class SimFrame extends JFrame {

    SimPanel panel;

    SimFrame() {
        panel = new SimPanel();
        this.add(panel);
        this.setTitle("Orbital Simulator");
        this.setResizable(true);
        this.setBackground(Color.BLACK);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        this.setVisible(true);
    }
}
