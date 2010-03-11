/*
 * Created on May 25, 2005
 */
package com.lti.civil.impl.jni;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lti.civil.CaptureException;
import com.lti.civil.CaptureObserver;
import com.lti.civil.CaptureStream;
import com.lti.civil.VideoFormat;

/**
 * 
 * @author Ken Larson
 */
public class NativeCaptureStream extends Peered implements CaptureStream
{
	private static final Logger logger = Logger.global;

	private NativeCaptureStreamThread thread;
	public NativeCaptureStream(long ptr)
	{
		super(ptr);

	}

	public synchronized native List<VideoFormat> enumVideoFormats() throws CaptureException;

	public synchronized native void setVideoFormat(VideoFormat f) throws CaptureException;

	public synchronized native VideoFormat getVideoFormat() throws CaptureException;

	public synchronized native void setObserver(CaptureObserver observer);
	
	private boolean started;
	
	public synchronized void start() throws CaptureException
	{
		if (started)
			return;
		
		if (thread == null)
		{
			thread = new NativeCaptureStreamThread();
			thread.setName("NativeCaptureStreamThread " + getPeerPtr());
			thread.start();	// TODO: when to stop?
		}
		
		nativeStart();
		
		started = true;
	}
	
	public synchronized void stop() throws CaptureException
	{
		if (!started)
			return;
		nativeStop();
		started = false;
	}
	
	public synchronized void dispose() throws CaptureException
	{	
		nativeDispose(); //TODO BOYAN: add a timeout to this external jni-ed function.
		if (thread != null)
		{	try
			{
				thread.join(5000);
			} catch (InterruptedException e)
			{
				logger.log(Level.WARNING, "" + e, e);
			}
		}
	}
	
	private synchronized native void nativeStart() throws CaptureException;
	private synchronized native void nativeStop() throws CaptureException;
	private synchronized native void nativeDispose() throws CaptureException;
	/** This is called from a new thread.  This allows us to provide threading from Java, rather than implement it in C++. Thread should terminate on dispose.*/
	public native void threadMain();
	
	class NativeCaptureStreamThread extends Thread
	{
		public void run()
		{	
			logger.fine("NativeCaptureStreamThread running");
			
			try
			{
				threadMain();
			}
			catch (Throwable t)
			{	logger.log(Level.SEVERE, "" + t, t);
			}
			logger.fine("NativeCaptureStreamThread exiting");
			
		}
	}
}
