package javavis.base.parameter;

import javavis.base.JIPParameter;
import javavis.base.ParamType;


/**
 * Integer JIPParameter
 * @author miguel
 */
public class JIPParamFloat extends JIPParameter {
	/**
	 * Default value
	 */
	private float defValue;
	
	/**
	 * Value of the parameter
	 */
	private float value;
	
	/** 
	 * Constructor
	 * @param n Name
	 * @param req Required
	 * @param input Input
	 */
	public JIPParamFloat (String n, boolean req, boolean input) {
		super(n, req, input);
		defValue=0.0f;
		value=0.0f;
	}
	
	/**
	 * Sets the parameter value
	 * @param v Value
	 */
	public void setValue (float v) {
		value=v;
		assigned=true;
	}
	
	/**
	 * Gets the parameter value
	 * @return Value
	 */
	public float getValue () {
		if (assigned) return value;
		else return defValue;
	}
	
	/**
	 * Sets the default parameter value
	 * @param v Value
	 */
	public void setDefault (float v) {
		defValue = v;
	}
	
	/**
	 * Returns the default parameter value
	 * @return Value
	 */
	public float getDefault () {
		return defValue;
	}
	
	public ParamType getType () {
		return ParamType.FLOAT;
	}

}
