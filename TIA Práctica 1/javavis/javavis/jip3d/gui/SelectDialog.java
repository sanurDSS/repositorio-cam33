package javavis.jip3d.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.vecmath.Color3f;


// TODO En el futuro esta clase debe crear el panel izquierdo de seleccion de propiedades de ScreenData
// Ahora mismo esta clase no se usa

public class SelectDialog extends JDialog implements ActionListener, ItemListener {

	private static final long serialVersionUID = 7600039601501224685L;

	ScreenOptions scr_opt;

	boolean confirmed;
	boolean delete;
	boolean ret_delete;

	JSpinner spinner = null;
	JSpinner jsp_alpha;
	JSpinner jsp_length;

	JCheckBox jcb_delete;
	JCheckBox jcb_visible;
	JCheckBox jcb_color;	//for PointSet3D
	JCheckBox jcb_improve;	//for PointSet3D

	JButton jbt_color;

	public double maxSpinner = 10;
	public double minSpinner = 0.1;

	public SelectDialog(JFrame frame, ScreenOptions opt)
	{
		super(frame);
		scr_opt = opt;
		JPanel pcontent = new JPanel(new BorderLayout());
		JPanel pcheckboxes = new JPanel(new GridLayout(4,0));
		JPanel pcolor = new JPanel();
		JPanel palpha = new JPanel();
		JPanel pspinner = new JPanel();
		SpinnerNumberModel snmR;

		jcb_delete = new JCheckBox("Delete");
		jcb_delete.setSelected(false);

		jcb_visible = new JCheckBox("Visible");
		jcb_visible.setSelected(opt.is_visible);

		jcb_delete.addItemListener(this);
		jcb_visible.addItemListener(this);

		pcheckboxes.add(jcb_delete);
		pcheckboxes.add(jcb_visible);

		if(opt.type == ScreenOptions.tPOINTSET3D)
		{
			jcb_color = new JCheckBox("Global color");
			jcb_color.setSelected(opt.global_color);
			jcb_color.addItemListener(this);
			pcheckboxes.add(jcb_color);

			jcb_improve = new JCheckBox("Improved Representation");
			jcb_improve.setSelected(opt.improved);
			jcb_improve.addItemListener(this);
			pcheckboxes.add(jcb_improve);
		}
		else if(opt.type == ScreenOptions.tTRAJ2D || opt.type == ScreenOptions.tTRAJ3D)
		{
			snmR = new SpinnerNumberModel(opt.alpha, 0.0, 1.0, 0.1);
			jsp_alpha = new JSpinner(snmR);
			jsp_alpha.setPreferredSize(new Dimension(50,20));
			//jsp_alpha.setSize(150,10);
			palpha.add("West", new JLabel("Transparency: "));
			palpha.add("East", jsp_alpha);
			pcheckboxes.add(palpha);
		}
		else if(opt.type == ScreenOptions.tNORMALSET3D)
		{
			snmR = new SpinnerNumberModel(opt.length, 0.0, 1.0, 0.1);
			jsp_length = new JSpinner(snmR);
			jsp_length.setPreferredSize(new Dimension(50, 20));
			palpha.add("West", new JLabel("Vector length (%): "));
			palpha.add("East", jsp_length);
			pcheckboxes.add(palpha);
		}
		else
		{
			jcb_color = null;
			jcb_improve = null;
			pcheckboxes.add(new JLabel(" "));
		}

		snmR = new SpinnerNumberModel(opt.width, minSpinner, maxSpinner, 1);
		spinner = new JSpinner(snmR);
		spinner.setPreferredSize(new Dimension(50,20));
		pspinner.add("West", new JLabel("Width: "));
		pspinner.add("East", spinner);

		pcolor.add("West", new JLabel("Global Color: "));
		jbt_color = new JButton("");
		jbt_color.setActionCommand("Color");
		jbt_color.addActionListener(this);
		jbt_color.setBackground(scr_opt.color.get());
		jbt_color.setPreferredSize(new Dimension(50,20));
		pcolor.add("East", jbt_color);
		if(opt.type == ScreenOptions.tPOINTSET3D && !jcb_color.isSelected())
			jbt_color.setEnabled(false);

		pcontent.add("North", pcheckboxes);
		pcontent.add("Center", pspinner);
		pcontent.add("South", pcolor);
		// Panel de los botones
		JButton bt0 = new JButton("Ok");
		bt0.setActionCommand("OK");
		bt0.addActionListener(this);
		JButton bt1 = new JButton("Cancel");
		bt1.setActionCommand("Cancel");
		bt1.addActionListener(this);
		JPanel pbot = new JPanel();
		pbot.add("West", bt0);
		pbot.add("East", bt1);


		getContentPane().add("North", new JLabel("Change Screen Object Parameters"));
		getContentPane().add("Center", pcontent);
		getContentPane().add("South", pbot);
		getContentPane().setBackground(new Color(232, 232, 232));
		pack();
		center(frame);

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

	void assignValues()
	{
		ret_delete = delete;
		double num;
		if(!delete)
		{
			scr_opt.is_visible = jcb_visible.isSelected();
			if(scr_opt.type == ScreenOptions.tPOINTSET3D)
			{
				scr_opt.global_color = jcb_color.isSelected();
				scr_opt.improved = jcb_improve.isSelected();
			}
			else if(scr_opt.type == ScreenOptions.tTRAJ2D)
			{
				num = (Double)jsp_alpha.getValue();
				scr_opt.alpha = (float)num;
			}
			scr_opt.width = (Double)spinner.getValue();//(Double)spinner.floatValue();
			scr_opt.color = new Color3f(jbt_color.getBackground());

		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			confirmed = true;
			assignValues();
			setVisible(false);
			return;
		}
		if (e.getActionCommand().equals("Cancel")) {
			confirmed = false;
			setVisible(false);
			return;
		}
		if (e.getActionCommand().equals("Color")) {
			Color newColor = JColorChooser.showDialog(
                    this,
                    "Choose Object Color",
                    jbt_color.getBackground());
			if(newColor != null)
				jbt_color.setBackground(newColor);
		}

	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

		if(source == jcb_delete)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				delete = true;
				jcb_visible.setEnabled(false);
				if(jcb_color != null) jcb_color.setEnabled(false);
				spinner.setEnabled(false);
				jbt_color.setEnabled(false);
			}
			else
			{
				delete = false;
				jcb_visible.setEnabled(true);
				if(jcb_color != null) jcb_color.setEnabled(jcb_color.isSelected());
				spinner.setEnabled(true);
				jbt_color.setEnabled(true);
			}
		}
		if(source == jcb_color)
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
				jbt_color.setEnabled(true);
			else
				jbt_color.setEnabled(false);

		}
	}

}
