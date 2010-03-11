package javavis.jip2d.functions;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpBit;

/**
*Calculates the skeleton of the binary input image.<BR>
*Applicable to: BIT<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Binary image with the skeleton of the input image.<BR><BR>
*</ul>
*/
public class FSkeleton extends JIPFunction {
	private static final long serialVersionUID = 6263008513202276002L;

	public FSkeleton() {
		super();
		name = "FSkeleton";
		description = "Obtains the skeleton from a binary image.";
		groupFunc = FunctionGroup.Others;
	}
	
	public JIPImage processImg(JIPImage img) throws JIPException {
		if (img.getType() != ImageType.BIT)
			throw new JIPException("Image format must be BIT");

		JIPBmpBit imgBit = (JIPBmpBit)img;
		int w = img.getWidth();
		int h = img.getHeight();
		int nb = imgBit.getNumBands();
		JIPBmpBit res = (JIPBmpBit)JIPImage.newImage(nb, w, h, ImageType.BIT);
		boolean[] o = new boolean[8];
		boolean[] bmp, destino;
		boolean cambios, cambiosLocal;

		for(int b=0; b<nb; b++){
			bmp = imgBit.getAllPixelsBool(b);
			destino = (boolean[]) bmp.clone();
			
			do{
				cambios = false;
				//B4
				cambiosLocal=false;
				for (int fil = 1; fil < h - 1; fil++)
					for (int col = 1; col < w - 1; col++) 
						if (bmp[fil*w + col ]) {
							getMasc(bmp,w,h,fil,col,o);
							boolean B4 = o[0] && (o[1] || o[2] || o[6] || o[7]) && (o[2] || !(o[3])) && (!(o[5]) || o[6]) && !o[4];
							if (B4) {
								cambiosLocal=true;
								cambios = true;
								destino[fil*w + col] = false;
							}
						}
				if(cambiosLocal) bmp=(boolean[]) destino.clone();

				//B0
				cambiosLocal=false;
				for (int fil = 1; fil < h - 1; fil++)
					for (int col = 1; col < w - 1; col++) 
						if (bmp[fil*w + col ]) {
							getMasc(bmp,w,h,fil,col,o);
							boolean B0 = o[4] && (o[2] || o[3] || o[5] || o[6]) && (o[6] || !(o[7])) && (!o[1] || o[2]) && !o[0];
							if (B0) {
								cambiosLocal=true;
								cambios = true;
								destino[fil*w + col] = false;
							}
						}
				if (cambiosLocal) bmp=(boolean[]) destino.clone();

				//B2
				cambiosLocal=false;
				for (int fil = 1; fil < h - 1; fil++)
					for (int col = 1; col < w - 1; col++) 
						if (bmp[fil*w + col ]) {
							getMasc(bmp,w,h,fil,col,o);
							boolean B2 = o[6] && (o[0] || o[4] || o[5] || o[7]) && (o[0] || !o[1]) && (!o[3] || o[4]) && !o[2];
							if (B2) {
								cambiosLocal=true;
								cambios = true;
								destino[fil*w + col] = false;
							}
						}
				if (cambiosLocal) bmp=(boolean[]) destino.clone();
				
				//B6
				cambiosLocal=false;
				for (int fil = 1; fil < h - 1; fil++)
					for (int col = 1; col < w - 1; col++)
						if (bmp[fil*w + col ]) {
							getMasc(bmp,w,h,fil,col,o);
							boolean B6 = o[2] && (o[0] || o[1] || o[3] || o[4]) && (o[4] || !o[5]) && (o[0] || !o[7]) && !o[6];
							if (B6) {
								cambiosLocal=true;
								cambios = true;
								destino[fil*w + col] = false;
							}
						}
				if (cambiosLocal) bmp=(boolean[]) destino.clone();
			} while (cambios);
			res.setAllPixelsBool(b, destino);
		}
		return res;
	}
	
	public void getMasc(boolean [] bmp, int w, int h, int fil, int col, boolean []m){
		m[0] = bmp[(fil)*w + (col + 1)];
		m[1] = bmp[(fil - 1)*w + (col + 1)];
		m[2] = bmp[(fil - 1)*w + (col)];
		m[3] = bmp[(fil - 1)*w + (col - 1)];
		m[4] = bmp[(fil)*w + (col - 1)];
		m[5] = bmp[(fil + 1)*w + (col - 1)];
		m[6] = bmp[(fil + 1)*w + (col)];
		m[7] = bmp[(fil + 1)*w + (col + 1)];
		return;
	}
}
