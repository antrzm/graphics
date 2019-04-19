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
    private int scaledWidth, scaledHeight;

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
                scaledHeight = height;
                scaledWidth = width;
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
                    scaledHeight = (int) (SIZE / widthToHeight);
                    scaledWidth = SIZE;
                    g.drawImage(image, 0, 0, SIZE, (int) (SIZE / widthToHeight), this);
                } else {
                    scaledHeight = SIZE;
                    scaledWidth = (int) (SIZE * widthToHeight);
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
            select1.x = (x - selectWidth / 2) > 0 ? (x - selectWidth / 2) : 0;
            select2.x = (int) (x + Math.ceil((double) selectWidth / 2)) < scaledWidth ? (int) (x + Math.ceil((double) selectWidth / 2)) : scaledWidth;
            select1.y = (y - selectHeight / 2) < 0 ? 0 : (y - selectHeight / 2);
            select2.y = (int) (y + Math.ceil((double) selectHeight / 2)) < scaledHeight ? (int) (y + Math.ceil((double) selectHeight / 2)) : scaledHeight;

            if (select2.x - select1.x < selectWidth) {
                if (select2.x >= scaledWidth) {
                    select1.x = scaledWidth - selectWidth;
                }
                if (select1.x <= 0) {
                    select2.x = selectWidth;
                }
            }
            if (select2.y - select1.y < selectHeight) {
                if (select1.y <= 0) {
                    select2.y = selectHeight;
                }
                if (select2.y >= scaledHeight) {
                    select1.y = scaledHeight - selectHeight;
                }
            }

            int x1 = select1.x * image.getWidth() / scaledWidth, x2 = select2.x * image.getWidth() / scaledWidth;
            int y1 = select1.y * image.getHeight() / scaledHeight, y2 = select2.y * image.getHeight() / scaledHeight;
            if (x2 - x1 < SIZE) {
                if (x2 == image.getWidth()) x1 = x2 - SIZE;
                if (x1 == 0) x2 = SIZE;
            }
            if (y2 - y1 < SIZE) {
                if (y2 == image.getHeight()) y1 = y2 - SIZE;
                if (y1 == 0) y2 = SIZE;
            }
            parentPanel.getZoneB().setImage(image.getSubimage(x1, y1, x2 - x1, y2 - y1));
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

    void setParentPanel(ZonesPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    public int getSelectWidth() {
        return selectWidth;
    }

    public int getSelectHeight() {
        return selectHeight;
    }
}
