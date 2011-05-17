
public class GroupingNode {
	
	//String representation of the file group
	private String group;
	//how many times this group occurred in the log history
	private int occurrences;
	
	/**
	 * Constructor. sets occurrences to 1 since if a node is made, at least one of this group was found
	 * @param theNewGroup is the String representation of this particular group of files
	 */
	public GroupingNode(String theNewGroup){
		group = theNewGroup;
		occurrences = 1;
	}
	
	/**
	 * gets the group of files String representation
	 * @return the group of files that this node represents
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * gets how many times this grouping shows up in the relevant logs
	 * @return the number of this groups occurrences
	 */
	public int getOccurences() {
		return occurrences;
	}

	/**
	 * checks to see whether or not this node's group is equivalent to another group
	 * @param anotherGroup the String representation of the group in question
	 * @return true if they are the same group, false otherwise
	 */
	public boolean isEqual(String anotherGroup) {
		if (group.equals(anotherGroup)){
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * increases the occurrence counter for this particular group of files
	 */
	public void anotherOccurrence() {
		occurrences++;
	}
}
