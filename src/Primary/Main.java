package Primary;

import java.util.ResourceBundle;


public class Main {
	
	/**
	 * main method of the history system
	 * @param args command line arguments supplied by the user
	 */
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		ResourceBundle bundle = ResourceBundle.getBundle("config");
		try {
			//information is parsed and printed here using a HistoryParser instance
		    HistoryParser history = new HistoryParser(args);
		    history.printHistoryInformation();  
		}
		//catches any exception thrown
		catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace(); //prints out the issue caught
		}
		long end = System.currentTimeMillis();
		if (bundle.getString("timeToggle").equals("true")) {
			System.out.println("\n(" + (end - start) / 1000.00 + " seconds for completion)");
		}
	}
}
