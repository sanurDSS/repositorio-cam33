package javavis;

import java.io.File;

import javavis.base.JIPException;
import javavis.base.JIPToolkit;
import javavis.jip2d.base.JIPSequence;


/**
	 * It groups several JIP files in a single file.	 
	 * This class is called from command line. 
	 * Use: java JoinIntoFrame dest.jip file1.jip file2.jip ...;
   */
public class JoinIntoFile {

	/**
	 * 	Methods which has the main of the classes (To run in commands line)
	 * @param Imagenes First argument: destination Remained: images to include
	 */
	public static void main(String[] args) {
		// Check the input arguments
		if (args.length < 2)
			error("Incorrect arguments. We need two files at least.");
		if (args[0].equals("-help"))
			help();
		JIPSequence secuencia = new JIPSequence(), aux = null;
		File fich;

		for (int f = 1; f < args.length - 1; f++) {
			fich = new File(args[f]);
			if (!fich.isFile()) {
				System.err.println("File '" + args[f]
						+ "' is not a file or does not exist. Avoiding it.");
			}
			aux = JIPToolkit.getSeqFromFile(args[f]);
			try {
				secuencia.appendSequence(aux);
			}catch (JIPException e){System.out.println("JoinIntoFile: "+e);}
		}
		secuencia.setName("Several File");

		// Save the sequence
		JIPToolkit.saveSeqIntoFile(secuencia, args[0]);
		System.exit(0);

	}

	/**
	 * 	It shows an error message
	 */
	static void error(String mensaje) {
		System.err.println(mensaje);
		System.exit(0);
	}

	/**
	 * 	It shows in screen the command help
	 */
	static void help() {
		System.out.println("JoinIntoFile: Join in a single file several JIP images.");
		System.out.println("Use: java JoinIntoFile dest.jip file1.jip file2.jip ...");
		System.exit(0);
	}
}
