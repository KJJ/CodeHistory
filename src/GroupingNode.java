
public class GroupingNode {
	
	private String group;
	private int occurences;
	
	public GroupingNode(String theNewGroup){
		group = theNewGroup;
		occurences = 1;
	}
	
	public String getGroup() {
		return group;
	}

	public int getOccurences() {
		return occurences;
	}

	public boolean isEqual(String anotherGroup) {
		if (group.equals(anotherGroup)){
			return true;
		}
		else {
			return false;
		}
	}
	
	public void anotherOccurence() {
		occurences++;
	}
}
