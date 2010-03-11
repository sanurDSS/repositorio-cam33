package javavis.base.parameter;

import javavis.base.JIPParameter;
import javavis.base.ParamType;


/**
 * Integer JIPParameter
 * @author miguel
 */
public class JIPParamObject extends JIPParameter {
	/**
	 * Default value
	 */
	private Object defValue;
	
	/**
	 * Value of the parameter
	 */
	private Object value;
	
	/** 
	 * Constructor
	 * @param n Name
	 * @param req Required
	 * @param input Input
	 */
	public JIPParamObject (String n, boolean req, boolean input) {
		super(n, req, input);
		defValue=null;
		value=null;
	}
	
	/**
	 * Sets the parameter value
	 * @param v Value
	 */
	public void setValue (Object v) {
		value=v;
		assigned=true;
	}
	
	/**
	 * Gets the parameter value
	 * @return Value
	 */
	public Object getValue () {
		if (assigned) return value;
		else return defValue;
	}
	
	/**
	 * Sets the default parameter value
	 * @param v Value
	 */
	public void setDefault (Object v) {
		defValue = v;
	}
	
	/**
	 * Returns the default parameter value
	 * @return Value
	 */
	public Object getDefault () {
		return defValue;
	}
	
	public ParamType getType () {
		return ParamType.OBJECT;
	}

}
