package spanglish;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Region {
	//////////////////////// CLASS VARIABLES
	
	// sets the Random class
	public static Random rng = new Random();
	
	// sets the grid that the Region is associated with
	public static TorusWorld grid;
	
	
	//////////////////////// INSTANCE VARIABLES
	
	// Coordinates containing region
	public int xMin;
	public int xMax;
	public int yMin;
	public int yMax;
	
	// Width and height of the region
	public int width;
	public int height;
	
	// the size of the TorusWorld that the Region is associated with
	public int xSize, ySize;
	
	
	////////////////////////// Constructors
	
	/**
	 * Main Constructor. takes in the x and y range of the Region
	 */
	public Region(int x1, int x2, int y1, int y2 ) {
		
		// records the size of the grid that the Region class is associated with
		xSize = grid.getSizeX();
		ySize = grid.getSizeY();
		
		// record the boundaries of the Region
		xMin = x1;
		xMax = x2;
		yMin = y1;
		yMax = y2;
		
		// set constraints on the placement of the regions to avoid out of bounds exceptions
		if ( xMin < 0 )
			xMin = 0;
		if ( yMin < 0 )
			yMin = 0;
		if ( xMax >= grid.getSizeX() )
			xMax = grid.getSizeX() - 1;
		if ( yMax >= grid.getSizeY() )
			yMax = grid.getSizeY();
		
		// calculate the actual size of the Region
		width = xMax - xMin +1;
		height = yMax - yMin +1;
	}
	
	
	/**
	 * getRandomPoint
	 * 
	 * returns a random Point that is within the Region
	 */
	public Point getRandomPoint() {
		
		// get a random x and y coordinate within the Region
		int x = rng.nextInt( width ) + xMin;
		int y = rng.nextInt( height ) + yMin;
		
		// Construct a Point object with the specified coordinates
		Point p = new Point( x, y );
		
		return p;
	}
	
	
	
	/**
	 * inRegion
	 * 
	 * Returns a boolean value that denotes whether an ObjectInGrid, Point or
	 * coordinate set are within the region
	 */
	
	/////////////////////// given an ObjectInGrid
	public boolean inRegion( ObjectInGrid o ) {
		
		// get the coordinates of the Object
		double x = o.getX();
		double y = o.getY();
		
		// return false if the coordinates are outside of the Region
		if ( x < xMin || x > xMax || y < yMin || y > yMax )
			return false;
		// return true otherwise
		else
			return true;
	}
	
	////////////////////// given a point
	public boolean inRegion( Point p ) {
		
		// get the coordinates of the Point
		double x = p.getX();
		double y = p.getY();
		
		// return false if the coordinates are outside of the Region
		if ( x < xMin || x > xMax || y < yMin || y > yMax )
			return false;
		// return true otherwise
		else
			return true;
		
	}
	
	////////////////////// given coordinates
	public boolean inRegion( int x, int y ) {
		
		// return false if the coordinates are outside of the Region
		if ( x < xMin || x > xMax || y < yMin || y > yMax )
			return false;
		// return true otherwise
		else
			return true;
	}
	
	/**
	 * getObjectsInRegion
	 * 
	 * returns an Person ArrayList with all of the people that are in the region
	 */
	
	public ArrayList<Person> getPeopleInRegion() {
		
		// Initialize point array
		ArrayList<Person> personArray = new ArrayList<Person>();
		
		// iterate through all of the grid points
		for ( int i = xMin ; i <= xMax ; ++i ) {
			for ( int j = yMin ; j <= yMax ; ++j ) {
				// if there is a Person at the specified location, then add it to personArray
				if ( grid.getPersonAt( i, j ) != null)
					personArray.add( grid.getPersonAt( i, j ) );
			}
		}
		return personArray;
	}
	
	/**
	 * Finds the number of objects in the region and returns whether or not the region is full of people
	 * The threshold for isFull is lower than the actual amount of cells, but this is to prevent against
	 * overpopulation issues.
	 */

	public boolean isFull() {
		
		// if there are more than ( height - 3) * ( width - 3 ) people in the region, then return true
		if ( getPeopleInRegion().size() >= ( height - 3) * ( width - 3 ) )
			return true;
		// otherwise, return false
		else
			return false;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS AND SETTERS
	////////////////////////////////////////////////////////////////////////////////////////
	
	
	public static Random getRng() {
		return rng;
	}
	public static void setRng(Random rng) {
		Region.rng = rng;
	}
	public static GridWorld getGrid() {
		return grid;
	}
	public static void setGrid(TorusWorld grid) {
		Region.grid = grid;
	}
	public int getxMin() {
		return xMin;
	}
	public void setxMin(int xMin) {
		this.xMin = xMin;
	}
	public int getxMax() {
		return xMax;
	}
	public void setxMax(int xMax) {
		this.xMax = xMax;
	}
	public int getyMin() {
		return yMin;
	}
	public void setyMin(int yMin) {
		this.yMin = yMin;
	}
	public int getyMax() {
		return yMax;
	}
	public void setyMax(int yMax) {
		this.yMax = yMax;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
