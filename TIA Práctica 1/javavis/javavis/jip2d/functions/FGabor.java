package javavis.jip2d.functions;

import javavis.base.JIPException;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpFloat;

/**
* Creates an image with a Gabor filter on the basis of orientation and a scale.<BR>
*Firstly, we set the coordinate origin in the center of the image,
*convert the orientation to radians, set the wavelength (2*scale) and recall
*the image with the values of the filter. A gaussian value is assigned in the pixel on
*the basis of sine or cosine.<br><BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: an image because it is a function, although it do not work in it.<br>
*<li>rows: Number of rows of the generated image.<br>
*<li>columns: Number of columns of the generated image.<br>
*<li>scale: scale of the filter (Gaussian sigma)<br>
*<li>orientation: Orientation of the filter (degrees)<br>
*<li>type: Type of wave to compress in the gaussian<br>

<BR> 
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Image with Gabor filter applied.<BR><BR>
*</ul>
*/

public class FGabor extends JIPFunction {
	private static final long serialVersionUID = 4650745710291222027L;

	public FGabor() {
		super();
		name = "FGabor";
		description = "Creates an image with a Gabor filter";
		groupFunc = FunctionGroup.Convolution;

		JIPParamInt filas = new JIPParamInt("rows", false, true);
		filas.setDefault(5);
		filas.setDescription("Filter rows");
		JIPParamInt columnas = new JIPParamInt("columns", false, true);
		columnas.setDefault(5);
		columnas.setDescription("Filter columns");
		JIPParamFloat escala = new JIPParamFloat("scale", false, true);
		escala.setDefault(1.0f);
		escala.setDescription("Filter scale");
		JIPParamFloat orientacion = new JIPParamFloat("orientation", false, true);
		orientacion.setDefault(0.0f);
		orientacion.setDescription("Filter orientation");
		JIPParamBool tipo = new JIPParamBool("type", false, true);
		tipo.setDefault(false);
		tipo.setDescription("Filter type (no marked: GCos, marked: GSin)");

		addParam(filas);
		addParam(columnas);
		addParam(escala);
		addParam(orientacion);
		addParam(tipo);
	}

	public JIPImage processImg(JIPImage plantilla) throws JIPException {
		JIPBmpFloat resultado = null;  

		int filas = getParamValueInt("rows");
		int columnas = getParamValueInt("columns");
		float escala = getParamValueFloat("scale");
		float orientacion = getParamValueFloat("orientation");
		boolean tipo = getParamValueBool("type");

		double[] mapa = new double[columnas * filas];
		orientacion *= Math.PI / 180.0;
		double longitud = 2.0 * escala;

		for (int x = -columnas/2; x <= columnas/2; x++) {
			for (int y = -filas/2; y <= filas/2; y++) {
				if (y+filas/2 >= filas || x+columnas/2 >= columnas) continue;
				float exponencial = (float) Math.exp(- (x * x + y * y) / (2 * escala * escala));
				if (!tipo)
					mapa[(x+columnas/2) * columnas + (y+filas/2)] = Math.cos((2 * Math.PI / longitud)
						* (x * Math.cos(orientacion) + y * Math.sin(orientacion))) * exponencial;
				else
					mapa[(x+columnas/2) * columnas + (y+filas/2)] = Math.sin((2 * Math.PI / longitud) 
						* (x * Math.cos(orientacion) + y * Math.sin(orientacion))) * exponencial;
			}
		}
		resultado = new JIPBmpFloat(columnas, filas);
		resultado.setAllPixels(mapa);

		return resultado;
	}
}
