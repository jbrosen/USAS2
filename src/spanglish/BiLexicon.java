package spanglish;

/**
 * BiLexicon.java
 * 
 * A subclass of Lexicon, has all of the same methods as Lexicon with a few overridden methods.
 * Allows for two languages to occupy one lexicon. This is mainly for American born Latinos who
 * develop this lexicon after they turn 6. Their jumbled first lexicon is converted to a 
 * BiLexicon, where the items get sorted by language and is not full until it has every item
 * from each language
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uchicago.src.sim.util.SimUtilities;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// A BiLexicon is almost identical to a lexicon, except it contains the maximum amount of items from two different languages. These
	// lexicons emerge when a Latino child is born in the model and has heard enough English and Spanish to be considered billigual. Instead
	// of having a second language, they have only a first language but it contains items from both languages

public class BiLexicon extends Lexicon {
	
	////////////////////// Class Variables
	
	// there are twice as many lexicon items in a BiLexicon as there are in a Lexicon
	public static int maxItemsBi = maxItems * 2;
	
	////////////////////// Instance Variables
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * creates an empty BiLexicon
	 */
	public BiLexicon() {
		// initialize empty ArrayList of lexicon items
		lexicon = new ArrayList<int[]>();
	}
	
	/**
	 * creates a BiLexicon with a single item, taken from the input
	 */
	public BiLexicon( int[] newItem ) {
		// initialize empty ArrayList of lexicon items
		lexicon = new ArrayList<int[]>();
		// add the one heard item into the lexicon
		lexicon.add( newItem );
	}
	 
	/**
	 * creates a BiLexicon with a pre-specified ArrayList of integer arrays
	 */
	public BiLexicon( ArrayList<int[]> newLang ) {
		// the matrix of items is now the input ArrayList of the constructor
		lexicon = newLang;
	}
	
	/**
	 * most commonly used, creates a BiLexicon from an already existing Lexicon
	 */
	public BiLexicon( Lexicon lex ) {
		// the matrix of items is now the ArrayList of the input lexicon
		lexicon = lex.getLexicon();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Methods
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * addItem
	 * 
	 * similar to that in the Lexicon class, but allows for two items with the same index to be added as
	 * long as they are of different languages. returns if the input is the wrong size or the lexicon is full
	 */
	@Override
	public boolean addItem( int[] item ) {
		// return false if the item is the wrong size
		if ( item.length != numElements )
			return false;
		
		// return false if there is no space
		if ( lexicon.size() >= maxItemsBi )	{
			return false;
		}
		
		// iterate through every item in the lexicon
		for ( int i = 0 ; i < lexicon.size(); ++i )
			// if one of the existing items has both the same index, and is of the same language, then the method returns false and 
			// the item is not inserted. We can check this by looking at the first two bits in the items
			if ( lexicon.get( i )[0] == item[0] && lexicon.get( i )[1] == item[1] ) {
				return false;
			}
		
		// if there is not another item with the same index and language, then we add the item
		lexicon.add( item );
		
		// sort the lexicon based on the comparator described below
		Collections.sort( lexicon, 
		  (java.util.Comparator<? super int[]>) new BiLexiconComparator() );
		
		return true;
	}
	
	
	/**
	 * getItemsFromLexicon
	 * 
	 * given a number of items as input, this method returns an ArrayList of numItems random items from the lexicon
	 */
	@Override
	public ArrayList<int[]> getRandomItemsFromLexicon( int numItems ) {
		// return null if the lexicon has no items in it
		if ( numItems < 1 )
			return null;
		
		// return the entire lexicon if numItems is greater than or equal to the number of items already in the lexicon
		if ( numItems > lexicon.size() )
			return lexicon;
		
		// if there are more items in the lexicon than in the input, initialize a new ArrayList
		ArrayList<int[]> tempArray = new ArrayList<int[]>();
		
		// randomly shuffle the existing ArrayList of lexicon items
		SimUtilities.shuffle( lexicon, uchicago.src.sim.util.Random.uniform );
		
		// draw the first numItems items from the randomly shuffled lexicon
		for ( int i = 0 ; i < numItems ; ++i )
			tempArray.add( lexicon.get( i ) );
		
		// resort the lexicon with the BiLexicon Comparator described below
		Collections.sort( lexicon, 
				  (java.util.Comparator<? super int[]>) new BiLexiconComparator() );
		return tempArray;
	}
	
	/**
	 * isFull
	 * 
	 * a BiLexicon is full when there are twice the amount of maxItems because it contains
	 * two different languages, as described above
	 */
	@Override
	public boolean isFull() {
		// the lexicon is not full if there are less than maxItemsBi items
		if( lexicon.size() < maxItemsBi)
			return false;
		
		// the lexicon is full otherwise
		else
			return true;
	}
	
	/**
	 * swapItem
	 * 
	 * this method searched for the first input, existingItem, in the lexicon. if it is found, then it
	 * replaces that item with newItem. this differs from the function it overrides only in the Comparator
	 */
	@Override
	public boolean swapItem( int[] existingItem, int[] newItem ) {
		// return false if existingItem is not in the lexicon
		if ( inLexicon( existingItem ) < 0 )
			return false;
		
		// inLexicon returns the index of the existingItem, so replace the item with that index
		replaceItem( inLexicon( existingItem ), newItem );
		
		// resorts the lexicon in case the replacement messed something up
		Collections.sort( lexicon, 
				  (java.util.Comparator<? super int[]>) new BiLexiconComparator() );
		return true;
	}
	
	/**
	 * swapInflection
	 * 
	 * if it exists, finds existingItem in the lexicon and replaces all bits except the first two
	 * with those of newItem, different Comparator than Lexicon class
	 */
	@Override
	public boolean swapInflection( int[] existingItem, int[] newItem ) {
		
		// gets the index that the existingItem is in, it equals -1 if the item is not in there
		int itemIndex = inLexicon( existingItem );
		
		// return false if the existingItem is not in the lexicon
		if ( itemIndex < 0 )
			return false;
		
		// iterate through every bit after the second one at the specified location and replace it with the bits
		// from newItem
		for ( int i = 2 ;  i < numElements ; ++i )
			lexicon.get( itemIndex )[i] = newItem[i];

		// resorts the list using the comparator described below
		Collections.sort( lexicon, 
				  (java.util.Comparator<? super int[]>) new BiLexiconComparator() );
		
		return true;
	}
	
	/**
	 * replaceInflection
	 * 
	 * replaces all bits of the item at existingItemIndex except the index and language, then sorts
	 * returns if the newItem is invalid or existingItemIndex is too large, different Comparator
	 * than Lexicon class
	 */
	
	@Override
	public void replaceInflection( int existingItemIndex, int[] newItem ) {
		
		// iterate through every bit in the specified item after the second and replaces it with
		// those in newItem
		for ( int j = 2 ; j < numElements ; ++j )
			lexicon.get( existingItemIndex )[j] = newItem[j];
		
		// resorts the list using the comparator described below
		Collections.sort( lexicon, 
				  (java.util.Comparator<? super int[]>) new BiLexiconComparator() );
		
		return;
	}
	
	@Override
	public void print() {
		// iterate through every item
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			// for every bit
			for ( int j = 0 ; j < numElements ; ++j ) {
				// print the digit and then a space
				System.out.printf( "%d ",lexicon.get(i)[j] );
			}
			// print a line break after every item
			System.out.print("\n");
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Comparator
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * First sorts the items by their language and then by their item index so it is easier to interpret
	 */
	private class BiLexiconComparator implements Comparator<int[]> {
		
		@Override
		public int compare ( int[] item1, int[] item2 ) {
			
			// the integer we are comparing take into account the language first, and then the index number,
			// thus, we assign a higher priority to the second bit in the item (the language) 
			// and a lower priority to the first (the index)
			int num1 = ( ( maxItems + 1 ) * item1[1] ) + item1[0];
			int num2 = ( ( maxItems + 1 ) * item2[1] ) + item2[0];
			
			// num2 smaller, so should be before d1 (ascending order)
			if ( num1 > num2 )
				return 1;
			else if ( num1 < num2 )
				return -1;
			return 0;
		}
	}
	
	
}
