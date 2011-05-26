
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
		if (this.whatsTheItem().equals(otherObj)){
			return true;
		}
		else {
			return false;
		}
	}

}
