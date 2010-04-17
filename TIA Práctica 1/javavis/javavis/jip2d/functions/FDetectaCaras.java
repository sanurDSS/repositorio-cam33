package javavis.jip2d.functions;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamInt;
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
 * <br />
 * <h2>Parámetros de entrada</h2>
 * <ul>
 * <li><b>imagen:</b> Imagen sobre la que se aplicará el algoritmo para detectar las caras.</li>
 * <li><b>hsb:</b> Color HSB de la piel y margen de error permitido para el tipo de piel que se quiere detectar.</li>
 * <li><b>cierre morfológico:</b> Referencia al fichero con el elemento estructurante para el cierre morfológico.</li>
 * <li><b>tamaño:</b> Tamaño mínimo que tienen las caras de la imagen.</li>
 * <li><b>aspect ratio:</b> Relación alto/ancho que pueden tener las caras.</li>
 * <li><b>porcentaje color de piel:</b> Indica el porcentaje que debe tener la cara con color de piel (para tratar bigotes, barba y otros aspectos).</li>
 * </ul>
 * 
 * <h2>Parámetros de salida</h2>
 * <ul>
 * <li><b>blobs:</b> Se devuelve una lista de Blobs con la información referente a las caras detectadas sobre la imagen.</li>
 * <li><b>retorno:</b> Se devuelve una imagen geométrica con rectángulos en las posiciones donde se han detectado caras.</li>
 * </ul>
 * @author Cristian Aguilera Martínez
 */
public class FDetectaCaras extends JIPFunction
{
	private static final long serialVersionUID = -3289373731488957402L;
	
	public FDetectaCaras()
	{
		super();
		name = "FDetectaCaras";
		description = "Detecta las caras en una imagen y genera una imagen geométrica.";
		groupFunc = FunctionGroup.Applic;
		
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
		p7.setDescription("Estructurant Element (para cierre morfológico)");
		addParam(p7);
		
		// Parámetros para el filtrado y selección de blobs: tamaño, aspect ratio, porcentaje de valores a 1.
		JIPParamInt p8 = new JIPParamInt("tamano", false, true);
		p8.setDefault(2000);
		p8.setDescription("Mínimo tamaño del blob en píxeles");
		addParam(p8);
		JIPParamFloat p9 = new JIPParamFloat("margen", false, true);
		p9.setDefault(0.50f);
		p9.setDescription("Margen permitido a la relación entre ancho y alto del blob (1 - margen <= ancho/alto <= 1 + margen)");
		addParam(p9);
		JIPParamInt p10 = new JIPParamInt("porcentaje", false, true);
		p10.setDefault(40);
		p10.setDescription("Porcentaje de píxeles con valor 1 en el blob");
		addParam(p10);
		
		// Parámetro para decidir hasta dónde ejecutar.
		JIPParamList p11 = new JIPParamList("fase", false, true);
		String []p11aux = new String[4];
		p11aux[0] = "Completo";
		p11aux[1] = "Convertir a HSB";
		p11aux[2] = "Segmentar HSB";
		p11aux[3] = "Cierre morfológico";
		p11.setDefault(p11aux);
		p11.setDescription("Hasta dónde ejecutar el algoritmo");
		addParam(p11);
		
		// Parámetro de salida con los blobs.
		JIPParamObject o1 = new JIPParamObject("blobs", false, false);
		addParam(o1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JIPImage processImg(JIPImage img) throws JIPException
	{
		JIPGeomPoly resultado = new JIPGeomPoly(img.getWidth(), img.getHeight());
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(new Color(255, 0, 0));
		colors.add(new Color(255, 0, 0));
		colors.add(new Color(255, 0, 0));
		colors.add(new Color(255, 0, 0));
		resultado.setColors(colors);
		JIPImage imgAux = img.clone();
		
		// Convertimos la imagen RGB a color.
		FRGBToColor fRGBToColor = new FRGBToColor();
		fRGBToColor.setParamValue("format", "HSB");
		imgAux = fRGBToColor.processImg(imgAux);
		
		if (getParamValueString("fase").equals("Convertir a HSB"))
			return imgAux;
		
		// Binarizamos la imagen: 1 donde haya color de piel; 0 en otro caso.
		FSegmentHSB fSegmentHSB = new FSegmentHSB();
		fSegmentHSB.setParamValue("h", getParamValueFloat("h"));
		fSegmentHSB.setParamValue("herror", getParamValueFloat("herror"));
		fSegmentHSB.setParamValue("s", getParamValueFloat("s"));
		fSegmentHSB.setParamValue("serror", getParamValueFloat("serror"));
		fSegmentHSB.setParamValue("b", getParamValueFloat("b"));
		fSegmentHSB.setParamValue("berror", getParamValueFloat("berror"));
		imgAux = fSegmentHSB.processImg(imgAux);
		
		if (getParamValueString("fase").equals("Segmentar HSB"))
			return imgAux;
		
		// Reducimos el ruido de la imagen binaria.
		FClousure fClousure = new FClousure();
		fClousure.setParamValue("ee", getParamValueString("ee"));
		imgAux = fClousure.processImg(imgAux);
		
		if (getParamValueString("fase").equals("Cierre morfológico"))
			return imgAux;
		
		// Añadimos las zonas candidatas a la imagen geométrica.
		ArrayList<Blob> caras = new ArrayList<Blob>();
		setParamValue("blobs", caras);
		FBlobs fBlobs = new FBlobs();
		fBlobs.processImg(imgAux);
		ArrayList<Blob> blobs = (ArrayList<Blob>) fBlobs.getParamValueObj("blobs");
		Iterator<Blob> i = blobs.iterator();
		while (i.hasNext())
		{
			Blob blob = i.next();
			blob.calcEverything();
			
			// Se comprueba que tenga un tamaño y relación adecuada.
			if (blob.xsize * blob.ysize >= getParamValueInt("tamano") &&
					1 - getParamValueFloat("margen") <= (blob.xsize / (float) blob.ysize) &&
					1 + getParamValueFloat("margen") >= (blob.xsize / (float) blob.ysize))
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
				if (pixelesBlancos/(float) pixelesTotales > getParamValueInt("porcentaje")/100.0f)
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
		
		return resultado;
	}
}