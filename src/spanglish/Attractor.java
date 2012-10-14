package spanglish;

public class Attractor extends Person {
	
	
	
	
	
	
	
	public Attractor( int r ) {
		super( r );
		// all attractors are between 12 and 25
		age = rng.nextInt( 14 ) + 12;
	}
	
	
	/**
	 * updateAge
	 * 
	 * attractor don't age
	 */
	@Override
	public void updateAge() {
	}
	
	/**
	 * step
	 * 
	 * the lexicons of a Attractors do not change, so their step method is empty. they move using
	 * the move method in the Person class.
	 */
	@Override
	public void step() {
	}
	
	/**
	 * updateStats
	 * 
	 * Attractors have a constant status, overrides the method in the Person class. They have a very high status
	 */
	@Override
	public void updateStats() {
		super.updateStats();
		status = 40;
	}
	
	/**
	 * isFertile
	 * 
	 * Attractors don't reproduce, so they are never fertile. they can only be initialized
	 * or cloned from an existing person
	 */
	@Override
	public boolean isFertile() {
		return false;
	}
	
	/**
	 * retire
	 * 
	 * Attractors never retire
	 */
	@Override
	public boolean retire() {
		return false;
	}
	
	/**
	 * die
	 * 
	 * Attractors don't die, they just retire. overrides the method in the person class
	 */
	@Override
	public boolean die() {
		return false;
	}
}
