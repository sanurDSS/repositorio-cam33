package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpFloat;
import javavis.jip2d.base.geometrics.JIPGeomPoly;
import javavis.jip2d.base.geometrics.JIPImgGeometric;
import javavis.jip2d.util.Junctions;

/**
*<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img1: Input image<BR>
* <li>thres_t: difference of grey levels that is considered as equal (It belongs to SUSAN area)<BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Result image with type ImageType.BYTE.<BR><BR>
*</ul>
*/
public class FJunctions extends JIPFunction {
	private static final long serialVersionUID = -6325253526988864760L;
	
	public FJunctions() {
		super();
		name = "Junctions";
		description = "Gets the junctions in the image";
		groupFunc = FunctionGroup.FeatureExtract;

		JIPParamInt umbral_t = new JIPParamInt("thres_t", false, true);
		umbral_t.setDefault(15);
		umbral_t.setDescription("Threshold to differentiate the color levels");

		addParam(umbral_t);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		ImageType t;
		JIPBmpFloat imgFloat;
		JIPGeomPoly res;
		
		t=img.getType();
		if (t == ImageType.BIT || img instanceof JIPImgGeometric) 
			throw new JIPException("Junctions can not be applied to this image format");
		
		switch (t) {
			case COLOR: FColorToGray fcg = new FColorToGray();
						fcg.setParamValue("gray", "FLOAT");
						imgFloat=(JIPBmpFloat)fcg.processImg(img);
						break;
			case FLOAT: imgFloat = (JIPBmpFloat)img;
						break;
			case BYTE:
			case SHORT: FGrayToGray fgg = new FGrayToGray();
						fgg.setParamValue("gray", "FLOAT");
						imgFloat=(JIPBmpFloat)fgg.processImg(img);
						break;
		}
		
		int w = img.getWidth();
		int h = img.getHeight();
		boolean corners = getParamValueBool("corners");
		
		
	}
	

	
	public float[] findHisto (JIPBmpFloat img, int x, int y, int r_i, int r_e, float threshold, int numbins) {
		float acum, aux_acum;
		int marca, bin;
		
		for (int ang=0; ang<360; ang++) {
			acum = 0.0f;
		    marca = 0;
	        for (int i=0; i<Junctions.discr_cont_seg(ang); i++)  {
		    	aux_acum = acum;
		    	acum += Junctions.discr_valor(i,ang);
		    	bin=which_bin (numbins, VMPixel (src, 0, y + Junctions.discr_y(i,ang), x + Junctions.discr_x(i,ang), VFloat), binbnd);
		    	if (acum >= r_i) {
		    		/* First time */
		    		if (marca==0) {
		    			vector_acum [0] = logponpoff[bin] * (acumulador-r_i);
		    			vector_acum2 [0] = VMPixel(src_orien, 0, y + Junctions.discr_y(i,ang), x + Junctions.discr_x(i,ang), VFloat) * (acumulador-r_i);
		    			marca=1;
		    			continue;
		    		}
		    		/* Final */
		    		if (acum>r_e) {
		    			if ((acum-(marca+r_i)) > 1) {
		    				vector_acum [marca-1] += logponpoff[bin] * ((r_i+marca)-aux_acum);
		    				vector_acum2 [marca-1] += VMPixel(src_orien, 0, y + discr_y(i,ang), x + Junctions.discr_x(i,ang), VFloat)
		    					* ((r_i+marca)-aux_acum);
		    				vector_acum [marca] = logponpoff[bin];
		    				vector_acum2 [marca] = VMPixel(src_orien, 0, y + discr_y(i,ang), x + Junctions.discr_x(i,ang), VFloat);
		    			}
		    			else {
		    				vector_acum2 [marca-1] += VMPixel(src_orien, 0, y + discr_y(i,ang), x + Junctions.discr_x(i,ang), VFloat)
		    					* (r_e-aux_acum);
		    				vector_acum [marca-1] += logponpoff[bin] * (r_e-aux_acum);
		    			}
		    			break;
		    		}
		    		/* One pixel is accumulated */
		    		if (acum>(r_i+marca)) {
		    			vector_acum [marca-1] += logponpoff[bin] *  ((r_i+marca)-aux_acum);
		    			vector_acum2 [marca-1] += VMPixel(src_orien, 0, y + discr_y(i,ang), x + Junctions.discr_x(i,ang), VFloat) *
		    				((r_i+marca)-aux_acum);
		    			vector_acum [marca] = logponpoff[bin] * (acumulador-(r_i+marca));
		    			vector_acum2 [marca] = VMPixel(src_orien, 0, y + discr_y(i,ang), x + Junctions.discr_x(i,ang), VFloat) *
		    				(acumulador-(r_i+marca));	
		    			marca++;
		    			continue;
		    		}
		    		vector_acum [marca-1] += logponpoff[bin] * Junctions.discr_valor(i,ang);
		    		vector_acum2 [marca-1] += VMPixel(src_orien, 0, y + discr_y(i,ang), x + Junctions.discr_x(i,ang), VFloat) *
		    		Junctions.discr_valor(i,ang);	
		    	}
	        }
	        // For each angle, the mean of the evidence is calculated
	        imagen[ang]=0.00;
	        imagen_o[ang]=0.00;
	        for (int i=0; i< r_e-r_i; i++) {
	        	imagen [ang] += vector_acum[i];
	        	imagen_o [ang] += (consistent((PI/2.00)+(ang*PI/180.00), vector_acum2[i], ang_tol) ? inbox : outbox);
	        }
	        imagen[ang] /= r_e-r_i;
	        imagen_o[ang] /= r_e-r_i;
		}
	}
}