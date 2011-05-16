import java.util.Iterator;
import java.util.LinkedList;


public class NodeStatistics {
	
	private LinkedList<RevisionNode> toAnalyze;
	private int revisionTotal;
	private double ratingAverage, highestRating, lowestRating;
	private int nFilesAverage, highestFileNumber, lowestFileNumber;
	
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
		while (runThrough.hasNext()){
			
			RevisionNode next = runThrough.next();
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
		ratingAverage = (ratingAverage/revisionTotal)*100000;
		ratingAverage = Math.round(ratingAverage);
		ratingAverage /= 100000;
		nFilesAverage = nFilesAverage/revisionTotal;
	}
	
	public void statsOut() {
		analyze();
		
		System.out.println("Total number of relevant Revisions: " + revisionTotal + "\n");
		
		System.out.println("Average Rating: "+ ratingAverage);
		System.out.println("\t Lowest Rating: " + lowestRating);
		System.out.println("\t Highest Rating: " + highestRating + "\n");
		
		System.out.println("Average Number of Changed files: "+ nFilesAverage);
		System.out.println("\t Lowest Rating: " + lowestFileNumber);
		System.out.println("\t Highest Rating: " + highestFileNumber + "\n");

	}
}
