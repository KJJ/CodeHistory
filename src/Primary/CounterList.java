package Primary;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;



public class CounterList<T> {
	
	private LinkedList<CounterNode<T>> list;
	private ResourceBundle bundle = ResourceBundle.getBundle("config");

	public CounterList() {
		list = new LinkedList<CounterNode<T>>();
	}
	
	public CounterList(CounterNode<T> firstElement) {
		list = new LinkedList<CounterNode<T>>();
		list.addFirst(firstElement);
	}
	
	public void newInput(T obj) {
		int i = 0;
		CounterNode<T> next = null;
		
		if (list.peek() == null){ //checks if the list is empty
			next = new CounterNode<T>(obj);
			list.addFirst(next);
		}
		else {
			for (i = 0; i < list.size(); i++) {
				next = list.get(i);
			
				if(next.compare(obj)){
					next = list.remove(i);
					next.anotherOne();
					break;
				}
				else {
					next = new CounterNode<T>(obj);
				}
			}
			i = 0; //set i to zero to get the full range of the list
			while (i < list.size()){ 
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
	
	public LinkedList<CounterNode<T>> getList() {
		return list;
	}
	
	public String toString() {
		Iterator<CounterNode<T>> i = list.iterator();
		String out = "";
		while(i.hasNext()){
			CounterNode<T> next = i.next();
			if (next.howManyAppearences() > Integer.parseInt(bundle.getString("filter"))) {
				out += "\n" + next.whatsTheItem() + "\t" + next.howManyAppearences();
			}
		}
		return out;
	}
}
