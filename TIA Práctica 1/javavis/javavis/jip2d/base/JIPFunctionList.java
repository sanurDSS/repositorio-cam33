package javavis.jip2d.base;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;


/**
 * Class to manage the function list. In this class is where the functions and their groups are specified.
 */
public class JIPFunctionList {
	
	private static Logger logger = Logger.getLogger(JIPFunctionList.class);
	
	/** Number of functions in the list */
	int nfunc;

	/** Array which has the names of the functions */
	String[] funcnames = null;
	
	/** Array which connect function with groups */
	FunctionGroup[] funcgroups = null;
	
	/** Array keeping the number of functions in each group */
	int[] fgnum = null;
	
	/** Integer indicating the number of groups */
	int ngrps;
	
	/**
	 *	Class constructor. Here the names of the function are inserted in the
	 * arrays and its groups.
	 */
	public JIPFunctionList() {
		// Functions are loaded in a dinamic way
		File f = new File("bin//javavis//jip2d//functions");
		
		String []funcs = f.list();
		Arrays.sort(funcs);
		nfunc = 0;
		for (int i=0; i<funcs.length; i++)
			if (funcs[i].matches("F\\w*.class")) nfunc++;
		funcnames = new String[nfunc];
		funcgroups = new FunctionGroup[nfunc];
		ngrps = FunctionGroup.values().length;
		fgnum = new int[ngrps];
		for (int i=0; i<ngrps; i++) 
			fgnum[i]=0;
		int cont=0;
		Class clase;
		for (int i=0; i<funcs.length; i++)
			if (funcs[i].matches("F\\w*.class")) {
				funcnames[cont] = funcs[i].substring(0,funcs[i].indexOf(".class"));
				try {
					clase = Class.forName("javavis.jip2d.functions." + funcnames[cont]);
					funcgroups[cont]=((JIPFunction)clase.newInstance()).getGroupFunc();
					fgnum[funcgroups[cont].ordinal()]++;
					cont++;
				} 
				catch (InstantiationException e) {logger.error(e);} 
				catch (ClassNotFoundException e) {logger.error(e);} 
				catch (IllegalAccessException e) {logger.error(e);}
			}
	}

	/**
	 *   Method to get the number of created function.
	 * @return Number of functions
	 */
	public int getNumFunctions() {
		return nfunc;
	}

	/**
	 *   Method to get the number of functions in each group.
	 * @return Array where each element is the number of functions of the
	 * corresponding group
	 */
	public int[] getFuncGroupNum() {
		return (int[])fgnum.clone();
	}

	/**
	 *   Method to get the name of the function name which is passed by parameter.	 
	 * @param f Number assigned to function
	 * @return Name of the asked function
	 */
	public String getName(int f) {
		if (f >= 0 && f < nfunc) return funcnames[f];
		else return ("");
	}

	/**
	 *	 Method to get the function object such as we know the name and we pass by parameter
	 * @param fname Name of the function
	 * @return JIPFuncion object which has the required function
	 */
	public JIPFunction getJIPFunction(String fname) {
		for (int i = 0; i < nfunc; i++) {
			if (fname.equals(funcnames[i])) {
				try {
					return (JIPFunction)Class.forName("javavis.jip2d.functions." + fname).newInstance();
				} catch (Exception e) {logger.error(e);}
				break;
			}
		}
		return null;
	}

	/**
	 *   Method to create the menu that contain the function.
	 * @param tittle Menu tittle
	 * @param al ActionListener
	 * @return menu that contain the function.
	 */
	public JMenu getFunctionMenu(String tittle, ActionListener al) {
		JMenu mfunc = new JMenu(tittle);
		JMenuItem item;
		JMenu m;

		for (FunctionGroup f : FunctionGroup.values()) {
			m = new JMenu(f.toString());
			for (int j = 0; j < nfunc; j++) {
				if (funcgroups[j] == f) {
					item = new JMenuItem(funcnames[j]);
					item.setActionCommand("F_" + funcnames[j]);
					item.addActionListener(al);
					m.add(item);
				}
			}
			mfunc.add(m);
		}
		return mfunc;
	}
	
	public String[] getFuncArray () {
		return funcnames;
	}

	/**
	 * @return  Returns the ngrps.
	 */
	public int getNgrps() {
		return ngrps;
	}

	/**
	 * @param ngrps  The ngrps to set.
	 */
	public void setNgrps(int ngrps) {
		this.ngrps = ngrps;
	}

	/**
	 * @return  Returns the funcgroups.
	 */
	public FunctionGroup[] getFuncgroups() {
		return funcgroups;
	}

	/**
	 * @param funcgroups  The funcgroups to set.
	 */
	public void setFuncgroups(FunctionGroup[] funcgroups) {
		this.funcgroups = funcgroups;
	}
}