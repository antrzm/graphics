package ru.nsu.fit.g16203.razumov.panels;

import ru.nsu.fit.g16203.razumov.filters.Filter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ZonesPanel extends JPanel {
    private static final int SIZE = 350;

    private ImagePanel zoneA, zoneB, zoneC;
    private JPanel borderA, borderB, borderC;

    public ZonesPanel() {
        zoneA = new ImagePanel();
        zoneB = new ImagePanel();
        zoneC = new ImagePanel();

        borderA = new JPanel(new GridLayout(1, 1));
        borderB = new JPanel(new GridLayout(1, 1));
        borderC = new JPanel(new GridLayout(1, 1));

        borderA.setBorder((BorderFactory.createDashedBorder
                (Color.DARK_GRAY, 1, 5, 2, true)));
        borderB.setBorder((BorderFactory.createDashedBorder
                (Color.DARK_GRAY, 1, 5, 2, true)));
        borderC.setBorder((BorderFactory.createDashedBorder
                (Color.DARK_GRAY, 1, 5, 2, true)));

        borderA.setPreferredSize(new Dimension(SIZE, SIZE));
        borderB.setPreferredSize(new Dimension(SIZE, SIZE));
        borderC.setPreferredSize(new Dimension(SIZE, SIZE));

        borderA.add(zoneA);
        borderB.add(zoneB);
        borderC.add(zoneC);

        add(borderA);
        add(borderB);
        add(borderC);

        setVisible(true);
    }

    public void loadPicture(File file) {
        zoneA.loadImage(file);
        if (zoneB.getImage() != null) zoneB.setImage(null);

        if (zoneC.getImage() != null) zoneC.setImage(null);
    }

    public void clearZones() {
        if (zoneA.getImage() != null) zoneA.setImage(null);

        if (zoneB.getImage() != null) zoneB.setImage(null);

        if (zoneC.getImage() != null) zoneC.setImage(null);
    }

    //now it just inverts full image from A and puts it in C without Select
    //TODO: make it go through select and B-zone
    public void applyFilter(Filter filter) {
        BufferedImage image = filter.Apply(zoneA.getImage());
        zoneC.setImage(image);
    }

    public void copyBC(){
        zoneC.setImage(zoneB.getImage());
    }
    public void copyCB(){
        zoneB.setImage(zoneC.getImage());
    }

    public ImagePanel getZoneA() {
        return zoneA;
    }

    public ImagePanel getZoneB() {
        return zoneB;
    }

    public ImagePanel getZoneC() {
        return zoneC;
    }
}
