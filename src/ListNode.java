import java.util.LinkedList;


public class ListNode {
	
	private String date;
	private String revision;
	private int numberOfRelevants;
	private double rating;
	private LinkedList<String> relevantFiles;
	private int totalQuery;
	private int totalChanges;
	private String ratingComment;

	public String getRatingComment() {
		return ratingComment;
	}

	private void setRatingComment(int relevant, int irrelevant, int totalChanged, int amountOfRelevantFiles) {
		if (relevant == 0) {
			ratingComment = "Irrelevant";
		}
		else if (relevant == totalChanged && relevant == amountOfRelevantFiles){
			ratingComment = "Very Relevant, contains only queried files";
		}
		else if (relevant == totalChanged && relevant < amountOfRelevantFiles) {
			ratingComment = "A Pure Subset of some of the queried files";
		}
		else if (relevant == amountOfRelevantFiles && relevant != totalChanged){
			ratingComment = "Impure Superset of the queried files found";
		}
		
		else ratingComment = "Subset of Relevants mixed with Irrelevants";
	}

	public ListNode(String rev, int query) {
		date = "default";
		revision = rev;
		numberOfRelevants = 0;
		rating = 0;
		relevantFiles = new LinkedList<String>();
		totalQuery = query;
	}
	
	public ListNode(String dat, String rev, int relevants, int query) {
		date = dat;
		revision = rev;
		numberOfRelevants = relevants;
		rating = 0;
		relevantFiles = new LinkedList<String>();
		totalQuery = query;
	}
	
	public int getTotalChanges() {
		return totalChanges;
	}

	public void setTotalChanges(int totalChanges) {
		this.totalChanges = totalChanges;
	}
	
	public void setDate(String Date) {
		date = Date;
	}
	
	public void newRelevantFile(String file){
		relevantFiles.addLast(file);
		numberOfRelevants++;
	}

	public String getDate() {
		return date;
	}

	public int getNumberOfRelevants() {
		return numberOfRelevants;
	}

	public void setRating() {
		int i = totalChanges-numberOfRelevants;
		if (i < 0){
			i = 0;
		}
		rating = calculateRating(numberOfRelevants,i,totalChanges,totalQuery);
		setRatingComment(numberOfRelevants,i,totalChanges,totalQuery);
	}
	
	public double getRating() {
		setRating();
		return rating;
	}

	public LinkedList<String> getRelevantFiles() {
		return relevantFiles;
	}

	public int getTotalQuery() {
		return totalQuery;
	}
	
	public String getRevision() {
		return revision;
	}

	public String dataOutput(){
		String out = "";
		out = getRevision()+"\t"+getDate()+"\t"+getNumberOfRelevants()+"/"+getTotalQuery();
		return out;
	}
	
	private double calculateRating(int relevant, int irrelevant, int totalChanged, int amountOfRelevantFiles) {
		if (relevant == 0) {
			return 0;
		}
		else if (relevant == totalChanged && relevant == amountOfRelevantFiles){
			return 1;
		}
		else if (relevant == totalChanged && relevant < amountOfRelevantFiles) {
			return ((double)totalChanged/amountOfRelevantFiles);
		}
		else if (relevant == amountOfRelevantFiles && relevant != totalChanged){
			return ((double)relevant/totalChanged);
		}
		
		else return (double)relevant/(amountOfRelevantFiles + (double)irrelevant/totalChanged);
	}

}
