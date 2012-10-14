package spanglish;

import java.lang.Math;
import java.util.Random;
import java.util.ArrayList;

public class GeneticAlg {
	
	//////////////////////////////////// CLASS VARIABLES
	
	// get the maximum number of items and elements, initialized in the create people function in the Model class
	public static int			maxItems;
	public static int			numElements;
	
	//initialize Random class
	public static Random		rng				= new Random();
	
	// the model instance
	public static Model			model;
	
	// sets the mutation rate to a class variable, currently constant but can become variable
	public static double		mutationRate = .004;
	
	// sets the threshold past which the number of times an item is heard does not affect the outcome of the GA, it is
	// described more in depth in the clacMicroFitnessFunction
	public static int			threshold;
	//////////////////////////////////// INSTANCE VARIABLES
	
	// the person whose lexicon is being changed
	public Person		person;
	
	// the lexicon of the person in which the changes are taking place
	public Lexicon		lexicon;
	
	// holds all of the person's lexicons, in case there are several items with the same index
	public ArrayList<Lexicon> lexicons = new ArrayList<Lexicon>();
	
	// records the Temporary Memory Buffer of the listener
	public TempLexicon	tempLexicon;		// relevent temp lexicon of listener
	
	// holds the fitness variables which are all doubles in between 0 and 1
	public double		microFitness, macroFitness, fitness;
	
	// the heard item that has the potential to be combined with an already existing item in the lexicon
	public int[]		item;
	
	// the item already in the listeners lexicon that will eventually be combined with item
	public int[]		changedItem; 
	
	// the result of the combination between item and changed item
	public int[]		newItem;
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * just takes in the listener. the speaker and item, or possibly just the item can be set later
	 */
	public GeneticAlg( Person p ) {
		// sets the input person as the listener
		person = p;
		
		// adds the first language to the ArrayList of lexicons
		lexicons.add( p.getFirstLang() );
		
		// if the person has a second language, then it gets added to the ArrayList of lexicons
		if ( p.getSecondLang() != null )
			lexicons.add( p.getSecondLang() );
		
		// sets the tempLexicon as the person's temporary memory buffer
		tempLexicon = person.getTempMemBuffer();
		
		// initialize both microFitness and macroFitness to 0
		microFitness = macroFitness = 0;
	}
	
	/**
	 * takes in the listener, the speaker and the item under consideration. this is currently ben
	 */
	public GeneticAlg( Person p, int[] heardItem ) {
		// sets the input person as the listener
		person = p;
		
		// if the person has a second language, then it gets added to the ArrayList of lexicons
		lexicons.add( p.getFirstLang() );
		
		// if the person has a second language, then it gets added to the ArrayList of lexicons
		if ( p.getSecondLang() != null )
			lexicons.add( p.getSecondLang() );
		
		// sets the tempLexicon as the person's temporary memory buffer
		tempLexicon = person.getTempMemBuffer();
		
		// initialize both microFitness and macroFitness to 0
		microFitness = macroFitness = 0;
		
		// set the heard item as the input int array
		item = heardItem;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Methods
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * calcInsertion
	 * 
	 * method that is called in the Person class when the GA is called. performs the crossover and mutation, and
	 * also calculates both the micro and macro fitness
	 */
	public void calcInsertion() {
		
		// method that creates the newItem. more details below
		crossOverItem();
		
		// calculates the microFitness. more details below
		calcMicroFitness();
		
		// calculates the macroFitness. more details below
		calcMacroFitness();
		
		// if there is a candidate for crossing over, then we mutate the item. more details below
		if ( newItem != null && newItem.length > 0 )
			mutate();
		
		// overall fitness is the macro and micro fitness multiplied against each other
		fitness = microFitness * macroFitness;

		// with probability of the fitness, insert the new item into the lexicon
		if ( rng.nextDouble() < fitness ) {
			// if the changedItem is in the first lexicon, then replace it there
			// don't change the item if it is USAS
			if ( person.getFirstLang().inLexicon( changedItem ) >= 0 && !isUSAS( changedItem ) ) {
				person.replaceInflectionInFirstLang( changedItem , newItem );
			}
			
			// otherwise, replace it in the second language if it isn't in the first language
			else if ( person.getSecondLang() != null && person.getSecondLang().inLexicon( changedItem ) >= 0 ) {
				person.replaceInflectionInSecondLang( changedItem , newItem );
			}
		}
	}
	
	
	/**
	 * crossOverItem
	 * 
	 * if the person already has that particular item in their lexicon, then they choose one of those items and
	 * they cross it over with the heard item. if the person has multiple similar items, then the GA acts on 
	 * whichever lexicon that the chosen word was from 
	 */
	public void crossOverItem() {
		// initialize an ArrayList of potential items to crossover with
		ArrayList<int[]> potentialItems = new ArrayList<int[]>();
		
		// first take note of the index of the heard item
		int itemIndex = item[0];
		
		// go through the first and possibly second language
		for ( Lexicon lex : lexicons ) {
			
			// add all of the items with the matching item index
			if (lex.getItemsFromLexiconWithIndex( itemIndex ) != null )
				potentialItems.addAll(lex.getItemsFromLexiconWithIndex( itemIndex ) );
		}
		
		// end the method if there are no items with the same index as the heard item
		if ( potentialItems == null || potentialItems.size() == 0 )
			return;
		
		// if there is only one matching item, then choose that one
		else if ( potentialItems.size() == 1 )
			changedItem = potentialItems.get( 0 );
		
		// if there is more than one matching item, then choose randomly between them
		else {
			int numItems = potentialItems.size();
			changedItem = potentialItems.get( rng.nextInt( numItems ) );
		}
		
		// find which lexicon the "changedItem" is in
		for ( Lexicon lex : lexicons ) {
			if( lex.inLexicon( changedItem ) > -1 )
				lexicon = lex;
		}
		
		// initialize the newItem
		newItem = new int[numElements];
		
		// the first bit in newItem has to have the same index as the heard item
		newItem[0] = itemIndex;
		
		// the newItem is of the same language of the pre-existing item that was just chosen
		newItem[1] = changedItem[1];
		
		// iterating through every bit in newItem
		for ( int i = 2 ; i < numElements ; ++i ) {
			
			// the bit comes from the pre-exisiting item with a 50% probability
			if ( rng.nextDouble() < .5 )
				newItem[i] = changedItem[i];
			// and comes from the heard item otherwise
			else
				newItem[i] = item[i];
		}
	}
	
	/**
	 * calcMicroFitness
	 *
	 * takes into account the item number, the number of times they've heard it, 
	 * they're openness to new words based on characteristics
	 */
	public void calcMicroFitness() {
		
		// get the number of times the item was heard
		int timesHeard;
		
		// if the item has been heard before, that is, it is in the temporary memory buffer
		if ( tempLexicon != null && tempLexicon.heardItem( item ) >= 0 ) {
			
			// note that the item has been heard again in the tempLexicon
			person.addToTempMemBuffer( item );
			
			// get the number of times that the specific item has been heard up to this point
			timesHeard = tempLexicon.getBit( tempLexicon.heardItem( item ), 0 );
		}
		else {
			// otherwise just add it to temporary memory buffer
			person.addToTempMemBuffer( item );
			// and note that it has only been heard once so far
			timesHeard = 1;
		}
		
		// if the item has been heard more times than threshold, then it does not hinder the microFitness
		if ( timesHeard >= threshold )
			microFitness = 1;
		
		// otherwise, the microFitness is initialized to the ratio of the number of times the item was heard to
		// the threshold, all put to some power. The higher the power, the more i
		else {
			microFitness = Math.pow( timesHeard / (double)threshold, 3.0);
		}
		
		
		// the lower the item number, the better chance it will get accepted
		microFitness *= Math.pow( ( maxItems - (double)item[0] ) / maxItems, 1.0 );
		
		// see the calcOpenness function for details
		microFitness *= calcOpenness();
	}
	
	public static int getThreshold() {
		return threshold;
	}

	public static void setThreshold(int threshold) {
		GeneticAlg.threshold = threshold;
	}

	/**
	 * calcMacroFitness
	 * 
	 * macrofitness of a word is just the percentage of people in the population who have
	 * the heard item in their first lexicon
	 */
	public void calcMacroFitness() {
		// calls the calcFreqOfItem method in the Model class which calculates the percentage of the entire
		// population, excluding Teachers and BabySitters, that has that item in their first language and sets
		// that as the macroFitness
		macroFitness = model.calcFreqOfItem( item );
		
		
		if ( person.getRace() != Model.LATINO ) {
			macroFitness = model.calcFreqOfItemInRace( item, person.getRace() );
		}
		else {
			macroFitness = ( model.calcFreqOfItemInRace( item, Model.LATINO ) + model.calcFreqOfItemInRace(item, Model.BLACK ) ) / 2;
		}
	}
	
	/**
	 * mutate
	 * 
	 * iterates through every bit after the first two in the already crossed over item and with
	 * probability mutationRate, the bit will switch from a 0 to a 1 or vise versa
	 */
	public void mutate() {
		// iterating through every bit of the newItem after the second
		for ( int i = 2 ; i < numElements ; ++i ) {
			// with probability mutationRate
			if ( rng.nextDouble() < mutationRate ) {
				// if the bit is one, then it switches to zero
				if ( newItem[i] == 1 )
					newItem[i] = 0;
				// otherwise, if the bit is zero, it switches to one
				else
					newItem[i] = 1;
			}
		}
	}
	
	/**
	 * calcOpenness
	 *
	 * Given the listeners traits, they will be more or less likely to accept a crossed over item. openness
	 * decreases with age, latinos have the most openness and whites have the least, and women are more
	 * open than men
	 */
	public double calcOpenness() {
		// initialize the openness at one
		double openness = 1;
		
		// if the listener is a child, they are the most open
		if ( person.getAgeClass() == Model.CHILD ) {
			openness *= 3.0;
		}
		// if the listener is a student, then they are slightly less open
		else if ( person.getAgeClass() == Model.STUDENT ) {
			// if they are 8 or older, then their openness decreases with age
			if ( person.getAge() >= 8 ) {
				openness *= Math.pow( ( 8.0 / person.getAge() ), 1.0 );
			}
			openness *= 10.0;
		}
		// if the listener is an adult, then they are the least open
		else if ( person.getAgeClass() == Model.ADULT ) {
			openness *= 2;
		}
		
		// men are less open than women
		if ( person.getGender() == Model.MALE ) {
			openness *= .8;
		}
		
		// Latinos are the most open
		if ( person.getRace() == Model.LATINO ) {
			openness *= 4.0;
		}
		// Blacks are slightly less open
		else if ( person.getRace() == Model.BLACK ) {
			openness *= 1.5;
		}
		// White are the least open
		else if ( person.getRace() == Model.WHITE ) {
			openness *= .8;
		}
		return openness;
	}
	
	
	/**
	 * isUSAS
	 * 
	 * returns true if the item in question is afro-spanish 
	 * i.e., takes the form (_ 1 0 1)
	 */
	public boolean isUSAS( int[] item ) {
		
		// return true if the input meets the proper qualifications
		if ( item[1] == 1 && item[2] == 0 && item[3] == 1 )
			return true;
		// return false otherwise
		else
			return false;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS AND SETTERS
	////////////////////////////////////////////////////////////////////////////////////////

	public static int getNumElements() {
		return numElements;
	}
	public static void setNumElements(int numElements) {
		GeneticAlg.numElements = numElements;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person p) {
		this.person = p;
	}
	public static Model getModel() {
		return model;
	}
	public static void setModel(Model model) {
		GeneticAlg.model = model;
	}
	public int[] getItem() {
		return item;
	}
	public void setItem(int[] item) {
		this.item = item;
	}
	public static int getMaxItems() {
		return maxItems;
	}
	public static void setMaxItems(int maxItems) {
		GeneticAlg.maxItems = maxItems;
	}
}