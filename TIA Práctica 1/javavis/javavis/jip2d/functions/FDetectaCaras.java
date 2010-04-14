package javavis.jip2d.functions;

import java.util.ArrayList;
import java.util.Iterator;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamList;
import javavis.base.parameter.JIPParamObject;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.geometrics.JIPGeomPoly;
import javavis.jip2d.util.Blob;

/**
 * Detecta las caras en una imagen y genera una imagen geométrica.
 * @author cristian
 */
public class FDetectaCaras extends JIPFunction
{
	private static final long serialVersionUID = -3289373731488957402L;
	
	public FDetectaCaras()
	{
		super();
		name = "FDetectaCaras";
		description = "Detecta las caras en una imagen y genera una imagen geométrica.";
		groupFunc = FunctionGroup.Cristian;

		// TODO: Cosas pendientes en la fase 1.
		// Añadir parámetros para elegir la localización de la foto: interior, exterior, soleado, nublado, oscuro, iluminado, ...
		// Añadir parámetros para filtrar los blobs: tamaño, aspect ratio, porcentaje de valores a 1.
		// Establecer otros elementos estructurantes por defecto.
		
		// Parámetros para la segmentación HSB.
		JIPParamFloat p1 = new JIPParamFloat("h", false, true);
		p1.setDescription("Hue value");
		p1.setDefault(0.025f);
		addParam(p1);
		JIPParamFloat p2 = new JIPParamFloat("herror", false, true);
		p2.setDescription("Margin of hue error");
		p2.setDefault(0.055f);
		addParam(p2);
		JIPParamFloat p3 = new JIPParamFloat("s", false, true);
		p3.setDescription("Saturation value");
		p3.setDefault(0.24f);
		addParam(p3);
		JIPParamFloat p4 = new JIPParamFloat("serror", false, true);
		p4.setDescription("Margin of saturation error");
		p4.setDefault(0.10f);
		addParam(p4);
		JIPParamFloat p5 = new JIPParamFloat("b", false, true);
		p5.setDescription("Brightness value");
		p5.setDefault(0.90f);
		addParam(p5);
		JIPParamFloat p6 = new JIPParamFloat("berror", false, true);
		p6.setDescription("Margin of brightness error");
		p6.setDefault(0.5f);
		addParam(p6);
		
		// Parámetros para el cierre morfológico.
		JIPParamFile p7 = new JIPParamFile("ee", false, true);
		p7.setDefault("Images/ee.txt");
		p7.setDescription("Estructurant Element");
		addParam(p7);
		
		// Parámetro para decidir hasta dónde ejecutar.
		JIPParamList p8 = new JIPParamList("fase", false, true);
		String []p8aux = new String[4];
		p8aux[0] = "Convertir a HSB";
		p8aux[1] = "Segmentar HSB";
		p8aux[2] = "Cierre morfológico";
		p8aux[3] = "Completo";
		p8.setDefault(p8aux);
		p8.setDescription("Hasta dónde ejecutar el algoritmo");
		addParam(p8);
		
		// Parámetro de salida con los blobs.
		JIPParamObject o1 = new JIPParamObject("blobs", false, false);
		addParam(o1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JIPImage processImg(JIPImage img) throws JIPException
	{
		JIPGeomPoly resultado = new JIPGeomPoly(img.getWidth(), img.getHeight());
		JIPImage imgAux = img.clone();
		
		// Convertimos la imagen RGB a color.
		FRGBToColor frtc = new FRGBToColor();
		frtc.setParamValue("format", "HSB");
		imgAux = frtc.processImg(imgAux);
		
		if (getParamValueString("fase").equals("Convertir a HSB"))
			return imgAux;
		
		// Binarizamos la imagen: 1 donde haya color de piel; 0 en otro caso.
		FSegmentHSB fsh = new FSegmentHSB();
		fsh.setParamValue("h", getParamValueFloat("h"));
		fsh.setParamValue("herror", getParamValueFloat("herror"));
		fsh.setParamValue("s", getParamValueFloat("s"));
		fsh.setParamValue("serror", getParamValueFloat("serror"));
		fsh.setParamValue("b", getParamValueFloat("b"));
		fsh.setParamValue("berror", getParamValueFloat("berror"));
		imgAux = fsh.processImg(imgAux);
		
		if (getParamValueString("fase").equals("Segmentar HSB"))
			return imgAux;
		
		// Reducimos el ruido de la imagen binaria.
		FClousure fc = new FClousure();
		fc.setParamValue("ee", getParamValueString("ee"));
		imgAux = fc.processImg(imgAux);
		
		if (getParamValueString("fase").equals("Cierre morfológico"))
			return imgAux;
		
		// Añadimos las zonas candidatas a la imagen geométrica.
		ArrayList<Blob> caras = new ArrayList<Blob>();
		FBlobs fb = new FBlobs();
		fb.processImg(imgAux);
		ArrayList<Blob> blobs = (ArrayList<Blob>) fb.getParamValueObj("blobs");
		Iterator<Blob> i = blobs.iterator();
		while (i.hasNext())
		{
			Blob blob = i.next();
			blob.calcEverything();
			
			// Se comprueba que tenga un tamaño y relación adecuada.
			if (blob.xsize * blob.ysize >= 1600 &&
					0.50f <= (blob.xsize / (float) blob.ysize) &&
					1.40f >= (blob.xsize / (float) blob.ysize))
			{
				// Se comprueba que hay un alto porcentaje de valores a 1.
				int pixelesBlancos = 0;
				int pixelesTotales = blob.xsize * blob.ysize;
				JIPBmpBit imgBit = (JIPBmpBit) imgAux;
				for (int j = blob.minx; j <= blob.maxx; j++)
					for (int k = blob.miny; k <= blob.maxy; k++)
					{
						if (imgBit.getPixelBool(j, k))
							pixelesBlancos++;
					}
				if (pixelesBlancos/(float) pixelesTotales > 0.4f)
				{
					// Llegados a este punto se añade el blob a la lista de caras y se dibuja el rectángulo en la imagen geométrica.
					caras.add(blob);
					ArrayList<Integer> rectangulo = new ArrayList<Integer>();
					rectangulo.add(blob.minx);
					rectangulo.add(blob.miny);
					rectangulo.add(blob.maxx);
					rectangulo.add(blob.miny);
					rectangulo.add(blob.maxx);
					rectangulo.add(blob.maxy);
					rectangulo.add(blob.minx);
					rectangulo.add(blob.maxy);
					resultado.addPoly(rectangulo);
				}
			}
		}
		setParamValue("blobs", caras);
		
		return resultado;
	}
}