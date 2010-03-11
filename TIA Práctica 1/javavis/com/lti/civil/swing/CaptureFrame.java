package com.lti.civil.swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Properties;

import javavis.Commons;
import javavis.Gui;
import javavis.base.Dialog;
import javavis.base.JIPToolkit;
import javavis.jip2d.base.JIPImage;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.lti.civil.CaptureDeviceInfo;
import com.lti.civil.CaptureException;
import com.lti.civil.CaptureObserver;
import com.lti.civil.CaptureStream;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;
import com.lti.civil.VideoFormat;
import com.lti.civil.awt.AWTImageConverter;

/**
 * Simple GUI to display captured video.
 * @author Ken Larson
 *
 */
public class CaptureFrame extends ImageFrame
{

	private static final long serialVersionUID = 3273589706822074954L;

	
	public static void main(String[] args) throws CaptureException
	{
		new CaptureFrame(DefaultCaptureSystemFactorySingleton.instance(),null,null).run();
		
	}
	
	private CaptureSystem system;
	private CaptureStream captureStream;
	private final CaptureSystemFactory factory;
	private volatile boolean disposing = false;
	private volatile boolean savetheimage = false;
	private int numberOfImage = 0;
	private Gui parentGui; // for disabling/enabling the main Gui
	private Properties prop;
	
	
	public CaptureFrame(CaptureSystemFactory factory, Gui parentGui, Properties prop)
	{	
		super("Webcam");
		this.parentGui = parentGui;
		this.prop = prop;
		this.factory = factory;
	}
	
	public void run() throws CaptureException
	{
		
		initCapture();
		
		if(captureStream==null){
			throw new CaptureException("In CaptureFrame, after initCapture(), captureStream is null",-1);
		}
		
		this.setLayout(new FlowLayout());
		JButton button = new JButton(Commons.getIcon("photocamera.png"));
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				savetheimage = true;
			}
	    });
		button.setSize(captureStream.getVideoFormat().getWidth(), 50);
		super.add(button);
		
		setSize(captureStream.getVideoFormat().getWidth(), captureStream.getVideoFormat().getHeight());
		setLocation(200, 200);
		
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				try
				{
					disposeCapture();
				} catch (CaptureException e1)
				{
					e1.printStackTrace();
				}
				if(parentGui != null){
					parentGui.setEnabled(true);
					parentGui.setFocusableWindowState(true);
				}
			}
		});

		
		super.setAlwaysOnTop(true);
		if(parentGui !=null){
			parentGui.setEnabled(false);
			parentGui.setFocusableWindowState(false);
		}

		setVisible(true);
		pack();
		
		startCapture();
	}
	

	
	public void initCapture() throws CaptureException
	{
		int ndevice=0;
		system = factory.createCaptureSystem();
		system.init();
		List list = system.getCaptureDeviceInfoList();
		if(list.size()==0){
			new Dialog(parentGui).information(prop.getProperty("NoCapturingDevices"),prop.getProperty("Error"));
			return;
		}else{
			Object[] possibilities = new Object[list.size()];
			if(list.size()>1){
				for(int i=0;i<list.size();i++){
					CaptureDeviceInfo info = (CaptureDeviceInfo) list.get(i);
					possibilities[i]= ""+i+":"+info.getDescription();
				}
				String selected = (String)JOptionPane.showInputDialog(parentGui,prop.getProperty("MultipleDevicesFound"),
						prop.getProperty("SelectADevice"),JOptionPane.QUESTION_MESSAGE,null,possibilities,possibilities[possibilities.length-1]);
				if(selected!=null)
					ndevice = (int)selected.charAt(0) - (int)'0';
				else{
					return;
				}
			}
		}
		CaptureDeviceInfo info = (CaptureDeviceInfo) list.get(ndevice);
		captureStream = system.openCaptureDeviceStream(info.getDeviceID());
		captureStream.setObserver(new MyCaptureObserver());
			
		
	}
	
	public void startCapture() throws CaptureException
	{
		captureStream.start();
	}

	public void disposeCapture() throws CaptureException
	{
		disposing = true;
		
		if (captureStream != null)
		{
			captureStream.stop();
			captureStream.dispose();
			captureStream = null;
		}
		
		if (system != null)
			system.dispose();
	}

	
	class MyCaptureObserver implements CaptureObserver
	{

		public void onError(CaptureStream sender, CaptureException e)
		{	
			e.printStackTrace();
		}


		public void onNewImage(CaptureStream sender, com.lti.civil.Image image)
		{	
			if (disposing)
				return;
			try
			{
				setImage(AWTImageConverter.toBufferedImage(image));
			}
			catch (Throwable t)
			{	t.printStackTrace();
			}
			if (savetheimage){
				final BufferedImage bimg;
				try
				{
					final VideoFormat format = image.getFormat();
					String ftype;
					switch (format.getFormatType())
					{
						case VideoFormat.RGB24:
							ftype= "RGB24"; break;
						case VideoFormat.RGB32:
							ftype= "RGB32"; break;
						default:
							ftype="image";
					}
					bimg = AWTImageConverter.toBufferedImage(image);
					JIPImage jipimg = JIPToolkit.getColorImage(bimg);
					jipimg.setName("wcam"+numberOfImage+"("+ftype+")");
					parentGui.getG2d().addJIPImageToSequence(jipimg);
				}
				catch (Exception e)
				{	e.printStackTrace();
					return;
				}
				/* //Save a jpeg file
				try
				{
					FileOutputStream fos = new FileOutputStream("out.jpg");
					JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(fos);
					jpeg.encode(bimg);
					fos.close();
				}
				catch (Exception e)
				{	e.printStackTrace();
				}*/
				savetheimage = false;
				numberOfImage++;
			}
		}
		
	}
}
