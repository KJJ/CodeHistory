import java.util.Iterator;
import java.util.LinkedList;


public class NodeStatistics {
	
	private LinkedList<RevisionNode> toAnalyze;
	private int revisionTotal;
	private double ratingAverage, highestRating, lowestRating;
	private int nFilesAverage, highestFileNumber, lowestFileNumber;
	private String then, now;
	
	public NodeStatistics(LinkedList<RevisionNode> list){
		toAnalyze = list;
		revisionTotal = list.size();
		ratingAverage = 0;
		highestRating = -1;
		lowestRating = 2;
		nFilesAverage = 0;
		highestFileNumber = Integer.MIN_VALUE;
		lowestFileNumber = Integer.MAX_VALUE;
	}
	
	public void analyze(){
		Iterator<RevisionNode> runThrough = toAnalyze.iterator(); 
		RevisionNode next = null;
		while (runThrough.hasNext()){
			
			next = runThrough.next();
			
			if (highestRating == -1){
				now = next.getDate();
			}
			
			ratingAverage += next.getRating();
			nFilesAverage += next.getTotalChanges();
			if (next.getRating() > highestRating){
				highestRating = next.getRating() *100000;
				highestRating = Math.round(highestRating);
				highestRating /= 100000;
			}
			if (next.getRating() < lowestRating){
				lowestRating = next.getRating() *100000;
				lowestRating = Math.round(lowestRating);
				lowestRating /= 100000;
			}
			
			if (next.getTotalChanges() > highestFileNumber){
				highestFileNumber = next.getTotalChanges();
			}
			if (next.getTotalChanges() < lowestFileNumber){
				lowestFileNumber = next.getTotalChanges();
			}
			
		}
		then = next.getDate();
		ratingAverage = (ratingAverage/revisionTotal)*100000;
		ratingAverage = Math.round(ratingAverage);
		ratingAverage /= 100000;
		nFilesAverage = nFilesAverage/revisionTotal;
	}
	
	public void statsOut() {
		analyze();
		
		System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");
		
		System.out.println("Total Number of Relevant Revisions: " + revisionTotal + "\n");
		
		System.out.println("Period of Revision History: ("+then+") to ("+now+") \n");
		
		System.out.println("Average Rating: "+ ratingAverage);
		System.out.println("\t Lowest Rating: " + lowestRating);
		System.out.println("\t Highest Rating: " + highestRating + "\n");
		
		System.out.println("Average Number of Changed files: "+ nFilesAverage);
		System.out.println("\t Lowest Rating: " + lowestFileNumber);
		System.out.println("\t Highest Rating: " + highestFileNumber + "\n");
		
		System.out.println("|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| \n");

	}
}
