package ru.nsu.fit.g16203.razumov.graphics;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Stack;

import static ru.nsu.fit.g16203.razumov.graphics.HexagonGrid.ALIVE_COLOR;
import static ru.nsu.fit.g16203.razumov.graphics.HexagonGrid.BACKGROUND_COLOR;

class Hexagon extends JPanel {

    private int gridX, gridY;
    private int coordsX, coordsY;
    private boolean isOddRow;

    double impact;

    boolean isDead = true;

    int currentColorRGB;

    private int size;

    private final int h, w;

    private BufferedImage imageGrid;

    private Hexagon[][] grid;

    Point[] firstOrderNeighbours;
    Point[] secondOrderNeighbours;

    Hexagon(int x, int y, BufferedImage image, Point[][] centers, Hexagon[][] grid, int size) {
        this.gridX = x;
        this.gridY = y;

        this.grid = grid;

        this.size = size;

        this.h = 2 * size;
        this.w = (int) (Math.sqrt(3) * size);

        this.firstOrderNeighbours = new Point[6];
        this.secondOrderNeighbours = new Point[6];

        this.isOddRow = y % 2 == 0;

        initNeighbours();

        this.impact = 0;

        this.currentColorRGB = BACKGROUND_COLOR;

        this.imageGrid = image;

        if (isOddRow) drawOddRowHex(centers);
        else drawEvenRowHex(centers);

        this.coordsX = centers[gridX][gridY].x;
        this.coordsY = centers[gridX][gridY].y;
    }

    private void initNeighbours() {
        int x = gridX, y = gridY;
        if (isOddRow) {
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

    private void drawEvenRowHex(Point[][] centers) {
        int off = 10;   //offset

        int wx = w * this.gridX + w / 2, hy = (int) (h * (this.gridY + 1) / 2 * 1.5) - 3 * h / 4;

        centers[this.gridX][this.gridY].x = off + w * gridX + w;
        centers[this.gridX][this.gridY].y = off + hy + h / 2;

        bresenham(off + wx, off + hy + h / 4, off + wx + w / 2, off + hy);
        bresenham(off + wx + w / 2, off + hy, off + wx + w, off + hy + h / 4);
        bresenham(off + wx + w, off + hy + h / 4, off + wx + w, off + hy + 3 * h / 4);
        bresenham(off + wx + w, off + hy + 3 * h / 4, off + wx + w / 2, off + hy + h);
        bresenham(off + wx + w / 2, off + hy + h, off + wx, off + hy + 3 * h / 4);
        bresenham(off + wx, off + hy + 3 * h / 4, off + wx, off + hy + h / 4);

    }

    private void drawOddRowHex(Point[][] centers) {
        int off = 10;   //offset

        int wx = w * this.gridX, hy = (int) (h * this.gridY * 1.5) / 2;

        centers[this.gridX][this.gridY].x = off + wx + w / 2;
        centers[this.gridX][this.gridY].y = off + hy + h / 2;

        bresenham(off + wx, off + hy + h / 4, off + wx + w / 2, off + hy);
        bresenham(off + wx + w / 2, off + hy, off + wx + w, off + hy + h / 4);
        bresenham(off + wx + w, off + hy + h / 4, off + wx + w, off + hy + 3 * h / 4);
        bresenham(off + wx + w, off + hy + 3 * h / 4, off + wx + w / 2, off + hy + h);
        bresenham(off + wx + w / 2, off + hy + h, off + wx, off + hy + 3 * h / 4);
        bresenham(off + wx, off + hy + 3 * h / 4, off + wx, off + hy + h / 4);

        System.out.println();
    }

    private void bresenham(int x1, int y1, int x2, int y2) {

        if (imageGrid.getRGB(x1, y1) != BACKGROUND_COLOR && imageGrid.getRGB(x2, y2) != BACKGROUND_COLOR && (imageGrid.getRGB((x1 + x2) / 2, (y1 + y2) / 2) != BACKGROUND_COLOR || imageGrid.getRGB((x1 + x2) / 2 + 1, (y1 + y2) / 2) != BACKGROUND_COLOR))
            return;        //avoiding double line

        int x = x1, y = y1;
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = Integer.compare(x2 - x1, 0);
        int sy = Integer.compare(y2 - y1, 0);
        int err = 2 * dy - dx;
        boolean change = false;
        if (dy > dx) {
            int z = dx;
            dx = dy;
            dy = z;
            change = true;
        }
        imageGrid.setRGB(x, y, Color.BLACK.getRGB());
        for (int k = 1; k <= (dx + dy); k++) {
            if (err < dx) {
                if (change) y += sy;
                else x += sx;
                err += 2 * dy;
            } else {
                if (change) x += sx;
                else y = y + sy;
                err -= 2 * dx;
            }
            imageGrid.setRGB(x, y, Color.BLACK.getRGB());
        }
    }

    void spanSelf(int color) {
        Stack<Pair<Integer, Integer>> toSpanDots = new Stack<>();
        toSpanDots.add(new Pair<>(this.coordsX - w / 4, this.coordsY));

        try {
            boolean upperSpanTrigger, lowerSpanTrigger;

            while (!toSpanDots.empty()) {

                Pair<Integer, Integer> dot = toSpanDots.pop();
                int x = dot.getKey(), y = dot.getValue();

                upperSpanTrigger = true;
                lowerSpanTrigger = true;

                while (imageGrid.getRGB(x, y) == currentColorRGB) x--;     //going to left border of hexagon

                x++;                                                   //making x is the first pixel from border

                while (imageGrid.getRGB(x, y) == currentColorRGB) {
                    imageGrid.setRGB(x, y, color);

                    if (imageGrid.getRGB(x, y + 1) == currentColorRGB) {
                        if (upperSpanTrigger) {
                            toSpanDots.push(new Pair<>(x, y + 1));
                            upperSpanTrigger = false;
                        }
                    } else upperSpanTrigger = true;

                    if (imageGrid.getRGB(x, y - 1) == currentColorRGB) {
                        if (lowerSpanTrigger) {
                            toSpanDots.push(new Pair<>(x, y - 1));
                            lowerSpanTrigger = false;
                        }
                    } else lowerSpanTrigger = true;

                    x++;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return;
        }
        if (color == ALIVE_COLOR) this.isDead = false;
        if (color == BACKGROUND_COLOR) this.isDead = true;
        this.currentColorRGB = color;
    }

    boolean isInside(int x, int y) {
        int x0 = x - coordsX, y0 = y - coordsY;
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

        if (Math.abs(px - py) > Math.sqrt(w / 2 * w / 2 + h / 2 * h / 2) / 2)
            return false;

        return true;
    }

    int getH() {
        return h;
    }

    int getW() {
        return w;
    }

    void showImpact() {
        Graphics2D g2d = imageGrid.createGraphics();
        g2d.setPaint(Color.BLACK);
        g2d.setFont(new Font("", Font.BOLD, 14));
        String s = new DecimalFormat("0.#").format(impact);
        g2d.drawString(s, coordsX - w/10, coordsY);
        g2d.dispose();
    }

    void hideImpact() {
        Graphics2D g2d = imageGrid.createGraphics();
        g2d.setPaint(currentColorRGB == ALIVE_COLOR ? Color.GREEN : Color.WHITE);
        g2d.setFont(new Font("", Font.BOLD, 14));
        String s = new DecimalFormat("0.#").format(impact);
        g2d.drawString(s, coordsX - w/10, coordsY);
        g2d.dispose();
        this.spanSelf(currentColorRGB == BACKGROUND_COLOR ? ALIVE_COLOR : BACKGROUND_COLOR);
        this.spanSelf(currentColorRGB == BACKGROUND_COLOR ? ALIVE_COLOR : BACKGROUND_COLOR);
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
