package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamObject;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.geometrics.JIPGeomPoly;
import javavis.jip2d.util.Circumference;


/**
*It calculates all the possible circumferences presents in the image. Every pixel votes for 
*a circumference to which it can belong, then we use the circumference which 
*its number of votes are maximum.
* We show circumferences as a tPOLY image. A result parameter containing the 
* number of circumferences is returned. Another result parameter with a vector
* of Circunferences is also returned.<br>
<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image in the function with BIT type.<br>
*<li>thres: Minimal number of votes to accept the circumference.<br>
*<li>Rmin: Minimum radius of the circumference.<br>
*<li>Rmax: Maximum radius of the circumference.<br>

<BR> 
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>tPOLY image which has found circumferences.<BR><BR>
*</ul>
*/
public class FHoughCirc extends JIPFunction {
	private static final long serialVersionUID = -3844283432270245143L;

	public FHoughCirc() {
		super();
		name = "FHoughCirc";
		description = "Obtains the number of circumferences in a input image.";
		groupFunc = FunctionGroup.Geometry;
		JIPParamInt p7 = new JIPParamInt("thres", false, true);
		p7.setDefault(30);
		p7.setDescription("Minimum percentage of votes");
		JIPParamInt p8 = new JIPParamInt("Rmin", false, true);
		p8.setDefault(10);
		p8.setDescription("Minimum radius");
		JIPParamInt p9 = new JIPParamInt("Rmax", false, true);
		p9.setDefault(80);
		p9.setDescription("Maximum radius");

		addParam(p7);
		addParam(p8);
		addParam(p9);
		
		// Return parameters
		JIPParamInt r1 = new JIPParamInt("ncirc", false, false);
		r1.setDescription("Number of circumferences found");
		JIPParamObject r2 = new JIPParamObject("circum", false, false);
		r2.setDescription("Circumferences found");
		
		addParam(r1);
		addParam(r2);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() != ImageType.BIT) 
			throw new JIPException("HoughCirc can not be applied to this image format");

		JIPBmpBit imgBit = (JIPBmpBit)img;
		int w = img.getWidth();
		int h = img.getHeight();
		int umbral = getParamValueInt("thres");
		int rmin = getParamValueInt("Rmin");
		int rmax = getParamValueInt("Rmax");
		
		//Creacion del cubo que usaremos para almacenar el numero de votos.
		int a1 = w-1;
		int a2 = h-1;
		int a3 = rmax - rmin;
		int tabla[][][] = new int[a1][a2][a3];

		//Inicializo la tabla (cubo) de votos a cero.
		for (int cont1 = 0; cont1 < a1; cont1++) 
			for (int cont2 = 0; cont2 < a2; cont2++) 
				for (int cont3 = 0; cont3 < a3; cont3++) 
					tabla[cont1][cont2][cont3] = 0;

		// Para cada pixel de la imagen
		for (int i = 0; i < w; i++) 
			for (int j = 0; j < h; j++) 
				if (imgBit.getPixelBool(i, j))
					for (int xc = 0; xc < w-1; xc++)
						for (int yc = 0; yc < h-1; yc++) {
							int r = Circumference.calculatesRadius(xc, yc, i, j);
							//Se comprueba que el value del radio calculado no exceda de las dimensiones del cubo
							if (r < rmax && r > rmin)
								tabla[xc][yc][r - rmin]++;
						}
		/* Preparamos el Vector que contendra los vectores de puntos para dibujar la circunferencia, y
		otro Vector para almacenar las circunferencias obtenidas antes de eliminar las parecidas.*/
		ArrayList<Circumference> resultTEMP = new ArrayList<Circumference>();

		// Las dimensiones de la tabla son a1, a2 y a3
		boolean flag;
		int ref = 0;
		int circ_f=0; 
		for (int i = 0; i < a1; i++) {
			for (int j = 0; j < a2; j++) {
				for (int k = 0; k < a3; k++) {
					flag = false;
					/* Comprobamos si esa circunferencia cumple con al menos el umbral% de pixeles pertenecientes
					    a su perimetro.*/
					if (checkCirc(tabla[i][j][k], umbral, k + rmin)) {
						ref = tabla[i][j][k];
						/* Si la circunferencia que se esta analizando cumple con el umbral minimo, debemos examinar
						    si tiene vecinos que sean iguales o mejores que ella. Para ello miramos en sus 26 vecinos 
						    (por ser un cubo)*/
						if (((i - 1) >= 0) && ((j - 1) >= 0)
							&& (tabla[i - 1][j - 1][k] >= ref))
							flag = true;
						if (((i - 1) >= 0) && (tabla[i - 1][j][k] >= ref))
							flag = true;
						if (((i - 1) >= 0) && ((j + 1) < a2) && (tabla[i - 1][j + 1][k] >= ref))
							flag = true;
						if (((j - 1) >= 0) && (tabla[i][j - 1][k] >= ref))
							flag = true;
						if (((j + 1) < a2) && (tabla[i][j + 1][k] >= ref))
							flag = true;
						if (((i + 1) < a1) && ((j - 1) >= 0) && (tabla[i + 1][j - 1][k] >= ref))
							flag = true;
						if (((i + 1) < a1) && (tabla[i + 1][j][k] >= ref))
							flag = true;
						if (((i + 1) < a1) && ((j + 1) < a2) && (tabla[i + 1][j + 1][k] >= ref))
							flag = true;

						//Plano paralelo
						if (((i - 1) >= 0) && ((j - 1) >= 0) && ((k - 1) >= 0)
								&& (tabla[i - 1][j - 1][k - 1] >= ref))
							flag = true;
						if (((i - 1) >= 0) && ((k - 1) >= 0)
								&& tabla[i - 1][j][k - 1] >= ref)
							flag = true;
						if (((i - 1) >= 0) && ((j + 1) < a2) && ((k - 1) >= 0)
								&& (tabla[i - 1][j + 1][k - 1] >= ref))
							flag = true;
						if (((j - 1) >= 0) && ((k - 1) >= 0)
								&& (tabla[i][j - 1][k - 1] >= ref))
							flag = true;
						if (((j + 1) < a2) && ((k - 1) >= 0)
								&& (tabla[i][j + 1][k - 1] >= ref))
							flag = true;
						if (((i + 1) < a1) && ((j - 1) >= 0) && ((k - 1) >= 0)
								&& (tabla[i + 1][j - 1][k - 1] >= ref))
							flag = true;
						if (((i + 1) < a1) && ((k - 1) >= 0)
								&& (tabla[i + 1][j][k - 1] >= ref))
							flag = true;
						if (((i + 1) < a1) && ((j + 1) < a2) && ((k - 1) >= 0)
								&& (tabla[i + 1][j + 1][k - 1] >= ref))
							flag = true;
						if (((k - 1) >= 0) && (tabla[i][j][k - 1] >= ref))
							flag = true;

						//Plano paralelo
						if (((i - 1) >= 0) && ((j - 1) >= 0) && ((k + 1) < a3)
								&& (tabla[i - 1][j - 1][k + 1] >= ref))
							flag = true;
						if (((i - 1) >= 0) && ((k + 1) < a3)
								&& tabla[i - 1][j][k + 1] >= ref)
							flag = true;
						if (((i - 1) >= 0) && ((j + 1) < a2) && ((k + 1) < a3)
								&& (tabla[i - 1][j + 1][k + 1] >= ref))
							flag = true;
						if (((j - 1) >= 0) && ((k + 1) < a3)
								&& (tabla[i][j - 1][k + 1] >= ref))
							flag = true;
						if (((j + 1) < a2) && ((k + 1) < a3)
								&& (tabla[i][j + 1][k + 1] >= ref))
							flag = true;
						if (((i + 1) < a1) && ((j - 1) >= 0) && ((k + 1) < a3)
								&& (tabla[i + 1][j - 1][k + 1] >= ref))
							flag = true;
						if (((i + 1) < a1) && ((k + 1) < a3)
								&& (tabla[i + 1][j][k + 1] >= ref))
							flag = true;
						if (((i + 1) < a1) && ((j + 1) < a2) && ((k + 1) < a3)
								&& (tabla[i + 1][j + 1][k + 1] >= ref))
							flag = true;
						if (((k + 1) < a3) && (tabla[i][j][k + 1] >= ref))
							flag = true;

						/* Si no tiene ningun vecino mejor, guardamos esa circunferencia en nuestro Vector
						    temporal de circunferencias resultado.*/
						if (!flag) {
							circ_f++;
							resultTEMP.add(new Circumference(i, j, k + rmin));
						}
					} 
				}
			}
		}

		/* Para cada circunferencia, se compara con las demas, y si no tiene parecidas se guarda en el
		Vector de resultados. Si tiene parecidas se mira si ya ha sido almacenada alguna parecida, y si no
		ha sido asi, se guarda esa misma circunferencia y se marca como guardada.
		Cada vez que se guarda una circunferencia en el vector de resultados se vota en su casilla del array
		de votos correspondiente.*/
		/* Lo que deberia hacer es quedarme con la mas grande de las circunscritas. */
		int mayor = 0;
		ArrayList<Circumference> vecAux = new ArrayList<Circumference>();
		int circFfinal=0;
		for (int i = 0; i < circ_f; i++) {
			mayor = i;
			flag = false;
			for (int j = 0; j < circ_f; j++) {
				if (cercanas(resultTEMP.get(mayor), resultTEMP.get(j), rmin)
					&& (resultTEMP.get(j)).radio > (resultTEMP.get(mayor)).radio) {
					mayor = j;
					flag = true;
				}
			}
			if (!flag) {
				vecAux.add(resultTEMP.get(mayor));
				circFfinal++;
			}
		}
		ArrayList<ArrayList<Integer>> puntos_d_circ = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < circFfinal; i++)
			puntos_d_circ.add(Circumference.getPoints(vecAux.get(i)));
		
		setParamValue("ncirc", circFfinal);
		setParamValue("circum", vecAux);

		JIPGeomPoly res = new JIPGeomPoly(w,h);
		res.setData(puntos_d_circ);
		return res;
	}

	/**
	 *<B>Description</B><BR>
	 *Function which checks if two circumferences are quite similar. It is checked from  
	 *their center distance and their radius. If distance is less than double of minimum radius
	 *then radius are checked.<br> If they are similar then we have similar circumferences so
	 *it returns 'true', else return 'false'.<br>   
	 *@param circ1 First circumference to check with second.
	 *@param circ2 Second circumference to chck with first.
	 *@param rMIN Minimal radius.
	 *@return Returns a boolean which tells us if two circumferences are nearest.
	 */
	public boolean cercanas(Circumference circ1, Circumference circ2, int rMIN) {
		int c = circ1.centroX - circ2.centroX;
		int d = circ1.centroY - circ2.centroY;

		if (Math.sqrt(c * c + d * d) < 2 * rMIN) {
			if (Math.abs(circ1.radio-circ2.radio) < rMIN) return true;
			else return false;
		} 
		else return false;
	}

	/**
	 * <B>Description</B><BR>
	 * Checks if the number of received votes (pixels with value '1'), are at least the same 
	 * the specified percentage in the circumference perimeter.
	 * @param num_votos Number of votes of the studied circumference.
	 * @param porcentaje Minimal percentage of pixels which value equal to '1' that should has from every posible '2 * Pi * radius.
	 * @param radio Radius of the circumference.
	 * @return Return a boolean value. 'True' if the upper number is more than required, or 'false' in another case.
	 */
	public boolean checkCirc(int num_votos, int porcentaje, int radio) {
		int perimetro = (int) (2 * Math.PI * radio);
		if (num_votos >= perimetro * (porcentaje / 100.0))
			return true;
		else
			return false;
	}
}
