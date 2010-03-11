package javavis.base.parameter;

import javavis.base.JIPParameter;
import javavis.base.ParamType;


/**
 * Integer JIPParameter
 * @author miguel
 */
public class JIPParamInt extends JIPParameter {
	/**
	 * Default value
	 */
	private int defValue;
	
	/**
	 * Value of the parameter
	 */
	private int value;
	
	/** 
	 * Constructor
	 * @param n Name
	 * @param req Required
	 * @param input Input
	 */
	public JIPParamInt (String n, boolean req, boolean input) {
		super(n, req, input);
		defValue=0;
		value=0;
		assigned=false;
	}
	
	/**
	 * Sets the parameter value
	 * @param v Value
	 */
	public void setValue (int v) {
		value=v;
		assigned=true;
	}
	
	/**
	 * Gets the parameter value
	 * @return Value
	 */
	public int getValue () {
		if (assigned) return value;
		else return defValue;
	}
	
	/**
	 * Sets the default parameter value
	 * @param v Value
	 */
	public void setDefault (int v) {
		defValue = v;
	}
	
	/**
	 * Returns the default parameter value
	 * @return Value
	 */
	public int getDefault () {
		return defValue;
	}
	
	public ParamType getType () {
		return ParamType.INT;
	}

}
