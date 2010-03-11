package javavis.jip3d.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javavis.Gui;
import javavis.jip3d.gui.dataobjects.PointSet3D;

import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;


public class Gui3D extends JPanel implements ActionListener, ItemListener, ChangeListener{
	private static final long serialVersionUID = 624955885721583016L;

	JLabel lpx, lpy, lpz, lox, loy, loz;
    public MyDialog dialogos;
    MyCanvas3D canvas;
    Gui father;

    // screen data panel
    JComboBox scr_list;


    //for scr_data panel
	JSpinner spinner = null; //width spinner
	JSpinner jsp_alpha;
	JSpinner jsp_length;

	JCheckBox jcb_visible;
	JCheckBox jcb_color;	//for PointSet3D
	JCheckBox jcb_improve;	//for PointSet3D

	JButton jbt_color;
	JButton jbt_remove;
	private ScreenData object_selected; //screen data object name that is already in use

	public double maxSpinner = 99;
	public double minSpinner = 0.1;
    public JPanel scrDataPanel;
    private Function3D current_func;
	JButton bcancelfunc;
	JPanel pizda;
	JLabel func_name;
	JProgressBar progress_bar;
	JLabel total_time;

	ScreenData noobject;
	boolean DEBUG;

    Properties prop;
    Properties paths;


    public Gui3D(Gui f, Properties pr, boolean debug)
    {
    	super(new BorderLayout());
        prop = pr;

    	dialogos = new MyDialog(f,prop);

    	father = f;
        lpx = new JLabel();
        lpy = new JLabel();
        lpz = new JLabel();
        lox = new JLabel();
        loy = new JLabel();
        loz = new JLabel();

        // Create Canvas3D
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsConfiguration[] gc =  gs[0].getConfigurations();
    	GraphicsConfigTemplate3D gct = new GraphicsConfigTemplate3D();
    	canvas = new MyCanvas3D(gct.getBestConfiguration(gc), this, prop, DEBUG);
    	addCanvas(canvas);

    	noobject = new PointSet3D(new ScreenOptions());
    	object_selected = noobject;
        DEBUG = debug;

    	FileInputStream fis=null;

        paths = new Properties();
        try {
       		fis = new FileInputStream("resources/path.properties");
        	paths.load(fis);
        	fis.close();
        } catch (IOException e) {
        	if(DEBUG)
        		System.out.println("Path Properties file not found, Using defaults values");
        	paths.setProperty("OpenSeq3D", ".");
        	paths.setProperty("OpenPoints3D", ".");
        	paths.setProperty("OpenFeatures2D", ".");
        	paths.setProperty("OpenPlanes3D", ".");
        	paths.setProperty("SaveJIP3D", ".");
        	paths.setProperty("OpenJIP3D", ".");
        	paths.setProperty("OpenSeq2D", ".");
        	paths.setProperty("Export3D", ".");
        	try{if (fis!=null) fis.close();}catch(IOException ex){}
        }

        JLabel laux;
        //Create the content-pane-to-be.
        setOpaque(true);
        setBackground(new Color(182, 182, 182));

        pizda = new JPanel(new BorderLayout(3,3));
        pizda.setBackground(new Color(182, 182, 182));

        JPanel pfrustrum = new JPanel(new GridLayout(4, 0, 2, 5));
        pfrustrum.setBackground(new Color(222, 222, 222));
        JPanel pfila = new JPanel(new GridLayout(0,3, 2, 5));


        laux = new JLabel("pos x", JLabel.CENTER);
        pfila.add(laux);
        laux = new JLabel("pos y", JLabel.CENTER);
        pfila.add(laux);
        laux = new JLabel("pos z", JLabel.CENTER);
        pfila.add(laux);
        pfrustrum.add(pfila);
        pfila = new JPanel(new GridLayout(0,3));
        lpx.setText((" "+canvas.globalx+"00000").substring(0, 6));
        lpy.setText((" "+canvas.globaly+"00000").substring(0, 6));
        lpz.setText((" "+canvas.globalz+"00000").substring(0, 6));
        pfila.add(lpx);
        pfila.add(lpy);
        pfila.add(lpz);
        pfrustrum.add(pfila);
        pfila = new JPanel(new GridLayout(0,3));
        laux = new JLabel("head x", JLabel.CENTER);
        pfila.add(laux);
        laux = new JLabel("head y", JLabel.CENTER);
        pfila.add(laux);
        laux = new JLabel("head z", JLabel.CENTER);
        pfila.add(laux);
        pfrustrum.add(pfila);
        pfila = new JPanel(new GridLayout(0,3));
        lox.setText((" "+canvas.orx+"00000").substring(0, 6));
        loy.setText((" "+canvas.ory+"00000").substring(0, 6));
        loz.setText((" "+canvas.orz+"00000").substring(0, 6));
        pfila.add(lox);
        pfila.add(loy);
        pfila.add(loz);
        pfrustrum.add(pfila);

        //Screen Object Selection
        scrDataPanel = new JPanel(new BorderLayout(1,1));
        String []data = {prop.getProperty("NoObject")};
        scr_list = new JComboBox(data);
        scr_list.setActionCommand("Lista");
        scr_list.addActionListener(this);
        createSelect(new ArrayList<ScreenData>());

        scrDataPanel.add("North", scr_list);
        scrDataPanel.add("Center", screenDataPanel(null));

        JPanel aux = new JPanel(new BorderLayout(1, 1));
        aux.add("Center", functionPanel(null));

        pizda.add("North",pfrustrum);
        pizda.add("Center",scrDataPanel);
        pizda.add("South", aux);

        add(pizda, BorderLayout.EAST);

        add(canvas, BorderLayout.CENTER);

    }

    public void endMenuContent()
    {
    	FileOutputStream fos = null;

    	try
    	{
    		fos = new FileOutputStream("resources/path.properties");
    		paths.store(fos, "");
    	}catch(Exception e)
    	{
    		System.out.println("Paths can not be stored");
    	}
    	finally {
    		if (fos!=null)
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    }

    public void addCanvas(MyCanvas3D can)
    {
    	canvas = can;
    }

    public void createSelect(ArrayList<ScreenData> names)
    {
    	noobject.name = prop.getProperty("NoObject"); //NoObject
        //Screen Object Selection

        //Clean objects
        scr_list.removeAllItems();
        scrDataPanel.removeAll();

    	int cont;
    	if(names.size()==0)
    	{
            scr_list.addItem(noobject);
            scrDataPanel.add("North", scr_list);
    		scrDataPanel.add("Center", screenDataPanel(null));
            object_selected = noobject;
    	}
    	else
    	{
    		for(cont=0;cont<names.size();cont++)
    		{
    			scr_list.addItem(names.get(cont));
    		}
    		ScreenOptions opt = object_selected.scr_opt;
            scrDataPanel.add("North", scr_list);
    		scrDataPanel.add("Center", screenDataPanel(opt));
    	}
		scr_list.setSelectedItem(object_selected);

    }


    protected JPanel screenDataPanel(ScreenOptions opt)
    {
    	JPanel ContentPane = new JPanel();
		JPanel pcontent = new JPanel(new BorderLayout());
		JPanel pcheckboxes = new JPanel(new GridLayout(5,0,1,0));
		JPanel pcolor = new JPanel();
		JPanel palpha = new JPanel();
		JPanel pspinner = new JPanel();
		JPanel pnumber = new JPanel();
		SpinnerNumberModel snmR;

		if(opt!=null)
			pnumber.add(new JLabel(prop.getProperty("Elements3D")+" "+opt.num_points)); //Elements3D
		else pnumber.add(new JLabel(prop.getProperty("Elements3D")+" 0   "));
		pcheckboxes.add(pnumber);

		jcb_visible = new JCheckBox(prop.getProperty("Visible3D")+"          "); //Visible3D
		if(opt!=null)
			jcb_visible.setSelected(opt.is_visible);
		else jcb_visible.setEnabled(false);

		jcb_visible.addItemListener(this);

		pcheckboxes.add(jcb_visible);

		if(opt!=null && opt.type == ScreenOptions.tPOINTSET3D)
		{
			jcb_color = new JCheckBox(prop.getProperty("Color3D")); //Color3D
			jcb_color.setSelected(opt.global_color);
			jcb_color.addItemListener(this);
			pcheckboxes.add(jcb_color);

			jcb_improve = new JCheckBox(prop.getProperty("Improved3D")); //Improved3D
			jcb_improve.setSelected(opt.improved);
			jcb_improve.addItemListener(this);
			pcheckboxes.add(jcb_improve);
		}
		else if((opt!=null && opt.type == ScreenOptions.tTRAJ2D) )
		{
			snmR = new SpinnerNumberModel(opt.alpha, 0.0, 1.0, 0.1);
			jsp_alpha = new JSpinner(snmR);
			jsp_alpha.setPreferredSize(new Dimension(50,20));
			//jsp_alpha.setSize(150,10);
			palpha.add("West", new JLabel(prop.getProperty("Transparency3D"))); //Transparency3D
			palpha.add("East", jsp_alpha);
			pcheckboxes.add(palpha);
		}
		else if((opt!=null && opt.type == ScreenOptions.tTRAJ3D))
		{
			snmR = new SpinnerNumberModel(opt.alpha, 0.0, 1.0, 0.1);
			jsp_alpha = new JSpinner(snmR);
			jsp_alpha.setPreferredSize(new Dimension(50,20));
			//jsp_alpha.setSize(150,10);
			palpha.add("West", new JLabel(prop.getProperty("Transparency3D"))); //Transparency3D
			palpha.add("East", jsp_alpha);
			pcheckboxes.add(palpha);
		}
		else if(opt!=null && opt.type == ScreenOptions.tNORMALSET3D)
		{
			snmR = new SpinnerNumberModel(opt.length, 0.0, 1.0, 0.1);
			jsp_length = new JSpinner(snmR);
			jsp_length.setPreferredSize(new Dimension(50, 20));
			palpha.add("West", new JLabel(prop.getProperty("VLength3D"))); //VLength3D
			palpha.add("East", jsp_length);
			pcheckboxes.add(palpha);
		}
		else
		{
			jcb_color = null;
			jcb_improve = null;
			pcheckboxes.add(new JLabel(" "));
		}

		if(opt!=null)
		{
			snmR = new SpinnerNumberModel(opt.width, minSpinner, maxSpinner, 1);
			spinner = new JSpinner(snmR);
			spinner.addChangeListener(this);
			spinner.setEnabled(true);
		}
		else
		{
			spinner = new JSpinner();
			spinner.setEnabled(false);
		}
		spinner.setPreferredSize(new Dimension(50,20));
		pspinner.add("West", new JLabel(prop.getProperty("Width3D")+": ")); //Width3D
		pspinner.add("East", spinner);


		pcolor.add("West", new JLabel(prop.getProperty("Color3D"))); //Color3D
		jbt_color = new JButton("");
		jbt_color.setActionCommand("Color");
		jbt_color.addActionListener(this);
		jbt_color.setPreferredSize(new Dimension(50,20));
		if(opt!=null)
			jbt_color.setBackground(opt.color.get());
		else jbt_color.setEnabled(false);

		pcolor.add("East", jbt_color);
		if(opt!=null && opt.type == ScreenOptions.tPOINTSET3D && !jcb_color.isSelected())
			jbt_color.setEnabled(false);

		JPanel p_remove = new JPanel(new GridLayout(2,0));
		jbt_remove = new JButton(prop.getProperty("Delete3D")); //Delete3D
		jbt_remove.setActionCommand("Remove Object");
		jbt_remove.addActionListener(this);
		if(opt==null)
			jbt_remove.setEnabled(false);
		p_remove.add(pcolor);
		p_remove.add(jbt_remove);

		pcontent.add("North", pcheckboxes);
		pcontent.add("Center", pspinner);
		pcontent.add("South", p_remove);

		ContentPane.add("Center", pcontent);
		ContentPane.setBackground(new Color(232, 232, 232));

    	return ContentPane;
    }

    protected JPanel functionPanel(Function3D func)
    {
    	JPanel ret = new JPanel(new BorderLayout());
		JPanel pmain = new JPanel(new GridLayout(4,0));
		JPanel pbot = new JPanel();
		bcancelfunc = new JButton(prop.getProperty("StopFunc3D")); //StopFunc3D
		bcancelfunc.setActionCommand("CancelFunc");
		bcancelfunc.addActionListener(this);
		pbot.add("Center", bcancelfunc);

		progress_bar = new JProgressBar();
		progress_bar.setValue(0);
		total_time = new JLabel("");

		if(func!=null)
		{
			func_name = new JLabel(prop.getProperty("Running3D")+" "+func.name); //Running3D
			pmain.add(func_name);
			bcancelfunc.setEnabled(true);
		}
		else
		{
			func_name = new JLabel(prop.getProperty("NoFunction3D")); //NoFunction3D
			pmain.add(func_name);
			bcancelfunc.setEnabled(false);
		}
		pmain.add(progress_bar);
		pmain.add(total_time);

		ret.add("Center", pmain);

    	return ret;
    }

    public void startFunction(String name)
    {
		func_name.setText(prop.getProperty("Running3D")+" "+name); //Running3D
		progress_bar.setValue(0);
		total_time.setText("");
    }

    public void endFunctionAction(String name, long time)
    {
    	float d_time;
    	if(name!=null)
    	{
    		d_time = time/1000.0f;
    		func_name.setText(prop.getProperty("Finished3D")+" "+name); //Finished3D
    		progress_bar.setValue(0);
    		total_time.setText(prop.getProperty("Time3D")+": "+d_time+" "+prop.getProperty("TUnits3D")); //Time3D //TUnits3D
    	}
    	else
    		func_name.setText(prop.getProperty("NoFunction3D"));
    }


    public void poseUpdate(double px, double py, double pz, double ox, double oy, double oz)
    {
    	double EPS = 0.001;
    	String text;

    	if(px>-EPS && px<EPS)
    		text = " 0.000000";
    	else
    		text = " "+px+"00000";
    	lpx.setText(text.substring(0, 6));

    	if(py>-EPS && py<EPS)
    		text = " 0.000000";
    	else
    		text = " "+py+"00000";
    	lpy.setText(text.substring(0, 6));

    	if(pz>-EPS && pz<EPS)
    		text = " 0.000000";
    	else
    		text = " "+pz+"00000";
    	lpz.setText(text.substring(0, 6));

    	if(ox>-EPS && ox<EPS)
    		text = " 0.000000";
    	else
    		text = " "+ox+"00000";
    	lox.setText(text.substring(0, 6));

    	if(oy>-EPS && oy<EPS)
    		text = " 0.000000";
    	else
    		text = " "+oy+"00000";
    	loy.setText(text.substring(0, 6));

    	if(oz>-EPS && oz<EPS)
    		text = " 0.000000";
    	else
    		text = " "+oz+"00000";
    	loz.setText(text.substring(0, 6));
    }

    public JProgressBar getProgressBar()
    {
    	return this.progress_bar;
    }

    public ScreenData getObjectSelected()
    {
    	if(object_selected != noobject)
    		return object_selected;
    	return null;
    }

    /*
     * Este metodo nos ayuda a determinar que objeto se ha pedido
     */
    protected int selectAction(String s)
    {
    	int ret = -1;
    	char c = s.charAt(1);
    	switch(c)
    	{
    	case 'o': //Points
    	case 'u': //Puntos
    		ret = 1;
    		break;
    	case 'l': //Planes or Planos
    		ret = 2;
    		break;
    	case 'e': //Sequences or Secuencias or Features
    		if (s.charAt(5)=='F')
    			ret=5;
    		else
    			ret = 3;
    		break;
    	case 'r': //Trajectories or Trayectorias
    		ret = 4;
    		break;
    	case 'a': //Caractertisticas
    		if (s.charAt(0)=='C')
    			ret=5;
    	}

    	return ret;
    }

    public OpenFileAction getOpenFileAction (String name) {
    	return new OpenFileAction(name);
    }

    public SaveFileAction getSaveFileAction (String name) {
    	return new SaveFileAction(name);
    }

    public ChangeViewAction getChangeViewAction (String name) {
    	return new ChangeViewAction(name);
    }

    class OpenFileAction extends AbstractAction
    {

    	String nombre;
		int action;
		/**
		 *
		 */
		private static final long serialVersionUID = 4223870651668267434L;

		public OpenFileAction(String name)
    	{
    		super(name.substring(6));
    		nombre = name;
			action = selectAction(nombre.substring(6));
    	}

		public void actionPerformed(ActionEvent e) {
			File datos;
			String path;
			String ppath; //name for path property
			String title = prop.getProperty("Import3D");
			ArrayList<String> extensions;

			extensions = new ArrayList<String>();
			if (e.getSource() == father.getMenuBarGui3D().openJIP3D)
			{
				extensions.add("jip3d");
				title = "";
			}
			ppath = "OpenJIP3D"; //default option
			switch(action)
			{
			case 1:
				ppath = "OpenPoints3D";
				break;
			case 2:
				ppath = "OpenPlanes3D";
				break;
			case 3:
				ppath = "OpenSeq2D";
				break;
			case 4:
				ppath = "OpenSeq3D";
				break;
			case 5:
				ppath = "OpenFeatures2D";
				break;
			}
			path = paths.getProperty(ppath);
			datos = dialogos.fileChooser(path, title+" "+nombre.substring(6), extensions, true); //Import3D
			if (datos!=null)
			{
				if (e.getSource() == father.getMenuBarGui3D().openJIP3D)
				{
					canvas.openJIP3D(datos.getName(), datos.getParent()+"/");
				}
				else
				{
					canvas.addScreenData(action, datos.getName(), datos.getParent()+"/");
				}
				paths.setProperty(ppath, datos.getParent()+"/");
			}
		}
    }

    class SaveFileAction extends AbstractAction
    {
		private static final long serialVersionUID = -6526129533901736429L;
		String nombre;

		public SaveFileAction(String name)
    	{
    		super(name.substring(8));
    		nombre = name;
    	}

		public void actionPerformed(ActionEvent e) {
			File datos;
			String path;
			String fname;
			String action = e.getActionCommand();
			String title = prop.getProperty("ExportObject3D"); //ExportObject3D
			ArrayList<String> types = new ArrayList<String>();
			if(DEBUG)
				System.out.println("Objeto: "+object_selected.name);

			if(object_selected!=null)
			{
				if(!nombre.equals(prop.getProperty("Export3D")))
				{
					if(DEBUG)
						System.out.println("Guardo jip "+action);
					types.add("jip3d");
					title = prop.getProperty("SaveJIP3D"); //SaveJIP3D
				}
				path = paths.getProperty(nombre);
				do
				{
					datos = dialogos.fileChooser(path, title, types, false);
				}while (datos!=null && datos.exists() && !dialogos.confirm(prop.getProperty("Thefile3D")+" "+datos.getName()+" "+prop.getProperty("Exist3D"), title+" "+nombre)); //Thefile3D //Exist3D
				if (datos!=null)
				{
					paths.setProperty(nombre, datos.getParent()+"/");
					if(nombre.equals(prop.getProperty("Export3D")))
						canvas.saveScreenData(object_selected, datos.getName(), datos.getParent()+"/");
					else
					{
						fname = datos.getName();
						if(fname.lastIndexOf('.')==-1 || !fname.substring(fname.lastIndexOf('.')+1).toLowerCase().equals("jip3d"))
							fname += ".jip3d";
						canvas.saveJIP3D(object_selected, fname, datos.getParent()+"/");
					}
				}
			}
			else
				dialogos.error(prop.getProperty("SaveError3D"), title); //SaveError3D
		}
    }

    class ChangeViewAction extends AbstractAction
    {
		private static final long serialVersionUID = -7174213429633165458L;

		public ChangeViewAction(String nombre)
		{
			super(nombre);
		}

		public void actionPerformed(ActionEvent e) {
			double px, py, pz, ox, oy, oz;
			double []result;
			Vector3f pos;
			double modulo;
			double []orientacion = new double[3];
			canvas.actualizarVista();

			if(e.getActionCommand().equals(prop.getProperty("Zenital")))
			{
				canvas.cambiarVistaCenital();
			}
			else if(e.getActionCommand().equals(prop.getProperty("Robot")))
			{
				canvas.cambiarVistaRobot();
			}
			else
			{
				px = canvas.globalx;
				py = canvas.globaly;
				pz = canvas.globalz;
				ox = canvas.orx;
				oy = canvas.ory;
				oz = canvas.orz;
				result = dialogos.changePose(px, py, pz, ox, oy, oz);
				pos = new Vector3f((float)result[0], (float)result[1], (float)result[2]);
				modulo = Math.sqrt(result[3]*result[3] + result[4]*result[4] + result[5]*result[5]);
				orientacion[0] = -result[3] / modulo;
				orientacion[1] = -result[4] / modulo;
				orientacion[2] = -result[5] / modulo;
				canvas.cambiarVista(pos, orientacion);
			}
		}
    }

    class SelectObjectAction extends AbstractAction
    {
		private static final long serialVersionUID = 510859158363323382L;
		int position;
		public SelectObjectAction(String name, int pos)
		{
			super(name);
			position = pos;
		}

		public void actionPerformed(ActionEvent arg0) {
			SelectDialog sdiag = new SelectDialog(dialogos.owner, canvas.scr_data.get(position).scr_opt);
			sdiag.setModal(true);
			sdiag.setVisible(true);

			if(sdiag.delete)
			{
				canvas.removeScreenData(canvas.scr_data.get(position));
			}
			else canvas.reDraw(canvas.scr_data.get(position));
		}

    }

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("Color")) {
			Color newColor = JColorChooser.showDialog(
                    father,
                    "Choose Object Color",
                    jbt_color.getBackground());
			if(newColor != null)
			{
				jbt_color.setBackground(newColor);
				object_selected.scr_opt.color = new Color3f(newColor);
				canvas.reDraw(object_selected);
			}
		}

		if(action.equals("Remove Object"))
		{
			if(dialogos.confirm("Object will be removed, are you sure?", "Remove Object"))
				canvas.removeScreenData(object_selected);
		}

		if(action.equals("Lista"))
		{
			ArrayList<ScreenData> data_list; int cont;
			ScreenData object_name = (ScreenData)scr_list.getSelectedItem();
			if (object_name != null)
			{
				if((object_name).equals(noobject))
				{
					scrDataPanel.add("Center", screenDataPanel(null));
				}
				else
				{
					data_list = new ArrayList<ScreenData>();
					for(cont=0;cont<scr_list.getItemCount();cont++)
						data_list.add((ScreenData)scr_list.getItemAt(cont));
					object_selected = object_name;
					createSelect(data_list);
				}
			}
			else
			{
				object_selected = null;
			}
		}
		if(action.equals("CancelFunc"))
		{
			if(current_func!=null)
			{
				System.out.println("Cancelo funcion: "+current_func.name);
				current_func.stop();
			}
			current_func = null;
		}
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		ScreenData datum;

		if(source == jcb_color)
		{
			datum = object_selected;
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				jbt_color.setEnabled(true);
				datum.scr_opt.global_color = true;
			}
			else
			{
				jbt_color.setEnabled(false);
				datum.scr_opt.global_color = false;
			}
			canvas.reDraw(datum);

		}
		if(source == jcb_visible)
		{
			datum = object_selected;
			datum.scr_opt.is_visible = jcb_visible.isSelected();
			canvas.reDraw(datum);
		}
		if(source == jcb_improve)
		{
			datum = object_selected;
			datum.scr_opt.improved = jcb_improve.isSelected();
			canvas.reDraw(datum);
		}

	}

	public void stateChanged(ChangeEvent arg0) {
		ScreenData datum;
		double value = (Double)spinner.getValue();
		datum = object_selected;
		if(arg0.getSource() == spinner)
			datum.scr_opt.width = value;
		canvas.reDraw(datum);
	}

	public MyCanvas3D getCanvas() {
		return canvas;
	}

	public void setCanvas(MyCanvas3D canvas) {
		this.canvas = canvas;
	}
}
