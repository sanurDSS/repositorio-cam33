package javavis.jip3d.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**<P><FONT COLOR="RED">
*<B>Description:</B><BR>
*<FONT COLOR="BLUE">
*   Class to create Warning, Information, Error and Confirmation Dialog windows
* */

public class MyDialog {
	/** Component */
	public JFrame owner;

    Properties prop;


	/**<P><FONT COLOR="RED">
	*<B>Description:</B><BR>
	*<FONT COLOR="BLUE">
	*   Class Constructor.
	* @param c Component caller
	* @param pr Names Properties
		 */
	public MyDialog(JFrame c, Properties pr) {
		owner = c;
		prop = pr;
	}


	/**<P><FONT COLOR="RED">
	 *<B>Description:</B><BR>
	 *<FONT COLOR="BLUE">
	 *   This method is invoked in case of Warning Dialog.
	 * @param message Warning message
	 * @param title Warning window title
	 */
	public void warning(String message, String title) {
		JOptionPane.showMessageDialog(owner, message, title,
			JOptionPane.WARNING_MESSAGE);
	}

	/**<P><FONT COLOR="RED">
	*<B>Description:</B><BR>
	*<FONT COLOR="BLUE">
	*   This method is invoked in case of Information Dialog.
	* @param message Information message
	* @param title Information window title
	*/
	public void information(String message, String title) {
		JOptionPane.showMessageDialog(owner, message, title,
			JOptionPane.INFORMATION_MESSAGE);
	}


	/**<P><FONT COLOR="RED">
	*<B>Description:</B><BR>
	*<FONT COLOR="BLUE">
	*   This method is invoked in case of Error Dialog.
	* @param message Error message
	* @param title Error window title
		 */

	public void error(String message, String title) {
		JOptionPane.showMessageDialog(owner, message, title,
			JOptionPane.ERROR_MESSAGE);
	}

	/**<P><FONT COLOR="RED">
	*<B>Description:</B><BR>
	*<FONT COLOR="BLUE">
	*   This method is invoked in case of Confirmation Dialog.
	* @param message Confirmation message
	* @param title Confirmation window title
		 */

	public boolean confirm(String message, String title) {
		Object[] options = { prop.getProperty("Yes"), prop.getProperty("No") };
		int aux=JOptionPane.showOptionDialog(owner, message, title,
			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
			null, options, prop.getProperty("Yes"));
		return 0==aux;
	}

	public String input(String message, String title) {
		return JOptionPane.showInputDialog(owner, message, title,
				JOptionPane.QUESTION_MESSAGE);
	}

	public double[] changePose(double px, double py, double pz, double ox, double oy, double oz)
	{
		double []ret = new double[6];
		ArrayList<String> labels;
		ArrayList<Double> data;
		ChangeViewDialog pdialogo;
		int cont;

		labels = new ArrayList<String>(6);
		data = new ArrayList<Double>(6);

		labels.add("PosX: ");
		labels.add("PosY: ");
		labels.add("PosZ: ");
		labels.add("OrX : ");
		labels.add("OrY : ");
		labels.add("OrZ : ");

		data.add(px);
		data.add(py);
		data.add(pz);
		data.add(ox);
		data.add(oy);
		data.add(oz);

		pdialogo = new ChangeViewDialog(owner, labels, data);
		pdialogo.setModal(true);
		pdialogo.setVisible(true);

		for(cont=0;cont<6;cont++)
			ret[cont] = data.get(cont).doubleValue();
		return ret;
	}

	public File fileChooser(String ruta, String title, ArrayList<String>types, boolean action)
	{
		return fileChooser(ruta, title, types, action, JFileChooser.FILES_ONLY);
	}


	public File fileChooser(String ruta, String title, ArrayList<String>types, boolean action, int type)
	{
		JFileChooser chooser = new JFileChooser(ruta);
		chooser.setFileSelectionMode(type);
		int returnVal;
		if (title!=null) chooser.setDialogTitle(title);
		if(action)
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		else
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);

		chooser.setAcceptAllFileFilterUsed(true);
		GeneralFilter filter = new GeneralFilter(0);
		if(types!=null && types.size()>0)
		{
			for(String s: types)
				filter.addExtension(s);
			chooser.setFileFilter(filter);
		}

		if(action)
			returnVal = chooser.showOpenDialog(owner);
		else
			returnVal = chooser.showSaveDialog(owner);

		if(returnVal==JFileChooser.APPROVE_OPTION)
			return chooser.getSelectedFile();
		else return null;

	}


}
