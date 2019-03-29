package ru.nsu.fit.g16203.razumov.window;

import ru.nsu.fit.g16203.razumov.filters.*;
import ru.nsu.fit.g16203.razumov.panels.ZonesPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Objects;

public class MainWindow extends MainFrame {
    private ZonesPanel imageZones;

    private static final String READY = "Ready", PAUSED = "Paused", RUNNING = "Running...";     //status bar values

    private JPanel statusBar;
    private JLabel status;
    private File dataDirectory;

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
        addToolBarButton("Edit/Select", "Select.png");
        addToolBarButton("Edit/Invert", "Invert.png");
        addToolBarButton("Edit/Black & white", "BlackWhite.png");
        addToolBarButton("Edit/Floyd-Steinberg", "Floyd.png");
        addToolBarButton("Edit/Ordered dithering", "Ordered.png");
        addToolBarButton("Edit/Sharpen", "Sharpen.png");
        addToolBarButton("Edit/Stamping", "Stamping.png");
        addToolBarButton("Edit/WaterColor", "Water.png");
        addToolBarButton("Edit/Gamma", "Gamma.png");
        addToolBarButton("Edit/Gauss", "Gauss.png");
        addToolBarButton("Edit/Sobel", "Sobel.png");
        addToolBarButton("Edit/Roberts", "Roberts.png");
        addToolBarSeparator();
        addToolBarButton("View/C --> B", "CtoB.png");
        addToolBarButton("View/B --> C", "BtoC.png");
        addToolBarButton("View/Zoom", "Zoom.png");
        addToolBarButton("View/Rotate", "Rotate.png");
        addToolBarSeparator();
        addToolBarButton("Help/About...", "About.png");
        //endregion

        JScrollPane scrollPane = new JScrollPane(imageZones);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(1200, 500));

        add(scrollPane);
        pack();
    }

    public void onNew() {
        imageZones.clearZones();
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
    }

    public void onInvert() {
        imageZones.applyFilter(new InversionFilter());
    }

    public void onBlackWhite() {
        imageZones.applyFilter(new BlackWhiteFilter());
    }

    public void onFS() {
        initRGBDialog();
        imageZones.applyFilter(new FloydDithering(2, 2, 2));
    }

    public void onOrdered() {
        imageZones.applyFilter(new OrderedDithering(16, 2, 2, 2));
    }

    private JDialog initRGBDialog() {
        JDialog rgbDialog = new JDialog(this, "Enter RGB", true);
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JPanel valuesPanel = new JPanel(new GridLayout(3, 2));

        JLabel rLabel = new JLabel("R:");
        JTextField rField = new JTextField(0);
        rField.setText("2");
        valuesPanel.add(rLabel);
        valuesPanel.add(rField);

        JLabel gLabel = new JLabel("G:");
        JTextField gField = new JTextField(0);
        gField.setText("2");
        valuesPanel.add(gLabel);
        valuesPanel.add(gField);

        JLabel bLabel = new JLabel("B:");
        JTextField bField = new JTextField(0);
        bField.setText("2");
        valuesPanel.add(bLabel);
        valuesPanel.add(bField);

        panel.add(valuesPanel);
        JButton okButton = new JButton("OK");
        panel.add(okButton, SwingConstants.CENTER);
        rgbDialog.add(panel);

        //TODO: listeners

        return rgbDialog;
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
        JDialog gammaDialog = new JDialog(this, "Enter coefficient", true);
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JSlider valueSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        panel.add(new JLabel("Gamma coefficient", SwingConstants.CENTER));
        panel.add(valueSlider);
        valueSlider.addChangeListener(e -> imageZones.applyFilter(new Gamma(((JSlider) e.getSource()).getValue())));

        gammaDialog.add(panel);
        gammaDialog.setPreferredSize(new Dimension(200, 100));
        gammaDialog.setResizable(false);
        gammaDialog.pack();
        gammaDialog.setLocationRelativeTo(this);
        gammaDialog.setVisible(true);
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
        JPanel valuePanel = new JPanel(new GridLayout(1, 2));
        JSlider valueSlider = new JSlider(JSlider.HORIZONTAL, 1, 500, 100);
        JTextField valueField = new JTextField(0);
        valueField.setText("100");
        valuePanel.add(valueSlider);
        valuePanel.add(valueField);
        panel.add(new JLabel("Threshold", SwingConstants.CENTER));
        panel.add(valuePanel);
        valueSlider.addChangeListener(e -> {
            try {
                int val = ((JSlider) e.getSource()).getValue();
                valueField.setText(String.valueOf(val));
                if (isSobel) imageZones.applyFilter(new Sobel(val));
                else imageZones.applyFilter(new Roberts(val));
            } catch (NumberFormatException ex) {
                if (isSobel) imageZones.applyFilter(new Sobel(200));
                else imageZones.applyFilter(new Roberts(50));
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
        thresholdDialog.setPreferredSize(new Dimension(200, 100));
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
        JDialog rotateDialog = new JDialog(this, "Choose angle", true);
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JPanel valuePanel = new JPanel(new GridLayout(1, 2));
        JSlider valueSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
        JTextField valueField = new JTextField(0);
        valueField.setText("0");
        valuePanel.add(valueSlider);
        valuePanel.add(valueField);
        panel.add(new JLabel("Angle", SwingConstants.CENTER));
        panel.add(valuePanel);
        valueSlider.addChangeListener(e -> {
            int val = ((JSlider) e.getSource()).getValue();
            valueField.setText(String.valueOf(val));
            imageZones.applyFilter(new Rotate(val));
        });
        valueField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!valueField.getText().isEmpty())
                    valueSlider.setValue(Integer.parseInt(valueField.getText()));
            }
        });

        rotateDialog.add(panel);
        rotateDialog.setPreferredSize(new Dimension(200, 100));
        rotateDialog.setResizable(false);
        rotateDialog.pack();
        rotateDialog.setLocationRelativeTo(this);
        rotateDialog.setVisible(true);
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
