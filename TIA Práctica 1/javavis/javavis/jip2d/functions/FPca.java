package javavis.jip2d.functions;

import cern.colt.matrix.impl.*;
import cern.colt.matrix.*;
import cern.colt.matrix.linalg.*;
import javavis.base.ImageType;
import javavis.base.JIPException;
import javavis.base.parameter.JIPParamFile;
import javavis.base.parameter.JIPParamFloat;
import javavis.jip2d.base.FunctionGroup;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.base.bitmaps.JIPImgBitmap;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
*<B>FPca: </B>The learning step for Principal Component Analysis (PCA) (H. Murase 
*and S.K. Nayar. Visual Learning and Recognition of 3-D Objects from Appearance.
*International Journal of Computer Vision.(1995))<BR>
*Given a set of input views it builds the associated eigenspace and projects them into such 
*space. This is done by computing the averaged image and the eigenvalues and eigenvectors of the covariance 
*matrix, and then using them to perform the projection of each view. 
*<ul><B>Parameters:</B><BR>
*<li>seq: Sequence with the input views (all must have equal size)<BR>
*<BR> 
*</ul>
*<ul><B>Outputs:</B><BR>
*<li>A multi-band image encoding the eigenspace: (i) the first band contains the relative importance of 
*each eigenvalue; (ii) the second one contains the averaged image (prototype); (iii) each of the following 
*bands has a eigenvector (one per each input view to simplify computations); (iv) finally, the following 
*bands are associated to the projection of each input images.<BR>
*<BR>
*</ul>
*/
public class FPca extends JIPFunction {
	private static final long serialVersionUID = -3266974331538240778L;

	public FPca() {
		name = "FPca";
		description = "Learning step of PCA";
		groupFunc = FunctionGroup.Pca;
		
		//Definicion de Parametros
		JIPParamFile p1 = new JIPParamFile("BD", false, true);
		p1.setDescription("File where eigenspace is saved");
		p1.setDefault("eigenInformation.pca");
		addParam(p1);
		JIPParamFloat p2 = new JIPParamFloat("perc", false, true);
		p2.setDescription("Percentage of the accumulative total (0..1)");
		p2.setDefault(0.9f);
		addParam(p2);
	}

	/**
	 *Not used in this application: only processSeq.<BR>
	*/
	public JIPImage processImg(JIPImage img) throws JIPException {
		throw new JIPException("Please, select Apply to complete sequence");
	}

	/**
	  *It computes the eigenspace of the sequence and the projections.<BR>
	  */

	public JIPSequence processSeq(JIPSequence seq) throws JIPException {
		for (int i=0; i<seq.getNumFrames(); i++) {
			ImageType t = seq.getFrame(i).getType();
			if (t != ImageType.BYTE && t != ImageType.FLOAT && t != ImageType.SHORT) 
				throw new JIPException("Pca can not be applied to this image format: some of the image in the sequence is one of an invalid type");
		}
		
		// Output File, where image size, mean and eigenspace is stored
		String ficSalida =  getParamValueString("BD");
		// Percentage
		double perc = getParamValueFloat("perc");
		
		// First, get the number of input views (samples): M
		int M = seq.getNumFrames();

		//Get image dimensions (all views with equal dimensions)
		int w = seq.getFrame(0).getWidth(); // width
		int h = seq.getFrame(0).getHeight(); // height
		int N = w * h; // Image dimension: N

		/************** Get the vectorized samples set *****************/
		// Each vectorized sample is a column in the samples matrix X
		DenseDoubleMatrix2D X = new DenseDoubleMatrix2D(N, M);
		
        // Each row in X
		int n = 0;
		// For each column in X
		for (int m = 0; m < M; m++) {
			// For each pixel in the sample
			for (int y = 0; y < h; y++)
				for (int x = 0; x < w; x++)  
					// Build its associated column in X
					X.set(n++, m, ((JIPImgBitmap)seq.getFrame(m)).getPixel(0, x, y));
			n = 0;
		}
		
		/************** Get the averaged view (prototype): P ***********/
		// Initialize its associated vector (with Colt it is automatically zeroed
		DenseDoubleMatrix1D P = new DenseDoubleMatrix1D(N);

		// Get the sum of all samples in X
		for (int m = 0; m < M; m++)
			for (n = 0; n < N; n++)
				P.set(n, P.get(n) + X.get(n, m));

		// Normalize each component
		for (n = 0; n < N; n++)
			P.set(n, P.get(n) / M);
		
		/******************* Center the samples wrt P *******************/
		// Subtract P to each sample in X.
		for (int m = 0; m < M; m++)
			for (n = 0; n < N; n++)
				X.set(n, m, X.get(n, m) - P.get(n));
		
		/******************* Build the Covariance Matrix ****************/
		// Transpose X
		DoubleMatrix2D Xt = X.viewDice();

		// Matrix D = XtX (covariance matrix is Q = XXt)
		Algebra alg = new Algebra();
		DoubleMatrix2D D = alg.mult(Xt,X);
		
		/******************* Obtain the eigenspace **********************/

		// SVD decomposition of D
		SingularValueDecomposition svd = new SingularValueDecomposition(D);
		
		// In this case, the eigenvalues are in the diagonal 
		double[] eigenValues = svd.getSingularValues();
		
		// Eigenvectors of D are the columns of V
		DoubleMatrix2D V = svd.getV();
		
		// First M eigenvectors of the covariance matrix Q are given by XV divided by 
		// the corresponding eigenvalue 
		//1)Divide each column of V (with dimension NxN) by the corresponding eigenvalue
		for (int m = 0; m<M; m++)
			for (int p = 0; p<M; p++) 
				V.set(m,p, V.get(m,p)/Math.sqrt(eigenValues[p]));
		
		// Then multiply X and the corrected V. Such a multiplication is of order NxMxMxM = NXM
		DoubleMatrix2D EQ = alg.mult(X,V);
		
		/******************* Store the eigenspace **********************/
        // Store the eigenvectors in aditional bands
        // They are scaled for visualization purposes only
        // First Eigenvectors of Q are in the columns of EQ
		JIPSequence result = new JIPSequence();
		JIPImgBitmap output = (JIPImgBitmap)JIPImage.newImage(M + 2, w, h, ImageType.SHORT);
		

		
		/************** Calculamos "k" *****************/
		//Eleccion de "k" en base al porcentaje especificado
		double acumulado = 0.0;
		double sumaNValores = 0.0;
		double sumaKValores = 0.0;
		int k = 0;
		
		//Sumamos los N eigenvalores
		for(int m=0; m<M; m++)
			sumaNValores += eigenValues[m];
		
		//Vamos incrementando "k" hasta que suepere o iguale el porcentaje
		for(int i=0; i < M && acumulado < perc; i++){
			sumaKValores += eigenValues[i];
			acumulado = sumaKValores / sumaNValores;
			k = i+1;
		}

		try{
			DataOutputStream outFichero = new DataOutputStream (new FileOutputStream(ficSalida));
			
			// Stores the number of samples
			outFichero.writeInt(M);
			outFichero.writeInt(k);
			
			// Stores the mean
			for (n = 0; n < N; n++)
				outFichero.writeDouble(P.get(n));
			
			double aux[] = new double[N];
			double aux2[] = new double[N]; 
			double val = 0.0; 
			for (int m = 0; m < k; m++) {
				for (n = 0; n < N; n++) {
					val = EQ.get(n,m);
					
					// Store the eigenvectors in the output file
					outFichero.writeDouble(val);
					
					//aux y aux2 nos sirven para ir creando la imagen de salida
					//donde se representa el autoespacio creado
					aux2[n] = val;  
					aux[n] = val*65535 + 20000;
				}
				output.setAllPixels(m + 2, aux);
			}
			
			// Store the eigenValues
			for(int p=0; p<k; p++)
				outFichero.writeDouble(eigenValues[p]);
			
			outFichero.close();
		}catch(IOException ex){
			throw new JIPException("Some error using the output file: "+ex);
		}
		
		// Instead of storing the eigenvalues, we store in the first band
		// the relative importance of each eigenvalue, which is more useful 
		// for recognition purposes
		
		// Total variability
		double total = 0.0d;
		for (int i = 0; i < M; i++)
			total = total + eigenValues[i];
		// Store both the relative importance (first band) and prototype (second)
		int a = 0; 
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				// For eigenvalues consider only the first M (others are neligible)
				if (a < M)
					output.setPixel(0, x, y, (eigenValues[a] / total) * 65535);
				// For the prototype
				output.setPixel(1, x, y, ((P.get(a) + 600) / 1200) * 65535);
				a++;
			}
		}
		
		/******************* Store the eigenspace **********************/
		result.addFrame(output);
		result.setName(seq.getName());
		return result;
	}
}
