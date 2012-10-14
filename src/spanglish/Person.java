package spanglish;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


	public class Person implements ObjectInGrid, Drawable {
		
		
		//////////////////////// CLASS VARIABLES
		
		//full number of language items in one's lexicon
		public static int		maxItems;
		//number of language bits in one language item, first bit denotes order
		public static int		numElements;
		// allows for unique ID number
		public static int		nextID = 1;
		// initialize associated model
		public static Model		model;
		// initialize associated GUI model
		public static GUIModel	guiModel;
		// we'll use this to draw a border around the bugs' cells (the f means float)
	    public static BasicStroke      	personEdgeStroke = new BasicStroke( 1.0f );
	    // the world that the agents are associated with
	    public static TorusWorld		world;
	    // the various regions in the world
	    public static Region		home, school, work;
	    // signifies the length of conversations. set in Model initialization
	    public static int 		numWordsHeard;
	    // determines whether or not the number of words exchanged is random, with numWordsHeard as the maximum
	    public static int		randomWordFlow;
	    // si
	    public static int		threshold;
	    // utility class for drawing the agents
	    public static SimGraphics 	sim = new SimGraphics();
	    
	    
	    // the probability that a word heard by an adult will be added to their first lexicon
	    public static double adultProbL1;
	    // the probability that a word heard by an adult will be added to their second lexicon
	    public static double adultProbL2;
	    // the probability that a word heard by an adult will call the GA on their lexicons
	    public static double adultProbGA;
	    
	    
		//////////////////////// INSTANCE VARIABLES
		Random 					rng 	= new Random();
		
		/*
		 * Person characteristics
		 */
		
		// the age of the Person depends on the number of ticks per year and numTicksAlive
		public int 				age; 
		// denotes whether they are a child(0-5), student(6-17) or adult(18+)
		public int 				ageClass;
		// records the number of ticks the Person has been alive for aging purposes
		public int 				numTicksAlive;
		// 0 for female and 1 for male
		public int 				gender;
		// 0 for those initialized in the model, always one more than their parents'
		public int 				generation;
		// white, black or Latino
		public int 				race;
		// lower class, middle class or upper class
		public int				socialClass;
		// details in calcStatus method
		public int				status;
		// details in calcMatingID method
		public int				matingID = 0;
		// if the Person is married, this contains their spouse
		public Person			spouse;
		// if the Person was born within the model, then this contains both of their parents
		public ArrayList<Person>parents		= new ArrayList<Person>();
		
		
	    // the radius around which someone can choose a speaking partner
	    public int 				sightRadius;
		// the x and y coordinates of the Person
		public int				x, y;
		// the unique ID number of each Person
		public int				id;
		// the Person's first and second lexicon
		public Lexicon			firstLang, secondLang;
		public ArrayList<Boolean> changedList = new ArrayList<Boolean>();
		// the Person's Temporary Memory Buffer
		public TempLexicon		tempMemBuffer;
		// an ArrayList of the Regions the Person is allowed to occupy
		public ArrayList<Region>regions = new ArrayList<Region>();
		// initialize the GeneticAlg class for each person
		public GeneticAlg		ga;
		// the fill and border color of each person
		public Color			myColor, borderColor;
		
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Constructors
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Default Constructor
		 * 
		 * Only used so Teachers and Babysitters can have an empty constructor
		 */
		
		public Person() {
		}
		
		/**
		 * Race Constructor
		 * 
		 * Called at implementation, creates a Person of a specified race. Latinos speak only Spanish, non-latinos speak only English
		 * Age and race randomized, then proper color, placement, ID numbers and status are assigned
		 * 
		 */
		public Person( int r ) {
			// Randomize age between 18 and 85 when born within the model
			age = rng.nextInt( 57 ) + 18;
			// randomize gender between male(1) and female(0)
			gender = rng.nextInt( 2 );
			// set unique ID number
			id = ++nextID;
			// set race as the input
			race = r;
			
			// if not latino, they get an english lexicon
			// see createEnglishLexicon method for details
			if ( race < Model.LATINO )
				createEnglishLexicon();
			// if latino, they get a spanish lexicon
			// see createSpanishLexicon method for details
			else if ( race == Model.LATINO )
				createSpanishLexicon();
			// sets color, see method below for details
			setColorByRace();
			// sets several important statistics, see method for details
			updateStats();
			// calculates social class, see method for details
			calcInitialClass();
			// see method for details
			calcMatingID();
			// generation is zero because they were initialized with the model
			generation = 0;
			// no parents or spouse if initialized with model
			parents = null;
			spouse = null;
		}
		
		/**
		 * Birth Constructor
		 * 
		 * Creates a baby from two other people, random gender, reset age, empty Lexicon, keeps track of parents
		 * called during the reproduction function in the model class
		 */
		public Person( Person p1, Person p2 ) {
			// randomize gender
			gender = rng.nextInt( 2 );
			// born at age zero
			age = 0;
			// sets unique ID
			id = ++nextID;
			// sets several important statistics, see method for details
			updateStats();
			// border color is white if they were born within the model
			setBorderColor( Color.white );
			// choose a parent and increment their generation
			generation = p1.getGeneration() + 1;
			// see method for details
			calcMatingID();
			
			// child is considered white if both of their parents are white
			if ( p1.getRace() == Model.WHITE && p2.getRace() == Model.WHITE )
				race = Model.WHITE;
			// child is considered the race of non-white parent if one parent is white and the other isn't
			else if ( p1.getRace() == Model.WHITE && p2.getRace() != Model.WHITE )
				race = p2.getRace();
			else if ( p1.getRace() != Model.WHITE && p2.getRace() == Model.WHITE )
				race = p1.getRace();
			// if neither parent is white, then they have a 50% chance of being considered either of
			// their parents race. only important with black-latino mating
			else {
				if ( rng.nextDouble() < .5 )
					race = p1.getRace();
				else
					race = p2.getRace();
			}
			
			// take on socialClass of one parent with 50% probability
			if ( rng.nextDouble() < .5 )
				socialClass = p1.getSocialClass();
			else
				socialClass = p2.getSocialClass();
			
			// sets color, see method for details
			setColorByRace();
			
			// add the parents to their ArrayList
			parents.add( p1 );
			parents.add( p2 );
			// no spouse, obviously
			spouse = null;
			
			// completely empty first language
			firstLang = new Lexicon();
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Initialization Methods
		////////////////////////////////////////////////////////////////////////////////////////	
		
		/**
		 * setColorByRace
		 * 
		 * white people are white, black people are blue and latinos are gray
		 */
		public void setColorByRace() {
			if ( race == Model.WHITE )
				setColor( Color.white );
			
			else if ( race == Model.BLACK )
				setColor( Color.blue );
			
			else if ( race == Model.LATINO )
				setColor( Color.gray );
		}
		
		
		/**
		 * createEnglishLexicon
		 * 
		 * called at the initialization constructor, it gives the person a full English lexicon with
		 * phonological items corresponding to their race.
		 * Note: The older an African American person is, the larger number of standard English items they can have in their
		 * lexicon. The greatest proportion is around 75%
		 */
		public void createEnglishLexicon() {
			
			// intialize an ArrayList of items that will eventually become their lexicon
			ArrayList<int[]> tempFirstLang = new ArrayList<int[]>();
			
			// sets proportion of Standard English in an African American's lexicon. Ranges
			// from 0% to 75%. higher proportion is correlated with higher age
			double tempStat = -( .75 / 57 ) * ( age - 18 ) + 1;
			tempStat = 1;
			
			// over every item
			for ( int i = 0 ; i < maxItems ; ++i ) {
				
				// initialize a new item
				int[] tempItem = new int[numElements];
				
				// sets the first bit to i which denotes the index
				tempItem[0] = i;
				
				// the second bit is 0 which means that the item is English
				tempItem[1] = 0;
				
				// if the Person is black
				if( race == Model.BLACK ) {
					// then with the probability determined earlier, this item is AAE
					if ( rng.nextDouble() < tempStat ) {
						tempItem[2] = 0;
						tempItem[3] = 1;
					}
					// otherwise, the item is standard english
					else {
						tempItem[2] = 1;
						tempItem[3] = 0;
					}
					
				}
				// if the person is white
				else {
					// then they only have Standard English
					tempItem[2] = 1;
					tempItem[3] = 0;
				}
				
				// add the new item to the ArrayList
				tempFirstLang.add( tempItem );
			}
			
			// creates a Lexicon using the ArrayList of items that was just created
			firstLang = new Lexicon( tempFirstLang );
		}
		
		/**
		 * createSpanishLexicon
		 * 
		 * called at the initialization constructor, gives the person a full Spanish lexicon with no
		 * Afro or Anglo phonology aspects, "pure" spanish.
		 */
		public void createSpanishLexicon() {
			
			// initialize an new ArrayList of items
			ArrayList<int[]> tempFirstLang = new ArrayList<int[]>();
			
			// for maxItems times
			for ( int i = 0 ; i < maxItems ; ++i ) {
				
				// initialize a new item
				int[] tempItem = new int[numElements];
				
				// the first element denotes the index, or what specific word it is
				tempItem[0] = i;
				
				// a one in the second bit signifies that it is Spanish
				tempItem[1] = 1;
				
				// the next two elements are zero, so there is no Afro or Anglo qualities to the item
				tempItem[2] = tempItem[3] = 0;
				
				// add this new item to the list
				tempFirstLang.add( tempItem );
			}
			
			// creates a Lexicon from the ArrayList we just created
			firstLang = new Lexicon( tempFirstLang );
		}
		
		/**
		 * calcInitialClass
		 * 
		 * calculates the socioeconomic class of a person based on their race. called on
		 * initialization of the model, and only takes into account race as of now.
		 */
		public void calcInitialClass() {
			
			// initialize continuous random double
			double dummyRand = rng.nextDouble();
			
			// if the person is white
			if ( race == Model.WHITE ) {
				// then they have a 35% chance of being upper class
				if ( dummyRand < 0.35 )
					socialClass = Model.UPPERCLASS;
				// a 45% chance of being middle class
				else if ( dummyRand < 0.8 )
					socialClass = Model.MIDDLECLASS;
				// and a 20% chance of being lower class
				else
					socialClass = Model.LOWERCLASS;
			}
			// if the person is black
			else if ( race == Model.BLACK ) {
				// then they have a 15% chance of being upper class
				if ( dummyRand < 0.15 )
					socialClass = Model.UPPERCLASS;
				// a 35% chance of being middle class
				else if ( dummyRand < 0.5 )
					socialClass = Model.MIDDLECLASS;
				// and a 50% chance of being lower class
				else
					socialClass = Model.LOWERCLASS;
			}
			// if the person is latino
			else if ( race == Model.LATINO ){
				// then they have a 10% chance of being upper class
				if ( dummyRand < 0.1 )
					socialClass = Model.UPPERCLASS;
				// a 25% chance of being middle class
				else if ( dummyRand < 0.35 )
					socialClass = Model.MIDDLECLASS;
				// and a 65% chance of being lower class
				else
					socialClass = Model.LOWERCLASS;
			}
		}
		
		/**
		 * calcMatingID
		 * 
		 * three digit number that quantifies similarity between people
		 * first digit is generation, second is race and third if social class. used when trying
		 * to find someone to mate with in the model class
		 */
		public void calcMatingID() {
			
			// constructs a three digit number, where the 100 digit is their generation, their 10 digit is
			// their race and the ones digit is their social class
			matingID += (100 * generation) + (10 * race) + socialClass;
		}	
		
		/**
		 * calcStatus
		 * 
		 * called in the constructor and when a person's age is changed. Status is most heavily
		 * dependent on age, then race, then gender and then social class.
		 * Note: The status of a Latino gets a three point boost if the majority of the population
		 * is Latino
		 */
		public void calcStatus() {
			
			// can't transmit language if younger then 6, thus thier status is zero so nobody will
			// listen to them
			status = 0;
			
			// add twenty to status if they are adults, and ten to status if they are students
			status += 10 * ageClass;
			
			// if the person is white
			if ( race == Model.WHITE ) {
				// add six to status
				status += 6;
				// add two to status if they are male
				status += 2 * gender;
			}
			// if the person is black
			else if ( race == Model.BLACK ) {
				// add four to their status
				status += 4;
				// add two to their status if they are female
				status += 2 * ( 1 - gender );
			}
			else if ( race == Model.LATINO ){
				// add one to their status if they are female
				status += ( 1 - gender );
				// add three to status if latinos are the majority of the population
				if ( model.latinoMajority )
					status += 3;
				// add one extra status point for each new generation
				status += ( 2 * generation );
			}
			
			// add two to status if upper class, and one to status if middle class
			status += socialClass;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Dynamic Methods
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * move
		 * 
		 * obviously, moves the person one random step within their list of regions. called in order
		 * and in priority of status
		 */
		public void move() {
			
			// moves person in a random direction within their region/s, called in Model.activatePeopleToTakeSteps()
			world.moveObjectInRegion( this );
		}
		
		
		/**
		 * step()
		 * 
		 * contains the methods for listening to other people's lexicons. if the person is a child, then there is a
		 * certain probability that they will listen to their parents. this is explored more in the listenToParents method.
		 * next, the person looks within sightRadius of their current position, and then listen to them as long as they have a status
		 * greater than or equal to themselves, and are not a child
		 */
		public void step() {
			
			// if the person is a child, then they will listen to their parents with a 35% probability
			// see listenToParents method below for details
			if ( ageClass == Model.CHILD && rng.nextDouble() < .5 )
				listenToParents();
			
			// if the person has at least one neighbor
			if ( world.getNeighbors( this, sightRadius ).size() > 0 && ageClass != Model.CHILD ) {
				
				Person p = Collections.max( world.getNeighbors( this, sightRadius ), new PersonStatusComparator() );
				
				// if the chosen neighbor has a status greater than or equal to this person, and the chosen
				// neighbor is not a child, then listen to them
				// see listen function below
				if ( p.getStatus() >= status && p.ageClass > Model.CHILD ) {
					listen( p );
				}
			}
		}
		
		
		/**
		 * listenToParents
		 * 
		 * Takes a random number of items from one of the parent's lexicons and
		 * processes it as listening. Calls the hearItems function, which will be explained later.
		 */
		public void listenToParents() {
			
			// return if the person has no parents
			if ( parents == null || parents.size() == 0 )
				return;
			
			// choose one parent randomly
			Person parent = getRandomPersonFromArray( parents );
			
			// return if the chosen parent has no first language
			if ( parent.getFirstLang().getNumItems() < 1 )
				return;
			
			// if there is no random word flow
			if ( randomWordFlow == 0 ) {
			// the child then hears numWordsHeard random items from the chosen parent's first lexicon
			// see hearItems method for details
			hearItems( parent.getFirstLang().getRandomItemsFromLexicon( numWordsHeard ) );
			}
			
			// if there is random word flow
			else {
				// then choose a random integer between one and numWordsHeard
				int numWords = rng.nextInt( numWordsHeard ) + 1;
				// the child then hears numWords random items from the chosen parent's first lexicon
				// see hearItems method for details
				hearItems( parent.getFirstLang().getRandomItemsFromLexicon( numWords ) );
			}
		}
		
		/**
		 * listen
		 * 
		 * picks a random amount of items to be heard from the other person's lexicon. The two exceptions exist
		 * so latinos don't have non-latino babysitters and so white children are the only ones who can
		 * communicate with white babysitters
		 */
		public void listen( Person p ) {
			
			// return if the person that is being listened to has no first language
			if ( p.getFirstLang().getNumItems() < 1 )
				return;
			
			// if the person being listened to is a BabySitter
			if ( p instanceof BabySitter ) {
				
				// Latinos cannot have non-Latino Babysitters
				if ( race == Model.LATINO && p.getRace() != Model.LATINO )
					return;
				
				// non-whites cannot have a white BabySitter
				if ( p.getRace() == Model.WHITE && race != Model.WHITE )
					return;
			}
			
			// if there is no random word flow
			if ( randomWordFlow == 0 ) {
			// the person hears numWordsHeard random items from the speaker's first lexicon
			// see hearItems method for details
			hearItems( p.getFirstLang().getRandomItemsFromLexicon( numWordsHeard ) );
			}
			
			// if there is random word flow
			else {
				// then choose a random integer between one and numWordsHeard
				int numWords = rng.nextInt( numWordsHeard ) + 1;
				// the person hears numWords random items from the speaker's first lexicon
				// see hearItems method for details
				hearItems( p.getFirstLang().getRandomItemsFromLexicon( numWords ) );
			}
		}
		
		/**
		 * updateAge
		 * 
		 * every step of the model, the person increments the number of time steps that they've been alive for. every year's
		 * worth of steps, their age is incremented by one and the updateStats function is called to make sure they
		 * are in the proper region. if the person is an American born latino who just turned six, then
		 * they have a chance of developing a BiLexicon
		 */
		public void updateAge() {
			
			// every step, increment the number of ticks the person has been alive
			++numTicksAlive;
			
			// once the person has been alive for a multiple of stepsPerYear ticks
			if ( numTicksAlive % model.getStepsPerYear() == 0 ) {
				
				// increment their age
				++age;
				
				// update certain statistics that are based on age
				// see updateStats method below for details
				updateStats();
				
				// if the person is a Latino student that was born within the model
				if ( ageClass == Model.STUDENT && generation > 0 && race == Model.LATINO ) {
					
					// then they have the potential to become bilingual
					// see chanceToBiLexicon method for details
					changeToBiLexicon();
				}
			}
		}
		
		/**
		 * updateStats()
		 * 
		 * called only in the constructor and when someone ages a year. updates their ageClass, their
		 * status, which heavily depends on age, and their region. changes their region if they
		 * move up an age class
		 */
		public void updateStats() {
			
			// Children are five and younger
			if ( age < 6 )
				ageClass = Model.CHILD;
			// Students are ages six to seventeen
			else if ( age < 18 )
				ageClass = Model.STUDENT;
			// Adults are eighteen and older
			else
				ageClass = Model.ADULT;
			
			// recalculate status after changing their age class
			calcStatus();
			
			// if the person is a child
			if ( ageClass == Model.CHILD ) {
				// then they occupy only the home Region
				regions.clear();
				regions.add( home );
			}
			// if the person is a student
			else if ( ageClass == Model.STUDENT ) {
				// then they occupy only the school Region
				regions.clear();
				regions.add( school );
			}
			// if the person is an adult
			else if ( ageClass == Model.ADULT ) {
				// then they occupy only the work Region
				regions.clear();
				regions.add( work );
			}
			
			// People can see longer as they age, but when they reach a certain age they shorten their radius
			if ( age < 10 ) {
				sightRadius = 3;
			}
			else if ( age < 18 ) {
				sightRadius = 6;
			}
			else if ( age < 50 ) {
				sightRadius = 10;
			}
			else {
				sightRadius = 3;
			}
			
			// if the person is in the incorrect Region, then move to the correct one
			// see inRegion method for details
			if ( !inRegion() )
				world.movePersonToRegion( this );
		}
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Lexicon Methods
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * hearItems
		 * 
		 * takes in an ArrayList of items and sends them to the above hearItem function where each
		 * item is processed separately. returns if the list of items is null or empty
		 */
		public void hearItems( ArrayList<int[]> items ) {
			
			// return if the items chosen were in valid
			if ( items == null || items.size() == 0 )
				return;
			
			// for every item in the ArrayList items
			for ( int i = 0 ; i < items.size() ; ++i ) {
				// hear the item, see method below for details
				hearItem( items.get(( i )) );
			}
			calcStatus();
		}
		
		/**
		 * hearItem
		 * 
		 * this is where an item is processed and it is determined what the person will do with the heard item. The
		 * four options are calling the GA, or having the item inserted into the first, second or temporary Lexicon.
		 * The probabilities of these various decisions are determined by the person's age. For children and adults, the
		 * probabilities are fixed, but the probabilities are dynamic for students.
		 * Note: The dynamic probabilities are set to continuously change the importance of the three possibilites other than
		 * the GA from being a child to being an adult.
		 */
		public void hearItem( int[] item ) {
			
			// initialize a uniform random double
			double tempRand = rng.nextDouble();
			
			// if the person is a child
			if ( ageClass == Model.CHILD ) {
				// if they are a Latino child, they get no exposure to English, so return
				// the second digit in an item signifies language, and a zero there signifies English
				if ( item[1] == 0 && race == 2 )
					return;
				
				// with an 80% probability
				if ( tempRand < .8 ) {
					// the item is sent to their first lexicon
					// see addToFirstLexicon method below for details
					addToFirstLexicon( item );
				}
				// with a 15% probability
				else if ( tempRand < .95 ){
					// construct an instance of GeneticAlg
					ga = new GeneticAlg( this, item );
					// and calculate whether it will be inserted into once of their lexicons
					// see GeneticAlg class for details
					ga.calcInsertion();
				}
				// with a 5% probability
				else {
					// add the item to the Temporary Memory Buffer
					// see addToTempMemBuffer method below for details
					addToTempMemBuffer( item );
				}
			}
			// if the person is a Student
			else if ( ageClass == Model.STUDENT ) {
				
				// when the person is a child, there is a high probability of an item going to the first lexicon,
				// a low probability of it going into the Temporary Memory Buffer and no chance of going to 
				// the second language. these statistics allow for prob(L1) to decrease, and prob(L2) and prob(TMB) to
				// increase to the proper proportions linearly as an adult would have, while maintaining the prob(GA) constant
				// at 50% percent
				double L1Prob = (.5 / .85) * ( -(.65 / 12) * age + .8 + (.65 / 2) );
				double L2Prob = (.5 / .85) * ( (1 / 60) * age - .1 );
				
				// with a 50% probability
				if ( tempRand < .5 ) {
					// construct an instance of GeneticAlg
					ga = new GeneticAlg( this, item );
					// and calculate whether it will be inserted into once of their lexicons
					// see GeneticAlg class for details
					ga.calcInsertion();
				}
				// with L1Prob probability (ranging from ~47% to ~12% linearly with age)
				else if ( tempRand < L1Prob + .5 ) {
					// the item is sent to their first lexicon
					// see addToFirstLexicon method below for details
					addToFirstLexicon( item );
				}
				// with L2Prob probability (ranging from 0% to ~12%) and if they are not bilingual
				else if ( tempRand < .5 + L1Prob + L2Prob && !( firstLang instanceof BiLexicon ) ) {
					// the item is sent to their second language
					// see addToSecondLexicon method below for details
					addToSecondLexicon( item );
				}
				// with TMBProb probability (ranging from ~3% to ~26%)
				else {
					// add the item to the Temporary Memory Buffer
					// see addToTempMemBuffer method below for details
					addToTempMemBuffer( item );
				}
			}
			// if the person is an adult
			else if ( ageClass == Model.ADULT ){
				// with adultProbGA probability
				if ( tempRand < adultProbGA ) {
					// construct an instance of GeneticAlg
					ga = new GeneticAlg( this, item );
					// and calculate whether it will be inserted into once of their lexicons
					// see GeneticAlg class for details
					ga.calcInsertion();
				}
				// with adultProbL1 probability
				else if ( tempRand < adultProbGA + adultProbL1 ) {
					// the item is sent to their first lexicon
					// see addToFirstLexicon method below for details
					addToFirstLexicon( item );
				}
				// with adultProbL2 probability and if the person is not bilingual
				else if ( tempRand < adultProbGA + adultProbL1 + adultProbL2 && !( firstLang instanceof BiLexicon ) ) {
					// the item is sent to their second language
					// see addToSecondLexicon method below for details
					addToSecondLexicon( item );
				}
				// with (1 - adultProbGA - adultProbL1 - adultProbL2) probability
				else {
					// add the item to the Temporary Memory Buffer
					// see addToTempMemBuffer method below for details
					addToTempMemBuffer( item );
				}
			}
		}
		
		
		/**
		 * replaceItemInFirstLang
		 * 
		 * if existingItem is in the person's first language, then it replaces it with newItem and returns true
		 * returns false otherwise
		 */
		public boolean replaceItemInFirstLang( int[] existingItem, int[] newItem ) {
			
			// tries to swaps existingItem with newItem and returns whether or not it was successful
			return firstLang.swapItem( existingItem , newItem );
		}
		
		/**
		 * replaceInflectionInFirstLang
		 * 
		 * if existingItem is the the person's first language, then we replace all bits in existingItem after
		 * the first two with those of newItem. returns false if existingItem is not in firstLang
		 */
		public boolean replaceInflectionInFirstLang( int[] existingItem, int[] newItem ) {
			
			// tries to swaps the inflection of existingItem with that of newItem and returns whether or not it was successful
			return firstLang.swapInflection( existingItem , newItem );
		}
		
		/**
		 * replaceInflectionInSecondLang
		 * 
		 * if existingItem is the the person's second language, then we replace all bits in existingItem after
		 * the first two with those of newItem. returns false if existingItem is not in firstLang
		 */
		public boolean replaceInflectionInSecondLang( int[] existingItem, int[] newItem ) {
			
			// tries to swaps the inflection of existingItem with that of newItem and returns whether or not it was successful
			return secondLang.swapInflection( existingItem , newItem );
		}
		
		/**
		 * addToFirstLexicon()
		 * 
		 * Takes in an item and returns true if it was successfully added into the first lexicon, false if
		 * newItem's size is invalid or if the insertion is not successful. more details are in the
		 * addItem function the the Lexicon/BiLexicon class
		 */
		public boolean addToFirstLexicon( int[] newItem ) {
			
			// return false if the input is invalid
			if ( newItem == null || newItem.length != numElements )
				return false;
			
			// if the person has no first language yet
			if ( firstLang == null ) {
				// then construct a first language with that item
				firstLang = new Lexicon( newItem );
				return true;
			}
			
			// otherwise, try to add newItem to the first lexicon
			// see Lexicon.addItem method for details
			return firstLang.addItem( newItem );
		}
		
		/**
		 * addToSecondLexicon()
		 * 
		 * Takes in an item and returns true if it was successfully added into the second lexicon, false if
		 * newItem's size is invalid or if the insertion is not successful. Also false if the item is
		 * already in someone's first lexicon. The probability of an item getting inserted into the second lexicon
		 * increases with it's frequency in the temporary memory buffer
		 */
		public boolean addToSecondLexicon( int[] newItem ) {
			
			// return false if the input is invalid or the item is already in the person's first language
			if ( newItem == null || newItem.length != numElements || firstLang.inLexicon( newItem ) != -1 )
				return false;
			
			// return false if the person has an ermpty Temporary Memory Buffer
			if ( tempMemBuffer == null)
				return false;
			
			// set the probability of accepting the word, which is the ratio of the number of times the item
			// has been heard with threshold, which is set in the GUI and is ten by default
			double tempProb = Math.pow( tempMemBuffer.numTimesHeard( newItem ) / (double)threshold, 1.0);

			// with probability tempProb
			if ( rng.nextDouble() < tempProb ) {
				// if the person has no second language
				if ( secondLang == null ) {
					// construct a second language with the heard word
					secondLang = new Lexicon( newItem );
					return true;
				}
				
				// otherwise, attempt to add the item to the already existing second language
				// see Lexicon.addItem method for details
				return secondLang.addItem( newItem );
			}
			return false;
		}
		
		
		/**
		 * addToTempMemBuffer()
		 * 
		 * Takes in an item and returns true if it was successfully added into the tempMemBuffer, false if
		 * newItem's size is invalid or if the insertion is not successful. 
		 * Note: The tempMemBuffer is a TempLexicon, so refer to that class for more details
		 */
		public boolean addToTempMemBuffer( int[] newItem ) {
			
			// return false if the input is invalid
			if ( newItem == null || newItem.length != numElements )
				return false;
			
			// if the person does not have a Temporary Memory Buffer
			if ( tempMemBuffer == null ) {
				// construct one with the heard word
				tempMemBuffer = new TempLexicon( newItem );
				return true;
			}
			
			return true;
		}
		
		/**
		 * inFirstLang
		 * 
		 * returns true if the specified item is in the first language, details are in the Lexicon class
		 * returns false if not in the first language or the item size is invalid
		 */
		public boolean inFirstLang( int[] item ) {
			
			// return false if the input is invalid
			if ( item == null || item.length == 0 )
				return false;
			
			// return false if the item is not in the person's first lexicon
			// see Lexicon.inLexicon method for details
			if ( firstLang.inLexicon( item ) < 0 )
				return false;
			// return false if it is in their first lexicon
			else
				return true;
		}
		
		/**
		 * sameItem
		 * 
		 * returns true if the two input items are exactly the same and false otherwise
		 * throws invalid item sizes
		 */
		public boolean sameItem( int[] item1, int[] item2 ) {
			
			// return false if the input is invalid or they are different sizes
			if ( item1 == null || item2 == null || item1.length != item2.length ) {
				return false;
			}
			
			// for every bit in the items
			for ( int i = 0 ; i < item1.length ; ++i ) {
				// return false if their corresponding bits are not equal
				if ( item1[i] != item2[i] )
					return false;
			}
			
			// return true is everything checks out
			return true;
		}	
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Assorted Methods
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * isFertile
		 * 
		 * returns true if the person is of a birthing age and false otherwise
		 */
		public boolean isFertile() {
			// a person is fertile if they are between the ages of 18 and 59
			if ( age > 17 && age < 60 )
				return true;
			// otherwise, they are not fertile
			else
				return false;
		}
		
		
		/**
		 * die
		 * 
		 * returns true if the person is "scheduled" to die. people have a probability of dying once they turn
		 * 45, and they must be dead by the time they are 85. The age of death is determined by both race and
		 * social class, denoted by the variable deathSensitivity. The higher this statistic is, this older people
		 * will tend to die. For example, a person in the lower class will have a relatively high probability of
		 * death at age 50, whereas a rich person will not have a significantly high probability of dying until they are about 65
		 */
		public boolean die() {
			
			// people cannot die if they are younger than 50
			if ( age < 50 )
				return false;
			
			// to keep population under control, and person only has a chance of dying on their birthday
			if ( numTicksAlive % model.getStepsPerYear() != 0 )
				return false;
			
			// initialize the deathSensativity statistic. the higher this number is, the older the person will be
			// when they die, on average. see deathProb calculation below for details.
			double deathSensitivity = 2.5;
			
			// if the person is white, they live the longest
			if ( race == Model.WHITE )
				deathSensitivity *= 2.0;
			// if the person is black, they live less
			else if ( race == Model.BLACK )
				deathSensitivity *= 1.5;
			// if the person is Latino, they live the least long
			else if ( race == Model.LATINO )
				deathSensitivity *= 1.0;
			
			// if the person is lower class, they live the least long
			if ( socialClass == Model.LOWERCLASS )
				deathSensitivity *= .5;
			// if the person is middle class, they live a little longer
			else if ( socialClass == Model.MIDDLECLASS )
				deathSensitivity *= 1.0;
			// if the person is upper class, they live the longest
			else if ( socialClass == Model.UPPERCLASS )
				deathSensitivity *= 1.5;
			
			// women live longer than men
			if ( gender == Model.FEMALE )
				deathSensitivity *= 2.0;
				
			// by convention, death prob will increase from 0% when the person is 50, to 100% when the person is 85
			// the higher deathSensitivity is, the more convex the curve is, so the probability is low until the person is older
			// lower statistics yield concave curves, and deaths are concentrated closer to fifty
			double deathProb = Math.pow(( 1 / 35.0 ) * ( age - 50 ), deathSensitivity );
			
			// the person dies with probability deathProb
			if ( rng.nextDouble() < deathProb )
				return true;
			// otherwise they remain alive
			else
				return false;
		}
		
		/**
		 * hasSpouse
		 * 
		 * returns true if the person has a spouse and false if they don't
		 */
		public boolean hasSpouse() {
			// return false if the person has no spouse
			if ( spouse == null )
				return false;
			// return true if they have a spouse
			else
				return true;
		}
		
		/**
		 * retire()
		 * 
		 * dummy method for Person super class. Only relevant for Teachers and BabySitters
		 */
		public boolean retire() {
			return false;
		}
		
		/**
		 * getRandomPersonFromArray
		 * 
		 * takes in an ArrayList of people and returns a random person from it. returns null if the array is null or
		 * empty and if the list only has one person in it, then it returns that one person
		 */
		public Person getRandomPersonFromArray( ArrayList<Person> personArray ) {
			
			// return null if ArrayList is empty
			if ( personArray == null || personArray.size() == 0)
				return null;
			
			// if theres only one element in the array, return it
			if ( personArray.size() == 1 )
				return personArray.get( 0 );
			
			// otherwise, get the size of the array
			int numPeople = personArray.size();
			
			// and choose a random person within that array
			return personArray.get( rng.nextInt( numPeople ) );
		}
		
		
		
		public Person getFriend( ArrayList<Person> personArray ) {
			// return null if ArrayList is empty
			if ( personArray == null || personArray.size() == 0)
				return null;
						
			// if theres only one element in the array, return it
			if ( personArray.size() == 1 )
				return personArray.get( 0 );
			
			Collections.shuffle( personArray );
			
			if ( race == Model.WHITE ) {
				for ( Person p : personArray ) {
					if ( p.getRace() == Model.WHITE )
						return p;
				}
				return getRandomPersonFromArray( personArray );
			}
			
			for ( Person p : personArray ) {
				if ( p.getRace() != Model.WHITE )
					return p;
			}
			return getRandomPersonFromArray( personArray );
		}
		
		
		
		/**
		 * changeToBiLexicon
		 * 
		 * This function is only called for latino students who have been born in the country. Such a person will
		 * only become fully billingual if they have at least one english item in their lexicon by the age of 8. more
		 * details about a BiLexicon are in the class itself
		 */
		public void changeToBiLexicon() {
			
			// if the Latino student has at least one English word in their first lexicon before they turn nine, then
			// they become bilinugal and obtain a BiLexicon. details in the BiLexicon class description
			if ( firstLang.getNumOfBitInColumn( 1 , 0 ) > 0 && age < 9 )
				firstLang = new BiLexicon( firstLang );
			// if not, then return
			else
				return;
		}
		
		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Region Methods
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * inRegion
		 * 
		 * tests all of the person's region to see whether or not they are in one of their regions
		 * returns true if they are, false otherwise
		 */
		public boolean inRegion() {
			
			// return false if the person has no regions for some reason
			if ( regions == null )
				return false;
			
			// for every region in their ArrayList
			for ( Region r : regions ) {
				// return true if they are in one of them
				if ( r.inRegion( this ) )
					return true;
			}
			
			// return false if they are not in any of them
			return false;
		}
		
		/**
		 * inRegion
		 * 
		 * checks a coordinate (dX, dY) and determines whether it is in any of the person's regions. returns
		 * true if it is in the person's regions ArrayList and false otherwise
		 */
		
		public boolean inRegion( int dX, int dY) {
			
			// return false if the person has no regions for some reason
			if ( regions == null) {
				return false;
			}
			
			// for every region in their ArrayList
			for ( Region r : regions )
				// return true if the coordinates are in any of the regions
				if ( r.inRegion( dX, dY ) )
					return true;
			
			// return false if they are not in any of them
			return false;
		}
		
		
		/**
		 * getRandomRegionPoint()
		 * 
		 * Returns a random point from one of the person's regions
		 */
		
		public Point getRandomRegionPoint() {
			
			// return null if the person has no regions for some reason
			if ( regions.size() == 0 )
				return null;
			
			// if the person only has one region, then get a random point from that one
			if ( regions.size() == 1 )
				return regions.get( 0 ).getRandomPoint();
			
			// if they have more than one, get the number of regions they have
			int numOfRegions = regions.size();
			
			// choose one of the person's regions
			Region oneOfRegions = regions.get( rng.nextInt( numOfRegions - 1 ) );
			
			// and return a random point from it
			return oneOfRegions.getRandomPoint();
		}
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Printing Methods
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * printFirstLang
		 * 
		 * prints the person's first language in matrix form
		 */
		public void printFirstLang() {
			firstLang.print();
			System.out.print("\n");
		}
		
		/**
		 * printSecondLang
		 * 
		 * prints the person's first language in matrix form if it exists
		 */
		public void printSecondLang() {
			if ( secondLang == null )
				return;
			secondLang.print();
			System.out.print("\n");
		}
		
		/**
		 * print
		 * 
		 * Prints a line of the given statistics about a person
		 */
		public void print() {
			System.out.printf("\nID: %d, Race: %d, Age: %d, Class: %d, Gender: %d, Generation: %d, Status: %d\n\n",
					id, race, age, socialClass, gender, generation, status );
		}
		
		////////////////////////////////////////////////////////////////////////////////////////
		// GUI Stuff
		////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		// draw 
		// we implement Drawable interface, so we need this method
		// so that the ant can draw itself when requested  (by the GUI display).
		*/
	    public void draw( SimGraphics g ) {
	    	
	    	// give the person an oval shape filled with myColor
		   	g.drawFastOval( myColor );
		   	
		   	// draw a border with thickness personEdgeStroke and color borderColor
	        g.drawOvalBorder( personEdgeStroke, borderColor );
	    }
	    
		////////////////////////////////////////////////////////////////////////////////////////
		// Comparators
		////////////////////////////////////////////////////////////////////////////////////////
	    public static class PersonStatusComparator implements Comparator<Person> {
	    	
	    	@Override
	    	public int compare( Person p1, Person p2 ) {
	    		return p1.getStatus() - p2.getStatus();
	    	}
	    }
	    
	    
	    
		
		////////////////////////////////////////////////////////////////////////////////////////
		// GETTERS AND SETTERS
		////////////////////////////////////////////////////////////////////////////////////////
	    public void setColor( Color c ) {
			myColor = c;
		}
		public void setBorderColor( Color c ) {
			borderColor = c;
		}
		public static int getMaxItems() {
			return maxItems;
		}
		public static void setMaxItems(int maxItems) {
			Person.maxItems = maxItems;
		}
		public static int getNumElements() {
			return numElements;
		}
		public static void setNumElements(int numElements) {
			Person.numElements = numElements;
		}
		public int getNumTicksAlive() {
			return numTicksAlive;
		}
		public void setNumTicksAlive(int numTicksAlive) {
			this.numTicksAlive = numTicksAlive;
		}
		public static int getNextID() {
			return nextID;
		}
		public static void setNextID(int nextID) {
			Person.nextID = nextID;
		}
		public Person getSpouse() {
			return spouse;
		}
		public void setSpouse(Person spouse) {
			this.spouse = spouse;
		}
		public static Model getModel() {
			return model;
		}
		public static void setModel(Model model) {
			Person.model = model;
		}
		public static GUIModel getGUIModel() {
			return guiModel;
		}
		public static void setGUIModel(GUIModel guiModel) {
			Person.guiModel = guiModel;
		}
		public static BasicStroke getPersonEdgeStroke() {
			return personEdgeStroke;
		}
		public int getAgeClass() {
			return ageClass;
		}
		public void setAgeClass(int ageClass) {
			this.ageClass = ageClass;
		}
		public static void setPersonEdgeStroke(BasicStroke personEdgeStroke) {
			Person.personEdgeStroke = personEdgeStroke;
		}
		public static GridWorld getWorld() {
			return world;
		}
		public static void setWorld(TorusWorld world) {
			Person.world = world;
		}
		public static Region getHome() {
			return home;
		}
		public static void setHome(Region home) {
			Person.home = home;
		}
		public static Region getSchool() {
			return school;
		}
		public static void setSchool(Region school) {
			Person.school = school;
		}
		public static Region getWork() {
			return work;
		}
		public static void setWork(Region work) {
			Person.work = work;
		}
		public static int getThreshold() {
			return threshold;
		}
		public static void setThreshold(int threshold) {
			Person.threshold = threshold;
		}
		public static double getAdultProbL1() {
			return adultProbL1;
		}
		public static void setAdultProbL1(double adultProbL1) {
			Person.adultProbL1 = adultProbL1;
		}
		public static double getAdultProbL2() {
			return adultProbL2;
		}
		public static void setAdultProbL2(double adultProbL2) {
			Person.adultProbL2 = adultProbL2;
		}
		public static double getAdultProbGA() {
			return adultProbGA;
		}
		public static void setAdultProbGA(double adultProbGA) {
			Person.adultProbGA = adultProbGA;
		}
		public static GUIModel getGuiModel() {
			return guiModel;
		}
		public static void setGuiModel(GUIModel guiModel) {
			Person.guiModel = guiModel;
		}
		public static int getRandomWordFlow() {
			return randomWordFlow;
		}
		public static void setRandomWordFlow(int randomWordFlow) {
			Person.randomWordFlow = randomWordFlow;
		}
		public int getGeneration() {
			return generation;
		}
		public void setGeneration(int generation) {
			this.generation = generation;
		}
		public int getSocialClass() {
			return socialClass;
		}
		public void setSocialClass(int socialClass) {
			this.socialClass = socialClass;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public GeneticAlg getGa() {
			return ga;
		}
		public void setGa(GeneticAlg ga) {
			this.ga = ga;
		}
		public static SimGraphics getSim() {
			return sim;
		}
		public static void setSim(SimGraphics sim) {
			Person.sim = sim;
		}
		public Random getRng() {
			return rng;
		}
		public void setRng(Random rng) {
			this.rng = rng;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public ArrayList<Person> getParents() {
			return parents;
		}
		public void setParents(ArrayList<Person> parents) {
			this.parents = parents;
		}
		public int getGender() {
			return gender;
		}
		public void setGender(int gender) {
			this.gender = gender;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public Lexicon getFirstLang() {
			return firstLang;
		}
		public void setFirstLang(Lexicon firstLang) {
			this.firstLang = firstLang;
		}
		public Lexicon getSecondLang() {
			return secondLang;
		}
		public void setSecondLang(Lexicon secondLang) {
			this.secondLang = secondLang;
		}
		public TempLexicon getTempMemBuffer() {
			return tempMemBuffer;
		}
		public void setTempMemBuffer( TempLexicon tempMem ) {
			this.tempMemBuffer = tempMem;
		}
		public ArrayList<Region> getRegions() {
			return regions;
		}
		public void setRegions(ArrayList<Region> regions) {
			this.regions = regions;
		}
		public Color getMyColor() {
			return myColor;
		}
		public void setMyColor(Color myColor) {
			this.myColor = myColor;
		}
		public Color getBorderColor() {
			return borderColor;
		}	
		public void setRace( int race ) {
			this.race = race;
		}	
		public int getRace() {
			return race;
		}
		public int getMatingID() {
			return matingID;
		}
		public void setMatingID(int matingID) {
			this.matingID = matingID;
		}
		public static int getNumWordsHeard() {
			return numWordsHeard;
		}
		public static void setNumWordsHeard(int numWordsHeard) {
			Person.numWordsHeard = numWordsHeard;
		}
	}
	