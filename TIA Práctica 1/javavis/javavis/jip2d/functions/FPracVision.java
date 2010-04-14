package javavis.jip2d.functions;

import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;

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
	}
	
	@Override
	public JIPImage processImg(JIPImage img) throws JIPException
	{
		throw new JIPException("FPracVision debe aplicarse a la secuencia completa.");
	}
	
	@Override	
	public JIPSequence processSeq(JIPSequence seq) throws JIPException
	{
		return seq;
	}
}
