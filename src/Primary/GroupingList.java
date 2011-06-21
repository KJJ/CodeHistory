package Primary;

import java.util.Iterator;
import java.util.LinkedList;

public class GroupingList extends CounterList<String>{
	
	//holds the revisions for each group
	private LinkedList<String[]> groupRevisions;
	
	//the list of GroupingNode's that hold the groups found in the RevisionNode list as it is processed
	/**
	 * Constructor, initializes the GroupingNode list
	 */
	public GroupingList () {
		super(false);
		groupRevisions = new LinkedList<String[]>();
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
			System.out.print("\t The group [" + next.whatsTheItem() + "] occurs " + next.howManyAppearences() + " time(s). \n");
			System.out.print("\t\t Revisions: "); 
			for (String[] info: groupRevisions) {
				if (next.whatsTheItem().equals(info[0])) {
					System.out.print(info[1]);
					break;
				}
			}
			System.out.println("\n");
		}
	}
	
	/**
	 * takes an object and what revision it is from and places it into its proper place.
	 * @param obj the new group object
	 * @param revision the revision tied with that object
	 */
	public void newInput(String obj, String revision) {
		super.newInput(obj); //use the inherited insertion
		int i; //loop counter
		String[] next = {obj, revision};
		if (groupRevisions.size() != 0) {
			for (i = 0; i < groupRevisions.size(); i++){
				String[] current = groupRevisions.get(i);
				if (current[0].equals(obj)){
					current[1] += ", " + revision;
					groupRevisions.remove(i);
					groupRevisions.add(i, current);
					break;
				}
			}
			
			if (groupRevisions.size() != 0 && i == groupRevisions.size()) {
				groupRevisions.addLast(next);
			}
			
		}
		else {
			groupRevisions.addFirst(next);
		}
	}

}
