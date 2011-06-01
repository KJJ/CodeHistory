import java.util.Iterator;

public class GroupingList extends CounterList<String>{
	
	//the list of GroupingNode's that hold the groups found in the RevisionNode list as it is processed
	/**
	 * Constructor, initializes the GroupingNode list
	 */
	public GroupingList () {
		super();
	}
	
	/**
	 * prints out the grouping statistics of the relevant parts of the revision log
	 */
	public void currentOutput() {
		Iterator<CounterNode<String>> lIterator = getList().iterator(); //for running through the entire list
		System.out.println("File Change Groupings: \n"); //header
		
		while (lIterator.hasNext()){ //for the duration of the list
			CounterNode<String> next = lIterator.next(); //take each element out
			//and print out its String representation and number of occurrences
			System.out.println("\t The group ["+next.whatsTheItem()+"] occurs "+next.howManyAppearences()+" time(s). \n");
		}
	}

}
