package ru.nsu.fit.g16203.razumov.panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private static final int SIZE = 350;
    private BufferedImage image, border;
    private int zoneWidth, zoneHight;

    ImagePanel() {
        setSize(SIZE, SIZE);
        setVisible(true);
        image = null;
    }

    public void loadImage(File file) {
        try {
            setImage(ImageIO.read(file));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveImage(File file) {
        try {
            if (file.getName().endsWith(".bmp"))
                ImageIO.write(image, "bmp", file);
            else ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image != null) {
            super.paintComponent(g);
            int width = image.getWidth(), height = image.getHeight();

            if (width <= SIZE && height <= SIZE) {      //2a
                zoneWidth = width;
                zoneHight = height;
                g.drawImage(image, 0, 0, this);
            } else {                                      //2b
                if (height < width) {
                    zoneHight = SIZE * SIZE / width;
                    zoneWidth = SIZE * SIZE / width;
                } else {
                    zoneHight = SIZE * SIZE / height;
                    zoneWidth = SIZE * SIZE / height;
                }

                double widthToHeight = (double) (width) / height;

                if (widthToHeight >= 1) {
                    g.drawImage(image, 0, 0, SIZE, (int) (SIZE / widthToHeight), this);
                } else {
                    g.drawImage(image, 0, 0, (int) (SIZE * widthToHeight), SIZE, this);
                }
            }
        } else {
            g.setColor(this.getBackground());
            g.fillRect(0, 0, SIZE, SIZE);
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }
}
