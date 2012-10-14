package spanglish;


import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.analysis.BinDataSource;
import uchicago.src.sim.analysis.Histogram;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.AbstractGUIController;
import uchicago.src.sim.engine.Controller;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.gui.TextDisplay;

public class GUIModel extends Model {
	
	
	//////////////////////// CLASS VARIABLES
	
	// colormap and scaling variables
	public static ColorMap		 pherColorMap;
	public static final int      colorMapSize = 64;
	public static final double   colorMapMax =  colorMapSize - 1.0;
	
	
	//////////////////////// INSTANCE VARIABLES
	
	// Initialize the SimGraphics class for display
	public SimGraphics 			sim = new SimGraphics();
	
	// Class that displays the Objects
	private Object2DDisplay  worldDisplay;
	
	// Class that allows the physical display of the Objects and statistics
    private DisplaySurface	 dsurf, statsurf;

    // Initialize the two sequences
    public  OpenSequenceGraph		graph, englishGraph, ageGraph, raceGraph;
    
    // Initialize the three histograms
    public	Histogram				hist, classHist, ageHist;
    
    // Initialize the text box Classes
    public 	TextDisplay				text, lexTable, lexTableTotal;

    // used to display histograms as percents
	public double[] 			indexFreq, percents;
	
	// Utility variable, used to debugging
	public boolean				displayStats = true;
	public boolean				testStats = true;
	
	
	
	/**
	 * setup
	 * 
	 * This runs automatically when the model starts and when the reload button is clicked
	 * "tears down" all existing displays and prepares for initialization
	 */
	public void setup() {
		
		// the super class does conceptual-model setup
		super.setup();

		// NOTE: you may want to set these next two to 'true'
		// if you are on a windows machine.  that would tell repast
		// to by default send System.out and .err output to
		// a special repast output window.
		AbstractGUIController.CONSOLE_ERR = false;
		AbstractGUIController.CONSOLE_OUT = false;
		AbstractGUIController.UPDATE_PROBES = true;

		
		// "tears down" all of the existing display by first disposing of them, and then setting them to null
		if ( dsurf != null ) dsurf.dispose();
		if ( statsurf != null ) statsurf.dispose();
		if ( graph != null )  graph.dispose();
		if ( englishGraph != null ) englishGraph.dispose();
		if ( hist != null ) hist.dispose();
		if ( classHist != null ) classHist.dispose();
		if ( ageHist != null ) ageHist.dispose();
		if ( raceGraph != null ) raceGraph.dispose();
		if ( ageGraph != null ) ageGraph.dispose();
		graph = null;
		dsurf = null;
		statsurf = null;
		englishGraph = null;
		hist = null;
		classHist = null;
		ageHist = null;
		raceGraph = null;
		ageGraph = null;
		
		// tell the Person class we are in GUI mode.
		Person.setGUIModel( this );

		// initialize, setup and turn on the modelMinipulator stuff (in custom actions)
		modelManipulator.init();
		
	}
	
	/**
	 * begin
	 * 
	 * this runs when the initialize button is clicked.
	 */
	public void begin()	{
		DMSG(1, "==> enter GUIModel-begin()" );
		
		// see details in the Model class
		buildModel();
		
		// see details below, initilizes and builds all of the displays for the current GUIModel instance
		buildDisplay();
		
		// see details below, sets up the scheduling for the GUIModel instance
		buildSchedule();
		
		DMSG(1, "<== leave GUIModel-begin() done." );
	}

	/**
	 * buildDisplay
	 * 
	 * sets up all of the physical aspects of the GUIModel. 
	 */
	public void buildDisplay() {

		// create the object we see as the 2D "world" on the screen 
		dsurf = new DisplaySurface( this, "Agent Display" );
		
		// creates the statistics screen and names it
		statsurf = new DisplaySurface( this, "Statistics Display" );
		
		
		// gives the respective surfaces names
		registerDisplaySurface( "Main Display", dsurf );
		registerDisplaySurface( "Statistics Display", statsurf);

		
		// enable the custom action(s)
		modelManipulator.setEnabled( true );
		
		// create mapper object, from 2D GridWorld to the display surface
		worldDisplay = new Object2DDisplay( world );
		
		// speed up display of People -- just display them!
        worldDisplay.setObjectList( visibleList );

		// now add the display of agents
        dsurf.addDisplayableProbeable( worldDisplay, "Agents");
        
        // link to the other parts of the repast gui
        addSimEventListener( dsurf );

        // actually displays the agent display screen
        dsurf.display();
        
        // display the statistics screen if displayStats is true
        if ( displayStats || testStats)
        	statsurf.display();
		
		
		/*
		 * Setup for the graph of USAS frequency in Latinos
		 */
		
		// sets up the sequence of USAS frequency over all Latinos
		class SeqUSASFreq implements Sequence {
			@Override
			public double getSValue() {
				return afroSpanishFrequency;
			}
		}
		
		// sets up the sequence of USAS frequency over Latino children
		class SeqChildUSASFreq implements Sequence {
			@Override
			public double getSValue() {
				return childUSASFreq;
			}
		}
		
		// sets up the sequence of USAS frequency over Latino students
		class SeqStudentUSASFreq implements Sequence {
			@Override
			public double getSValue() {
				return studentUSASFreq;
			}
		}
		
		// sets up the sequence of USAS frequency over Latino adults
		class SeqAdultUSASFreq implements Sequence {
			@Override
			public double getSValue() {
				return adultUSASFreq;
			}
		}
		
		// Constructs the graph by naming it and connecting it to this instance of the GUIModel
		graph = new OpenSequenceGraph( "USAS Frequency", this );
		
		// Arbitrarily sets the X range
		graph.setXRange( 0, 200 );
		// at most, people can have maxItems USAS items, the minimum is set at -.001 for logistical reasons
		graph.setYRange( -.001, (double)maxItems );
		
		// sets the names of the axis titles
		graph.setAxisTitles( "time", "Average Frequency of USAS" );
		
		// add all of the sequences to the graph and set their respective colors
		graph.addSequence("USAS Freq", new SeqUSASFreq(), Color.BLACK );
		graph.addSequence("Child USAS Freq", new SeqChildUSASFreq(), Color.RED );
		graph.addSequence("Student USAS Freq", new SeqStudentUSASFreq(), Color.GREEN );
		graph.addSequence("Adult USAS Freq", new SeqAdultUSASFreq(), Color.BLUE );
		
		
		// sets up the sequence of percent Children
		class SeqPercWhite implements Sequence {
			@Override
			public double getSValue() {
				return percWhite;
			}
		}
		
		// sets up the sequence of percent Students
		class SeqPercBlack implements Sequence {
			@Override
			public double getSValue() {
				return percBlack;
			}
		}
		
		// sets up the sequence of percent Adults
		class SeqPercLatino implements Sequence {
			@Override
			public double getSValue() {
				return percLatino;
			}
		}
		
		
		// Construct age distribution graph and connect it to GUIModel instance
		raceGraph = new OpenSequenceGraph( "Race Graph", this );
		
		// Arbitrarily sets the X range
		raceGraph.setXRange( 0, 200 );
		// set Y range to include some percentage overflow
		raceGraph.setYRange( -.001, 1.0 );
		
		// set the axis titles
		raceGraph.setAxisTitles( "time", "Percent of Population" );
		
		// add the three sequences
		raceGraph.addSequence("Percent White", new SeqPercWhite(), Color.RED );
		raceGraph.addSequence("Percent Black", new SeqPercBlack(), Color.BLACK );
		raceGraph.addSequence("Percent Latino", new SeqPercLatino(), Color.BLUE );
		
		
		
		
		
		// sets up the sequence of percent Children
		class SeqPercChildren implements Sequence {
			@Override
			public double getSValue() {
				return percChildren;
			}
		}
		
		// sets up the sequence of percent Students
		class SeqPercStudents implements Sequence {
			@Override
			public double getSValue() {
				return percStudents;
			}
		}
		
		// sets up the sequence of percent Adults
		class SeqPercAdults implements Sequence {
			@Override
			public double getSValue() {
				return percAdults;
			}
		}
		
		
		// Construct age distribution graph and connect it to GUIModel instance
		ageGraph = new OpenSequenceGraph( "Age Class Graph", this );
		
		// Arbitrarily sets the X range
		ageGraph.setXRange( 0, 200 );
		// set Y range to include some percentage overflow
		ageGraph.setYRange( -.001, 1.2 );
		
		// set the axis titles
		ageGraph.setAxisTitles( "time", "Percent of Population" );
		
		// add the three sequences
		ageGraph.addSequence("Percent Children", new SeqPercChildren(), Color.RED );
		ageGraph.addSequence("Percent Students", new SeqPercStudents(), Color.BLUE );
		ageGraph.addSequence("Percent Adults", new SeqPercAdults(), Color.BLACK );
		
		
		// display the graph is displayStats is true
		if ( displayStats || testStats) {
			graph.display();
		}
		
		if ( displayStats || testStats ) {
			ageGraph.display();
			raceGraph.display();
		}
		
		
		/*
		 * Setup the graph for frequency of standard english and AAE
		 */
		
		// gets the average frequency of Standard English over all non-latinos as a sequence
		class SeqStandardFreq implements Sequence {
			@Override
			public double getSValue() {
				return standardFreq;
			}
		}
		
		// gets the average frequency of AAE over all non-latinos as a sequence
		class SeqAAEFreq implements Sequence {
			@Override
			public double getSValue() {
				return AAEFreq;
			}
		}
		
		// if showEnglishFreqGraph is set to true in the GUI and displayStats is true
		if ( showEnglishFreqGraph > 0 && displayStats ) {
			
			// Construct the graph by naming it and connecting it to the instance of the GUIModel
			englishGraph = new OpenSequenceGraph( "English Type Frequency ", this );
			
			// Arbitrarily sets the X range
			englishGraph.setXRange( 0, 200 );
			// at most, people can have maxItems items, the minimum is set at -.001 for logistical reasons
			englishGraph.setYRange( -.001, (double)maxItems );
			
			// sets the names of the axis titles
			englishGraph.setAxisTitles( "time", "Average Frequency of English Type" );
			
			// adds the sequences to the graphs and colors them respectively
			englishGraph.addSequence("Standard English Freq", new SeqStandardFreq(), Color.RED );
			englishGraph.addSequence("AAE Freq", new SeqAAEFreq(), Color.GREEN );
			
			// actually displays the graph
			englishGraph.display();
		}
		
		/*
		 * Item index frequency Histogram
		 */
		
		// names the histogram, sets the number of bins (maxItems), the max and min
		// (0 and maxItems), and connects it to the GUIModel instance
		hist = new Histogram( "USAS in Item Indices", maxItems, 0, (double)(maxItems), this );
		
		// names the axis titles
		hist.setAxisTitles( "Index Number", "% of USAS Occurences with Index " );
		
		// the histogram displays percents, so the maximum is 100
		hist.setYRange( 0, 100.0 );
		
		// the stats display for the histogram are irrelevant in this context
		hist.setStatsVisible( false );
		
		// display the histogram is displayStats is true
		if ( displayStats )
			hist.display();
		
		
		/*
		 * Class Distribution Histogram
		 */
		
		// names the histogram, sets the number of bins (3), the max and min
		// (0 and 3), and connects it to the GUIModel instance
		classHist = new Histogram( "Class Distribution", 3, 0.0, 3.0, this );
		
		// sets the names of the axis titles
		classHist.setAxisTitles( "Social Class" , "Number of People in the Social Class" );
		
		// the histogram displays percents, so the maximum is 100
		classHist.setYRange( 0, 100.0 );
		
		// the stats display for the histogram are irrelevant in this context
		classHist.setStatsVisible( false );
		
		// display the histogram is displayStats is true
		if (displayStats)
			classHist.display();
		
		
		/*
		 * Age Distribution Histogram
		 */
		
		// initializes a BinDataSource by taking in a Person and getting their age, allows for a display of
		// the age distribution
		BinDataSource ageSource = new BinDataSource() {
			@Override
			public double getBinValue( Object o ) {
				Person p = (Person)o;
				return (double)p.getAge();
			}
		};
		
		// names the histogram, sets the number of bins (85), the max and min
		// (0 and 85), and connects it to the GUIModel instance
		ageHist = new Histogram( " Age Distribution ", 85, 0, 85, this );
		
		// sets the names of the axis titlesthis collection of yours http://prod-images.exhibit-e.com/www_markmooregallery_com/BarelyInBrazil7.jpg .
		ageHist.setAxisTitles("Age", "Number of People Between Ages" );
		
		// creates the display of the age distribution by taking in the personList and processing their
		// ages using the BinDataSource constructed above
		ageHist.createHistogramItem("Age Distribution", personList, ageSource );
		
		// display the histogram is displayStats is true
		if ( displayStats )
			ageHist.display();
		
		
		/*
		 * Text Box displaying age group distribution
		 */
		
		// initialize the textbox by setting its position ( 1, 200 ) and setting the color of the text
		text = new TextDisplay(1, 200, Color.green );
		
		// add the line that displays the number of children
		text.addLine( "Number of Children: " + Integer.toString( numChildren ) );
		// add the line that displays the number of students
		text.addLine( "Number of Students: " + Integer.toString( numStudents ) );
		// add the line that displays the number of adults
		text.addLine( "Number of Adults: " + Integer.toString( numAdults ) );
		
		// display the text is displayStats is truethis collection of yours http://prod-images.exhibit-e.com/www_markmooregallery_com/BarelyInBrazil7.jpg .
		if ( displayStats || testStats )
			statsurf.addDisplayableProbeable( text, "text");
		
		/*
		 * Display of most common Latino Lexicon
		 * During this setup, the commonLex is all zeros. The actual calculation of the commonLex is done in the
		 * Model class, which one should reference for more detail
		 */
		
		// initialize the textbox by setting its position ( 1, 1 ) and setting the color of the text
		lexTable = new TextDisplay( 1, 1, Color.white );
		
		// adds the label of the lexicon
		lexTable.addLine( "Most Common Latino Lexicon" );
		
		// initializes an ArrayList that contains the most common latino lexicon
		commonLex = new ArrayList<int[]>();
		
		// for every item in the "commonLex"
		for ( int i = 0 ; i < maxItems ; ++i ) {
			
			// initialize a new empty item and add it to the commonLex
			int[] item = new int[numElements];
			commonLex.add( item );
			
			// initialize a new sting
			String newLine = new String();
			
			// for every bit in the item, convert it to a string and add it to the line
			for ( int j = 0 ; j < numElements ; ++j ) {
				newLine += Integer.toString( commonLex.get( i )[j] ) + " ";
			}
			
			// add the line to the table
			lexTable.addLine( newLine );
		}
		
		// add to the statistics display if displayStats is true
		if ( displayStats || testStats )
			statsurf.addDisplayableProbeable( lexTable, "lexTable");	
		
		
		
		/*
		 * Display of most common Lexicon overall
		 * During this setup, the commonLexTotal is all zeros. The actual calculation of the commonLex is done in the
		 * Model class, which one should reference for more detail
		 */
		
		// initialize the textbox by setting its position ( 200, 1 ) and setting the color of the text
		lexTableTotal = new TextDisplay( 200, 1, Color.white );
		
		// adds the label of the lexicon
		lexTableTotal.addLine( "Most Common Lexicon Overall" );
		
		// initializes an ArrayList that contains the most common lexicon
		commonLexTotal = new ArrayList<int[]>();
		
		// for every item in the "commonLexTotal"
		for ( int i = 0 ; i < maxItems ; ++i ) {
			
			// initialize a new empty item and add it to the commonLex
			int[] item = new int[numElements];
			commonLexTotal.add( item );
			
			// initialize a new sting
			String newLine = new String();
			
			// for every bit in the item, convert it to a string and add it to the line
			for ( int j = 0 ; j < numElements ; ++j ) {
				newLine += Integer.toString( commonLexTotal.get( i )[j] ) + " ";
			}
			
			// add the line to the table
			lexTableTotal.addLine( newLine );
		}		
		
		
		if ( tickCount() == 4000 ) {
			this.pause();
		}
		
		// add to the statistics display if displayStats is true
		if ( displayStats || testStats )
			statsurf.addDisplayableProbeable( lexTableTotal, "lexTableTotal");
	
	}


	////////////////////////////////////////////////////////////////
	// buildSchedule
	//
	// This builds the entire schedule, i.e., 
	//  - the base model step (calls stepReport)
	//  - display steps.

	public void buildSchedule() {

		if ( rDebug > 0 )
			System.out.printf( "==> GUIModel buildSchedule...\n" );

		// schedule the current GUIModel's step() function
		// to execute every time step starting with time step 0
		schedule.scheduleActionBeginning( 0, this, "step" );

		// schedule the current GUIModel's processEndOfRun() 
		// function to execute at the end of the run
		schedule.scheduleActionAtEnd( this, "processEndOfRun" );
	}


	/**
	 * getPercents
	 * 
	 * Takes in a double array of values, a min and a max. Returns a double array of size 100 with values in the
	 * array corresponding to percents. Ex, if valueList could be the size of personList, that only contains each
	 * person's social class, which ranges in between 0 and 2. Thus, we would set min=0 and max=1. If there were equal
	 * numbers of each class, then it would return {33, 33, 33}. This allows us to manipulate the Histogram to display
	 * percents
	 */
	public double[] getPercents( double[] valueList, int min, int max ) {
		
		// return null if the inputs are invalid
		if ( valueList == null || valueList.length == 0 )
			return null;
		
		// initialize a new double array with the proper number of bins. we will use this to measure the
		// frequency of each value in valueList
		double[] prop = new double[max - min + 1];
		
		// Initialize an ArrayList of Doubles and a double arrary of size 100 that we will eventually
		// use as the returned array
		ArrayList<Double> tempPercents = new ArrayList<Double>();
		double[] perc = new double[100];
		
		// for every item in valueList
		for ( int i = 0 ; i < valueList.length ; ++i ) {
			// casts the entry as an integer and references the corresponding index in prop. We then add
			// "1.0 / valueList.length" to signify that there is an instance of that specific value. Through this process,
			// prop will be an array list where each entry signifies the frequency of the corresponding index in valueList
			prop[(int)valueList[i]] += 1.0 / valueList.length;
		}
		
		// for every item in prop
		for ( int i = 0 ; i < prop.length ; ++i ) {
			
			// multiply by 100 and take the floor to represent the frequency as a percent value
			prop[i] *= 100;
			prop[i] = Math.floor( prop[i] );
		}
		
		// for every item in prop
		for ( int i = 0 ; i < prop.length ; ++i ) {
			// add the index to tempPercents as many times as the percent value signifies. ex: if prop signified that
			// 25% of valueList was 5, then the sixth slot of prop would hold the value 25.0. This loop would then add
			// 5 to to tempPercents twenty-five times
			for ( int j = 0 ; j < prop[i] ; ++j ) {
				tempPercents.add( (double)i );
			}
		}
		
		// by convention, there are going to be 100 or 99 entries in tempPercents. so this loop adds every value in tempPercents
		// to the double array perc, which is the array that we return
		for ( int i = 0 ; i < tempPercents.size() ; ++i ) {
			perc[i] = tempPercents.get( i );
		}
		
		return perc;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	// step
	//
	// executed each step of the model.
	// Ask the super-class to do its step() method,
	// and then this does display related activities.
	
	public void step() {

		// the Model class step method moves first
		super.step();

		// initialize a new double array that will contain the index of every instance of USAS across the entire population
		indexFreq = new double[USASList.size()];
		
		// USASList contains every item that is found in the entire population with USAS
		for ( int i = 0 ; i < USASList.size() ; ++i ) {
			// records the index of every instance of USAS across the population
			indexFreq[i] = (double)(USASList.get( i )[0]);
		}
		
		// records the index frequencies as percents
		percents = getPercents( indexFreq, 0, 9 );
		
		// if there are more than zero instances of USAS and displayStats is true, increment the histogram
		if ( USASList.size() > 0 && displayStats )
			hist.step( percents );
		
		
		/*
		 * Calculate stats for and plot class distribution Histogram
		 */
		
		// intialize a double array which is the size of the current population
		double[] classDist = new double[personList.size()];
		
		// record the social class of every person currently in the model and stick in the double array
		for ( int i = 0 ; i < personList.size() ; ++i ) {
			classDist[i] = (double)personList.get( i ).getSocialClass();
		}
		
		
		// if displayStats is true
		if ( displayStats ) {
			
			// get the social classes as a distribution and increments the histogram
			classHist.step( getPercents( classDist, 0, 2) );
			
			// also increments the age distribution
			ageHist.step();
		}
		
		
		/*
		 * Reset display of most common latino lexicon
		 */
		
		// clear the commonLex table and re-write the title
		lexTable.clearLines();
		lexTable.addLine( "Most Common Latino Lexicon" );
		
		// for every item in the commonLex
		for ( int i = 0 ; i < commonLex.size() ; ++i ) {
			
			// initialize a new String
			String newLine = new String();
			
			// for every bit in the current item
			for ( int j = 0 ; j < numElements ; ++j ) {
				// cast the bit as a String and add it to the newLine
				newLine += Integer.toString( commonLex.get( i )[j] ) + " ";
			}
			
			// add the line to the text display
			lexTable.addLine( newLine );
		}
		
		
		/*
		 * Reset display of most common lexicon overall
		 */
		
		// clear the commonLexTotal table and re-write the title
		lexTableTotal.clearLines();
		lexTableTotal.addLine( "Most Common Lexicon Overall" );
		
		// for every item in the commonLexTotal
		for ( int i = 0 ; i < commonLexTotal.size() ; ++i ) {
			
			// initialize a new String
			String newLine = new String();
			
			// for every bit in the current item
			for ( int j = 0 ; j < numElements ; ++j ) {
				// cast the bit as a String and add it to the newLine
				newLine += Integer.toString( commonLexTotal.get( i )[j] ) + " ";
			}
			
			// add the line to the text display
			lexTableTotal.addLine( newLine );
		}
		
		
		/*
		 * Setup Display of the demographics
		 */
		
		// clear the text
		text.clearLines();
		
		// gets the demographics as strings and adds them to the display
		text.addLine( "Number of Children: " + Integer.toString( numChildren ) );
		text.addLine( "Number of Students: " + Integer.toString( numStudents ) );
		text.addLine( "Number of Adults: " + Integer.toString( numAdults ) );
		
		
		// display the english type frequency if displayStats and showEnglishFreqGraph are true
		if ( showEnglishFreqGraph > 0 && displayStats )
			englishGraph.step();
		
		
		// Display the USAS graph and statistics display if displayStats is true
		if ( displayStats || testStats ) {
			statsurf.updateDisplay();
		}
		
		if ( displayStats || testStats ) {
			ageGraph.step();
			raceGraph.step();
			graph.step();
		}
		
		// always increment the agent display
		dsurf.updateDisplay();
		
		if ( tickCount() == 4000 )
			this.pause();
	}

	
	// processEndOfRun
	// called once, at end of run.
	public void processEndOfRun ( ) {
		if ( rDebug > 0 )  
			System.out.printf("\n\n===== GUIModel processEndOfRun =====\n\n" );
		applyAnyStoredChanges();
		endReportFile();
		this.fireStopSim();
	}
	

/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
//   ****  NO NEED TO CHANGE THE REST OF THIS  *****

	////////////////////////////////////////////////////////////////////
	// main entry point
	public static void main( String[] args ) {

		uchicago.src.sim.engine.SimInit init =
			new uchicago.src.sim.engine.SimInit();
		GUIModel model = new GUIModel();

		//System.out.printf("==> GUIMOdel main...\n" );

		// set the type of model class, this is necessary
		// so the parameters object knows whether or not
		// to do GUI related updates of panels,etc when a
		// parameter is changed
		model.setModelType("GUIModel");

        // Do this to set the Update Probes option to true in the
        // Repast Actions panel
        Controller.UPDATE_PROBES = true;

		model.setCommandLineArgs( args );
		init.loadModel( model, null, false ); // does setup()

		// this new function calls ProbeUtilities.updateProbePanels() and 
		// ProbeUtilities.updateModelProbePanel()
		model.updateAllProbePanels();

	}

}
