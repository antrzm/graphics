package ru.nsu.fit.g16203.razumov.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class HexagonGrid extends JPanel {

    private BufferedImage image;

    static final int BACKGROUND_COLOR = Color.WHITE.getRGB();
    static final int ALIVE_COLOR = Color.GREEN.getRGB();

    private boolean replaceMode;
    private boolean isImpactShown;

    private Hexagon prevHex;

    private boolean isRunning;

    private Hexagon[][] grid;

    private int n, m;

    public int width, height;

    public HexagonGrid(int n, int m) {
        grid = new Hexagon[n][m];
        Point[][] centers = new Point[n][m];

        this.n = n;
        this.m = m;

        replaceMode = true;
        isImpactShown = false;
        isRunning = false;
        prevHex = null;

        image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getWidth(); i++)
            for (int j = 0; j < image.getHeight(); j++)
                image.setRGB(i, j, Color.WHITE.getRGB());


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (i == n - 1 && j % 2 != 0) continue;
                centers[i][j] = new Point();
                grid[i][j] = new Hexagon(i, j, image, centers, grid);
            }
        }

        this.width = n * grid[0][0].getW() + 100;
        this.height = m * grid[0][0].getH();

        addMouseListener(new MouseAdapter() {                               //for 1 click
            @Override
            public void mouseClicked(MouseEvent e) {
                Hexagon hex = getHexAt(e.getX(), e.getY());
                if (hex != null) {
                    if (hex.isDead) {
                        hex.setAlive(isImpactShown);
                    } else if (!replaceMode) {
                        hex.setDead(isImpactShown);
                    }
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionListener() {                 //for multiple hexagons
            @Override
            public void mouseDragged(MouseEvent e) {
                Hexagon hex = getHexAt(e.getX(), e.getY());
                if (hex != null && hex != prevHex) {
                    if (replaceMode && hex.isDead) {
                        hex.setAlive(isImpactShown);
                    } else if (!replaceMode && !hex.isDead) {
                        hex.setDead(isImpactShown);
                    } else if (!replaceMode && hex.isDead) {
                        hex.setAlive(isImpactShown);
                    }
                    prevHex = hex;
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        Runnable play = () -> {
            if (isRunning) nextStep();
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(play, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void paintComponent(Graphics g) {  //paint hexagon field
//        Graphics2D graphics = (Graphics2D) g;
//        TODO:
//                Stroke stroke = new BasicStroke(5.0f, CAP_SQUARE, JOIN_MITER, 10.0f, null, 0.0f);
//                graphics.setStroke(stroke);
        g.drawImage(image, 0, 0, null);
    }

    private Hexagon getHexAt(int x, int y) {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == null) continue;
                if (grid[i][j].isInside(x, y)) {
                    return grid[i][j];
                }
            }
        return null;
    }

    public void init() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                if (grid[i][j] != null) {
                    if (grid[i][j].currentColorRGB != BACKGROUND_COLOR) grid[i][j].setDead(isImpactShown);
                    grid[i][j].setImpact(0, isImpactShown);
                }
        repaint();
    }

    public void setReplaceMode() {
        replaceMode = true;
    }

    public void setXorMode() {
        replaceMode = false;
    }

    private void updateImpacts() {
        List<Hexagon> toKillList = new LinkedList<>();
        List<Hexagon> toBeBornList = new LinkedList<>();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                if (grid[i][j] != null)
                    grid[i][j].updateImpact(toKillList, toBeBornList);
        for (Hexagon hex : toBeBornList) hex.setAlive(isImpactShown);
        for (Hexagon hex : toKillList) hex.setDead(isImpactShown);
    }

    public void switchRun(){
        isRunning = !isRunning;
    }

    public void nextStep() {
        updateImpacts();
        repaint();
    }

    public void showImpact() {
        isImpactShown = !isImpactShown;

        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                if (grid[i][j] != null)
                    if (isImpactShown) grid[i][j].showImpact();
                    else grid[i][j].hideImpact();
        repaint();
    }

}
