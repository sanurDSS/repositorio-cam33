package javavis;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import javavis.base.JIPToolkit;
import javavis.desktop.gui.GuiDesktop;
import javavis.desktop.gui.MenuBarDesktop;
import javavis.jip2d.base.*;
import javavis.jip2d.gui.Gui2D;
import javavis.jip2d.gui.MenuBarGui2D;
import javavis.jip3d.base.JIP3DFunctionList;
import javavis.jip3d.gui.Gui3D;
import javavis.jip3d.gui.MenuBarGui3D;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *  Main class of the program, here is where  everything is started, all menus are loaded.
 */
public class Gui extends JFrame implements ChangeListener {
	private static final long serialVersionUID = 5359289125586625385L;

	private static Logger logger = Logger.getLogger(Gui.class);

	/** Object managing the tab panel */
	JTabbedPane tabPane;
	public static final int INDEX2D = 0;
	public static final int INDEXDESKTOP = 1;
	public static final int INDEX3D = 2;
	
	/** Guis for the different guis */
	Gui2D g2d;
	Gui3D g3d;
	GuiDesktop gdesk;

	/** Menu bar for the differents tabs */
	MenuBarGui2D menuBar2D;
	MenuBarDesktop menuBarDesktop;
	MenuBarGui3D menuBar3D;

	/** Property file */
	Properties prop;

	/** Available function list */
	JIPFunctionList funclist2d;

	/** Class constructor. Here is where list of function, and the menu bar, data panel, etc... are created. */
	public Gui() {
		super("JavaVis");
    	FileInputStream fis=null;

        prop = new Properties();
        try {
        	Locale l=Locale.getDefault();
        	if ("es".equalsIgnoreCase(l.getCountry()))
        		fis = new FileInputStream("resources/spanish.properties");
        	else
        		fis = new FileInputStream("resources/english.properties");
        	prop.load(fis);
        	fis.close();
        } catch (IOException e) {
        	System.out.println("Properties file not found");
        	logger.error("Properties file not found");
        	try{if (fis!=null) fis.close();}catch(IOException ex){}
        }
		funclist2d = new JIPFunctionList();

		getContentPane().setLayout(new BorderLayout());

        tabPane = new JTabbedPane();
        tabPane.addChangeListener(this);
        g2d = new Gui2D(this, prop, funclist2d);
        tabPane.add(g2d);
        tabPane.setTitleAt(INDEX2D,"2D");

        // Add DesktopPane
        gdesk = new GuiDesktop(this, prop, funclist2d);
        tabPane.add(gdesk,INDEXDESKTOP);
        tabPane.setTitleAt(INDEXDESKTOP,"Desktop");

        // Add Javavis3D (if Java3D is installed)
        try {
        	Class.forName("javax.media.j3d.TransformGroup");
        	g3d = new Gui3D(this, prop, false);
        	tabPane.add(g3d,INDEX3D);
            tabPane.setTitleAt(INDEX3D,"3D");
            JIP3DFunctionList jfl3 = new JIP3DFunctionList();
    		menuBar3D = new MenuBarGui3D(g3d, jfl3, prop);
        }
        catch (ClassNotFoundException e) {
        	logger.warn("Java3D is not present");
        }

		menuBar2D = new MenuBarGui2D(g2d, funclist2d, prop);
		menuBarDesktop = new MenuBarDesktop(gdesk, prop);
		setJMenuBar(menuBar2D);

        getContentPane().add(tabPane, BorderLayout.CENTER);

		setIconImage(Commons.getIcon("vg.gif").getImage());
        setSize(800,600);
	}

	public void stateChanged(ChangeEvent e) {
		switch (tabPane.getSelectedIndex()) {
			case INDEX2D : setJMenuBar(menuBar2D); break;
			case INDEXDESKTOP : setJMenuBar(menuBarDesktop); break;
			case INDEX3D : setJMenuBar(menuBar3D); break;
			default: break;
		}
	}

	public MenuBarGui2D getMenuBarGui2D() {
		return menuBar2D;
	}

	public MenuBarGui3D getMenuBarGui3D() {
		return menuBar3D;
	}

	/** @return  the menuBarDesktop */
	public MenuBarDesktop getMenuBarDesktop() {
		return menuBarDesktop;
	}

	public boolean is3dPresent () {
		return g3d!=null;
	}

	/** This is the main of the application. Here the main
	 * class is called and it is which creates all the menus system, information
	 * and it receive users orders.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		String lcOSName = System.getProperty("os.name").toLowerCase();
		Boolean IS_MAC = lcOSName.startsWith("mac os x");
		
		if (IS_MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JavaVis");
		}
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		PropertyConfigurator.configure("resources/log4j.properties");
		final Gui frame = new Gui();
		WindowListener l = frame.new MyWindowAdapter(frame);
		frame.addWindowListener(l);
		frame.setDefaultCloseOperation(Gui.DO_NOTHING_ON_CLOSE);
		
		frame.setVisible(true);
	}

	/** @return  Returns the g2d. */
	public Gui2D getG2d() {
		return g2d;
	}

	/** @param g2d  The g2d to set. */
	public void setG2d(Gui2D g2d) {
		this.g2d = g2d;
	}

	/** @return  Returns the g3d. */
	public Gui3D getG3d() {
		return g3d;
	}

	/** @param g3d  The g3d to set. */
	public void setG3d(Gui3D g3d) {
		this.g3d = g3d;
	}

	/** @return  Returns the gdesk. */
	public GuiDesktop getGdesk() {
		return gdesk;
	}

	/** @param gdesk  The gdesk to set. */
	public void setGdesk(GuiDesktop gd) {
		this.gdesk = gd;
	}

	/** @return  Returns the tabPane. */
	public JTabbedPane getTabPane() {
		return tabPane;
	}

	/** @param tabPane  The tabPane to set. */
	public void setTabPane(JTabbedPane tabPane) {
		this.tabPane = tabPane;
	}

	/** @return  Returns the prop. */
	public Properties getProp() {
		return prop;
	}

	/** @param prop  The prop to set. */
	public void setProp(Properties prop) {
		this.prop = prop;
	}


	/**
	 * Auxiliar class to capture the closing window event.
	 * @author     Miguel
	 */
	class MyWindowAdapter extends WindowAdapter {
		Gui gui;
		MyWindowAdapter (Gui guii) {
			gui=guii;
		}
		public void windowClosing(WindowEvent e) {
			JIPToolkit.exit (gui.getProp(), gui);
		}
	};
}