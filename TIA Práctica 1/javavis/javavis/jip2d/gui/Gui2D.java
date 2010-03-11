package javavis.jip2d.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import javavis.Commons;
import javavis.Gui;
import javavis.base.Dialog;
import javavis.base.FileType;
import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.JIPImageFilter;
import javavis.base.JIPToolkit;
import javavis.jip2d.base.*;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPBmpByte;
import javavis.jip2d.base.bitmaps.JIPBmpFloat;
import javavis.jip2d.base.bitmaps.JIPBmpShort;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;
import javavis.jip2d.functions.FColorToGray;
import javavis.jip2d.gui.ALLImageFilter;
import javavis.jip2d.gui.Canvas2D;
import javavis.jip2d.gui.InfoPanelBottom;
import javavis.jip2d.gui.InfoPanelGeom;
import javavis.jip2d.util.WebCam;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;

import com.centerkey.utils.BareBonesBrowserLaunch;


/**
 * This class loads all menus and the ActionListener is
 * implemented.
 */
public class Gui2D extends JPanel implements ActionListener {
	private static final long serialVersionUID = 5359289125586625385L;

	private static Logger logger = Logger.getLogger(Gui2D.class);

	Canvas2D canvas;

	/**
	 * Scroll pane of canvas
	 */
	JScrollPane canvasScr;

	/** Last directory accessed in load*/
	String ultimoDirectorio;

	/**
	 * Type of the open file
	 */
	FileType fichAbierto = null;

	/**
	 * Information panel
	 */
	InfoPanelGeom infoGeom;

	InfoPanelBottom infoBottom;

	JSplitPane spane;

	/** Function list */
	JIPFunctionList funclist;

	/** ArrayList for keeping previous sequences. Allows to implement Undo/Redo */
	ArrayList<JIPSequence> undoSeq;

	/** Index of the undo sequence */
	int undoIndex;

	/** Reference to the main mainGui */
	Gui mainGui;

	/** Last function which stores the last param values */
	JIPFunction lastFuncApplied = null;

	/** Flag indicating if file is saved */
	boolean isSaved;

	/** Maximum number of undo actions */
	static final int UNDO_LENGTH = 10;

	static final int GENERALTOOLBAR = 1;

	static final int FUNCTIONTOOLBAR = 2;

	DoAction acciones;

    JButton changeViewButton;

    JScrollPane panelFuncList;

    Properties prop;

	/** Constructor */
	public Gui2D(Gui frameAux, Properties propi, JIPFunctionList funclisti) {
		mainGui=frameAux;
		funclist = funclisti;
		acciones = new DoAction(this);
		undoSeq = new ArrayList<JIPSequence>();
		undoIndex = 0;
		prop = propi;
		isSaved = true;

		setLayout(new BorderLayout());

		spane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        ultimoDirectorio = null;

		canvas = new Canvas2D(400, 300);
		canvasScr = new JScrollPane(canvas,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		canvasScr.setViewportBorder(new BevelBorder(BevelBorder.RAISED));

		infoGeom = new InfoPanelGeom(canvas, prop);
		infoBottom = new InfoPanelBottom(canvas, prop);

		canvas.assoccInfoGeom(infoGeom);
		canvas.assocInfoBottom(infoBottom);

		spane.setDividerLocation(600);
		spane.setResizeWeight(1);
		spane.setOneTouchExpandable(true);

		spane.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
		spane.setLeftComponent(canvasScr);
		spane.setRightComponent(infoGeom);

		JToolBar tb = new JToolBar();
		addButtonsGeneral(tb);
		tb.setRollover(true);
		tb.setFloatable(false);

        JPanel auxPanel =  new JPanel();

        addButtonsFunctions(auxPanel);

        panelFuncList= new JScrollPane(auxPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        setLayout(new BorderLayout());
        add(tb,BorderLayout.NORTH);
        add(spane,BorderLayout.CENTER);
        add(infoBottom,BorderLayout.SOUTH);
	}

    protected void addButtonsFunctions(JPanel jp){
        int heightTB = 586;
        int tamIconX=32, tamIconY=31, x=0, h=0;
        JToolBar tba[] = new JToolBar[100];
        int contTB = 0;

        tba[0] = new JToolBar();
        tba[0].setBounds(x, 0, tamIconX, heightTB);
        tba[0].setFloatable(false);
        tba[0].setOrientation(JToolBar.VERTICAL);
        tba[0].setAutoscrolls(false);
        tba[0].setSize(tamIconX,0);

        JButton button = null;
        int numF = funclist.getNumFunctions();
        String nomF = null;
        for (FunctionGroup f : FunctionGroup.values()) {
            for (int i=0; i < numF; i++) {
                if (funclist.getFuncgroups()[i] == f) {
                    nomF = funclist.getName(i);
                    button = new JButton(Commons.getIcon(nomF+".jpg"));
                    button.setToolTipText(nomF);
                    button.setActionCommand(nomF);
                    button.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            acciones.functions(funclist.getJIPFunction(e.getActionCommand()));
                        }});
                    tba[contTB].add(button);
                    h += tamIconY;
                    if (h+tamIconY > heightTB) {
                        contTB++;
                        x += tamIconX;
                        h = 0;
                        tba[contTB] = new JToolBar();
                        tba[contTB].setBounds(x, 0, tamIconX, heightTB);
                        tba[contTB].setFloatable(false);
                        tba[contTB].setOrientation(JToolBar.VERTICAL);
                        tba[contTB].setAutoscrolls(false);
                    }
                }
            }
            if (h>0) {
                tba[contTB].addSeparator();
                h += 2;
            }
        }

        for (int i=0; i<=contTB; i++)
            jp.add(tba[i]);
    }

	protected void addButtonsGeneral (JToolBar t) {
		JButton button = null;

		button = new JButton(Commons.getIcon("new.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("New"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acciones.nnew();
			}
		});
		t.add(button);

		button = new JButton(Commons.getIcon("open.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("Open"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JIPToolkit.askForFile(ultimoDirectorio, true, prop, mainGui);
				}catch (JIPException ex) {logger.error(ex);}
			}
		});
		t.add(button);

		button = new JButton(Commons.getIcon("capture.gif"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("Capture"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					JIPToolkit.askForCapture(prop, mainGui);
				}catch (JIPException ex) {logger.error(ex);}
			}
		});
		t.add(button);

		button = new JButton(Commons.getIcon("guardar_ascii.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("Save"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acciones.save();
			}
		});
		t.add(button);

		button = new JButton(Commons.getIcon("guardar_ascii.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("SaveJPG"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acciones.save_as_jpg();
			}
		});
		t.add(button);

		button = new JButton(Commons.getIcon("background.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("BackColor"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acciones.backgroundColor();
			}
		});
		t.add(button);

		button = new JButton(Commons.getIcon("panelcolor.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("PanelColor"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acciones.panelColor();
			}
		});
		t.add(button);

		button = new JButton(Commons.getIcon("segmentcolor.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("SegmentColor"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acciones.segmentColor();
			}
		});
		t.add(button);

		button = new JButton(Commons.getIcon("pointcolor.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("PointColor"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acciones.pointColor();
			}
		});
		t.add(button);

		button = new JButton(Commons.getIcon("polygoncolor.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("PolygonColor"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acciones.polygonColor();
			}
		});
		t.add(button);

		button = new JButton(Commons.getIcon("edgecolor.jpg"));
		button.setBorderPainted(false);
		button.setToolTipText(prop.getProperty("EdgeColor"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acciones.edgeColor();
			}
		});
		t.add(button);

		changeViewButton = new JButton(prop.getProperty("Functions"));
		changeViewButton.putClientProperty("JButton.buttonType", "roundRect");
        changeViewButton.setToolTipText(prop.getProperty("Functions"));
        changeViewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acciones.changeRightView();
			}
		});
		t.add(changeViewButton);
	}

	/**
	 * Event handler.
	 * @param e Action produced
	 */
	public void actionPerformed(ActionEvent e) {
		/***********************************************************************
		 * * New
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().nnew) {
			acciones.nnew();
			return;
		}

		/***********************************************************************
		 * * Open Image
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().open) {
			try {
				JIPToolkit.askForFile(ultimoDirectorio, true, prop, mainGui);
			}catch (JIPException ex) {logger.error(ex);}
			return;
		}

		/***********************************************************************
		 * * Capture Image from Webcam
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().capture) {
			try {
				JIPToolkit.askForCapture(prop, mainGui);
			}catch (JIPException ex) {logger.error(ex);}
			return;
		}

		/***********************************************************************
		 * * Save
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().save) {
			acciones.save();
			return;
		}

		/***********************************************************************
		 * * Save as
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().saveas) {
			acciones.save_as();
			return;
		}

		/***********************************************************************
		 * * Save jpg
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().savejpg) {
			acciones.save_as_jpg();
			return;
		}

		/***********************************************************************
		 * * Exit
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().exit) {
			JIPToolkit.exit(prop, mainGui);
		}

		/***********************************************************************
		 * * Color selection
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().bcolor) {
			acciones.backgroundColor();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().pcolor) {
			acciones.panelColor();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().scolor) {
			acciones.segmentColor();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().cscolor) {
			canvas.currentLineColor(JColorChooser.showDialog(this,
					"Select color", new Color(255, 255, 255)));
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().pointcolor) {
			acciones.pointColor();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().cpointcolor) {
			canvas.currentPointColor(JColorChooser.showDialog(this,
					"Select color", new Color(255, 255, 255)));
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().polycolor) {
			acciones.polygonColor();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().cpolycolor) {
			canvas.currentPolygonColor(JColorChooser.showDialog(this,
					"Select color", new Color(255, 255, 255)));
			canvasScr.repaint();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().edgecolor) {
			acciones.edgeColor();
			return;
		}

		/***********************************************************************
		 * * Highlight
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().hsegment) {
			canvas.enhanceSegments(((JCheckBoxMenuItem) e.getSource()).getState());
			canvasScr.repaint();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().hpoint) {
			canvas.enhancePoints(((JCheckBoxMenuItem) e.getSource()).getState());
			canvasScr.repaint();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().hpolygon) {
			canvas.enhancePolygon(((JCheckBoxMenuItem) e.getSource()).getState());
			canvasScr.repaint();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().hedge) {
			canvas.enhanceEdges(((JCheckBoxMenuItem) e.getSource()).getState());
			canvasScr.repaint();
			return;
		}

		/***********************************************************************
		 * * View
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().vbitmap) {
			canvas.bitmapVisible(((JCheckBoxMenuItem) e.getSource()).getState());
			canvasScr.repaint();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().vsegment) {
			canvas.segmentsVisible(((JCheckBoxMenuItem) e.getSource()).getState());
			canvasScr.repaint();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().vpoint) {
			canvas.pointsVisible(((JCheckBoxMenuItem) e.getSource()).getState());
			canvasScr.repaint();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().vpolygon) {
			canvas.polygonsVisible(((JCheckBoxMenuItem) e.getSource()).getState());
			canvasScr.repaint();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().vedge) {
			canvas.edgesVisible(((JCheckBoxMenuItem) e.getSource()).getState());
			canvasScr.repaint();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().vgeom) {
		    panelFuncList.setVisible(false);
            spane.setRightComponent(infoGeom);
            infoGeom.setVisible(true);
            changeViewButton.setText(prop.getProperty("Functions"));
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().vfunc) {
            infoGeom.setVisible(false);
            spane.setRightComponent(panelFuncList);
            panelFuncList.setVisible(true);
            changeViewButton.setText(prop.getProperty("Geometry"));
			return;
		}

		/***********************************************************************
		 * * Segments data
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().datasegment) {
			String cols[] = { prop.getProperty("Index"), prop.getProperty("StartX"),
					prop.getProperty("StartY"), prop.getProperty("EndX"),
					prop.getProperty("EndY"), prop.getProperty("Length") };
			Object[][] datos = canvas.getSegmentData();
			JTable tabla = new JTable(datos, cols);
			JDialog dialog = new JDialog(mainGui, prop.getProperty("SegmentData"), true);
			JScrollPane tablaScroll = new JScrollPane(tabla);
			tablaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			JPanel pan = new JPanel();
			pan.add(tablaScroll);
			dialog.setContentPane(pan);
			dialog.setSize(new Dimension(500, 400));
			dialog.setResizable(false);
			dialog.setLocationRelativeTo(this);
			dialog.pack();
			dialog.setVisible(true);
			return;
		}

		/***********************************************************************
		 * * Scale
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().iscale) {
			String esc = JOptionPane.showInputDialog(this, prop.getProperty("IntroScaleFactor"),
					prop.getProperty("IntroduceScale"), JOptionPane.QUESTION_MESSAGE);
			float f = -1f;
			try {
				if (esc.length() > 0)
					f = Float.valueOf(esc);
			} catch (Exception err) {logger.error(err);}
			if (f != -1)
				infoGeom.changeScale(f);
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().slsegment) {
			float longitud = canvas.getLengthLastSegment();
			if (longitud == -1)
				return;
			String esc = JOptionPane.showInputDialog(this,
					prop.getProperty("IntroLengthLastSegment"),
					prop.getProperty("ScaleLastSegment"), JOptionPane.QUESTION_MESSAGE);
			float f = -1f;
			try {
				if (esc.length() > 0)
					f = Float.valueOf(esc);
			} catch (Exception err) {}
			if (f != -1)
				infoGeom.changeScale(f / longitud);
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().sselseg) {
			float longitud = canvas.getLengthSelectedSegment();
			if (longitud == -1)
				return;
			String esc = JOptionPane.showInputDialog(this,
					prop.getProperty("IntroLengthSelSegment"),
					prop.getProperty("ScaleSelectedSegment"), JOptionPane.QUESTION_MESSAGE);
			float f = -1f;
			try {
				if (esc.length() > 0)
					f = Float.valueOf(esc);
			} catch (Exception err) {logger.error(err);}
			if (f != -1)
				infoGeom.changeScale(f / longitud);
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().sl2points) {
			float longitud = canvas.getDistanceLastPoints();
			if (longitud == -1)
				return;
			String esc = JOptionPane.showInputDialog(this,
					prop.getProperty("IntroDist2Points"),
					prop.getProperty("ScaleLast2Points"), JOptionPane.QUESTION_MESSAGE);
			float f = -1f;
			try {
				if (esc.length() > 0)
					f = Float.valueOf(esc);
			} catch (Exception err) {}
			if (f != -1)
				infoGeom.changeScale(f / longitud);
			return;
		}

		/***********************************************************************
		 * * Units
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().units) {
			infoGeom.changeUnits(JOptionPane.showInputDialog(this,
					prop.getProperty("IntroUnitMeasure"), prop.getProperty("Units"),
					JOptionPane.QUESTION_MESSAGE));
			return;
		}

		/***********************************************************************
		 * * Zoom
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().zoom) {
			String factorS = JOptionPane.showInputDialog(this,
					prop.getProperty("IntroPerc"), prop.getProperty("Zoom"),
					JOptionPane.QUESTION_MESSAGE);
			int f = -1;
			if (factorS != null && factorS.length() > 0)
				f = Integer.valueOf(factorS);
			if (f > 1)
				try {
					canvas.zoomWindow(f);
				}catch (JIPException ex) {logger.error(ex);}
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().osize) {
			if (undoSeq != null) {
				try {
					canvas.setSequence(canvas.getSequence());
					canvas.outView();
					canvas.changeToFrame(canvas.getFrameNum());
				}catch (JIPException ex) {logger.error(ex);}
			} else
				new Dialog(this).information(prop.getProperty("ErrorOSize"), prop.getProperty("Error"));
			return;
		}

		/***********************************************************************
		 * * Sequences
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().renameseq) {
			if (canvas.getSequence() == null)
				return;
			String nombreS = JOptionPane.showInputDialog(this,
					prop.getProperty("IntroNewName"), prop.getProperty("RenameSequence"),
					JOptionPane.QUESTION_MESSAGE);
			canvas.setSequenceName(nombreS);
			try {
				addUndo(canvas.getSequence());
			}catch (JIPException ex) {logger.error(ex);}
			mainGui.setTitle("JavaVis - " + nombreS);
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().renameframe) {
			if (canvas.getSequence() == null)
				return;
			String nombreS = JOptionPane.showInputDialog(this,
					prop.getProperty("IntroNewName"), prop.getProperty("RenameFrame"),
					JOptionPane.QUESTION_MESSAGE);
			try {
				addUndo(canvas.getSequence());
				canvas.setFrameName(nombreS);
			}catch (JIPException ex) {logger.error(ex);}
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().dupframe) {
			if (canvas.getSequence() == null)
				return;
			try {
				canvas.duplicateFrame();
				addUndo(canvas.getSequence());
			}catch (JIPException ex) {logger.error(ex);}
			return;
		}

		/***********************************************************************
		 * * Add frames
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().addframe) {
			if (canvas.getSequence() == null)
				return;
			JFileChooser abreFich = askForFileSave(ultimoDirectorio, true);
			if (abreFich != null) {
				try {
					File fich = abreFich.getSelectedFile();
					if (abreFich.getFileFilter() instanceof ALLImageFilter) {
						Image img = JIPToolkit.getAWTImage(fich.getAbsolutePath());
						if (img == null)
							return;
						JIPImage jipimg = JIPToolkit.getColorImage(img);
						jipimg.setName(fich.getName().substring(0,
								fich.getName().lastIndexOf(".")));
						canvas.addFrame(jipimg);
						addUndo(canvas.getSequence());
					} else if (abreFich.getFileFilter() instanceof JIPImageFilter) {
						JIPSequence sec = JIPToolkit.getSeqFromFile(fich.getAbsolutePath());
						if (sec == null)
							return;
						canvas.addFrames(sec);
						addUndo(canvas.getSequence());
					}
				}catch (JIPException ex) {logger.error(ex);}
			}
			return;
		}

		/***********************************************************************
		 * * Add bands (of the first numFrame from a JIP file with the same rows
		 * and columns)
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().addbands) {
			if (canvas.getSequence() == null)
				return;
			try {
				JIPImage actual = canvas.getSequence().getFrame(canvas.getFrameNum());
				if (actual.getType() == ImageType.COLOR) {
					new Dialog(this).information(prop.getProperty("MessageNoColor"),
							prop.getProperty("Error"));
					return;
				}
				JFileChooser abreFich = askForFileSave(ultimoDirectorio, true);
				JIPSequence sec = null;
				if (abreFich != null) {
					File fich = abreFich.getSelectedFile();
					if (abreFich.getFileFilter() instanceof ALLImageFilter) {
						Image img = JIPToolkit.getAWTImage(fich.getAbsolutePath());
						if (img == null) {
							logger.error("Error reading the image");
							return;
						}
						JIPImage jipimg = JIPToolkit.getColorImage(img);
						FColorToGray fctg = new FColorToGray();
						fctg.setParamValue("GRAY", "BYTE");
						JIPImage jipimg2;
						try {
							jipimg2 = fctg.processImg(jipimg);
							jipimg2.setName(fich.getName().substring(0,
									fich.getName().lastIndexOf(".")));
							sec = new JIPSequence();
							sec.addFrame(jipimg2);
						}
						catch (JIPException ejip) {
							logger.error(ejip);
						}
					} else if (abreFich.getFileFilter() instanceof JIPImageFilter) {
						sec = JIPToolkit.getSeqFromFile(fich.getAbsolutePath());
						if (sec == null) {
							logger.error("Error reading the image");
							return;
						}
					}
					if (checkSize(actual, sec)) {
						JIPImage img = sec.getFrame(0);
						if (img instanceof JIPImgBitmap) {
							for (int j = 0; j < ((JIPImgBitmap)img).getNumBands(); j++)
								((JIPImgBitmap)actual).appendBand(((JIPImgBitmap)img).getAllPixels(j));
							canvas.changeFrame(actual);
							addUndo(canvas.getSequence());
						}
						else {
							logger.error("addbands does not valid for geometric data");
							throw new JIPException("Gui2D.addbands: do not valid for geometric data");
						}
					}
					else {
						logger.error("addnbands size or type are not the same");
						throw new JIPException("Gui2D.addbands: size or type are not the same");
					}
				}
			}catch (JIPException ex) {logger.error(ex);}
			return;
		}

		/***********************************************************************
		 * * Delete bands
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().delband) {
			if (canvas.getSequence() == null) {
				new Dialog(this).information(prop.getProperty("EmptySequence"),
					prop.getProperty("Error"));
				return;
			}
			try {
				JIPImage actual = canvas.getSequence().getFrame(canvas.getFrameNum());
				if (actual instanceof JIPImgGeometric) {
					new Dialog(this).information(prop.getProperty("ErrorGeomData"),
						prop.getProperty("Error"));
					return;
				}
				if (((JIPImgBitmap)actual).getNumBands() > 1) {
					if (actual.getType() == ImageType.COLOR) {
						new Dialog(this).information(prop.getProperty("MessageNoColor2"),
								prop.getProperty("Error"));
						return;
					}
					if (new Dialog(this).confirm(prop.getProperty("MessageDelBand"),
							prop.getProperty("DeleteBand"))) {
						canvas.removeBand();
						addUndo(canvas.getSequence());
					}
				} else
					new Dialog(this).information(prop.getProperty("MessageNumBand"), prop.getProperty("Error"));
			}catch (JIPException ex) {logger.error(ex);}
			return;
		}

		/***********************************************************************
		 * * Extract band
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().extband) {
			if (canvas.getSequence() == null) {
				new Dialog(this).information(prop.getProperty("NoSeqToOperate"),
						prop.getProperty("Attention"));
				return;
			}
			try {
				JIPImage actual = canvas.getSequence().getFrame(canvas.getFrameNum());
				if (actual.getType() == ImageType.COLOR) {
					new Dialog(this).information(prop.getProperty("MessageExtBand"),
							prop.getProperty("Attention"));
					return;
				}
				if (actual instanceof JIPImgGeometric) {
					new Dialog(this).information(prop.getProperty("ErrorGeomData"),
						prop.getProperty("Error"));
					return;
				}
				JIPImgBitmap nueva;
				switch (actual.getType()) {
					case BIT:   nueva = new JIPBmpBit(actual.getWidth(), actual.getHeight());
								break;
					case BYTE:  nueva = new JIPBmpByte(actual.getWidth(), actual.getHeight());
								break;
					case SHORT: nueva = new JIPBmpShort(actual.getWidth(), actual.getHeight());
								break;
					case FLOAT: nueva = new JIPBmpFloat(actual.getWidth(), actual.getHeight());
								break;
					default: nueva=null;
				}
				nueva.setAllPixels(((JIPImgBitmap)actual).getAllPixels(canvas.getBandNum()));
				canvas.changeFrame(nueva);
				addUndo(canvas.getSequence());
			}catch (JIPException ex) {logger.error(ex);}
			return;
		}

		/***********************************************************************
		 * * Delete frame
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().delframe) {
			if (canvas.getSequence().getNumFrames() > 1) {
				if (new Dialog(this).confirm(prop.getProperty("MessageDelFrame"), prop.getProperty("DeleteFrame"))) {
					try {
						canvas.removeFrame();
						addUndo(canvas.getSequence());
					}catch (JIPException ex) {logger.error(ex);}
				}
			} else
				new Dialog(this).information(prop.getProperty("NoSeqToOperate"), prop.getProperty("Error"));
			return;
		}

		/***********************************************************************
		 * * Capture One Image from WebCam
		 **********************************************************************/
		if(e.getSource() == mainGui.getMenuBarGui2D().caponeimg)
		{
			final WebCam wc = new WebCam();

			//New jpanel
			JPanel jpanel = new JPanel();
			jpanel.setLayout(null);

			//Add to a frame
			final JFrame jframe = new JFrame();
			jframe.setSize(250, 175);
			jframe.setContentPane(jpanel);
			jframe.setTitle(prop.getProperty("TitleCapOneImage"));

			//New label from radiobuttons
			JLabel jlabradio = new JLabel(prop.getProperty("CapVideoSize"));
			jlabradio.setBounds(new Rectangle(40,4,100,18));

			//New radiobuttons from image size
			final JRadioButton jrb640 = new JRadioButton();
			jrb640.setBounds(new Rectangle(48, 24, 100, 18));
			jrb640.setName("640x480");
			jrb640.setText("640x480");
			jrb640.setSelected(true);
			final JRadioButton jrb320 = new JRadioButton();
			jrb320.setBounds(new Rectangle(48, 48, 100, 18));
			jrb320.setName("320x240");
			jrb320.setText("320x240");
			final JRadioButton jrb170 = new JRadioButton();
			jrb170.setBounds(new Rectangle(48,72, 100, 18));
			jrb170.setName("170x120");
			jrb170.setText("170x120");

			//New button to capture
			JButton jbcapture = new JButton();
			jbcapture.setBounds(new Rectangle(12,96,100,30));
			jbcapture.setText(prop.getProperty("CapVideoButCapture"));
			jbcapture.addActionListener( new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					jframe.setVisible(false);
					try
					{
						if( jrb640.isSelected())
						{
							wc.setHeight(480);
							wc.setWidth(640);
						}
						if( jrb320.isSelected() )
						{
							wc.setHeight(240);
							wc.setWidth(320);
						}
						if( jrb170.isSelected() )
						{
							wc.setHeight(120);
							wc.setWidth(170);
						}
						//TODO : Erase the next two lines when openCV corrects the bug.
						wc.setHeight(480);
						wc.setWidth(640);

						JIPImage jimg = wc.captureOneImage();
						JIPSequence jsec = new JIPSequence(jimg);
						//Show the image in the screen
						canvas.setSequence(jsec);
						canvas.changeFrame(jimg);
					}
					catch( Exception ex ) {
						logger.error(ex);
					}
				};
			} );


			//New button to canel
			JButton jbcancel = new JButton();
			jbcancel.setBounds(new Rectangle(120,96,100,30));
			jbcancel.setText(prop.getProperty("Cancel"));
			jbcancel.addActionListener( new ActionListener ()
			{
				public void actionPerformed(ActionEvent e)
				{
					jframe.setVisible(false);
				}
			});

			//Group the radiobuttons
			ButtonGroup group = new ButtonGroup();
			group.add(jrb640);
			group.add(jrb320);
			group.add(jrb170);

			//Add to a panel
			jpanel.add(jlabradio,null);
			jpanel.add(jrb640,null);
			jpanel.add(jrb320,null);
			jpanel.add(jrb170,null);
			jpanel.add(jbcapture,null);
			jpanel.add(jbcancel, null);

			//Show the window
			jframe.setVisible(true);


		}

		/***********************************************************************
		 * * Capture Video from WebCam
		 **********************************************************************/
		if(e.getSource() == mainGui.getMenuBarGui2D().capvideo)
		{
			final WebCam wc = new WebCam(canvasScr, canvas);
			final Thread th = new Thread(wc);
			//New jpanel
			JPanel jpanel = new JPanel();
			jpanel.setLayout(null);

			//Add to a frame
			final JFrame jframe = new JFrame();
			jframe.setSize(300, 350);
			jframe.setContentPane(jpanel);
			jframe.setTitle(prop.getProperty("TitleCapVideo"));

			//New label from radiobuttons
			JLabel jlabradio = new JLabel(prop.getProperty("CapVideoSize"));
			jlabradio.setBounds(new Rectangle(24,4,100,18));

			//New radiobuttons from image size
			final JRadioButton jrb640 = new JRadioButton();
			jrb640.setBounds(new Rectangle(32, 24, 100, 18));
			jrb640.setName("640x480");
			jrb640.setText("640x480");
			jrb640.setSelected(true);
			final JRadioButton jrb320 = new JRadioButton();
			jrb320.setBounds(new Rectangle(32, 48, 100, 18));
			jrb320.setName("320x240");
			jrb320.setText("320x240");
			final JRadioButton jrb170 = new JRadioButton();
			jrb170.setBounds(new Rectangle(32,72, 100, 18));
			jrb170.setName("170x120");
			jrb170.setText("170x120");

			//New label from number of frames
			JLabel jlabframes = new JLabel(prop.getProperty("CapVideoFrames"));
			jlabframes.setBounds(new Rectangle(24,96,100,18));

			//New jspinner from frames per second
			SpinnerNumberModel snm = new SpinnerNumberModel(4,1,100,1);
			final JSpinner jspin = new JSpinner(snm);
			jspin.setBounds(new Rectangle(130,96,71,18));

			//New label from function list
			JLabel jlabfunction = new JLabel(prop.getProperty("CapVideoFuctions"));
			jlabfunction.setBounds(new Rectangle(24,125,100,18));

			//New jcombobox from functions
			String []funcaux = funclist.getFuncArray();
			String []functions = new String[funcaux.length+1];
			functions[0] = prop.getProperty("CapVideoNoFunc");
			for(int i = 0; i < funcaux.length; i++ )
				functions[i+1] = funcaux[i];
			final JComboBox jcbfunc = new JComboBox(functions);
			jcbfunc.setBounds(new Rectangle(130,125,120,18));

			//New label from sequences
			JLabel jlabsequences = new JLabel(prop.getProperty("CapVideoSaveSequences"));
			jlabsequences.setBounds(new Rectangle(50,150,125,18));

			//New label from number of sequences
			final JLabel jlabnumsequences = new JLabel(prop.getProperty("CapVideoNumberSequences"));
			jlabnumsequences.setBounds(new Rectangle(24,175,150,18));
			jlabnumsequences.setEnabled(false);

			//New jspinner from number of sequences
			SpinnerNumberModel snmseq = new SpinnerNumberModel(10,2,100,1);
			final JSpinner jspinsequence = new JSpinner(snmseq);
			jspinsequence.setBounds(new Rectangle(175,175,71,18));
			jspinsequence.setEnabled(false);

			//New checkbox from sequences
			final JCheckBox jcbsequences = new JCheckBox();
			jcbsequences.setBounds(new Rectangle(20,150,20,20));
			jcbsequences.setSelected(false);
			jcbsequences.addActionListener(new ActionListener () {
				//Enable some features
				public void actionPerformed(ActionEvent e) {
					if( jcbsequences.isSelected() ) {
						jlabnumsequences.setEnabled(true);
						jspinsequence.setEnabled(true);
					}
					else {
						jlabnumsequences.setEnabled(false);
						jspinsequence.setEnabled(false);
					}
				}
			});

			//New button to capture
			JButton jbcapture = new JButton();
			jbcapture.setBounds(new Rectangle(12,200,100,30));
			jbcapture.setText(prop.getProperty("CapVideoButCapture"));
			jbcapture.addActionListener( new ActionListener()  {
				public void actionPerformed(ActionEvent e) {
					jframe.setVisible(false);
					try {
						if( jrb640.isSelected()) {
							wc.setHeight(480);
							wc.setWidth(640);
						}
						if( jrb320.isSelected() ) {
							wc.setHeight(240);
							wc.setWidth(320);
						}
						if( jrb170.isSelected() ) {
							wc.setHeight(120);
							wc.setWidth(170);
						}
						//TODO : Erase the next two lines when openCV corrects the bug.
						wc.setHeight(480);
						wc.setWidth(640);

						wc.setNumberFrames(Integer.parseInt(jspin.getValue().toString()));

						//Deal with functions
						wc.setNumberFuction(jcbfunc.getSelectedIndex());
						if( jcbfunc.getSelectedIndex() != 0 ) {
							JIPFunction function = funclist.getJIPFunction(jcbfunc.getSelectedItem().toString());
							JIPFunctionDialog funcDialog = new JIPFunctionDialog(mainGui, function, prop);
							funcDialog.setVisible(true);
							if (funcDialog.isConfirmed()) {
								if (funcDialog.isAssignedOK())
								{
									funcDialog.setVisible(false);
									repaint();
									wc.setFunction(function);
								}
							}
						}

						if( jcbsequences.isSelected() )
						{
							wc.setNumberSequences(Integer.parseInt(jspinsequence.getValue().toString()));
						}

						wc.initializeWebCam();
						//Starts to run
						th.start();
					}
					catch( Exception ex ) {
						logger.error(ex);
					}
				};
			} );

			//New button to stop the capture
			JButton jbstop = new JButton();
			jbstop.setBounds(new Rectangle(120,200,100,30));
			jbstop.setText(prop.getProperty("CapVideoButStop"));
			jbstop.addActionListener( new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					//Kills the thread
					jframe.setVisible(false);
					wc.setAlive(false);
					while( th.isAlive() );
					wc.finishWebCam();
				}
			} );

			//New button to cancel
			JButton jbcancel = new JButton();
			jbcancel.setBounds(new Rectangle(65,240,100,30));
			jbcancel.setText(prop.getProperty("Cancel"));
			jbcancel.addActionListener( new ActionListener ()
			{
				public void actionPerformed(ActionEvent e)
				{
					jframe.setVisible(false);
				}
			});

			//Group the radiobuttons
			ButtonGroup group = new ButtonGroup();
			group.add(jrb640);
			group.add(jrb320);
			group.add(jrb170);

			//Add to a panel
			jpanel.add(jlabradio,null);
			jpanel.add(jrb640,null);
			jpanel.add(jrb320,null);
			jpanel.add(jrb170,null);
			jpanel.add(jlabframes,null);
			jpanel.add(jspin,null);
			jpanel.add(jlabfunction,null);
			jpanel.add(jcbfunc,null);
			jpanel.add(jcbsequences,null);
			jpanel.add(jlabsequences,null);
			jpanel.add(jlabnumsequences,null);
			jpanel.add(jspinsequence,null);
			jpanel.add(jbcapture,null);
			jpanel.add(jbstop,null);
			jpanel.add(jbcancel,null);

			//Show the window
			jframe.setVisible(true);

		}

		/***********************************************************************
		 * * Stop Capture Video
		 **********************************************************************/
		if(e.getSource() == mainGui.getMenuBarGui2D().nocapvideo)
		{
			WebCam wc = new WebCam();
			wc.setAlive(false);
		}


		/***********************************************************************
		 * * Segments
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().dellsegment) {
			canvas.deleteLastSegment();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().dellpoint) {
			canvas.deleteLastPoint();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().delpoly) {
			canvas.deleteLastPolygon();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().delselec) {
			canvas.deleteSelection();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().addgeom) {
			canvas.saveGeometry();
			try {
				addUndo (canvas.getSequence());
			} catch (JIPException e1) {
				logger.error(e1);
			}
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().expascii) {
			canvas.exportAscii();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().importascii) {
			try {
				canvas.importAscii();
			} catch (JIPException e1) {
				logger.error(e1);
			}
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().pointframe) {
			try {
				canvas.addEmptyFrame(ImageType.POINT);
			} catch (JIPException e1) {
				logger.error(e1);
			}
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().segmentframe) {
			try {
				canvas.addEmptyFrame(ImageType.SEGMENT);
			} catch (JIPException e1) {
				logger.error(e1);
			}
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().polyframe) {
			try {
				canvas.addEmptyFrame(ImageType.POLY);
			} catch (JIPException e1) {
				logger.error(e1);
			}
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().emptyall) {
			canvas.emptyAll();
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().emptypoints) {
			try {
				canvas.emptyPoints();
			} catch (JIPException e1) {
				logger.error(e1);
			}
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().emptysegments) {
			try {
				canvas.emptySegments();
			} catch (JIPException e1) {
				logger.error(e1);
			}
			return;
		}
		if (e.getSource() == mainGui.getMenuBarGui2D().emptypoly) {
			try {
				canvas.emptyPolygons();
			} catch (JIPException e1) {
				logger.error(e1);
			}
			return;
		}

		/***********************************************************************
		 * * Undo
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().undo) {
			try {
				JIPSequence auxSeq = getLastSequence();
				if (auxSeq != null) {
					canvas.setSequence(auxSeq);
					canvas.setBackGround(null);
					canvas.outView();
					canvas.changeToFrame(canvas.getFrameNum());
					infoBottom.assocSequence(canvas.getSequence());
					repaint();
				} else
					new Dialog(this).information(prop.getProperty("MessageUndo"), prop.getProperty("Error"));
			}catch (JIPException ex) {logger.error(ex);}
			return;
		}

		/***********************************************************************
		 * * Redo
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().redo) {
			JIPSequence auxSeq = getNextSequence();
			if (auxSeq != null) {
				try {
					canvas.setSequence(auxSeq);
					canvas.setBackGround(null);
					canvas.outView();
					canvas.changeToFrame(canvas.getFrameNum());
					infoBottom.assocSequence(canvas.getSequence());
					repaint();
				}catch (JIPException ex) {logger.error(ex);}
			} else
				new Dialog(this).information(prop.getProperty("MessageRedo"), prop.getProperty("Error"));
			return;
		}

		/***********************************************************************
		 * * About and help
		 **********************************************************************/
		if (e.getSource() == mainGui.getMenuBarGui2D().about) {
			Commons.showAbout();
			return;
		}
		if (e.getSource()==mainGui.getMenuBarGui2D().help) {
			BareBonesBrowserLaunch.openURL("file:///"+System.getProperty("user.dir")+File.separator+
					"javavis"+File.separator+"help"+File.separator+"manual.html");
		}

		/***********************************************************************
		 * * Applying functions
		 **********************************************************************/
		if (e.getActionCommand().startsWith("F_")
				&& canvas.getSequence() != null) {
			JIPFunction f = funclist.getJIPFunction(e.getActionCommand().substring(2));
			acciones.functions(f);
			return;
		}
	}

	/**
	 *  This method ask for a file with specify types.
	 *
	 * @param cadena Directory where it starts to find the file.
	 * @return Devuelve A JFileChoser created object or null if we cancel it.
	 */
	public JFileChooser askForFileSave(String cadena, boolean jpggif) {
		JFileChooser eligeFich = new JFileChooser();
		eligeFich.setDialogType(JFileChooser.SAVE_DIALOG);
		if (cadena != null) {
			String path = cadena.substring(0, cadena.lastIndexOf("\\") + 1);
			String nfile = cadena.substring(cadena.lastIndexOf("\\") + 1,
					cadena.length());
			eligeFich.setCurrentDirectory(new File(path));
			eligeFich.setSelectedFile(new File(nfile));
		} else {
			eligeFich.setCurrentDirectory(new File("."));
			eligeFich.setSelectedFile(new File(prop.getProperty("NewFile")));
		}
		eligeFich.addChoosableFileFilter(new JIPImageFilter());
		if (jpggif)
			eligeFich.addChoosableFileFilter(new ALLImageFilter());
		if (eligeFich.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			return eligeFich;
		else
			return null;
	}

	/**
	 * Checks if the size and type of the images are of the same type.
	 */
	public boolean checkSize(JIPImage img1, JIPSequence sec) throws JIPException {
		JIPImage img2 = sec.getFrame(0);
		return (img1.getType() == img2.getType()
				&& img1.getHeight() == img2.getHeight()
				&& img1.getWidth() == img2.getWidth());
	}

	/**
	 * Method to add a sequence to the undo manager
	 * @param seq JIPSequence to add
	 */
	public void addUndo (JIPSequence seq) throws JIPException {
		if (undoSeq!=null) {
			int undoSize =undoSeq.size();
			if (undoSize > undoIndex) {
				for (int i=undoSize-1; i>undoIndex-1; i--)
					undoSeq.remove(i);
			}
			if (undoSeq.size() >= UNDO_LENGTH)
				undoSeq.remove(0);
			undoSeq.add(new JIPSequence(seq));
			undoIndex=undoSeq.size();
		}
	}

	/**
	 * Method to get the last sequence from the undo manager
	 * @return JIPSequence
	 */
	public JIPSequence getLastSequence () throws JIPException {
		if (undoSeq==null) return null;
		int aux = undoSeq.size();
		if (aux == 0 || undoIndex <= 1) return null;
		undoIndex--;
		return new JIPSequence(undoSeq.get(undoIndex-1));
	}

	/**
	 * Method to get the next sequence from the undo manager
	 * @return JIPSequence
	 */
	public JIPSequence getNextSequence () {
		int aux = undoSeq.size();
		if (aux == 0 || undoIndex == aux) return null;
		return undoSeq.get(undoIndex++);
	}

	/**
	 * Opens a jpg or gif image
	 * @param fich File to save
	 */
	public void openJPGImage(File fich) throws JIPException {
		fichAbierto = null;
		canvas.emptyAll();
		ultimoDirectorio = fich.getAbsolutePath();
		canvas.putBitmap(fich.getAbsolutePath());
		String nSeq = ultimoDirectorio.substring(ultimoDirectorio.lastIndexOf("\\") + 1, ultimoDirectorio
				.lastIndexOf("."));
		canvas.setSequenceName(nSeq);
		canvas.setFrameName(nSeq);
		fichAbierto = FileType.JPG;
		addUndo(canvas.getSequence());
		mainGui.setTitle("JavaVis - "+prop.getProperty("Sequence")+
			": [" + canvas.getSequenceName()
			+ "] "+prop.getProperty("File")+": [" + ultimoDirectorio + "]");
	}

	/**
	 * Opens a jip image
	 * @param fich File to save
	 */
	public void openJIPImage(File fich) throws JIPException {
		fichAbierto = null;
		canvas.emptyAll();
		ultimoDirectorio = fich.getAbsolutePath();
		canvas.loadSequence(fich.getAbsolutePath());
		fichAbierto = FileType.JIP;
		addUndo(canvas.getSequence());
		mainGui.setTitle("JavaVis - "+prop.getProperty("Sequence")+
			": [" + canvas.getSequenceName()
			+ "] "+prop.getProperty("File")+": [" + ultimoDirectorio + "]");
	}

	/**
	 * Adds a JIPImage to the sequence of the canvas.
	 * @img JIPImage to be added
	 * @throws JIPException 
	 */
	public void addJIPImageToSequence(JIPImage img) throws JIPException{
		canvas.addFrame(img);
		canvas.changeToFrame(canvas.getSequence().getNumFrames()-1);
		addUndo(canvas.getSequence());
		setSaved(false);
	}


	/**
	 * @return Returns the mainGui.
	 */
	public Gui getMainGui() {
		return mainGui;
	}

	/**
	 * @param mainGui The mainGui to set.
	 */
	public void setMainGui(Gui frame) {
		this.mainGui = frame;
	}


	/**
	 * @return Returns the isSaved.
	 */
	public boolean isSaved() {
		return isSaved;
	}


	/**
	 * @param isSaved The isSaved to set.
	 */
	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	/**
	 * This class is created to support actions (menu and toolbar actions)
	 * @author Miguel
	 *
	 */
    public class DoAction implements Runnable {
		Gui2D prog;
		/**
		 * Objects used in thread task
		 */
		private JIPFunction funcThread=null;
		private boolean isProcSeq=false;


		public DoAction(Gui2D g) {
			prog = g;
		}

		public void edgeColor() {
			canvas.edgeColor(JColorChooser.showDialog(prog, prop.getProperty("SelectColor"),
					new Color(255, 255, 255)));
			canvasScr.repaint();
		}

		public void changeRightView() {
			if(prog.infoGeom.isVisible()){
				prog.infoGeom.setVisible(false);
                spane.setRightComponent(prog.panelFuncList);
				prog.panelFuncList.setVisible(true);
                changeViewButton.setText(prop.getProperty("Geometry"));
			}
			else{
				prog.panelFuncList.setVisible(false);
                spane.setRightComponent(prog.infoGeom);
				prog.infoGeom.setVisible(true);
                changeViewButton.setText(prop.getProperty("Functions"));
			}
		}

		public void polygonColor() {
			canvas.polygonColor(JColorChooser.showDialog(prog, prop.getProperty("SelectColor"),
					new Color(255, 255, 255)));
			canvasScr.repaint();
		}

		public void pointColor() {
			canvas.pointColor(JColorChooser.showDialog(prog, prop.getProperty("SelectColor"),
					new Color(255, 255, 255)));
			canvasScr.repaint();
		}

		public void segmentColor() {
			canvas.lineColor(JColorChooser.showDialog(prog, prop.getProperty("SelectColor"),
					new Color(255, 255, 255)));
			canvasScr.repaint();
		}

		public void functions(JIPFunction f) {
			if (canvas.getSequence() != null) {
				JIPFunction funcAux;
				// Now, we check if the function to apply is the last one.
				// If so, we use the lastFuncApplied, where the last parameters values
				// were stored
				if (lastFuncApplied!= null && f!=null &&
						lastFuncApplied.getName().equals(f.getName())) {
					funcAux=lastFuncApplied;
				}
				else funcAux=f;
				JIPFunctionDialog fdialog = new JIPFunctionDialog(mainGui, funcAux, prop);
				fdialog.setVisible(true);
				if (fdialog.isConfirmed()) {
					if (fdialog.isAssignedOK()) {
						fdialog.setVisible(false);
						repaint();
						isProcSeq=fdialog.applyToSeq();
						if (isProcSeq) {
							mainGui.setTitle("JavaVis - "+prop.getProperty("Applying")
									+ " " + funcAux.getName()
									+ " "+prop.getProperty("to")+" " + canvas.getSequenceName());
						} else {
							mainGui.setTitle("JavaVis - "+prop.getProperty("Applying")+ " "
									+ funcAux.getName() + " "+prop.getProperty("to")+
									" numFrame " + canvas.getFrameNum()
									+ " "+prop.getProperty("of")+
									" " + canvas.getSequenceName());
						}
						infoBottom.setBar(0);
						Thread th = new Thread(this);
						funcThread=funcAux;
						MyProgressBar mpb = new MyProgressBar(funcThread, infoBottom, th);
						Thread th2 = new Thread(mpb);
						th2.start();
						th.start();
					} else {
						new Dialog(prog).information(prop.getProperty("ParamsReq"), prop.getProperty("Error"));
						return;
					}
					lastFuncApplied=funcAux;
				}
			}
		}

		public void run () {
			JIPImage auxImg=null;
			JIPSequence auxSeq=null;
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			infoBottom.setBar(0);

			try {
				long t1=System.currentTimeMillis();
				if (isProcSeq)
					auxSeq = funcThread.processSeq(canvas.getSequence());
				else
					auxImg = funcThread.processImg(canvas.getSequence().getFrame(canvas.getFrameNum()));
				long t2=System.currentTimeMillis();
				infoBottom.setBar(100);
				if (isProcSeq)
					canvas.setSequence(auxSeq);
				else {
					auxImg.setName(canvas.getSequence().getFrame(canvas.getFrameNum()).getName());
					canvas.getSequence().setFrame(auxImg, canvas.getFrameNum());
					canvas.reassignedSeq();
				}
				if (funcThread.isInfo())
					new Dialog(prog).information(funcThread.getInfo(),
							prop.getProperty("Information"));
				canvas.outView();
				canvas.setBackGround(null);
				infoBottom.assocSequence(canvas.getSequence());
				canvas.changeToFrame(canvas.getFrameNum());
				infoBottom.setFrame(canvas.getFrameNum());
				addUndo(canvas.getSequence());
				isSaved=false;
				mainGui.setTitle("JavaVis - " + canvas.getSequenceName()+"; "+funcThread.getName()+" applied in "+(t2-t1)+" milliseconds");
			}
			catch (JIPException e) {
				new Dialog(prog).information(e.getMessage(), prop.getProperty("Error"));
				logger.error(e);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			repaint();
		}

		public void panelColor() {
			canvas.backgroundColorScroll(JColorChooser.showDialog(prog,
					prop.getProperty("SelectColor"), new Color(255, 255, 255)));
			canvasScr.repaint();
		}

		public void backgroundColor() {
			canvas.backgroundColor(JColorChooser.showDialog(prog, prop.getProperty("SelectColor"),
					new Color(255, 255, 255)));
			canvasScr.repaint();
		}

		public void save_as_jpg() {
			if (canvas.getSequence() == null) {
				new Dialog(prog).information(prop.getProperty("MessageSave"),
						prop.getProperty("Attention"));
				return;
			}
			String tituloAnt = mainGui.getTitle();
			mainGui.setTitle("JavaVis - [" + ultimoDirectorio + "] "+
					prop.getProperty("saving")+"...");
			JFileChooser eligeFich = new JFileChooser();
			eligeFich.setDialogType(JFileChooser.SAVE_DIALOG);
			if (ultimoDirectorio != null) {
				String path = ultimoDirectorio.substring(0, ultimoDirectorio
						.lastIndexOf("\\") + 1);
				String nfile = ultimoDirectorio.substring(ultimoDirectorio
						.lastIndexOf("\\") + 1, ultimoDirectorio.length());
				eligeFich.setCurrentDirectory(new File(path));
				eligeFich.setSelectedFile(new File(nfile));
			} else {
				eligeFich.setCurrentDirectory(new File("."));
				eligeFich.setSelectedFile(new File(prop.getProperty("New")));
			}
			eligeFich.addChoosableFileFilter(new ALLImageFilter());
			if (eligeFich.showSaveDialog(prog) == JFileChooser.APPROVE_OPTION) {
				File fich = eligeFich.getSelectedFile();
				ultimoDirectorio = fich.getAbsolutePath();
				String path = ultimoDirectorio.substring(0, ultimoDirectorio
						.lastIndexOf("\\") + 1);
				int aux = ultimoDirectorio.lastIndexOf(".");
				if (aux < 0)
					aux = ultimoDirectorio.length();
				String nfile = ultimoDirectorio.substring(ultimoDirectorio
						.lastIndexOf("\\") + 1, aux);
				if (!nfile.toLowerCase().endsWith(".jpg"))
					nfile += ".jpg";
				JIPToolkit.saveImgIntoFileJpg(canvas.getSequence(), canvas
						.getFrameNum(), path, nfile);
				mainGui.setTitle("JavaVis - "+prop.getProperty("Sequence")+
						": [" + canvas.getSequenceName()
						+ "] File: [" + ultimoDirectorio + "]");
				new Dialog(prog).information(prop.getProperty("JPGAtten"),
						prop.getProperty("Attention"));
			} else {
				mainGui.setTitle(tituloAnt);
				return;
			}

		}

		// TODO: cambiar esto, parece que no hace las cosas bien
		public void save_as() {
			FileType tipo = null;
			if (canvas.getSequence() == null) {
				new Dialog(prog).information(prop.getProperty("noseq"),
						prop.getProperty("Attention"));
				return;
			}
			String tituloAnt = mainGui.getTitle();
			mainGui.setTitle("JavaVis - [" + ultimoDirectorio + "] "+
					prop.getProperty("saving")+"...");
			JFileChooser abreFich = askForFileSave(ultimoDirectorio, false);
			if (abreFich == null) {
				mainGui.setTitle(tituloAnt);
				return;
			} else {
				File fich = abreFich.getSelectedFile();
				ultimoDirectorio = fich.getAbsolutePath();
				if (abreFich.getFileFilter() instanceof JIPImageFilter) {
					if (!ultimoDirectorio.toLowerCase().endsWith(".jip"))
						ultimoDirectorio += ".jip";
					tipo = FileType.JIP;
				} else
					tipo = null;
			}
			String path = ultimoDirectorio.substring(0, ultimoDirectorio
					.lastIndexOf("\\") + 1);
			String nfile = ultimoDirectorio.substring(ultimoDirectorio
					.lastIndexOf("\\") + 1, ultimoDirectorio.length());
			fichAbierto = null;
			String ext = "";
			if (tipo == FileType.JIP || tipo == null) {
				JIPToolkit.saveSeqIntoFile(canvas.getSequence(), path, nfile);
				fichAbierto = FileType.JIP;
				ext = "JIP";
			} else
				return;
			mainGui.setTitle("JavaVis - "+prop.getProperty("Sequence")+": [" +
					canvas.getSequenceName()
					+ "] "+prop.getProperty("File")+": [" + ultimoDirectorio + "]");
			new Dialog(prog).information(prop.getProperty("filesaved") +
					prop.getProperty("format")+" "+ext,
					prop.getProperty("Attention"));
			isSaved=true;
		}

		public void save() {
			FileType tipo = null;
			if (canvas.getSequence() == null) {
				new Dialog(prog).information(prop.getProperty("noseq"),
						prop.getProperty("Attention"));
				return;
			}
			String tituloAnt = mainGui.getTitle();
			mainGui.setTitle("JavaVis - [" + ultimoDirectorio + "] "+
					prop.getProperty("saving")+"...");
			if (fichAbierto == null || fichAbierto == FileType.JPG) {
				JFileChooser abreFich = askForFileSave(ultimoDirectorio, false);
				if (abreFich == null) {
					mainGui.setTitle(tituloAnt);
					return;
				} else {
					File fich = abreFich.getSelectedFile();
					ultimoDirectorio = fich.getAbsolutePath();
					if (abreFich.getFileFilter() instanceof JIPImageFilter) {
						tipo = FileType.JIP;
						if (!ultimoDirectorio.toLowerCase().endsWith(".jip"))
							ultimoDirectorio += ".jip";
					} else
						tipo = null;
				}
			}
			// Si ya lo teniamos abierto nos quedamos con el tipo.
			else
				tipo = fichAbierto;
			String path = ultimoDirectorio.substring(0, ultimoDirectorio
					.lastIndexOf("\\") + 1);
			String nfile = ultimoDirectorio.substring(ultimoDirectorio
					.lastIndexOf("\\") + 1, ultimoDirectorio.length());
			fichAbierto = null;
			String ext = "";
			if (tipo == FileType.JIP || tipo == null) {
				JIPToolkit.saveSeqIntoFile(canvas.getSequence(), path, nfile);
				fichAbierto = FileType.JIP;
				ext = "JIP";
			} else
				return;
			mainGui.setTitle("JavaVis - "+prop.getProperty("Sequence")+": [" +
					canvas.getSequenceName()
					+ "] "+prop.getProperty("File")+": [" + ultimoDirectorio + "]");
			new Dialog(prog).information(prop.getProperty("filesaved") +
					prop.getProperty("format")+" "+ext,
					prop.getProperty("Attention"));
			isSaved=true;
		}

		public void nnew() {
			String tamX, tamY;
			int w = -1, h = -1;
			tamX = JOptionPane.showInputDialog(prog, prop.getProperty("introwidth"));
			tamY = JOptionPane.showInputDialog(prog, prop.getProperty("introheight"));
			try {
				if (tamX.length() > 0)
					w = Integer.valueOf(tamX);
				if (tamY.length() > 0)
					h = Integer.valueOf(tamY);
			} catch (Exception err) {logger.error(err);}
			if (w > 0 && h > 0) {
				canvas.emptyAll();
				try {
					canvas.setSequence(null);
				} catch (JIPException e) {logger.error(e);}
				if (w > 0 && h > 0)
					canvas.newBitmap(w, h, null);
				undoSeq = null;
				mainGui.setTitle("JavaVis - "+prop.getProperty("nocurrentseq"));
				ultimoDirectorio = null;
				fichAbierto = null;
				isSaved=true;
			}
		}
	}


    /**
     * Class to manage a thread to control the progress bar
     * @author Miguel
     */
    class MyProgressBar implements Runnable {
		private static final long serialVersionUID = 2265668831857808113L;
		private JIPFunction func;
		private InfoPanelBottom ipb;
		private Thread th;

    	public MyProgressBar (JIPFunction f, InfoPanelBottom ipbAux,
    			Thread thAux) {
    		ipb = ipbAux;
    		func = f;
    		th = thAux;
    	}

    	public void run () {
    		try {
    			while (th.isAlive()) {
    				ipb.setBar(func.getProgress());
    				Thread.sleep(1000);
    			}
    		} catch (InterruptedException e) {logger.error(e);}
    	}
    }
}