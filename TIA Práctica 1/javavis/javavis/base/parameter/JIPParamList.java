package javavis.base.parameter;

import javavis.base.JIPParameter;
import javavis.base.ParamType;


/**
 * Integer JIPParameter
 * @author miguel
 */
public class JIPParamList extends JIPParameter {
	/**
	 * Default value
	 */
	private String[] defValue;
	
	/**
	 * Value of the parameter
	 */
	private String value;
	
	/** 
	 * Constructor
	 * @param n Name
	 * @param req Required
	 * @param input Input
	 */
	public JIPParamList (String n, boolean req, boolean input) {
		super(n, req, input);
		defValue=null;
		value=null;
	}
	
	/**
	 * Sets the parameter value
	 * @param v Value
	 */
	public void setValue (String v) {
		value=v;
		assigned=true;
	}
	
	/**
	 * Gets the parameter value
	 * @return Value
	 */
	public String getValue () {
		if (assigned) return value;
		else return defValue[0];
	}
	
	/**
	 * Sets the default parameter value
	 * @param v Value
	 */
	public void setDefault (String[] v) {
		defValue = v;
	}
	
	/**
	 * Returns the default parameter value
	 * @return Value
	 */
	public String[] getDefault () {
		return defValue;
	}
	
	public ParamType getType () {
		return ParamType.LIST;
	}

}
