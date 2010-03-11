package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamObject;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.geometrics.JIPGeomPoint;
import javavis.jip2d.util.Blob;


/**
*This function receives a binary image and returns a point image which 
*represents the centroids of the obtained blobs. It returns a parameter with the
*blob objects obtained. Only applied to the first band.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
<BR> 
*</ul>

*<ul><B>Output parameters:</B><BR>
*<li>Point image which points indicate centroids of the Blobs found.<BR><BR>
*</ul>
*/
public class FBlobs extends JIPFunction {
	private static final long serialVersionUID = -803595271042342803L;

	public FBlobs() {
		super();
		name = "FBlobs";
		description = "Obtains the blobs from an image.";
		groupFunc = FunctionGroup.Others;
		
		JIPParamObject o1 = new JIPParamObject ("blobs", false, false);
		addParam(o1);
	}
	
	public JIPImage processImg(JIPImage src) throws JIPException {
		if (src.getType() != ImageType.BIT) 
			throw new JIPException("Blobs can only be applied to BIT images");
		int code;
		JIPBmpBit srcbit = (JIPBmpBit)src;
		
		int ancho = src.getWidth();
		int alto = src.getHeight();
		int[][] regiones = new int[ancho][alto];

		for (int x = 0; x < ancho; x++) {
			regiones[x][0] = 0;
			srcbit.setPixel(x, 0, 0);
		}
		for (int y = 0; y < alto; y++) {
			regiones[0][y] = 0;
			srcbit.setPixel(0, y, 0);
		}

		ArrayList<Blob> lista_blobs = new ArrayList<Blob>();
		Blob blob;

		for (int y = 1; y < alto; y++) {
			for (int x = 1; x < ancho; x++) {
				// Calculates the code 
				code = (int)(srcbit.getPixel(x-1, y-1) + 2*srcbit.getPixel(x, y-1) + 
						4*srcbit.getPixel(x-1, y) + 8*srcbit.getPixel(x, y));
				if (code < 8) 
					regiones[x][y] = 0;
				// New blob
				else if (code == 8 || code == 9) {
					blob = new Blob();
					blob.lista_x.add(x);
					blob.lista_y.add(y);
					lista_blobs.add(blob);
					regiones[x][y] = lista_blobs.size();
					// Assigns the point to a previous created blob
				} else if (code == 10 || code == 11 || code == 15) {
					blob = lista_blobs.get(regiones[x][y - 1] - 1);
					blob.lista_x.add(x);
					blob.lista_y.add(y);
					regiones[x][y] = regiones[x][y - 1];
					// Assigns the point to a previous created blob
				} else if (code == 12 || code == 13) {
					blob = lista_blobs.get(regiones[x - 1][y] - 1);
					blob.lista_x.add(x);
					blob.lista_y.add(y);
					regiones[x][y] = regiones[x - 1][y];
					// Merge the two correlatives blobs
				} else if (code == 14) {
					if (regiones[x][y - 1] != regiones[x - 1][y])
						merge(regiones[x][y - 1], regiones[x - 1][y], regiones, lista_blobs);
					blob = lista_blobs.get(regiones[x][y - 1] - 1);
					blob.lista_x.add(x);
					blob.lista_y.add(y);
					regiones[x][y] = regiones[x][y - 1];
				}
			}
		}

		lista_blobs.trimToSize();
				
		JIPGeomPoint result = new JIPGeomPoint(ancho, alto);
		ArrayList<Blob> lista_aux = new ArrayList<Blob>();
		
		for (Blob b : lista_blobs) {
			b.calcCentroid();
			if (b.length()>0 && b.valido) {
				result.addPoint(b.centro_x, b.centro_y);
				lista_aux.add(b);
			}
		}
		setParamValue("blobs", lista_aux);
		return result;
	}

	/**
	* Merge two blobs.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>blob1: Index of the first blob<BR>
	*<li>blob2: Index of the second blob<BR>
	<BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>NONE<BR>
	<BR>
	*</ul>
	*/
	private void merge(int blob1, int blob2, int[][]regiones, ArrayList<Blob> lista_blobs) {
		Blob b1 = lista_blobs.get(blob1 - 1);
		Blob b2 = lista_blobs.get(blob2 - 1);
		// Assigns the blob number of the first blob to the points in the second one
		for (int i=0; i<b2.length(); i++)
			regiones[b2.lista_x.get(i)][b2.lista_y.get(i)] = blob1;

		// Add the coordinates from the second blob to the first one
		b1.lista_x.addAll(b2.lista_x);
		b1.lista_y.addAll(b2.lista_y);
		
		// Delete the second blob
		b2.lista_x.clear();
		b2.lista_y.clear();
	}
}
