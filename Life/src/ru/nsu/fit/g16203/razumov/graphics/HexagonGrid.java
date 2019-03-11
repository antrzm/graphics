package ru.nsu.fit.g16203.razumov.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class HexagonGrid extends JPanel {

    private BufferedImage image;

    static final int BACKGROUND_COLOR = Color.WHITE.getRGB();
    static final int ALIVE_COLOR = Color.GREEN.getRGB();

    private boolean replaceMode;
    private boolean isImpactShown;

    private double liveBegin = 2.0, liveEnd = 3.3, birthBegin = 2.3, birthEnd = 2.9, fstImpact = 1.0, sndImpact = 0.3;

    private Hexagon prevHex;

    private boolean isRunning;

    private Hexagon[][] grid;
    private Point[][] centers;

    private Integer n;
    private Integer m;
    private Integer size;

    public int width, height;

    private int w, h;

    public HexagonGrid(int n, int m) {
        this.n = n;
        this.m = m;

        this.size = 30;
        this.h = 2 * size;
        this.w = (int) (Math.sqrt(3) * size);

        this.width = n * w + 100;
        this.height = m * h;

        replaceMode = true;
        isImpactShown = false;
        isRunning = false;
        prevHex = null;

        initGrid();

        addMouseListener(new MouseAdapter() {                               //for 1 click
            @Override
            public void mouseClicked(MouseEvent e) {
                Hexagon hex = getHexAt(e.getX(), e.getY());
                if (hex != null) {
                    if (hex.isDead) {
                        setAlive(hex, isImpactShown);
                    } else if (!replaceMode) {
                        setDead(hex, isImpactShown);
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
                        setAlive(hex, isImpactShown);
                    } else if (!replaceMode && !hex.isDead) {
                        setDead(hex, isImpactShown);
                    } else if (!replaceMode && hex.isDead) {
                        setAlive(hex, isImpactShown);
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

    private void initGrid() {
        this.h = 2 * size;
        this.w = (int) (Math.sqrt(3) * size);

        grid = new Hexagon[n][m];

        centers = new Point[n][m];

        if (n * w > 1920) {
            size = (int) (1920 / (n * Math.sqrt(3)));
            initGrid();
        }
        if (m * h > 1080) {
            size = 1080 / (m * 2);
            initGrid();
        }

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getWidth(); i++)
            for (int j = 0; j < image.getHeight(); j++)
                image.setRGB(i, j, Color.WHITE.getRGB());


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (i == n - 1 && j % 2 != 0) continue;
                centers[i][j] = new Point();
                grid[i][j] = new Hexagon(i, j, image, centers, grid, size);
            }
        }
        repaint();
        if (isImpactShown) showImpact();
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
                    if (grid[i][j].currentColorRGB != BACKGROUND_COLOR) setDead(grid[i][j], isImpactShown);
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
                    updateHexImpact(grid[i][j], toKillList, toBeBornList);
        for (Hexagon hex : toBeBornList) setAlive(hex, isImpactShown);
        for (Hexagon hex : toKillList) setDead(hex, isImpactShown);
    }

    private void updateHexImpact(Hexagon hexagon, List<Hexagon> toKillList, List<Hexagon> toBeBornList) {
        if (hexagon.isDead && hexagon.impact >= birthBegin && hexagon.impact <= birthEnd)
            toBeBornList.add(hexagon);
        else if (!hexagon.isDead && hexagon.impact < liveBegin || hexagon.impact > liveEnd)
            toKillList.add(hexagon);
    }

    public void switchRun() {
        isRunning = !isRunning;
    }

    public void nextStep() {
        updateImpacts();
        repaint();
    }

    public void showImpact() {
        isImpactShown = !isImpactShown;

        if (size > 15) {
            for (int i = 0; i < n; i++)
                for (int j = 0; j < m; j++)
                    if (grid[i][j] != null)
                        if (isImpactShown) grid[i][j].showImpact();
                        else grid[i][j].hideImpact();
            repaint();
        } else JOptionPane.showMessageDialog(this,
                "Too little hexagon size for displaying impacts",
                "Can't display impacts", JOptionPane.ERROR_MESSAGE);
    }

    private void setAlive(Hexagon hex, boolean isImpactShown) {
        hex.spanSelf(ALIVE_COLOR);
        for (Point n1 : hex.firstOrderNeighbours) {
            if (isValid(n1.x, n1.y)) {
                double impact = grid[n1.x][n1.y].getImpact();
                grid[n1.x][n1.y].setImpact(impact + fstImpact, isImpactShown);
            }
        }
        for (Point n2 : hex.secondOrderNeighbours) {
            if (isValid(n2.x, n2.y)) {
                double impact = grid[n2.x][n2.y].getImpact();
                grid[n2.x][n2.y].setImpact(impact + sndImpact, isImpactShown);
            }
        }
    }

    private void setDead(Hexagon hex, boolean isImpactShown) {
        if (hex.isDead) return;
        hex.spanSelf(BACKGROUND_COLOR);
        for (Point n1 : hex.firstOrderNeighbours) {
            if (isValid(n1.x, n1.y)) {
                double impact = grid[n1.x][n1.y].getImpact();
                grid[n1.x][n1.y].setImpact(impact - fstImpact, isImpactShown);
            }
        }
        for (Point n2 : hex.secondOrderNeighbours) {
            if (isValid(n2.x, n2.y)) {
                double impact = grid[n2.x][n2.y].getImpact();
                grid[n2.x][n2.y].setImpact(impact - sndImpact, isImpactShown);
            }
        }
    }

    private boolean isValid(int i, int j) {
        if (i < 0 || j < 0) return false;
        if (i < grid.length && j < grid[0].length)
            return grid[i][j] != null;
        return false;
    }

    public void initDialog(JDialog dialog) {
        JPanel mainPanel = new JPanel(new GridLayout(1, 5));
        JPanel sizesNamesPanel = new JPanel(new GridLayout(6, 1));
        JPanel slidersPanel = new JPanel(new GridLayout(5, 1));
        JPanel sizesValuesPanel = new JPanel(new GridLayout(6, 1));
        JPanel impactNamesPanel = new JPanel(new GridLayout(6, 1));
        JPanel impactValuesPanel = new JPanel(new GridLayout(6, 1));

        //N
        JLabel nLabel = new JLabel("Number of columns", SwingConstants.CENTER);
        sizesNamesPanel.add(nLabel);
        JTextField nField = new JTextField(this.n.toString());
        sizesValuesPanel.add(nField);
        JSlider sliderN = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
        sliderN.setValue(n);
        slidersPanel.add(sliderN);
        sizesValuesPanel.add(nField);

        //M
        JLabel mLabel = new JLabel("Number of rows", SwingConstants.CENTER);
        sizesNamesPanel.add(mLabel);
        JTextField mField = new JTextField(this.m.toString());
        sizesValuesPanel.add(mField);
        JSlider sliderM = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
        sliderM.setValue(m);
        slidersPanel.add(sliderM);

        //Size
        JLabel sizeLabel = new JLabel("Size", SwingConstants.CENTER);
        sizesNamesPanel.add(sizeLabel);
        JTextField sizeField = new JTextField(this.size.toString());
        sizesValuesPanel.add(sizeField);
        JSlider sliderSize = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
        sliderSize.setValue(size);
        slidersPanel.add(sliderSize);

        JLabel liveBeginLabel = new JLabel("LIVE BEGIN", SwingConstants.CENTER);
        impactNamesPanel.add(liveBeginLabel);
        JTextField liveBeginText = new JTextField(new DecimalFormat("0.#").format(liveBegin));
        impactValuesPanel.add(liveBeginText);

        JLabel liveEndLabel = new JLabel("LIVE END", SwingConstants.CENTER);
        impactNamesPanel.add(liveEndLabel);
        JTextField liveEndText = new JTextField(new DecimalFormat("0.#").format(liveEnd));
        impactValuesPanel.add(liveEndText);

        JLabel birthBeginLabel = new JLabel("BIRTH BEGIN", SwingConstants.CENTER);
        impactNamesPanel.add(birthBeginLabel);
        JTextField birthBeginText = new JTextField(new DecimalFormat("0.#").format(birthBegin));
        impactValuesPanel.add(birthBeginText);

        JLabel birthEndLabel = new JLabel("BIRTH END", SwingConstants.CENTER);
        impactNamesPanel.add(birthEndLabel);
        JTextField birthEndText = new JTextField(new DecimalFormat("0.#").format(birthEnd));
        impactValuesPanel.add(birthEndText);

        JLabel fstImpactLabel = new JLabel("FIRST IMPACT", SwingConstants.CENTER);
        impactNamesPanel.add(fstImpactLabel);
        JTextField fstImpactText = new JTextField(new DecimalFormat("0.#").format(fstImpact));
        impactValuesPanel.add(fstImpactText);

        JLabel sndImpactLabel = new JLabel("SECOND IMPACT", SwingConstants.CENTER);
        impactNamesPanel.add(sndImpactLabel);
        JTextField sndImpactText = new JTextField(new DecimalFormat("0.#").format(sndImpact));
        impactValuesPanel.add(sndImpactText);

        ButtonGroup xorReplace = new ButtonGroup();

        JRadioButton xorButton = new JRadioButton("XOR", false);
        xorButton.setSelected(!replaceMode);
        xorReplace.add(xorButton);

        JRadioButton replaceButton = new JRadioButton("Replace", true);
        replaceButton.setSelected(replaceMode);
        xorReplace.add(replaceButton);

        sizesNamesPanel.add(xorButton);
        sizesValuesPanel.add(replaceButton);

        JButton confirmButton = new JButton("Confirm");

        sliderN.addChangeListener(e ->
                nField.setText(((Integer) ((JSlider) e.getSource()).getValue()).toString())
        );

        sliderM.addChangeListener(e ->
                mField.setText(((Integer) ((JSlider) e.getSource()).getValue()).toString())
        );

        sliderSize.addChangeListener(e ->
                sizeField.setText(((Integer) ((JSlider) e.getSource()).getValue()).toString())
        );

        nField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!nField.getText().isEmpty())
                    sliderN.setValue(Integer.parseInt(nField.getText()));
            }
        });

        mField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!nField.getText().isEmpty())
                    sliderM.setValue(Integer.parseInt(mField.getText()));
            }
        });

        sizeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!sizeField.getText().isEmpty())
                    sliderSize.setValue(Integer.parseInt(sizeField.getText()));
            }
        });

        confirmButton.addActionListener(e -> {
            int n = this.n, m = this.m, size = this.size;

            double tmp;

            if (!nField.getText().isEmpty()) {
                n = Integer.parseInt(nField.getText());
            }
            if (!mField.getText().isEmpty()) {
                m = Integer.parseInt(mField.getText());
            }
            if (!sizeField.getText().isEmpty()) {
                size = sliderSize.getValue() >= 10 ? sliderSize.getValue() : 10;
            }

            if (!fstImpactText.getText().isEmpty()) {
                try {
                    fstImpact = Double.parseDouble(fstImpactText.getText().replace(",", "."));
                } catch (NumberFormatException err) {
                    err.printStackTrace();
                }

            }

            if (!sndImpactText.getText().isEmpty()) {
                try {
                    sndImpact = Double.parseDouble(sndImpactText.getText().replace(",", "."));
                } catch (NumberFormatException err) {
                    err.printStackTrace();
                }
            }

            if (!liveBeginText.getText().isEmpty()) {
                try {
                    tmp = Double.parseDouble(liveBeginText.getText().replace(",", "."));
                    if (tmp <= birthBegin) {
                        liveBegin = tmp;
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "LIVE BEGIN should be less than BIRTH BEGIN",
                                "Wrong input", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException err) {
                    err.printStackTrace();
                }
            }


            if (!birthBeginText.getText().isEmpty()) {
                try {
                    tmp = Double.parseDouble(birthBeginText.getText().replace(",", "."));
                    if (tmp <= birthBegin && liveBegin <= tmp) {
                        birthBegin = tmp;

                    } else {
                        JOptionPane.showMessageDialog(this,
                                "BIRTH BEGIN shouldn't be larger BIRTH END",
                                "Wrong input", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException err) {
                    err.printStackTrace();
                }
            }
            if (!liveEndText.getText().isEmpty()) {
                try {
                    tmp = Double.parseDouble(liveEndText.getText().replace(",", "."));
                    if (birthEnd <= tmp) {
                        liveEnd = tmp;
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "LIVE END should be larger than BIRTH END",
                                "Wrong input", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException err) {
                    err.printStackTrace();
                }
            }

            if (!birthEndText.getText().isEmpty()) {
                try {
                    tmp = Double.parseDouble(birthEndText.getText().replace(",", "."));
                    if (birthBegin <= tmp && tmp <= liveEnd) {
                        birthEnd = tmp;
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "BIRTH END value should be between BIRTH BEGIN and LIVE END",
                                "Wrong input", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException err) {
                    err.printStackTrace();
                }
            }

            if (this.size != size || this.n != n || this.m != m) {
                this.size = size;
                this.n = n;
                this.m = m;
                dialog.dispose();
                initGrid();
            }
        });

        mainPanel.add(sizesNamesPanel);
        mainPanel.add(slidersPanel);
        mainPanel.add(sizesValuesPanel);
        mainPanel.add(impactNamesPanel);
        mainPanel.add(impactValuesPanel);

        dialog.add(mainPanel);
        dialog.add(confirmButton, BorderLayout.PAGE_END);

        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setPreferredSize(new Dimension(720, 480));
        dialog.setResizable(false);
        dialog.pack();
    }
}
