package spanglish;

/**
 * TempLexicon.java
 * 
 * A subclass of the Lexicon class, has all of the same methods as the Lexicon class with a few extras
 * The purpose of the TempLexicon is to act as a Temporary Memory Buffer by collecting all of the words
 * that a person has heard, regardless of language. It also has the capacity to store the number of times any
 * given word has been heard by the person
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TempLexicon extends Lexicon {
	
	// because the TempLexicon records the number of times the item was heard in the first bit,
	// we must add one extra to the capacity for the size of items
	public static int numTempElements = numElements + 1;

	//////////////////////////////////////////////////////////////////////////////////////
	// Constructors
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * empty constructor that is not really used.
	 */
	public TempLexicon() {
		
		// initialize an ArrayList of items
		lexicon = new ArrayList<int[]>();
	}
	
	/**
	 * Default Constructor
	 * 
	 * Begins temp lexicon by adding one heard item
	 * sticks a one in front of the item to denote the fact its only been heard once
	 */
	public TempLexicon( int[] item ) {

		// initialize an ArrayList of items
		lexicon = new ArrayList<int[]>();
		
		// create a new item with one extra bin for the number of times heard
		int[] tempItem = new int[numTempElements];
		
		// the first bit is one to signify that the item has been heard once
		tempItem[0] = 1;
		
		// after the first bit, the item should look like the input item
		for ( int i = 1 ; i < numTempElements ; ++i )
			tempItem[i] = item[i - 1];
		
		// add the item to the new initialized ArrayList
		lexicon.add(tempItem);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	// Methods
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * hearItem
	 * 
	 * If someone hears an item for the first time, it is inserted into their temp lexicon with a one in front of it
	 * If it was already in the working memory, the number in front of the item is incremented
	 * to denote the number of times it's been heard
	 */
	public int hearItem( int[] item ) {
	
		// return -1 if the input it invalid
		if ( item.length != numElements )
			return -1;
		
		// if they have already heard the item
		// see the heardItem method for details
		if ( heardItem( item ) != -1 ) {
			
			// increment the number of times the item was heard
			++lexicon.get( heardItem( item ) )[0];
			
			// return the number of times the item has now been heard
			return lexicon.get(heardItem( item ))[0];
		}
		
		// if they haven't heard the input item yet, initialize an int array
		int[] tempItem = new int[numTempElements];
		
		// the first bit is one to signify that the item has been heard once
		tempItem[0] = 1;
		
		// after the first bit, the item should look like the input item
		for ( int i = 1 ; i < numTempElements ; ++i )
			tempItem[i] = item[i - 1];
		
		// add tempItem to the TempLexicon
		lexicon.add(tempItem);
		
		// re-sort the TempLexicon using the Comparator
		// see the Comparator below for details
		Collections.sort( lexicon,
				  (java.util.Comparator<? super int[]>) new TempLexiconComparator() );
		
		// return one to signify that the item has been heard once
		return 1;
	}
	
	/**
	 * hearItem
	 * 
	 * determines whether the person has heard the item, if they haven't return -1
	 * if they have heard it, then it returns the index in the temp lexion in which it was heard
	 */
	public int heardItem( int[] item ) {
		
		// initialize a new item that has a regular amount of elements. that is, it doens't include
		// a bit that signifies the number of times heard
		int[] tempItem = new int[numElements];
		
		// iterating over all of the items in the TempLexicon
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			
			// set tempItem equal to the current item, not including the first bit
			for ( int j = 1 ; j < numTempElements ; ++j ) {
				tempItem[j - 1] = lexicon.get( i )[j];
			}
			
			// if the item was found in the TempLexicon, then return the index in which is was heard
			if ( itemsEqual( item, tempItem ) ) {
				return i;
			}
		}
		
		// if it hasn't been heard, return -1
		return -1;
	}
	
	/**
	 * numTimesHeard
	 * 
	 * if the item was heard, then returns the number of times it was heard. Otherwise, returns zero
	 */
	public int numTimesHeard( int[] item ) {
		
		// if the item is not in the TempLexicon, then return 0
		if ( heardItem( item ) == -1 )
			return 0;
		
		// otherwise, return the first bit at the index the indicates the number of times the item was heard
		return lexicon.get( heardItem( item ) )[0];
	}
	
	/**
	 * getMostHeardWithIndex
	 * 
	 * given an index, this method returns the item with the index which has been heard the most. mostly used
	 * to calculate the commonLex in the model class
	 */
	public int[] getMostHeardWithIndex( int index ) {
		
		// initialize a new ArrayList of items
		ArrayList<int[]> tempArray = new ArrayList<int[]>();
		
		// iterate over every item in the TempLexicon
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			// if the item has the same index as the input, add it to the tempArray
			if ( lexicon.get(i)[1] == index ) {
				tempArray.add( lexicon.get(i) );
			}
		}
		
		// return null if there are no items in the temp lexicon with the given index
		if ( tempArray == null || tempArray.size() == 0 )
			return null;
		
		// initialize the maximum number of times an item was heard and the index of that item
		int maxTimesHeard = 0;
		int maxIndex = 0;
		
		// iterating over every item in the TempLexicon with the specified index
		for ( int i = 0 ; i < tempArray.size() ; ++i ) {	
			// if the number of times the item was heard is greater than the current maximum
			if ( tempArray.get( i )[0] > maxTimesHeard ) {
				
				// reset the maximum and record the index of that maximum
				maxTimesHeard = tempArray.get( i )[0];
				maxIndex = i;
			}
		}
		
		// initialize a new int array
		int[] mostHeardItem = new int[numElements];
		
		// construct the mostHeardItem from the item at maxIndex calculated earlier
		for ( int i = 0 ; i < numElements ; ++i ) {
			mostHeardItem[i] = tempArray.get( maxIndex )[i + 1];
		}
		
		return mostHeardItem;
	}
	
	
	@Override
	public void print() {
		for ( int i = 0 ; i < lexicon.size() ; ++i ) {
			for ( int j = 0 ; j < numTempElements ; ++j ) {
				System.out.printf( "%d ",lexicon.get( i )[j]);
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Comparator
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sorts by the second element of each item because the first element is the count
	 * Also sorts by the language, because the temp lexicon takes in both languages
	 */
	private class TempLexiconComparator implements Comparator<int[]> {
		@Override
		public int compare ( int[] item1, int[] item2 ) {
			int num1 = ( 100 * item1[1] ) + item1[2];
			int num2 = ( 100 * item2[1] ) + item2[2];
			if ( num1 > num2 )
				return 1;
			else if ( num1 < num2 )
				return -1;
			return 0;
		}
	}
}
