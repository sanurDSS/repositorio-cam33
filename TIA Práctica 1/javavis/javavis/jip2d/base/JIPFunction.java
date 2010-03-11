package javavis.jip2d.base;

import java.io.Serializable;
import java.util.ArrayList;
import javavis.base.JIPException;
import javavis.base.JIPParameter;
import javavis.base.ParamType;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamDir;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamImage;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.base.parameter.JIPParamObject;
import javavis.base.parameter.JIPParamString;

/**
* Class to define function objects. A function is used to process an image 
* (sequence) and to return an image (sequence). A function can have some additional 
* parameters (input and/or output). Parameters can be get or set by name. Moreover, 
* a function has a name and a description of what it does to the input image.
* This class is abstract, so a function must implement this class.
*/
public abstract class JIPFunction implements Serializable {
	private static final long serialVersionUID = 6929836837647191255L;

	/**
	 * Name of the group at which this function is assigned.
	 */
	protected FunctionGroup groupFunc=FunctionGroup.Others;

	/**
	 * Name of the function. 
	 */
	protected String name;

	/**
	 * Description of the function. 
	 */
	protected String description;

	/**
	 * Array of Input and output parameters of the function. 
	 */
	private ArrayList<JIPParameter> params;

	/**
	 * Can contain some information at the end of the processing.
	 */
	protected String info;

	/**
	 * Contains the percentage of completeness in the current processing
	 */
	protected int percProgress = 0;

	/**
	* Function constructor. 
	*/
	public JIPFunction() {
		params =  new ArrayList<JIPParameter>();
	}


	/**
	 * Gets the name of the function.
	 * @return Name of the function.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the description of the function. 
	 * @return Description of the function.
	 */
	public String getDescription() {
		return description;
	}

	/**
	* Gets an output image from a input image. This method must be implemented and contains the
	* core of the function. 
	* @param img Input image.
	* @return Output image.
	*/
	public abstract JIPImage processImg(JIPImage img) throws JIPException;

	/**
	 * This is the default behavior when processing a sequence: every image in the sequence
	 * is processed when the processImg method. Thus, the result is a sequence with the same
	 * number of images than the input sequence. Every image in the new sequence is the result
	 * of applying the processImg method.
	 * This method can be overloaded if we wish to process the sequence in a different way.
	* @param seq Input sequence.
	* @return Output sequence.
	*/
	public JIPSequence processSeq(JIPSequence seq) throws JIPException {
		JIPSequence res = null;
		JIPImage imgRes;
		if (seq.getNumFrames() > 0) {
			res = new JIPSequence();
			for (JIPImage img : seq.getFrames()) {
				imgRes = processImg(img);
				if (imgRes != null) {
					imgRes.setName(img.getName());
					res.addFrame(imgRes);
				}
				else  res.addFrame(img.clone());	
			}
			res.setName(seq.getName());
		}
		return res;
	}

	/**
	 * Gets the number of input parameters of the function.	 
	 * @return Number of input parameters.
	 */
	public int getNumInputParams() {
		int cont=0;
		for (JIPParameter param : params) 
			if (param.isInput()) cont++;
		return cont;
	}

	/**
	 * Gets the number of output parameters of the function.	 
	 * @return Number of output parameters.
	 */
	public int getNumOutputParams() {
		int cont=0;
		for (JIPParameter param : params) 
			if (!param.isInput()) cont++;
		return cont;
	}

	/**
	 * Gets all the input parameters name of the function.	 
	 * @return Array with input parameters name of the function.
	 */
	public String[] getInputParamNames() {
		String[] res = null;
		JIPParameter param;
		int nparams = getNumInputParams();
		if (nparams > 0)
			res = new String[nparams];
		for (int i = 0, j=0; i < params.size(); i++) {
			param = params.get(i);
			if (param.isInput()) 
				res[j++] = param.getName();
		}
		return res;
	}

	/**
	 * Gets all the output parameter name of the function.	 
	 * @return Array with output parameters name of the function.
	 */
	public String[] getOutputParamNames() {
		String[] res = null;
		JIPParameter param;
		int nparams = getNumInputParams();
		if (nparams > 0)
			res = new String[nparams];
		for (int i = 0; i < nparams; i++) {
			param = params.get(i);
			if (!param.isInput()) 
				res[i] = param.getName();
		}
		return res;
	}

	/**
	* Returns if there is or not a input parameter of the function with the given name.
	* @param nom Name to check.
	* @return true if the function has a parameter which is equal to nom, false elsewhere.  
	*/
	public boolean isInputParam(String nom) {
		for (JIPParameter p : params)
			if (p.isInput() && nom.equals(p.getName())) 
				return true;
		return false;
	}

	/**
	* Returns if there is or not a output parameter of the function with the given name.
	* @param nom Name to check.
	* @return true if the function has a output parameter which is equal to nom, false elsewhere.  
	*/
	public boolean isOutputParam(String nom) {
		for (JIPParameter p : params)
			if (!p.isInput() && nom.equals(p.getName())) 
				return true;
		return false;
	}

	/**
	* Returns if a input parameter of the function is required.	 
	* @param nom Name to check.
	* @return true if function has an input parameter which name is nom and this is required, false elsewhere. 
	*/
	public boolean isInputParamRequired(String nom) {
		for (JIPParameter p : params)
			if (p.isInput() && nom.equals(p.getName()) && p.isRequired())
				return true;
		return false;
	}

	/**
	* Returns if a output parameter of the function is required.	 
	* @param nom Name to check.
	* @return true if function has an output parameter which name is nom and this is required, false elsewhere. 
	*/
	public boolean isOutputParamRequired(String nom) {
		for (JIPParameter p : params)
			if (!p.isInput() && nom.equals(p.getName()) && p.isRequired())
				return true;
		return false;
	}

	/**
	 * Returns if a input parameter of the function has assigned a value.	 
	 * @param nom Name to check.
	 * @return true if function has a input parameter which name is nom and its value has been assigned 
	 * false elsewhere.
	 */
	public boolean isInputParamAssigned(String nom) {
		for (JIPParameter p : params)
			if (p.isInput() && nom.equals(p.getName()) && p.isAssigned())
				return true;
		return false;
	}

	/**
	 * Returns if a output parameter of the function has assigned a value.	 
	 * @param nom Name to check.
	 * @return true if function has a output parameter which name is nom and its value has been assigned 
	 * false elsewhere.
	 */
	public boolean isOutputParamAssigned(String nom) {
		for (JIPParameter p : params)
			if (!p.isInput() && nom.equals(p.getName()) && p.isAssigned())
				return true;
		return false;
	}

	/**
	* Gets the function parameter type.	
	* @param nom Parameter name.
	* @return Parameter type
	*/
	public ParamType getParamType(String nom) throws JIPException {
		for (JIPParameter p : params)
			if (nom.equals(p.getName()))
				return p.getType();
		throw new JIPException("JIPFunction.getParamType: parameter not found");
	}

	/**
	 * Gets a description of the function parameter.
	 * @param nom Parameter name.
	 * @return Description of parameter. 	 
	 */
	public String getParamDescr(String nom) throws JIPException {
		for (JIPParameter p : params)
			if (nom.equals(p.getName()))
				return p.getDescription();
		throw new JIPException("JIPFunction.getParamDesc: parameter not found");
	}

	/**
	 * Sets a boolean value in a parameter of the function.	 
	 * @param nom Name of parameter to assign (It must exist and be a BOOL type)
	 * @param v Value to assign.
	 */
	public void setParamValue(String nom, boolean v) throws JIPException {
		boolean found=false;
		for (JIPParameter p : params)
			if (nom.equals(p.getName()) && p.getType() == ParamType.BOOL) {
				((JIPParamBool)p).setValue(v);
				found=true;
				break;
			}
		if (!found) 
			throw new JIPException("JIPFunction.setParamValue: parameter not found");
	}

	/**
	 * Sets a integer value in a parameter of the function.
	 * @param nom Name of parameter to assign (It must exist and be a INT type)
	 * @param v Value to assign.
	 */
	public void setParamValue(String nom, int v) throws JIPException {
		boolean found=false;
		for (JIPParameter p : params)
			if (nom.equals(p.getName()) && p.getType() == ParamType.INT) {
				((JIPParamInt)p).setValue(v);
				found=true;
				break;
			}
		if (!found) 
			throw new JIPException("JIPFunction.setParamValue: parameter not found");
	}

	/**
	 * Sets a real value in a parameter of the function.	 
	 * @param nom Name of Parameter to assign (It should exist and be a FLOAT type)
	 * @param v Value to assign.
	 */
	public void setParamValue(String nom, float v) throws JIPException {
		boolean found=false;
		for (JIPParameter p : params)
			if (nom.equals(p.getName()) && p.getType() == ParamType.FLOAT) {
				((JIPParamFloat)p).setValue(v);
				found=true;
				break;
			}
		if (!found) 
			throw new JIPException("JIPFunction.setParamValue: parameter not found");
	}

	/**
	 * Sets a string value in a parameter of the function.	 
	 * @param nom Name of Parameter to assign (It must exist and be a STRING, FILE or DIR type)
	 * @param v Value to assign.
	 */
	public void setParamValue(String nom, String v) throws JIPException {
		boolean found=false;
		for (JIPParameter p : params)
			if (nom.equals(p.getName())) {
				switch (p.getType()) {
					case STRING: ((JIPParamString)p).setValue(v); 
								found=true; break;
					case FILE: ((JIPParamFile)p).setValue(v); 
								found=true; break;
					case DIR: ((JIPParamDir)p).setValue(v); 
								found=true; break;
					case LIST: ((JIPParamList)p).setValue(v); 
								found=true; break;
				}
				break;
			}
		if (!found) 
			throw new JIPException("JIPFunction.setParamValue: parameter not found");
	}

	/**
	 * Sets a string value in a parameter of the function.	 
	 * @param nom Name of Parameter to assign (It must exist and be a LIST type)
	 * @param v Value to assign.
	 */
	public void setParamValue(String nom, String []v) throws JIPException {
		boolean found=false;
		for (JIPParameter p : params)
			if (p.getType()==ParamType.LIST && nom.equals(p.getName())) {
				((JIPParamList)p).setDefault(v);
				found=true;
				break;
			}
		if (!found) 
			throw new JIPException("JIPFunction.setParamValue: parameter not found");
	}

	/**
	 * It assigns a value of the image in a parameter of the function.	 
	 * @param nom Name of parameter to assign (It must exist and be a IMAGE type)
	 * @param img Value to assign.
	 */
	public void setParamValue(String nom, JIPImage img) throws JIPException {
		boolean found=false;
		for (JIPParameter p : params)
			if (nom.equals(p.getName()) && p.getType() == ParamType.IMAGE) {
				((JIPParamImage)p).setValue(img);
				found=true;
				break;
			}
		if (!found) 
			throw new JIPException("JIPFunction.setParamValue: parameter not found");
	}

	/**
	 * It assigns a value of the image in a parameter of the function.	 
	 * @param nom Name of parameter to assign (It must exist and be a OBJECT type)
	 * @param img Value to assign.
	 */
	public void setParamValue(String nom, Object obj) throws JIPException {
		boolean found=false;
		for (JIPParameter p : params)
			if (nom.equals(p.getName()) && p.getType() == ParamType.OBJECT) {
				((JIPParamObject)p).setValue(obj);
				found=true;
				break;
			}
		if (!found) 
			throw new JIPException("JIPFunction.setParamValue: parameter not found");
	}

	/**
	 * Gets a boolean value from a parameter of the function.	 
	 * @param nom Name of parameter to find (It must exist and be a BOOL type)
	 * @return Value found.
	 */
	public boolean getParamValueBool(String nom) throws JIPException {
		for (JIPParameter p : params)
			if (nom.equals(p.getName()) && p.getType() == ParamType.BOOL) 
				return ((JIPParamBool)p).getValue();
		throw new JIPException("JIPFunction.getParamValue: parameter not found");
	}

	/**
	 * Gets a integer value from a parameter of the function.
	 * @param nom Name of parameter to find (It must exist and be a INT type)
	 * @return Value found
	 */
	public int getParamValueInt(String nom) throws JIPException {
		for (JIPParameter p : params)
			if (nom.equals(p.getName()) && p.getType() == ParamType.INT) 
				return ((JIPParamInt)p).getValue();
		throw new JIPException("JIPFunction.getParamValue: parameter not found");
	}

	/**
	 * Gets a float value from a parameter of the function.	 
	 * @param nom Name of Parameter to find (It must exist and be a FLOAT type)
	 * @return v Value found.
	 */
	public float getParamValueFloat(String nom) throws JIPException {
		for (JIPParameter p : params)
			if (nom.equals(p.getName()) && p.getType() == ParamType.FLOAT) 
				return ((JIPParamFloat)p).getValue();
		throw new JIPException("JIPFunction.getParamValue: parameter not found");
	}

	/**
	 * Gets a string value from a parameter of the function.	 
	 * @param nom Name of Parameter to found (It must exist and be a STRING, FILE or DIR type)
	 * @return Value found.
	 */
	public String getParamValueString(String nom) throws JIPException {
		for (JIPParameter p : params)
			if (nom.equals(p.getName())) {
				switch (p.getType()) {
					case STRING: return ((JIPParamString)p).getValue();
					case FILE: return ((JIPParamFile)p).getValue();
					case DIR: return ((JIPParamDir)p).getValue();
					case LIST: return ((JIPParamList)p).getValue();
					case IMAGE: return "";
				}
				break;
			}
		throw new JIPException("JIPFunction.getParamValue: parameter not found");
	}

	/**
	 * Gets a string value from a parameter of the function.	 
	 * @param nom Name of Parameter to find (It must exist and be a LIST type)
	 * @return Value found.
	 */
	public String[] getParamValueList(String nom) throws JIPException {
		for (JIPParameter p : params)
			if (p.getType()==ParamType.LIST && nom.equals(p.getName())) 
				return ((JIPParamList)p).getDefault();
		throw new JIPException("JIPFunction.getParamValue: parameter not found");
	}

	/**
	 * Gets a value of the image from a parameter of the function.	 
	 * @param nom Name of parameter to find (It must exist and be a IMAGE type)
	 * @return Value found.
	 */
	public JIPImage getParamValueImg(String nom) throws JIPException {
		for (JIPParameter p : params) 
			if (nom.equals(p.getName()) && p.getType() == ParamType.IMAGE) 
				return ((JIPParamImage)p).getValue();
		throw new JIPException("JIPFunction.getParamValue: parameter not found");
	}

	/**
	 * Gets a value of the image from a parameter of the function.	 
	 * @param nom Name of parameter to find (It must exist and be a OBJECT type)
	 * @return Value found.
	 */
	public Object getParamValueObj(String nom) throws JIPException {
		for (JIPParameter p : params)
			if (nom.equals(p.getName()) && p.getType() == ParamType.OBJECT) 
				return ((JIPParamObject)p).getValue();
		throw new JIPException("JIPFunction.getParamValue: parameter not found");
	}

	/**
	 * It checks if every function the required parameters have an assigned value.	 
	 * @return true if every function required parameter have some assigned value.
	 * or if the function does not have parameters. Else return false.
	 */
	public boolean paramsOK() {
		for (JIPParameter p : params)
			if (p.isRequired() && !p.isAssigned())
				return false;
		return true;
	}
	
	/**
	 * It returns if there was some information when function was used.	 
	 * @return true if there was some information false elsewhere.
	 */
	public boolean isInfo() {
		return info!=null;
	}
	
	/**
	 * It returns the information message of the function.	 
	 * @return The generated information.
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * It returns the group at which this function is assigned.	 
	 * @return A FunctionGroup indicating the group.
	 */
	public FunctionGroup getGroupFunc() {
		return groupFunc;
	}

	/**
	 * It returns the group at which this function is assigned.	 
	 * @param gf A integer indicating the group.
	 */
	public void setGroupFunc(FunctionGroup gf) {
		groupFunc = gf;
	}

	/**
	 * Returns the percentage of completeness	 
	 * @return Integer.
	 */
	public int getProgress() {
		return percProgress;
	}

	/**
	 * Add a new parameter to the parameter array 	 
	 */
	public void addParam(JIPParameter p) {
		params.add(p);
	}
}
