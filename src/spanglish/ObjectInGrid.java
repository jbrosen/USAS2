package spanglish;

import java.util.ArrayList;

public interface ObjectInGrid {

	public int getX();
	public void setX( int i );
	public int getY();
	public void setY( int i );
	public ArrayList<Region> getRegions();
	public void setRegions( ArrayList<Region> r );
}
