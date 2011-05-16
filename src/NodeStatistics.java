import java.util.Iterator;
import java.util.LinkedList;


public class NodeStatistics {
	
	private LinkedList<RevisionNode> toAnalyze;
	private int revisionTotal;
	private double ratingAverage, highestRating, lowestRating;
	private int nFilesAverage, highestFileNumber, lowestFileNumber;
	private String then, now;
	private String[] args;
	private int[] relevantPresent;
	private int[] irrelevantPresent;
	private String[] revisionReference = new String[4];
	
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
			
			ratingAverage += next.getRating();
			nFilesAverage += next.getTotalChanges();
			if (next.getRating() > highestRating){
				highestRating = next.getRating() *100000;
				highestRating = Math.round(highestRating);
				highestRating /= 100000;
				revisionReference[0] = next.getRevision();
			}
			if (next.getRating() < lowestRating){
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
		}
		
		System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");

	}
}
