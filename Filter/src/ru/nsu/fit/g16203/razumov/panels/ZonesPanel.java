package ru.nsu.fit.g16203.razumov.panels;

import ru.nsu.fit.g16203.razumov.filters.Filter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ZonesPanel extends JPanel {
    private static final int SIZE = 350;

    private ImagePanel zoneA, zoneB, zoneC;
    BufferedImage initialImg;
    private JPanel selectZone;

    public ZonesPanel() {
        zoneA = new ImagePanel();
        zoneB = new ImagePanel();
        zoneC = new ImagePanel();

        zoneA.setParentPanel(this);

        selectZone = new JPanel();

        selectZone.setOpaque(false);
        this.selectZone.setVisible(false);

        zoneA.setBorder((BorderFactory.createDashedBorder
                (Color.DARK_GRAY, 1, 5, 2, true)));
        zoneB.setBorder((BorderFactory.createDashedBorder
                (Color.DARK_GRAY, 1, 5, 2, true)));
        zoneC.setBorder((BorderFactory.createDashedBorder
                (Color.DARK_GRAY, 1, 5, 2, true)));
        selectZone.setBorder((BorderFactory.createDashedBorder
                (Color.DARK_GRAY, 1, 3, 1, true)));

        zoneA.setPreferredSize(new Dimension(SIZE, SIZE));
        zoneB.setPreferredSize(new Dimension(SIZE, SIZE));
        zoneC.setPreferredSize(new Dimension(SIZE, SIZE));

        add(zoneA);
        add(zoneB);
        add(zoneC);


        setVisible(true);
    }


    public void loadPicture(File file) throws IOException {
        this.initialImg = ImageIO.read(file);
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
        try {
            BufferedImage image = filter.Apply(zoneB.getImage());
            zoneC.setImage(image);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this, "Firstly, put an image in zone B using Select", "No image", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void copyBC() {
        zoneC.setImage(zoneB.getImage());
    }

    public void copyCB() {
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
