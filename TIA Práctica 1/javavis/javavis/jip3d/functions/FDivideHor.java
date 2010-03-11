package javavis.jip3d.functions;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javavis.base.ParamType;
import javavis.base.JIPException;
import javavis.jip3d.geom.Point3D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.PointSet3D;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class FDivideHor extends Function3D {

	double height;

	public FDivideHor()
	{
		super();
		this.allowed_input = ScreenOptions.tPOINTSET3D;

		FunctionParam p1 = new FunctionParam("Manual", ParamType.BOOL);
		p1.setValue(true);
		FunctionParam p2 = new FunctionParam("Height", ParamType.FLOAT);
		p2.setValue(0.0);

		this.addParam(p1);
		this.addParam(p2);


	}

	@Override
	public void proccessData(ScreenData scr_data)  throws JIPException{
		result_list = new ArrayList<ScreenData>();
		ScreenData scr_new;
		ArrayList<Object []> sets;
		Point3D point;
		int cont;
		Object []elements;
		DivideDialog div_diag = null;

		boolean manual = paramValueBool("Manual");
		height = paramValueReal("Height");

		if(manual)
		{
			height = scr_data.getMinRango()[1];
			div_diag = new DivideDialog(scr_data);
			div_diag.setModal(true);
			div_diag.setVisible(true);
		}
		if(!manual || div_diag.isConfirmed())
		{
			sets = divideTree(scr_data);

			scr_new = new PointSet3D(new ScreenOptions());
			scr_new.name = "up_" + scr_data.name;
			elements = sets.get(1);
			for(cont=0;cont<elements.length;cont++)
			{
				point = (Point3D)elements[cont];
				scr_new.insert(point);
			}
			result_list.add(scr_new);

			scr_new = new PointSet3D(new ScreenOptions());
			scr_new.name = "down_" + scr_data.name;
			elements = sets.get(0);
			for(cont=0;cont<elements.length;cont++)
			{
				point = (Point3D)elements[cont];
				scr_new.insert(point);
			}
			result_list.add(scr_new);

		}
		else
			result_list = null;

		return;
	}

	private ArrayList<Object []> divideTree(ScreenData scr_data)
	{
		ArrayList<Object []>ret = new ArrayList<Object[]>(2);
		double []center = new double [3];

		try
		{
			center[0] = scr_data.getMaxRango()[0];
			center[1] = height;
			center[2] = scr_data.getMaxRango()[2];
			ret.add(scr_data.range(scr_data.getMinRango(), center));
			center[0] = scr_data.getMinRango()[0];
			center[2] = scr_data.getMinRango()[2];
			ret.add(scr_data.range(center, scr_data.getMaxRango()));

		} catch(Exception e)
		{
			ret = null;
		}

		return ret;
	}

	class DivideDialog extends JDialog implements ActionListener, ChangeListener
	{
		private static final long serialVersionUID = -8084428193661519578L;

		JLabel utag;
		JLabel btag;
		JLabel thres;

		JSlider slider;

		boolean confirmed;

		ScreenData myscrdata;

		public DivideDialog(ScreenData scr_data)
		{
			super(dialog.owner);
			myscrdata = scr_data;
			JPanel pmain = new JPanel(new GridLayout(4,0));
			JPanel paux;

			ArrayList<Object []> sets = divideTree(scr_data);

			int s1 = sets.get(0).length;
			int s2 = sets.get(1).length;

			paux = new JPanel();
			thres = new JLabel(" "+height);
			paux.add("West", new JLabel("Threshold: "));
			paux.add("East", thres);
			pmain.add(paux);

			slider = new JSlider(JSlider.HORIZONTAL, (int)(scr_data.getMinRango()[1]*100), (int)(scr_data.getMaxRango()[1]*100), (int)(height*100));
			slider.addChangeListener(this);
			pmain.add(slider);

			paux = new JPanel();
			utag = new JLabel(s2 + " points  ");
			paux.add("West", new JLabel("  Upper set contains: "));
			paux.add("East", utag);
			pmain.add(paux);

			paux = new JPanel();
			btag = new JLabel(s1 + " points");
			paux.add("West", new JLabel("Bottom set contains: "));
			paux.add("East", btag);
			pmain.add(paux);


			// Panel de los botones
			JButton bt0 = new JButton("Accept");
			bt0.setActionCommand("Accept");
			bt0.addActionListener(this);
			JButton bt1 = new JButton("Decline");
			bt1.setActionCommand("Decline");
			bt1.addActionListener(this);
			JPanel pbot = new JPanel();
			pbot.add("West", bt0);
			pbot.add("East", bt1);


			getContentPane().add("North", new JLabel("Original Set contains: "+scr_data.scr_opt.num_points+" points"));
			getContentPane().add("Center", pmain);
			getContentPane().add("South", pbot);
			getContentPane().setBackground(new Color(232, 232, 232));
			pack();
			center(dialog.owner);

		}

		/**
		 * <P><FONT COLOR="RED">
		 * <B>Description:</B><BR>
		 * <FONT COLOR="BLUE">
		 * Shows if OK button has been pressed
		 * @return TRUE if the button has been pressed. FALSE if the button has not been pressed
		 *
		 * @uml.property name="confirmed"
		 */
		public boolean isConfirmed() {
			return confirmed;
		}

		/**<P><FONT COLOR="RED">
		*<B>Description:</B><BR>
		*<FONT COLOR="BLUE">
		* Indica si se ha pulsado el boton Cancel
		* Shows if Cancel button has been pressed
		* @return TRUE if the button has been pressed. FALSE if the button has not been pressed
		*/
		public boolean isCancelled() {
			return !confirmed;
		}


		/**<P><FONT COLOR="RED">
		*<B>Description:</B><BR>
		*<FONT COLOR="BLUE">
		* Centra la ventana a la mitad de la ventana desde la que se llamo
		* It centres the window in the midle of window which make the call
		* @param numFrame JFrame that we want to center.
		*/
		void center(JFrame frame) {
			Point p = frame.getLocationOnScreen();
			Dimension dframe = frame.getSize();
			Dimension dthis = getSize();
			int x = (dframe.width - dthis.width) / 2;
			int y = (dframe.height - dthis.height) / 2;
			if (x < 0) x = 0;
			if (y < 0) y = 0;
			setLocation(p.x + x, p.y + y);
		}


		public void actionPerformed(ActionEvent evt) {
			String act = evt.getActionCommand();
			if (act.equals("Accept")) {
				confirmed = true;
//				assignValues();
				setVisible(false);
				return;
			}
			if (act.equals("Decline")) {
				confirmed = false;
				setVisible(false);
				return;
			}

		}

		public void stateChanged(ChangeEvent arg0) {
			int val = slider.getValue();
			height = (double)val/100.0;
			ArrayList<Object []> sets = divideTree(myscrdata);

			int s1 = sets.get(0).length;
			int s2 = sets.get(1).length;

			utag.setText(s2 + " points");
			btag.setText(s1 + " points");
			thres.setText(" "+height);
		}
	}
}
