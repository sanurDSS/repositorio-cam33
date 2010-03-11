package javavis.jip3d.functions;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Raster;
import javax.media.j3d.Transform3D;

import javavis.base.Function3DGroup;
import javavis.base.JIPException;
import javavis.base.ParamType;
import javavis.jip3d.geom.MyTransform;
import javavis.jip3d.geom.MyTransform3D;
import javavis.jip3d.gui.Function3D;
import javavis.jip3d.gui.FunctionParam;
import javavis.jip3d.gui.MyCanvas3D;
import javavis.jip3d.gui.ScreenData;
import javavis.jip3d.gui.ScreenOptions;
import javavis.jip3d.gui.dataobjects.Trajectory;

/**
 * Class FGrabVideo. This function is used for grabbing a sequence of images at different points of view.
 * A Trajectory3D must be declared and this function grabs a picture of the scene at each pose in the
 * trajectory.
 * @author dviejo
 *
 */
//TODO Let the user decide the image output format
public class FGrabVideo extends Function3D {

	public FGrabVideo() {
		super();
		this.allowed_input = ScreenOptions.tTRAJ3D;
		this.group = Function3DGroup.Others;

		FunctionParam p1 = new FunctionParam("Output File Name", ParamType.STRING);
		p1.setValue("Output");

		FunctionParam p2 = new FunctionParam("Output Path", ParamType.DIR);

		addParam(p1);
		addParam(p2);
	}

	@Override
	public void proccessData(ScreenData scr_data) throws JIPException {
		String filename = paramValueString("Output File Name");
		String path = paramValueString("Output Path");
		int num_poses, cont;
		Trajectory traj;
		File outputFile;
		String nextFileName;
		MyTransform tr_global = new MyTransform3D();
		MyTransform tr_actual;
		Transform3D transform;

		traj = (Trajectory) scr_data;
		num_poses = traj.files.size();

		for(cont=0;cont<num_poses;cont++)
		{
			nextFileName = getNextFileName(path+"/"+filename, cont);
			tr_actual = new MyTransform3D(traj.transforms.get(cont));
			tr_actual.applyTransform(tr_global);
			tr_global = tr_actual;
			transform = new Transform3D(tr_global.getMatrix4d());

			this.getCanvas().TGVista.setTransform(transform);

			outputFile = new File(nextFileName);
			try {
				ImageIO.write(getSnapShot(getCanvas()), "PNG", outputFile);
				Thread.sleep(750); //this is to ensure the scene has been repainted after transformation
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method obtains a snapshot from the canvas3D. It grabs the scene just as it
	 * is watched by the user
	 * @param canvas3d
	 * @return
	 */
	public BufferedImage getSnapShot(MyCanvas3D canvas3d)
	{
		GraphicsContext3D ctx = canvas3d.getGraphicsContext3D();
		Dimension scrDim = canvas3d.getSize();

		// setting raster component
		Raster ras =
			new javax.media.j3d.Raster(
			new javax.vecmath.Point3f(-1.0f, -1.0f, -1.0f),
			javax.media.j3d.Raster.RASTER_COLOR,
			0,
			0,
			scrDim.width,
			scrDim.height,
			new javax.media.j3d.ImageComponent2D(
			javax.media.j3d.ImageComponent.FORMAT_RGB,
			new java.awt.image.BufferedImage(scrDim.width, scrDim.height, java.awt.image.BufferedImage.TYPE_INT_RGB)),
			null);

		ctx.readRaster(ras);
		BufferedImage img = ras.getImage().getImage();
		return img;
	}

	/**
	 * This method is used for obtaining a new file name in a sequence. It merges a file
	 * root plus a sequence number [0,999], plus an image file extension.
	 * @param root File name root
	 * @param cont Sequence number
	 * @return Complete file name
	 */
	public String getNextFileName(String root, int cont)
	{
		String ret;
		if(cont<10)
			ret = root + "00" + cont;
		else if(cont<100)
			ret = root + "0" + cont;
		else ret = root + cont;
		return ret + ".png";
	}


}
