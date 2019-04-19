package ru.nsu.fit.g16203.razumov.graphics;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Vector;

public class Absorption extends JPanel {
    private boolean drawSelf = false;
    Vector<Pair<Integer, Double>> vertices;

    public Absorption() {
        this.setPreferredSize(new Dimension(500, 110));
        repaint();
    }

    public void setVertices(Vector<Pair<Integer, Double>> vertices) {
        this.vertices = vertices;
    }

    public void drawSelf() {
        drawSelf = true;
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
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, null, 0));
            g2d.setPaint(Color.BLACK);

            Iterator<Pair<Integer, Double>> iterator = vertices.iterator();
            Pair<Integer, Double> prevVertex = iterator.next();
            while (iterator.hasNext()) {
                Pair<Integer, Double> nextValue = iterator.next();
                g2d.drawLine(prevVertex.getKey() * 5, 110 - (int) (prevVertex.getValue() * 100), nextValue.getKey() * 5, 110 - (int) (nextValue.getValue() * 100));
                prevVertex = nextValue;
            }
        }
        //gets rid of the cop
        g2d.dispose();
    }
}
