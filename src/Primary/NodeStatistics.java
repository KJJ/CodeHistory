package primary;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;


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
	private String[] revisionReference = new String[6];
	//the list of file groupings found in this instance of the Subversion log
	private GroupingList grouping = new GroupingList(); 
	//average number of relevant files per revision
	private double relevantAverage;
	//average time difference between 2 relevant revisions
	private long timeDiffAverage;
	//longest period of time between 2 relevant revisions
	private long timeDiffHigh;
	//shortest period of time between 2 relevant revisions
	private long timeDiffLow;
	//stores the various time intervals for later
	private String[] flowOfTime;
	//holds the various relevant revision number pairs in string format
	private String[] revisionsToo;
	//config file commands
	private ResourceBundle bundle = ResourceBundle.getBundle("config");
	//revision numbers in string format
	private String revisions[];
	//how many irrelevant files there are to each revision
	private String irrelevants[];
	//how many relevant files there are to each revision
	private String relevants[];
	//the rating for each revision are held here
	private String ratings[];
	//the number of commits per interval
	private String[] commits;
	//indicates which internal is being displayed, format; i + number
	private String[] intervals;
	//the date of the previous revision
	private Calendar lastTime;
	//holds data on where and when each file was present ( 1 = present, 0 = not present)
	private int[][] existsHere;
	//holds the comments for each revision
	private LinkedList<String> commenting;

	/**
	 * Constructor: initializes all fields to prepare for analysis
	 * @param list the list of RevisionNode's to be analyzed for patterns and statistics
	 * @param arg the queried files paired with the linked list
	 */
	public NodeStatistics(LinkedList<RevisionNode> list, String[] arg, LinkedList<Integer> intervalList){
		
		//initializations
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
		timeDiffHigh = Long.MIN_VALUE;
		timeDiffLow = Long.MAX_VALUE;
		flowOfTime = new String[list.size()];
		revisionsToo = new String[list.size()];
		commits = new String[intervalList.size()+1];
		intervals = new String[intervalList.size()+1];
		Iterator<Integer> i = intervalList.iterator();
		
		int j = 0;
		while (i.hasNext()) {
			commits[j] = Integer.toString(i.next());
			intervals[j] = "i" + (j + 1);
			j++;
		}
		
		//the following strings placed in all of these arrays are used in the csv file later
		
		if (list.size() != 0) {
			
			flowOfTime[list.size()-1] = "Time Between Revisions";
			revisionsToo[list.size()-1] = "Revision Pair";
			commits[commits.length-1] = "commits per";
			intervals[intervals.length-1] = "interval";
		}
		
		revisions = new String[list.size()+1];
		relevants = new String[list.size()+1];
		irrelevants = new String[list.size()+1];
		ratings = new String[list.size()+1];
		revisions[list.size()] = "Revision";
		relevants[list.size()] = "Relevants Present";
		irrelevants[list.size()]= "Irrelevants Present";
		ratings[list.size()] = "Rating of Relevance";
		existsHere = new int[list.size()][args.length];
		commenting = new LinkedList<String>();
	}
	
	/**
	 * analyze, as its name suggests, analyzes a RevisionNode list and finds helpful data on the revisions as a whole
	 * data found includes:
	 *  max/min/average relevance rating
	 *  max/min/average number of files changed in a revision
	 *  average number of relevant files
	 *  file groupings
	 */
	public void analyze(){
		
		//for pairing consecutive revisions for time interval output
		String previousRev = "";
		Iterator<RevisionNode> runThrough = toAnalyze.iterator(); //preparing to go through every relevant revision's data
		RevisionNode next = null; //null to determine later whether or not the list is empty or not
		//loop counter
		int j;
		
		while (runThrough.hasNext()){ 
			
			next = runThrough.next(); //the next revision's data node
			
			commenting.addLast(next.getComments());
			
			revisions[toAnalyze.indexOf(next)] = "r" + next.getRevision();
			relevants[toAnalyze.indexOf(next)] = Integer.toString(next.getNumberOfRelevants());
			
			for (j = 0; j < args.length; j++){
				LinkedList<String> relevantList = next.getRelevantFiles();
				if (relevantList.contains(args[j])) {
					existsHere[toAnalyze.indexOf(next)][j] = 1;
				}
			}
			
			if (next.getTotalChanges()-next.getNumberOfRelevants() >= 0) {
				irrelevants[toAnalyze.indexOf(next)]= Integer.toString(next.getTotalChanges()-next.getNumberOfRelevants());
			}
			else {
				irrelevants[toAnalyze.indexOf(next)]= "0";
			}
			ratings[toAnalyze.indexOf(next)] = Double.toString(next.getRating());
			
			Calendar thisTime = new GregorianCalendar(Integer.parseInt(next.getDate().split(" ")[0].split("-")[0]), Integer.parseInt(next.getDate().split(" ")[0].split("-")[1]) - 1,
					Integer.parseInt(next.getDate().split(" ")[0].split("-")[2]), Integer.parseInt(next.getDate().split(" ")[1].split(":")[0]),
						Integer.parseInt(next.getDate().split(" ")[1].split(":")[1]), Integer.parseInt(next.getDate().split(" ")[1].split(":")[2]));
		
			if (highestRating == -1){ //implies this is the first iteration
				lastTime = new GregorianCalendar(Integer.parseInt(next.getDate().split(" ")[0].split("-")[0]), Integer.parseInt(next.getDate().split(" ")[0].split("-")[1]) - 1, 
						Integer.parseInt(next.getDate().split(" ")[0].split("-")[2]), Integer.parseInt(next.getDate().split(" ")[1].split(":")[0]), 
							Integer.parseInt(next.getDate().split(" ")[1].split(":")[1]), Integer.parseInt(next.getDate().split(" ")[1].split(":")[2]));
				
				now = "Revision " + next.getRevision() + " at (" + next.getDate() + ")"; //the latest revision's date and number
				previousRev = next.getRevision();
			}
			
			else {
				long timeDiff = lastTime.getTimeInMillis() - thisTime.getTimeInMillis();
				String a = Double.toString(timeDiff / 1000 / 60 / 60 / 24.0);
				a = a.substring(0, a.indexOf('.')) + " days & "+ Math.round(Double.parseDouble(a.substring(a.indexOf("."))) * 24) + " hours";
				flowOfTime[toAnalyze.indexOf(next) - 1] = a;
				revisionsToo[toAnalyze.indexOf(next) - 1] = next.getRevision() + "-" + previousRev;
				
				if (timeDiff > timeDiffHigh){
					timeDiffHigh = timeDiff;
					revisionReference[4] = next.getRevision() + " & " + previousRev;
				}
				if (timeDiff < timeDiffLow){
					timeDiffLow = timeDiff;
					revisionReference[5] = next.getRevision() + " & " + previousRev;
				}
				
				timeDiffAverage += timeDiff;
				lastTime = thisTime;
			}
			
			
			relevantPresent[next.getNumberOfRelevants() - 1] += 1; //how many relevant files there are here
			if ((next.getTotalChanges() - next.getNumberOfRelevants()) < (Integer.parseInt(bundle.getString("irrelevantPerRelevant")) * next.getNumberOfRelevants())) {
				irrelevantPresent[next.getNumberOfRelevants() - 1] += 1; //having the acceptable number of irrelevant files
			}
			
			String files = "";
			Iterator<String> listIt = next.getRelevantFiles().iterator();
			while (listIt.hasNext()){
				String nextFile = listIt.next();
				nextFile = nextFile.substring(nextFile.lastIndexOf('/') + 1); //only take the file's name, not its path
				if (listIt.hasNext()){
					files += nextFile + ", "; //if this is not the last file, put in a comma
				}
				else {
					files += nextFile; //else, just enter the file name
				}
			}
				
			grouping.newInput(files, next.getRevision()); //send the group of files for file-group processing
			
			relevantAverage += next.getNumberOfRelevants(); //aggregation period of average calculation
			ratingAverage += next.getRating(); //aggregation period of average calculation
			nFilesAverage += next.getTotalChanges(); //aggregation period of average calculation
			if (next.getRating() > highestRating){
				//information for this rounding found on http://www.java-forums.org/advanced-java/4130-rounding-double-two-decimal-places.html
				highestRating = next.getRating() * 100000;
				highestRating = Math.round(highestRating);
				highestRating /= 100000;
				revisionReference[0] = next.getRevision();
			}
			if (next.getRating() < lowestRating){
				//information for this rounding found on http://www.java-forums.org/advanced-java/4130-rounding-double-two-decimal-places.html
				lowestRating = next.getRating() * 100000;
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
			
			previousRev = next.getRevision();
		}
		if (next != null) { //if the list was not empty
			then = "Revision " + next.getRevision() + " at (" + next.getDate() + ")"; //the last relevant revision and date of the log
			//information for this rounding found on http://www.java-forums.org/advanced-java/4130-rounding-double-two-decimal-places.html
			ratingAverage = (ratingAverage/revisionTotal) * 100000; 
			ratingAverage = Math.round(ratingAverage);
			ratingAverage /= 100000;
			nFilesAverage = nFilesAverage/revisionTotal; //rounding not used since it is an integer
			relevantAverage = relevantAverage/revisionTotal; //rounding not used since it is an integer
			if (revisionTotal > 1) {
				timeDiffAverage = timeDiffAverage/(revisionTotal - 1);
			}
			else {
				timeDiffLow = 0;
				timeDiffHigh = 0;
				revisionReference[4] = "N/A";
				revisionReference[5] = "N/A";
			}
		}
		
	}
	
	/**
	 * prints out all of the statistics that have been gathered from the log analysis and grouping code
	 * @throws IOException in the event of a csv error
	 */
	public void statsOut(PrintWriter out) throws IOException {
		analyze(); //see the above method
		int i;
		
		if (bundle.getString("otherStatsToggle").equals("true") || bundle.getString("groupsToggle").equals("true") || bundle.getString("percentToggle").equals("true") || 
																																		bundle.getString("diffToggle").equals("true")) {
			System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");
			out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");
		}
		
		if (lowestRating == 2) { //implies no data was ever given since no rating will ever be above 1, let alone at 2
			System.out.println("Nothing here to give statistics on \n");
			out.println("Nothing here to give statistics on \n");
		}
		
		else {
			if (bundle.getString("otherStatsToggle").equals("true")) {
				System.out.println("Relevant Segment of Revision History: " + then + " to " + now + " \n");
				out.println("Relevant Segment of Revision History: " + then + " to " + now + " \n");
				System.out.println("Total Number of Relevant Revisions: " + revisionTotal);
				out.println("Total Number of Relevant Revisions: " + revisionTotal);
				System.out.println("\t Average Number of relevant files per revision: " + Math.round(relevantAverage) + "\n");
				out.println("\t Average Number of relevant files per revision: " + Math.round(relevantAverage) + "\n");
				for (i = 0; i < args.length; i++) {
					System.out.println("\t Number of Revisions Changing " + (i+1) + " of the Relevant Files: " + relevantPresent[i]);
					out.println("\t Number of Revisions Changing " + (i+1) + " of the Relevant Files: " + relevantPresent[i]);
					System.out.println("\t\t Number of these revisions with under " + (10 * (i + 1)) + " irrelevant extra files: " + irrelevantPresent[i] + "\n");
					out.println("\t\t Number of these revisions with under " + (10 * (i + 1)) + " irrelevant extra files: " + irrelevantPresent[i] + "\n");
				}
		
				System.out.println();
				out.println();
				if (timeDiffAverage / 1000 / 60 < 1){
					System.out.println("Average Time Between Revisions: " + timeDiffAverage / 1000 + " seconds");
					out.println("Average Time Between Revisions: " + timeDiffAverage / 1000 + " seconds");
				}
				else if (timeDiffAverage / 1000 / 60 / 60 < 1){
					System.out.println("Average Time Between Revisions: " + timeDiffAverage / 1000 / 60 + " minutes");
					out.println("Average Time Between Revisions: " + timeDiffAverage / 1000 / 60 + " minutes");
				}
				else if (timeDiffAverage / 1000 / 60 / 60 < 1000) {
					System.out.println("Average Time Between Revisions: " + timeDiffAverage / 1000 / 60 / 60 + " hours");
					out.println("Average Time Between Revisions: " + timeDiffAverage / 1000 / 60 / 60 + " hours");
				}
				else {
					System.out.println("Average Time Between Revisions: " + timeDiffAverage / 1000 / 60 / 60 / 24 + " days");
					out.println("Average Time Between Revisions: " + timeDiffAverage / 1000 / 60 / 60 / 24 + " days");
				}
				
				if (timeDiffLow / 1000 / 60 < 1){
					System.out.println("\t Lowest Time Between Revisions: " + timeDiffLow / 1000 + " seconds between Revisions "+revisionReference[5]);
					out.println("\t Lowest Time Between Revisions: " + timeDiffLow / 1000 + " seconds between Revisions "+revisionReference[5]);
				}
				else if (timeDiffLow / 1000 / 60 / 60 < 1){
					System.out.println("\t Lowest Time Between Revisions: " + timeDiffLow / 1000 / 60 + " minutes between Revisions "+revisionReference[5]);
					out.println("\t Lowest Time Between Revisions: " + timeDiffLow / 1000 / 60 + " minutes between Revisions "+revisionReference[5]);
				}
				else if (timeDiffLow / 1000 / 60 / 60 < 1000) {
					System.out.println("\t Lowest Time Between Revisions: " + timeDiffLow / 1000 / 60 / 60 + " hours between Revisions "+revisionReference[5]);
					out.println("\t Lowest Time Between Revisions: " + timeDiffLow / 1000 / 60 / 60 + " hours between Revisions "+revisionReference[5]);
				}
				else {
					System.out.println("\t Lowest Time Between Revisions: " + timeDiffLow / 1000 / 60 / 60 / 24 + " days between Revisions "+revisionReference[5]);
					out.println("\t Lowest Time Between Revisions: " + timeDiffLow / 1000 / 60 / 60 / 24 + " days between Revisions "+revisionReference[5]);
				}
				
				if (timeDiffHigh / 1000 / 60 < 1){
					System.out.println("\t Highest Time Between Revisions: " + timeDiffHigh / 1000 +" seconds between Revisions "+revisionReference[4]);
					out.println("\t Highest Time Between Revisions: " + timeDiffHigh / 1000 +" seconds between Revisions "+revisionReference[4]);
				}
				else if (timeDiffHigh/1000 / 60 / 60 < 1){
					System.out.println("\t Highest Time Between Revisions: " + timeDiffHigh / 1000 / 60 +" minutes between Revisions "+revisionReference[4]);
					out.println("\t Highest Time Between Revisions: " + timeDiffHigh / 1000 / 60 +" minutes between Revisions "+revisionReference[4]);
				}
				else if (timeDiffHigh / 1000 / 60 / 60 < 1000) {
					System.out.println("\t Highest Time Between Revisions: " + timeDiffHigh / 1000 / 60 / 60 +" hours between Revisions "+revisionReference[4]);
					out.println("\t Highest Time Between Revisions: " + timeDiffHigh / 1000 / 60 / 60 +" hours between Revisions "+revisionReference[4]);
				}
				else {
					System.out.println("\t Highest Time Between Revisions: " + timeDiffHigh / 1000 / 60 / 60 / 24 + " days between Revisions "+revisionReference[4]);
					out.println("\t Highest Time Between Revisions: " + timeDiffHigh / 1000 / 60 / 60 / 24 + " days between Revisions "+revisionReference[4]);
				}
				
				System.out.println("\nAverage Rating: " + ratingAverage);
				out.println("\nAverage Rating: " + ratingAverage);
				System.out.println("\t Lowest Rating: " + lowestRating + " for Revision " + revisionReference[1]);
				out.println("\t Lowest Rating: " + lowestRating + " for Revision " + revisionReference[1]);
				System.out.println("\t Highest Rating: " + highestRating + " for Revision " + revisionReference[0] + "\n");
				out.println("\t Highest Rating: " + highestRating + " for Revision " + revisionReference[0] + "\n");
		
				System.out.println("Average Number of Changed files: " + nFilesAverage);
				out.println("Average Number of Changed files: " + nFilesAverage);
				System.out.println("\t Lowest Number of Changed Files: " + lowestFileNumber + " changed at Revision " + revisionReference[3]);
				out.println("\t Lowest Number of Changed Files: " + lowestFileNumber + " changed at Revision " + revisionReference[3]);
				System.out.println("\t Highest Number of Changed Files: " + highestFileNumber + " changed at Revision " + revisionReference[2] + "\n");
				out.println("\t Highest Number of Changed Files: " + highestFileNumber + " changed at Revision " + revisionReference[2] + "\n");
			}
			
			if (bundle.getString("groupsToggle").equals("true")) {
				grouping.currentOutput(out);
			}
			if (args.length > 1) {
				if (bundle.getString("percentToggle").equals("true")) {
					percentages(args, out);
				}
			}
			
			if (bundle.getString("CSVToggle").equals("true")) {
				csv();
			}
			
			if (bundle.getString("diffToggle").equals("true") && revisionTotal > 1) {		
				diff(out);
			}
			
			if (bundle.getString("commentToggle").equals("true")) {
				System.out.println("Revision Comments (IMPORTANT DISCLAIMER: all commas have been replaced with semi-colons to allow insertion into a csv file): \n");
				out.println("Revision Comments (IMPORTANT DISCLAIMER: all commas have been replaced with semi-colons to allow insertion into a csv file): \n");
				for (i = 0; i < commenting.size(); i++) {
					System.out.println("\t" + revisions[i] + ":");
					out.println("\t" + revisions[i] + ":");
					System.out.println(commenting.get(i) + "\n");
					out.println(commenting.get(i) + "\n");
				}
			}

		}
		
		
		
		if (bundle.getString("otherStatsToggle").equals("true") || bundle.getString("groupsToggle").equals("true") || 
				bundle.getString("percentToggle").equals("true") || bundle.getString("diffToggle").equals("true")) {
			
			System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");
			out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");
		}

	}
	
	public void csv() throws IOException {
		System.out.println(); //spacing
		FileWriter f = new FileWriter(bundle.getString("csvName"));
		PrintWriter p = new PrintWriter(f);
		Object[][] input = {revisionsToo, flowOfTime};
		CSVWork(input, p, f);
		Object[][] nextInput = {revisions, ratings};
		CSVWork(nextInput, p, f);
		nextInput[1] = irrelevants;
		CSVWork(nextInput, p, f);
		nextInput[1] = relevants;
		CSVWork(nextInput, p, f);
		nextInput[0] = intervals;
		nextInput[1] = commits;
		CSVWork(nextInput, p, f);
		specialCSV(existsHere, p, f);
		p.flush();
		p.close();
		f.close();
	}
	
	//IO code from http://javacodeonline.blogspot.com/2009/09/java-code-to-write-to-csv-file.html
	public void CSVWork(Object[][] arrayIn, PrintWriter p, FileWriter f) throws IOException {
		int i, j;

		for (i = arrayIn[0].length - 1; i >= 0; i--){
			for (j = 0; j < arrayIn.length; j++) {
				p.print(arrayIn[j][i] + ",");
			}
			p.println();
		}
		p.println();
	}
	
	//IO code from http://javacodeonline.blogspot.com/2009/09/java-code-to-write-to-csv-file.html
	public void specialCSV(int[][] arrayIn, PrintWriter p, FileWriter f) throws IOException {
		int i, j;
		p.print(",");
		for (j = 0; j < args.length; j++){
			p.print(args[j].substring(args[j].lastIndexOf('/') + 1) + ",");
		}
		p.print("Time elapsed since the last significant revision:,");
		p.print("Comments:,");
		p.println();
		
		for (i = arrayIn.length - 1; i >= 0; i--){
			p.print(revisions[i] + ",");
			for (j = 0; j < arrayIn[i].length; j++) {
				
				if (arrayIn[i][j] == 1) {
					p.print("1,");
				}
				else {
					p.print(",");
				}
			}
			if (i < arrayIn.length - 1) {
				p.print(flowOfTime[i] + ",");
			}
			else {
				p.print("N/A,");
			}
			p.print(commenting.get(i));
			p.println();
		}
		p.println();
	}
	
	public void diff(PrintWriter out) throws IOException{
		int j, i;
		String pathFull = "";
		DiffParser dp = new DiffParser();
		String path = bundle.getString(bundle.getString("repo"));
		System.out.print("\t");
		
		for (i = 0; i < args.length; i++) {
			System.out.print("\t\t " + args[i].substring(args[i].lastIndexOf('/')));
			out.print("\t\t " + args[i].substring(args[i].lastIndexOf('/')));
		}
		
		System.out.println("\n");
		out.println("\n");
		
			for (i = 0; i < revisionsToo.length-1; i++) {
				System.out.print("Revisions " + revisionsToo[i] + ":\t");
				out.print("Revisions " + revisionsToo[i] + ":\t");
				
				for (j = 0; j < args.length; j++) {
					pathFull = path + args[j] + " ";
					Process exec = Runtime.getRuntime().exec("svn diff -r " + revisionsToo[i].split("-")[0] + ":" + revisionsToo[i].split("-")[1] + " " + pathFull);
					dp.diffOut(exec, out);
				}
				
				System.out.println("\n");
				out.println("\n");
			}
	}
	
	/**
	 * percentages, using the user-defined parameters coupled with the parsed log information it determines
	 * the percentage of the occurrences of one file's changes in relation to the presence of another at that revision
	 * for all files in the parameters
	 * @param files relevant files, as determined by the user in the command-line parameters
	 * @throws IOException 
	 */
	public void percentages(String[] files, PrintWriter out) throws IOException {
		int i, j; //loop counters
		int[] infoArray; //array that holds the counters for each file-to-file comparison 
		LinkedList<RevisionNode> theList = toAnalyze; // takes a copy of the RevisionNode list of data parsed from the log
		
		for (i = 0; i < files.length; i++) { //for the every element in the parameter array
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
			out.println();
			System.out.println("For the file " + files[i].substring(files[i].lastIndexOf("/") + 1) + ":"); //indicates what file we are talking about
			out.println("For the file " + files[i].substring(files[i].lastIndexOf("/") + 1) + ":");
			for (j = 0; j < files.length; j++) { //for the length of our data array
				if (j != i) { //ignoring the current files slot since it would be 100% no matter what
					int percent = (int) Math.round((infoArray[j] / (double) infoArray[i]) * 100); //round to the nearest percent
					//indicates that when file i is changed in a revision, file j is also changed at the same revision percent% of the time
					System.out.println("\t When changed, " + files[j].substring(files[j].lastIndexOf("/") + 1) + " is changed " + percent + "% of the time.");
					out.println("\t When changed, " + files[j].substring(files[j].lastIndexOf("/") + 1) + " is changed " + percent + "% of the time.");
				}
			}
		}
	}
}