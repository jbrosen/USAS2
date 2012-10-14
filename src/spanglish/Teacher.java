package spanglish;

import java.awt.Color;

public class Teacher extends Person {
	
	
	////////////////////////CLASS VARIABLES
	/*
	 * Teachers have a set distribution of traits
	 */
	public static double probAnglo = .8;
	public static double probFemale = .7;
	public static double probUpperClass = .6;
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * constructor called an initialization. sets random age between 22 and 64, and sets other statistics
	 * based on a prior distribution. gives the teachers a strict afro/anglo first lexicon, sets color yellow
	 */
	public Teacher() {
		
		// the Teacher is white with probAnglo probability
		if ( rng.nextDouble() < probAnglo )
			race = Model.WHITE;
		// otherwise, the Teacher is Black
		else
			race = Model.BLACK;
		
		// the Teacher is female with probFemale probability
		if ( rng.nextDouble() < probFemale )
			gender = Model.FEMALE;
		// otherwise, the Teacher is male
		else
			gender = Model.MALE;
		
		// the Teacher is upper class with probUpperClass probability
		if ( rng.nextDouble() < probUpperClass )
			socialClass = Model.UPPERCLASS;
		// otherwise they are middle class, which is classified as working poor
		else
			socialClass = Model.MIDDLECLASS;
		
		// when randomly assigned, the Teacher's age is between 22 and 63
		age = rng.nextInt( 42 ) + 22;
		// all Teachers are adults
		ageClass = Model.ADULT;
		// Teachers have the highest possible status
		status = 40;
		
		// all Teachers have an English lexicon
		// see the Person class for more details on this method
		createEnglishLexicon();
		
		// set Teacher color yellow
		setColor( Color.yellow );
		
		// Teachers can occupy both the school and work areas
		regions.add( school );
		regions.add( work );
	}
	
	/**
	 * called when another teacher retires. clones a different person and copies all of their
	 * qualities into that of a teacher. 
	 */
	public Teacher( Person p ) {
		
		/*
		 * Gets the race, gender, social class and both languages of the input Person and
		 * makes a Teacher with those traits
		 */
		race = p.getRace();
		gender = p.getGender();
		socialClass = p.getSocialClass();
		firstLang = p.getFirstLang();
		secondLang = p.getSecondLang();
		
		// Teachers have the highest possible status
		status = 40;
		
		// new Teachers have an age of 22
		age = 22;

		// set Teacher color yellow
		setColor( Color.yellow );
		
		// Teachers can occupy both the school and work areas
		regions.add( school );
		regions.add( work );
	}
	
	/**
	 * step
	 * 
	 * the lexicons of a teacher do not change, so their step method is empty. they move using
	 * the move method in the Person class
	 */
	@Override
	public void step() {
	}
	
	/**
	 * updateStats
	 * 
	 * teachers have a constant status
	 */
	@Override
	public void updateStats() {
		status = 40;
		
	}
	
	/**
	 * isFertile
	 * 
	 * Teachers don't reproduce, they can only be initialized or cloned from another Person,
	 * thus they are never fertile
	 */
	@Override
	public boolean isFertile() {
		return false;
	}
	
	/**
	 * retire
	 * 
	 * teachers retire at age 65
	 */
	@Override
	public boolean retire() {
		// Teachers are retired if they are 65 or older
		if ( age > 64 )
			return true;
		// otherwise, they are not retired
		else
			return false;
	}
	
	/**
	 * die
	 * 
	 * teachers don't die, they just retire. overrides the method in the Person class
	 */
	@Override
	public boolean die() {
		return false;
	}

}
