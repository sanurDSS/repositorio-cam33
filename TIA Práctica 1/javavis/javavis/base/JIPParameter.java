package javavis.base;

import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamDir;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamImage;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.base.parameter.JIPParamObject;
import javavis.base.parameter.JIPParamScrData;
import javavis.base.parameter.JIPParamString;


/**
* Class to define the parameter object. 
* A parameter can be: BOOL, INT, FLOAT, STRING, IMAGE, LIST, DIR and OBJECT, all of them
* defined in the ParameterType enum. A parameter can be required or no.
* In case of not required, the parameter can have a default assigned value.
* The basic constructor of parameter should specify its name, type and if it is required
* or not. That characteristics do not change during the object life.
* We can assign a description parameter which shows its operation.
* The parameter value can be get or set. 
*/
public abstract class JIPParameter {
	/**
	 * Name of parameter
	 */
	String name;

	/**
	 * Indicates if the parameter is required
	 */
	boolean required;

	/**
	 * Indicates if the parameter has a value assigned
	 */
	protected boolean assigned;

	/**
	 * Description of the parameter
	 */
	String description;
	
	/** 
	 * Indicates if it is a input (true) or output (false) parameter
	 */
	private boolean input;


	/**
	* Constructor.
	* @param n Name
	* @param req Flag which indicates if it is required (true) or not (false).
	* @param input Flag which indicates if it is an input (true) or output (false) parameter.
	*/
	public JIPParameter(String n, boolean req, boolean input) {
		name = n;
		assigned = false;
		this.input = input;
		if (!input)
			required=false; // Output parameters can not be required
		else
			required = req;
	}
	
	public static JIPParameter newInstance (String n, boolean req, boolean input, ParamType pt) {
		switch (pt) {
			case BOOL: return new JIPParamBool(n, req, input);
			case DIR: return new JIPParamDir(n, req, input);
			case FILE: return new JIPParamFile(n, req, input);
			case FLOAT: return new JIPParamFloat(n, req, input);
			case IMAGE: return new JIPParamImage(n, req, input);
			case INT: return new JIPParamInt(n, req, input);
			case LIST: return new JIPParamList(n, req, input);
			case OBJECT: return new JIPParamObject(n, req, input);
			case SCRDATA: return new JIPParamScrData(n, req, input);
			case STRING: return new JIPParamString(n, req, input);
			default: return null;
		}
	}

	/**
	 * Gets the name of the parameter.
	 * @return Name of parameter.
	 */
	public String getName() {
		return name;
	}

	/**
	 * It gets the type of parameter.	
	 * @return Type of parameter.
	 */
	public abstract ParamType getType();

	/**
	 * Returns if the parameter is required or not.
	 * @return true if the parameter is required else return false.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Returns if the parameter value is assigned o no.	 
	 * @return true if the parameter value is assigned else return false.
	 */
	public boolean isAssigned() {
		return assigned;
	}

	/**
	 * Sets a description.	 
	 * @param d Description assigned to the parameter.
	 */
	public void setDescription(String d) {
		description = d;
	}

	/**
	 * It gets the description that assigns on the parameter.
	 * @return Description assigns to the parameter.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns if the parameter is a input or output parameter
	 * @return true if input parameter, false else where
	 */
	public boolean isInput() {
		return input;
	}
}
