package javavis.jip3d.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JProgressBar;

public class FunctionPanel implements ActionListener,Runnable {

	private static final long serialVersionUID = -8036902089475720573L;

	public boolean confirmed = true;

	Function3D func;
	JProgressBar progress_bar;
	MyCanvas3D canvas;

	public FunctionPanel (MyCanvas3D can)
	{
		func = null;
		canvas = can;
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals("Cancel")) {
			confirmed = false;
			func.stop();
			return;
		}

	}

	public void setFunction(Function3D func, JProgressBar jpb)
	{
		this.func = func;
		progress_bar = jpb;
	}

	private Thread blinker;

	public void start()
	{
		blinker = new Thread(this);
		blinker.start();
	}


	public void run() {

		try
		{
			while(func.progress<100)
			{
				func.getThread().join(150);
				progress_bar.setValue((int)func.progress);
			}
			//wait for the actual end of the function
			func.getThread().join();
		}catch (InterruptedException e)
		{}
		canvas.functionEnded(func);
		func = null;
	}
}
