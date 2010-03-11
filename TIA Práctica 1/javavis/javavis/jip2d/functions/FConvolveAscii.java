package javavis.jip2d.functions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpByte;


/**
*Makes a Convolution of an image with a matrix which is read from a file
*in a Ascii format. In the first row of the file we get the width and height 
*and the rest of rows define the matrix.<BR>
*It is applicable for: BYTE, SHORT, COLOR and FLOAT types.<BR>
*Use: FConvolveImage<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>matrix: File containing the matrix we use to do the convolution.<BR>
*<li>mult: Multiplier<BR>
*<li>div: Divisor <BR>
*<li>method: Method to treat the borders. ZERO->border pixels are marked as 0.
*PAD->The first row is duplicated so that the -1 row is the same. The same for last row and first and last columns. 
*WRAP->The -1 row is the last row and the n+1 row is the first. The same for columns.<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Convolution of Image with the input matrix, with the same type as the input image.<BR><BR>
*</ul>
*Additional notes: The matrix is converted in an image and then FConvolveImage is called.
*/

public class FConvolveAscii extends JIPFunction {
	private static final long serialVersionUID = -2958725137803649960L;

	public FConvolveAscii() {
		super();
		name = "FConvolveAscii";
		description = "Convolution of an image with a matrix";
		groupFunc = FunctionGroup.Convolution;

		JIPParamFile p1 = new JIPParamFile("matrix", true, true);
		p1.setDescription("Matrix for convolution");
		JIPParamFloat p2 = new JIPParamFloat("mult", false, true);
		p2.setDefault(1.0f);
		p2.setDescription("Multiplier");
		JIPParamFloat p3 = new JIPParamFloat("div", false, true);
		p3.setDefault(1.0f);
		p3.setDescription("Divisor");
		JIPParamList p4 = new JIPParamList("method", false, true);
		String []paux = new String[3];
		paux[0]="ZERO";
		paux[1]="WRAP";
		paux[2]="PAD";
		p4.setDefault(paux);
		p4.setDescription("Method to process the border");

		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		JIPImage res = null;
		ImageType t = img.getType();
		if (t == ImageType.EDGES || t == ImageType.POINT || t == ImageType.SEGMENT || 
				t == ImageType.POLY) 
			throw new JIPException("ConvolveAscii can not be applied to this image type");
		
		String convoMat = getParamValueString("matrix");
		float p2 = getParamValueFloat("mult");
		float p3 = getParamValueFloat("div");
		String p4 = getParamValueString("method");

		try {
			FileInputStream convoF = new FileInputStream(convoMat);
			Reader r = new BufferedReader(new InputStreamReader(convoF));
			StreamTokenizer st = new StreamTokenizer(r);

			st.nextToken();
			int cw = (int) st.nval;
			st.nextToken();
			int ch = (int) st.nval;

			double[] mat = new double[cw * ch];
			for (int cont = 0; cont < cw * ch; cont++) {
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_EOF) 
					throw new JIPException("Error reading ascii file");
				mat[cont] = (float) st.nval;
			}
			JIPFunction convolucion = new FConvolveImage();
			JIPBmpByte convo = new JIPBmpByte(cw, ch);
			convo.setAllPixels(mat);
			convolucion.setParamValue("image", convo);
			convolucion.setParamValue("div", p3);
			convolucion.setParamValue("mult", p2);
			convolucion.setParamValue("method", p4);

			res = convolucion.processImg(img);
			if (convolucion.isInfo())
				info = "ConvolveAscii info: "+convolucion.getInfo();
		} catch (FileNotFoundException e) {
			throw new JIPException("File Not Found");
		} catch (IOException e) {
			throw new JIPException("IO Exception");
		}
		return res;
	}
}
