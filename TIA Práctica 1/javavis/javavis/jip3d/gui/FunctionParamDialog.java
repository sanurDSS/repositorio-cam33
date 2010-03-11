package javavis.jip3d.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javavis.base.ParamType;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;


public class FunctionParamDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -1181844930391165575L;

	private int num_botones;

	public boolean confirmed;

	ArrayList<JComponent> componentes;

	ArrayList<FunctionParam>local_param_list;

	MyDialog diag;

	ArrayList<ScreenData> scr_list;

	public FunctionParamDialog(MyDialog d, ArrayList<FunctionParam> param_list, ArrayList<ScreenData> data_list)
	{
		super(d.owner, "Change Function Parameters");
		componentes = new ArrayList<JComponent>(); //En este array vamos almacenando los componentes que creamos
		num_botones = 0;
		diag = d;
		local_param_list = param_list;
		JPanel pmain;
		JPanel pcomp;
		JPanel paux = null;
		JLabel label;
		FunctionParam param;
		int cont, tam;
		tam = param_list.size();
		JComponent comp = null;
		ParamType type;
		scr_list = data_list;

		pmain = new JPanel(new GridLayout(tam, 0));

		for(cont=0;cont<tam;cont++)
		{
			param = param_list.get(cont);
			type = param.type;

			if(type == ParamType.INT)
			{
				comp = createNumberComponent(param.getValueInt(), param.iminvalue, param.imaxvalue);
				paux = new JPanel();
				paux.add(comp);
			}
			else if(type==ParamType.FLOAT)
			{
				comp = createNumberComponent(param.getValueReal(), param.dminvalue, param.dmaxvalue, param.dstep);
				paux = new JPanel();
				paux.add(comp);
			}
			else if(type == ParamType.BOOL)
			{
				comp = createBooleanComponent(param.getValueBool());
				paux = new JPanel();
				paux.add(comp);
			}
			else if(type == ParamType.STRING)
			{
				comp = createStringComponent(param.getValueString());
				paux = new JPanel();
				paux.add(comp);
			}
			else if(type == ParamType.FILE)
			{
				/*
				 * Esta opcion ha de crear un boton que cuando se clickee abra un cuadro de dialogo para seleccionar un
				 * fichero. Al boton se le asignara un nombre de accion consecutivamente: boton0, boton1,... Despu'es, si es
				 * pulsado se asignara su valor al i-esimo parametro de tipo tPATH

				 */
				comp = createPathComponent(param.getValueString());
				JButton but = new JButton("...");
				but.setActionCommand("botonf"+num_botones);
				but.addActionListener(this);
				paux = new JPanel();
				paux.add("West", comp);
				paux.add("East", but);
			}
			else if(type == ParamType.DIR)
			{
				/*
				 * Esta opcion ha de crear un boton que cuando se clickee abra un cuadro de dialogo para seleccionar un
				 * fichero. Al boton se le asignara un nombre de accion consecutivamente: boton0, boton1,... Despu'es, si es
				 * pulsado se asignara su valor al i-esimo parametro de tipo tPATH

				 */
				comp = createPathComponent(param.getValueString());
				JButton but = new JButton("...");
				but.setActionCommand("botond"+num_botones);
				but.addActionListener(this);
				paux = new JPanel();
				paux.add("West", comp);
				paux.add("East", but);
			}
			else if(type == ParamType.SCRDATA)
			{
				comp = createScrDataComponent(param.getValueScrData());
				paux = new JPanel();
				paux.add(comp);
			}

			num_botones++;
			componentes.add(comp);
			label = new JLabel(param.name+":");
			pcomp = new JPanel();
			pcomp.add("West", label);
			pcomp.add("East", paux);
			pmain.add(pcomp);

		}

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
		getContentPane().add("Center", pmain);
		getContentPane().add("South", pbot);
		getContentPane().setBackground(new Color(232, 232, 232));

		pack();
		center(d.owner);
	}

	private JSpinner createNumberComponent(int value, int min, int max)
	{
		JSpinner ret;
		SpinnerNumberModel snmR = new SpinnerNumberModel(value, min, max, 1);
		ret = new JSpinner(snmR);
		ret.setPreferredSize(new Dimension(100,20));
		return ret;
	}

	private JSpinner createNumberComponent(double value, double min, double max, double step)
	{
		JSpinner ret;
		SpinnerNumberModel snmR = new SpinnerNumberModel(value, min, max, step);
		ret = new JSpinner(snmR);
		ret.setPreferredSize(new Dimension(100,20));
		return ret;
	}

	private JCheckBox createBooleanComponent(boolean value)
	{
		JCheckBox ret = new JCheckBox();
		ret.setSelected(value);
		return ret;
	}

	private JTextField createStringComponent(String value)
	{
		JTextField ret = new JTextField(20);
		if (value!=null) ret.setText(value);
		return ret;
	}

	private JComboBox createScrDataComponent(ScreenData value)
	{
		JComboBox list = new JComboBox();
		int index = 0;
		String name;
		if(value!=null) name = value.name;
		else name = "";
		for(ScreenData dat: scr_list)
		{
			list.addItem(dat.name);
			if(name.equals(dat.name)) index = list.getItemCount();
		}
		list.setSelectedIndex(index);
		return list;
	}

	/**
	 * @param value
	 * @return
	 */
	private JTextField createPathComponent(String value)
	{
		JTextField ret = new JTextField(20);
		if (value!=null) ret.setText(value);
		return ret;
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
		int cont, tam;
		tam = componentes.size();
		JComponent comp;
		FunctionParam param;
		String name;
		ScreenData scr;
		for(cont=0;cont<tam;cont++)
		{
			comp = componentes.get(cont);
			param = local_param_list.get(cont);

			if(param.type == ParamType.INT)
			{
				param.setValue((Integer)((JSpinner)comp).getValue());
			}
			else if(param.type == ParamType.FLOAT)
				param.setValue((Double)((JSpinner)comp).getValue());
			else if(param.type == ParamType.BOOL)
			{
				param.setValue(((JCheckBox)comp).isSelected());
			}
			else if(param.type == ParamType.STRING || param.type == ParamType.FILE || param.type == ParamType.DIR)
				param.setValue(((JTextField)comp).getText());
			else if(param.type == ParamType.SCRDATA)
			{
				name = (String)((JComboBox)comp).getSelectedItem();
				scr = null;
				for(ScreenData dat: scr_list)
				{
					if(dat.name.equals(name))
						scr = dat;
				}
				param.setValue(scr);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		String act = e.getActionCommand();
		String sub;
		char t;
		File data;

		if (act.equals("OK")) {
			confirmed = true;
			assignValues();
			setVisible(false);
			return;
		}
		if (act.equals("Cancel")) {
			confirmed = false;
			setVisible(false);
			return;
		}

		sub = act.substring(6);
		t = act.charAt(5);

		int pos = new Integer(sub).intValue();

		if(t=='f')
			data = diag.fileChooser(null, null, null, false);
		else
			data = diag.fileChooser(null, null, null, true, JFileChooser.DIRECTORIES_ONLY);

		String path;
		if(data!=null)
		{
			path = data.getAbsolutePath();
			((JTextField)componentes.get(pos)).setText(path);
		}

	}
}
