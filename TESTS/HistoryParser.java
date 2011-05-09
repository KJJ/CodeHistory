import java.io.*;
import java.util.*;

//Source of IO reading code: stackoverflow.com

public class HistoryParser {
	
	private ResourceBundle bundle = ResourceBundle.getBundle("config");
	
	private String[] args;
	LinkedList<Double> ratingList = new LinkedList<Double>();
	LinkedList<String> revisionList = new LinkedList<String>();
	LinkedList<String> caseList = new LinkedList<String>();
	LinkedList<String> dateList = new LinkedList<String>();
	LinkedList<String> specialList = new LinkedList<String>();

	
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
		String change = "";
			
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
						specialList.addLast(change);
					}
					//reset fields
					ss = "";
					relevant = 0;
					irrelevant = 0;
					totalChanged = 0;
					change = "";
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
			else if (s.startsWith("   M") || s.startsWith("   D") || s.startsWith("   A") || s.startsWith("   R")){
				
				totalChanged++; // how many files were changed in the revision 
				int i = 0;
				String[] splitting = s.split(" "); //separate to get the file name
				while (i < arg.length) {
					if (!arg[i].startsWith("/")) {
						arg[i] = "/" + arg[i];
					}
					if (splitting[4].contains(bundle.getString("mainBranch") + arg[i]) && splitting[4].endsWith(arg[i])) { // compare to all queried files
						
						if (splitting[3].contains("A")){
							change += " "+arg[i].substring(arg[i].lastIndexOf('/')+1)+": Added  |";
						}
						else if (splitting[3].contains("D")){
							change += " "+arg[i].substring(arg[i].lastIndexOf('/')+1)+": Deleted  |";
						}
						else if (splitting[3].contains("M")){
							change += " "+arg[i].substring(arg[i].lastIndexOf('/')+1)+": Modified  |";
						}
						else if (splitting[3].contains("R")){
							change += " "+arg[i].substring(arg[i].lastIndexOf('/')+1)+": Replaced  |";
						}
						
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
			listOfLists.add(specialList);
			
			return listOfLists;
		}
		
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public LinkedList<String>[] collectTargetData(Process exec) throws IOException{
		BufferedReader  stdInput=  new  BufferedReader(new
	              InputStreamReader(exec.getInputStream()));
		
		BufferedReader  stdError=  new  BufferedReader(new
	              InputStreamReader(exec.getErrorStream()));
		
		@SuppressWarnings("unchecked")
		LinkedList<String>[] data = new LinkedList[2];
		String s;
		String[] ss;
		LinkedList<String> rev = new LinkedList<String>();
		LinkedList<String> date = new LinkedList<String>();
		
		while  ((s=  stdError.readLine())  !=  null)  {
            System.out.println(s);
        }
		
		while  ((s=  stdInput.readLine())  !=  null)  {
			ss = s.split(" ");
			rev.addLast(ss[0]);
			date.addLast(ss[4]+" "+ss[5]);
		}
		
		data[0] = rev;
		data[1] = date;
		return data;
	}
	
	@SuppressWarnings("rawtypes")
	public LinkedList<LinkedList> getHistoricalRelevancy(LinkedList<LinkedList<String>> RevisionList){
		int i, j;
		int presentAtRevision;
		LinkedList<String> revision = new LinkedList<String>();
		LinkedList<Integer> relevantN = new LinkedList<Integer>();
		String tar;
		String com;
		for (i = 0; i < RevisionList.size(); i++) {
			presentAtRevision = 1;
			LinkedList<String> target = RevisionList.get(i);
			Iterator<String> targetIterator = target.iterator();
			while (targetIterator.hasNext()) {
				tar = targetIterator.next();
				if (!revision.contains(tar)) {
					revision.addLast(tar);
					for (j = 0; j<RevisionList.size(); j++) {
						if (j == i) {
							continue;
						}
						LinkedList<String> toCompare = RevisionList.get(j);
						Iterator<String> comparing = toCompare.iterator();
						while (comparing.hasNext()) {
							com = comparing.next();
							if (tar.equals(com)) {
								presentAtRevision++;
								break;
							}
						}
					}
					relevantN.addLast(presentAtRevision);
					presentAtRevision = 1;
				}
			}
		}
		LinkedList<LinkedList> resultList = new LinkedList<LinkedList>();
		resultList.addLast(revision);
		resultList.addLast(relevantN);
		return resultList;
	}
	
	@SuppressWarnings("rawtypes")
	public void printHistoryInformation() throws IOException{
		
		/*LinkedList<LinkedList> historicResults = enableProcess();
		ListIterator a = historicResults.get(0).listIterator();
		ListIterator b = historicResults.get(1).listIterator();
		ListIterator c = historicResults.get(2).listIterator();
		ListIterator d = historicResults.get(3).listIterator();
		ListIterator e = historicResults.get(4).listIterator();

        System.out.println(" Revision \t\t Date \t\t\t Rating \t\t\t\t Case");
		System.out.println("|---------|-------------------------------|-------------------------------|--------------------------------------------------|----------------------------------------------------------------------------------------------------------------|");
		
		while(a.hasNext()){
			
			String rat = c.next().toString();
			String spaces = "\t  | \t";
			
			if (rat.length() <= 8){
				spaces = "\t\t" + spaces;
			}
			else if (rat.length() <= 8){
				spaces = "\t" + spaces;
			}
			String revision = (String)a.next();
			if (revision.length() <=5){
				revision+="\t";
			}
			System.out.println("| "+revision+ "  | \t" + b.next() + "\t  | \t" + rat + spaces + d.next()+"   |"+ e.next());
			System.out.println("|---------|-------------------------------|-------------------------------|--------------------------------------------------|----------------------------------------------------------------------------------------------------------------|");
		}*/
		
		int i;
		LinkedList<LinkedList<String>> dataRepoRevision = new LinkedList<LinkedList<String>>();
		LinkedList<LinkedList<String>> dataRepoDate = new LinkedList<LinkedList<String>>();

		System.out.println("\nQueried Files:");
		String p = bundle.getString("repo");
		
		for (i = 0; i < args.length; i++){
			System.out.println("\n"+args[i]);
			String n = p+args[i];
			//shell wrapping technique found at http://stackoverflow.com/questions/3776195/problem-using-java-processbuilder-to-execute-a-piped-command
			ProcessBuilder ex =  new ProcessBuilder("/bin/sh", "-c", "svn log -v "+n+" -q | grep ,");
			Process exec = ex.start();
			LinkedList<String>[] info = collectTargetData(exec);
			dataRepoRevision.addLast(info[0]);
			dataRepoDate.addLast(info[1]);
			Iterator rev = info[0].iterator();
			Iterator date = info[1].iterator();
			while(rev.hasNext()){
				System.out.println(rev.next() + "\t" + date.next());
			}
		}
		System.out.println();
		LinkedList<LinkedList> ratings = getHistoricalRelevancy(dataRepoRevision);
		System.out.println(ratings.get(0).size());
		System.out.println(ratings.get(1).size());
		System.out.println();
		Iterator revision = ratings.get(0).iterator();
		Iterator numbers = ratings.get(1).iterator();
		while (numbers.hasNext()){
			System.out.println(revision.next() + "\t" + numbers.next() +"/"+args.length+" files found");
		}
	}

}
