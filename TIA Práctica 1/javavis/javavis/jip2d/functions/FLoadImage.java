package javavis.jip2d.functions;

import java.awt.Image;

import javavis.base.JIPException;
import javavis.base.JIPToolkit;
import javavis.base.parameter.JIPParamFile;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;

/**
* Load the image (jpeg, gif) in the file indicated as parameter.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>file: File name.<BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<BR><BR>
*</ul>
*/

public class FLoadImage extends JIPFunction {
	private static final long serialVersionUID = -4783214630724479756L;

	public FLoadImage() {
		super();
		name = "FLoadImage";
		description = "Loads an image.";
		groupFunc = FunctionGroup.Others;

		JIPParamFile p1 = new JIPParamFile("file", false, true);
		p1.setDescription("File name.");
		addParam(p1);
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {			
		String nameFile = getParamValueString("file");
		JIPImage res=null;
		
		if (nameFile.endsWith(".jpg") || nameFile.endsWith(".gif") ||
				nameFile.endsWith(".jpeg")) {
			Image aux = JIPToolkit.getAWTImage(nameFile);
			if (aux==null) throw new JIPException("Some error with file "+nameFile);
			res = JIPToolkit.getColorImage(aux);
		}
		else if (nameFile.endsWith(".jip")) {
			res = JIPToolkit.getSeqFromFile(nameFile).getFrame(0);
		}
		return res;
	}
}
