package ru.nsu.fit.g16203.razumov.panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private static final int SIZE = 350;
    private BufferedImage image;
    private int selectWidth, selectHeight;

    private boolean isSelectShown;
    private Point select1, select2;

    private ZonesPanel parentPanel;

    ImagePanel() {
        isSelectShown = false;
        setSize(SIZE, SIZE);
        setVisible(true);
        image = null;

        select1 = new Point();
        select2 = new Point();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isSelectShown)
                    getSelectCoords(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isSelectShown)
                    getSelectCoords(e);
                    repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isSelectShown) {
                    repaint();
                }
            }
        });
    }

    void loadImage(File file) throws IOException {
        setImage(ImageIO.read(file));
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
                selectWidth = width;
                selectHeight = height;
                g.drawImage(image, 0, 0, this);
            } else {                                      //2b
                if (height < width) {
                    selectHeight = SIZE * SIZE / width;
                    selectWidth = SIZE * SIZE / width;
                } else {
                    selectHeight = SIZE * SIZE / height;
                    selectWidth = SIZE * SIZE / height;
                }

                double widthToHeight = (double) (width) / height;

                if (widthToHeight >= 1) {
                    g.drawImage(image, 0, 0, SIZE, (int) (SIZE / widthToHeight), this);
                } else {
                    g.drawImage(image, 0, 0, (int) (SIZE * widthToHeight), SIZE, this);
                }
                if (isSelectShown) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setXORMode(Color.WHITE);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{5f, 5f}, 0f));
                    g2.drawRect(select1.x, select1.y, select2.x - select1.x, select2.y - select1.y);
                }

            }
        } else {
            g.setColor(this.getBackground());
            g.fillRect(0, 0, SIZE, SIZE);
        }
    }

    private void getSelectCoords(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (image != null) {
            select1.x = (x - selectWidth / 2) < 0 ? 0 : (x - selectWidth / 2);
            select1.y = (y - selectHeight / 2) < 0 ? 0 : (y - selectHeight / 2);

            select2.x = (x + selectWidth / 2) < image.getWidth() ? (x + selectWidth / 2) : image.getWidth();
            select2.y = (y + selectHeight / 2) < image.getHeight() ? (y + selectHeight / 2) : image.getHeight();

            parentPanel.getZoneB().setImage(image.getSubimage(select1.x, select1.y, SIZE, SIZE));
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.isSelectShown = false;
        repaint();
    }

    public void setSelectShown(boolean selectShown) {
        isSelectShown = selectShown;
    }

    public boolean isSelectShown() {
        return isSelectShown;
    }

    public void setParentPanel(ZonesPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    public int getSelectWidth() {
        return selectWidth;
    }

    public int getSelectHeight() {
        return selectHeight;
    }
}
