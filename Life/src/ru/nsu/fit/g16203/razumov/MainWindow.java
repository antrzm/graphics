package ru.nsu.fit.g16203.razumov;

import ru.nsu.fit.g16203.razumov.graphics.HexagonGrid;
import ru.nsu.fit.g16203.razumov.window.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.List;
import java.util.Objects;

public class MainWindow extends MainFrame {

    private JLabel status;

    private static final String READY = "Ready", PAUSED = "Paused", RUNNING = "Running...";

    private HexagonGrid hexagonGrid;

    private JPanel statusBar;
    private JDialog dialog;
    private JScrollPane scrollPane;

    private File dataDirectory;

    private MainWindow() {
        super(800, 600, "Conway's Game of Life");

        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/New", "Create new file", KeyEvent.VK_N, "onNew");
            addMenuItem("File/Open...", "Open an existing file", KeyEvent.VK_O, "onOpen");
            addMenuItem("File/Save as...", "Save active file as", "onSaveAs");
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_E, "Exit.png", "onExit");

            addSubMenu("Modify", KeyEvent.VK_M);
            addMenuItem("Modify/Options", "Options", KeyEvent.VK_O, "onOptions");
            addMenuItem("Modify/Replace", "Replace mode", KeyEvent.VK_R, "onReplace");
            addMenuItem("Modify/Xor", "XOR mode", KeyEvent.VK_X, "onXor");
            addMenuItem("Modify/Impact", "Show/hide impacts", KeyEvent.VK_I, "onImpact");

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
            addToolBarButton("File/Save as...", "Save.png");
            addToolBarSeparator();
            addToolBarButton("Modify/Options", "Options.png");
            addToolBarButton("Modify/Replace", "Replace.png");
            addToolBarButton("Modify/Xor", "Xor.png");
            addToolBarButton("Modify/Impact", "Impact.png");
            addToolBarSeparator();
            addToolBarButton("Action/Init", "Init.png");
            addToolBarButton("Action/Next", "Next.png");
            addToolBarButton("Action/Run", "Run.png");
            addToolBarSeparator();
            addToolBarButton("Help/About...", "About.png");

            hexagonGrid = new HexagonGrid(15, 10);
            add(hexagonGrid);

            statusBar = new JPanel();
            status = new JLabel(READY);
            statusBar.add(status);
            add(statusBar, BorderLayout.PAGE_END);

            scrollPane = new JScrollPane(hexagonGrid, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setPreferredSize(new Dimension(800, 600));

            hexagonGrid.setPreferredSize(new Dimension(hexagonGrid.width, hexagonGrid.height));

            add(scrollPane);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
                status.setText(READY);
            }
        };

        for (Component comp : this.toolBar.getComponents()) {
            comp.addMouseListener(ml);
        }

        dialog = new JDialog(this, "Options", true);
        hexagonGrid.initDialog(dialog);
        dialog.setLocationRelativeTo(this);
        pack();
    }

    public void onNew() {
        if (hexagonGrid.isChanged) {
            int res = JOptionPane.showConfirmDialog(this, "Save game?", "Save", JOptionPane.YES_NO_CANCEL_OPTION);
            if (res == JOptionPane.YES_OPTION) saveFile();
        }
        if (hexagonGrid.isRunning) hexagonGrid.switchRun();
        hexagonGrid.setN(15);
        hexagonGrid.setM(10);
        hexagonGrid.setHexSize(30);
        hexagonGrid.setThickness(1);
        hexagonGrid.setImpactShown(false);
        hexagonGrid.initGrid();
        hexagonGrid.setPreferredSize(new Dimension(hexagonGrid.width, hexagonGrid.height));
        dialog.setVisible(true);
    }

    public void onOpen() {
        hexagonGrid.isRunning = false;
        if (hexagonGrid.isChanged) {
            int res = JOptionPane.showConfirmDialog(this, "Save game?", "Save", JOptionPane.YES_NO_CANCEL_OPTION);
            if (res == JOptionPane.YES_OPTION) saveFile();
        }
        try {
            loadFile();
            hexagonGrid.initDialog(dialog);
            hexagonGrid.setPreferredSize(new Dimension(hexagonGrid.width, hexagonGrid.height));
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

    public void onOptions() {
        dialog.setVisible(true);
    }

    public void onReplace() {
        hexagonGrid.setReplaceMode();
    }

    public void onXor() {
        hexagonGrid.setXorMode();
    }

    public void onImpact() {
        hexagonGrid.switchImpact();
    }

    public void onInit() {
        hexagonGrid.reset();
    }

    public void onNext() {
        if (!status.getText().equals(RUNNING))
            hexagonGrid.nextStep();
    }

    public void onRun() {
        if (status.getText().equals(RUNNING)) status.setText(PAUSED);
        else status.setText(RUNNING);
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

    private void saveFile() {
        int n = hexagonGrid.getN();
        int m = hexagonGrid.getM();
        int size = hexagonGrid.getHexSize();
        int thickness = hexagonGrid.getThickness();

        List<Point> aliveCells = hexagonGrid.getAliveGrid();
        String fileName;
        File file = new File("file" + java.time.LocalDate.now() + ".txt");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(file);
        int res = fileChooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            fileName = file.getAbsolutePath();
            if (!fileName.endsWith(".txt")) {
                file = new File(fileName + ".txt");
            }
        } else return;

        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(n + " " + m + "\n");
            writer.write(thickness + "\n");
            writer.write(size + " " + "\n");
            writer.write(aliveCells.size() + "\n");
            for (Point cell : aliveCells) {
                writer.write(cell.x + " " + cell.y + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        String line;
        int[] params = new int[5];
        int i = 0;
        int n, m, size, thick;
        int cellNum;
        File file;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(getDataDirectory());

        int ret = fileChooser.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            dataDirectory = file;
        } else return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null && i < 5) {
                if (line.contains("/")) {
                    line = line.substring(0, line.indexOf("/"));
                }

                String[] split = line.split(" ");
                for (String s : split) {
                    params[i] = Integer.parseInt(s);
                    i++;
                }
            }

            n = params[0];
            m = params[1];
            thick = params[2];
            size = params[3];
            cellNum = params[4];

            hexagonGrid.setN(n);
            hexagonGrid.setM(m);
            hexagonGrid.setHexSize(size);
            hexagonGrid.setThickness(thick);
            hexagonGrid.setImpactShown(false);

            hexagonGrid.initGrid();

            for (int j = 0; j < cellNum; j++) {
                if (line != null) {
                    if (line.contains("/")) {
                        line = line.substring(0, line.indexOf("/"));
                    }
                    line = line.trim();
                    String[] split = line.split(" ");
                    int x = Integer.parseInt(split[0]), y = Integer.parseInt(split[1]);
                    if (x >= n || y >= m) {
                        JOptionPane.showMessageDialog(this, "Wrong file params", "File Error", JOptionPane.ERROR_MESSAGE);
                    }
                    hexagonGrid.setAlive(hexagonGrid.grid[x][y], false);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }

}
