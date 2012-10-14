package spanglish;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import uchicago.src.sim.space.Object2DGrid;

	public class GridWorld extends Object2DGrid {
	
	
	/////////////////// Class Variables
	
	// initialize the Random class
	public static Random rng = new Random();
	
	
	/////////////////// Instance Variables
	
	// the GridWorld must be connected to a Model instance
	public Model theModel;
	

	/**
	 * 
	 * constructor just takes in the size of the grid and the model that its connected to
	 */
	public GridWorld(int sizeX, int sizeY, Model aModel) {
		// Constructor from Object2DGrid, just takes in the size of the Grid
		super(sizeX, sizeY);
		
		// GridWorld must take in the Model instance that the world is connected to
		theModel = aModel;
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Person Methods
	////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * getPersonAt
	 * 
	 * similar to the getObjectAt method, except returns null if the object is not an instance of a Person.
	 * this is convenient for referencing people because it casts the Object as a Person
	 */
	public Person getPersonAt( int x, int y ) {
		// if the Object at ( x, y ) is a Person, then return the person
		if ( getObjectAt(x, y) instanceof Person )
			return (Person)getObjectAt(x, y);
		// otherwise, return null
		else
			return null;
	}
	
	/**
	 * moveObjectTo
	 * 
	 * takes in a person and an ( x, y ) coordinate as inputs and attempts to move them there
	 * returns true if the move is successful and false if the new space is already occupied or there
	 * is an out of bounds exception
	 */
	public boolean movePersonTo( Person person, int newX, int newY) {
		
		// return false if the coordinate is out of bounds
		if ( newX < 0 || newY < 0 || newX >= xSize || newY >= ySize ) {
			return false;
		}

		// also return false if the new coordinate is already occupied by another Person
		if ( getPersonAt(newX, newY) != null ) {
			return false;
		}

		// if everything checks out, remove the Person from their current location
		putObjectAt( person.getX(), person.getY(), null);
		
		// then put the Person at ( newX, newY ) in the grid
		putObjectAt(newX, newY, person);
		
		// set the new x and y coordinates for the Person
		person.setX(newX);
		person.setY(newY);
		
		return true;
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Region Methods
	////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * moveObjectInRegion
	 * 
	 * moves the inputed person one grid cell in a random direction, as long as the new
	 * grid cell is in one of its regions
	 */
	public boolean moveObjectInRegion( Person person ) {
		
		// first get the current x and y coordinates of the input Person
		int currentX = person.getX();
		int currentY = person.getY();
		
		// get an ArrayList of Points that contain open neighbors
		ArrayList<Point> openNgh = getOpenNeighborLocations( currentX, currentY );
		
		// return false if their are no open neighbor locations
		if ( openNgh == null || openNgh.size() == 0 )
			return false;
		

		// otherwise, get a random neighbor point
		int dx = rng.nextInt(3) - 1;
		int dy = rng.nextInt(3) - 1;
		
		// if the new chosen point is not in the Person's region, then randomly try another point
		while( !person.inRegion( currentX + dx, currentY + dy ) ) {
			dx = rng.nextInt(3) - 1;
			dy = rng.nextInt(3) - 1;
		}
		
		// once a neighbor point in the proper region is found, set the new coordinates
		int newX = currentX + dx;
		int newY = currentY + dy;
		

		// return false if the new chosen point is not in the grid
		if ( newX < 0 || newY < 0 || newX >= xSize || newY >= ySize ) {
			return false;
		}

		// return false if the new chosen point is already occupied by a person
		if ( getPersonAt(newX, newY) != null ) {
			return false;
		}

		// if everything checks out, remove the Person from their current location
		putObjectAt(currentX, currentY, null);
		
		// then put the Person at ( newX, newY ) in the grid
		putObjectAt(newX, newY, person);
		
		// set the new x and y coordinates for the Person
		person.setX(newX);
		person.setY(newY);
		return true;
	}
	
	/**
	 * moveObjectToRegion
	 * 
	 * Given a region and a Person, places the person randomly inside of Region r
	 */
	public void movePersonToRegion( Person person, Region r ) {
		
		// terminate the method if the region is full
		if ( r.isFull() )
			return;
		
		// otherwise, get a random point in the region and record the coordinates
		Point p = r.getRandomPoint();
		int x = (int)p.getX();
		int y = (int)p.getY();
		
		// while the move was not successful, pick a new point in the Region and try to move there
		while ( !movePersonTo( person, x, y ) ) {
			p = r.getRandomPoint();
			x = (int)p.getX();
			y = (int)p.getY();
		}
	}
	
	/**
	 * movePersonToRegion
	 * 
	 * takes in a Person and iterates through all of their Regions and places them randomly inside of one of them
	 */
	public void movePersonToRegion( Person person ) {
		
		// get a random point in one of the Person's regions and record the x and y coordinates
		// see the Person class for more details on the method
		Point p = person.getRandomRegionPoint();
		
		// record the x and y coordinates of the point as integers
		int x = (int)p.getX();
		int y = (int)p.getY();
		
		// initialize a boolean variable to check if all of the regions are full
		boolean isFull = true;
		
		// for every Region in the person's region list
		for ( Region r : person.getRegions() ) {
			// if the Region is not full, then set isFull false
			if ( !r.isFull() )
				isFull = false;
		}
		
		// isFull will only be true at this point is all of the Person's regions are full, in which case the
		// method terminates
		if ( isFull )
			return;
		
		// while the random region point is occupied, pick a new point and try to move them there
		while ( !movePersonTo( person, x, y ) ) {
			p = person.getRandomRegionPoint();
			x = (int)p.getX();
			y = (int)p.getY();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Neighbor Methods
	////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * getOpenNeighborLocations
	 * 
	 * given an ( x, y ) pair, we iterate over all of the neighboring points and returns a list of all
	 * of the Points that do not have a Person on them already
	 */
	public ArrayList<Point> getOpenNeighborLocations(int x, int y) {
		
		// initialize an ArrayList of Points
		ArrayList<Point> ptList = new ArrayList<Point>();

		// if the input coordinates are on a border, we make sure that we do not get an out of bounds exception
		int minX = Math.max(0, x - 1);
		int maxX = Math.min(x + 1, xSize - 1);
		int minY = Math.max(0, y - 1);
		int maxY = Math.min(y + 1, ySize - 1);

		// iterating through every neighboring point
		for (int ty = minY; ty <= maxY; ++ty) {
			for (int tx = minX; tx <= maxX; ++tx) {
				// if there is no Person already at the Point
				if (getPersonAt(tx, ty) == null) {
					
					// Construct the open point
					Point p = new Point(tx, ty);
					
					// and add it to the ArrayList
					ptList.add(p);
				}
			}
		}

		return ptList;
	}
	
	/**
	 * getNeighbors()
	 * 
	 * Gets all neighbors within a one grid radius, returns null if there are no neighbors
	 */
	public ArrayList<Person> getNeighbors( Person p ) {
		
		// initialize an ArrayList of Person
		ArrayList<Person> objList = new ArrayList<Person>();
		
		// if the input coordinates are on a border, we make sure that we do not get an out of bounds exception
		int minX = Math.max( 0, p.getX() - 1 );
		int maxX = Math.min( p.getX()  + 1, xSize - 1 );
		int minY = Math.max( 0, p.getY()  - 1 );
		int maxY = Math.min( p.getY()  + 1, ySize - 1 );
		
		// iterate through all of the neighboring points
		for (int ty = minY; ty <= maxY; ++ty) {
			for (int tx = minX; tx <= maxX; ++tx) {
				// if there is a Person on that point and the Person is not the inputed Person
				if (getPersonAt( tx, ty ) != null && getPersonAt( tx, ty ) != p ) {
					
					// Get the Person at the specified coordinates
					Person obj1 = (Person)getObjectAt( tx, ty );
					
					// add the Person to the list
					objList.add( obj1 );
				}
			}
		}
		return objList;
	}
	
	
	/**
	 * testGetOpenNeighborLocations
	 * 
	 * @param x
	 * @param y
	 *            for testing...call from Model buildModel, step, etc.
	 */
	public void testGetOpenNeighborLocations(int x, int y) {

		System.out.printf("--- Test:  open pts around %d,%d:", x, y);

		ArrayList<Point> nborPts = getOpenNeighborLocations(x, y);

		for (Point p : nborPts) {
			int px = (int) p.getX();
			int py = (int) p.getY();
			System.out.printf(" %d,%d", px, py);
		}
		System.out.printf("\n");

	}

}
