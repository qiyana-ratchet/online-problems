package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class Display {
    private JFrame frame;
    private Canvas canvas;

    public Display(String title, int width, int height) {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setFocusable(false);

        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Graphics getGraphics() {
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(3);
            return null;
        }
        return bs.getDrawGraphics();
    }

    public void clear() {
        // Clear the canvas before rendering
        // Implement as needed based on your rendering strategy
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void update() {
        // Update the canvas and display
        // Implement as needed based on your rendering strategy
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }
        bs.show();
    }

    // Implement additional methods for handling game window and rendering
}
