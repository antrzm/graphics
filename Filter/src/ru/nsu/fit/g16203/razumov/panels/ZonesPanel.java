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
            JOptionPane.showMessageDialog(this, "Open an image first", "No image", JOptionPane.WARNING_MESSAGE);
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

    Point[] getSelectCoords(int x, int y) {
        int width = initialImg.getWidth(), height = initialImg.getHeight();
        int origX = (int) ((double) x * (double) width / (double) zoneA.getSelectWidth());
        int origY = (int) ((double) y * (double) height / (double) zoneA.getSelectHeight());

        double ratio = (double) (zoneA.getImage().getWidth() / width);

        Point[] points = new Point[2];

        if (width <= SIZE && height <= SIZE) {
            points[0] = new Point(0, 0);
            points[1] = new Point(width, height);
        } else if (width > SIZE && height < SIZE) {
            int minusHalf = origX - SIZE / 2;
            int plusHalf = origX + SIZE / 2;
            if (minusHalf >= 0 && plusHalf < width) {
                points[0] = new Point(minusHalf, 0);
                points[0] = new Point(plusHalf, height);
            } else if (minusHalf < 0) {
                points[0] = new Point(0, 0);
                points[1] = new Point(SIZE, height);
            } else {
                points[0] = new Point(width - SIZE, 0);
                points[1] = new Point(width, height);
            }
        } else if (width < SIZE) {
            int minusHalf = origY - SIZE / 2;
            int plusHalf = origY + SIZE / 2;
            if (minusHalf >= 0 && plusHalf < height) {
                points[0] = new Point(0, minusHalf);
                points[0] = new Point(width, plusHalf);
            } else if (minusHalf < 0) {
                points[0] = new Point(0, 0);
                points[1] = new Point(width, SIZE);
            } else {
                points[0] = new Point(0, height - SIZE);
                points[1] = new Point(width, height);
            }
        } else {
            int minusHalfX = origX - SIZE / 2;
            int plusHalfX = origX + SIZE / 2;
            int minusHalfY = origY - SIZE / 2;
            int plusHalfY = origY + SIZE / 2;

            if (minusHalfX >= 0 && plusHalfX < width && minusHalfY >= 0 && plusHalfY < height) {
                points[0] = new Point(minusHalfX, minusHalfY);
                points[1] = new Point(plusHalfX, plusHalfY);
            } else if (minusHalfX < 0) {
                if (minusHalfY >= 0 && plusHalfY < height) {
                    points[0] = new Point(0, minusHalfY);
                    points[1] = new Point(SIZE - 1, plusHalfY);
                } else if (minusHalfY < 0) {
                    points[0] = new Point(0, 0);
                    points[1] = new Point(SIZE - 1, SIZE - 1);
                } else {
                    points[0] = new Point(0, height - SIZE);
                    points[1] = new Point(SIZE - 1, height - 1);
                }
            } else if (plusHalfX >= width) {
                if (minusHalfY >= 0 && plusHalfY < height) {
                    points[0] = new Point(width - SIZE - 1, minusHalfY);
                    points[1] = new Point(width - 1, plusHalfY);
                } else if (minusHalfY < 0) {
                    points[0] = new Point(width - SIZE - 1, 0);
                    points[1] = new Point(width - 1, SIZE);
                } else {
                    points[0] = new Point(width - SIZE - 1, height - SIZE - 1);
                    points[1] = new Point(width - 1, height - 1);
                }
            } else if (minusHalfY < 0) {
                points[0] = new Point(minusHalfX, 0);
                points[1] = new Point(plusHalfX, SIZE);
            } else {
                points[0] = new Point(minusHalfX, height - SIZE - 1);
                points[1] = new Point(plusHalfX, height - 1);
            }
        }
        zoneB.setImage(initialImg.getSubimage(points[0].x, points[0].y, points[1].x - points[0].x, points[1].y - points[0].y));

        points[0].x = (int) (points[0].getX() * ratio);
        points[0].y = (int) (points[0].getY() * ratio);
        points[1].x = (int) (points[1].getX() * ratio);
        points[1].y = (int) (points[1].getY() * ratio);
        repaint();
        return points;
    }
}
