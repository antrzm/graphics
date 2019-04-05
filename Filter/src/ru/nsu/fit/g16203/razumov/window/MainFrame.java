package ru.nsu.fit.g16203.razumov.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JMenuBar menuBar;
    JToolBar toolBar;

    private MainFrame() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ignored) {
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("MainWindow toolbar");
        toolBar.setRollover(true);
        add(toolBar, BorderLayout.PAGE_START);
    }

    MainFrame(int x, int y, String title) {
        this();
        setSize(x, y);
        setLocationByPlatform(true);
        setTitle(title);
    }

    private JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String icon, String actionMethod) throws SecurityException, NoSuchMethodException {
        JMenuItem item = new JMenuItem(title);
        //if (mnemonic != -1)
        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);
        if (icon != null)
            item.setIcon(new ImageIcon(getClass().getResource("../resources/" + icon), title));
        final Method method = getClass().getMethod(actionMethod);
        item.addActionListener(evt -> {
            try {
                method.invoke(MainFrame.this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return item;
    }

    //if no icon
    public JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String actionMethod) throws SecurityException, NoSuchMethodException {
        return createMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }

    private JMenu createSubMenu(String title, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    void addSubMenu(String title, int mnemonic) {
        MenuElement element = getParentMenuElement(title);
        if (element == null)
            throw new InvalidParameterException("Menu path not found: " + title);
        JMenu subMenu = createSubMenu(getMenuPathName(title), mnemonic);
        if (element instanceof JMenuBar)
            ((JMenuBar) element).add(subMenu);
        else if (element instanceof JMenu)
            ((JMenu) element).add(subMenu);
        else if (element instanceof JPopupMenu)
            ((JPopupMenu) element).add(subMenu);
        else
            throw new InvalidParameterException("Invalid menu path: " + title);
    }

    void addMenuItem(String title, String tooltip, int mnemonic, String icon, String actionMethod) throws SecurityException, NoSuchMethodException {
        MenuElement element = getParentMenuElement(title);
        if (element == null)
            throw new InvalidParameterException("Menu path not found: " + title);
        JMenuItem item = createMenuItem(getMenuPathName(title), tooltip, mnemonic, icon, actionMethod);
        if (element instanceof JMenu)
            ((JMenu) element).add(item);
        else if (element instanceof JPopupMenu)
            ((JPopupMenu) element).add(item);
        else
            throw new InvalidParameterException("Invalid menu path: " + title);
    }

    //if no icon
    public void addMenuItem(String title, String tooltip, int mnemonic, String actionMethod) throws SecurityException, NoSuchMethodException {
        addMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }

    //if no mnemonic and icon
    public void addMenuItem(String title, String tooltip, String actionMethod) throws SecurityException, NoSuchMethodException {
        addMenuItem(title, tooltip, -1, null, actionMethod);
    }

    public void addMenuSeparator(String title) {
        MenuElement element = getMenuElement(title);
        if (element == null)
            throw new InvalidParameterException("Menu path not found: " + title);
        if (element instanceof JMenu)
            ((JMenu) element).addSeparator();
        else if (element instanceof JPopupMenu)
            ((JPopupMenu) element).addSeparator();
        else
            throw new InvalidParameterException("Invalid menu path: " + title);
    }

    private String getMenuPathName(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if (pos > 0)
            return menuPath.substring(pos + 1);
        else
            return menuPath;
    }

    private MenuElement getParentMenuElement(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if (pos > 0)
            return getMenuElement(menuPath.substring(0, pos));
        else
            return menuBar;
    }

    MenuElement getMenuElement(String menuPath) {
        MenuElement element = menuBar;
        for (String pathElement : menuPath.split("/")) {
            MenuElement newElement = null;
            for (MenuElement subElement : element.getSubElements()) {
                if ((subElement instanceof JMenu && ((JMenu) subElement).getText().equals(pathElement))
                        || (subElement instanceof JMenuItem && ((JMenuItem) subElement).getText().equals(pathElement))) {
                    if (subElement.getSubElements().length == 1 && subElement.getSubElements()[0] instanceof JPopupMenu)
                        newElement = subElement.getSubElements()[0];
                    else
                        newElement = subElement;
                    break;
                }
            }
            if (newElement == null) return null;
            element = newElement;
        }
        return element;
    }

    public JButton createToolBarButton(JMenuItem item, String icon) {
        Icon iconForButton = item.getIcon();
        if (iconForButton == null && icon != null)
            iconForButton = new ImageIcon(getClass().getResource("../resources/" + icon));
        JButton button = new JButton(iconForButton);
        for (ActionListener listener : item.getActionListeners())
            button.addActionListener(listener);
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    public JToggleButton createToolBarToggleButton(JMenuItem item, String icon) {
        Icon iconForButton = item.getIcon();
        if (iconForButton == null && icon != null)
            iconForButton = new ImageIcon(getClass().getResource("../resources/" + icon));
        JToggleButton button = new JToggleButton(iconForButton);
        for (ActionListener listener : item.getActionListeners())
            button.addActionListener(listener);
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    private JButton createToolBarButton(String menuPath, String icon) {
        JMenuItem item = (JMenuItem) getMenuElement(menuPath);
        if (item == null)
            throw new InvalidParameterException("Menu path not found: " + menuPath);
        return createToolBarButton(item, icon);
    }

    private JToggleButton createToolBarToggleButton(String menuPath, String icon) {
        JMenuItem item = (JMenuItem) getMenuElement(menuPath);
        if (item == null)
            throw new InvalidParameterException("Menu path not found: " + menuPath);
        return createToolBarToggleButton(item, icon);
    }

    protected JButton addToolBarButton(String menuPath, String icon) {
        JButton toolBarButton = createToolBarButton(menuPath, icon);
        toolBar.add(toolBarButton);
        return toolBarButton;
    }

    protected JToggleButton addToolBarToggleButton(String menuPath, String icon) {
        JToggleButton toolBarButton = createToolBarToggleButton(menuPath, icon);
        toolBar.add(toolBarButton);
        return toolBarButton;
    }

    public void addToolBarButton(String menuPath) {
        addToolBarButton(menuPath, null);
    }


    protected void addToolBarSeparator() {
        toolBar.addSeparator();
    }
}
