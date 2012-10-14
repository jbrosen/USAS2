package spanglish;

import java.awt.Color;

public class BabySitter extends Person {
	
	
	////////////////////////CLASS VARIABLES
	

	/*
	 * BabySitters have a set distribution of demographics
	 */
	public static double probBlack = .45;
	public static double probLatino = .45;
	public static double probMiddleClass = .3;
	
	
	/**
	 * constructor called at initialization. creates BabySitters with certain proportional traits
	 */
	public BabySitter() {
		// draw a uniform random number
		double tempRand = rng.nextDouble();
		
		// the BabySitter is latino with probLatino probability
		if ( tempRand < probLatino )
			race = Model.LATINO;
		// black with blackProbab probability
		else if ( tempRand < probLatino + probBlack )
			race = Model.BLACK;
		// white otherwise
		else
			race = Model.WHITE;
	
		// all BabySitters are female
		gender = Model.FEMALE;
		
		// working poor with probMiddleClass
		if ( rng.nextDouble() < probMiddleClass )
			socialClass = Model.MIDDLECLASS;
		// poor otherwise
		else
			socialClass = Model.LOWERCLASS;
		
		// all BabySitters are within the ages 22 and 63, contructor randomizes the age
		age = rng.nextInt( 42 ) + 22;
		// all BabySitters are adults
		ageClass = Model.ADULT;
		// BabySitters have the highest possible status
		status = 40;
		
		// if the BabySitter is latino, give them a spanish lexicon
		// see Person class for details
		if ( race == Model.LATINO )
			createSpanishLexicon();
		// otherwise, create an english lexicon
		// also see Person class for details
		else
			createEnglishLexicon();
		
		// set BabySitter color cyan
		setColor( Color.cyan);
		
		// the only region BabySitters are in are the home
		regions.add( home );
	}
	
	/**
	 * constructor that is called inside of the model. creates a BabySitter with the same traits as a given person
	 * and sets their age to be 22
	 */
	public BabySitter( Person p ) {
		
		/*
		 * Gets the race, gender, social class and both languages of the input person and
		 * makes a BabySitter with those traits
		 */
		race = p.getRace();
		gender = p.getGender();
		socialClass = p.getSocialClass();
		firstLang = p.getFirstLang();
		secondLang = p.getSecondLang();
		
		// set age to 22, they are a new BabySitter
		age = 22;
		
		// BabySitters get the highest possible status
		status = 40;
		
		// BabySitters are cyan
		setColor( Color.cyan);
		
		// the only region BabySitters are in are the home
		regions.add( home );
	}
	
	
	/**
	 * step
	 * 
	 * the lexicons of a BabySitter do not change, so their step method is empty. they move using
	 * the move method in the Person class.
	 */
	@Override
	public void step() {
	}
	
	/**
	 * updateStats
	 * 
	 * BabySitters have a constant status, overrides the method in the Person class
	 */
	@Override
	public void updateStats() {
		status = 40;
	}
	
	/**
	 * isFertile
	 * 
	 * BabySitters don't reproduce, so they are never fertile. they can only be initialized
	 * or cloned from an existing person
	 */
	@Override
	public boolean isFertile() {
		return false;
	}
	
	/**
	 * retire
	 * 
	 * BabySitters retire at age 65. when a BabySitter "retires", she is removed from the model and a new
	 * BabySitter is cloned from an existing person
	 */
	@Override
	public boolean retire() {
		
		// they are retired if they are older than 64
		if ( age > 64 )
			return true;
		// not retired otherwise
		else
			return false;
	}
	
	/**
	 * die
	 * 
	 * BabySitters don't die, they just retire. overrides the method in the person class
	 */
	@Override
	public boolean die() {
		return false;
	}
}
