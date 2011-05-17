import java.util.Iterator;
import java.util.LinkedList;


public class GroupingList {
	
	//the list of GroupingNode's that hold the groups found in the RevisionNode list as it is processed
	private LinkedList<GroupingNode> theList;

	/**
	 * Constructor, initializes the GroupingNode list
	 */
	public GroupingList () {
		theList = new LinkedList<GroupingNode>(); //list initialized
	}
	
	/**
	 * newInput takes a group of the queried files from a RevisionNode and checks to see if it is 
	 * already present or brand new
	 * @param group String representation of the file group
	 */
	public void newInput(String group) {
		Iterator<GroupingNode> lIterator = theList.iterator(); //prepares to iterate through the entire GroupingNode list
		int i = 0; //loop counter
		GroupingNode node; //node to be added to theList
		
		if (theList.size() == 0) { //empty list implies a need to instantly add the new group
			node = new GroupingNode(group); //Completely new node created
			theList.addLast(node); //added to the end of theList
		}
		
		else { //if there are other nodes present
			while (lIterator.hasNext()) { //checks all elements
				GroupingNode next = lIterator.next(); //next node to check
				if (next.isEqual(group)) { //if the group is already found in theList
					break; //end the loop early to maintain list position
				}
				else if (!lIterator.hasNext()) { //if at the end of theList
					i = -2; //index will never be -2 so this is a flag value
				}
			
				i++; //increment to represent the next index position
			}
			if (i < 0) { //if i is at our flag value
				node = new GroupingNode(group); //create the new node
				theList.addLast(node); //and add it to the end
			}
			else { //if we found a matching node at index i
				node  = theList.remove(i); //take the node out for adjustment
				node.anotherOccurrence(); //marks that another occurrence of this grouping occurred
				theList.add(i, node); //return the adjusted node back to its initial position
			}
		}
	}
	
	/**
	 * prints out the grouping statistics of the relevant parts of the revision log
	 */
	public void currentOutput() {
		Iterator<GroupingNode> lIterator = theList.iterator(); //for running through the entire list
		System.out.println("File Change Groupings: \n"); //header
		
		while (lIterator.hasNext()){ //for the duration of the list
			GroupingNode next = lIterator.next(); //take each element out
			//and print out its String representation and number of occurrences
			System.out.println("\t The group ["+next.getGroup()+"] occurs "+next.getOccurences()+" time(s). \n");
		}
	}

}
