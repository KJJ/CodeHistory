import java.io.*;
import java.util.*;


//Source of IO reading code: stackoverflow.com

public class HistoryParserAdjust {
	
	//bundle gets the root name of our repository address from the config.properties file
	private ResourceBundle bundle = ResourceBundle.getBundle("config");
	
	// args holds the string array of passed parameters from the command line
	private String[] args;
	private String[] sArgs;
	
	/**
	 * Constructor: allows for access in the main code
	 * @param arg file names to be looked for in the code
	 */
	public HistoryParserAdjust(String[] arg) {
		args = arg;
		sArgs = new String[arg.length];
		int i;
		for(i=0; i<arg.length; i++){
			sArgs[i] = arg[i];
		}
	}
	
	/**
	 * collects data on the specified files in relation to revision numbers, date of revision and the number of files changed in a relevant revision
	 * @param exec Process executed, in this case a log command for a subversion repository
	 * @return An array of String type linked lists containing in this order; revision numbers, revision date, revision changes. 
	 * all lists are synchronized such that index i in all three lists pertains to a single revision.
	 * @throws IOException due to the input from running the process exec
	 */
	public LinkedList<RevisionNode> collectTargetData(Process exec) throws IOException{
		BufferedReader  stdInput=  new  BufferedReader(new
	              InputStreamReader(exec.getInputStream()));
		
		//holds the data on the targeted file log
		LinkedList<RevisionNode> data = new LinkedList<RevisionNode>();
		//holds the current line of input in String form
		String s;
		//holds the split string
		String[] ss;
		int count = 0;
		RevisionNode node = null;
		
		// while there is input to process, execute this loop
		while  ((s=  stdInput.readLine())  !=  null)  {
			
			if (s.startsWith("r")) {  //a line starting with a lower case r implies that we are at a new revision
				if (count != 0) {  // check to see whether or not this is the first iteration
					node.setTotalChanges(count);
					sortedInsert(data, node);
					count = 0;  //reset the counter
				}
				ss = s.split(" ");
				node = new RevisionNode((ss[4]+" "+ss[5]), (ss[0].substring(1)), args.length, (ss[2]));
			}

			else if (s.startsWith("   M ") || s.startsWith("   A ") || s.startsWith("   D ") || s.startsWith("   R ")){
				ss = s.split(" ");
				String sss = ss[3];
				int i;
				String path = ss[4];
				for(i = 0; i < args.length; i++) {
					if (s.contains("(from /") && !ss[4].contains(".")) {
						sArgs[i] = ss[6].split(":")[0] + args[i].split(ss[4])[0];
					}
					if (path.endsWith(args[i]) || path.endsWith(sArgs[i])){
						node.newRelevantFile(args[i] + " ("+sss+") ");
						if (s.contains("(from /")){
							String[] ssssss = ss[6].split(":");
							sss = ssssss[0];
							String[] ssss = sss.split("/");
							int k;
							String sssss = "";
							for (k = 3; k < ssss.length; k++){
								sssss += "/"+ssss[k];
							}
							sArgs[i] = sssss;
						}
					}
				}
				count++; //increase the counter for files changed in a certain revision
			}
		}
		node.setTotalChanges(count);
		sortedInsert(data, node);
		return data; //return the array as output
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
		
		System.out.println("Queried Files:"); //indicates the next lines show what was entered on the command line
		String p = bundle.getString(bundle.getString("repo")); //uses the config.properties file to get the path to the svn working copy being used
		String n = p;
		
		for (i = 0; i < args.length; i++){  //loops for every specified file
			System.out.println("\n"+args[i]); //prints the files name and path from the start of the working copy
			if (args[i].startsWith("/")){ //all command line arguments must start with a / so it is checked if that is the case
				args[i] = args[i].substring(1); //if not then the / is added to the argument at runtime
			}
			n += " "+args[i]; //get the path to the file in question
		}
		
			Process exec = Runtime.getRuntime().exec("svn log -v "+n+" -q"); //uses the svn's log command to get the history of the queried file
			LinkedList<RevisionNode> history = collectTargetData(exec);
		int j; //loop counter
		
		System.out.print("\n");
		for (j = 0; j < 5; j++) { //create a line break to separate the query print out from the data table
			System.out.print("=========="); //indicates the end of the list of queried files
		}
		System.out.print("\n\n"); //provide spacing between output

		System.out.println("commit \t user \t\t date \t\t\t relevants \t      changed \t rating \t\t rating comment \t\t\t\t actual relevant files");
		for (j = 0; j < 5; j++) { //used to separate the rows of data and improve appearance and ease of use
			System.out.print("----------"); //the lines used to separate the information rows
		}

		int[] statArray = new int[10];
		System.out.println(); //further increase spacing between line break and table
		for (i = 0; i < history.size(); i++){ //iterates through the entire RevisionNode list to print out its collected data
			RevisionNode current = history.get(i); //takes the next node to be printed
			fillArray(statArray, current.getRating());
			System.out.println(current.toString()); //prints the String representation of all the nodes data
			for (j = 0; j < 5; j++) { //used to separate the rows of data and improve appearance and ease of use
				System.out.print("----------"); //the lines used to separate the information rows
			}
			System.out.print("\n"); //newline to skip down to the next row's position
		}
		System.out.println("\n");
		System.out.println("Rating Graph: looking for grouping\n");
		for (i = 1; i <= statArray.length; i++){
			System.out.print((double)(i-1)/10+"-"+(double)i/10+"  ");
			for (j = 0; j < statArray[i-1]; j++){
				System.out.print("|");
			}
			System.out.print("  ("+statArray[i-1]+")");
			System.out.println();
			for (j = 0; j < 10; j++) { //create a line break to separate the query print out from the data table
				System.out.print("=========="); //indicates the end of the list of queried files
			}
			System.out.println();
		}
	}

	public void fillArray(int[] array, double rating){
		int i = 1;
		while (i <= array.length){
			if (rating <= (double)i/10){
				array[i-1] = array[i-1]+1;
				break;
			}
			i++;
		}
	}
}
