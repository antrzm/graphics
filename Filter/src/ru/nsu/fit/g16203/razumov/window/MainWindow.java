package ru.nsu.fit.g16203.razumov.window;

import ru.nsu.fit.g16203.razumov.filters.BlackWhiteFilter;
import ru.nsu.fit.g16203.razumov.filters.InversionFilter;
import ru.nsu.fit.g16203.razumov.filters.SharpingFilter;
import ru.nsu.fit.g16203.razumov.filters.StampingFilter;
import ru.nsu.fit.g16203.razumov.panels.ZonesPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Objects;

public class MainWindow extends MainFrame {
    private ZonesPanel zones;

    private static final String READY = "Ready", PAUSED = "Paused", RUNNING = "Running...";     //status bar values

    private JPanel statusBar;
    private JLabel status;
    private File dataDirectory;

    public MainWindow() {
        super(1200, 600, "Filter");
        zones = new ZonesPanel();
        statusBar = new JPanel();

        statusBar = new JPanel();
        status = new JLabel(READY);
        statusBar.add(status);
        add(statusBar, BorderLayout.PAGE_END);
        add(zones);

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
            addMenuItem("Edit/B --> C", "Copy B to C", KeyEvent.VK_C, "onBC");
            addMenuItem("Edit/C --> B", "Copy C to B", KeyEvent.VK_B, "onCB");

            addSubMenu("View", KeyEvent.VK_V);
            addMenuItem("View/Toolbar", "Toolbar", KeyEvent.VK_T, "onToolBar");
            addMenuItem("View/Status bar", "Status bar", KeyEvent.VK_S, "onStatusBar");

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
        addToolBarSeparator();
        addToolBarButton("Edit/C --> B", "CtoB.png");
        addToolBarButton("Edit/B --> C", "BtoC.png");
        addToolBarSeparator();
        addToolBarButton("Help/About...", "About.png");
        //endregion
    }

    public void onNew() {
        zones.clearZones();
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

    }  //TODO

    public void onInvert() {
        zones.applyFilter(new InversionFilter());
    }

    public void onBlackWhite() {
        zones.applyFilter(new BlackWhiteFilter());
    }

    public void onBC() {
        zones.copyBC();
    }

    public void onCB() {
        zones.copyCB();
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

        zones.getZoneC().saveImage(file);
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

        zones.loadPicture(file);
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
