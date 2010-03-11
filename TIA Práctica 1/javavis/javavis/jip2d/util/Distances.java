package javavis.jip2d.util;

/**
*It implements a class with auxiliar methods to work with
*distances in the FKMedias function.<BR>
 */
public class Distances {
	
	public double distance;
	public int group;
	public String nameImg;
	
	/**
	 */
	public Distances() {
		distance = 0.0;
		group = 0;
		nameImg = "";
	}
	

	/**
	 * @return Returns the distance.
	 */
	public double getDistance() {
		return distance;
	}
	/**
	 * @param distance The distance to set.
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
	/**
	 * @return Returns the group.
	 */
	public int getGroup() {
		return group;
	}
	/**
	 * @param group The group to set.
	 */
	public void setGroup(int group) {
		this.group = group;
	}
	/**
	 * @return Returns the nameImg.
	 */
	public String getNameImg() {
		return nameImg;
	}
	/**
	 * @param nameImg The nameImg to set.
	 */
	public void setNameImg(String nameImg) {
		this.nameImg = nameImg;
	}
	
	/**
	 * @param Distances
	 * copy one object to another except the String
	 */
	public void copyDis(Distances dis)
	{
		this.distance = dis.getDistance();
		this.group = dis.getGroup();
		this.nameImg = dis.getNameImg();
	}
	
	/**
	 * checks if the object is empty
	 */
	public boolean isEmpty() {
		if (distance == 0.0 && group == 0 && nameImg.equals(""))
			return true;
		else 
			return false;
	}
	
	/**
	 * compares if the objects are the same
	 */
	public boolean isEquals(Distances obj)
	{
		if(this.distance==obj.getDistance() && this.group==obj.getGroup() && this.nameImg.equals(obj.getNameImg()))
			return true;
		else
			return false;
	}
}
