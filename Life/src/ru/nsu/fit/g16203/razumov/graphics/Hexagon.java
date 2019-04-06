package ru.nsu.fit.g16203.razumov.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Stack;

import static ru.nsu.fit.g16203.razumov.graphics.HexagonGrid.ALIVE_COLOR;
import static ru.nsu.fit.g16203.razumov.graphics.HexagonGrid.BACKGROUND_COLOR;
import static ru.nsu.fit.g16203.razumov.graphics.HexagonGrid.DEAD_COLOR;

class Hexagon extends JPanel {

    public int gridX, gridY;
    private Point center;
    private boolean isEvenRow;

    private int thickness;

    double impact;

    boolean isDead = true;

    int currentColorRGB;

    private final double h, w;
    private final int size;

    private BufferedImage imageGrid;

    Point[] firstOrderNeighbours;
    Point[] secondOrderNeighbours;

    Hexagon(int x, int y, BufferedImage image, int size, Integer thickness) {
        this.gridX = x;
        this.gridY = y;

        this.h = 2.0 * size;
        this.w = Math.sqrt(3) * size;
        this.size = size;

        this.thickness = thickness;

        this.firstOrderNeighbours = new Point[6];
        this.secondOrderNeighbours = new Point[6];

        this.isEvenRow = y % 2 == 0;

        initNeighbours();

        this.impact = 0;

        this.currentColorRGB = BACKGROUND_COLOR;

        this.imageGrid = image;

        if (isEvenRow) drawEvenRowHex();
        else drawOddRowHex();

        spanSelf(DEAD_COLOR);
    }

    private void initNeighbours() {
        int x = gridX, y = gridY;
        if (isEvenRow) {
            firstOrderNeighbours[0] = new Point(x - 1, y);
            firstOrderNeighbours[1] = new Point(x - 1, y - 1);
            firstOrderNeighbours[2] = new Point(x - 1, y + 1);
            firstOrderNeighbours[3] = new Point(x, y + 1);
            firstOrderNeighbours[4] = new Point(x, y - 1);
            firstOrderNeighbours[5] = new Point(x + 1, y);

            secondOrderNeighbours[0] = new Point(x - 2, y - 1);
            secondOrderNeighbours[1] = new Point(x - 2, y + 1);
            secondOrderNeighbours[2] = new Point(x + 1, y - 1);
            secondOrderNeighbours[3] = new Point(x + 1, y + 1);
            secondOrderNeighbours[4] = new Point(x, y - 2);
            secondOrderNeighbours[5] = new Point(x, y + 2);
        } else {
            firstOrderNeighbours[0] = new Point(x, y + 1);
            firstOrderNeighbours[1] = new Point(x, y - 1);
            firstOrderNeighbours[2] = new Point(x - 1, y);
            firstOrderNeighbours[3] = new Point(x + 1, y + 1);
            firstOrderNeighbours[4] = new Point(x + 1, y - 1);
            firstOrderNeighbours[5] = new Point(x + 1, y);

            secondOrderNeighbours[0] = new Point(x - 1, y - 1);
            secondOrderNeighbours[1] = new Point(x - 1, y + 1);
            secondOrderNeighbours[2] = new Point(x + 2, y - 1);
            secondOrderNeighbours[3] = new Point(x + 2, y + 1);
            secondOrderNeighbours[4] = new Point(x, y - 2);
            secondOrderNeighbours[5] = new Point(x, y + 2);
        }
    }

    private void drawOddRowHex() {
        int off = 10;   //offset

        double wx = off + w * this.gridX + w / 2.0, hy = off + gridY * (h * 3.0 / 4.0);
        this.center = new Point((int) (wx + w / 2.0), (int) (hy + h / 2.0));

        int x1 = (int) wx, x2 = (int) (wx + w / 2.0), x3 = (int) (wx + w), x4 = x3, x5 = x2, x6 = x1;
        int y1 = (int) (hy + h / 4.0), y2 = (int) hy, y3 = y1, y4 = (int) (hy + 3.0 * h / 4.0), y5 = (int) (hy + h), y6 = y4;

        if (thickness == 1) {
            bresenham(x1, y1, x2, y2);
            bresenham(x2, y2, x3, y3);
            bresenham(x3, y3, x4, y4);
            bresenham(x5, y5, x4, y4);
            bresenham(x6, y6, x5, y5);
            bresenham(x6, y6 + 1, x1, y1);
        } else {
            Graphics2D g = (Graphics2D) this.imageGrid.getGraphics();
            g.setPaint(Color.BLACK);
            g.setStroke(new BasicStroke(thickness));
            g.drawLine(x1, y1, x2, y2);
            g.drawLine(x2, y2, x3, y3);
            g.drawLine(x3, y3, x4, y4);
            g.drawLine(x4, y4, x5, y5);
            g.drawLine(x5, y5, x6, y6);
            g.drawLine(x6, y6, x1, y1);
            g.setStroke(new BasicStroke(1));
        }

    }

    private void drawEvenRowHex() {
        int off = 10;   //offset

        double wx = off + w * this.gridX, hy = off + (1.5 * h * this.gridY) / 2.0;
        this.center = new Point((int) (wx + w / 2.0), (int) (hy + h / 2.0));

        int x1 = (int) wx, x2 = (int) (wx + w / 2.0), x3 = (int) (wx + w), x4 = x3, x5 = x2, x6 = x1;
        int y1 = (int) (hy + h / 4.0), y2 = (int) hy, y3 = y1, y4 = (int) (hy + 3.0 * h / 4.0), y5 = (int) (hy + h), y6 = y4;

        if (thickness == 1) {
            bresenham(x1, y1, x2, y2);
            bresenham(x2, y2, x3, y3);
            bresenham(x3, y3, x4, y4);
            bresenham(x5, y5, x4, y4);
            bresenham(x6, y6, x5, y5);
            bresenham(x6, y6 + 1, x1, y1);
        } else {
            Graphics2D g = this.imageGrid.createGraphics();
            g.setPaint(Color.BLACK);
            g.setStroke(new BasicStroke(thickness));
            g.drawLine(x1, y1, x2, y2);
            g.drawLine(x2, y2, x3, y3);
            g.drawLine(x3, y3, x4, y4);
            g.drawLine(x4, y4, x5, y5);
            g.drawLine(x5, y5, x6, y6);
            g.drawLine(x6, y6, x1, y1);
            g.setStroke(new BasicStroke(1));
        }
    }

    private void bresenham(int x1, int y1, int x2, int y2) {
        int x = x1, y = y1;
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = Integer.compare(x2 - x1, 0), sy = Integer.compare(y2 - y1, 0);
        if (dy > dx) {
            int err = -dy;
            for (int i = 0; i < dy; i++) {
                err += 2 * dx;
                y += sy;

                if (err > 0) {
                    err -= 2 * dy;
                    x += sx;
                }
                if (imageGrid.getRGB(x, y) != Color.BLACK.getRGB())
                    imageGrid.setRGB(x, y, Color.BLACK.getRGB());
            }
        } else {
            int err = -dx;
            for (int i = 0; i < dx; i++) {
                err += 2 * dy;
                x += sx;

                if (err > 0) {
                    err -= 2 * dx;
                    y += sy;
                }
                if (imageGrid.getRGB(x, y) != Color.BLACK.getRGB())
                    imageGrid.setRGB(x, y, Color.BLACK.getRGB());
            }
        }
    }

    void spanSelf(int color) {
        spanFill(imageGrid, (int) (this.center.x - w / 4.0), this.center.y, color, currentColorRGB);
        if (color == ALIVE_COLOR) this.isDead = false;
        if (color == DEAD_COLOR) this.isDead = true;
        this.currentColorRGB = color;
    }

    private static void spanFill(BufferedImage image, int seedX, int seedY, int color, int currentColorRGB) {
        Stack<Point> toSpanDots = new Stack<>();
        toSpanDots.add(new Point(seedX, seedY));

        boolean upperSpanTrigger, lowerSpanTrigger;

        while (!toSpanDots.empty()) {

            Point dot = toSpanDots.pop();
            int x = dot.x, y = dot.y;

            upperSpanTrigger = true;
            lowerSpanTrigger = true;

            while (image.getRGB(x, y) == currentColorRGB) x--;     //going to left border of hexagon

            x++;                                                   //making x is the first pixel from border

            while (image.getRGB(x, y) == currentColorRGB) {
                image.setRGB(x, y, color);

                if (image.getRGB(x, y + 1) == currentColorRGB) {
                    if (upperSpanTrigger) {
                        toSpanDots.push(new Point(x, y + 1));
                        upperSpanTrigger = false;
                    }
                } else upperSpanTrigger = true;

                if (image.getRGB(x, y - 1) == currentColorRGB) {
                    if (lowerSpanTrigger) {
                        toSpanDots.push(new Point(x, y - 1));
                        lowerSpanTrigger = false;
                    }
                } else lowerSpanTrigger = true;
                x++;
            }
        }
    }

    boolean isInside(int x, int y) {
        int x0 = x - center.x, y0 = y - center.y;
        float l2 = x0 * x0 + y0 * y0;
        if (l2 > (h / 2) * (h / 2)) return false;
        if (l2 < (w / 2) * (w / 2))
            return true;


        double px = x0 * 2 / Math.sqrt(3);
        if (Math.abs(px) > Math.sqrt(w / 2 * w / 2 + h / 2 * h / 2) / 2)
            return false;

        double py = px / 2 + y0;
        if (Math.abs(py) > Math.sqrt(w / 2 * w / 2 + h / 2 * h / 2) / 2)
            return false;

        return !(Math.abs(px - py) > Math.sqrt(w / 2 * w / 2 + h / 2 * h / 2) / 2);
    }

    void showImpact() {
        Graphics2D g2d = imageGrid.createGraphics();
        g2d.setPaint(Color.RED);
        int fontSize = this.size * 3 / 4;
        g2d.setFont(new Font("", Font.BOLD, fontSize));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        String s = new DecimalFormat("0.#").format(Math.abs(impact));

        int hOffset = fontSize * 2 / 5;
        int wOffset = fontMetrics.stringWidth(s) / 2;

        g2d.drawString(s, (float) (center.x - wOffset), center.y + hOffset);
        g2d.dispose();
    }

    void hideImpact() {
        Graphics2D g2d = imageGrid.createGraphics();
        g2d.setPaint(currentColorRGB == ALIVE_COLOR ? Color.GREEN : Color.GRAY);
        int fontSize = this.size * 3 / 4;
        g2d.setFont(new Font("", Font.BOLD, fontSize));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        String s = new DecimalFormat("0.#").format(Math.abs(impact));

        int hOffset = fontSize * 2 / 5;
        int wOffset = fontMetrics.stringWidth(s) / 2;

        g2d.drawString(s, (float) (center.x - wOffset), center.y + hOffset);
        g2d.dispose();
    }

    void setImpact(double newVal, boolean isShown) {
        if (isShown) {
            hideImpact();
            this.impact = newVal;
            showImpact();
        } else {
            this.impact = newVal;
        }
    }

    double getImpact() {
        return impact;
    }
}
