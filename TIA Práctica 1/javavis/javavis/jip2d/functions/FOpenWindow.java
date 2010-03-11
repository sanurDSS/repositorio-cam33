package javavis.jip2d.functions;

import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.util.NewWindow;

import javax.swing.JFrame;


/**
*It checks image type and afterwards New Window is created
*and shown. If the numFrame has several bands only the first one is shown<BR>
*Applicable to: BIT, BYTE, WORD, COLOR and REAL. 
*<ul><B>Input parameters:</B><BR>
*<li>img1: Input image to show in the window.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Window with the actual image.<BR><BR>
*</ul>
*/
public class FOpenWindow extends JIPFunction {
	private static final long serialVersionUID = 2688851364816071757L;

	public FOpenWindow() {
		super();
		name = "FOpenWindow";
		description = "Open the image in a new window";
		groupFunc = FunctionGroup.Manipulation;
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img instanceof JIPImgBitmap) {
			JFrame frame = new NewWindow(img);

			frame.setSize(img.getWidth() + 5, img.getHeight() + 35);
			frame.setVisible(true);
		}
		else throw new JIPException("OpenWindow can not be applied to this image format");
			
		return img;
	}
}
