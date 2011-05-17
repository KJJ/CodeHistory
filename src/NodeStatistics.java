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
	
	private GroupingList grouping = new GroupingList(); 
	
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
	
	public void analyze(){
		Iterator<RevisionNode> runThrough = toAnalyze.iterator(); 
		RevisionNode next = null;
		while (runThrough.hasNext()){
			
			next = runThrough.next();
			
			if (highestRating == -1){
				now = "Revision "+next.getRevision()+" at ("+next.getDate()+")";
			}
			
			relevantPresent[next.getNumberOfRelevants()-1] += 1;
			if ((next.getTotalChanges()-next.getNumberOfRelevants()) < (10*next.getNumberOfRelevants())) {
				irrelevantPresent[next.getNumberOfRelevants()-1] += 1;
			}
			
			String files = "";
			Iterator<String> listIt = next.getRelevantFiles().iterator();
			while (listIt.hasNext()){
				String nextFile = listIt.next();
				nextFile = nextFile.substring(nextFile.lastIndexOf('/')+1);
				if (listIt.hasNext()){
					files += nextFile+", ";
				}
				else {
					files += nextFile;
				}
			}
				grouping.newInput(files);
			
			relevantAverage += next.getNumberOfRelevants();
			ratingAverage += next.getRating();
			nFilesAverage += next.getTotalChanges();
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
			
			if (next.getTotalChanges() > highestFileNumber){
				highestFileNumber = next.getTotalChanges();
				revisionReference[2] = next.getRevision();
			}
			if (next.getTotalChanges() < lowestFileNumber){
				lowestFileNumber = next.getTotalChanges();
				revisionReference[3] = next.getRevision();
			}
			
		}
		if (next != null) {
			then = "Revision "+next.getRevision()+" at ("+next.getDate()+")";
			ratingAverage = (ratingAverage/revisionTotal)*100000;
			ratingAverage = Math.round(ratingAverage);
			ratingAverage /= 100000;
			nFilesAverage = nFilesAverage/revisionTotal;
			relevantAverage = relevantAverage/revisionTotal;
		}
		
	}
	
	public void statsOut() {
		analyze();
		
		System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");
		
		if (lowestRating == 2) {
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
		}
		
		System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");

	}
}
