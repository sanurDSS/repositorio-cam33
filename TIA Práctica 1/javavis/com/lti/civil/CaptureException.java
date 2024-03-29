/*
 * Created on May 25, 2005
 */
package com.lti.civil;

/**
 * 
 * @author Ken Larson
 */
public class CaptureException extends Exception
{
	private static final long serialVersionUID = 9115471648559905726L;

	private int errorCode;
	
	public CaptureException()
	{
		super();
		
	}

	public CaptureException(String message, final int errorCode, Throwable cause)
	{
		super(message + ": " + errorCode, cause);
		this.errorCode = errorCode;
	}

	public CaptureException(String message, final int errorCode)
	{
		super(message + ": " + errorCode);
		this.errorCode = errorCode;
		
	}

	public CaptureException(Throwable cause)
	{
		super(cause);
		
	}

	public int getErrorCode()
	{
		return errorCode;
	}

}
