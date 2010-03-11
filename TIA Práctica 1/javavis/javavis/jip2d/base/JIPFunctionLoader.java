package javavis.jip2d.base;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import javavis.base.JIPException;
import javavis.base.JIPToolkit;
import javavis.base.ParamType;

import org.apache.log4j.Logger;

/**
 * This class receives a descriptor file of functions and it loads an array with this functions. 
 * The file must have this syntax:  FNombreFuncion1 { <BR> parameter11=value11; <BR> parameter21=value21;<BR> }<BR> <BR> 
 * FNombreFuncion2 { <BR> parameter12=value12; <BR> parameter22=value22;<BR> }<BR> <BR> ...<BR> 
 * The value of the parameters depends on type:<BR> BOOL : true o false<BR> IMAGE : string with the JIP image name<BR> 
 * STRING : string<BR> INT :  numerical<BR> FLOAT : numerical<BR> <BR> Use:<BR> 
 * JIPFunctionLoader loader = new JIPFunctionLoader("functions_file_descriptor");<BR> 
 * <BR> JIPFunction [] funcions = loader.getFunctions();<BR>
 */
public class JIPFunctionLoader {
	public final static int T_IDENT = 0;
	public final static int T_INI_BRACK = 1;
	public final static int T_END_BRACK = 2;
	public final static int T_STRING = 3;
	public final static int T_TRUE = 4;
	public final static int T_FALSE = 5;
	public final static int T_NUMBER = 6;
	public final static int T_UNKNOW = 7;
	public final static int T_EQUAL = 8;
	public final static int T_SEMICOLON = 9;
	public final static int T_EOF = 10;
	
	private static Logger logger = Logger.getLogger(JIPFunctionLoader.class);

	protected final static String[] t_names = {
			"identifier",
			"{",
			"}",
			"string",
			"true",
			"false",
			"number",
			"unknow",
			"=",
			";",
			"eof" };

	StreamTokenizer st;

	/**
	 */
	ArrayList<JIPFunction> functions = new ArrayList<JIPFunction>();

	/**
	 * 
	 */
	JIPFunction current_func;

	String function_name;
	String current_param;

	int line = 1;

	public JIPFunctionLoader(String file) {
		try {
			FileReader in = new FileReader(file);
			st = new StreamTokenizer(in);
			st.eolIsSignificant(true);

			try {
				parse();
			} catch (ParseException pe) {logger.error(pe);}

		} catch (IOException e) {
			logger.error("Can not open file " + file);
		}
	}

	/**
	 * @return  the functions
	 */
	public JIPFunction[] getFunctions() {
		JIPFunction[] func_array = new JIPFunction[functions.size()];

		for (int i = 0; i < func_array.length; i++)
			func_array[i] = functions.get(i);

		return func_array;
	}

	// --------------------------------------------------------------------------------
	//	Lexical analyzer
	// --------------------------------------------------------------------------------

	private int nextToken() {
		try {
			do {
				st.nextToken();

				if (st.ttype == StreamTokenizer.TT_EOL)
					line++;
			} while (st.ttype == StreamTokenizer.TT_EOL);
		} catch (Exception e) {
			logger.error("Can not read data from file");
		}

		switch (st.ttype) {
			case StreamTokenizer.TT_EOF :
				return T_EOF;
			case StreamTokenizer.TT_NUMBER :
				return T_NUMBER;
			case StreamTokenizer.TT_WORD :
				if (st.sval.equals("true"))
					return T_TRUE;
				if (st.sval.equals("false"))
					return T_FALSE;
				return T_IDENT;
			case '"' :
				return T_STRING;
			case '{' :
				return T_INI_BRACK;
			case '}' :
				return T_END_BRACK;
			case '=' :
				return T_EQUAL;
			case ';' :
				return T_SEMICOLON;
			default :
				return T_UNKNOW;
		}
	}

	// --------------------------------------------------------------------------------
	//	Parser functions
	// --------------------------------------------------------------------------------

	private void parse() throws ParseException {
		S();
	}

	private void eat(int tok) throws ParseException {
		int token = nextToken();

		if (token != tok) {
			throw new ParseException(
				"Found " + t_names[token] + ", " + t_names[tok] + " expected",
				line);
		}
	}

	private void S() throws ParseException {
		int token = nextToken();

		if (token == T_IDENT) {
			// Creates new JIPFunction
			function_name = st.sval;
			try {
				current_func = (JIPFunction) Class.forName("javavis.jip2d.functions." + function_name).newInstance();
			} catch (InstantiationException e) {
				throw new ParseException(
					"Function " + function_name + " not found",
					line);
			} catch (ClassNotFoundException e) {
				throw new ParseException(
						"Function " + function_name + " not found",
						line);
			} catch (IllegalAccessException e) {
				throw new ParseException(
						"Function " + function_name + " not found",
						line);
			}

			eat(T_INI_BRACK);
			filterBlock();
			eat(T_END_BRACK);

			// Checks for missing params
			String[] params = current_func.getInputParamNames();
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					if (current_func.isInputParamRequired(params[i])
						&& !current_func.isInputParamAssigned(params[i])) {
						logger.warn("Param " + params[i] + " is required in function "
								+ function_name);
					}
				}
			}

			// Adds the new JIPFunction
			functions.add(current_func);

			S();
		} else if (token != T_EOF) {
			throw new ParseException("Found " + t_names[token] + ", identifier or eof expected",
				line);
		}
	}

	private void filterBlock() throws ParseException {
		int token = nextToken();

		if (token == T_IDENT) {
			st.pushBack();

			filterParam();
			eat(T_SEMICOLON);
			filterBlock();
		} else if (token == T_END_BRACK) {
			st.pushBack();
		} else {
			throw new ParseException("Found " + t_names[token] + ", identifier or } expected",
				line);
		}
	}

	private void filterParam() throws ParseException {
		eat(T_IDENT);

		current_param = st.sval;

		eat(T_EQUAL);
		paramValue();
	}

	private void paramValue() throws ParseException {
		int token = nextToken();
		try {
			ParamType p_type = current_func.getParamType(current_param);
	
			if (token == T_STRING) {
				if (p_type == ParamType.STRING) {
					current_func.setParamValue(current_param, st.sval);
				} else if (p_type == ParamType.IMAGE) {
					JIPImage img = JIPToolkit.getColorImage(JIPToolkit.getAWTImage(st.sval));
					current_func.setParamValue(current_param, img);
				} else if (p_type == null) {
					throw new ParseException(
						"Param "
							+ current_param
							+ " does not exist in "
							+ function_name,
						line);
				} else {
					throw new ParseException("Param " + current_param
							+ " has a wrong type in " + function_name, line);
				}
			} else if (token == T_TRUE) {
				if (p_type == ParamType.BOOL) {
					current_func.setParamValue(current_param, true);
				} else if (p_type == null) {
					throw new ParseException("Param " + current_param
							+ " does not exist in " + function_name, line);
				} else {
					throw new ParseException("Param "+ current_param
							+ " has a wrong type in "+ function_name,line);
				}
			} else if (token == T_FALSE) {
				if (p_type == ParamType.BOOL) {
					current_func.setParamValue(current_param, false);
				} else if (p_type == null) {
					throw new ParseException(
						"Param "
							+ current_param
							+ " does not exist in "
							+ function_name,
						line);
				} else {
					throw new ParseException(
						"Param "
							+ current_param
							+ " has a wrong type in "
							+ function_name,
						line);
				}
			} else if (token == T_NUMBER) {
				if (p_type == ParamType.INT) {
					current_func.setParamValue(current_param, (int) st.nval);
				} else if (p_type == ParamType.FLOAT) {
					current_func.setParamValue(current_param, (float) st.nval);
				} else if (p_type == null) {
					throw new ParseException(
						"Param "
							+ current_param
							+ " does not exist in "
							+ function_name,
						line);
				} else {
					throw new ParseException(
						"Param "
							+ current_param
							+ " has a wrong type in "
							+ function_name,
						line);
				}
			} else {
				throw new ParseException(
					"Found "
						+ t_names[token]
						+ ", string, true, false or number expected",
					line);
			}
		}catch (JIPException e) {logger.error(e);}
	}
}
