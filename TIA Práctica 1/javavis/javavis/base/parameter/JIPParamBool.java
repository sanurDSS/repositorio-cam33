package javavis.base.parameter;

import javavis.base.JIPParameter;
import javavis.base.ParamType;

/**
 * Boolean JIPParameter
 * @author miguel
 */
public class JIPParamBool extends JIPParameter {
	/**
	 * Default value
	 */
	private boolean defValue;
	
	/**
	 * Value of the parameter
	 */
	private boolean value;
	
	/** 
	 * Constructor
	 * @param n Name
	 * @param req Required
	 * @param input Input
	 */
	public JIPParamBool (String n, boolean req, boolean input) {
		super(n, req, input);
		defValue=false;
		value=false;
	}
	
	/**
	 * Sets the parameter value
	 * @param v Value
	 */
	public void setValue (boolean v) {
		value=v;
		assigned=true;
	}
	
	/**
	 * Gets the parameter value
	 * @return Value
	 */
	public boolean getValue () {
		if (assigned) return value;
		else return defValue;
	}
	
	/**
	 * Sets the default parameter value
	 * @param v Value
	 */
	public void setDefault (boolean v) {
		defValue = v;
	}
	
	/**
	 * Returns the default parameter value
	 * @return Value
	 */
	public boolean getDefault () {
		return defValue;
	}
	
	public ParamType getType () {
		return ParamType.BOOL;
	}

}
