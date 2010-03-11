package javavis;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author Miguel
 * This class contains several methods used in different parts of JavaVis 
 *
 */
public class Commons {

	/** gets an image icon from a file */
	public static ImageIcon getIcon (String name) {
		URL ic = Commons.class.getClass().getResource("/javavis/icons/"+name);

		if (ic!=null) return new ImageIcon(ic);
		else return null;
	}
	
	/** Shows a message with information about JavaVis */
	public static void showAbout () {
		JFrame imgfra = new JFrame("About");
		JLabel afoto = new JLabel(Commons.getIcon("about.jpg"));

		imgfra.getContentPane().add(afoto, BorderLayout.CENTER);
		imgfra.setIconImage(Commons.getIcon("vg.gif").getImage());
		imgfra.pack();
		imgfra.setResizable(false);
		imgfra.setLocation(100, 150);
		imgfra.setVisible(true);
		return;
	}
}
