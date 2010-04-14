package javavis.jip2d.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.base.bitmaps.JIPBmpBit;
import javavis.jip2d.base.bitmaps.JIPBmpByte;
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
		
		JIPParamList p11 = new JIPParamList("fase", false, true);
		String []p11aux = new String[7];
		p11aux[0] = "Completo";
		p11aux[1] = "Restar imágenes";
		p11aux[2] = "Segmentar movimiento";
		p11aux[3] = "Cierre morfológico";
		p11aux[4] = "Filtrar blobs";
		p11aux[5] = "Detectar caras en esos blobs";
		p11aux[6] = "Asociar caras con caras del frame anterior";
		p11.setDefault(p11aux);
		p11.setDescription("Hasta dónde ejecutar el algoritmo");
		addParam(p11);
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
		// Construimos la lista que almacena los blobs de cada fotograma.
		ArrayList<ArrayList<Blob>> fotogramas = new ArrayList<ArrayList<Blob>>(seq.getNumFrames());
		for (int i = 0; i < seq.getNumFrames(); i++)
		{
			fotogramas.add(new ArrayList<Blob>());
		}
		
		// Añadimos las caras detectadas en el primer fotograma.
		FDetectaCaras fdc = new FDetectaCaras();
		fdc.processImg(seq.getFrame(0));
		fotogramas.get(0).addAll((ArrayList<Blob>) fdc.getParamValueObj("blobs"));
		
		// Construimos la secuencia con la segmentación de movimiento entre cada par de fotogramas (primer fotograma es todo blanco).
		JIPSequence movimiento = new JIPSequence();
		// TODO movimiento.addFrame(new JIPBmpBit(720, 576));
		//boolean []tautologia = new boolean[720 * 576];
		//for (int i = 0; i < tautologia.length; i++) tautologia[i] = true;
		//((JIPBmpBit) (movimiento.getFrame(0))).setAllPixelsBool(tautologia);
		
		// Funciones de JavaVis que se van a utilizar.
		FColorToGray fctg = new FColorToGray();
		FBinarize fb = new FBinarize();
		fb.setParamValue("u1", getParamValueInt("u1"));
		FClousure fc = new FClousure();
		fc.setParamValue("ee", getParamValueString("ee"));
		FDilate fd = new FDilate();
		fd.setParamValue("ee", "Images/ee2.txt");
		FErode fe = new FErode();
		fe.setParamValue("ee", "Images/ee.txt");
		
		// Recorremos la secuencia de imágenes.	
		JIPImage imagenAnterior = null;
		JIPBmpByte imagenAnteriorByte = null;
		for (JIPImage i : seq.getFrames())
		{
			JIPImage imagenActual = i;
			JIPBmpByte imagenActualByte = (JIPBmpByte) fctg.processImg(imagenActual); 
			if (imagenAnterior != null)
			{
				// Convertimos las imágenes a gris y restamos los dos frames.
				JIPBmpByte fotogramaByte = new JIPBmpByte(720, 576);
				byte [] pixeles1 = imagenActualByte.getAllPixelsByte();
				byte [] pixeles2 = imagenAnteriorByte.getAllPixelsByte();
				byte [] pixeles = new byte[720 * 576];
				for (int j = 0; j < pixeles1.length; j++)
				{
					pixeles[j] = (byte) Math.abs(pixeles1[j] - pixeles2[j]);
				}
				fotogramaByte.setAllPixelsByte(pixeles);
				
				if (!getParamValueString("fase").equals("Restar imágenes"))
				{
					// Segmentamos la zona de movimiento.
					JIPBmpBit fotogramaBit = (JIPBmpBit) fb.processImg(fotogramaByte);
				
					if (!getParamValueString("fase").equals("Segmentar movimiento"))
					{
						// Aplicamos el cierre morfológico para reducir el ruido.
						fotogramaBit = (JIPBmpBit) fe.processImg(fotogramaBit);
						fotogramaBit = (JIPBmpBit) fe.processImg(fotogramaBit);
						fotogramaBit = (JIPBmpBit) fd.processImg(fotogramaBit);
						fotogramaBit = (JIPBmpBit) fd.processImg(fotogramaBit);
						fotogramaBit = (JIPBmpBit) fd.processImg(fotogramaBit);
						fotogramaBit = (JIPBmpBit) fc.processImg(fotogramaBit);
						
						if (!getParamValueString("fase").equals("Cierre morfológico"))
						{
							// Extraer los blobs, recortar los fragmentos de la imagen original y detectar caras en trozos pequeños.
							
							// Asociar los blobs con los anteriores (ver si falta alguno y si sobra alguno).
							
						}
					}	
					movimiento.addFrame(fotogramaBit);
				}
				else
				{
					movimiento.addFrame(fotogramaByte);
				}
			}
			imagenAnterior = imagenActual;
			imagenAnteriorByte = imagenActualByte;
		}
		
		// TODO: Detectamos las caras en las zonas con cambios.
		// Comprobamos si las nuevas caras ya estaba en el fotograma anterior o si falta alguna cara que estaba antes. 
		
		if (true)
			return movimiento;
		
		// Construimos la secuencia de imágenes geométricas que hay que devolver.
		// Generamos la secuencia de imágenes geométricas a partir de los blobs detectados.
		JIPSequence resultado = new JIPSequence();
		for (ArrayList<Blob> i : fotogramas)
		{
			JIPGeomPoly jgp = new JIPGeomPoly(720, 576); //TODO ajustar tamaño bien
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
			resultado.addFrame(jgp);
		}
		return resultado;
	}
}
