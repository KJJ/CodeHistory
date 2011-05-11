import java.util.Comparator;


public class RevisionNodeComparator implements Comparator<RevisionNode>{
	
	/**
	 * constructor, allows use of comparator
	 */
	public RevisionNodeComparator() {
		
	}

	/**
	 * compare takes two ListNode objects and compares them based on the integer representation of their revision 
	 * numbers. If o1 is a later revision than o2, then compare returns 1 and returns -1 if it is an earlier revision.
	 * 0 is returned if o1 and o2 are the same revision.
	 */
	public int compare(RevisionNode o1, RevisionNode o2) {
		if (Integer.parseInt(o1.getRevision()) == Integer.parseInt(o2.getRevision())) {
			return 0; //it is the same revision
		}
		else if (Integer.parseInt(o1.getRevision()) > Integer.parseInt(o2.getRevision()) || o2 == null) {
			return 1; //o1 is a later revision revision than o2
		}
		else {
			return -1; //o1 is an earlier revision than o2
		}
	}

}
