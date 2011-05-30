import java.util.Iterator;
import java.util.LinkedList;


public class CounterList<T> {
	
	private LinkedList<CounterNode<T>> list;

	public CounterList() {
		list = new LinkedList<CounterNode<T>>();
	}
	
	public CounterList(CounterNode<T> firstElement) {
		list = new LinkedList<CounterNode<T>>();
		list.addFirst(firstElement);
	}
	
	public boolean newInput(T obj) {
		int i = 0;
		
		for (i = 0; i < list.size(); i++) {
			CounterNode<T> next = list.get(i);
			
			if(next.compare(obj)){
				next  = list.remove(i);
				next.anotherOne();
				list.add(i, next);
				return true;
			}
			else {
				continue;
			}
		}
		list.addLast(new CounterNode<T>(obj));
		return false;
	}
	
	public String toString() {
		String out = "";
		Iterator<CounterNode<T>> i = list.iterator();
		while(i.hasNext()){
			CounterNode<T> next = i.next();
			out += "\n" + next.whatsTheItem() + "\t" + next.howManyAppearences();// + "\n";
		}
		return out;
	}
	
	public void sorting() {
		
	}
}
