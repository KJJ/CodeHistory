package primary;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;



public class CounterList<T> {
	
	//the list that holds the counter nodes
	private LinkedList<CounterNode<T>> list;
	// the config file bundle
	private ResourceBundle bundle = ResourceBundle.getBundle("config");
	private boolean limitation;

	/**
	 * Constructor, initializes the list
	 */
	public CounterList(boolean limit) {
		list = new LinkedList<CounterNode<T>>();
		limitation = limit;
	}
	
	/**
	 * Constructor, initializes the list and enters the first element to be tracked
	 */
	public CounterList(CounterNode<T> firstElement, boolean limit) {
		list = new LinkedList<CounterNode<T>>();
		list.addFirst(firstElement);
		limitation = limit;
	}
	
	/**
	 * inserts a new object into the list to be tracked.
	 * if it is a new object, then a new node is created.
	 * if it was already present, then that node increases its counter.
	 * nodes are sorted based on how many times their object appears
	 * @param obj the new input to be entered and logged
	 */
	public void newInput(T obj) {
		
		int i = 0; //loop counter
		CounterNode<T> next = null; // the next node to compare
		
		if (list.peek() == null){ //checks if the list is empty
			next = new CounterNode<T>(obj);
			list.addFirst(next); // avoids messy null exceptions
		}
		else {
			for (i = 0; i < list.size(); i++) { //prepares to go through the entire list to look for the same object already present
				next = list.get(i);
			
				if(next.compare(obj)){ //checks to see if the objects are the same
					next = list.remove(i); //if so remove the node...
					next.anotherOne(); //...and increment it
					break; //end the loop since the object can only occur once 
				}
				else {
					next = new CounterNode<T>(obj); //else, reset the node to one based on the entered object
				}
			}
			i = 0; //set i to zero to get the full range of the list
			while (i < list.size()){ //track through the entire list if needed
				if (list.get(i).compareOccurrence(next) > 0) { 
					i++; //if this is the case, then prepare to check the next node in the list
				}
				else {
					break; //if the nodes revision is greater or equal, then it should be placed at index i, so the loop ends early
				}
			}
			list.add(i, next); //place the node in it proper place	
		}
	}
	
	/**
	 * 
	 * @return the entire list of object counters
	 */
	public LinkedList<CounterNode<T>> getList() {
		return list;
	}
	
	/**
	 * print out the objects along with their occurrence number
	 * 
	 * overrides the inherited toString operation 
	 */
	public String toString() {
		int j = 0;
		int displaySize = 1029384756;
		if (limitation) {
			displaySize = Integer.parseInt(bundle.getString("occurrenceDisplayLimit"));
		}
		else {
			displaySize = Integer.MAX_VALUE;
		}
		Iterator<CounterNode<T>> i = list.iterator(); //since all nodes will be touched, but not changed
		String out = ""; //holds what will be returned
		while(i.hasNext()){
			CounterNode<T> next = i.next();
			if (next.howManyAppearences() > Integer.parseInt(bundle.getString("counterLowerLimit"))) { //keeps values too low to be considered important out
				out += "\n" + next.toString(); //concatenates the next node to the output
				if (j >= displaySize) {
					break;
				}
				j++;
			}
		}
		return out; //only returns the string, it does not print it out
	}
}
