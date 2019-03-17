package ru.nsu.fit.g16203.razumov.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class HexagonGrid extends JPanel {

    private static final int MAX_W = 3840, MAX_H = 2160;

    private BufferedImage image;
    private BufferedImage impactImage;

    static final int BACKGROUND_COLOR = Color.WHITE.getRGB();
    static final int ALIVE_COLOR = Color.GREEN.getRGB();
    static final int DEAD_COLOR = Color.GRAY.getRGB();

    private boolean replaceMode;
    private boolean isImpactShown;


    public boolean isChanged;

    private double liveBegin = 2.0, liveEnd = 3.3, birthBegin = 2.3, birthEnd = 2.9, fstImpact = 1.0, sndImpact = 0.3;

    private Hexagon prevHex;

    public boolean isRunning;

    public Hexagon[][] grid;

    private Integer n;
    private Integer m;
    private Integer hexSize;
    private Integer thickness;

    public int width, height;

    private int w, h;

    public HexagonGrid(int n, int m) {

        this.n = n;
        this.m = m;

        this.hexSize = 30;
        this.thickness = 1;

        image = new BufferedImage(MAX_W, MAX_H, BufferedImage.TYPE_INT_RGB);

        replaceMode = true;
        isImpactShown = false;
        isRunning = false;
        prevHex = null;
        isChanged = false;

        for (int i = 0; i < image.getWidth(); i++)
            for (int j = 0; j < image.getHeight(); j++)
                image.setRGB(i, j, BACKGROUND_COLOR);   //erasing old grid

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

    public void initGrid() {
        this.h = 2 * hexSize;
        this.w = (int) (Math.sqrt(3) * hexSize);

        grid = new Hexagon[n][m];

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                image.setRGB(i, j, BACKGROUND_COLOR);   //erasing old grid

        this.width = n * w + 20;
        this.height = m * h * 3 / 4 + 50;

        this.setPreferredSize(new Dimension(width, height));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (i == n - 1 && j % 2 != 0) continue;
                grid[i][j] = new Hexagon(i, j, image, hexSize, thickness);
            }
        }
        repaint();
        if (isImpactShown) switchImpact();
    }

    @Override
    protected void paintComponent(Graphics g) {  //paint hexagon field
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

    public void reset() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                if (grid[i][j] != null) {
                    if (grid[i][j].currentColorRGB != DEAD_COLOR) setDead(grid[i][j], isImpactShown);
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
        if (hexagon.impact == 2)
            System.out.println();

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

    public void switchImpact() {
        isImpactShown = !isImpactShown;

        if (hexSize >= 15) {
            for (int i = 0; i < n; i++)
                for (int j = 0; j < m; j++)
                    if (grid[i][j] != null) {
                        if (isImpactShown) grid[i][j].showImpact();
                        else grid[i][j].hideImpact();
                    }
            repaint();
        } else JOptionPane.showMessageDialog(this,
                "Too little hexagon hexSize for displaying impacts",
                "Can't display impacts", JOptionPane.ERROR_MESSAGE);
    }

    public void setAlive(Hexagon hex, boolean isImpactShown) {
        if (!isChanged) isChanged = true;
        if (isImpactShown) {
            hex.hideImpact();
            hex.spanSelf(ALIVE_COLOR);
            hex.showImpact();
        } else hex.spanSelf(ALIVE_COLOR);
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
        if (!isChanged) isChanged = true;
        if (hex.isDead) return;
        if (!isImpactShown) {
            hex.spanSelf(DEAD_COLOR);
        } else {
            hex.hideImpact();
            hex.spanSelf(DEAD_COLOR);
            hex.showImpact();
        }
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
        JPanel slidersPanel = new JPanel(new GridLayout(6, 1));
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
        JTextField sizeField = new JTextField(this.hexSize.toString());
        sizesValuesPanel.add(sizeField);
        JSlider sliderSize = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
        sliderSize.setValue(hexSize);
        slidersPanel.add(sliderSize);

        //Thickness
        JLabel thicknessLabel = new JLabel("Thickness", SwingConstants.CENTER);
        sizesNamesPanel.add(thicknessLabel);
        JTextField thicknessField = new JTextField(this.thickness.toString());
        sizesValuesPanel.add(thicknessField);
        JSlider sliderThickness = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        sliderThickness.setValue(thickness);
        slidersPanel.add(sliderThickness);

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

        sliderThickness.addChangeListener(e ->
                thicknessField.setText(((Integer) ((JSlider) e.getSource()).getValue()).toString())
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

        thicknessField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!thicknessField.getText().isEmpty())
                    sliderThickness.setValue(Integer.parseInt(thicknessField.getText()));
            }
        });

        confirmButton.addActionListener(e -> {
            int n = this.n, m = this.m, size = this.hexSize, thickness = this.thickness;

            double tmp;

            if (!nField.getText().isEmpty()) {
                try {
                    n = Integer.parseInt(nField.getText());
                } catch (NumberFormatException ignored) {
                }
            }
            if (!mField.getText().isEmpty()) {
                try {
                    m = Integer.parseInt(mField.getText());
                } catch (NumberFormatException ignored) {
                }
            }
            if (!sizeField.getText().isEmpty()) {
                try {
                    size = sliderSize.getValue();
                } catch (NumberFormatException ignored){}
            }

            if (!thicknessField.getText().isEmpty()) {
                try{
                thickness = sliderThickness.getValue();
                } catch (NumberFormatException ignored){}
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

            if (this.hexSize != size || this.n != n || this.m != m || this.thickness != thickness) {
                int newSize = isResolutionOk(n, m, size);
                if (newSize != -1) {
                    int res = JOptionPane.showConfirmDialog(this,
                            "To display grid with this number of rows and columns size will be changed to " + newSize,
                            "Size change", JOptionPane.OK_CANCEL_OPTION);
                    if (res == JOptionPane.OK_OPTION) {
                        this.hexSize = newSize;
                        this.n = n;
                        this.m = m;
                    } else return;
                } else {
                    this.hexSize = size;
                    this.n = n;
                    this.m = m;
                }
                this.thickness = thickness;
                initGrid();
            }
            dialog.dispose();
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

    private int isResolutionOk(int n, int m, int size) {
        int newSize = -1;
        if (n * size * Math.sqrt(3) > MAX_W) newSize = (int) (MAX_W / (n * Math.sqrt(3)));
        if (m * size * 2 > MAX_H) newSize = MAX_H / (2 * m);
        return newSize;
    }

    public Integer getN() {
        return n;
    }

    public Integer getM() {
        return m;
    }

    public Integer getHexSize() {
        return hexSize;
    }

    public Integer getThickness() {
        return thickness;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public void setM(Integer m) {
        this.m = m;
    }

    public void setHexSize(Integer hexSize) {
        this.hexSize = hexSize;
    }

    public void setThickness(Integer thickness) {
        this.thickness = thickness;
    }

    public List<Point> getAliveGrid() {
        List<Point> alive = new ArrayList<>();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == null) continue;
                if (!grid[i][j].isDead) alive.add(new Point(i, j));
            }
        return alive;
    }

    public void setImpactShown(boolean impactShown) {
        isImpactShown = impactShown;
    }
}
