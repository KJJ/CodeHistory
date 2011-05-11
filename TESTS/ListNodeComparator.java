import java.util.Comparator;


public class ListNodeComparator implements Comparator<ListNode>{
	
	public ListNodeComparator() {
		
	}

	public int compare(ListNode o1, ListNode o2) {
		if (Integer.parseInt(o1.getRevision()) == Integer.parseInt(o2.getRevision())) {
			return 0;
		}
		else if (Integer.parseInt(o1.getRevision()) > Integer.parseInt(o2.getRevision()) || o2 == null) {
			return 1;
		}
		else {
			return -1;
		}
	}

}
