package ru.nsu.fit.g16203.razumov.window;

import ru.nsu.fit.g16203.razumov.filters.*;
import ru.nsu.fit.g16203.razumov.panels.ZonesPanel;
import ru.nsu.fit.g16203.razumov.volume.Cube;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Objects;
import java.util.Vector;

public class MainWindow extends MainFrame {
    private ZonesPanel imageZones;

    private static final String READY = "Ready", PAUSED = "Paused", RUNNING = "Running...";     //status bar values

    private JPanel statusBar;
    private JLabel status;
    private File dataDirectory;

    private int angle = 0, r = 2, g = 2, b = 2, gamma = 0, sobel = 100, roberts = 50, x = 350, y = 350, z = 350;

    private JButton selectButton, renderButton;

    private Vector<JButton> filterButtons = new Vector<>();

    public MainWindow() {
        super(1200, 500, "Filter");
        imageZones = new ZonesPanel();
        statusBar = new JPanel();

        statusBar = new JPanel();
        status = new JLabel(READY);
        statusBar.add(status);
        add(statusBar, BorderLayout.PAGE_END);
        add(imageZones);

        //region MenuItems
        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/New", "Create new file", KeyEvent.VK_N, "onNew");
            addMenuItem("File/Open...", "Open an existing file", KeyEvent.VK_O, "onOpen");
            addMenuItem("File/Save as...", "Save active file as", "onSaveAs");
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_E, "Exit.png", "onExit");

            addSubMenu("Edit", KeyEvent.VK_M);   //TODO: add others
            addMenuItem("Edit/Select", "Select", KeyEvent.VK_S, "onSelect");
            addMenuItem("Edit/Invert", "Invert colors", KeyEvent.VK_I, "onInvert");
            addMenuItem("Edit/Black & white", "To black and white", KeyEvent.VK_W, "onBlackWhite");
            addMenuItem("Edit/Floyd-Steinberg", "Floyd-Steinberg dithering", KeyEvent.VK_F, "onFS");
            addMenuItem("Edit/Ordered dithering", "Ordered dithering", KeyEvent.VK_O, "onOrdered");
            addMenuItem("Edit/Sharpen", "Sharpen filter", KeyEvent.VK_S, "onSharpen");
            addMenuItem("Edit/Stamping", "Stamping filter", -1, "onStamping");
            addMenuItem("Edit/WaterColor", "WaterColor filter", KeyEvent.VK_W, "onWaterColor");
            addMenuItem("Edit/Gamma", "Gamma correction", KeyEvent.VK_G, "onGamma");
            addMenuItem("Edit/Gauss", "Gauss blur", -1, "onGauss");
            addMenuItem("Edit/Sobel", "Sobel threshold", -1, "onSobel");
            addMenuItem("Edit/Roberts", "Roberts threshold", -1, "onRoberts");

            addSubMenu("View", KeyEvent.VK_V);
            addMenuItem("View/Toolbar", "Toolbar", KeyEvent.VK_T, "onToolBar");
            addMenuItem("View/Status bar", "Status bar", KeyEvent.VK_S, "onStatusBar");
            addMenuItem("View/B --> C", "Copy B to C", KeyEvent.VK_C, "onBC");
            addMenuItem("View/C --> B", "Copy C to B", KeyEvent.VK_B, "onCB");
            addMenuItem("View/Zoom", "Zoom in", KeyEvent.VK_Z, "onZoom");
            addMenuItem("View/Rotate", "Rotate", KeyEvent.VK_R, "onRotate");

            addSubMenu("Volume", KeyEvent.VK_R);
            addMenuItem("Volume/Open config", "Open config file", KeyEvent.VK_O, "onConfig");
            addMenuItem("Volume/Absorption", "Absorption", KeyEvent.VK_A, "onAbsorption");
            addMenuItem("Volume/Emission", "Emission", KeyEvent.VK_E, "onEmission");
            addMenuItem("Volume/Render", "Render volume", KeyEvent.VK_R, "onRender");

            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("Help/About...", "Program info", KeyEvent.VK_A, "onAbout");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        //endregion

        //region addToolBarButton
        addToolBarButton("File/New", "New.png");
        addToolBarButton("File/Open...", "Open.png");
        addToolBarButton("File/Save as...", "Save.png");
        addToolBarSeparator();
        selectButton = addToolBarButton("Edit/Select", "Select.png");
        filterButtons.add(addToolBarButton("Edit/Invert", "Invert.png"));
        filterButtons.add(addToolBarButton("Edit/Black & white", "BlackWhite.png"));
        filterButtons.add(addToolBarButton("Edit/Floyd-Steinberg", "Floyd.png"));
        filterButtons.add(addToolBarButton("Edit/Ordered dithering", "Ordered.png"));
        filterButtons.add(addToolBarButton("Edit/Sharpen", "Sharpen.png"));
        filterButtons.add(addToolBarButton("Edit/Stamping", "Stamping.png"));
        filterButtons.add(addToolBarButton("Edit/WaterColor", "Water.png"));
        filterButtons.add(addToolBarButton("Edit/Gamma", "Gamma.png"));
        filterButtons.add(addToolBarButton("Edit/Gauss", "Gauss.png"));
        filterButtons.add(addToolBarButton("Edit/Sobel", "Sobel.png"));
        filterButtons.add(addToolBarButton("Edit/Roberts", "Roberts.png"));
        addToolBarSeparator();
        filterButtons.add(addToolBarButton("View/C --> B", "CtoB.png"));
        filterButtons.add(addToolBarButton("View/B --> C", "BtoC.png"));
        filterButtons.add(addToolBarButton("View/Zoom", "Zoom.png"));
        filterButtons.add(addToolBarButton("View/Rotate", "Rotate.png"));
        addToolBarSeparator();
        addToolBarButton("Volume/Open config", "Config.png");
        addToolBarButton("Volume/Absorption", "Absorption.png");
        addToolBarButton("Volume/Emission", "Emission.png");
        renderButton = addToolBarButton("Volume/Render", "Render.png");
        addToolBarSeparator();
        addToolBarButton("Help/About...", "About.png");
        //endregion

        selectButton.setEnabled(false);
        renderButton.setEnabled(false);
        for (JButton btn : filterButtons)
            btn.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(imageZones);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(1200, 500));

        add(scrollPane);
        pack();
    }

    public void onNew() {
        imageZones.clearZones();
        selectButton.setEnabled(false);
        for (JButton btn : filterButtons)
            btn.setEnabled(false);
    }

    public void onOpen() {
        try {
            loadFile();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "File format is unfamiliar", "Wrong file", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onSaveAs() {
        saveFile();
    }

    public void onExit() {
        System.exit(0);
    }

    public void onSelect() {
        if (!imageZones.getZoneA().isSelectShown()) imageZones.getZoneA().setSelectShown(true);
        else imageZones.getZoneA().setSelectShown(false);

        for (JButton btn : filterButtons) btn.setEnabled(true);
    }

    public void onInvert() {
        imageZones.applyFilter(new InversionFilter());
    }

    public void onBlackWhite() {
        imageZones.applyFilter(new BlackWhiteFilter());
    }

    public void onFS() {
        JDialog frame = new JDialog(this, "RGB", true);
        frame.setPreferredSize(new Dimension(250, 150));
        frame.setResizable(false);
        JPanel panel = new JPanel(new GridLayout(1, 2));

        JPanel fields = new JPanel(new GridLayout(3, 2));
        JPanel button = new JPanel();

        JLabel rLabel = new JLabel("R:");
        JLabel gLabel = new JLabel("G:");
        JLabel bLabel = new JLabel("B:");

        JTextField rField = new JTextField();
        JTextField gField = new JTextField();
        JTextField bField = new JTextField();

        rField.setText(String.valueOf(r));
        gField.setText(String.valueOf(g));
        bField.setText(String.valueOf(b));

        JButton okButton = new JButton("OK");

        fields.add(rLabel);
        fields.add(rField);
        fields.add(gLabel);
        fields.add(gField);
        fields.add(bLabel);
        fields.add(bField);

        button.add(okButton);

        panel.add(fields);
        panel.add(button);

        okButton.addActionListener(e -> {
            try {
                r = Integer.parseInt(rField.getText());
                g = Integer.parseInt(gField.getText());
                b = Integer.parseInt(bField.getText());
            } catch (NumberFormatException ex) {
                r = g = b = 2;
            }

            imageZones.applyFilter(new FloydDithering(r, g, b));

            frame.setVisible(false);
            frame.dispose();
        });

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    public void onOrdered() {
        imageZones.applyFilter(new OrderedDithering(16, 2, 2, 2));
    }

    public void onSharpen() {
        imageZones.applyFilter(new SharpenFilter());
    }

    public void onStamping() {
        imageZones.applyFilter(new StampingFilter());
    }

    public void onWaterColor() {
        imageZones.applyFilter(new WaterColorFilter());
    }

    public void onGamma() {
        JDialog frame = new JDialog(this, "Choose gamma", true);
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JPanel valuePanel = new JPanel(new GridLayout(2, 2));
        JSlider valueSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, gamma);
        JTextField valueField = new JTextField(0);
        valueField.setText(String.valueOf(gamma));
        valuePanel.add(valueSlider);
        valuePanel.add(valueField);

        valueSlider.addChangeListener(e -> {
            int val = ((JSlider) e.getSource()).getValue();
            valueField.setText(String.valueOf(val));
        });
        valueField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!valueField.getText().isEmpty())
                    try {
                        valueSlider.setValue(Integer.parseInt(valueField.getText()));
                    } catch (NumberFormatException ex) {
                        valueSlider.setValue(0);
                    }
            }
        });

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            try {
                gamma = Integer.parseInt(valueField.getText());
            } catch (NumberFormatException ex) {
                gamma = 0;
            }

            imageZones.applyFilter(new Gamma(gamma));

            frame.setVisible(false);
            frame.dispose();
        });

        valuePanel.add(okButton);
        panel.add(new JLabel("Gamma", SwingConstants.CENTER));
        panel.add(valuePanel);

        frame.add(panel);
        frame.setPreferredSize(new Dimension(200, 200));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    public void onGauss() {
        imageZones.applyFilter(new GaussFilter());
    }

    public void onSobel() {
        initThresholdDialog(true);
    }

    public void onRoberts() {
        initThresholdDialog(false);
    }

    private void initThresholdDialog(boolean isSobel) {
        String title = (isSobel ? "Sobel" : "Roberts") + " threshold";
        JDialog thresholdDialog = new JDialog(this, title, true);
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JPanel valuePanel = new JPanel(new GridLayout(2, 2));
        JTextField valueField = new JTextField(0);
        JSlider valueSlider;
        if (isSobel) {
            valueField.setText(String.valueOf(sobel));
            valueSlider = new JSlider(JSlider.HORIZONTAL, 1, 500, sobel);
        } else {
            valueField.setText(String.valueOf(roberts));
            valueSlider = new JSlider(JSlider.HORIZONTAL, 1, 500, roberts);
        }
        valuePanel.add(valueSlider);
        valuePanel.add(valueField);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            int val;
            try {
                val = Integer.parseInt(valueField.getText());
                if (isSobel) {
                    sobel = val;
                    imageZones.applyFilter(new Sobel(val));
                } else {
                    roberts = val;
                    imageZones.applyFilter(new Roberts(val));
                }
            } catch (NumberFormatException ignored) {
            }

            thresholdDialog.setVisible(false);
            thresholdDialog.dispose();
        });

        valuePanel.add(okButton);

        panel.add(new JLabel("Threshold", SwingConstants.CENTER));
        panel.add(valuePanel);
        valueSlider.addChangeListener(e -> {
            try {
                int val = ((JSlider) e.getSource()).getValue();
                valueField.setText(String.valueOf(val));
            } catch (NumberFormatException ignored) {
            }
        });
        valueField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!valueField.getText().isEmpty())
                    valueSlider.setValue(Integer.parseInt(valueField.getText()));
            }
        });

        thresholdDialog.add(panel);
        thresholdDialog.setPreferredSize(new Dimension(200, 200));
        thresholdDialog.setResizable(false);
        thresholdDialog.pack();
        thresholdDialog.setLocationRelativeTo(this);
        thresholdDialog.setVisible(true);
    }

    public void onBC() {
        imageZones.copyBC();
    }

    public void onCB() {
        imageZones.copyCB();
    }

    public void onZoom() {
        imageZones.applyFilter(new Zoom());
    }

    public void onRotate() {
        JDialog frame = new JDialog(this, "Choose angle", true);
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JPanel valuePanel = new JPanel(new GridLayout(2, 2));
        JSlider valueSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, angle);
        JTextField valueField = new JTextField(0);
        valueField.setText(String.valueOf(angle));
        valuePanel.add(valueSlider);
        valuePanel.add(valueField);

        valueSlider.addChangeListener(e -> {
            int val = ((JSlider) e.getSource()).getValue();
            valueField.setText(String.valueOf(val));
            imageZones.applyFilter(new Rotate(val));
        });
        valueField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!valueField.getText().isEmpty())
                    try {
                        valueSlider.setValue(Integer.parseInt(valueField.getText()));
                    } catch (NumberFormatException ex) {
                        valueSlider.setValue(0);
                    }
            }
        });

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            try {
                angle = Integer.parseInt(valueField.getText());
            } catch (NumberFormatException ex) {
                angle = 0;
            }

            imageZones.applyFilter(new Rotate(angle));

            frame.setVisible(false);
            frame.dispose();
        });

        valuePanel.add(okButton);
        panel.add(new JLabel("Angle", SwingConstants.CENTER));
        panel.add(valuePanel);

        frame.add(panel);
        frame.setPreferredSize(new Dimension(200, 200));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    public void onToolBar() {
        if (this.toolBar.isShowing()) this.toolBar.setVisible(false);
        else this.toolBar.setVisible(true);
    }

    public void onStatusBar() {
        if (this.statusBar.isVisible())
            this.statusBar.setVisible(false);
        else
            this.statusBar.setVisible(true);
    }

    public void onConfig() {    //TODO
        //...........//
        renderButton.setEnabled(true);  //after success
    }

    public void onAbsorption() {
    }   //TODO

    public void onEmission() {      //TODO
    }

    public void onRender() {
        JDialog frame = new JDialog(this, "VR params", true);
        frame.setPreferredSize(new Dimension(250, 150));
        frame.setResizable(false);
        JPanel panel = new JPanel(new GridLayout(1, 2));

        JPanel fields = new JPanel(new GridLayout(3, 2));
        JPanel button = new JPanel();

        JLabel xLabel = new JLabel("X:");
        JLabel yLabel = new JLabel("Y:");
        JLabel zLabel = new JLabel("Z:");

        JTextField xField = new JTextField();
        JTextField yField = new JTextField();
        JTextField zField = new JTextField();

        xField.setText(String.valueOf(x));
        yField.setText(String.valueOf(y));
        zField.setText(String.valueOf(z));

        JToggleButton okButton = new JToggleButton("OK");

        fields.add(xLabel);
        fields.add(xField);
        fields.add(yLabel);
        fields.add(yField);
        fields.add(zLabel);
        fields.add(zField);

        button.add(okButton);

        panel.add(fields);
        panel.add(button);

        okButton.addActionListener(e -> {
            try {
                x = Integer.parseInt(xField.getText());
                y = Integer.parseInt(yField.getText());
                z = Integer.parseInt(zField.getText());
                if (x > 350 || y > 350 || z > 350 || x < 1 || y < 1 || z < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Values should be numbers between 1 and 350", "Wrong input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cube cube = new Cube(x, y, z);
            cube.startRendering();  //TODO;
            frame.setVisible(false);
            frame.dispose();
        });

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    public void onAbout() {
        JOptionPane.showMessageDialog(this, "Init, version 1.0\nCopyright (c) 2019 Anton Razumov, FIT, group 16203", "About Init", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveFile() {
        String fileName;
        File file = new File("file" + java.time.LocalDate.now() + ".bmp");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(getDataDirectory());
        fileChooser.setSelectedFile(file);
        int res = fileChooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            fileName = file.getAbsolutePath();
            if (!fileName.endsWith(".bmp")) {
                file = new File(fileName + ".bmp");
            }
        } else return;

        imageZones.getZoneC().saveImage(file);
    }

    private void loadFile() {
        File file;
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "bmp", "png");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(getDataDirectory());

        int ret = fileChooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            dataDirectory = file;
        } else return;

        try {
            imageZones.loadPicture(file);
        } catch (IOException e) {
            loadFile();
        }
        selectButton.setEnabled(true);
    }

    /*Based on same name method from project from nsucgcourse.github.io*/
    private File getDataDirectory() {
        if (dataDirectory == null) {
            dataDirectory = new File("../").getParentFile();
            if (dataDirectory == null || !dataDirectory.exists()) dataDirectory = new File(".");
            for (File f : Objects.requireNonNull(dataDirectory.listFiles())) {
                if (f.isDirectory() && f.getName().endsWith("Data")) {
                    dataDirectory = f;
                    break;
                }
            }
        }
        return dataDirectory;
    }


}
