package javavis.jip3d.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javavis.jip3d.base.JIP3DFunctionList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;


public class MenuBarGui3D extends JMenuBar {
	private static final long serialVersionUID = 6182348633458105353L;

    Properties prop;
    Properties paths;

    public JMenuItem openJIP3D;
    public JMenuItem openPoints3D;
    public JMenuItem openFeatures2D;
    public JMenuItem openPlanes3D;
    public JMenuItem openSeq2D;
    public JMenuItem openSeq3D;
    public JMenuItem saveJIP3D;
    public JMenuItem export3D;
    public JMenuItem robot;
    public JMenuItem zenital;
    public JMenuItem changeView3D;


    public MenuBarGui3D(Gui3D gui3D, JIP3DFunctionList funclist, Properties prop) {
    	super();
        this.prop = prop;

    	FileInputStream fis=null;
        paths = new Properties();
        try {
       		fis = new FileInputStream("path.properties");
        	paths.load(fis);
        	fis.close();
        } catch (IOException e) {System.out.println();}

        JMenu menu, submenu;
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        //Build the first menu.
        menu = new JMenu(prop.getProperty("File3D")); //File3D
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "Opciones comunes para manejar archivos 3d");
        add(menu);


        openJIP3D = new JMenuItem(gui3D.getOpenFileAction(prop.getProperty("OpenJIP3D"))); //OpenJIP3D
        menu.add(openJIP3D);

        // submenu abrir
        submenu = new JMenu(prop.getProperty("Open3D")); //Open3D
        submenu.setMnemonic(KeyEvent.VK_F);

        openPoints3D = new JMenuItem(gui3D.getOpenFileAction(prop.getProperty("OpenPoints3D"))); //OpenPoints3D
        openPoints3D.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        submenu.add(openPoints3D);

        openFeatures2D = new JMenuItem(gui3D.getOpenFileAction(prop.getProperty("OpenFeatures2D"))); //OpenFeatures2D
        submenu.add(openFeatures2D);

        openPlanes3D = new JMenuItem(gui3D.getOpenFileAction(prop.getProperty("OpenPlanes3D"))); //OpenPlanes3D
        openPlanes3D.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        submenu.add(openPlanes3D);

        openSeq2D = new JMenuItem(gui3D.getOpenFileAction(prop.getProperty("OpenSeq2D"))); //OpenSeq2D
        submenu.add(openSeq2D);

        openSeq3D = new JMenuItem(gui3D.getOpenFileAction(prop.getProperty("OpenSeq3D"))); //OopenSeq3D
        submenu.add(openSeq3D);
        menu.add(submenu);

        //submenu abrir
      submenu = new JMenu(prop.getProperty("Save3D")); //Save3D
      submenu.setMnemonic(KeyEvent.VK_G);

      saveJIP3D = new JMenuItem(prop.getProperty("SaveJIP3D")); //SaveJIP3D
      saveJIP3D.addActionListener(gui3D.getSaveFileAction(prop.getProperty("SaveJIP3D")));
      submenu.add(saveJIP3D);

      export3D = new JMenuItem(prop.getProperty("Export3D")); //Export3D
      export3D.addActionListener(gui3D.getSaveFileAction(prop.getProperty("Export3D")));
      submenu.add(export3D);

      menu.add(submenu);


        //Build second menu in the menu bar.
        menu = new JMenu(prop.getProperty("view3D")); //view3D
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription(
        		prop.getProperty("view3D")); //ViewDesc

        robot = new JMenuItem(prop.getProperty("Robot")); //Robot
        robot.addActionListener(gui3D.getChangeViewAction(prop.getProperty("Robot")));
        menu.add(robot);

        zenital = new JMenuItem(prop.getProperty("Zenital")); //Zenital
        zenital.addActionListener(gui3D.getChangeViewAction(prop.getProperty("Zenital")));
        menu.add(zenital);


        changeView3D = new JMenuItem(prop.getProperty("ChangeView3D")); //ChangeView3D
        changeView3D.addActionListener(gui3D.getChangeViewAction(prop.getProperty("ChangeView3D")));
        menu.add(changeView3D);

        add(menu);

        add(funclist.getFunctionMenu(prop.getProperty("Func3D"), gui3D.getCanvas())); //Func3D
    }

}
