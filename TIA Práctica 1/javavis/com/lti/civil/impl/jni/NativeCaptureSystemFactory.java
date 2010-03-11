/*
 * Created on May 25, 2005
 */
package com.lti.civil.impl.jni;

import com.lti.civil.CaptureException;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;

/**
 * 
 * @author Ken Larson
 */
public class NativeCaptureSystemFactory implements CaptureSystemFactory
{

	public CaptureSystem createCaptureSystem() throws CaptureException
	{
		
		String libcivil=new String();
		String so = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		if(so.contains("inux")){
			if(arch.contains("x86_64") || arch.contains("amd64"))
				libcivil = System.getProperty("user.dir")+"/lib/lti-civil/linux-amd64/libcivil.so";
			else
				libcivil = System.getProperty("user.dir")+"/lib/lti-civil/linux-x86/libcivil.so";
		}else
			if(so.contains("indows"))
				libcivil = System.getProperty("user.dir")+"\\lib\\lti-civil\\win32-x86\\civil.dll";
		
		try
		{
			java.lang.System.load(libcivil);
		}
		catch (UnsatisfiedLinkError e)
		{	
			try{ java.lang.System.loadLibrary("civil"); } 
			catch(UnsatisfiedLinkError e2){
				System.err.println("There is a problem loading the external library "+
						"'civil'. The OS detected is "+so+", the architecture is "+
						arch+" and the library path is "+ libcivil +
						" . Correct them if you think they may be wrong. "+ 
						"The 'civil' library has linux-x86, linuxamd64, win32-x86 and MacOS"+
						"universal implementations.");
				throw new CaptureException(e);
			}
		}
		
		return newCaptureSystemObj();
	}
	private static native CaptureSystem newCaptureSystemObj();
	
}
