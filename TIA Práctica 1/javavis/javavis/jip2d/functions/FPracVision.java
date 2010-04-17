package javavis.jip2d.functions;

import java.awt.Color;
import java.util.ArrayList;

import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamFloat;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.base.parameter.JIPParamObject;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPBmpByte;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.geometrics.JIPGeomPoly;
import javavis.jip2d.util.Blob;

/**
 * Detecta las caras en una secuencia de imágenes.
 * <br />
 * <h2>Parámetros de entrada</h2>
 * <ul>
 * <li><b>secuencia:</b> Secuencia de imágenes sobre la que se ejecuta la función.</li>
 * <li><b>umbral:</b> Valor mínimo que debe superar el nivel de "movimiento" para considerarse un movimiento.</li>
 * <li><b>cierre morfológico:</b> Referencia al fichero con el elemento estructurante para el cierre morfológico, para unir zonas cercanas de movimiento.</li>
 * </ul>
 * 
 * <h2>Parámetros de salida</h2>
 * <ul>
 * <li><b>caras:</b> Se devuelve una matriz dinámica en la que cada fila representa una secuencia de caras de una misma persona que ha pasado por la escena.</li>
 * <li><b>retorno:</b> Se devuelve una secuencia con la secuencia original e, intercaladamente, las imágenes geométricas con las caras detectadas durante el movimiento producido entre cada par de fotogramas.</li>
 * </ul>
 * @author Cristian Aguilera Martínez
 */
public class FPracVision extends JIPFunction
{ 
	private static final long serialVersionUID = -3221456052818528739L;

	public FPracVision()
	{
		super();
		name = "FPracVision";
		description = "Detecta las caras en una secuencia de imágenes.";
		groupFunc = FunctionGroup.Applic;
		
		// Parámetros para binarizar la imagen de grises.
		JIPParamInt p1 = new JIPParamInt("u1", false, true);
		p1.setDefault(20);
		p1.setDescription("Umbral para determinar si un byte es 0 o 1 (si se supera el umbral, es 1)");
		addParam(p1);
		
		// Parámetros para el cierre morfológico.
		JIPParamFile p2 = new JIPParamFile("ee", false, true);
		p2.setDefault("Images/ee.txt");
		p2.setDescription("Estructurant Element (para cierre morfológico)");
		addParam(p2);
		
		// Parámetros para el cierre morfológico.
		JIPParamFloat p3 = new JIPParamFloat("distancia", false, true);
		p3.setDefault(45.0f);
		p3.setDescription("Distancia límite entre un mismo blob en dos fotogramas consecutivos");
		addParam(p3);
		
		JIPParamList p11 = new JIPParamList("retorno", false, true);
		String []p11aux = new String[6];
		p11aux[0] = "Completo";
		p11aux[1] = "Resta de imágenes";
		p11aux[2] = "Segmentación de movimiento";
		p11aux[3] = "Cierre morfológico";
		p11aux[4] = "Fragmentos con movimiento detectado";
		p11aux[5] = "Secuencia de las personas detectadas";
		p11.setDefault(p11aux);
		p11.setDescription("Elige qué devuelve la función");
		addParam(p11);
		
		JIPParamObject o1 = new JIPParamObject("caras", false, false);
		addParam(o1);
	}
	
	@Override
	public JIPImage processImg(JIPImage img) throws JIPException
	{
		throw new JIPException("FPracVision debe aplicarse a la secuencia completa.");
	}
	
	@SuppressWarnings("unchecked")
	@Override	
	public JIPSequence processSeq(JIPSequence seq) throws JIPException
	{	
		int width = seq.getFrame(0).getWidth();
		int height = seq.getFrame(0).getHeight();
		
		// Construimos la secuencia con la segmentación de movimiento entre cada par de fotogramas.
		JIPSequence movimientoResta = new JIPSequence();
		JIPSequence movimientoSegmentacion = new JIPSequence();
		JIPSequence movimientoCierre = new JIPSequence();
		JIPSequence movimientoBlobs = new JIPSequence();
		
		// Funciones de JavaVis que se van a utilizar.
		FDetectaCaras fDetectaCaras = new FDetectaCaras();
		FColorToGray fColorToGray = new FColorToGray();
		FBinarize fBinarize = new FBinarize();
		fBinarize.setParamValue("u1", getParamValueInt("u1"));
		FClousure fClousure = new FClousure();
		fClousure.setParamValue("ee", getParamValueString("ee"));
		FBlobs fBlobs = new FBlobs();
		
		// Construimos la lista que almacena los blobs de caras encontrados entre cada par de fotogramas.
		ArrayList<ArrayList<Blob>> fotogramas = new ArrayList<ArrayList<Blob>>(seq.getNumFrames());
		
		// Recorremos la secuencia de imágenes.	
		JIPImage imagenAnterior = null;
		JIPBmpByte imagenAnteriorByte = null;
		for (JIPImage imagenActual : seq.getFrames())
		{
			JIPBmpByte imagenActualByte = (JIPBmpByte) fColorToGray.processImg(imagenActual); 
			if (imagenAnterior != null)
			{
				// Convertimos las imágenes a gris y restamos los dos fotogramas (valor absoluto).
				JIPBmpByte fotogramaByte = new JIPBmpByte(width, height);
				byte [] pixeles1 = imagenActualByte.getAllPixelsByte();
				byte [] pixeles2 = imagenAnteriorByte.getAllPixelsByte();
				byte [] pixeles = new byte[width * height];
				for (int i = 0; i < pixeles1.length; i++)
				{
					pixeles[i] = (byte) Math.abs(pixeles1[i] - pixeles2[i]);
				}
				fotogramaByte.setAllPixelsByte(pixeles);
				movimientoResta.addFrame(fotogramaByte);
				
				// Segmentamos la zona de movimiento.
				JIPBmpBit fotogramaBit = (JIPBmpBit) fBinarize.processImg(fotogramaByte);
				movimientoSegmentacion.addFrame(fotogramaBit);
				
				// Aplicamos el cierre morfológico para reducir el ruido y unir áreas.
				fotogramaBit = (JIPBmpBit) fClousure.processImg(fotogramaBit);
				movimientoCierre.addFrame(fotogramaBit);
						
				// Extraemos las áreas donde se produce movimiento. 
				fBlobs.processImg(fotogramaBit);
				ArrayList<Blob> blobsMovimiento = (ArrayList<Blob>) fBlobs.getParamValueObj("blobs");
				ArrayList<Blob> carasMovimiento = new ArrayList<Blob>();
				for (Blob i : blobsMovimiento)
				{
					i.calcEverything();
					
					// Filtramos las zonas de movimiento que no tengan un tamaño suficiente en el que pueda haber una cara
					// (depende del valor que tenga el tamaño en FDetectaCaras).
					if (i.xsize * i.ysize > fDetectaCaras.getParamValueInt("tamano"))
					{
						// Recortamos el fragmento de movimiento sobre la imagen original.
						JIPBmpColor imagenMovimiento = recortarFragmento((JIPBmpColor) imagenActual, i);
						
						// Detectamos las caras en ese trozo pequeño.
						fDetectaCaras.processImg(imagenMovimiento);
						ArrayList<Blob> carasMovimientoAux = (ArrayList<Blob>) fDetectaCaras.getParamValueObj("blobs");
						
						// Actualizamos todos los blobs encontrados a su localización absoluta y los añadimos a la lista de caras que han sido detectadas durante el movimiento.
						for (Blob j : carasMovimientoAux)
						{
							j.centro_x += i.minx;
							j.centro_y += i.miny;
							j.maxx += i.minx;
							j.minx += i.minx;
							j.maxy += i.miny;
							j.miny += i.miny;
							carasMovimiento.add(j);
						}
						
						movimientoBlobs.addFrame(imagenMovimiento);
					}
				}
				fotogramas.add(carasMovimiento);							
				movimientoBlobs.addFrame(fotogramaBit);
			}
			imagenAnterior = imagenActual;
			imagenAnteriorByte = imagenActualByte;
		}
		
		// Generamos una matriz dinámica de Blobs en la que cada fila representa el movimiento de una persona diferente a través de la escena.
		// Posteriormente, se generará a partir de esta matriz otra matriz de JIPImage con los fragmentos de las imágenes en vez de los Blobs.
		ArrayList<ArrayList<Blob>> blobsCaras = new ArrayList<ArrayList<Blob>>();
		ArrayList<Blob> fotogramaAnterior = null;
		int contador = 1;
		for (ArrayList<Blob> fotogramaActual : fotogramas)
		{
			if (fotogramaAnterior == null || fotogramaAnterior.size() == 0)
			{
				// Si es el primer fotograma o en el fotograma anterior no se habían detectado caras, todos los Blobs de este fotograma se consideran nuevas personas/caras.
				for (Blob i : fotogramaActual)
				{
					i.maxDistX = contador;
					
					ArrayList<Blob> nuevaPersona = new ArrayList<Blob>();
					nuevaPersona.add(i);
					blobsCaras.add(nuevaPersona);
				}
			}
			else
			{
				// Si no es el primer fotograma y había Blobs en el anterior, hay que hacer un emparejamiento entre los Blobs del fotograma anterior y el fotograma actual.
				// De manera que los Blobs que correspondan a la misma persona se introduzcan en la lista ya creada en vez de crear una nueva lista.
				for (Blob i : fotogramaActual)
				{
					i.maxDistX = contador;
					
					// Buscamos el Blob del fotograma anterior que mejor encaja con el Blob actual.
					Blob blobAnterior = emparejarBlob(i, fotogramaAnterior);
					
					if (blobAnterior != null)
					{
						// Buscamos en qué fila está el Blob anterior (blobAnterior) en la matriz dinámica de Blobs para introducir el Blob actual (i) también en dicha fila.
						for (ArrayList<Blob> j : blobsCaras)
						{
							if (j.contains(blobAnterior))
							{
								j.add(i);
								break;
							}
						}
					}
					else
					{
						// Si no se ha emparejado con ningún Blob, significa que es un Blob de una nueva persona/cara.
						ArrayList<Blob> nuevaPersona = new ArrayList<Blob>();
						nuevaPersona.add(i);
						blobsCaras.add(nuevaPersona);
					}
				}					
			}
			contador++;			
			fotogramaAnterior = fotogramaActual;
		}
		
		// Cuando se completa la lista de Blobs con las caras de la persona, se debe crear la matriz de imágenes con dichas cara.
		// (JIPBmpColor en vez de los Blobs que sólo contienen información de la localización de la cara).
		JIPSequence probandoParametroSalida = new JIPSequence();
		ArrayList<ArrayList<JIPImage>> caras = new ArrayList<ArrayList<JIPImage>>();
		for (ArrayList<Blob> i : blobsCaras)
		{
			ArrayList<JIPImage> fotogramaColor = new ArrayList<JIPImage>();
			for (Blob j : i)
			{
				fotogramaColor.add(recortarFragmento((JIPBmpColor) seq.getFrame(j.maxDistX), j));
				probandoParametroSalida.addFrame(recortarFragmento((JIPBmpColor) seq.getFrame(j.maxDistX), j));
			}
			probandoParametroSalida.addFrame(new JIPBmpBit(179, 179));
			caras.add(fotogramaColor);
		}
		setParamValue("caras", caras);		
		
		// Construimos la secuencia de imágenes geométricas que hay que devolver.
		// Después de cada imagen geométrica se introduce el fotogama original donde se ha detecado movimiento y una cara.
		JIPSequence resultado = new JIPSequence();
		contador = 0;
		resultado.addFrame(seq.getFrame(contador++));
		for (ArrayList<Blob> i : fotogramas)
		{
			JIPGeomPoly jgp = new JIPGeomPoly(width, height);
			ArrayList<Color> colors = new ArrayList<Color>();
			colors.add(new Color(255, 0, 0));
			jgp.setColors(colors);
			
			for (Blob j : i)
			{
				ArrayList<Integer> rectangulo = new ArrayList<Integer>();
				rectangulo.add(j.minx);
				rectangulo.add(j.miny);
				rectangulo.add(j.maxx);
				rectangulo.add(j.miny);
				rectangulo.add(j.maxx);
				rectangulo.add(j.maxy);
				rectangulo.add(j.minx);
				rectangulo.add(j.maxy);
				jgp.addPoly(rectangulo);
			}
			// Sólo introducidos la imagen geométrica si se han detecado caras; la imagen original se introduce siempre.
			if (jgp.getLength() > 0)
				resultado.addFrame(jgp);
			resultado.addFrame(seq.getFrame(contador++));
		}

		// Se devuelve la secuencia que se haya indicado en los parámetros de entrada.
		if (getParamValueString("retorno").equals("Resta de imágenes"))
			return movimientoResta;
		if (getParamValueString("retorno").equals("Segmentación de movimiento"))
			return movimientoSegmentacion;
		if (getParamValueString("retorno").equals("Cierre morfológico"))
			return movimientoCierre;
		if (getParamValueString("retorno").equals("Fragmentos con movimiento detectado"))
			return movimientoBlobs;
		if (getParamValueString("retorno").equals("Secuencia de las personas detectadas"))
			return probandoParametroSalida;
		
		// Si no se indica ninguna salida especial, se devuelve lo que se espera que se devuelva FPracVision,
		// es decir, las imágenes geométricas intercaladas entre la secuencia original.
		return resultado;
	}
	
	/**
	 * Dado un Blob y una lista de Blobs, busca cuál de todos los Blobs de la lista es más parecido al primer Blob.
	 * Utiliza la distancia entre los Blobs y la diferencia de tamaño para ajustarse lo mejor posible.
	 * Además, utiliza unos umbrales de distancia máxima.
	 * @param blobActual Blob que se quiere emparejar con otro Blob.
	 * @param listaBlobs Lista de Blobs donde se buscará el emparejamiento del Blob.
	 * @return Devuelve el Blob emparejado o null si no hay ninguno factible.
	 */
	private Blob emparejarBlob(Blob blobActual, ArrayList<Blob> listaBlobs) throws JIPException
	{
		Blob mejor = null;
		double distancia = getParamValueFloat("distancia");
		
		// Se recorre la lista de blobs.
		for (Blob i : listaBlobs)
		{
			// Si el Blob está más cerca que el último candidato seleccionado, lo actualizamos como nuevo candidato.
			if (distancia > dist(i, blobActual))
			{
				mejor = i;
				distancia = dist(i, blobActual);
			}
		}
		return mejor;
	}
	
	/**
	 * Mide la distancia entre dos Blobs entre sus centros.
	 * @param b1 Blob origen.
	 * @param b2 Blob destino.
	 * @return Devuelve la distancia entre los Blobs. Valor 0 o positivo.
	 */
	private double dist(Blob b1, Blob b2)
	{
		return Math.sqrt(Math.pow(b1.centro_x - b2.centro_x, 2) + Math.pow(b1.centro_y - b2.centro_y, 2));
	}

	/**
	 * Dada una imagen a color y un blob, recorta un fragmento de la imagen a partir de la información del blob.
	 * @param imagen Imagen original desde la que se va a generar una imagen más pequeña.
	 * @param blob Blob que contiene la información de desplazamiento y tamaño del fragmento.
	 * @return Devuelve el fragmento de la imagen a color que se corresponde con el área que marca el blob.
	 * @throws JIPException
	 */
	private JIPBmpColor recortarFragmento(JIPBmpColor imagen, Blob blob) throws JIPException
	{
		JIPBmpColor fragmento = new JIPBmpColor(blob.xsize, blob.ysize);
		for (int i = 0; i < blob.xsize; i++)
		{
			for (int j = 0; j < blob.ysize; j++)
			{
				fragmento.setPixelRed(i, j, ((JIPBmpColor) imagen).getPixelRed(i + blob.minx, j + blob.miny));
				fragmento.setPixelGreen(i, j, ((JIPBmpColor) imagen).getPixelGreen(i + blob.minx, j + blob.miny));
				fragmento.setPixelBlue(i, j, ((JIPBmpColor) imagen).getPixelBlue(i + blob.minx, j + blob.miny));
			}
		}
		return fragmento;
	}
}
