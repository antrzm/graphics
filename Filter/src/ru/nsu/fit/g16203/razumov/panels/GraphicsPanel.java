package ru.nsu.fit.g16203.razumov.panels;

import javafx.util.Pair;
import ru.nsu.fit.g16203.razumov.graphics.Absorption;
import ru.nsu.fit.g16203.razumov.graphics.Emission;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class GraphicsPanel extends JPanel {
    private Absorption absorption;
    private Emission emission;

    public GraphicsPanel() {
        absorption = new Absorption();
        emission = new Emission();

        this.setLayout(new GridLayout(1, 2));
        this.add(absorption);
        this.add(emission);
        //just draw axes
    }

    public void setAbsorptionVertices(Vector<Pair<Integer, Double>> vertices) {
        absorption.setVertices(vertices);
    }

    public void setEmissionVertices(Vector<Pair<Integer, Color>> vertices) {
        emission.setVertices(vertices);
    }

    public void drawGraphics() {
        emission.drawSelf();
        absorption.drawSelf();
    }
}
