package javavis.jip2d.gui;

import java.awt.event.ActionListener;
import java.util.Properties;

import javavis.Commons;
import javavis.jip2d.base.JIPFunctionList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


/**
* 	Class which has the menu bar of the main program. Here is created the
* upper menu, with all its deployed menus.
*/

public class MenuBarGui2D extends JMenuBar {
	public static final long serialVersionUID = 8044804452084319776L;

	public JMenuItem nnew;
	public JMenuItem open;
	public JMenuItem capture;
	public JMenuItem save;
	public JMenuItem saveas;
	public JMenuItem savejpg;
	public JMenuItem exit;
	public JMenuItem zoom;
	public JMenuItem osize;
	public JMenuItem bcolor;
	public JMenuItem pcolor;
	public JMenuItem scolor;
	public JMenuItem cscolor;
	public JMenuItem pointcolor;
	public JMenuItem cpointcolor;
	public JMenuItem polycolor;
	public JMenuItem cpolycolor;
	public JMenuItem edgecolor;
	public JMenuItem hpoint;
	public JMenuItem hsegment;
	public JMenuItem hpolygon;
	public JMenuItem hedge;
	public JMenuItem vbitmap;
	public JMenuItem vsegment;
	public JMenuItem vpoint;
	public JMenuItem vedge;
	public JMenuItem vpolygon;
	public JMenuItem vgeom;
	public JMenuItem vfunc;
	public JMenuItem datasegment;
	public JMenuItem iscale;
	public JMenuItem slsegment;
	public JMenuItem sl2points;
	public JMenuItem sselseg;
	public JMenuItem units;
	public JMenuItem undo;
	public JMenuItem redo;
	public JMenuItem renameseq;
	public JMenuItem renameframe;
	public JMenuItem dupframe;
	public JMenuItem addframe;
	public JMenuItem delframe;
	public JMenuItem addbands;
	public JMenuItem delband;
	public JMenuItem extband;
	public JMenuItem caponeimg;
	public JMenuItem capvideo;
	public JMenuItem nocapvideo;
	public JMenuItem importascii;
	public JMenuItem expascii;
	public JMenuItem dellsegment;
	public JMenuItem dellpoint;
	public JMenuItem delpoly;
	public JMenuItem delselec;
	public JMenuItem addgeom;
	public JMenuItem addvoidgeom;
	public JMenuItem segmentframe;
	public JMenuItem pointframe;
	public JMenuItem polyframe;
	public JMenuItem emptyall;
	public JMenuItem emptypoints;
	public JMenuItem emptysegments;
	public JMenuItem emptypoly;
	public JMenuItem about;
	public JMenuItem help;

	/**
	*    Class constructor.
	* @param oyente ActionListener
	* @param funclist2d List of aplicable functions which we have to put in the menu
	* @param lastAction Last action which can be undoed to put its name in the
	* undo option.
	*/
	public MenuBarGui2D(ActionListener oyente, JIPFunctionList funclist,
			Properties prop) {
		JMenu menu = new JMenu(prop.getProperty("File"));
		add(menu);

		nnew = new JMenuItem(prop.getProperty("New"), Commons.getIcon("new.jpg"));
		nnew.addActionListener(oyente);
		menu.add(nnew);

		open = new JMenuItem(prop.getProperty("Open"), Commons.getIcon("open.jpg"));
		open.addActionListener(oyente);
		menu.add(open);

		capture= new JMenuItem(prop.getProperty("Capture"), Commons.getIcon("capture.gif"));
		capture.addActionListener(oyente);
		menu.add(capture);

		save = new JMenuItem(prop.getProperty("Save"), Commons.getIcon("guardar_ascii.jpg"));
		save.addActionListener(oyente);
		menu.add(save);

		saveas = new JMenuItem(prop.getProperty("SaveAs"), Commons.getIcon("guardar_ascii.jpg"));
		saveas.addActionListener(oyente);
		menu.add(saveas);

		savejpg = new JMenuItem(prop.getProperty("SaveJPG"), Commons.getIcon("guardar_ascii.jpg"));
		savejpg.addActionListener(oyente);
		menu.add(savejpg);

		menu.addSeparator();

		exit = new JMenuItem(prop.getProperty("Exit"), Commons.getIcon("salir.jpg"));
		exit.addActionListener(oyente);
		menu.add(exit);

		menu = new JMenu(prop.getProperty("Appearance"));
		add(menu);

		zoom = new JMenuItem(prop.getProperty("Zoom"), Commons.getIcon("zoom.jpg"));
		zoom.addActionListener(oyente);
		menu.add(zoom);

		osize = new JMenuItem(prop.getProperty("OrigSize"));
		osize.addActionListener(oyente);
		menu.add(osize);

		menu.addSeparator();

		bcolor = new JMenuItem(prop.getProperty("BackColor"));
		bcolor.addActionListener(oyente);
		menu.add(bcolor);

		pcolor = new JMenuItem(prop.getProperty("PanelColor"));
		pcolor.addActionListener(oyente);
		menu.add(pcolor);

		scolor = new JMenuItem(prop.getProperty("SegmentColor"));
		scolor.addActionListener(oyente);
		menu.add(scolor);

		cscolor = new JMenuItem(prop.getProperty("CurrentSegmentColor"));
		cscolor.addActionListener(oyente);
		menu.add(cscolor);

		pointcolor = new JMenuItem(prop.getProperty("PointColor"));
		pointcolor.addActionListener(oyente);
		menu.add(pointcolor);

		cpointcolor = new JMenuItem(prop.getProperty("CurrentPointColor"));
		cpointcolor.addActionListener(oyente);
		menu.add(cpointcolor);

		polycolor = new JMenuItem(prop.getProperty("PolygonColor"));
		polycolor.addActionListener(oyente);
		menu.add(polycolor);

		cpolycolor = new JMenuItem(prop.getProperty("CurrentPolygonColor"));
		cpolycolor.addActionListener(oyente);
		menu.add(cpolycolor);

		edgecolor = new JMenuItem(prop.getProperty("EdgeColor"));
		edgecolor.addActionListener(oyente);
		menu.add(edgecolor);

		menu.addSeparator();

		hsegment = new JCheckBoxMenuItem(prop.getProperty("HighlightSegment"), false);
		hsegment.addActionListener(oyente);
		menu.add(hsegment);

		hpoint = new JCheckBoxMenuItem(prop.getProperty("HighlightPoint"), false);
		hpoint.addActionListener(oyente);
		menu.add(hpoint);

		hpolygon = new JCheckBoxMenuItem(prop.getProperty("HighlightPolygon"), true);
		hpolygon.addActionListener(oyente);
		menu.add(hpolygon);

		hedge = new JCheckBoxMenuItem(prop.getProperty("HighlightEdge"), true);
		hedge.addActionListener(oyente);
		menu.add(hedge);

		menu = new JMenu(prop.getProperty("View"));
		add(menu);

		vbitmap = new JCheckBoxMenuItem(prop.getProperty("ViewBitmap"), true);
		vbitmap.addActionListener(oyente);
		menu.add(vbitmap);

		vsegment = new JCheckBoxMenuItem(prop.getProperty("ViewSegment"), true);
		vsegment.addActionListener(oyente);
		menu.add(vsegment);

		vpoint = new JCheckBoxMenuItem(prop.getProperty("ViewPoint"), true);
		vpoint.addActionListener(oyente);
		menu.add(vpoint);

		vpolygon = new JCheckBoxMenuItem(prop.getProperty("ViewPolygon"), true);
		vpolygon.addActionListener(oyente);
		menu.add(vpolygon);

		vedge = new JCheckBoxMenuItem(prop.getProperty("ViewEdge"), true);
		vedge.addActionListener(oyente);
		menu.add(vedge);

		menu.addSeparator();

		vgeom = new JMenuItem(prop.getProperty("ViewGeometry"), Commons.getIcon("toolbar.jpg"));
		vgeom.addActionListener(oyente);
		menu.add(vgeom);

		vfunc = new JMenuItem(prop.getProperty("ViewFunctions"), Commons.getIcon("toolbarf.jpg"));
		vfunc.addActionListener(oyente);
		menu.add(vfunc);

		menu = new JMenu(prop.getProperty("Scale"));
		add(menu);

		iscale = new JMenuItem(prop.getProperty("IntroduceScale"));
		iscale.addActionListener(oyente);
		menu.add(iscale);

		slsegment = new JMenuItem(prop.getProperty("ScaleLastSegment"));
		slsegment.addActionListener(oyente);
		menu.add(slsegment);

		sl2points = new JMenuItem(prop.getProperty("ScaleLast2Points"));
		sl2points.addActionListener(oyente);
		menu.add(sl2points);

		sselseg = new JMenuItem(prop.getProperty("ScaleSelectedSegment"));
		sselseg.addActionListener(oyente);
		menu.add(sselseg);

		units = new JMenuItem(prop.getProperty("Units"));
		units.addActionListener(oyente);
		menu.add(units);

		menu = funclist.getFunctionMenu(prop.getProperty("Functions"), oyente);
		add(menu);

		menu = new JMenu(prop.getProperty("Sequence"));
		add(menu);

		undo = new JMenuItem(prop.getProperty("Undo"), Commons.getIcon("undo.jpg"));
		undo.addActionListener(oyente);
		menu.add(undo);

		redo = new JMenuItem(prop.getProperty("Redo"), Commons.getIcon("redo.jpg"));
		redo.addActionListener(oyente);
		menu.add(redo);
		menu.addSeparator();

		renameseq = new JMenuItem(prop.getProperty("RenameSequence"));
		renameseq.addActionListener(oyente);
		menu.add(renameseq);

		renameframe = new JMenuItem(prop.getProperty("RenameFrame"));
		renameframe.addActionListener(oyente);
		menu.add(renameframe);
		menu.addSeparator();

		dupframe = new JMenuItem(prop.getProperty("DuplicateFrame"));
		dupframe.addActionListener(oyente);
		menu.add(dupframe);

		addframe = new JMenuItem(prop.getProperty("AddFrames"));
		addframe.addActionListener(oyente);
		menu.add(addframe);

		delframe = new JMenuItem(prop.getProperty("DeleteFrame"));
		delframe.addActionListener(oyente);
		menu.add(delframe);
		menu.addSeparator();

		addbands = new JMenuItem(prop.getProperty("AddBands"));
		addbands.addActionListener(oyente);
		menu.add(addbands);

		delband = new JMenuItem(prop.getProperty("DeleteBand"));
		delband.addActionListener(oyente);
		menu.add(delband);

		extband = new JMenuItem(prop.getProperty("ExtractBand"));
		extband.addActionListener(oyente);
		menu.add(extband);
		menu.addSeparator();

		caponeimg = new JMenuItem(prop.getProperty("CaptureOneImage"));
		caponeimg.addActionListener(oyente);
		menu.add(caponeimg);

		capvideo = new JMenuItem(prop.getProperty("CaptureVideo"));
		capvideo.addActionListener(oyente);
		menu.add(capvideo);

		nocapvideo = new JMenuItem(prop.getProperty("StopCaptureVideo"));
		nocapvideo.addActionListener(oyente);
		menu.add(nocapvideo);

		menu = new JMenu(prop.getProperty("Geometry"));
		add(menu);

		datasegment = new JMenuItem(prop.getProperty("SegmentData"));
		datasegment.addActionListener(oyente);
		menu.add(datasegment);

		importascii = new JMenuItem(prop.getProperty("ImportASCII"));
		importascii.addActionListener(oyente);
		menu.add(importascii);

		expascii = new JMenuItem(prop.getProperty("ExportASCII"));
		expascii.addActionListener(oyente);
		menu.add(expascii);

		menu.addSeparator();
		dellsegment = new JMenuItem(prop.getProperty("DeleteLastSegment"));
		dellsegment.addActionListener(oyente);
		menu.add(dellsegment);

		dellpoint = new JMenuItem(prop.getProperty("DeleteLastPoint"));
		dellpoint.addActionListener(oyente);
		menu.add(dellpoint);

		delpoly = new JMenuItem(prop.getProperty("DeletePolygon"));
		delpoly.addActionListener(oyente);
		menu.add(delpoly);

		delselec = new JMenuItem(prop.getProperty("DeleteSelected"));
		delselec.addActionListener(oyente);
		menu.add(delselec);
		menu.addSeparator();

		addgeom = new JMenuItem(prop.getProperty("AddGeometrySequence"));
		addgeom.addActionListener(oyente);
		menu.add(addgeom);

		JMenu agregar2 = new JMenu(prop.getProperty("AddVoidGeometricFrame"));

		pointframe = new JMenuItem(prop.getProperty("PointFrame"));
		pointframe.addActionListener(oyente);
		agregar2.add(pointframe);

		segmentframe = new JMenuItem(prop.getProperty("SegmentFrame"));
		segmentframe.addActionListener(oyente);
		agregar2.add(segmentframe);

		polyframe = new JMenuItem(prop.getProperty("PolygonFrame"));
		polyframe.addActionListener(oyente);
		agregar2.add(polyframe);

		menu.add(agregar2);

		agregar2 = new JMenu(prop.getProperty("EmptyGeometry"));

		emptyall = new JMenuItem(prop.getProperty("EmptyAll"));
		emptyall.addActionListener(oyente);
		agregar2.add(emptyall);

		emptypoints = new JMenuItem(prop.getProperty("EmptyPoints"));
		emptypoints.addActionListener(oyente);
		agregar2.add(emptypoints);

		emptysegments = new JMenuItem(prop.getProperty("EmptySegments"));
		emptysegments.addActionListener(oyente);
		agregar2.add(emptysegments);

		emptypoly = new JMenuItem(prop.getProperty("EmptyPolygons"));
		emptypoly.addActionListener(oyente);
		agregar2.add(emptypoly);

		menu.add(agregar2);


		menu = new JMenu(prop.getProperty("Help"));
		add(menu);

		help = new JMenuItem(prop.getProperty("Help"));
		help.addActionListener(oyente);
		menu.add(help);
		about = new JMenuItem(prop.getProperty("About"));
		about.addActionListener(oyente);
		menu.add(about);
	}
}
