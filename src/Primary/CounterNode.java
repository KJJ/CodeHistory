package primary;

public class CounterNode<T> {
	
	private T objectOfInterest; //the object we are currently looking at
	private int countOfInterest; //how many time said object appears
	
	/**
	 * Constructor, sets the count for that object to one
	 * @param obj the new object that has occurred
	 */
	public CounterNode(T obj) {
		
		objectOfInterest = obj; //sets the object of interest
		countOfInterest = 1; //shows that it has already occurred once
	}
	
	/**
	 * used to represent finding another one of the current object
	 */
	public void anotherOne(){
		
		countOfInterest++; //increase the counter
	}
	
	/**
	 * 
	 * @return the object being tracked
	 */
	public T whatsTheItem(){
		
		return objectOfInterest;
	}
	
	/**
	 * 
	 * @return how many occurrences of this object have been tracked
	 */
	public int howManyAppearences() {
		
		return countOfInterest;
	}
	
	/**
	 * compares 2 objects to check for equality
	 * @param otherObj the object to be compared to the current object
	 * @return true if equal, false otherwise
	 */
	public boolean compare(T otherObj) {
		
		return this.whatsTheItem().toString().equals(otherObj.toString());
	}
	
	/**
	 * compare one node to another in terms of occurrences
	 * @param node the node to compare with
	 * @return numerical representation of the comparison
	 */
	public int compareOccurrence(CounterNode<T> node) {
		
		if (node.countOfInterest == this.countOfInterest) {
			return 0;  //if the occurrences are equal
		}
		
		else if (node.countOfInterest > this.countOfInterest) {
			return -1; //if this node has less occurrences than the other
		}
		
		else {
			return 1;  //if this node has more occurrences than the other
		}
	}
	
	/**
	 * print out the object along with its occurrence number
	 * 
	 * overrides the inherited toString operation 
	 */
	public String toString() {
		
		return whatsTheItem() + "\t" + howManyAppearences(); //only returns the string, it does not print it out
	}

}
