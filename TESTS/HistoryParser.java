import java.io.*;
import java.util.*;

//Source of IO reading code: stackoverflow.com

public class HistoryParser {
	
	//bundle gets the root name of our repository address frim the config file
	private ResourceBundle bundle = ResourceBundle.getBundle("config");
	
	// args holds the string array of passed parameters from the command line
	private String[] args;
	
	/**
	 * Constructor: allows for access in the main code
	 * @param arg file names to be looked for in the code
	 */
	public HistoryParser(String[] arg) {
		args = arg;
	}
	
	/**
	 * collects data on the specified files in relation to revision numbers, date of revision and the number of files changed in a relevant revision
	 * @param exec Process executed, in this case a log command for a subversion repository
	 * @return An array of String type linked lists containing in this order; revision numbers, revision date, revision changes. 
	 * all lists are synchronized such that index i in all three lists pertains to a single revision.
	 * @throws IOException due to the input from running the process exec
	 */
	public LinkedList<String>[] collectTargetData(Process exec) throws IOException{
		BufferedReader  stdInput=  new  BufferedReader(new
	              InputStreamReader(exec.getInputStream()));
		
		@SuppressWarnings("unchecked")
		//holds the data on the targeted file log
		LinkedList<String>[] data = new LinkedList[3];
		//holds the current line of input in String form
		String s;
		//holds the split string
		String[] ss;
		//revision list
		LinkedList<String> rev = new LinkedList<String>();
		//list of revision dates
		LinkedList<String> date = new LinkedList<String>();
		//number of changed files list
		LinkedList<String> nChanged = new LinkedList<String>();
		//counter for how many files are changed
		int count = 0;
		
		// while there is input to process, execute this loop
		while  ((s=  stdInput.readLine())  !=  null)  {
			
			if (s.startsWith("r")) {  //a line starting with a lower case r implies that we are at a new revision
				if (count != 0) {  // check to see whether or not this is the first iteration
					nChanged.addLast(Integer.toString(count));  //add the previous revision's file count to the chnaged list
					count = 0;  //reset the counter
				}
				ss = s.split(" "); //split the string along white spaces
				rev.addLast(ss[0].substring(1)); //gets the revision number at the very beginning of s, removing the r to just get the number
				date.addLast(ss[4]+" "+ss[5]);  // gets both the date and time of the revision
			}
			/* 
			 * other lines fall into two categories, those pertaining to file history and those that are
			 * minor bits of information. Currently the only ones observed here are the files that are
			 * modified, added, replaced, or deleted 
			 */
			else if (s.startsWith("   M") || s.startsWith("   A") || s.startsWith("   D") || s.startsWith("   R")){
				count++; //increase the counter for files changed in a certain revision
			}
		}
		
		nChanged.addLast(Integer.toString(count));
				
		data[0] = rev; //store the revision list
		data[1] = date; //store the date list
		data[2] = nChanged; //store the number of files changed list
		return data; //return the array as output
	}
	
	/**
	 * takes the information found in collectTargetData and calculates the relevance rating and stores the data in a 
	 * RevisionNode object which is placed in a RevisionNode LinkedList
	 * @param RevisionList list of revision numbers in String form
	 * @param DateList list of Strings specifying the respective revision's date
	 * @param fileList String list of the number of files changed
	 * @return the RevisionNode list holding all of the gathered/processed log data
	 */
	public LinkedList<RevisionNode> getHistoricalRelevancy(LinkedList<LinkedList<String>> RevisionList, LinkedList<LinkedList<String>> DateList, LinkedList<LinkedList<String>> fileList){
		int i, j; //loop counters
		LinkedList<RevisionNode> results = new LinkedList<RevisionNode>(); //holds the results of processing the data
		LinkedList<String> revision = new LinkedList<String>(); //stores the revisions encountered in order to check whether or not another file shares that revision
		RevisionNode node; //node to hold the current revision's data before it is placed on the list
		String currentRev; //the current revision
		String elementToCompare; // used later to compare revisions to find linked files
		
		/*
		 * since revisionList is the same size as the other lists, this makes it feasible to use its size as the iteration
		 * manager. The Lists are lists of lists so each one is iterated through separately to fully collect all of the
		 * data.
		 */
		for (i = 0; i < RevisionList.size(); i++) {
			LinkedList<String> target = RevisionList.get(i);  //the list of revisions collected earlier
			Iterator<String> targetIterator = target.iterator(); //iterator for the list of revisions		
			Iterator<String> dateIterator = DateList.get(i).iterator(); //iterator for the list of dates	
			Iterator<String> changeIterator = fileList.get(i).iterator(); //iterator for the list of files	
			while (targetIterator.hasNext()) { //uses the equality of list lengths to avoid bounds exceptions
				currentRev = targetIterator.next(); //gets the next revision number to process
				
				//instantiates the new node with the revision, date, total changed files and the number of changed queried files 
				node = new RevisionNode(dateIterator.next(), currentRev, Integer.parseInt(changeIterator.next()), args.length); 
				node.newRelevantFile(args[i]); //add the current file to the revision list (NOTE: this makes use of how the file data is collected)
				if (!revision.contains(currentRev)) { //if the revision has not been previously encountered
					revision.addLast(currentRev); //add it to the revision tracking list
					
					/*
					 * This loop jumps through all of the lists in RevisionList to look if any other file was affected 
					 * by the revision in question.
					 */
					for (j = 0; j<RevisionList.size(); j++) { 
						if (j == i) { //check to see if the list is the one currently being processed in the outer loop
							continue; //skip the current list to avoid counting that file twice
						}
						Iterator<String> comparing = RevisionList.get(j).iterator(); //prepare an iterator to go through the list
						while (comparing.hasNext()) { //spans the entire iteration					 
							elementToCompare = comparing.next();  //get the next revision number in the list
							if (currentRev.equals(elementToCompare)) { //indicates another file was changed in this revision
								node.newRelevantFile(args[j]); //add the file name to the node's list (again, the code uses the order of arguments)
								break; //end the loop since a revision only appears at most once per list
							}
						}
					}
					sortedInsert(results, node); //places the node in the list in such a way that the list is ordered by revision number (descending)
				}
			}
		}
		return results; //return the linked list of RevisionNode's
	}
	
	/**
	 * sortedInsert takes a RevisionNode and places it into the chosen RevisionNode linked list in such a way that
	 * the descending order of the revision numbers is preserved
	 * @param list the list that the new RevisionNode will be inserted into
	 * @param node the RevisionNode to be inserted
	 */
	public void sortedInsert(LinkedList<RevisionNode> list, RevisionNode node){
		
		RevisionNodeComparator RNC = new RevisionNodeComparator(); //instantiate a new RevisionNode comparator
		int i; //loop counter
		if (list.peek() == null){ //checks if the list is empty
			list.addFirst(node); //if the list is empty then simply place the node at the head
		}
		else { //otherwise the node must be compared to the node currently present in the list
			i = 0; //set i to zero to get the full range of the list
			while (i < list.size()){ //iterates through the whole list until the correct location is found
				
				/*
				 * since compare returns 1 if the first argument is greater then the second, 
				 * then it implies that node's revision is to small to be placed in front of this currently present node
				 */
				if (RNC.compare(list.get(i), node) > 0) { 
					i++; //if this is the case, then prepare to check the next node in the list
				}
				else {
					break; //if the nodes revision is greater, then it should be placed at index i, so the loop ends early
				}
			}
			list.add(i, node); //place the node in it proper place
			//if the loop fully completes then it implies that node's revision number is the smallest and so it is placed at the end
		}
		
	}

	/**
	 * printHistoryInformation both processes the log information of a Subversion repository through the methods above
	 * and then prints out the names of the queried files along with all of the information stored in the RevisionNode
	 * linked list in a table-style format
	 * @throws IOException if the input from the command line processes throw an exception due to some error
	 */
	public void printHistoryInformation() throws IOException{
		
		int i;//loop counter
		LinkedList<LinkedList<String>> dataRepoRevision = new LinkedList<LinkedList<String>>(); //list that holds the revision number lists
		LinkedList<LinkedList<String>> dataRepoDate = new LinkedList<LinkedList<String>>(); //list that holds the date of revision lists
		LinkedList<LinkedList<String>> dataRepoNumberOfFiles = new LinkedList<LinkedList<String>>(); //list that holds list of the number of files changed in a revision

		System.out.println("Queried Files:"); //indicates the next lines show what was entered on the command line
		String p = bundle.getString("repo"); //uses the config.properties file to get the path to the svn working copy being used
		
		for (i = 0; i < args.length; i++){  //loops for every specified file
			System.out.println("\n"+args[i]); //prints the files name and path from the start of the working copy
			if (!args[i].startsWith("/")){ //all command line arguments must start with a / so it is checked if tht is the case
				args[i] = "/"+args[i]; //if not then the / is added to the argument at runtime
			}
			String n = p+args[i]; //get the path to the file in question
			Process exec = Runtime.getRuntime().exec("svn log -v "+n+" -q"); //uses the svn's log command to get the history of the queried file
			LinkedList<String>[] info = collectTargetData(exec); //using the process from the previous line we parse the svn log for the current queried file
			dataRepoRevision.addLast(info[0]); //the revision list is added to the main list for later processing
			dataRepoDate.addLast(info[1]); //the date list is added to the main list for later processing
			dataRepoNumberOfFiles.addLast(info[2]); //the amount of files changed list is added to the main list for later processing
		}
		int j; //loop counter
		
		System.out.print("\n");
		for (j = 0; j < 40; j++) { //create a line break to separate the query print out from the data table
			System.out.print("=========="); //indicates the end of the list of queried files
		}
		System.out.print("\n\n"); //provide spacing between output

		System.out.println("commit \t date \t\t\t relevants \t      changed \t rating \t\t rating comment \t\t\t\t actual relevant files");
		for (j = 0; j < 40; j++) { //used to separate the rows of data and improve appearance and ease of use
			System.out.print("----------"); //the lines used to separate the information rows
		}

		
		LinkedList<RevisionNode> history = getHistoricalRelevancy(dataRepoRevision, dataRepoDate, dataRepoNumberOfFiles); //fully process the collected data from each file's log
		System.out.println(); //further increase spacing between line break and table
		for (i = 0; i < history.size(); i++){ //iterates through the entire RevisionNode list to print out its collected data
			RevisionNode current = history.get(i); //takes the next node to be printed
			System.out.println(current.toString()); //prints the String representation of all the nodes data
			for (j = 0; j < 40; j++) { //used to separate the rows of data and improve appearance and ease of use
				System.out.print("----------"); //the lines used to separate the information rows
			}
			System.out.print("\n"); //newline to skip down to the next row's position
		}
	}

}
