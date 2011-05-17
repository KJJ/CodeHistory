import java.util.Iterator;
import java.util.LinkedList;


public class GroupingList {
	
	private LinkedList<GroupingNode> theList;

	public GroupingList () {
		theList = new LinkedList<GroupingNode>();
	}
	
	public void newInput(String group) {
		Iterator<GroupingNode> lIterator = theList.iterator();
		int i = 0;
		GroupingNode node;
		
		if (theList.size() == 0) {
			node = new GroupingNode(group);
			theList.addLast(node);
		}
		
		else {
			while (lIterator.hasNext()) {
				GroupingNode next = lIterator.next();
				if (next.isEqual(group)) {
					break;
				}
				else if (!lIterator.hasNext()) {
					i = -2;
				}
			
				i++;
			}
			if (i < 0) {
				node = new GroupingNode(group);
				theList.addLast(node);
			}
			else {
				node  = theList.remove(i);
				node.anotherOccurence();
				theList.add(i, node);
			}
		}
	}
	
	public void currentOutput() {
		Iterator<GroupingNode> lIterator = theList.iterator();
		System.out.println("File Change Groupings: \n");
		
		while (lIterator.hasNext()){
			GroupingNode next = lIterator.next();
			System.out.println("The group "+next.getGroup()+" Occurs "+next.getOccurences()+" time(s). ");
		}
		System.out.println();
	}

}
