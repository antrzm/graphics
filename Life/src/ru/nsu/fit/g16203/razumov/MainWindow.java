package ru.nsu.fit.g16203.razumov;

import ru.nsu.fit.g16203.razumov.graphics.HexagonGrid;
import ru.nsu.fit.g16203.razumov.window.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MainWindow extends MainFrame {

    private JLabel status;

    private HexagonGrid hexagonGrid;

    private JPanel statusBar;
    private JDialog dialog;

    private JScrollPane scrollPane;

    private MainWindow() {
        super(800, 600, "Conway's Game of Life");

        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/New", "Create new file", KeyEvent.VK_N, "onNew");
            addMenuItem("File/Open...", "Open an existing file", KeyEvent.VK_O, "onOpen");
            addMenuItem("File/Save", "Save active file", KeyEvent.VK_S, "onSave");
            addMenuItem("File/Save as...", "Save active file as", "onSaveAs");
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_E, "Exit.png", "onExit");

            addSubMenu("Modify", KeyEvent.VK_M);
            addMenuItem("Modify/Options", "Options", KeyEvent.VK_O, "onOptions");
            addMenuItem("Modify/Replace", "Replace mode", KeyEvent.VK_R, "onReplace");
            addMenuItem("Modify/Xor", "XOR mode", KeyEvent.VK_X, "onXor");
            addMenuItem("Modify/Impact", "Show/hide impacts", KeyEvent.VK_I, "onImpact");
            //addMenuItem("Modify/Colors", "Colors", KeyEvent.VK_C, "onColors");

            addSubMenu("Action", KeyEvent.VK_A);
            addMenuItem("Action/Init", "Init field", KeyEvent.VK_I, "onInit");
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
            //addToolBarButton("Modify/Colors", "Colors.png");  //TODO?
            addToolBarSeparator();
            addToolBarButton("Action/Init", "Init.png");
            addToolBarButton("Action/Next", "Next.png");
            addToolBarButton("Action/Run", "Run.png");
            addToolBarSeparator();
            addToolBarButton("Help/About...", "About.png");

            hexagonGrid = new HexagonGrid(15, 10);
            add(hexagonGrid);

            statusBar = new JPanel();
            status = new JLabel("Ready");        //TODO: hardcode as a constant
            statusBar.add(status);
            add(statusBar, BorderLayout.PAGE_END);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.setPreferredSize(new Dimension(hexagonGrid.width - 50, hexagonGrid.height + 50));

        scrollPane = new JScrollPane(hexagonGrid, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);

        add(scrollPane);

        MouseListener ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                try {
                    status.setText(((JButton) e.getSource()).getToolTipText());
                } catch (ClassCastException ignored) {
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                status.setText("Ready");        //TODO: current state of game
            }
        };

        for (Component comp : this.toolBar.getComponents()) {
            comp.addMouseListener(ml);
        }

        dialog = new JDialog(this, "Options", true);
        hexagonGrid.initDialog(dialog);
        dialog.setLocationRelativeTo(this);
    }

    public void onNew() {       //TODO

    }

    public void onOpen() {      //TODO

    }

    public void onSave() {      //TODO
    }

    public void onSaveAs() {    //TODO
    }

    public void onExit() {
        System.exit(0);
    }

    public void onOptions() {   //TODO
        dialog.setVisible(true);
    }

    public void onReplace() {
        hexagonGrid.setReplaceMode();
    }

    public void onXor() {
        hexagonGrid.setXorMode();
    }

    public void onImpact() {
        hexagonGrid.showImpact();
    }

    public void onColors() {   //TODO ?
    }

    public void onInit() {
        hexagonGrid.init();
    }

    public void onNext() {
        if (!status.getText().equals("Running..."))         //TODO: hardcode as a constant
            hexagonGrid.nextStep();
    }

    public void onRun() {
        if (status.getText().equals("Running...")) status.setText("Paused");  //TODO: hardcode as a constant
        else status.setText("Running...");
        hexagonGrid.switchRun();
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

    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }

}
