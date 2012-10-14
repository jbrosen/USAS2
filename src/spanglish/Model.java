package spanglish;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.util.SimUtilities;


public class Model extends ModelParameters{
	
	////////////////////////////////// Class Variables
	
	// initialize Random class
	public static Random		rng = new Random();
	
	// initialize the 2D Grid
	public TorusWorld			world;
	
	// control how person activation is executed
	public int					activationOrder = statusActivationOrder;
	
	// fixed order
	public static final int 	fixedActivationOrder = 0;
	// random with replacement
	public static final int 	rwrActivationOrder = 1; 
	// random without replacement
	public static final int 	rworActivationOrder = 2;
	// in order of status
	public static final int		statusActivationOrder = 3;
	
	// final variables for demographic classification
	public static final int		WHITE = 0;
	public static final int		BLACK = 1;
	public static final int		LATINO = 2;
	public static final int		CHILD = 0;
	public static final int		STUDENT = 1;
	public static final int		ADULT = 2;
	public static final int		FEMALE = 0;
	public static final int		MALE = 1;
	public static final int		UPPERCLASS = 2;
	public static final int		MIDDLECLASS = 1;
	public static final int		LOWERCLASS = 0;
	
	
	//////////////////////////////////Instance Variables
	
	/*
	 * Logistical Variables that must stay fixed throughout one model run
	 */
	
	// the maximum number of items/words in a Lexicon
	public final int			maxItems = 10;
	// the number of bits in any given item
	public final int			numElements = 4;
	// the number of model ticks that represent a year in model time
	public final int			stepsPerYear = 100;
	// the maximum amount of status a person can have, mostly used for activation order
	public final double			maxStatus = 40.0;
	
	
	/*
	 * GUI Parameters
	 */
	
	// probability of a person of a certain race in any given step attempting to reproduce
//	public double				blackBirthRate = .003;
//	public double 				latinoBirthRate = .0013;
//	public double				whiteBirthRate = .0009;
	
	public double				blackBirthRate = .0018;
	public double 				latinoBirthRate = .0001;
	public double				whiteBirthRate = .0002;
	
	
	// the number of Latino adults that are created within the model per year
	// Note: created with 100% Spanish lexicon
	public int					immigrationPerYear = 8;
	// the number of white people that are randomly chosen to leave the model each year
	public int					whiteFlightPerYear = 6;
	
	// the number of words exchanged in any given interaction
	// see Person class for details
	public int					wordFlow = 6;
	// determines whether the number of words heard is random, where wordFlow is the maximum number of words
	// 0: no Random word flow, 1: Random word flow
	// see the Person class for details
	public int					randomWordFlow = 0;
	// a statistic related to the amount of times a word must be heard until it is registered
	// see Person and GeneticAlg class for details
	public int					wordThreshold = 10;
	
	// set to zero if you don't want statistics about the different types of English and greater
	// then zero if you do
	public int					showEnglishFreqGraph = 0;
	
    // the probability that a word heard by an adult will be added to their first lexicon
	public double				adultProbGA = .15;
    // the probability that a word heard by an adult will be added to their second lexicon
	public double				adultProbL1 = .15;
    // the probability that a word heard by an adult will call the GA on their lexicons
	public double 				adultProbL2 = .2;
	
	/*
	 * Value Variables
	 */
	
	// initial demographics for the model
	public double 				initialPercentLatino = .04;
	public double				initialPercentBlack = .13;
	public int					initialPopulation = 500;
	
	// there will always be this number of Teachers
	public int					numTeachers = 30;
	// there will always be this number of BabySitters
	public int					numBabySitters = 30;
	// the set number of attractors
	public int					numAttractors = 20;
	
	// the probability of two people marrying if they have a baby
	public double				marriageProb = .25;
	
	// denotes whether or not Latinos make up most of the population
	public boolean 				latinoMajority = false;
	
	// define size of world
	public int 					sizeX = 100;
	public int					sizeY = 100;
	
	public ArrayList<Double>	testMeans = new ArrayList<Double>();
	public int					runNumber = 0;
	
	
	/*
	 * List Variables
	 */
	
	// holds all live people
	public ArrayList<Person>			visibleList = new ArrayList<Person>();
	// holds all of the live people, excluding BabySitters/Teachers
	public ArrayList<Person> 			personList = new ArrayList<Person>();
	// holds all of the live Latinos, excluding BabySitters/Teachers
	public ArrayList<Person>			latinoList = new ArrayList<Person>();
	
	// the name of the three Regions in the model
	public Region 				home, school, work;
	
	/*
	 * Statistics
	 */
	
	// the number of different types of people
	public int numBlack,numWhite,numLatino,numTotal,numChildren,numStudents,numAdults;
	// the percentage of non-BabySitters/Teachers of different races
	public double percBlack, percLatino, percWhite;
	// the percentage of Students, Children and Adults
	public double percChildren, percStudents, percAdults;
	// the number of latino and black students and adults
	public int numLatinoStudents, numBlackStudents, numLatinoAdults, numBlackAdults;
	// the average instances of USAS over all latinos, and specific latino age groups
	public double afroSpanishFrequency, childUSASFreq, studentUSASFreq, adultUSASFreq;
	// the average instances of the two different types of English across all non-Latinos
	public double standardFreq, AAEFreq;
	// double array containing the number of instances of USAS in each kind of item, corresponding to the array index
	// e.x: if the first entry was 30.0, then 30 Latinos have the item (0 1 0 1) in their first language
	public double[] indexFreqOfUSAS = new double[maxItems];
	// an ArrayList of all instances of USAS at any given tick
	public ArrayList<int[]> USASList = new ArrayList<int[]>();
	// ArrayLists representing, respectively, the most common lexicon of Latinos and the most
	// common lexicon over the entire population, excluding Teachers/BabySitters
	public ArrayList<int[]> commonLex, commonLexTotal = new ArrayList<int[]>();
	// ArrayLists representing, respectively, the most common second lexicon of Latinos and the most
	// common second lexicon over the entire population, excluding Teachers/BabySitters
	public ArrayList<int[]> commonSecondLex, commonSecondLexTotal = new ArrayList<int[]>();
	
	
	
	/**
	 * addModelSpecificParameters
	 * 
	 * allows for certain variables to be set as parameters in the batch run or GUI run
	 * the first input is the shorthand name and the second is the actual variable name
	 */
	public void addModelSpecificParameters () {
		parametersMap.put("bBRate", "blackBirthRate");
		parametersMap.put("lBRate", "latinoBirthRate");
		parametersMap.put("wBRate", "whiteBirthRate");
		parametersMap.put("englishGraph", "showEnglishFreqGraph");
		parametersMap.put("immRate", "immigrationPerYear");
		parametersMap.put("whiteFlightRate", "whiteFlightPerYear");
		parametersMap.put("words", "wordFlow");
		parametersMap.put("wordThreshold", "wordThreshold");
		parametersMap.put("randWordFlow", "randomWordFlow");
		parametersMap.put("adultProbL1", "adultProbL1");
		parametersMap.put("adultProbL2", "adultProbL2");
		parametersMap.put("adultProbGA", "adultProbGA");
	}

	/**
	 * getInitParam
	 * 
	 * specifies what is shown on Repast panel
	 */
	public String[] getInitParam () {
		String[] params = { "blackBirthRate", "latinoBirthRate", "whiteBirthRate","showEnglishFreqGraph" ,
				"immigrationPerYear", "whiteFlightPerYear","wordFlow", "wordThreshold","randomWordFlow",
				"adultProbL1", "adultProbL2", "adultProbGA"
				 };
		return params;
	}
	

	/**
	// userSetup()
	// called when user presses the "reset" button.
	// discard any existing model parts, and re-initialize as desired.
	//
	// NB: if you want values entered via the GUI to remain after restart,
	//     do not initialize them here.
	*/
	public void userSetup() {
		if ( rDebug > 0 )
			System.out.printf( "==> userSetup...\n" );
		
		visibleList.clear();
		personList.clear();
		latinoList.clear();
		
	}

	/**
	// userBuildModel
	 * 
	// called when model initialized, eg, with Initialize button.
	// create all the objects that constitute the model:
	*/
	public void userBuildModel () {
		
		// initialize a new TorusWorld of (sizeX, sizeY), that is connected to this Model instance
		world = new TorusWorld( sizeX, sizeY , this );
		
		// statically connect the Region and Person class to the grid
		Region.setGrid( world );
		Person.setWorld( world );
		
		// Home is the uppermost fifteen cell block
		home = new Region( 0 , sizeX - 1, 0, 15 );
		// School is from the 11th block, down to the 30th
		// Note: Home and School overlap by a few cells
		school = new Region( 0 , sizeX - 1, 11, 30 );
		// Work is from the 31st cell all the way down
		work = new Region( 0 , sizeX - 1, 31, sizeY - 1 );
		
		// see createPeople method below
		createPeople();
	}
	
	/**
	 * setStaticParameters
	 * 
	 * there are several class variables that need to be references by the current model instance, this method
	 * contains all such static "setting" methods
	 */
	public void setStaticParameters() {
		
		// references the current model instance
		Person.setModel( this );
		// reset the ID numbers to start at 1
		Person.setNextID( 1 );
		// set the Regions that were just set up and give them to the Person class
		Person.setHome( home );
		Person.setSchool( school );
		Person.setWork( work );
		// tell the Person class of any parameters that were set in the Model class/Repast panel
		Person.setMaxItems( maxItems );
		Person.setNumElements( numElements );
		Person.setNumWordsHeard( wordFlow );
		Person.setThreshold( wordThreshold );
		Person.setRandomWordFlow( randomWordFlow );
		Person.setAdultProbL1( adultProbL1 );
		Person.setAdultProbL2( adultProbL2 );
		Person.setAdultProbGA( adultProbGA );
		
		// references the current model instance
		GeneticAlg.setModel( this );
		// tell the GeneticAlg class of any parameters that were set in the Model class/Repast panel
		GeneticAlg.setThreshold( wordThreshold );
		GeneticAlg.setMaxItems( maxItems );
		GeneticAlg.setNumElements( numElements );
		
		// tell the Lexicon class of any parameters that were set in the Model class/Repast panel
		Lexicon.setMaxItems( maxItems );
		Lexicon.setNumElements( numElements );
	}
	
	/**
	 * createPeople()
	 * 
	 * Initializes some static variables to various Person sub/classes, creates agents with specified attributes
	 * also creates teachers and babysitters
	 */
	public void createPeople() {
		
		// see method above for details
		setStaticParameters();
		
		// initialize a person
		Person p;
		
		// for as many times as there are initialPopulation
		for ( int i = 0 ; i < initialPopulation ; ++i ) {
			
			// set a uniform random number
			double tempRand = rng.nextDouble();
			
			// with probability initialPercentLatino
			if ( tempRand < initialPercentLatino) {
				// create a new Latino person
				p = new Person( LATINO );
				// add them to the latinoList
				latinoList.add( p );
			}
			// with probability initialPercentBlack
			else if ( tempRand < initialPercentLatino + initialPercentBlack ) {
				// create a new black person
				p = new Person( BLACK );
			}
			// with probability ( 1 - initialPercentLatino - initialPercentBlack )
			else {
				// create a new white person
				p = new Person ( WHITE );
			}
			
			// move the new person to their proper region
			world.movePersonToRegion( p );
			
			// add the new person to their proper lists
			personList.add( p );
			visibleList.add( p );
		}
		
		// create numTeachers teachers and only add them to visibleList
		for ( int i = 0 ; i < numTeachers ; ++i ) {
			p = new Teacher();
			world.movePersonToRegion( p );
			visibleList.add( p );
		}
		
		// create numBabySitters BabySitters and only add them to visibleList
		for ( int i = 0 ; i < numBabySitters ; ++i ) {
			p = new BabySitter();
			world.movePersonToRegion( p );
			visibleList.add( p );
		}
		
		for ( int i = 0 ; i < numAttractors ; ++i ) {
			p = new Attractor( Model.BLACK );
			world.movePersonToRegion( p );
			visibleList.add( p );
			personList.add( p );
		}
	}
	
	
	
	
	/**
	 * step()
	 * 
	 * first, every person is activated to listen, and then there is a certain probability of them moving based on their status.
	 * next, 
	 */
	
	public void step() {
		
		if ( tickCount() == 40000 && tickCount() > 5 ) {
			for ( int i = 0 ; i < testMeans.size() ; ++i ) {
				System.out.print( "Tick Number "+Integer.toString(i*10)+": "+testMeans.get( i ).toString() +"\n" );
			}
		}
		
		// does the physical moving and listening of agents, depends on activationOrder
		// see activatePeopleToTakeSteps method below for details
		activatePeopleToTakeSteps();
		
		// deals with aging the people and reproduction
		// see ageAndReproduceMethod below for details
		ageAndReproduce();
		
		// halts the model run if the grid is overpopulated
		// see haltIfOverPopulated method below for details
		haltIfOverPopulated();
		
		
		// immigrationPerYear Latinos are initialized every year
		// see immigrate method below for details
		if ( tickCount() % ( stepsPerYear / immigrationPerYear ) == 0 ) {
			immigrate();
		}
		
		// whiteFlightPerYear whites exit the model every year
		// see whiteFlight method below for details
		if ( tickCount() % ( stepsPerYear / whiteFlightPerYear ) == 0 ) {
			whiteFlight();
		}
		
		/*
		if ( tickCount() % 998 == 0 ) {
			calcCommonLex();
			Lexicon tempPrint = new Lexicon( commonLex );
			tempPrint.print();
			for ( int i = 0 ; i < maxItems ; ++i ) {
				System.out.print( Double.toString( indexFreqOfUSAS[i]) + "\n" );
			}
			System.out.print( "White: " + Double.toString(percWhite)+"  Black: "+Double.toString(percBlack)+"  Latino: "+Double.toString(percLatino));
		}
		
		*/
		
		if ( tickCount() % 500 == 0 && tickCount() > 5 ) {
			
			int countPR = 0;
			int countUSAS = 0;
			for ( Person p : latinoList ) {
				if ( p.getFirstLang() != null ) {
					countPR += p.getFirstLang().freqOfWhiteSpanish();
					countUSAS += p.getFirstLang().freqOfUSAS();
				}
			}
			double avePR = countPR / (double)latinoList.size();
			double aveUSAS = countUSAS / (double)latinoList.size();
			System.out.print( "Average PR: "+Double.toString(avePR)+"\nAverage USAS: "+Double.toString(aveUSAS)+"\n\n");
			
			System.out.printf( "Tick %d\n\n", tickCount() );
		}
		
		// calculates all of the statistics at the end of each model step
		// see calcStats method below for details
		calcStats();
		
//		if ( tickCount() > 3500 && tickCount() % 50 == 0 ) {
//			System.out.printf( "Tick %d\n\n", tickCount() );
//		}
		
		
		if ( tickCount() % 10 == 0 ) {
			int countUSAS = 0;
			for ( Person p : latinoList ) {
				if ( p.getFirstLang() != null ) {
					countUSAS += p.getFirstLang().freqOfUSAS();
				}
			}
			double aveUSAS = countUSAS / (double)latinoList.size();
			if ( runNumber == 0 ) {
				testMeans.add( aveUSAS );
			}
			else {
				double newMean = ( aveUSAS + testMeans.get( ( tickCount() % 4000 ) / 10 ) ) / 2;
				testMeans.set(( tickCount() % 4000 ) / 10 , newMean );
			}
		}
		
		if ( tickCount() % 500 == 0 ) {
			System.out.print("\n\nTick Count:"+Integer.toString(tickCount())+"\n\n");
		}
		
		if ( tickCount() > 5 && tickCount() % 4000 == 0 ) {
			userSetup();
			userBuildModel();
			runNumber += 1;
		}

	}
	
	
	/**
	 * activatePeopleToTakeSteps
	 * 
	 * Several different orders in which people are called to step
	 * Depends on value of activationOrder, usually set to statusActivationOrder
	 */
	public void activatePeopleToTakeSteps() {
		
		// people are moved in order they appear in visibleList, quasi-random
		if ( activationOrder == fixedActivationOrder ) {
			
			// initialize an iterator to avoid concurrentModification exception
			Iterator<Person> visibleIter = visibleList.iterator();
			// for every person in visibleList
			while( visibleIter.hasNext() ) {
				// move/step the person
				Person p = visibleIter.next();
				p.move();
				p.step();
			}
		}
		// for as many times as there are people, a random person is chosen from the list
		// Note: a person can be chosen to move multiple times or no times
		else if ( activationOrder == rwrActivationOrder ) {
			
			// for as many times are there are people in visibleList
			for ( int i = 0; i < visibleList.size(); i++ ) {
				// choose a random integer between zero and visibleList.size()
				int r = getUniformIntFromTo( 0, visibleList.size()-1 );
				// choose and move the chosen person
				Person p = visibleList.get( r );
				p.move();
				p.step();
			}
		}
		// visibleList is shuffled randomly and each person is chosen to move in random order
		// Note: every person moves exactly once
		else if (  activationOrder == rworActivationOrder ) {
			
			// shuffle visibleList
			SimUtilities.shuffle( visibleList, uchicago.src.sim.util.Random.uniform );
			// initialize an iterator to avoid concurrentModification exception
			Iterator<Person> visibleIter = visibleList.iterator();
			// for every person in visibleList
			while ( visibleIter.hasNext() ) {
				// move the person
				Person p = visibleIter.next();
				p.move();
				p.step();
			}
		}
		// every person is assigned a probability of moving in any given step. their probability is
		// the ratio of their status to the maximum status. they will also move in order of status
		else if ( activationOrder == statusActivationOrder ) {
			
			// sort visibleList in terms of status
			Collections.sort( visibleList, 
					  (java.util.Comparator<? super Person>) new PersonStatusComparator() );
			// initialize an iterator to avoid concurrentModification exception
			Iterator<Person> visibleIter = visibleList.iterator();
			// for every person in the list
			while ( visibleIter.hasNext() ) {
				// call the person's step function
				Person p = visibleIter.next();
				p.step();
				// initialize a uniform random double
				double r = rng.nextDouble();
				// with probability of the ratio of the person's status with the maximum status
				if ( r < ( p.getStatus() / maxStatus ) ) {
					// the person physically moves
					p.move();
				}
			}
		}
	}
	
	
	/**
	 * immigrate
	 * 
	 * creates one new Latino adult and sticks them in the model. happens with frequency
	 * immigrationPerYear. Added to the three lists. also, the new Latino speaks only Spanish
	 */
	public void immigrate() {
		
		// create a new Latino person, add them to all three lists and move them to their proper region
		Person p = new Person( LATINO );
		latinoList.add( p );
		personList.add( p );
		visibleList.add( p );
		world.movePersonToRegion( p );
	}
	
	
	/**
	 * whiteFlight
	 * 
	 * a random white person is removed from the model with frequency whiteFlightPerYear,
	 * removed from visibleList and personList, does not include teacher or babysitters
	 */
	public void whiteFlight() {
		
		// initialize and ArrayList of people
		ArrayList<Person> whitePeople = new ArrayList<Person>();
		
		// for every people in personList
		for ( Person p : personList ) {
			// add them to the new ArrayList if they are white and under 25
			if ( p.getRace() == WHITE && p.getAge() < 25 )
				whitePeople.add( p );
		}
		
		// return if there are no white people currently in the model
		if ( whitePeople.size() == 0 )
			return;
		
		// otherwise, pick a random white person and remove them from all relevant lists
		Person whitePerson = getRandomPersonFromArray( whitePeople );
		visibleList.remove( whitePerson );
		personList.remove( whitePerson );
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Reproduction Methods
	////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * ageAndReproduce
	 * 
	 * updates the number of steps each agent has been alive for and increments the age when
	 * necessary. also creates children with the birth rates specified in the field. adds
	 * all new people to personList
	 */
	public void ageAndReproduce() {
		
		// initialize the number of new Teachers/BabySitters that need to be created this step
		int countNewTeachers = 0;
		int countNewBabySitters = 0;
		
		// initialize a new ArrayList of new babies
		ArrayList<Person> babies = new ArrayList<Person>();
		// initialize an iterator to avoid concurrentModification exception
		Iterator<Person> visibleIter = visibleList.iterator();
		
		// for every person in visibleList
		while ( visibleIter.hasNext() ) {
			Person p = visibleIter.next();
			// update the number of ticks they've been alive, and subsequently their age
			p.updateAge();
			
			// if the person is scheduled to die or retire at this step
			if ( p.die() || p.retire() ) {
				
				// tell their spouse that they have died
				if ( p.hasSpouse() ) {
					p.getSpouse().setSpouse( null );
				}
				
				// remove them from the visibleIter/visibleList, and the other two lists if relevant
				visibleIter.remove();
				personList.remove( p );
				latinoList.remove( p );
				
				// must create a new Teacher if they are a retired Teacher
				if ( p instanceof Teacher )
					++countNewTeachers;
				// must create a new BabySitter if they are a retired BabySitter
				else if ( p instanceof BabySitter )
					++countNewBabySitters;
			}
			
			// initialize a potential mate
			Person mate;
			// initialize a random uniform double
			double tempRand = rng.nextDouble();
			
			// Note: BabySitters and Teachers are never fertile
			// if the person is white and fertile, then with probability whiteBirthRate
			if ( p.getRace() == WHITE && tempRand < whiteBirthRate && p.isFertile() ) {
				// find a mate
				// see findMate function below for details
				mate = findMate( p );
				// make a baby is the home space isn't full and a suitable mate was found
				if ( mate != null && !home.isFull() ) {
					// see createPerson method below for details
					babies.add( createPerson( p, mate ) );
				}
			}
			// if the person is black and fertile, then with probability blackBirthRate
			else if ( p.getRace() == BLACK && tempRand < blackBirthRate && p.isFertile() ) {
				// find a mate
				// see findMate function below for details
				mate = findMate( p );
				// make a baby is the home space isn't full and a suitable mate was found
				if ( mate != null && !home.isFull() ) {
					// see createPerson method below for details
					babies.add( createPerson( p, mate ) );
				}
			}
			// if the person is Latino and fertile, then with probability latinoBirthRate
			else if ( p.getRace() == LATINO && tempRand < latinoBirthRate && p.isFertile() ) {
				// find a mate
				// see findMate function below for details
				mate = findMate( p );
				// make a baby is the home space isn't full and a suitable mate was found
				if ( mate != null && !home.isFull() ) {
					// see createPerson method below for details
					Person babyLatino = createPerson( p, mate );
					babies.add( babyLatino );
					// add it to the Latino list
					latinoList.add( babyLatino );
				}
			}
		}
		
		// add all of the new babies to both visibleList and personList
		visibleList.addAll( babies );
		personList.addAll( babies );
		
		// create as many Teachers as retired this past time step
		// see createTeacher method below for details
		for ( int i = 0 ; i < countNewTeachers ; ++i )
			createTeacher();
		// create as many BabySitters as retired this past time step
		// see createBabySitter method below for details
		for ( int i = 0 ; i < countNewBabySitters ; ++i )
			createBabySitter();
	}
	
	/**
	 * findMate
	 * 
	 * If they have a spouse, it returns them, otherwise
	 * takes in a person and compiles a list of potential mates with similar qualities
	 * returns a random one of them, determined by matingID. The closer two mating IDs are, the higher probability
	 * they have of mating
	 */
	public Person findMate( Person p ) {
		
		// declare new arrayList for potential mates
		ArrayList<Person> mateArray = new ArrayList<Person>();
		
		// if they are married, they mate with their spouse
		if ( p.getSpouse() != null )
			return p.getSpouse();
		
		// for every person in personList
		// Note: Teachers and BabySitters are not included
		for ( Person oneOfMates : personList ) {
			
			// if the person is fertile and of the opposite sex
			if ( oneOfMates.isFertile() && p.getGender() != oneOfMates.getGender() )
				// if the person is not themselves
				if ( p != oneOfMates ) {
					// get the ratio of the difference between the two people's matingID over the maximum matingID
					// Note: Generation matters most, then race and then social class
					double testStat = ( 999 - Math.abs( p.getMatingID() - oneOfMates.getMatingID() ) ) / 999.0;
					// set the probability to testStat to the 20th power
					double mateProb = Math.pow(testStat, 20.0 );
				
					// with mateProb probability and if the other person is single
					if ( rng.nextDouble() < mateProb && oneOfMates.getSpouse() == null ) {
						// add this person to the list of potential mates
						mateArray.add( oneOfMates );
					}
			 	}
		}
		
		// choose a random potential mate to mate with
		Person mate = getRandomPersonFromArray( mateArray );
		
		// the two get married with probability marriageProb
		if ( rng.nextDouble() < marriageProb )
			marry( p, mate );
		
		return mate;
	}
	
	/**
	 * marry
	 * 
	 * sets the two input people as eachother's spouses
	 */
	public void marry( Person p1, Person p2 ) {
		p1.setSpouse( p2 );
		p2.setSpouse( p1 );
	}
	
	/**
	 * createPerson
	 * 
	 * Takes two people, makes a baby and adds it to the world. returns the baby
	 * no birth takes place if the home space is full
	 */
	public Person createPerson( Person p1, Person p2 ) {
		
		// make sure the home space isn't full before creating a person
		if ( home.isFull() )
			return null;
		
		// creates a person and moves them to the proper region
		// see the specific Person constructor in the Person class for more details
		Person p = new Person( p1, p2 );
		world.movePersonToRegion( p );
		
		return p;
	}
	
	/**
	 * createTeacher
	 * 
	 * there must be a fixed number of teachers in the world, so when one retires, we create a new
	 * one picking picking a random non-latino adult from the population and cloning them into
	 * a teacher
	 */
	public void createTeacher() {
		
		// get a random uniform double
		double tempRand = rng.nextDouble();
		
		// initialize a Person
		Person p;
		
		// with a 70% probability
		if ( tempRand < .7 ) {
			// choose a random female adult from personList
			p = getRandomPersonFromArray( personList, ADULT, FEMALE );
		}
		// with a 30% probability
		else {
			// choose a random adult male from personList
			p = getRandomPersonFromArray( personList, ADULT, MALE );
		}
		
		// if the person that was chosen was Latino, repeat the process until you get a non-latino
		while( p.getRace() == LATINO ) {
			if ( tempRand < .7 )
				p = getRandomPersonFromArray( personList, ADULT, FEMALE );
			else
				p = getRandomPersonFromArray( personList, ADULT, MALE );
		}
		
		// clone a teacher from the chosen person and add them to visibleList
		Teacher teacher = new Teacher( p );
		visibleList.add( teacher );
	}
	
	/**
	 * createBabySitter
	 * 
	 * there must be a fixed number of babysitters in the world, so when one retires, we create a new
	 * one picking picking a random female adult from the population and cloning them into
	 * a babysitter. The race of babysitters is added proportional to the races that exist in the model
	 */
	public void createBabySitter() {
		
		// get a random adult female from personList
		Person p = getRandomPersonFromArray( personList, ADULT, FEMALE );
		
		// clone a BabySitter from the chosen person and add them to visibleList
		BabySitter sitter = new BabySitter( p );
		visibleList.add( sitter );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Lexicon Methods
	////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * calcFreqOfItem
	 * 
	 * returns the percentage of people that have the input item in their first language
	 */
	public double calcFreqOfItem( int[] item ) {
		
		// initialize the total number of people and items found to zero
		int personCount = 0;
		int itemCount = 0;
		
		// for every person in personList
		for ( Person p : personList ) {
			// increment the total number of people
			++personCount;
			// increment the number of item instances found it is in their first language
			if( p.inFirstLang( item ) )
				++itemCount;
		}
		
		// return the percentage of people with item in their first language
		return ( (double)itemCount ) / personCount;
	}
	
	/**
	 * calcFreqOfItem
	 * 
	 * returns the percentage of people in a given age group with a given item in their first language
	 */
	public double calcFreqOfItem( int[] item, int ageGroup ) {
		
		// initialize the total number of people in the input age and items found to zero
		int itemCount = 0;
		int personCount = 0;
		
		// for every person in personList
		for ( Person p : personList ) {
			// increment the number of people if they are of the proper age group
			if( p.getAgeClass() == ageGroup ) {
				++personCount;
				// increment the number of item instances found if it is in their first language
				if ( p.inFirstLang( item ))
					++itemCount;
			}
		}
		
		// return the percentage of people in the specified age group with item in their first language
		return ( (double)itemCount ) / personCount;
	}
	
	/**
	 * calcFreqOfItem
	 * 
	 * returns the percentage of people in a given age group with a given item in their first language
	 */
	public double calcFreqOfItemInRace( int[] item, int race ) {
		
		// initialize the total number of people in the input age and items found to zero
		int itemCount = 0;
		int personCount = 0;
		
		// for every person in personList
		for ( Person p : personList ) {
			// increment the number of people if they are of the proper age group
			if( p.getRace() == race ) {
				++personCount;
				// increment the number of item instances found if it is in their first language
				if ( p.inFirstLang( item ))
					++itemCount;
			}
		}
		
		// return the percentage of people in the specified age group with item in their first language
		return ( (double)itemCount ) / personCount;
	}
	
	/**
	 * calcFreqOfUSAS
	 * 
	 * finds the number of occurrences of USAS ( _ 1 0 1 ) over the entire population and divides it
	 * by the number of people, can at most be maxItems
	 * Used for the main graph in the GUIModel that calculates USAS frequency
	 */
	public void calcFreqOfUSAS() {
		
		// initialize and set all of the count variables to zero
		// they represent the total number of instance of USAS in each specified age group
		int countTotal, countChildren, countStudents, countAdults;
		countTotal = countChildren = countStudents = countAdults = 0;
		
		// for every person in personList
		for ( Person p : personList ) {
			// if they are Latino
			if ( p.getRace() == LATINO ) {
				// increment the total number of USAS instances by the number of USAS items in their first language
				// see the freqOfUSAS method in Lexicon class for details
				countTotal += p.getFirstLang().freqOfUSAS();
				// increment the child count if the person is a child
				if ( p.getAgeClass() == CHILD )
					countChildren += p.getFirstLang().freqOfUSAS();
				// increment the student count if the person is a student
				else if ( p.getAgeClass() == STUDENT )
					countStudents += p.getFirstLang().freqOfUSAS();
				// increment the adult count if the person is adult
				else if ( p.getAgeClass() == ADULT )
					countAdults += p.getFirstLang().freqOfUSAS();
			}
		}
		
		// calculates the average number of USAS items in the specified Latino groups
		afroSpanishFrequency = (double)countTotal / numLatino;
		childUSASFreq = (double)countChildren / ( numLatino - numLatinoStudents - numLatinoAdults);
		studentUSASFreq = (double)countStudents / numLatinoStudents;
		adultUSASFreq = (double)countAdults / numLatinoAdults;
	}
	
	
	/**
	 * calcFreqOfEnglish
	 * 
	 * calculates the frequency of different types of English throughout the non-Latino population. specifically the
	 * occurrences of standard English and AAE. Collects the average number of each item that people have
	 * in their first lexicon
	 * Note: Standard English: (_ 0 1 0), AAE: (_ 0 0 1)
	 */
	public void calcFreqOfEnglish() {
		
		// initialize the total number of Standard and AAE instances to zero
		int standardTotal = 0;
		int AAETotal = 0;
		
		// for every person in personList
		for ( Person p : personList ) {
			// if the person is not Latino
			if ( p.getRace() != LATINO ) {
				// increment the variables by the number of English stype instances in their first language
				// see freqOfStandardEnglish and freqOfAAE methods in Lexicon class for details
				standardTotal += p.getFirstLang().freqOfStandardEnglish();
				AAETotal += p.getFirstLang().freqOfAAE();
			}
		}
		
		// calculates the average number of english types in the non-Latino groups
		standardFreq = (double)standardTotal / ( numWhite + numBlack );
		AAEFreq = (double)AAETotal / ( numWhite + numBlack );
	}
	
	/**
	 * calcIndexFreqOfUSAS()
	 * 
	 * finds all occurrences of USAS in the population and collects the most common indices for which
	 * one has USAS and stores it in the array indexFreqOfUSAS
	 */
	public void calcIndexFreqOfUSAS() {
		
		// clear indexFreqOfUSAS variable
		indexFreqOfUSAS = new double[maxItems];
		// initialize int array that will mirror indexFreqOfUSAS and
		int[] temp = new int[maxItems];
		// clear the list of all instances of USAS throughout the population
		USASList.clear();
		
		// for every person in personList
		for ( Person p : personList ) {
			
			// for the amount of items in the person's first lexicon
			for ( int i = 0 ; i < p.getFirstLang().getNumItems() ; ++i ) {
				// increment the bin in the temp array that corresponds to the index number of any USAS items
				// found in their first language
				// e.x: if a person had (2 1 0 1) in their first language, then the third bin of temp would be incremented
				if ( p.getFirstLang().isUSAS( i ) ) {
					++temp[p.getFirstLang().getBit( i, 0 ) ];
				}
				// if the item is an instance of USAS, then add it to USASList
				if ( isUSAS(p.getFirstLang().getItem( i ) ) )
					USASList.add( p.getFirstLang().getItem( i ) );
			}
		}
		
		// indexFreqOfUSAS is a double array, so we just cast every element of temp as a double and assign
		// it to indexFreqOfUSAS
		for ( int i = 0 ; i < maxItems ; ++i ) {
			indexFreqOfUSAS[i] = (double)temp[i];
		}
	}
	
	/**
	 * calcCommonLex
	 * 
	 * calculates the most common lexicon of both just Latinos and everyone
	 * by iterating over everyones first lexicon and collects the most
	 * common item choices from each person. returns an ArrayList of items that are the most common
	 */
	public void calcCommonLex() {
		
		// initialize two TempLexicons that we will use to calculate the most common occurences
		// of each word type
		TempLexicon tempCommonLex = new TempLexicon();
		TempLexicon tempCommonLexTotal = new TempLexicon();
		TempLexicon tempCommonSecondLex = new TempLexicon();
		TempLexicon tempCommonSecondLexTotal = new TempLexicon();
		
		// re-initialize both ArrayLists
		commonLex = new ArrayList<int[]>();
		commonLexTotal = new ArrayList<int[]>();
		commonSecondLex = new ArrayList<int[]>();
		commonSecondLexTotal = new ArrayList<int[]>();
		
		// for every person in personList
		for ( Person p : personList ) {
			
			// if they have a first language
			if ( p.getFirstLang() != null ) {
				// for every item in their first lexicon
				for ( int i = 0 ; i < p.getFirstLang().getNumItems() ; ++i ) {
					// add it to tempCommonLexTotal to calculate the most common lexicon overall
					tempCommonLexTotal.hearItem( p.getFirstLang().getItem( i ) );
					// if they are Latino, add it to tempCommonLex to calculate the most common Latino lexicon
					if ( p.getRace() == LATINO )
						tempCommonLex.hearItem( p.getFirstLang().getItem( i ) );
				}
			}
			
			// if they have a second language
			if ( p.getSecondLang() != null ) {
				// for every item in their second lexicon
				for ( int i = 0 ; i < p.getSecondLang().getNumItems() ; ++i ) {
					// add it to tempCommonSecondLexTotal to calculate the most common second lexicon overall
					tempCommonSecondLexTotal.hearItem( p.getSecondLang().getItem( i ) );
					// if they are Latino, add it to tempCommonSecondLex to calculate the most common Latino lexicon
					if ( p.getRace() == LATINO )
						tempCommonSecondLex.hearItem( p.getSecondLang().getItem( i ) );
				}
			}
		}
		
		// for every possible item index (up to maxItems - 1)
		for ( int i = 0 ; i < maxItems ; ++i ) {
			
			// get the item that had the most occurrences with the current index
			// see the getMostHeardWithIndex method in the TempLexicon class for details
			int[] itemTotal = tempCommonLexTotal.getMostHeardWithIndex( i );
			
			// add it to the commonLexTotal if it is not null
			if ( itemTotal != null ) {
				commonLexTotal.add( itemTotal );
			}
			
			// get the item that had the most occurrences with the current index
			// see the getMostHeardWithIndex method in the TempLexicon class for details
			int[] item = tempCommonLex.getMostHeardWithIndex( i );
			
			// add it to the commonLex if it is not null
			if ( item != null ) {
				commonLex.add( item );
			}
		}
	}
	
	/**
	 * sameItem
	 * 
	 * Looks at all of the bits after the first one and decides if they are the same
	 */
	public boolean sameItem( int[] item1, int[] item2 ) {
		
		// return false if different sizes
		if ( item1.length != item2.length )
			return false;
		
		// for every bit in each item
		for ( int i = 1 ; i < item1.length ; ++i ) {
			// return false if the corresponding bits are different
			if ( item1[i] != item2[i] )
				return false;
		}
		
		// return true if everything checks out
		return true;
	}	
	
	/**
	 * sameInflection
	 * 
	 * Looks at all of the bits after the first two and decides whether the inflection is the same
	 */
	public boolean sameInflection( int[] item1, int[] item2 ) {
		
		// return false if different sizes
		if ( item1.length != item2.length )
			return false;
		
		// for every bit after the second in each item
		for ( int i = 2 ; i < item1.length ; ++i ) {
			// return false if the corresponding bits are different
			if ( item1[i] != item2[i] )
				return false;
		}
		
		// return true if everything checks out
		return true;
	}
	
	
	public void calcStats() {
		
		// set all of the statistics to zero
		numWhite = numBlack = numLatino = 0;
		numLatinoStudents = numBlackStudents = numLatinoAdults = numBlackAdults = 0;
		numChildren = numStudents = numAdults = 0;
		
		// for every person in personList
		for ( Person p : personList ) {
			
			// add one white person if they're white
			if ( p.getRace() == WHITE )
				++numWhite;
			// add one black person if they're black
			else if ( p.getRace() == BLACK )
				++numBlack;
			// add one Latino if they're Latino
			else if ( p.getRace() == LATINO )
				++numLatino;
			
			// if the person is an adult, increment the proper statistics
			if ( p.getAgeClass() == ADULT ) {
				++numAdults;
				if ( p.getRace() == BLACK )
					++numBlackAdults;
				else if ( p.getRace() == LATINO )
					++numLatinoAdults;
			}
			// if the person is a student, increment the proper statistics
			else if ( p.getAgeClass() == STUDENT ) {
				++numStudents;
				if ( p.getRace() == BLACK )
					++numBlackStudents;
				else if ( p.getRace() == LATINO )
					++numLatinoStudents;
			}
			// if the person is a child, increment the proper statistics
			else if ( p.getAgeClass() == CHILD ) {
				++numChildren;
			}
		}
		
		// the number of people is just the size of personList, because we don't include Teachers/BabySitters
		numTotal = personList.size();
		
		// calculate the percentages
		percBlack = (double)numBlack / numTotal;
		percLatino = (double)numLatino / numTotal;
		percWhite = (double)numWhite / numTotal;
		
		percChildren = (double)numChildren / numTotal;
		percAdults = (double)numAdults / numTotal;
		percStudents = 1 - percChildren - percAdults;
		
		// Latinos are the majority if they make up more than 50% of the population
		if ( percLatino < .5 )
			latinoMajority = false;
		else
			latinoMajority = true;
		
		// calculates the previous statistics
		// see above methods for details
		calcCommonLex();
		calcFreqOfUSAS();
		calcIndexFreqOfUSAS();
		calcFreqOfEnglish();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	// Array Methods
	////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * getRandomPersonFromArray
	 * 
	 * takes in an ArrayList of people and returns a random person from it
	 * returns null if the list is empty
	 */
	public Person getRandomPersonFromArray( ArrayList<Person> pArray ) {
		
		// return null if ArrayList is empty
		if ( pArray == null || pArray.size() == 0)
			return null;
		
		// if theres only one element in the array, return it
		if ( pArray.size() == 1 )
			return pArray.get( 0 );
		
		// otherwise, get the size of the array
		int numPeople = pArray.size();
		
		// and return a random person from it
		return pArray.get( rng.nextInt( numPeople ) );
	}
	
	/**
	 * getRandomPersonFromArray
	 * 
	 * Returns a random person from the speicifed age class
	 */
	public Person getRandomPersonFromArray( ArrayList<Person> pArray, int ageClass ) {
		
		// return null if ArrayList is empty
		if ( pArray == null || pArray.size() == 0)
			return null;
		
		// if theres only one element in the array, return it
		if ( pArray.size() == 1 )
			return pArray.get( 0 );
		
		// otherwise, get the size of the array
		int numPeople = pArray.size();
		
		// pick a random person from pArray
		Person p = pArray.get( rng.nextInt( numPeople ) );
		
		// initialize the number of tries to zero
		int count = 0;
		
		// while the randomly chosen person is not of the correct age class
		while ( p.getAgeClass() != ageClass ) {
			// pick another random person and increment the number of tries
			p = pArray.get( rng.nextInt( numPeople ) );
			++count;
			
			// return null if we have tried more times than there are people
			if ( count > numPeople )
				return null;
		}
		
		return p;
	}
	
	/**
	 * getRandomPersonFromArray
	 * 
	 * returns a random person from an array with the specified age class and gender
	 */
	public Person getRandomPersonFromArray( ArrayList<Person> pArray, int ageClass, int gender ) {
		
		// return null if ArrayList is empty
		if ( pArray == null || pArray.size() == 0)
			return null;
		
		// if theres only one element in the array, return it
		if ( pArray.size() == 1 )
			return pArray.get( 0 );
		
		// otherwise, get the size of the array
		int numPeople = pArray.size();
		
		// pick a random person from pArray
		Person p = pArray.get( rng.nextInt( numPeople ) );
		
		// initialize the number of tries to zero
		int count = 0;
		
		// while the randomly chosen person is not of the correct age class and gender
		while ( p.getAgeClass() != ageClass || p.getGender() != gender ) {
			// pick another random person and increment the number of tries
			p = pArray.get( rng.nextInt( numPeople ) );
			++count;
			
			// return null if we have tried more times than there are people
			if ( count > numPeople )
				return p;
		}
		
		return p;
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
	
	public void haltIfOverPopulated() {
		
		// if both the school and work place are full, then halt the model
		// the home space will not fill up because the birth method includes a catch
		if ( school.isFull() || work.isFull() ) {
			this.stop();
			System.out.print("\n\nOVERPOPULATED\n\n");
		}
	}
	
	///////////////////////////////////////////////
	///////////////// Comparators for Sorting
	///////////////////////////////////////////////
	
	/*
	 * These are just some Comparators that I thought might be useful at some point
	 * They're commented out so I don't get warnings
	 * 
	 * 
	 * 
	private class PersonAgeComparator implements Comparator<Person> {
	
		@Override
		public int compare ( Person p1, Person p2 ) {
			double age1 = p1.getAge();
			double age2 = p2.getAge();
			if ( age1 > age2 )    // d2 smaller, so should be before d1 (ascending order)
				return 1;
			else if ( age1 < age2 )
				return -1;
			return 0;
		}
	}
	
	private class PersonRaceComparator implements Comparator<Person> {
	
		@Override
		public int compare ( Person p1, Person p2 ) {
			double race1 = p1.getRace();
			double race2 = p2.getRace();
			if ( race1 > race2 )    // d2 smaller, so should be before d1 (ascending order)
				return 1;
			else if ( race1 < race2 )
				return -1;
			return 0;
		}
	}
	private class PersonClassComparator implements Comparator<Person> {
	
		@Override
		public int compare ( Person p1, Person p2 ) {
			double class1 = p1.getSocialClass();
			double class2 = p2.getSocialClass();
			if ( class1 > class2 )    // d2 smaller, so should be before d1 (ascending order)
				return 1;
			else if ( class1 < class2 )
				return -1;
			return 0;
		}
	}
	*/
	
	// sorts people in order of status
	private class PersonStatusComparator implements Comparator<Person> {
		
		@Override
		public int compare ( Person p1, Person p2 ) {
			double status1 = p1.getStatus();
			double status2 = p2.getStatus();
			if ( status1 > status2 )    // d2 smaller, so should be before d1 (ascending order)
				return 1;
			else if ( status1 < status2 )
				return -1;
			return 0;
		}
	}
	
	// returns tick count as an integer from a double
	public int tickCount() {
		return (int)getTickCount();
	}
	
	///////////////////////////////////////////////
	///////////////// Print Stuff
	///////////////////////////////////////////////
	/**
	// stepReport
	// called each model time step to write out lines that look like: 
    //     timeStep  ...data...data...data...
	// first it calls a method to calculate stats to be written.
	*/
	public void stepReport () {
		if ( rDebug > 0 )
			System.out.printf( "==> Model stepReport %.0f:\n", getTickCount() );

		calcStats();
		
		if ( getTickCount() %  reportFrequency == 0 ) {
			// set up a string with the values to write -- start with time step
			String s = String.format( "%5.0f  ", getTickCount() );

			// Append to String s here to write other data to report lines:

			s += String.format( " %3d ", personList.size()  );
//			s += String.format( "  %3d %6.2f", deathsPerStep, antPopAvgX );
//			s += String.format( "  %6.3f   %6.3f", antPopAvgDistanceFromSource, avgDStats.getMean()  );
//			s += String.format( "   %6.2f   %6.2f", avgProbRandomMove, avgProbDieCenter );

			// write it to the plain text report file, 'flush' buffer to file
			writeLineToPlaintextReportFile( s );
			getPlaintextReportFile().flush();
		}

	}
	
	
	
	public void writeHeaderCommentsToReportFile () {
		writeLineToPlaintextReportFile( "#                                    Win10   " );
		writeLineToPlaintextReportFile( "#       Num   Num  avg    AveDist   AveDist    Avg      Avg" );
		writeLineToPlaintextReportFile( "# time  Ants  Die  AntX   toSource  toSource  RandMov  PrDieC" );
	}
	
	///////////////////////////////////////////////
	///////////////// Getters and Setters
	///////////////////////////////////////////////
	

	public int getSizeX() {
		return sizeX;
	}
	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}
	public int getSizeY() {
		return sizeY;
	}
	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}
	public int getNumElements() { 
		return numElements;
	}
	public int getStepsPerYear() {
		return stepsPerYear;
	}
	public double getChildUSASFreq() {
		return childUSASFreq;
	}
	public void setChildUSASFreq(double childUSASFreq) {
		this.childUSASFreq = childUSASFreq;
	}
	public int getNumLatinoStudents() {
		return numLatinoStudents;
	}
	public void setNumLatinoStudents(int numLatinoStudents) {
		this.numLatinoStudents = numLatinoStudents;
	}
	public int getShowEnglishFreqGraph() {
		return showEnglishFreqGraph;
	}
	public void setShowEnglishFreqGraph(int showEnglishFreqGraph) {
		this.showEnglishFreqGraph = showEnglishFreqGraph;
	}
	public double getAdultProbGA() {
		return adultProbGA;
	}
	public void setAdultProbGA(double adultProbGA) {
		this.adultProbGA = adultProbGA;
	}
	public double getAdultProbL1() {
		return adultProbL1;
	}
	public void setAdultProbL1(double adultProbL1) {
		this.adultProbL1 = adultProbL1;
	}
	public double getAdultProbL2() {
		return adultProbL2;
	}
	public double getPercChildren() {
		return percChildren;
	}

	public void setPercChildren(double percChildren) {
		this.percChildren = percChildren;
	}

	public double getPercStudents() {
		return percStudents;
	}

	public void setPercStudents(double percStudents) {
		this.percStudents = percStudents;
	}

	public double getPercAdults() {
		return percAdults;
	}

	public void setPercAdults(double percAdults) {
		this.percAdults = percAdults;
	}

	public void setAdultProbL2(double adultProbL2) {
		this.adultProbL2 = adultProbL2;
	}
	public int getNumLatinoAdults() {
		return numLatinoAdults;
	}
	public void setNumLatinoAdults(int numLatinoAdults) {
		this.numLatinoAdults = numLatinoAdults;
	}
	public boolean isLatinoMajority() {
		return latinoMajority;
	}
	public void setLatinoMajority(boolean latinoMajority) {
		this.latinoMajority = latinoMajority;
	}
	public double getStudentUSASFreq() {
		return studentUSASFreq;
	}
	public int getRandomWordFlow() {
		return randomWordFlow;
	}
	public void setRandomWordFlow(int randomWordFlow) {
		this.randomWordFlow = randomWordFlow;
	}
	public void setStudentUSASFreq(double studentUSASFreq) {
		this.studentUSASFreq = studentUSASFreq;
	}
	public double getAdultUSASFreq() {
		return adultUSASFreq;
	}
	public void setAdultUSASFreq(double adultUSASFreq) {
		this.adultUSASFreq = adultUSASFreq;
	}
	public double getAfroSpanishFrequency() {
		return afroSpanishFrequency;
	}
	public void setAfroSpanishFrequency(int afroSpanishFrequency) {
		this.afroSpanishFrequency = afroSpanishFrequency;
	}	
	public double getBlackBirthRate() {
		return blackBirthRate;
	}
	public void setBlackBirthRate(double blackBirthRate) {
		this.blackBirthRate = blackBirthRate;
	}
	public double getLatinoBirthRate() {
		return latinoBirthRate;
	}
	public void setLatinoBirthRate(double latinoBirthRate) {
		this.latinoBirthRate = latinoBirthRate;
	}
	public double getWhiteBirthRate() {
		return whiteBirthRate;
	}
	public void setWhiteBirthRate(double whiteBirthRate) {
		this.whiteBirthRate = whiteBirthRate;
	}
	public double getPercBlack() {
		return percBlack;
	}
	public void setPercBlack(double percBlack) {
		this.percBlack = percBlack;
	}
	public int getImmigrationPerYear() {
		return immigrationPerYear;
	}
	public void setImmigrationPerYear(int immigrationPerYear) {
		this.immigrationPerYear = immigrationPerYear;
	}
	public int getWhiteFlightPerYear() {
		return whiteFlightPerYear;
	}
	public void setWhiteFlightPerYear(int whiteFlightPerYear) {
		this.whiteFlightPerYear = whiteFlightPerYear;
	}
	public double getPercLatino() {
		return percLatino;
	}
	public void setPercLatino(double percLatino) {
		this.percLatino = percLatino;
	}
	public double getPercWhite() {
		return percWhite;
	}
	public void setPercWhite(double percWhite) {
		this.percWhite = percWhite;
	}
	public int getWordThreshold() {
		return wordThreshold;
	}
	public void setWordThreshold(int wordThreshold) {
		this.wordThreshold = wordThreshold;
	}
	public int getWordFlow() {
		return wordFlow;
	}
	public void setWordFlow(int wordFlow) {
		this.wordFlow = wordFlow;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	// constructor, if need to do anything special.
	public Model () {
	}

	///////////////////////////////////////////////////////////////////////////
	// setup
	// set generic defaults after a run start or restart
	// calls userSetup() which the model-author defines
	//   for the specific model.

	public void setup () {
		schedule = null;
		if ( rDebug > 1 )
			System.out.printf( "==> Model-setup...\n" );

		userSetup();

		System.gc ();   // garabage collection of discarded objects
		super.setup();  // THIS SHOULD BE CALLED after setting defaults in setup().
		schedule = new Schedule (1);  // create AFTER calling super.setup()

		if ( rDebug > 1 )
			System.out.printf( "\n<=== Model-setup() done.\n" );

	}

	///////////////////////////////////////////////////////////////////////////
	// buildModel
	// build the generic "architecture" for the model,
	// and call userBuildModel() which the model-author defines
	// to create the model-specific components.

	public void buildModel () {
		if ( rDebug > 1 )
			System.out.printf( "==> buildModel...\n" );

		// CALL FIRST -- defined in super class -- it starts RNG, etc
		buildModelStart();

		userBuildModel();

		// some post-load finishing touches
		startReportFile();

		// you probably don't want to remove any of the following
		// calls to process parameter changes and write the
		// initial state to the report file.
		// NB -> you might remove/add more agentChange processing
        applyAnyStoredChanges();
        getReportFile().flush();
        getPlaintextReportFile().flush();

		if ( rDebug > 1 )
			System.out.printf( "<== buildModel done.\n" );
	}




	//////////////////////////////////////////////////////////////////////////////
	public Schedule getSchedule () { return schedule; }

	public String getName () { return "Model"; }



	/////////////////////////////////////////////////////////////////////////////
	// processEndOfRun
	// called once, at end of run.
	// writes some final info, closes report files, etc.
	public void processEndOfRun ( ) {
		if ( rDebug > 0 )  
			System.out.printf("\n\n===== processEndOfRun =====\n\n" );
		applyAnyStoredChanges();
		endReportFile();

		this.fireStopSim();
	}
	
	
}