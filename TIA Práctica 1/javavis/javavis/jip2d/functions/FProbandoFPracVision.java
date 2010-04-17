package javavis.jip2d.functions;

import java.util.ArrayList;

import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.base.bitmaps.JIPBmpColor;

/**
 * Función para evaluar la función FPracVision.
 * Esta función de prueba devuelve un mosaico con las caras de las personas encontradas.
 * @author Cristian Aguilera Martínez
 */
public class FProbandoFPracVision extends JIPFunction {
	private static final long serialVersionUID = -2316890482034204804L;

	public FProbandoFPracVision()
	{
		super();
		name = "FProbandoFPracVision";
		description = "Pruebas de FPracVision";
		groupFunc = FunctionGroup.Applic;
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
		// Se procesa la secuencia y se extrae la matriz dinámica con las personas detectadas.
		FPracVision fPracVision = new FPracVision();
		fPracVision.processSeq(seq);
		ArrayList<ArrayList<JIPImage>> personas = (ArrayList<ArrayList<JIPImage>>) fPracVision.getParamValueObj("caras");
		
		// Se calcula la dimensión que tendrá el mosaico.
		int width = 0;
		int height = 0;
		for (ArrayList<JIPImage> i : personas)
		{
			int widthAux = 0;
			int heightAux = 0;
			for (JIPImage j : i)
			{
				widthAux += j.getWidth();
				heightAux = Math.max(heightAux, j.getHeight());
			}
			width = Math.max(width, widthAux);
			height += heightAux;
		}		
		
		// Se construye el mosaico.
		JIPBmpColor destino = new JIPBmpColor(width, height);
		int desplazamientoY = 0;
		for (ArrayList<JIPImage> i : personas)
		{
			int desplazamientoX = 0;
			int desplazamientoYAux = 0;
			for (JIPImage j : i)
			{
				copiarCacho((JIPBmpColor) j, destino, desplazamientoX, desplazamientoY);
				desplazamientoX += j.getWidth();
				desplazamientoYAux = Math.max(desplazamientoYAux, j.getHeight());
			}
			desplazamientoY += desplazamientoYAux;
		}
		
		// Se devuelve una secuencia con sólo la imagen del mosaico.
		JIPSequence secuencia = new JIPSequence();
		secuencia.addFrame(destino);
		return secuencia;
	}

	/**
	 * Copia una imagen dentro de otra imagen según un desplazamiento en X y en Y.
	 * @param origen Imagen que se va a copiar.
	 * @param destino Imagen donde se va a copiar la primera imagen.
	 * @param desplazamientoX Desplazamiento horizontal desde el que se comienza la copia de la imagen.
	 * @param desplazamientoY Desplazamiento vertical desde el que se comienza la copia de la imagen.
	 * @throws JIPException
	 */
	private void copiarCacho(JIPBmpColor origen, JIPBmpColor destino, int desplazamientoX, int desplazamientoY) throws JIPException
	{
		for (int i = desplazamientoX; i < desplazamientoX + origen.getWidth(); i++)
		{
			for (int j = desplazamientoY; j < desplazamientoY + origen.getHeight(); j++)
			{
				destino.setPixelBlue(i, j, origen.getPixelBlue(i-desplazamientoX, j-desplazamientoY));
				destino.setPixelRed(i, j, origen.getPixelRed(i-desplazamientoX, j-desplazamientoY));
				destino.setPixelGreen(i, j, origen.getPixelGreen(i-desplazamientoX, j-desplazamientoY));
			}
		}
	}
}
