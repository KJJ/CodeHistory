
public class CounterNode<T> {
	
	private T objectOfInterest;
	private int countOfInterest;
	
	public CounterNode(T obj) {
		objectOfInterest = obj;
		countOfInterest = 1;
	}
	
	public void anotherOne(){
		countOfInterest++;
	}
	
	public T whatsTheItem(){
		return objectOfInterest;
	}
	
	public int howManyAppearences() {
		return countOfInterest;
	}
	
	public boolean compare(T otherObj) {
		return this.whatsTheItem().toString().equals(otherObj.toString());
	}
	
	public int compareOccurrence(CounterNode<T> node) {
		if (node.countOfInterest == this.countOfInterest) {
			return 0;
		}
		else if (node.countOfInterest > this.countOfInterest) {
			return -1;
		}
		else {
			return 1;
		}
	}

}
