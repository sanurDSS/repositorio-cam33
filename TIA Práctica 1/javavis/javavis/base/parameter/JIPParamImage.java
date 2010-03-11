package javavis.base.parameter;

import javavis.base.JIPParameter;
import javavis.base.ParamType;
import javavis.jip2d.base.JIPImage;


/**
 * Integer JIPParameter
 * @author miguel
 */
public class JIPParamImage extends JIPParameter {
	/**
	 * Default value
	 */
	private JIPImage defValue;
	
	/**
	 * Value of the parameter
	 */
	private JIPImage value;
	
	/** 
	 * Constructor
	 * @param n Name
	 * @param req Required
	 * @param input Input
	 */
	public JIPParamImage (String n, boolean req, boolean input) {
		super(n, req, input);
		defValue=null;
		value=null;
	}
	
	/**
	 * Sets the parameter value
	 * @param v Value
	 */
	public void setValue (JIPImage v) {
		value=v;
		assigned=true;
	}
	
	/**
	 * Gets the parameter value
	 * @return Value
	 */
	public JIPImage getValue () {
		if (assigned) return value;
		else return defValue;
	}
	
	/**
	 * Sets the default parameter value
	 * @param v Value
	 */
	public void setDefault (JIPImage v) {
		defValue = v;
	}
	
	/**
	 * Returns the default parameter value
	 * @return Value
	 */
	public JIPImage getDefault () {
		return defValue;
	}
	
	public ParamType getType () {
		return ParamType.IMAGE;
	}

}
