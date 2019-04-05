package ru.nsu.fit.g16203.razumov.graphics;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Vector;

public class Emission extends JPanel {
    private boolean drawSelf = false;
    private Vector<Pair<Integer, Color>> vertices;

    public Emission() {
        this.setPreferredSize(new Dimension(500, 110));
        repaint();
    }

    public void setVertices(Vector<Pair<Integer, Color>> vertices) {
        this.vertices = vertices;
    }

    public void drawSelf() {
        this.drawSelf = true;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        g2d.setPaint(Color.GRAY);
        g2d.drawLine(0, 110, 0, 10);
        g2d.drawLine(0, 110, 500, 110);

        if (vertices != null && drawSelf) {
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, null, 0));
            Iterator<Pair<Integer, Color>> iterator = vertices.iterator();
            Pair<Integer, Color> prev = iterator.next();
            while (iterator.hasNext()) {
                Pair<Integer, Color> next = iterator.next();
                int x1 = prev.getKey(), x2 = next.getKey();
                Color color1 = prev.getValue(), color2 = next.getValue();
                g2d.setPaint(Color.RED);
                g2d.drawLine(x1 * 5, 110 - (100 * color1.getRed() / 255), x2 * 5, 110 - (100 * color2.getRed() / 255));
                g2d.setPaint(Color.GREEN);
                g2d.drawLine(x1 * 5 + 2, 110 - (100 * color1.getGreen() / 255) - 2, x2 * 5 + 4, 110 - (100 * color2.getGreen() / 255) - 2);
                g2d.setPaint(Color.BLUE);
                g2d.drawLine(x1 * 5 + 4, 110 - (100 * color1.getBlue() / 255) - 4, x2 * 5 + 4, 110 - (100 * color2.getBlue() / 255) - 4);
                prev = next;
            }
        }

        g2d.dispose();
    }
}
