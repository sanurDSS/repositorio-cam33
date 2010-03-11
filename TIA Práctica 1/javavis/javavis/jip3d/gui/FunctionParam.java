package javavis.jip3d.gui;

import javavis.base.ParamType;

public class FunctionParam {

	public int iminvalue;
	public int imaxvalue;
	public double dminvalue;
	public double dmaxvalue;
	public double dstep;

	private int ivalue;
	private double rvalue;
	private String svalue;
	private boolean bvalue;
	private ScreenData sdvalue;

	public String name;
	public ParamType type;

	public FunctionParam(String n, ParamType t)
	{
		name = n;
		type = t;
		rvalue = ivalue = 0;
		svalue = "";
		bvalue = false;
		sdvalue = null;

		iminvalue = Integer.MIN_VALUE;
		imaxvalue = Integer.MAX_VALUE;
		dminvalue = -Double.MAX_VALUE;
		dmaxvalue = Double.MAX_VALUE;
		dstep = 0.5;
	}

	public void setValue(int val)
	{
		if(type==ParamType.INT)
		{
			ivalue = val;
		}
		else
		{
			System.err.println("FunctionParam::setValue<int> Error: Can not assing int to " + type);
		}
	}

	public void setValue(double val)
	{
		if(type==ParamType.FLOAT)
		{
			rvalue = val;
		}
		else
		{
			System.err.println("FunctionParam::setValue<double> Error: Can not assing double to " + type);
		}
	}

	public void setValue(String val)
	{
		if(type==ParamType.STRING || type==ParamType.FILE || type==ParamType.DIR)
		{
			svalue = val;
		}
		else
		{
			System.err.println("FunctionParam::setValue<String> Error: Can not assing String to " + type);
		}
	}

	public void setValue(boolean val)
	{
		if(type==ParamType.BOOL)
		{
			bvalue = val;
		}
		else
		{
			System.err.println("FunctionParam::setValue<int> Error: Can not assing boolean to " + type);
		}
	}

	public void setValue(ScreenData val)
	{
		if(type==ParamType.SCRDATA)
		{
			sdvalue = val;
		}
		else
		{
			System.err.println("FunctionParam::setValue<int> Error: Can not assing ScreenData to " + type);
		}
	}

	public int getValueInt()
	{
		return ivalue;
	}

	public double getValueReal()
	{
		return rvalue;
	}

	public String getValueString()
	{
		return svalue;
	}

	public boolean getValueBool()
	{
		return bvalue;
	}

	public ScreenData getValueScrData()
	{
		return sdvalue;
	}
}
