package javavis.jip3d.gui;

import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javavis.jip3d.base.JIP3DFunctionList;
import javavis.jip3d.geom.Vector3D;
import javavis.jip3d.gui.dataobjects.FeatureSet2D;
import javavis.jip3d.gui.dataobjects.PlaneSet3D;
import javavis.jip3d.gui.dataobjects.PointSet3D;
import javavis.jip3d.gui.dataobjects.Trajectory2D;
import javavis.jip3d.gui.dataobjects.Trajectory3D;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BadTransformException;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
//Uncomment next line for zoomming camera by mouse middle button
//import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class MyCanvas3D extends Canvas3D implements ActionListener {

	private static final long serialVersionUID = 4750736186582863505L;

	Locale miLocale;
	BranchGroup miBranchGroup;
	ViewingPlatform miViewingPlatform;
	View vista;
	public TransformGroup TGVista;
	double globalx, globaly, globalz, orx, ory, orz;
	Gui3D menu;
	ArrayList <ScreenData> scr_data;

	JIP3DFunctionList function_list;
	Function3D current_func;
	FunctionPanel func_panel;

	boolean DEBUG;

    Properties prop;

	public MyCanvas3D(GraphicsConfiguration arg0, Gui3D parent, Properties pr, boolean debug) {
		super(arg0);
		scr_data = new ArrayList<ScreenData>();
		menu = parent;

		DEBUG = debug;
		prop = pr;

		//creamos la lista de funciones
		function_list = new JIP3DFunctionList();
		current_func = null;
        func_panel = new FunctionPanel(this);


		// Definimos el universo y el locale
		VirtualUniverse miUniverso = new VirtualUniverse();
		miLocale = new Locale(miUniverso);
		vista = construirVista();
		construirRamaVista(vista, new Vector3f(0.0f, 0.0f, 5.0f)); //0.85
		cambiarVistaRobot();
		menu.poseUpdate(globalx, globaly, globalz, orx, ory, orz);
		this.setSize(640, 480);
		actualizarVista();

		this.addKeyListener(new KeyListener()
		{

			public void keyPressed(KeyEvent e) {
				actualizarVista();
				if(menu!=null)
					menu.poseUpdate(globalx, globaly, globalz, orx, ory, orz);
			}

			public void keyReleased(KeyEvent e) {
				actualizarVista();
				if(menu!=null)
					menu.poseUpdate(globalx, globaly, globalz, orx, ory, orz);

			}

			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});
	}

	private View construirVista()
	{
		View miVista = new View();
		miVista.addCanvas3D(this);
		PhysicalBody miCuerpo = new PhysicalBody();

		miVista.setPhysicalBody(miCuerpo);

		miVista.setPhysicalEnvironment(new PhysicalEnvironment());
		miVista.setBackClipDistance(5000.0f);
		miVista.setFrontClipDistance(0.001f);
		return(miVista);
	}

	/**
	  * Construye la rama de vista, el puente entre la vista y el universo
	  */
	private void construirRamaVista(View miVista, Vector3f posicion)
	{
		miBranchGroup = new BranchGroup();
		TransformGroup TGV = new TransformGroup();
		//para borrar
		TransformGroup tgaux = new TransformGroup();
		TransformGroup tgauxX;
		TransformGroup tgauxY;
		TransformGroup tgauxZ;
		Transform3D tr3d;
		Appearance app = new Appearance();
		ColoringAttributes ca;
		BranchGroup bgaux = new BranchGroup();
		if(DEBUG)
		{
			Cylinder cil;
			cil = new Cylinder(0.25f, 0.5f);
			tgaux.addChild(cil);
			bgaux.addChild(tgaux);
			tr3d = new Transform3D();
			tr3d.setTranslation(new Vector3d(5,0,0));
			tgauxX = new TransformGroup(tr3d);
			cil = new Cylinder(0.25f, 0.5f);
			ca = new ColoringAttributes(1,0,0,ColoringAttributes.SHADE_GOURAUD);
			app.setColoringAttributes(ca);
			cil.setAppearance(app);
			tgauxX.addChild(cil);
			bgaux.addChild(tgauxX);
			//ejeY
			tr3d = new Transform3D();
			tr3d.setTranslation(new Vector3d(0,5,0));
			tgauxY = new TransformGroup(tr3d);
			cil = new Cylinder(0.25f, 0.5f);
			ca = new ColoringAttributes(0,1,0,ColoringAttributes.SHADE_GOURAUD);
			app = new Appearance();
			app.setColoringAttributes(ca);
			cil.setAppearance(app);
			tgauxY.addChild(cil);
			bgaux.addChild(tgauxY);
			//eje Z
			tr3d = new Transform3D();
			tr3d.setTranslation(new Vector3d(0,0,5));
			tgauxZ = new TransformGroup(tr3d);
			cil = new Cylinder(0.25f, 0.5f);
			ca = new ColoringAttributes(0,0,1,ColoringAttributes.SHADE_GOURAUD);
			app = new Appearance();
			app.setColoringAttributes(ca);
			cil.setAppearance(app);
			tgauxZ.addChild(cil);
			bgaux.addChild(tgauxZ);
			miLocale.addBranchGraph(bgaux);
		}

		// Establecemos el ViewPlatform
		miViewingPlatform = new ViewingPlatform(3);
		miViewingPlatform.getViewPlatform().setViewAttachPolicy(View.NOMINAL_HEAD);
		TGV.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		TGV.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D t = new Transform3D();

		TGV.setTransform(t);

		TGVista = miViewingPlatform.getViewPlatformTransform();

		//Esta 'conducta' maneja el teclado sin importarle los obstaculos
		KeyNavigatorBehavior conducta = new KeyNavigatorBehavior(TGVista);
		conducta.setSchedulingBounds(new BoundingSphere(new Point3d(),1000.0));

		//Anyadimos interaccion con el mouse

		MouseTranslate mt = new MouseTranslate();
		mt.setTransformGroup(TGVista);
		mt.setSchedulingBounds(new BoundingSphere(new Point3d(),1000.0));

		//Uncomment next lines for zoomming camera by mouse middle button
		//MouseWheelZoom mwz = new MouseWheelZoom();
		//mwz.setTransformGroup(TGVista);
		//mwz.setSchedulingBounds(new BoundingSphere(new Point3d(),1000.0));

		MouseZoom mz = new MouseZoom();
		mz.setTransformGroup(TGVista);
		mz.setSchedulingBounds(new BoundingSphere(new Point3d(),1000.0));

		TGV.addChild(miViewingPlatform);

		miBranchGroup.addChild(TGV);

		miBranchGroup.addChild(conducta);
		miBranchGroup.addChild(mt);
		miBranchGroup.addChild(mz);

		//anyadimos un fondo de color blanco
		Background bg = new Background(new Color3f(1,1,1));
		bg.setApplicationBounds(new BoundingSphere(new Point3d(), 1000.0));
		miBranchGroup.addChild(bg);

		// Anyadimos la rama de vista al arbol
		miLocale.addBranchGraph(miBranchGroup);

		miVista.attachViewPlatform(miViewingPlatform.getViewPlatform());
	}

	public void cambiarVistaCenital()
	{
		double [] or = {0, 1, 0};

		cambiarVista(new Vector3f(0, 20, 0), or);
	}

	public void cambiarVistaRobot()
	{
		double [] or = {0, 0, 1};

		cambiarVista(new Vector3f(), or);
	}


	public void cambiarVista(Vector3f posicion, double[]orientacion)
	{
		Transform3D t;
		Matrix3d candidata;
		double angle;
		Vector3D v_or = new Vector3D(orientacion);
		Vector3D ejeZ = new Vector3D(0, 0, 1);
		Vector3D director;

		//casos degenerados
		if(v_or.getZ()==1)
		{
			director = new Vector3D(0, 1, 0);
			angle = 0;
		}
		else if(v_or.getZ()==-1)
		{
			director = new Vector3D(0, 1, 0);
			angle = Math.PI;
		}
		else
		{
			director = ejeZ.crossProduct(v_or);
			angle = Math.asin(director.module);
			director.normalize();
		}

		double []rot_mat = director.generalRotationMatrix(angle);
		t = new Transform3D();
		candidata = new Matrix3d(rot_mat);
		t.setRotation(candidata);
		t.setTranslation(posicion);
		try
		{
			TGVista.setTransform(t);
		} catch(BadTransformException e)
		{
			System.out.println(e.toString());
		}
		this.actualizarVista();
		if(menu!=null)
			menu.poseUpdate(globalx, globaly, globalz, orx, ory, orz);

	}

	public void actualizarVista()
	{
		Transform3D orig = new Transform3D();
		Vector3d vector = new Vector3d();
		Matrix3f mat = new Matrix3f();

		TGVista.getTransform(orig);
		orig.get(mat, vector);
		globalx = (float)vector.x;
		globaly = (float)vector.y;
		globalz = (float)vector.z;

		orx = -mat.m02;
		ory = -mat.m12;
		orz = -mat.m22;
	}

	public void saveScreenData(ScreenData selected, String name, String path)
	{
		ScreenData obj = selected;
		if(DEBUG)
		{
			System.out.println("Guardamos el fichero: "+name);
			System.out.println("En el directorio: "+path);
		}
		obj.writeData(name, path);
	}

	public void saveJIP3D(ScreenData selected, String name, String path)
	{
		if(DEBUG)
		{
			System.out.println("Guardamos "+path+name);
		}
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
			fos = new FileOutputStream(path+name);
			out = new ObjectOutputStream(fos);
			out.writeObject(selected);
			out.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public void openJIP3D(String name, String path)
	{
		ScreenData data;
		if(DEBUG)
			System.out.println("Abrimos "+path+name);
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try
		{
			fis = new FileInputStream(path+name);
			in = new ObjectInputStream(fis);
			data = (ScreenData)in.readObject();
			data.scr_opt.is_visible = true;
			scr_data.add(data);
			createSelection();
			this.reDraw();
		} catch(IOException ex)
		{
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public void addScreenData(int type, String name, String path)
	{
		switch(type)
		{
		case 1:
			addScreenPointSet3D(name, path);
			break;
		case 2:
			addScreenPlaneSet3D(name, path);
			break;
		case 3:
			addScreenTraj2D(name, path);
			break;
		case 4:
			addScreenTraj3D(name, path);
			break;
		case 5:
			addScreenFeat2D(name, path);
			break;
		}
		createSelection();
		this.reDraw();
	}

	private void addScreenFeat2D(String name, String path)
	{
		FeatureSet2D f2d = new FeatureSet2D(new ScreenOptions());
		f2d.readData(name, path);
		f2d.scr_opt.is_visible = true;
		scr_data.add(f2d);

	}

	private void addScreenPointSet3D(String name, String path)
	{
		PointSet3D ps3d = new PointSet3D(new ScreenOptions());
		ps3d.readData(name, path);
		ps3d.scr_opt.is_visible = true;
		scr_data.add(ps3d);

	}

	private void addScreenPlaneSet3D(String name, String path)
	{
		PlaneSet3D ps3d = new PlaneSet3D(new ScreenOptions());
		ps3d.readData(name, path);
		ps3d.scr_opt.is_visible = true;
		scr_data.add(ps3d);

	}

	private void addScreenTraj2D(String name, String path)
	{
		Trajectory2D tr2d = new Trajectory2D(new ScreenOptions(), 1.0);
		tr2d.readData(name, path);
		tr2d.scr_opt.is_visible = true;
		scr_data.add(tr2d);
	}

	private void addScreenTraj3D(String name, String path)
	{
		Trajectory3D tr3d = new Trajectory3D(new ScreenOptions());
		tr3d.readData(name, path);
		tr3d.scr_opt.is_visible = true;
		scr_data.add(tr3d);
	}

	/**
	 * Re-Draw all elements loaded into the 3D Scene
	 */
	public void reDraw()
	{
		BranchGroup BG;
		for(ScreenData it: scr_data)
		{
			BG = it.draw();
			if(BG!=null)
			miLocale.addBranchGraph(BG);
		}
	}

	public void reDraw(int pos)
	{
		ScreenData elem = scr_data.get(pos);
		reDraw(elem);
	}

	public void reDraw(ScreenData elem)
	{
		BranchGroup BG = elem.draw();
		if(BG!=null)
			miLocale.addBranchGraph(BG);
	}


	public ArrayList<String> getNames(int valid_types, ArrayList<Integer> positions)
	{
		ArrayList<String> ret = new ArrayList<String>();
		int cont;
		int tam = scr_data.size();
		ScreenData scr_obj;

		for(cont=0;cont<tam;cont++)
		{
			scr_obj = scr_data.get(cont);
			if((scr_obj.getType() & valid_types)!=0)
			{
				ret.add(scr_obj.name);
				positions.add(cont);
			}
		}

		return ret;
	}

	public ArrayList<String> getNames(int valid_types)
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(ScreenData obj: scr_data)
		{
			if((obj.getType() & valid_types)!=0)
				ret.add(obj.name);
		}
		return ret;
	}

	public void createSelection()
	{
		menu.createSelect(scr_data);
	}

	public void removeScreenData(ScreenData object)
	{
		BranchGroup BG = object.BGPaint;
		if(BG!=null&&BG.isLive()) BG.detach();
		scr_data.remove(object);
		createSelection();
		reDraw();

	}

	public boolean launchFunction(Function3D func, ScreenData selected)
	{
		FunctionParamDialog pdiag;


		if(!func.loadData(selected))
		{
			menu.dialogos.error("Incorrect input data type", "MyCanvas3D::launchFunction Error");
			return false;
		}
		else
		{
			// get Parameters
			if(!func.param_list.isEmpty())
			{
				pdiag = new FunctionParamDialog(menu.dialogos, func.param_list, scr_data);
				pdiag.setModal(true);
				pdiag.setVisible(true);
				if(pdiag.isCancelled()) return false;
			}
			//finally, we launch the function
			func.start();
		}
		return true;

	}

	public JIP3DFunctionList getFunction3DList()
	{
		return function_list;
	}

	/**
	 * This method is called when a function is detected to be finished successfully
	 */
	public void functionEnded(Function3D func)
	{
		int cont, tam;
		if(!func.cancelled && func.result_list!=null)
		{
			tam = func.result_list.size();
			for(cont=0;cont<tam;cont++)
				scr_data.add(func.result_list.get(cont));
		}
		menu.endFunctionAction(func.name, func.elapsed_time);
		current_func = null;
		createSelection();
		this.reDraw();
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if(action.charAt(0) == 'F') //launch function
		{
			if(current_func == null)
			{
				current_func = function_list.getJIPFunction3D(action);
				current_func.setCanvas(this);
				current_func.name = action;
				current_func.dialog = menu.dialogos;
				menu.startFunction(current_func.name);
				func_panel.setFunction(current_func, menu.getProgressBar());
				if(launchFunction(current_func, menu.getObjectSelected()))
					func_panel.start();
				else
				{
					current_func = null;
					menu.endFunctionAction(null, 0);
				}

			}
			return;
		}
		if(action.equals("Remove Object")) //cancel function
		{
			if(current_func!=null)
			{
				current_func.stop();
			}
			return;
		}
	}
}
