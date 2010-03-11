package javavis.jip3d.base;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;



import javavis.base.Function3DGroup;
import javavis.jip3d.gui.Function3D;

import javax.swing.JMenu;
import javax.swing.JMenuItem;


public class JIP3DFunctionList {
	/** Number of functions in the list */
	int nfunc;
	/** Array which has the names of the functions */
	String[] funcnames = null;
	/**
	 * Array which connect function with groups
	 * @uml.property  name="funcgroups"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	Function3DGroup[] funcgroups = null;
	/** Array keeping the number of functions in each group */
	int[] fgnum = null;
	/**
	 * Integer indicating the number of groups *
	 * @uml.property  name="ngrps"
	 */
	int ngrps;

	/**<P><FONT COLOR="RED">
	 *<B>Description:</B><BR>
	 *<FONT COLOR="BLUE">
	 *		Class constructor. Here the names of the function are inserted in the
	 * arrays and its groups.
	 */
	@SuppressWarnings("unchecked")
	public JIP3DFunctionList()
	{
		File func_dir = new File(".//bin//javavis//jip3d//functions");
        String []functions;
        int cont;
		Class clase;

        if(func_dir.isDirectory())
        {
        	functions = func_dir.list();
        	Arrays.sort(functions);
    		nfunc = 0;
    		funcnames = new String[functions.length];
    		funcgroups = new Function3DGroup[functions.length];
    		ngrps = Function3DGroup.values().length;
    		fgnum = new int[ngrps];
    		for (int i=0; i<ngrps; i++)
    			fgnum[i]=0;

    		cont = 0;
        	for(String fname: functions)
        		if(fname.charAt(0)=='F' && fname.indexOf("$")==-1)
        		{
        			funcnames[nfunc] = fname.substring(0, fname.lastIndexOf("."));
        			if (fname.charAt(0)=='F' && fname.indexOf("$")==-1) {
        				try {
        					clase = Class.forName("javavis.jip3d.functions." + funcnames[cont]);
        					funcgroups[cont]=((Function3D)clase.newInstance()).group;
        					fgnum[funcgroups[cont].ordinal()]++;
        					cont++;
        				} catch (Exception e) {
        					System.err.println(e);
        				}
        			}
        			nfunc++;
        		}
        }
	}


	/**<P><FONT COLOR="RED">
	 *<B>Description:</B><BR>
	 *<FONT COLOR="BLUE">
	 *   Method to get the number of created function.
	 * @return Number of functions
	 */
	public int getNumFunctions() {
		return nfunc;
	}

	/**<P><FONT COLOR="RED">
	 *<B>Description:</B><BR>
	 *<FONT COLOR="BLUE">
	 *   Method to get the number of functions in each group.
	 * @return Array where each element is the number of functions of the
	 * corresponding group
	 */
	public int[] getFuncGroupNum() {
		return (int[])fgnum.clone();
	}

	/**<P><FONT COLOR="RED">
	 *<B>Description:</B><BR>
	 *<FONT COLOR="BLUE">
	 *   Method to get the name of the function name which is passed by parameter.
	 * @param f Number assigned to function
	 * @return Name of the asked function
	 */
	public String getName(int f) {
		if (f >= 0 && f < nfunc) return funcnames[f];
		else return ("");
	}

	/**<P><FONT COLOR="RED">
	 *<B>Description:</B><BR>
	 *<FONT COLOR="BLUE">
	 *	 Method to get the function object such as we know the name and we pass by parameter
	 * @param fname Name of the function
	 * @return JIPFuncion object which has the required function
	 */
	@SuppressWarnings("unchecked")
	public Function3D getJIPFunction3D(String fname) {
		for (int i = 0; i < nfunc; i++) {
			if (fname.equals(funcnames[i])) {
				try {
					Class clase = Class.forName("javavis.jip3d.functions." + fname);
					return (Function3D)clase.newInstance();
				} catch (Exception e) {
					System.err.println(e);
				}
				break;
			}
		}
		return null;
	}

	/**<P><FONT COLOR="RED">
	 *<B>Description:</B><BR>
	 *<FONT COLOR="BLUE">
	 *   Method to create the menu that contain the function.
	 * @param title Menu title
	 * @param al ActionListener
	 * @return menu that contain the function.
	 */
	public JMenu getFunctionMenu(String title, ActionListener al) {
		JMenu mfunc = new JMenu(title);
		JMenuItem item;
		JMenu m;

		for (Function3DGroup f : Function3DGroup.values()) {
			m = new JMenu(f.toString());
			for (int j = 0; j < nfunc; j++) {
				if (funcgroups[j] == f) {
					item = new JMenuItem(funcnames[j]);
					item.setActionCommand(funcnames[j]);
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
	 * @uml.property  name="ngrps"
	 */
	public int getNgrps() {
		return ngrps;
	}

	/**
	 * @param ngrps  The ngrps to set.
	 * @uml.property  name="ngrps"
	 */
	public void setNgrps(int ngrps) {
		this.ngrps = ngrps;
	}

	/**
	 * @return  Returns the funcgroups.
	 * @uml.property  name="funcgroups"
	 */
	public Function3DGroup[] getFuncgroups() {
		return funcgroups;
	}

	/**
	 * @param funcgroups  The funcgroups to set.
	 * @uml.property  name="funcgroups"
	 */
	public void setFuncgroups(Function3DGroup[] funcgroups) {
		this.funcgroups = funcgroups;
	}

}
