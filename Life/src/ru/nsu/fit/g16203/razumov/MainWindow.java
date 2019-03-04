package ru.nsu.fit.g16203.razumov;

import ru.nsu.fit.g16203.razumov.graphics.HexagonGrid;
import ru.nsu.fit.g16203.razumov.window.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainWindow extends MainFrame {

    private JLabel status;

    private HexagonGrid hexagonGrid;

    private MainWindow() {
        super(800, 600, "Conway's Game of Life");

        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/New", "New file", KeyEvent.VK_N, "onNew");
            addMenuItem("File/Open...", "Open file", KeyEvent.VK_O, "onOpen");
            addMenuItem("File/Save", "Save file", KeyEvent.VK_S, "onSave");
            addMenuItem("File/Save as...", "Save file as", "onSaveAs");
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_E, "Exit.png", "onExit");

            addSubMenu("Modify", KeyEvent.VK_M);
            addMenuItem("Modify/Options", "Options", KeyEvent.VK_O, "onOptions");
            addMenuItem("Modify/Replace", "Replace", KeyEvent.VK_R, "onReplace");
            addMenuItem("Modify/Xor", "XOR", KeyEvent.VK_X, "onXor");
            addMenuItem("Modify/Impact", "Impact", KeyEvent.VK_I, "onImpact");
            addMenuItem("Modify/Colors", "Colors", KeyEvent.VK_C, "onColors");

            addSubMenu("Action", KeyEvent.VK_A);
            addMenuItem("Action/Init", "Init", KeyEvent.VK_I, "onInit");
            addMenuItem("Action/Next", "Next step", KeyEvent.VK_N, "onNext");
            addMenuItem("Action/Run", "Run/Pause", KeyEvent.VK_R, "onRun");

            addSubMenu("View", KeyEvent.VK_V);
            addMenuItem("View/Toolbar", "Toolbar", KeyEvent.VK_T, "onToolBar");
            addMenuItem("View/Status bar", "Status bar", KeyEvent.VK_S, "onStatusBar");

            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("Help/About...", "Program info", KeyEvent.VK_A, "onAbout");

            addToolBarButton("File/New", "New.png");
            addToolBarButton("File/Open...", "Open.png");
            addToolBarButton("File/Save", "Save.png");
            addToolBarSeparator();
            addToolBarButton("Modify/Options", "Options.png");
            addToolBarButton("Modify/Replace", "Replace.png");
            addToolBarButton("Modify/Xor", "Xor.png");
            addToolBarButton("Modify/Impact", "Impact.png");
            addToolBarButton("Modify/Colors", "Colors.png");
            addToolBarSeparator();
            addToolBarButton("Action/Init", "Init.png");
            addToolBarButton("Action/Next", "Next.png");
            addToolBarButton("Action/Run", "Run.png");
            addToolBarSeparator();
            addToolBarButton("Help/About...", "About.png");

            hexagonGrid = new HexagonGrid(10, 10);
            add(hexagonGrid);

            JPanel statusBar = new JPanel();
            status = new JLabel("Ready");
            statusBar.add(status);
            add(statusBar, BorderLayout.PAGE_END);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        setSize(hexagonGrid.width, hexagonGrid.height);
    }

    public void onNew() {       //TODO
        status.setText("New file");
    }

    public void onOpen() {      //TODO
        status.setText("New file");
    }

    public void onSave() {      //TODO
    }

    public void onSaveAs() {    //TODO
    }

    public void onExit() {
        System.exit(0);
    }

    public void onOptions() {   //TODO
    }

    public void onReplace() {
        hexagonGrid.setReplaceMode();
    }

    public void onXor() {
        hexagonGrid.setXorMode();
    }

    public void onImpact() {   //TODO
    }

    public void onColors() {   //TODO
    }

    public void onInit() {
        hexagonGrid.init();
    }

    public void onNext() {   //TODO
    }

    public void onRun() {   //TODO
        status.setText("Running...");
    }

    public void onToolBar() {   //TODO
    }

    public void onStatusBar() {   //TODO
    }

    public void onAbout() {         //TODO: change info
        JOptionPane.showMessageDialog(this, "Init, version 1.0\nCopyright (c) 2019 Anton Razumov, FIT, group 16203", "About Init", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }

}
