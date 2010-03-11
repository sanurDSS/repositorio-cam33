package javavis.jip3d.gui;

import java.util.ArrayList;

import javavis.base.Function3DGroup;
import javavis.base.JIPException;
import javavis.base.ParamType;

/**
 * Clase Abstracta Function3D que hereda de Thread. Extiende el metodo Thread.run dentro del cual hace
 * una llamada ala metodo abstracto processData que tendra que ser implementado por cada instancia de
 * Function3D. Ademas, el metodo run se encarga de almacenar en la variable del clase elapsed_time el
 * tiempo en ms. que tarda en ejecutarse la funcion.
 * @author dviejo
 *
 */
public abstract class Function3D implements Runnable {

	protected volatile Thread blinker;

	private ScreenData scr_data;

	public MyDialog dialog;

	public ArrayList<ScreenData> result_list;
	public int allowed_input;

	public ArrayList<FunctionParam> param_list;
	public ArrayList<String> param_names;

	public boolean data_loaded;

	long elapsed_time;

	/// progress is the percentage of function already performed
	public double progress;

	public String name;

	public boolean cancelled;

	public Function3DGroup group;

	private MyCanvas3D canvas;

	/**
	 * Abstract Constructor.
	 * @param d Dialog object for showing information about warnings, errors, etc.
	 */
	public Function3D()
	{
		name = "";
		param_list = new ArrayList<FunctionParam>();
		param_names = new ArrayList<String>();
		data_loaded = false;
		allowed_input = ScreenOptions.tALLTYPES;
		progress = 0;
		group = Function3DGroup.Others;
		canvas = null;
	}


    public void start() {
    	cancelled = false;
        blinker = new Thread(this, name);
        blinker.start();
    }

	public void stop() {
		cancelled = true;
        Thread tmpBlinker = blinker;
        blinker = null;
        if (tmpBlinker != null) {
           tmpBlinker.interrupt();
        }
    }

	public Thread getThread()
	{
		return blinker;
	}

	/**
	 * loadData must be called before start this thread.
	 * @param sd Input data for executing this function
	 */
	public boolean loadData(ScreenData sd)
	{
		if(sd!=null && (allowed_input & sd.getType()) != 0)
		{
			scr_data = sd;
			data_loaded = true;
		}
		else
			data_loaded = false;
		return data_loaded;
	}

	public void run()
	{

		elapsed_time = 0;
		try
		{
			long t1 = System.currentTimeMillis();
			proccessData(scr_data);
			long t2 = System.currentTimeMillis();
			elapsed_time = t2 - t1;
			progress = 100;
		} catch(JIPException e)
		{
			dialog.error(e.getMessage(), "Error: "+name+"::run");
		}

		return;
	}

	public abstract void proccessData(ScreenData scr_data) throws JIPException;

	public void addParam(FunctionParam p)
	{
		int pos = param_names.indexOf(p.name);
		if(pos==-1)
		{
			param_list.add(p);
			param_names.add(p.name);
		}
		else
		{
			dialog.error("Parameter name already exists", "Function3D::addParam Error");
		}
	}

	public int paramValueInt(String name)
	{
		int value = 0;
		int pos = param_names.indexOf(name);

		if(pos != -1)
		{
			value = param_list.get(pos).getValueInt();
		}
		else
		{
			dialog.error("Parameter "+name+" not found", "Function3D::paramValueInt Error");
		}

		return value;
	}

	public double paramValueReal(String name)
	{
		double value = 0;
		int pos = param_names.indexOf(name);

		if(pos != -1)
		{
			value = param_list.get(pos).getValueReal();
		}
		else
		{
			dialog.error("Parameter "+name+" not found", "Function3D::paramValueReal Error");
		}

		return value;
	}

	public boolean paramValueBool(String name)
	{
		boolean value = false;
		int pos = param_names.indexOf(name);

		if(pos != -1)
		{
			value = param_list.get(pos).getValueBool();
		}
		else
		{
			dialog.error("Parameter "+name+" not found", "Function3D::paramValueBool Error");
		}

		return value;
	}

	public String paramValueString(String name)
	{
		String value = "";
		int pos = param_names.indexOf(name);

		if(pos != -1)
		{
			value = param_list.get(pos).getValueString();
		}
		else
		{
			dialog.error("Parameter "+name+" not found", "Function3D::paramValueString Error");
		}

		return value;
	}

	public ScreenData paramValueScrData(String name)
	{
		ScreenData value = null;
		int pos = param_names.indexOf(name);

		if(pos != -1)
		{
			value = param_list.get(pos).getValueScrData();
		}
		else
		{
			dialog.error("Parameter "+name+" not found", "Function3D::paramValueScrData Error");
		}

		return value;
	}

	public void updateProgress(double value)
	{
		progress = value;
		if (progress <0) progress = 0;
		if(progress>100) progress = 100;

	}

	public boolean setParamValue(String name, int value)
	{
		boolean ret = false;
		int pos;
		pos = param_names.indexOf(name);
		if(pos!=-1 && param_list.get(pos).type == ParamType.INT)
		{
			param_list.get(pos).setValue(value);
		}

		return ret;
	}

	public boolean setParamValue(String name, double value)
	{
		boolean ret = false;
		int pos;
		pos = param_names.indexOf(name);
		if(pos!=-1 && param_list.get(pos).type == ParamType.FLOAT)
		{
			param_list.get(pos).setValue(value);
		}

		return ret;
	}

	public boolean setParamValue(String name, boolean value)
	{
		boolean ret = false;
		int pos;
		pos = param_names.indexOf(name);
		if(pos!=-1 && param_list.get(pos).type == ParamType.BOOL)
		{
			param_list.get(pos).setValue(value);
		}

		return ret;
	}

	public boolean setParamValue(String name, String value)
	{
		boolean ret = false;
		int pos;
		pos = param_names.indexOf(name);
		if(pos!=-1 && (param_list.get(pos).type == ParamType.STRING || param_list.get(pos).type == ParamType.FILE))
		{
			param_list.get(pos).setValue(value);
		}

		return ret;
	}

	public boolean setParamValue(String name, ScreenData value)
	{
		boolean ret = false;
		int pos;
		pos = param_names.indexOf(name);
		if(pos!=-1 && (param_list.get(pos).type == ParamType.SCRDATA))
		{
			param_list.get(pos).setValue(value);
		}

		return ret;
	}

	public void setCanvas(MyCanvas3D can)
	{
		canvas = can;
	}

	public MyCanvas3D getCanvas()
	{
		return canvas;
	}
}
