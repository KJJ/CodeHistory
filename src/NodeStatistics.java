import java.util.Iterator;
import java.util.LinkedList;


public class NodeStatistics {
	
	//toAnalyze is the linked list holding the raw revision data
	private LinkedList<RevisionNode> toAnalyze;
	//Total number of revisions being analyzed
	private int revisionTotal;
	//values used to reference the rating statistics
	private double ratingAverage, highestRating, lowestRating;
	//values for file statistics
	private int nFilesAverage, highestFileNumber, lowestFileNumber;
	//holds the information for the first and last revisions of the list to put the information in a time-based context
	private String then, now;
	//holds the names of the queried files that helped create the list being analyzed
	private String[] args;
	//how many relevant files are present at any one time
	private int[] relevantPresent;
	//how many files are not relevant based on filtering
	private int[] irrelevantPresent;
	//holds the revision numbers for the bounds in the numerical statistics sections
	private String[] revisionReference = new String[4];
	//the list of file groupings found in this instance of the Subversion log
	private GroupingList grouping = new GroupingList(); 
	//average number of relevant files per revision
	private int relevantAverage;
	
	/**
	 * Constructor: initializes all fields to prepare for analysis
	 * @param list the list of RevisionNode's to be analyzed for patterns and statistics
	 * @param arg the queried files paired with the linked list
	 */
	public NodeStatistics(LinkedList<RevisionNode> list, String[] arg){
		toAnalyze = list;
		revisionTotal = list.size();
		ratingAverage = 0;
		highestRating = -1;
		lowestRating = 2;
		nFilesAverage = 0;
		highestFileNumber = Integer.MIN_VALUE;
		lowestFileNumber = Integer.MAX_VALUE;
		args = arg;
		relevantPresent = new int[arg.length];
		irrelevantPresent = new int[arg.length];
		relevantAverage = 0;
	}
	
	/**
	 * analyze, as its name suggests, analyzes a RevisionNode list and finds helpful data on the revisions as a whole
	 * data found includes:
	 *  max/min/average relevance rating
	 *  max/min/average number of files changed i a revision
	 *  average number of relevant files
	 *  file groupings
	 */
	public void analyze(){
		Iterator<RevisionNode> runThrough = toAnalyze.iterator(); //preparing to go through every relevant revision's data
		RevisionNode next = null; //null to determine later whether or not the list is empty or not
		while (runThrough.hasNext()){ 
			
			next = runThrough.next(); //the next revision's data node
			
			if (highestRating == -1){ //implies this is the first iteration
				now = "Revision "+next.getRevision()+" at ("+next.getDate()+")"; //the latest revision's date and number
			}
			
			relevantPresent[next.getNumberOfRelevants()-1] += 1; //how many relevant files there are here
			if ((next.getTotalChanges()-next.getNumberOfRelevants()) < (10*next.getNumberOfRelevants())) {
				irrelevantPresent[next.getNumberOfRelevants()-1] += 1; //having the acceptable number of irrelevant files
			}
			
			String files = "";
			Iterator<String> listIt = next.getRelevantFiles().iterator();
			while (listIt.hasNext()){
				String nextFile = listIt.next();
				nextFile = nextFile.substring(nextFile.lastIndexOf('/')+1); //only take the file's name, not its path
				if (listIt.hasNext()){
					files += nextFile+", "; //if this is not the last file, put in a comma
				}
				else {
					files += nextFile; //else, just enter the file name
				}
			}
				grouping.newInput(files); //send the group of files for file-group processing
			
			relevantAverage += next.getNumberOfRelevants(); //aggregation period of average calculation
			ratingAverage += next.getRating(); //aggregation period of average calculation
			nFilesAverage += next.getTotalChanges(); //aggregation period of average calculation
			if (next.getRating() > highestRating){
				//information for this rounding found on http://www.java-forums.org/advanced-java/4130-rounding-double-two-decimal-places.html
				highestRating = next.getRating() *100000;
				highestRating = Math.round(highestRating);
				highestRating /= 100000;
				revisionReference[0] = next.getRevision();
			}
			if (next.getRating() < lowestRating){
				//information for this rounding found on http://www.java-forums.org/advanced-java/4130-rounding-double-two-decimal-places.html
				lowestRating = next.getRating() *100000;
				lowestRating = Math.round(lowestRating);
				lowestRating /= 100000;
				revisionReference[1] = next.getRevision();
			}
			
			if (next.getTotalChanges() > highestFileNumber){ //is the current file count higher than the current maximum?
				highestFileNumber = next.getTotalChanges(); //changes the value to the new maximum
				revisionReference[2] = next.getRevision(); //gets what revision this was found at for later reference
			}
			if (next.getTotalChanges() < lowestFileNumber){ //is the current file count lower than the current minimum?
				lowestFileNumber = next.getTotalChanges(); //changes the value to the new minimum
				revisionReference[3] = next.getRevision(); //gets what revision this was found at for later reference
			}
			
		}
		if (next != null) { //if the list was not empty
			then = "Revision "+next.getRevision()+" at ("+next.getDate()+")"; //the last relevant revision and date of the log
			//information for this rounding found on http://www.java-forums.org/advanced-java/4130-rounding-double-two-decimal-places.html
			ratingAverage = (ratingAverage/revisionTotal)*100000; 
			ratingAverage = Math.round(ratingAverage);
			ratingAverage /= 100000;
			nFilesAverage = nFilesAverage/revisionTotal; //rounding not used since it is an integer
			relevantAverage = relevantAverage/revisionTotal; //rounding not used since it is an integer
		}
		
	}
	
	/**
	 * prints out all of the statistics that have been gathered from the log analysis and grouping code
	 */
	public void statsOut() {
		analyze();
		
		System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");
		
		if (lowestRating == 2) { //implies no data was ever given since no rating will ever be above 1, let alone at 2
			System.out.println("Nothing here to give statistics on \n");
		}
		
		else {
			System.out.println("Relevant Segment of Revision History: "+then+" to "+now+" \n");
		
			System.out.println("Total Number of Relevant Revisions: " + revisionTotal);
			System.out.println("\t Average Number of relevant files per revision: "+ relevantAverage + "\n");
			int i;
			for (i = 0; i < args.length; i++) {
				System.out.println("\t Number of Revisions Changing "+(i+1)+" of the Relevant Files: " + relevantPresent[i]);
				System.out.println("\t\t Number of these revisions with under "+(10*(i+1))+" irrelevant extra files: " + irrelevantPresent[i] + "\n");
			}
		
			System.out.println();
		
			System.out.println("Average Rating: "+ ratingAverage);
			System.out.println("\t Lowest Rating: " + lowestRating+" for Revision "+revisionReference[1]);
			System.out.println("\t Highest Rating: " + highestRating +" for Revision "+revisionReference[0]+ "\n");
		
			System.out.println("Average Number of Changed files: "+ nFilesAverage);
			System.out.println("\t Lowest Number of Changed Files: " + lowestFileNumber+" changed at Revision "+revisionReference[3]);
			System.out.println("\t Highest Number of Changed Files: " + highestFileNumber+" changed at Revision "+revisionReference[2] + "\n");
			
			grouping.currentOutput();
			if (args.length > 1) {
				percentages(args);
			}
		}
		
		System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");

	}
	
	/**
	 * percentages, using the user-defined parameters coupled with the parsed log information it determines
	 * the percentage of the occurrences of one file's changes in relation to the presence of another at that revision
	 * for all files in the parameters
	 * @param files relevant files, as determined by the user in the command-line parameters
	 */
	public void percentages(String[] files) {
		int i, j; //loop counters
		int[] infoArray; //array that holds the counters for each file-to-file comparison 
		LinkedList<RevisionNode> theList = toAnalyze; // takes a copy of the RevisionNode list of data parsed from the log
		
		for (i=0; i < files.length; i++) { //for the every element in the parameter array
			infoArray = new int[files.length]; //set the array length to reflect how many elements there are
			Iterator<RevisionNode> lIterator = theList.iterator(); //iterate through the list, for every node
			while (lIterator.hasNext()) { //for every node in the RevisionNode list
				RevisionNode next = lIterator.next(); //take the next node in the list
				if (next.getRelevantFiles().contains(files[i])) { //if the current revision involved the current file, else skip this
					for (j = 0; j < files.length; j++) { //checks for the presence of every other file
						if (next.getRelevantFiles().contains(files[j])) { //if another is present
							infoArray[j] += 1; //increment its respective counter, also counts occurrences of the current file as well
						}
					}
				}
			}
			System.out.println(); //spacing
			System.out.println("For the file "+files[i].substring(files[i].lastIndexOf("/")+1)+":"); //indicates what file we are talking about
			for (j = 0; j < files.length; j++) { //for the length of our data array
				if (j != i) { //ignoring the current files slot since it would be 100% no matte what
					int percent = (int) Math.round((infoArray[j]/(double)infoArray[i])*100); //round to the nearest percent
					//indicates that when file i is changed in a revision, file j is also changed at the same revision percent% of the time
					System.out.println("\t When changed, " + files[j].substring(files[j].lastIndexOf("/")+1) +" is changed "+percent+"% of the time.");
				}
			}
		}
		System.out.println(); //spacing
	}
}