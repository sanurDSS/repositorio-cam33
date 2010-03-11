package javavis.desktop.gui;

import com.centerkey.utils.BareBonesBrowserLaunch;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javavis.Commons;
import javavis.Gui;
import javavis.base.Dialog;
import javavis.base.JIPException;
import javavis.base.JIPToolkit;
import javavis.base.ParamType;
import javavis.desktop.xml.XMLParser;
import javavis.jip2d.base.JIPFunctionList;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.apache.log4j.Logger;


/**
 * @author     Miguel Cazorla
 * @version     0.1
 * @date     5-2006
 */
public class GuiDesktop extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7032982634615964499L;
	
	private static Logger logger = Logger.getLogger(GuiDesktop.class);
	/**
	 * check if everything is saved
	 * @uml.property  name="isSaved"
	 */
	public boolean isSaved = true;

	File projectFile;
	
	private DesktopAction action;

	private JToolBar tb;
	
	public DesktopPane mainContentPane;
	
	// Reference to the main numFrame
	private Gui mainGui;
	
	private JScrollPane scrollPane;
	
	Properties prop;
	
	/**
	 * This is used to open and saving projects
	 */
	private JFileChooser chooser;
	
	//array of functions
	public ArrayList<DrawFunction> functionsList;
	
	//Build the numFrame
	public GuiDesktop(Gui mf, Properties propi, JIPFunctionList jfli) {
		mainGui=mf;
		prop = propi;

		action=DesktopAction.NOACTION;
		chooser = new JFileChooser(".");
		setLayout(new BorderLayout());
		mainContentPane=new DesktopPane(mainGui, this, jfli);	

	    mainContentPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
	    
	    //functions list
	    functionsList = new ArrayList<DrawFunction>(20);
	    setLayout(new BorderLayout());

		scrollPane = new JScrollPane(mainContentPane,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollPane.setViewportBorder(new BevelBorder(BevelBorder.RAISED));
		add(scrollPane,BorderLayout.CENTER);
		createToolBar();
        add(tb,BorderLayout.NORTH);
		
		btNew_action();
	}

	
	protected void createToolBar () {
		tb = new JToolBar();
		tb.setRollover(true);
		tb.setFloatable(false);
		JButton button = null;

		button = new JButton(Commons.getIcon("addImage.png"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("AddImageTipTex"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAction(DesktopAction.ADDLOADIMAGE);
				mainGui.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
		});
		tb.add(button);

		button = new JButton(Commons.getIcon("addFunc1.png"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("AddFunction"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAction(DesktopAction.ADDFUNCTION);
				mainGui.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
		});
		tb.add(button);

		button = new JButton(Commons.getIcon("insFunc.png"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("InsertFunction"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAction(DesktopAction.SELECTFUNCTION);
				mainGui.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
		});
		tb.add(button);

		button = new JButton(Commons.getIcon("remFunc.png"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("RemoveFunction"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAction(DesktopAction.REMOVEFUNCTION);
				mainGui.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
		});
		tb.add(button);

		
		button = new JButton(Commons.getIcon("bt_cancel.png"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("CancelFunction"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAction(DesktopAction.NOACTION);
				mainGui.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		tb.add(button);
		
		tb.add(new JToolBar.Separator());
		
		button = new JButton(Commons.getIcon("bt_play.png"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("RunAllfunctions"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAction(DesktopAction.RUNALLFUNCTIONS);
				mainGui.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				mainContentPane.runAllFunctionsAction();
			}
		});
		tb.add(button);
		
		tb.add(new JToolBar.Separator());
		
		button = new JButton(Commons.getIcon("anima.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("GenFunc"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				genFunc();
			}
		});
		tb.add(button);		
	}
	
	/**
	 * It gets the action desired 
	 * @return  the current action
	 * @uml.property  name="action"
	 */
	public DesktopAction getAction() {
		return action;
	}
	
	/**
	 * It gets the action desired 
	 * @return  the current action
	 * @uml.property  name="action"
	 */
	public void setAction(DesktopAction act) {
		action=act;
	}
	
	public JFrame getDesktop () {
		return mainGui;
	}
	
	/**
	 * create new file method
	 * 
	 * e - action event to button
	 */
	public void btNew_action() {
		ConfirmWindow cf = new ConfirmWindow(mainGui, this);
		
		if (!isSaved) {		
			cf.setModal(true);
			cf.setVisible(true);
			cf.pack();
		}
		
		if ((!cf.getButCancel() && isSaved) || cf.getButNo()) {
			functionsList.clear();
			this.mainContentPane.removeAll();
		
			this.mainContentPane.repaint();
			isSaved = false;

			//draw function panel
	   		Function func=new Function("FLoadImage", functionsList);
	   		DrawFunction newFunction=new DrawFunction(this, func, new Point(10,10));
	   		mainContentPane.add(newFunction);
	   		newFunction.show();
	   		newFunction.pack();
        	//append function to the functions list
        	functionsList.add(newFunction);
			this.mainContentPane.repaint();
		}
	}
	
	public void openDesktopFile (File fich) {
		this.mainContentPane.removeAll();
		functionsList.clear();
	
		projectFile = fich;
		this.parseXML(projectFile.getAbsolutePath(),false);

		this.mainContentPane.repaint();		
		
		isSaved = true;
	}

	/**
	 * open strategy method
	 * 
	 */
	public void btOpen_action() {
		String aux;
		if (projectFile!=null) aux=projectFile.getAbsolutePath();
		else aux=new File(".").getAbsolutePath();
		try {
			JIPToolkit.askForFile(aux, true, prop, mainGui);
		} catch (JIPException e) {
			logger.error(e);
		}
	}
	
	/**
	 * save strategy method
	 * 
	 */
	public void btSave_action() {
		if (!isSaved)
			btSaveAs_action();
		else {
			try {
				if(!projectFile.exists())
					projectFile.createNewFile();
				
				BufferedWriter out = new BufferedWriter(new FileWriter(projectFile));
				out.write(this.generateXML());
				out.close();
				isSaved = true;
			} catch (IOException e) {logger.error(e);}
		}
	}
	
	/**
	 * save as strategy method
	 * 
	 */
	public boolean btSaveAs_action() {	
		boolean confirmSave = false;
		
		try {
			int value = chooser.showSaveDialog(this);
			
			while (value == JFileChooser.APPROVE_OPTION && confirmSave == false) {
				confirmSave = true;
				projectFile = chooser.getSelectedFile();
				
				if(!projectFile.exists())
					projectFile.createNewFile();
				else 
					if (JOptionPane.showConfirmDialog(null, 
							prop.getProperty("OverwriteFile"), 
							prop.getProperty("OverwriteFile"), 
							JOptionPane.YES_NO_OPTION)== JOptionPane.NO_OPTION)
						confirmSave = false;
			
				if (confirmSave) {
					BufferedWriter out = new BufferedWriter(new FileWriter(projectFile));
					out.write(this.generateXML());
					out.close();
					isSaved = true;
				}
				else
					value = chooser.showSaveDialog(this);
			} 
			return confirmSave;
		}catch (IOException e) {logger.error("Error opening the file");return false;}
	}
	
	/**
	 * help method
	 * 
	 */
	public void btHelp_action() {
		//Browser br = new Browser();
	}
	
	/**
	 * Check if the DrawFunction trying to be deleted is the first
	 * The first function will be always a FLoadImage one
	 * @param df The DrawFunction to check
	 * @return True if the it df is the first function 
	 */
	public boolean isFirstLoadImage (DrawFunction df) {
		return df==functionsList.get(0); 
	}
	
	/**
	 * Return the properties for language
	 * @return Properties
	 */
	public Properties getProperties () {
		return prop;
	}
	
	/**
	 * Generates a new function in javavis.jip2d.functions with the current 
	 *  list of functions
	 */
	public void genFunc() {
		if (!isSaved) {
			new Dialog(this).information(prop.getProperty("Desktop1a"),
					prop.getProperty("Information"));
			if (!btSaveAs_action()) {// The file has not been saved
				new Dialog(this).information(prop.getProperty("Desktop1"),
						prop.getProperty("Information"));
				return;
			}
		}
		
		String pname = projectFile.getName();
		int dotindex = pname.indexOf(".");
		if(dotindex > 0){
			pname = pname.substring(0,dotindex);
		}
		
		String imports = "package javavis.jip2d.functions;\n\n"+
			"import javavis.base.JIPException;\n"+
			"import javavis.jip2d.base.FunctionGroup;\n"+
			"import javavis.jip2d.base.JIPFunction;\n"+
			"import javavis.jip2d.base.JIPImage;\n";
		String output =	"/**\n * Function generated by Desktop\n"+
			" **/\npublic class F"+ pname+" extends JIPFunction {\n"+
			"\tprivate static final long serialVersionUID = -7262973524107183332L;\n\n"+
			"\tpublic F"+pname+"() {\n"+
			"\t\tsuper();\n\t\tname = \"F"+pname+"\";\n"+
			"\t\tdescription = \"Function generated by Desktop\";\n"+
			"\t\tgroupFunc = FunctionGroup.Others;\n"+
			"\t}\n\n"+
			"\tpublic JIPImage processImg(JIPImage img) throws JIPException {\n"+
			"\t\tJIPImage res = null;\n" +
			"\t\tJIPFunction func = null;\n\n";

		// This variable allows to change between images
		boolean change=false; 
		ParamType paramType;
		String [] paramNames;
		try {
			for (DrawFunction df : functionsList) {
				if (functionsList.indexOf(df)!=0) { // First FLoadImage not included
					imports += "import javavis.jip2d.functions."+df.func.getName()+";\n";
					output += "\t\tfunc = new "+df.func.getName()+"();\n";
					paramNames = df.func.jipfunction.getInputParamNames();
					if (paramNames != null) {
						for (String name : paramNames) {
							paramType = df.func.jipfunction.getParamType(name);
							if (paramType!=ParamType.OBJECT) { // Object type is not contempled
								output += "\t\tfunc.setParamValue(\"";
								output += name+"\", ";
								switch (paramType) {
									case FLOAT: output += df.func.jipfunction.getParamValueFloat(name)+"f"; break;
									case INT: output += df.func.jipfunction.getParamValueInt(name); break;
									case BOOL: output += df.func.jipfunction.getParamValueBool(name); break;
									case STRING: 
									case LIST: 
									case DIR:  
									case FILE: output += "\""+df.func.jipfunction.getParamValueString(name)+"\""; break;
								}
								output += ");\n";
							}
						}
					}
					output += "\t\ttry {\n";
					if (!change) {
						output += "\t\t\tres = func.processImg(img);\n";
						change=true;
					}
					else {
						output += "\t\t\timg = func.processImg(res);\n";
						change=false;
					}
					output += "\t\t}\n";
					output += "\t\tcatch(JIPException e) {\n";
					output += "\t\t\tthrow new JIPException(e.getMessage());\n";
					output += "\t\t};\n\n";
				}
			}
		} catch (JIPException e) {logger.error(e);}
		output += "\t\treturn "+(change?"res":"img")+";\n";
		output += "\t}\n}\n";
		try {
			FileWriter fich = new FileWriter("javavis//jip2d//functions//F"+pname+".java");
			fich.write(imports+"\n"+output);
			fich.close();
			new Dialog(this).information(prop.getProperty("Desktop2"),
					prop.getProperty("Information"));
		}
		catch (IOException e) {
			new Dialog(this).information(prop.getProperty("Desktop5"),
					prop.getProperty("Error"));
		}
	}
	
	public void setLightsOff (DrawFunction df) {
		boolean found=false;
		for (DrawFunction aux : functionsList) {
			if (!found) {
				if (aux==df) {
					found=true;
					aux.setLightOff();
				}
			}
			else 
				aux.setLightOff();
		}
	}
	/**
	 * event to buttons
	 * 
	 * arg0 - event
	 */
	public void actionPerformed (ActionEvent arg0) {
		// Save
		if (arg0.getSource()==this.mainGui.getMenuBarDesktop().jmsave) {
			this.btSave_action();
		}
		else if (arg0.getSource()==this.mainGui.getMenuBarDesktop().jmabout) {
			Commons.showAbout();
		}
		else if (arg0.getSource()==this.mainGui.getMenuBarDesktop().jmhelp) {
			BareBonesBrowserLaunch.openURL("file:///"+System.getProperty("user.dir")+File.separator+
					"javavis"+File.separator+"help"+File.separator+"manual.html");
		}
		else if (arg0.getSource()==this.mainGui.getMenuBarDesktop().jmexit) {
			JIPToolkit.exit(prop, mainGui);
		}
		else if (arg0.getSource()==this.mainGui.getMenuBarDesktop().jmnew) {
			this.btNew_action();
		}
		else if (arg0.getSource()==this.mainGui.getMenuBarDesktop().jmopen) {
			this.btOpen_action();
		}
		else if (arg0.getSource()==this.mainGui.getMenuBarDesktop().jmsaveas) {
			this.btSaveAs_action();
		}
		else if (arg0.getSource()==this.mainGui.getMenuBarDesktop().jmgenerate) {
			this.genFunc();
		}
	}
	
	
	
//////////////////////////XML	
	public void parseXML (String fileName, boolean extern) {
		XMLReader reader;
		XMLParser parser;
		
		try {
			reader=XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			reader.setFeature("http://xml.org/sax/features/validation",false);
			
			parser=new XMLParser(this);
			reader.setContentHandler(parser);
			reader.parse(fileName);
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	// XML Generation from drawFunctions arraylist
	public String generateXML() {
		String output = "<project ";
		output+= "count='" + functionsList.size() + "'>\n";
		try {
			for (DrawFunction df : functionsList) {
				output += "\t";
				output += "<function ";
				output += "name='" + df.func.getName() + "' ";
				output += "positionx='" + df.getPosition().x + "' ";
				output += "positiony='" + df.getPosition().y + "' ";
				
				output += ">\n";
						
				int numParams = df.func.jipfunction.getNumInputParams();
				String []paranNames = df.func.jipfunction.getInputParamNames();
				for (int j=0;j<numParams;j++) {
					output += "\t\t<param name='" + paranNames[j] + "' ";
					output += "type='";
					
					ParamType paramType = df.func.jipfunction.getParamType(paranNames[j]);
					switch (paramType) {
						case FLOAT: output += "real"; break;
						case INT: output += "int"; break;
						case BOOL: output += "bool"; break;
						case STRING: output += "string"; break;
						case LIST: output += "list"; break; // as string 
						case DIR: output += "dir"; break; // as string 
						case FILE: output += "file"; break; // as string
						case OBJECT: output += "object"; // always null
					}
					
					output += "' ";
					output += "value='";
					
					if (paramType == ParamType.FLOAT) 
						output += df.func.jipfunction.getParamValueFloat(paranNames[j]);
					else if (paramType == ParamType.INT)
						output += df.func.jipfunction.getParamValueInt(paranNames[j]);
					else if (paramType == ParamType.BOOL)
						output += df.func.jipfunction.getParamValueBool(paranNames[j]);
					else if (paramType == ParamType.STRING || paramType == ParamType.LIST 
							|| paramType == ParamType.DIR || paramType == ParamType.FILE)
						output += df.func.jipfunction.getParamValueString(paranNames[j]);
					// no value stored for object
					
					output += "'></param>\n";
				}
			    output += "\t</function>\n";
			}
		} catch (JIPException e){logger.error(e);}
		output += "</project>\n";
		return output;
	}


	/**
	 * @return  Returns the isSaved.
	 * @uml.property  name="isSaved"
	 */
	public boolean isSaved() {
		return isSaved;
	}


	/**
	 * @param isSaved  The isSaved to set.
	 * @uml.property  name="isSaved"
	 */
	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}
}




