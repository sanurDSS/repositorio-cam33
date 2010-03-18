package javavis.jip2d.functions;

import java.util.ArrayList;
import java.util.Iterator;
import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.util.Blob;

/**
*Converts a COLOR image, to a BIT, BYTE, SHORT or FLOAT image.<BR>
*It is only applicable for COLOR type.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>gray: Type of the destination image. (BIT, BYTE, SHORT, FLOAT)<BR><BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>Image equivalent to the COLOR type but in grey scale.<BR><BR>
*</ul>
*/
public class FDetectaCaras extends JIPFunction
{
	private static final long serialVersionUID = -3289373731488957402L;

	public FDetectaCaras()
	{
		super();
		name = "FDetectaCaras";
		description = "Detecta las caras en una imagen..";
		groupFunc = FunctionGroup.Cristian;

		/*JIPParamList p1 = new JIPParamList("gray", false, true);
		String []paux = new String[4];
		paux[0]="BYTE";
		paux[1]="BIT";
		paux[2]="SHORT";
		paux[3]="FLOAT";
		p1.setDefault(paux);
		p1.setDescription("Type of result image");

		addParam(p1);*/
	}

	@SuppressWarnings("unchecked")
	@Override
	public JIPImage processImg(JIPImage img) throws JIPException
	{
		JIPImage resultado;
		
		// Convertimos la imagen RGB a color.
		FRGBToColor frtc = new FRGBToColor();
		frtc.setParamValue("format", "HSB");
		resultado = frtc.processImg(img);
		
		// Binarizamos la imagen: 1 donde haya color de piel; 0 en otro caso.
		FSegmentHSB fsh = new FSegmentHSB();
		fsh.setParamValue("h", 0.03f);
		fsh.setParamValue("herror", 0.15f);
		fsh.setParamValue("s", 0.24f);
		fsh.setParamValue("serror", 0.10f);
		fsh.setParamValue("b", 0.90f);
		fsh.setParamValue("berror", 0.5f);
		resultado = fsh.processImg(resultado);
		
		// Reducimos el ruido de la imagen binaria.
		FClousure fc = new FClousure();
		fc.setParamValue("ee", "Images/ee.txt");
		resultado = fc.processImg(resultado);
		
		// Seleccionamos las zonas candidatas.
		FBlobs fb = new FBlobs();
		fb.processImg(resultado);
		ArrayList<Blob> blobs = (ArrayList<Blob>) fb.getParamValueObj("blobs");
		
		Iterator<Blob> i = blobs.iterator();
		while (i.hasNext())
		{
			Blob blob = i.next();
			blob.calcEverything();
			
			if (blob.xsize * blob.ysize >= 400 &&
					0.5f <= (blob.xsize / (float) blob.ysize) &&
					1.5f >= (blob.xsize / (float) blob.ysize))
			{
				System.out.println("xsize: " + blob.xsize);
				System.out.println("ysize: " + blob.ysize);
				System.out.println("centro_x: " + blob.centro_x);
				System.out.println("centro_y: " + blob.centro_y);
				System.out.println("__________________________________________");
			}
		}
		
		return resultado;
	}
	
	/*public JIPImage processImg(JIPImage img) throws JIPException
	{
		String p1 = getParamValueString("gray");
		ImageType tipo = ImageType.BYTE;
		if (p1.equals("BIT"))
			tipo = ImageType.BIT;
		else if (p1.equals("BYTE"))
			tipo = ImageType.BYTE;
		else if (p1.equals("SHORT"))
			tipo = ImageType.SHORT;
		else if (p1.equals("FLOAT"))
			tipo = ImageType.FLOAT;

		JIPImgBitmap res = null;
		if (img.getType() == ImageType.COLOR) {
			int totalPix = img.getWidth()*img.getHeight();
			JIPBmpColor imgCol = (JIPBmpColor)img;
			byte[] red = imgCol.getAllPixelsByteRed();
			byte[] green = imgCol.getAllPixelsByteGreen();
			byte[] blue = imgCol.getAllPixelsByteBlue();
			double[] gray = new double[totalPix];
			double max=0;

			switch (tipo) {
				case BYTE: max=255; break;
				case FLOAT:
				case BIT: max=1; break;
				case SHORT: max=65535; break;
			}
			for (int i = 0; i < totalPix; i++)
			  gray[i] = max*Math.round(0.299 * (red[i]&0xFF) + 0.587 * (green[i]&0xFF) + 0.114 * 
					  (blue[i]&0xFF))/255.0;

			res = (JIPImgBitmap)JIPImage.newImage(img.getWidth(), img.getHeight(), tipo);
			res.setAllPixels(gray);
		}
		else
			throw new JIPException("Function ColorToGray only applied to COLOR images");
		
		return res;
	}*/
}