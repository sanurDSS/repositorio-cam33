package javavis.jip2d.functions;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javavis.base.JIPException;
import javavis.base.parameter.JIPParamBool;
import javavis.base.parameter.JIPParamFile;
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
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPGeomPoly;
import javavis.jip2d.util.Blob;
import javavis.jip2d.util.MatMorph;

/**
 * Detecta las caras en una secuencia de imágenes.
 * @author cristian
 */
public class FPracVision extends JIPFunction
{ 
	private static final long serialVersionUID = -3221456052818528739L;

	public FPracVision()
	{
		super();
		name = "FPracVision";
		description = "Detecta las caras en una secuencia de imágenes.";
		groupFunc = FunctionGroup.Cristian;
		
		// Parámetros para binarizar la imagen de grises.
		JIPParamInt p1 = new JIPParamInt("u1", false, true);
		p1.setDefault(30);
		p1.setDescription("Umbral para determinar si un byte es 0 o 1 (si se supera el umbral, es 1)");
		addParam(p1);
		
		// Parámetros para el cierre morfológico.
		JIPParamFile p2 = new JIPParamFile("ee", false, true);
		p2.setDefault("Images/ee4.txt");
		p2.setDescription("Estructurant Element");
		addParam(p2);
		
		// Filtro para limitar los blobs que se procesan con FDetectaCaras y ahorrar tiempo.
		JIPParamInt p3 = new JIPParamInt("tamano", false, true);
		p3.setDefault(800);
		p3.setDescription("Tamaño mínimo de los blobs para considerarlos \"movimiento\"");
		addParam(p3);
		
		JIPParamList p11 = new JIPParamList("retorno", false, true);
		String []p11aux = new String[5];
		p11aux[0] = "Completo";
		p11aux[1] = "Resta de imágenes";
		p11aux[2] = "Segmentación de movimiento";
		p11aux[3] = "Cierre morfológico";
		p11aux[4] = "Fragmentos con movimiento detectado";
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
				JIPBmpByte fotogramaByte = new JIPBmpByte(720, 576);
				byte [] pixeles1 = imagenActualByte.getAllPixelsByte();
				byte [] pixeles2 = imagenAnteriorByte.getAllPixelsByte();
				byte [] pixeles = new byte[720 * 576];
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
					
					// Filtramos las zonas de movimiento que no tengan un tamaño suficiente.
					if (i.xsize * i.ysize > getParamValueInt("tamano"))
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
		
		// TODO: Detectamos las caras en las zonas con cambios.
		// Comprobamos si las nuevas caras ya estaba en el fotograma anterior o si falta alguna cara que estaba antes. 
		// Asociar los blobs con los anteriores (ver si falta alguno y si sobra alguno).
		// Generamos el parámetro de salida "caras", que contiene la secuencia de caras/personas que han pasado por delante de la cámara.
		
		// Generamos una matriz dinámica de Blobs en la que cada fila representa el movimiento de una persona diferente a través de la escena.
		// Posteriormente, se generará a partir de esta matriz otra matriz de JIPImage con los fragmentos de las imágenes en vez de los Blobs.
		ArrayList<ArrayList<Blob>> blobsCaras = new ArrayList<ArrayList<Blob>>();
		ArrayList<Blob> fotogramaAnterior = null;
		for (ArrayList<Blob> fotogramaActual : fotogramas)
		{
			if (fotogramaAnterior == null || fotogramaAnterior.size() == 0)
			{
				// Si es el primer fotograma o en el fotograma anterior no se habían detectado caras, todos los Blobs de este fotograma se consideran nuevas personas/caras.
				for (Blob i : fotogramaActual)
				{
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
					//TODO para cada blob buscamos en el fotograma anteriro cual es el que es válido emparejarlo (establecer umbral de distancia ¿y de tamaño?
					// si no hay ninguno valido, repetimos las 3 lineas del otro bucle y fuera
				}					
			}
			
			fotogramaAnterior = fotogramaActual;
		}
		
		// Cuando se completa la lista de Blobs con las caras de la persona, se debe crear la matriz de imágenes con dichas cara.
		// (JIPBmpColor en vez de los Blobs que sólo contienen información de la localización de la cara).
		ArrayList<ArrayList<JIPBmpColor>> caras = new ArrayList<ArrayList<JIPBmpColor>>();
		//TODO convertir ALISTALISTBLOB en ALISTALISJIPBmpColor
		setParamValue("caras", caras);
		
		// Construimos la secuencia de imágenes geométricas que hay que devolver.
		// Después de cada imagen geométrica se introduce el fotogama original donde se ha detecado movimiento y una cara.
		JIPSequence resultado = new JIPSequence();
		int contador = 0;
		resultado.addFrame(seq.getFrame(contador++));
		for (ArrayList<Blob> i : fotogramas)
		{
			JIPGeomPoly jgp = new JIPGeomPoly(720, 576); //TODO ajustar tamaño bien
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
		if (getParamValueString("retorno").equals("Blobs detectados"))
			return movimientoBlobs;
		return resultado;
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
