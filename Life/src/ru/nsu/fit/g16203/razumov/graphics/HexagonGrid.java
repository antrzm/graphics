package ru.nsu.fit.g16203.razumov.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class HexagonGrid extends JPanel {

    private BufferedImage image;

    static final int BACKGROUND_COLOR = Color.WHITE.getRGB();
    static final int ALIVE_COLOR = Color.GREEN.getRGB();

    private boolean replaceMode;
    private Hexagon prevHex;


    private Hexagon[][] grid;
    private Point[][] centers;

    private int n, m;

    public int width, height;

    public HexagonGrid(int n, int m) {
        grid = new Hexagon[n][m];
        centers = new Point[n][m];

        this.n = n;
        this.m = m;

        replaceMode = true;
        prevHex = null;

        image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getWidth(); i++)
            for (int j = 0; j < image.getHeight(); j++)
                image.setRGB(i, j, Color.WHITE.getRGB());


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                centers[i][j] = new Point();
                grid[i][j] = new Hexagon(i, j, image, centers);
            }
        }

        this.width = n * grid[0][0].getW() + 100;
        this.height = m * grid[0][0].getH();

        addMouseListener(new MouseAdapter() {                               //for 1 click
            @Override
            public void mouseClicked(MouseEvent e) {
                Hexagon hex = getHexAt(e.getX(), e.getY());
                if (hex != null) {
                    if (replaceMode && hex.currentColor == BACKGROUND_COLOR)
                        hex.spanSelf(ALIVE_COLOR);
                    else if (!replaceMode && hex.currentColor == BACKGROUND_COLOR)
                        hex.spanSelf(ALIVE_COLOR);
                    else if (!replaceMode && hex.currentColor == ALIVE_COLOR)
                        hex.spanSelf(BACKGROUND_COLOR);
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionListener() {                 //for multiple hexagons
            @Override
            public void mouseDragged(MouseEvent e) {
                Hexagon hex = getHexAt(e.getX(), e.getY());
                if (hex != null && hex != prevHex) {
                    if (replaceMode && hex.currentColor == BACKGROUND_COLOR)
                        hex.spanSelf(ALIVE_COLOR);
                    else if (!replaceMode && hex.currentColor == BACKGROUND_COLOR)
                        hex.spanSelf(ALIVE_COLOR);
                    else if (!replaceMode && hex.currentColor == ALIVE_COLOR)
                        hex.spanSelf(BACKGROUND_COLOR);
                    prevHex = hex;
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {  //paint hexagon field
        Graphics2D graphics = (Graphics2D) g;
        //TODO:
        //        Stroke stroke = new BasicStroke(5.0f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f);
        //        graphics.setStroke(stroke);
        graphics.drawImage(image, 0, 0, null);
    }

    private Hexagon getHexAt(int x, int y) {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                if (grid[i][j].isInside(x, y)) {
                    return grid[i][j];
                }
        return null;
    }

    public void init() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                if (grid[i][j].currentColor != BACKGROUND_COLOR) grid[i][j].spanSelf(BACKGROUND_COLOR);
        repaint();
    }

    public void setReplaceMode() {
        replaceMode = true;
    }

    public void setXorMode() {
        replaceMode = false;
    }

//    private void spanHexagon(int i, int j) {
//        Stack<Pair<Integer, Integer>> toSpanDots = new Stack<>();
//        toSpanDots.add(new Pair<>(i, j));
//
//        int prevColor = BACKGROUND_COLOR, newColor = ALIVE_COLOR;
//
//        try {
//            if (image.getRGB(i, j) == newColor) {
//                if (replaceMode)
//                    return;
//                else {
//                    newColor = BACKGROUND_COLOR;
//                    prevColor = ALIVE_COLOR;
//                }
//            }
//
//            boolean upperSpanTrigger, lowerSpanTrigger;
//
//            while (!toSpanDots.empty()) {
//
//                Pair<Integer, Integer> dot = toSpanDots.pop();
//                int x = dot.getKey(), y = dot.getValue();
//
//                upperSpanTrigger = true;
//                lowerSpanTrigger = true;
//
//                while (image.getRGB(x, y) == prevColor) x--;     //going to left border of hexagon
//
//                x++;                                                   //making x is the first pixel from border
//
//                while (image.getRGB(x, y) == prevColor) {
//                    image.setRGB(x, y, newColor);
//
//                    if (image.getRGB(x, y + 1) == prevColor) {
//                        if (upperSpanTrigger) {
//                            toSpanDots.push(new Pair<>(x, y + 1));
//                            upperSpanTrigger = false;
//                        }
//                    } else upperSpanTrigger = true;
//
//                    if (image.getRGB(x, y - 1) == prevColor) {
//                        if (lowerSpanTrigger) {
//                            toSpanDots.push(new Pair<>(x, y - 1));
//                            lowerSpanTrigger = false;
//                        }
//                    } else lowerSpanTrigger = true;
//
//                    x++;
//                }
//            }
//        } catch (ArrayIndexOutOfBoundsException ignored) {
//            return;
//        }
//        repaint();
//    }
}
