package javavis.jip2d.util;

import javax.swing.JScrollPane;

import javavis.base.JIPException;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.gui.Canvas2D;

/**
*Capture a image from a webcam using the WebCam.dll.
*/
public class WebCam implements Runnable {
	static {
		System.loadLibrary("WebCam");		
	}

	/**
	 */
	private int[] image;
	
	/**
	 */
	private int[] red;
	
	/**
	 */
	private int[] blue;
	
	/**
	 */
	private int[] green;
	
	private int numSequences;
	private int numFrames;	
	private int size;
	private int height;
	private int width;	
	private int sleeping;
	private int numFunction;	
	private JScrollPane canvasScr;
	private Canvas2D canvas;
	private static boolean alive;
	JIPFunction func;
	
	/**
	*It constructs a WebCam with JScrollPane, Canvas2D start with the 
	*received values as parameters.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>cs: JScrollPane to refresh the screen.<BR>
	*<li>c: Canvas2D where it puts the image.<BR>	
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Object of the webcam class.<BR><BR>
	*</ul>
	*/
	public WebCam(JScrollPane cs, Canvas2D c)
	{
		image = null;
		red = null;
		green = null;
		blue = null;
		func = null;
		size = 0;
		numFunction = 0;
		numFrames = 0;
		numSequences = 0;
		sleeping = 1;
		height = 640;
		width = 480;		
		canvas = c;
		canvasScr = cs;
		alive = true;
	}
	
	/**
	*It constructs a WebCam with JScrollPane, Canvas2D, width and height start 
	*with the received values as parameters.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>cs: JScrollPane to refresh the screen.<BR>
	*<li>c: Canvas2D where it puts the image.<BR>
	*<li>w: The width of the image.<BR><BR>
	*<li>h: The height of the image.<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Object of the webcam class.<BR><BR>
	*</ul>
	*/
	public WebCam(JScrollPane cs, Canvas2D c, int w, int h)
	{
		image = null;
		red = null;
		green = null;
		blue = null;
		func = null;
		size = 0;
		numFrames = 0;
		numFunction = 0;
		numSequences = 0;
		sleeping = 1;
		height = h;
		width = w;		
		canvas = c;
		canvasScr = cs;
		alive = true;
	}
	
	/**
	*It constructs a WebCam without its values are started.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Object of the webcam class whith its members has default values.<BR><BR>
	*</ul>
	*/
	public WebCam()
	{
		image = null;
		red = null;
		green = null;
		blue = null;
		func = null;
		size = 0;
		numFrames = 0;
		numFunction = 0;
		numSequences = 0;
		sleeping = 1;
		height = 640;
		width = 480;		
		canvas = null;
		canvasScr = null;
		alive = true;
	}
	
	/**
	*This method calls a native method that initialize the WebCam.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>	
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>NONE<BR><BR>
	*</ul>
	*/
	private native void start();
	
	/**
	*It activates the webcam in order to capture images.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>	
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>NONE<BR><BR>
	*</ul>
	*/
	private native void activate();
	
	/**
	*It closes the camera to stop the images capture.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>	
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>NONE<BR><BR>
	*</ul>
	*/
	private native void finish();
	
	/**
	*It destroys the WebCam object.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>NONE<BR><BR>
	*</ul>
	*/
	private native void destroy();
	
	/**
	*It captures one image from a web cam.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Array with an output image (values RGB separates).<BR><BR>
	*</ul>
	*/
	private native int[] captureImage();
	
	/**
	*It captures an image but it do not close the web cam in order to capture
	*another image.<BR>
	*<ul><B>Input parameters:</B><BR>	
	*<li>w: The width of the image.<BR><BR>
	*<li>h: The height of the image.<BR><BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>Array with an output image (values RGB separates).<BR><BR>
	*</ul>
	*/
	private native int[] captureContinuousImage(int h, int w);
	
	/**
	*<B>Native method getWidth</B><BR>
	*It obtains the width of the image.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>	
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>The width of the image.<BR><BR>
	*</ul>
	*/
	private native int getWidth();
	
	/**
	*<B>Native method getHeight</B><BR>
	*It obtains the height of the image.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>	
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>The height of the image.<BR><BR>
	*</ul>
	*/
	private native int getHeight();	
	
	/**
	*<B>Turn Image</B><BR>
	*It turns an image and puts the coordenates in bottom-left.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>	
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>NONE<BR><BR>
	*</ul>
	*/
	private void turnImage()
	{
		for(int i = 0, n = height-1; i < n; i++, n--)
		{		
			for(int j = 0; j < width; j++)
			{
				int k = i*width + j;
				int m = n*width + j;				
				
				int aux = red[k];
				red[k] = red[m];
				red[m] = aux;
				
				int aux2 = green[k];
				green[k] = green[m];
				green[m] = aux2;
				
				int aux3 = blue[k];
				blue[k] = blue[m];
				blue[m] = aux3;				
			}
		}
	}		
	
	/**
	*<B>Split Image</B><BR>
	*It separates the image in the three components red, green and blue.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>NONE<BR><BR>
	*</ul>
	*/
	private void splitImage()
	{
		red = new int[size/3];
		green = new int[size/3];
		blue = new int[size/3];
		for( int i = 0, j = 0; i < size-3; i = i + 3, j++ ) {								
			blue[j] = image[i];
			green[j] = image[i+1];
			red[j] = image[i+2];
		}
	}
	
	/**
	*<B>InitializeWebCam</B><BR>
	*It calls to a native methodes in order to initializate the camara's 
	*parameters.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>	
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>NONE<BR><BR>
	*</ul>
	*/
	public void initializeWebCam() {			
		start();	
		activate();
	}
	
	/**
	*<B>Finish WebCam</B><BR>
	*It calls a native methodes for finish the image captures.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>	
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>NONE<BR><BR>
	*</ul>
	*/
	public void finishWebCam() {
		finish();
		destroy();
	}
	
	/**
	*<B>Capture One Image</B><BR>
	*It calls a native method and captures only one image. The native method 
	*starts, initializes and finishes the webcam's capture.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>	
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>The captured image in JIPImage format.<BR><BR>
	*</ul>
	*/
	public JIPImage captureOneImage() {
		this.setImage(this.captureImage());					
		splitImage();		
		width = getWidth();
		height = getHeight();
		
		turnImage();
		
		//TODO: Solucionar esto 
		/*JIPImage jipimag = new JIPBmpColor(width, height, red, green, blue);
				
		return jipimag;*/
		return null;
	}	
	
	public void setImage(int[] img)
	{
		size = img.length;
		image = new int[size];
		image = img;		
	}
	
	public int getSize()
	{
		return size;
	}	
	
	public void setAlive(boolean a)
	{
		alive = a;
	}
	
	public void setWidth(int w)
	{
		width = w;
	}
	
	public void setHeight(int h)
	{
		height = h;
	}
	
	public void setNumberSequences(int ns)
	{
		System.out.println("setsequences="+ns);
		numSequences = ns;
	}
	
	public void setNumberFrames(int nf)
	{
		numFrames = nf;
		sleeping = 1000 / numFrames;
	}
	
	public void setNumberFuction(int nf)
	{
		numFunction = nf;
	}
	
	public void setFunction(JIPFunction jf)
	{
		func = jf;
	}
	
	/**
	*<B>Run</B><BR>
	*This methods is always capturing one image until the user stops it. It 
	*shows the image in the screen.<BR>
	*<ul><B>Input parameters:</B><BR>
	*<li>NONE<BR>
	*</ul>
	*<ul><B>Output parameters:</B><BR>
	*<li>NONE<BR><BR>
	*</ul>
	*/
	public void run()
	{			
		NewWindow window = new NewWindow(); //New windows to show a real image
		if( numFunction != 0 ) //Only show the window if we have to apply a function
			window.setSize(width, height);				
		
		int count = 0; //Count the number of images captured.
		JIPSequence jseqtotal = new JIPSequence();
		
		while( alive ) //While the thread is alive
		{			
			setImage(captureContinuousImage(height, width)); //Capture an image						
			splitImage(); //Split the image					
			turnImage(); //Turn the image
			// TODO: solucionar esto
			/*
			JIPImage jipimag = new JIPImage(width, height, red, green, blue); //Construct a JIPImage
			JIPSequence jseq = new JIPSequence(jipimag); //Get a JIPSequence
			JIPImage jipfunc = new JIPImage(jipimag); //JIPImage for a function
			JIPSequence jseqfunc = new JIPSequence(jipfunc); //JIPSequence for a function*/
			JIPImage jipimag = null;
			JIPSequence jseq = null;
			JIPImage jipfunc = null;
			JIPSequence jseqfunc=null;
			
			if( numFunction != 0 ) //We apply a function
			{
				try 
				{
					jipfunc = func.processImg(jipimag); //Process the image
					jseqfunc = new JIPSequence(jipfunc); //Obtains a sequence
					if( numSequences == 0 ) //We have not to save the sequence
					{						
						//Show the real image and the other image.
						canvas.setSequence(jseqfunc); 
						canvas.changeFrame(jipfunc);
						canvasScr.repaint();
						window.repaint();
						window.setImage(jipimag);
						window.setVisible(true);
					}
				}
				catch( JIPException jex )
				{
					System.err.println(jex.getMessage());
				}
			}
			else //We do not apply a function
			{
				if( numSequences == 0 ) //Do not save a sequence
				{				
					//Show the image
					try {
						canvas.setSequence(jseq);			
						canvas.changeFrame(jipimag);	
					}catch (JIPException e) {System.out.println("WebCam.run: "+e);}
					canvasScr.repaint();
				}
			}
			
			count++; //Add a sequence	
			
			try {
			//Option for obtain a sequence of images.
			if( numSequences > 0 ) {
				if( count <= numSequences ) { //We have not finished.
					if( numFunction != 0 ) {//We apply a function.
						jseqtotal.addFrame(jipfunc);
						canvas.setSequence(jseqfunc);
					}
					else {//No function.
						jseqtotal.addFrame(jipimag);
						canvas.setSequence(jseq);
					}
				}
				else {//We have finished.
					alive = false;										
					canvas.addFrames(jseqtotal);
					canvasScr.repaint();
				}
			}
			}catch (JIPException e) {System.out.println("WebCam: "+e);}
			
			try {				
				Thread.sleep(sleeping);
			}
			catch( InterruptedException iex) {
				System.err.println(iex.getMessage());
			}			
		}
		finishWebCam(); //Disconnects the webcam
	}
	
}
