package spanglish;

/**
 * Lexicon.java
 * 
 * It's main purpose it to hold a matrix that contains lexicon items as rows, with columns as elements
 * in the items. Most of the methods concern either getting specific items or bits from the ArrayList
 * of integer arrays, or changing certain aspects of the lexicon
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Random;
import uchicago.src.sim.util.SimUtilities;

public class Lexicon {
	
	//////////////////////// CLASS VARIABLES
	public static int			maxItems;		// sets maximum size of lexicon
	public static int			numElements;	// num of allocated bits in each item
	public static Random		rng;

	//////////////////////// INSTANCE VARIABLES
	
	public ArrayList<int[]>			lexicon;		// actual lexicon
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates an empty ArrayList for a lexicon
	 */
	public Lexicon() {
		
		// initialize the lexicon ArrayList
		lexicon = new ArrayList<int[]>();
	}
	
	/**
	 * Creates a lexicon with a single item. Used when a child is accumulating their first language or
	 * someone is building their second language
	 */
	public Lexicon( int[] newItem ) {
		
		// initialize the lexicon ArrayList
		lexicon = new ArrayList<int[]>();
		
		// add the input item
		lexicon.add( newItem );
	}
	
	/**
	 * Creates a lexicon with an ArrayList of items. Used when initializing a first language
	 */
	public Lexicon( ArrayList<int[]> newLang ) {
		
		// sets entire lexicon given an ArrayList of integer arrays
		lexicon = newLang;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Methods
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * addItem
	 * 
	 * Throws out of bounds inputs and returns false. Will also return false if an item with the same index is
	 * already in the lexicon. Otherwise, adds the item and resorts the ArrayList.
	 * is overrided by BiLexicon class
	 */
	public boolean addItem( int[] item ) {
		
		// return false if the item is the wrong size
		if ( item.length != numElements )
			return false;
		
		// return false if there is no space
		if ( lexicon.size() >= maxItems )	{
			return false;
		}
		
		// return false if there is already an item with the same index in the lexicon
		for ( int i = 0 ; i < lexicon.size(); ++i )
			if ( lexicon.get( i )[0] == item[0] )
				return false;
		
		// if everything checks out, add the item, re-sort the lexicon and update the size
		lexicon.add( item );
		
		Collections.sort( lexicon, 
		  (java.util.Comparator<? super int[]>) new LexiconComparator() );
		
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Retrieving Methods
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * getItem()
	 * 
	 * Given an index i, returns the i^th item, starting at i=0
	 * Returns null if index is out of bounds
	 */
	public int[] getItem( int i ) {
		
		// return null if the index is out of bounds
		if ( i < 0 || i >= lexicon.size() )
			return null;
		
		return lexicon.get( i );
	}
	
	/**
	 * getBit
	 * 
	 * gets the bit at the input item and element number
	 * returns -1 if the inputs are invalid
	 */
	public int getBit( int item, int element) {
		
		// return null if the index is out of bounds
		if ( item >= lexicon.size() || element >= numElements )
			return -1;
		
		return lexicon.get( item )[element];
	}
	
	
	
	/**
	 * getRandomItemsFromLexicon
	 * 
	 * given a positive integer numItems, returns numItems items chosen randomly from the lexicon by
	 * shuffling, picking the first numItems, and re-sorting
	 * if numItems is larger than the size of the lexicon, then returns the entire lexicon
	 * returns null if the input it non-positive 
	 */
	public ArrayList<int[]> getRandomItemsFromLexicon( int numItems ) {
		
		// return null if the number of items to be chosen is less than one
		if ( numItems < 1 )
			return null;
		
		// if the size of the input is greater than or equal to the number of items, return the whole list 
		if ( numItems >= lexicon.size() )
			return lexicon;
		
		// initialize an ArrayList of items
		ArrayList<int[]> tempArray = new ArrayList<int[]>();
		
		// shuffle the list of items
		SimUtilities.shuffle( lexicon, uchicago.src.sim.util.Random.uniform );
		
		// add the first numItems items to the tempArray
		for ( int i = 0 ; i < numItems ; ++i )
			tempArray.add( lexicon.get( i ) );
		
		// re-sort the lexicon
		Collections.sort( lexicon, 
				  (java.util.Comparator<? super int[]>) new LexiconComparator() );
		
		return tempArray;
	}
	
	/**
	 * getItemsFromLexiconWithIndex
	 * 
	 * given a specific item, denoted by its first bit, finds all of the items in the lexicon
	 * that share that same index and returns it as an ArrayList
	 */
	public ArrayList<int[]> getItemsFromLexiconWithIndex( int index ) {
		
		// returns null if the input is invalid
		if ( index < 0 || index >= maxItems )
			return null;
		
		// initialize an ArrayList of items
		ArrayList<int[]> tempArray = new ArrayList<int[]>();
		
		// iterates through every item in the lexicon
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			// if the item shares the same index as the input value, add that item to the list
			if ( lexicon.get(i)[0] == index )
				tempArray.add( lexicon.get( i ) );
		}
		
		return tempArray;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Replacing/Swapping Methods
	////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * replaceItem
	 * 
	 * replaces the item at index existingItemIndex with newItem, then sorts
	 * returns if the newItem is invalid or existingItemIndex is too large
	 */
	public void replaceItem( int existingItemIndex, int[] newItem ) {
		
		// returns if the inputs are invalid
		if ( newItem == null || newItem.length != numElements || existingItemIndex > lexicon.size() )
			return;
		
		// iterates through every bit of the item at the specified index and replaces it with newItem
		for ( int j = 0 ; j < numElements ; ++j )
			lexicon.get( existingItemIndex )[j] = newItem[j];
		
		// re-sorts the lexicon
		Collections.sort( lexicon, 
				  (java.util.Comparator<? super int[]>) new LexiconComparator() );
	}
	
	/**
	 * replaceInflection
	 *
	 * replaces all bits of the item at existingItemIndex except the index and language, then sorts
	 * returns if the newItem is invalid or existingItemIndex is too large
	 */
	public void replaceInflection( int existingItemIndex, int[] newItem ) {
		
		// returns if the inputs are invalid
		if ( newItem == null || newItem.length != numElements || existingItemIndex > lexicon.size() )
			return;
		
		// iterates through every bit after the second at the specified index and replaces it with those of newItem
		for ( int j = 2 ; j < numElements ; ++j )
			lexicon.get( existingItemIndex )[j] = newItem[j];
		
		// re-sorts the lexicon
		Collections.sort( lexicon, 
				  (java.util.Comparator<? super int[]>) new LexiconComparator() );
	}
	
	/**
	 * swapItem
	 * 
	 * attempts to replace existingItem in the lexicon with newItem, returns true if it is successful
	 * returns false if the inputs are invalid, or existingItem is not in the lexicon
	 */
	public boolean swapItem( int[] existingItem, int[] newItem ) {
		
		// records the index of the existingItem if it exists
		// see the inLexicon method for more details
		int itemIndex = inLexicon( existingItem );
		
		// return false if the item is not in the lexicon or if the other two inputs are invalid
		if ( itemIndex < 0 || existingItem.length != numElements || newItem.length != numElements )
			return false;
		
		// replace the item
		// see the replaceItem function for details
		replaceItem( itemIndex, newItem );
		
		return true;
	}
	
	/**
	 * swapInflection
	 * 
	 * if it exists, finds existingItem in the lexicon and replaces all bits except the first two
	 * with those of newItem. Used when the GA is called
	 */
	public boolean swapInflection( int[] existingItem, int[] newItem ) {
		
		// records the index of the existingItem if it exists
		// see the inLexicon method for details
		int itemIndex = inLexicon( existingItem );
		
		// return false if the item is not in the lexicon or if the other two inputs are invalid
		if ( itemIndex < 0 || existingItem.length != numElements || newItem.length != numElements )
			return false;
		
		// replace the inflection
		// see the replaceInflection method for details
		replaceInflection( itemIndex, newItem );
		
		return true;
	}
	
	/**
	 * swapBit
	 * 
	 * takes in an item index and an element number and replaces the bit with newElement
	 * returns if itemIndex or elementNum are out of bounds
	 */
	public void swapBit ( int itemIndex, int elementNum, int newElement) {
		
		// return if the inputs are invalid
		if ( itemIndex >= lexicon.size() || elementNum >= numElements )
			return;
		
		// replaces the bit with newElement
		lexicon.get( itemIndex )[elementNum] = newElement;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Checking Methods
	////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * itemsEqual
	 * 
	 * Takes in two items and checks if they are equal to each other. Returns true if they are
	 * returns false if inputs are out of bounds or they are not equal to each other
	 */
	public boolean itemsEqual( int[] item1, int[] item2 ) {
		
		// return false if either items are null or if their lengths aren't equal
		if ( item1 == null || item2 == null || item1.length != item2.length )
			return false;
		
		// for every bit in the items
		for ( int i = 0 ; i < item1.length ; ++i ) {
			// return false if any of the bits are not equal
			if ( item1[i] != item2[i] )
				return false;
		}
		
		return true;
	}
	
	/**
	 * inLexicon
	 * 
	 * if the input is in the lexicon, it returns the index in which it was found
	 * returns -1 if the item is invalid or it is not in the lexicon
	 */
	public int inLexicon( int[] item ) {
		
		// return -1 if the inputs are invalid
		if ( item == null || item.length != numElements )
			return -1;
		
		// for every item in the lexicon
		for ( int i = 0 ;  i < lexicon.size() ; ++i ) {
			// if the item at the index is equal to the input, then return the index
			if ( itemsEqual( lexicon.get( i ), item ) )
				return i;
		}
		
		// if the item is not found, then return -1
		return -1;
	}
	
	/**
	 * isFull
	 * 
	 * returns false if there are less items than the maximum amount and true if the lexicon is full
	 */
	public boolean isFull() {
		
		// return false if the number of items in the lexicon is less than maxItems
		if( lexicon.size() < maxItems)
			return false;
		// return true otherwise
		else
			return true;
	}
	
	/**
	 * isEqual
	 * 
	 * takes in another lexicon and checks if the two are equal, if so, return true
	 */
	public boolean isEqual( Lexicon l ) {
		
		// return false if the number of items between the two lexicons are not equal
		if ( lexicon.size() != l.getNumItems() )
			return false;
		
		// for every item in the lexicons
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			
			// return false if the lengths of any individual item are different
			if ( lexicon.get( i ).length != l.getItem( i ).length )
				return false;
			
			// for every bit in the current item
			for ( int j = 0 ; j < lexicon.get( i ).length ; ++j ) {
				
				// return false if the bits differ from each other
				if ( lexicon.get( i )[j] != l.getItem( i )[j] )
					return false;
			}
		}
		
		// if nothing was different, then return true
		return true;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Methods for Calculating Frequency of various item types
	////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * freqOfUSAS
	 * 
	 * iterates through the whole lexicon and returns the number of items that are USAS
	 */
	public int freqOfUSAS() {
		
		// initialize the count
		int count = 0;
		
		// iterating through every item in the lexicon
		for ( int i = 0 ; i < lexicon.size(); ++i ) {
			// increment the count if the item at index i is USAS
			if ( isUSAS( lexicon.get( i ) ) )
				++count;
		}
		
		return count;
	}
	
	
	/**
	 * freqOfStandardEnglish
	 * 
	 * iterates through the whole lexicon and returns the number of items that are standard English
	 */
	public int freqOfStandardEnglish() {
		
		// initialize the count
		int count = 0;
		
		// iterating through every item in the lexicon
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			// increment the count if the item at index i is Standard English
			if ( isStandardEnglish( lexicon.get( i ) ) )
				++count;
		}
		
		return count;
	}
	
	
	/**
	 * freqOfAAE
	 * 
	 * iterates through the whole lexicon and returns the number of items that are African-American English
	 */
	public int freqOfAAE() {
		
		// initialize the count
		int count = 0;
		
		// iterating through every item in the lexicon
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			// increment the count if the item at index i is AAE
			if ( isAAE( lexicon.get( i ) ) )
				++count;
		}
		
		return count;
	}
	
	/**
	 * freqOfWhiteSpanish
	 * 
	 * iterates through the whole lexicon and returns the number of items that are Puerto-Rican
	 */
	public int freqOfWhiteSpanish() {
		
		// initialize the count
		int count = 0;
		
		// iterating through every item in the lexicon
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			// increment the count if the item at index i is Puerto-Rican
			if ( isWhiteSpanish( lexicon.get( i ) ) )
				++count;
		}
		
		return count;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Methods for Checking Item Qualities
	////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * isUSAS
	 * 
	 * returns true if the item in question is afro-spanish (_ 1 0 1)
	 */
	public static boolean isUSAS( int[] item ) {
		
		// return true if all of the bits match up
		if ( item[1] == 1 && item[2] == 0 && item[3] == 1 )
			return true;
		// return false otherwise
		else
			return false;
	}
	
	/**
	 * isUSAS
	 * 
	 * returns true if the item in the lexicon denoted by itemIndex is USAS (_ 1 0 1)
	 */
	
	public boolean isUSAS( int itemIndex ) {
		
		// return true if all of the bits at the specified index match up
		if ( lexicon.get( itemIndex )[1] == 1 && lexicon.get( itemIndex )[2] == 0 && lexicon.get( itemIndex )[3] == 1 )
			return true;
		// return false otherwise
		else
			return false;
	}
	
	
	/**
	 * isStandardEnglish
	 * 
	 * returns true if the input is an item of standard English (_ 0 1 0), false otherwise
	 */
	public static boolean isStandardEnglish( int[] item ) {
		
		// return true if all of the bits match up
		if ( item[1] == 0 && item[2] == 1 && item[3] == 0 )
			return true;
		// return false otherwise
		else
			return false;
	}
	
	/**
	 * isStandardEnglish
	 * 
	 * returns true if the item with the specified index is standard English (_ 0 1 0)
	 */
	public boolean isStandardEnglish( int itemIndex ) {
		
		// return true if all of the bits at the specified index match up
		if ( lexicon.get( itemIndex )[1] == 0 && lexicon.get( itemIndex )[2] == 1 && lexicon.get( itemIndex )[3] == 0 )
			return true;
		// return false otherwise
		else
			return false;
	}
	
	/**
	 * isAAE
	 * 
	 * returns true if the input item is AAE (_ 0 0 1), false otherwise
	 */
	public static boolean isAAE( int[] item ) {
		
		// return true if all of the bits match up
		if ( item[1] == 0 && item[2] == 0 && item[3] == 1 )
			return true;
		// return false otherwise
		else
			return false;
	}
	
	/**
	 * isAAE
	 * 
	 * returns true if the item with the specified index is AAE (_ 0 0 1), false otherwise
	 */
	public boolean isAAE( int itemIndex ) {
		
		// return true if all of the bits at the specified index match up
		if ( lexicon.get( itemIndex )[1] == 0 && lexicon.get( itemIndex )[2] == 0 && lexicon.get( itemIndex )[3] == 1 )
			return true;
		// return false otherwise
		else
			return false;
	}
	
	/**
	 * isAAE
	 * 
	 * returns true if the input item is Puerto-Rican (_ 1 1 0), false otherwise
	 */
	public static boolean isWhiteSpanish( int[] item ) {
		
		// return true if all of the bits match up
		if ( item[1] == 1 && item[2] == 1 && item[3] == 0 )
			return true;
		// return false otherwise
		else
			return false;
	}
	
	/**
	 * isWhiteSpanish
	 * 
	 * returns true if the item with the specified index is Puerto-Rican (_ 1 1 0), false otherwise
	 */
	public boolean isWhiteSpanish( int itemIndex ) {
		
		// return true if all of the bits at the specified index match up
		if ( lexicon.get( itemIndex )[1] == 1 && lexicon.get( itemIndex )[2] == 1 && lexicon.get( itemIndex )[3] == 0 )
			return true;
		// return false otherwise
		else
			return false;
	}
	
	/**
	 * getNumOfBitInColumn
	 * 
	 * given a certain bit and column, calculates the number of items with that aspect
	 * returns -1 if the element number is out of bounds
	 */
	public int getNumOfBitInColumn( int element, int bit ) {
		
		// return -1 if the element input is invalid
		if( element >= numElements )
			return -1;
		
		// initialize the count
		int count = 0;
		
		// for every item in the lexicon
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			// increment the count if the bit is equal to the input
			if ( lexicon.get( i )[element] == bit )
				++count;
		}
		
		return count;
	}
	
	
	
	/**
	 * print
	 * 
	 * prints the lexicon as a matrix with each row as an item
	 */
	
	public void print() {
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			for ( int j = 0 ; j < numElements ; ++j ) {
				System.out.printf( "%d ",lexicon.get(i)[j] );
			}
			System.out.print("\n");
		}
		System.out.print("\n\n");
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Comparator
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sorts by the first element in each item, which denotes which word/grammatical structure it is
	 */
	private class LexiconComparator implements Comparator<int[]> {
		@Override
		public int compare ( int[] item1, int[] item2 ) {
			if ( item1[0] > item2[0] )
				return 1;
			else if ( item1[0] < item2[0] )
				return -1;
			return 0;
		}
	}

	
	////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS AND SETTERS
	////////////////////////////////////////////////////////////////////////////////////////

	public static int getMaxItems() {
		return maxItems;
	}
	public static void setMaxItems(int maxItems) {
		Lexicon.maxItems = maxItems;
	}
	public static int getNumElements() {
		return numElements;
	}
	public static void setNumElements(int numElements) {
		Lexicon.numElements = numElements;
	}
	public ArrayList<int[]> getLexicon() {
		return lexicon;
	}
	public void setLexicon( ArrayList<int[]> lexicon ) {
		this.lexicon = lexicon;
	}
	public int getNumItems() {
		return lexicon.size();
	}
}
