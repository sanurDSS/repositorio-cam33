package javavis.jip2d.functions;

import java.util.Arrays;
import java.lang.Math;

import javavis.base.ImageType; 
import javavis.base.JIPException;
import javavis.base.RGBBandType;
import javavis.base.parameter.JIPParamInt;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.bitmaps.JIPBmpColor;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;
import javavis.jip2d.base.geometrics.JIPImgGeometric;


/**
*Makes a segmentation of the input image using the fuzzy k-means algorithm.<BR>
*An image segmentation is done.
*This method divides the image into 'k' homogeneous clusters.
*It can be applied to images of gray as to those of color.<BR>
*<ul><B>Input parameters:</B><BR>
*<li>img: Input image<BR>
*<li>number: number of clusters. This parameter can not be negative.<BR><BR>
*</ul>
*<ul><B>Output parameters:</B><BR>
*<li>Segmented input image.<BR><BR>
*</ul>
*/


public class FFuzzyKMeans extends JIPFunction {
	private static final long serialVersionUID = -6916738905053544345L;
	
	public FFuzzyKMeans() {
		super();
		name = "FFuzzyKMeans";
		description = "Applies the fuzzy k-means algorithm";
		groupFunc = FunctionGroup.Manipulation;

		JIPParamInt param = new JIPParamInt("number", false, true);
		param.setDescription("Number of clusters");
		param.setDefault(4);
		addParam(param);

		param = new JIPParamInt("threshold", false, true);
		param.setDescription("Threshold");
		param.setDefault(6);
		addParam(param);
		
		param = new JIPParamInt("iter", false, true);
		param.setDescription("Max number of iterations");
		param.setDefault(50);
		addParam(param);
	}

	public JIPImage processImg(JIPImage img) throws JIPException {
		JIPImage aux=null;
		
		if (img instanceof JIPImgGeometric || img.getType() == ImageType.BIT)
			throw new JIPException("FuzzyKMeans can not be applied to this image format");
		
		int numberOfClasses=getParamValueInt("number");
		if (numberOfClasses <= 0) 
			throw new JIPException("Clusters number must be greater than 0");

		int threshold = getParamValueInt("threshold");
		if (threshold <= 0) 
			throw new JIPException("Threshold must be greater than 0");

		int iter = getParamValueInt("iter");
		if (iter <= 0) 
			throw new JIPException("Number of iteration must be greater than 0");
		
		if (img.getType() == ImageType.COLOR)
			aux = fuzzyKMeansColor(img, numberOfClasses, threshold, iter);
		else
			aux = fuzzyKMeansGray(img, numberOfClasses, threshold, iter);
		
		return aux;
	}

	public JIPImage fuzzyKMeansColor(JIPImage img, int numberOfClasses, 
			int threshold, int kMaxIteration) throws JIPException {		
		JIPBmpColor aux = (JIPBmpColor)img.clone();
		double[][] pixelVector = new double[3][];
		int numPixels = aux.getHeight()*aux.getWidth();;
		
		pixelVector[RGBBandType.RED.ordinal()]   = aux.getAllPixelsRed();
		pixelVector[RGBBandType.GREEN.ordinal()] = aux.getAllPixelsGreen();
		pixelVector[RGBBandType.BLUE.ordinal()]  = aux.getAllPixelsBlue();
		
		// Find initial guesses for center values
		double[][] centerValues = findInitialCenters (numberOfClasses, pixelVector);
		double[][] lastValues = new double[3][numberOfClasses];
		System.arraycopy(centerValues[0],0,lastValues[0],0,numberOfClasses);
		System.arraycopy(centerValues[1],0,lastValues[1],0,numberOfClasses);
		System.arraycopy(centerValues[2],0,lastValues[2],0,numberOfClasses);
		
		double [][]probMatrix = new double[numberOfClasses][numPixels];
		double total, auxDif;
		for (int iteration=0; iteration<kMaxIteration; iteration++) {
			// Update the progress bar
			percProgress = (int)(100*iteration/(double)kMaxIteration);
			// compute the table P(Wm / Xj) for each class _m_ and each point _j_
			for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
				// get probability for current pixel and each cluster
				total = 0;
				for (int classIndex = 0; classIndex < numberOfClasses; classIndex++) {
					// get the distance for each channel to its center in the current pixel
					auxDif = Math.pow(pixelVector [RGBBandType.RED.ordinal()][pixelIndex] - 
							centerValues[RGBBandType.RED.ordinal()][classIndex], 2.0) +
							Math.pow(pixelVector [RGBBandType.GREEN.ordinal()][pixelIndex] - 
									centerValues[RGBBandType.GREEN.ordinal()][classIndex], 2.0) +
							Math.pow(pixelVector [RGBBandType.BLUE.ordinal()][pixelIndex] - 
									centerValues[RGBBandType.BLUE.ordinal()][classIndex], 2.0);
					if (auxDif==0) probMatrix [classIndex][pixelIndex] = 1.0;
					else probMatrix [classIndex][pixelIndex] = 1.0/auxDif; 
					total += probMatrix [classIndex][pixelIndex];
				}
				// normalize probabilities
				for (int classIndex = 0; classIndex < numberOfClasses; classIndex++)
					probMatrix [classIndex][pixelIndex] /= total;
			}

			double divisor, dividendR, dividendG, dividendB;
			// now compute new center
			for (int classIndex = 0; classIndex < numberOfClasses; classIndex++) {
				// get the divisor and the dividend
				divisor = 0; dividendR = 0; dividendG = 0; dividendB=0; 
				for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
					// dividend is P(w/x) * x 
					dividendR += probMatrix [classIndex][pixelIndex] * pixelVector [0][pixelIndex];
					dividendG += probMatrix [classIndex][pixelIndex] * pixelVector [1][pixelIndex];
					dividendB += probMatrix [classIndex][pixelIndex] * pixelVector [2][pixelIndex];
					// divisor is the sum of all probabilities
					divisor  += probMatrix [classIndex][pixelIndex]; 
				}
				// get the new center
				centerValues [0][classIndex] = (int)(dividendR / divisor);
				centerValues [1][classIndex] = (int)(dividendG / divisor);
				centerValues [2][classIndex] = (int)(dividendB / divisor);
			}

			// check for end criteria (minor changes?)
			double changes = 0;
			for (int classIndex = 0; classIndex < numberOfClasses; classIndex++) {
				double value = 0;
				for (int colorIndex = 0; colorIndex < 3; colorIndex++) {
					value += Math.pow(centerValues[colorIndex][classIndex] - lastValues[colorIndex][classIndex], 2.0);
					// get the new values for the next iteration
					lastValues[colorIndex][classIndex] = centerValues[colorIndex][classIndex]; 
				}
				changes += Math.sqrt (value);
			}
			
			if (changes < threshold)
				break;
		}


		double bestProb;
		// fit each pixel in the proper class		
		for (int pixelIndex = 0; pixelIndex < numPixels; pixelIndex++) {
			// get the maximum probability
			bestProb=0;
			for (int classIndex = 0; classIndex < numberOfClasses; classIndex++)
				if (probMatrix [classIndex][pixelIndex] > bestProb) {
					pixelVector [0][pixelIndex] = centerValues [0][classIndex];
					pixelVector [1][pixelIndex] = centerValues [1][classIndex];
					pixelVector [2][pixelIndex] = centerValues [2][classIndex];
					bestProb  = probMatrix [classIndex][pixelIndex];
				}
		}
		aux.setAllPixelsRed (pixelVector[RGBBandType.RED.ordinal()]);
		aux.setAllPixelsGreen (pixelVector[RGBBandType.GREEN.ordinal()]);
		aux.setAllPixelsBlue (pixelVector[RGBBandType.BLUE.ordinal()]);

		return aux;
	}
	
	public JIPImage fuzzyKMeansGray(JIPImage img, int numberOfClasses, 
			int threshold, int kMaxIteration) throws JIPException {
		JIPImgBitmap aux = (JIPImgBitmap)img.clone();
		double []pixelVector = aux.getAllPixels();
		
		// Find initial guesses for center values
		double []centerValues = findInitialCenters (numberOfClasses, pixelVector);
		double []lastValues=new double[centerValues.length];;
		System.arraycopy(centerValues,0,lastValues,0,centerValues.length);
		double [][]probMatrix = new double[numberOfClasses][pixelVector.length];

		double total, auxDif;
		for (int iteration=0; iteration<kMaxIteration; iteration++) {
			// Update the progress bar
			percProgress = (int)(100*iteration/(double)kMaxIteration);
        	// compute the table P(Wm / Xj) for each class _m_ and each point _j_
        	for (int pixelIndex = 0; pixelIndex < pixelVector.length; pixelIndex++) {
        		// get probability for current pixel and each cluster
        		total = 0;
        		for (int classIndex = 0; classIndex < numberOfClasses; classIndex++) {
        			auxDif = pixelVector[pixelIndex] - centerValues[classIndex];
        			if (auxDif==0) probMatrix [classIndex][pixelIndex] = 1.0;
        			else probMatrix [classIndex][pixelIndex] = Math.pow(auxDif, -2.0); 
        			total += probMatrix [classIndex][pixelIndex];
        		}
        		for (int classIndex = 0; classIndex < numberOfClasses; classIndex++) 
        			probMatrix [classIndex][pixelIndex] /= total;
        	}
        	
        	// now compute new center
        	for (int classIndex = 0; classIndex < numberOfClasses; classIndex++) {
        		// get the divisor and the dividend
        		double divisor = 0, dividend = 0;
        		for (int pixelIndex = 0; pixelIndex < pixelVector.length; pixelIndex++) {
        			// dividend is P(w/x) * x 
        			dividend += probMatrix[classIndex][pixelIndex] * pixelVector[pixelIndex];
        			// divisor is the sum of all probabilities
        			divisor  += probMatrix[classIndex][pixelIndex];
        		}
        		// get the new center
        		centerValues [classIndex] = (int)(dividend / divisor);
        	}
        	
        	// check for end criteria (minor changes?)
        	int  changes = 0;
        	for (int classIndex = 0; classIndex < numberOfClasses; classIndex++) {
        		changes += Math.abs(centerValues [classIndex] - lastValues [classIndex]);
        		// get last values
        		lastValues [classIndex] = centerValues [classIndex];
        	}
        	if (changes < threshold) 
        		break;
        }
        
		// Assigns the cluster value to each pixel
        for (int pixelIndex = 0; pixelIndex < pixelVector.length; pixelIndex++) {
        	// get the maximum probability
        	double bestProb = 0;
        	for (int classIndex = 0; classIndex < numberOfClasses; classIndex++)
        		if (probMatrix [classIndex][pixelIndex] > bestProb) {
        			pixelVector [pixelIndex] = centerValues [classIndex];
        			bestProb  = probMatrix [classIndex][pixelIndex];
        		}
        }
        aux.setAllPixels(pixelVector);
		return aux;
	}
		
	/**
	 * Find the initial centers of the clusters (for color images). Just sorts the arrays and
	 * take a guess for each cluster
	 * @param numberOfClasses Number of classes
	 * @param orgPixelVector Original data
	 * @return Array with numberOfClasses guesses
	 */
	public double[][] findInitialCenters (int numberOfClasses, double [][]orgPixelVector) {
		double [][] vectorClases = new double [3][numberOfClasses];
		double []pixelVector=new double[orgPixelVector[0].length];;
		
		for (int colorIndex = 0; colorIndex < 3; colorIndex++) {
			System.arraycopy(orgPixelVector[colorIndex],0,pixelVector,0,orgPixelVector[colorIndex].length);
			// incremental sort
			Arrays.sort (pixelVector);
		
			int sep = pixelVector.length / numberOfClasses;
			for (int i = 0, j = 0; i < pixelVector.length && j < numberOfClasses; j++) {
				vectorClases[colorIndex][j] = pixelVector[i];
				i += sep;
			}
		}
		return vectorClases;
	}

	/**
	 * Find the initial centers of the cluster. Just sorts the arrays and
	 * take a guess for each cluster
	 * @param numberOfClasses Number of classes
	 * @param orgPixelVector Original data
	 * @return Array with numberOfClasses guesses
	 */
	public double[] findInitialCenters (int numberOfClasses, double []orgPixelVector) {
		double [] vectorClases = new double[numberOfClasses];
		double [] pixelVector = new double[orgPixelVector.length];
		System.arraycopy(orgPixelVector,0,pixelVector,0,orgPixelVector.length);
		
		Arrays.sort (pixelVector); // minor to greater sort
				
		int sep = pixelVector.length / numberOfClasses;
		for (int i = 0, j = 0; i < pixelVector.length && j < numberOfClasses; j++) {
			vectorClases[j] = pixelVector[i];
			i += sep;
		}
		return vectorClases;
	}
	
}


