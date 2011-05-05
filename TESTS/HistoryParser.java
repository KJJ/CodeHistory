import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ResourceBundle;

//Source of IO reading code: stackOverflow.com

public class HistoryParser {
	
	private ResourceBundle bundle = ResourceBundle.getBundle("config");

	
	private String[] args;
	LinkedList<Double> ratingList = new LinkedList<Double>();
	LinkedList<String> revisionList = new LinkedList<String>();
	LinkedList<String> caseList = new LinkedList<String>();
	LinkedList<String> dateList = new LinkedList<String>();
	
	public HistoryParser(String[] arg) {
		args = arg;
	}

	/*
	 * prints out the revisions that have some remote relevance to the queried files
	 * - displays: revision, rating (based on algorithm #1) and the case it is under
	 */
	public void printRelevancy(Process exec, String[] arg) throws IOException {
		BufferedReader  stdInput=  new  BufferedReader(new
	              InputStreamReader(exec.getInputStream()));

		String s;
	    char c = ' ';
	    String ss = "";
		String date = "Never";
		int totalChanged = 0;
		int relevant = 0;
		int irrelevant = 0;
		Double rating = 0.0;
		String theCase = "";
			
		while  ((s=  stdInput.readLine())  !=  null)  {
			if (s.startsWith("r")) { //indicates a new revision
				if (ss != "") { //will only be this if it is the very first iteration
					theCase = caseDetermine(relevant, irrelevant, totalChanged, arg.length); //analyze the current case
					rating = calculateRating(relevant, irrelevant, totalChanged, arg.length); //calculate the current rating
					if (rating > 0) { // if relevant, store it
						ratingList.addLast(rating);
						revisionList.addLast(ss);
						caseList.addLast(theCase);
						dateList.addLast(date);	
					}
					//reset fields
					ss = "";
					relevant = 0;
					irrelevant = 0;
					totalChanged = 0;
				}
				// get the next revision number
				int i = 1;
				while (s.charAt(i) != c) { 
					ss += s.charAt(i);
					i++;
				}
				date = s.substring(s.lastIndexOf('|') +2, s.lastIndexOf('|')+21);
			}
			// if it is not a revision, it is either a file or junk, discard junk and process the file names
			else if (s.startsWith("   M") || s.startsWith("   D") || s.startsWith("   A")){
				totalChanged++; // how many files were changed in the revision 
				int i = 0;
				String[] splitting = s.split(" "); //separate to get the file name
				while (i < arg.length) {
					if (!arg[i].startsWith("/")) {
						arg[i] = "/" + arg[i];
					}
					if (splitting[4].contains(bundle.getString("mainBranch") + arg[i]) && splitting[4].endsWith(arg[i])) { // compare to all queried files
						i = arg.length+77;							
					}
					else {
						i++;
					}
				}
				if (i == arg.length+77){ //indicates presence of relevant file
					relevant++;
				}
				else {
					irrelevant++;
				}
			}
         }
		theCase = caseDetermine(relevant, irrelevant, totalChanged, arg.length);
		rating = calculateRating(relevant, irrelevant, totalChanged, arg.length);
		if (rating > 0) {
			ratingList.addLast(rating);
			revisionList.addLast(ss);
			caseList.addLast(theCase);
			dateList.addLast(date);
		}
}
	
	/*
	 * calculates the relevance rating for its case type based on algorithm #1
	 */
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

	/*
	 * Uses the same process as calcRating to discern what case the revision is in
	 */
	private String caseDetermine(int relevant, int irrelevant, int totalChanged, int amountOfRelevantFiles) {
		if (relevant == 0) {
			return "Irrelevant";
		}
		else if (relevant == totalChanged && relevant == amountOfRelevantFiles){
			return "Very Relevant, contains only queried files";
		}
		else if (relevant == totalChanged && relevant < amountOfRelevantFiles) {
			return "A Pure Subset of some of the queried files";
		}
		else if (relevant == amountOfRelevantFiles && relevant != totalChanged){
			return "Impure Superset of the queried files found";
		}
		
		else return "Subset of Relevants mixed with Irrelevants";
	}
	
	@SuppressWarnings("rawtypes")
	public LinkedList<LinkedList> enableProcess(){
		try {
			Process exec;
			String p = bundle.getString("repo");
			exec = Runtime.getRuntime().exec("svn log -q -v "+p);
			printRelevancy(exec, args);

			LinkedList<LinkedList> listOfLists = new LinkedList<LinkedList>();
			listOfLists.add(revisionList);
			listOfLists.add(dateList);
			listOfLists.add(ratingList);
			listOfLists.add(caseList);
			
			return listOfLists;
		}
		
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void printHistoryInformation(){
		
		LinkedList<LinkedList> historicResults = enableProcess();
		ListIterator a = historicResults.get(0).listIterator();
		ListIterator b = historicResults.get(1).listIterator();
		ListIterator c = historicResults.get(2).listIterator();
		ListIterator d = historicResults.get(3).listIterator();

        System.out.println(" Revision \t\t Date \t\t\t Rating \t\t\t\t Case");
		System.out.println("|---------|-------------------------------|-------------------------------|--------------------------------------------------|");
		
		while(a.hasNext()){
			
			String rat = c.next().toString();
			String spaces = "\t  | \t";
			
			if (rat.length() <= 5){
				spaces = "\t\t" + spaces;
			}
			else if (rat.length() <= 8){
				spaces = "\t" + spaces;
			}
			
			System.out.println("| "+a.next()+ "  | \t" + b.next() + "\t  | \t" + rat + spaces + d.next()+"   |");
			System.out.println("|---------|-------------------------------|-------------------------------|--------------------------------------------------|");

		}
	}

}
