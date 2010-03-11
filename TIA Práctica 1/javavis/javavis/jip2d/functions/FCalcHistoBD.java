package javavis.jip2d.functions;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.awt.Image;

import javavis.base.JIPException;
import javavis.base.JIPToolkit;
import javavis.base.parameter.JIPParamDir;
import javavis.base.parameter.JIPParamInt;
import javavis.base.parameter.JIPParamList;
import javavis.base.parameter.JIPParamString;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;


/**
*Calculates the histograms of images in a directory. It gets the directory
*name and gets all the subdirectories in it. Every directory is a cluster
*containing more images (all of them in the same group). It uses FCalcHistoColor
*to get the histogram of each image<BR>
*Use: FCalcHistoBD<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>dir: Directory containing the BD.<BR>
*<li>disc: Discretization (number of bins)<BR>
*<li>fileBD: File to save the results.<BR>
*<li>type: Type of the image.<BR>
*</ul>
<ul><B>Output parameters:</B><BR>
*<li>The input image remains the same.<BR><BR>
*</ul>
*/

public class FCalcHistoBD extends JIPFunction {
	private static final long serialVersionUID = -3069473379369433977L;

	public FCalcHistoBD() {
		super();
		name = "FCalcHistoBD";
		description = "Calculates the histograms of images in a directory";
		groupFunc = FunctionGroup.ImageBD;

		JIPParamDir p1 = new JIPParamDir("dir", false, true);
		p1.setDescription("Directory to process");
		JIPParamInt p2 = new JIPParamInt("disc", false, true);
		p2.setDefault(20);
		p2.setDescription("Discretization (number of bins)");
		JIPParamString p3 = new JIPParamString("fileBD", false, true);
		p3.setDefault("out");
		p3.setDescription("File to save the results");
		JIPParamList p4 = new JIPParamList("type", false, true);
		String []paux = new String[3];
		paux[0]="RGB";
		paux[1]="YCbCr";
		paux[2]="HSI";
		p4.setDefault(paux);
		p4.setDescription("Type of the image");

		addParam(p1);
		addParam(p2);
		addParam(p3);
		addParam(p4);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		// First, open the file to save the histograms
		String fileBD = getParamValueString("fileBD");
		FileOutputStream fos=null;
		ObjectOutputStream oos=null;
		try {
			fos = new FileOutputStream(fileBD);
			oos = new ObjectOutputStream(fos);
			int disc = getParamValueInt("disc");
			String type = getParamValueString("type");
			String dir = getParamValueString("dir");
			File f = new File(dir);
			// Get the names of files and directories in the current directory
			String []clusters = f.list();
			String []images;
			JIPImage imgAux=null;
			FCalcHistoColor chc = new FCalcHistoColor();
			chc.setParamValue("disc", disc);
			chc.setParamValue("type",type);
			for (String clus : clusters) {
				String group=dir+File.separator+clus;
				File f2 = new File(group);
				// Only processes the directories
				if (f2.isDirectory()) {
					images=f2.list();
					// Processes all the images in the directory
					for (String im : images) {
						String fileImg=group+File.separator+im;
						Image imgAWT = JIPToolkit.getAWTImage(fileImg);
						if (imgAWT != null) 
							imgAux=JIPToolkit.getColorImage(imgAWT);
						else continue;
						// Do not process files which are not images
						if (imgAux != null) {
							chc.processImg(imgAux);
							if (chc.isInfo()) {
								info = "CalcHistoColor info: "+chc.getInfo();
								continue;
							}
							float [][][]acumF =(float[][][])chc.getParamValueObj("histo");
							// Stores the filename and the histogram
							oos.writeUTF(fileImg);
							oos.writeObject(acumF);
						}
						else info = "FCalcHistoBD: some files are not images (JPEG, GIF)";
					}
				}
				else //The images are directly in the folder
				{
					//Processes all the images in the directory
					Image imgAWT = JIPToolkit.getAWTImage(group);
					if (imgAWT != null)
						imgAux=JIPToolkit.getColorImage(imgAWT);
					else continue;
					// Do not process files which are not images
					if (imgAux != null) {
						chc.processImg(imgAux);
						if (chc.isInfo()) {
							info = "CalcHistoColor info: "+chc.getInfo();
							continue;
						}
						float [][][]acumF =(float[][][])chc.getParamValueObj("histo");
						// Stores the filename and the histogram
						oos.writeUTF(group);
						oos.writeObject(acumF);
					}
					else info = "FCalcHistoBD: some files are not images (JPEG, GIF)";
				}
			}
		}
		catch (IOException e) {
			throw new JIPException("FCalcHistBD: error opening or writing in file "+fileBD);
		}		
		finally {
			try {
				if (oos != null && fos != null) {
					oos.close();
					fos.close();
				}
			}
			catch (IOException e) {System.out.println(e);} 				
		}
		return img;
	}
}
