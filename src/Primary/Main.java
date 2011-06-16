package Primary;

import java.util.ResourceBundle;


public class Main {
	
	public static int[] checkOver(String[] args, int[] ceiling) throws Exception {
		if (args.length == 0) {
			throw new Exception("No Files");
		}
		int i,j; //loops counters
		for (i=0; i < args.length; i++){
			if (args[i].contains("(")) {
				ceiling[i] = Integer.parseInt(args[i].substring(args[i].indexOf('(')+1, args[i].indexOf(')')));
				args[i] = args[i].substring(0, args[i].indexOf('('));
			}
			for(j = i+1; j < args.length; j++){
				if (args[i].contains("(")) {
					ceiling[i] = Integer.parseInt(args[i].substring(args[i].indexOf('(')+1, args[i].indexOf(')')));
					args[i] = args[i].substring(0, args[i].indexOf('('));
				}
				if (args[i].equals(args[j])){
					throw new Exception("2 queried files are the same file: " + args[i]);
				}
			}
		}
		return ceiling;
	}
	
	/**
	 * main method of the history system
	 * @param args command line arguments supplied by the user
	 */
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		int[] ceiling = new int[args.length];
		ResourceBundle bundle = ResourceBundle.getBundle("config");
		
		try {
			ceiling = checkOver(args, ceiling);
			//information is parsed and printed here using a HistoryParser instance
		    HistoryParser history = new HistoryParser(args, ceiling);
		    history.printHistoryInformation();  
		}
		//catches any exception thrown
		catch (Exception e) {
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace(); //prints out the issue caught
		}
		long end = System.currentTimeMillis();
		if (bundle.getString("timeToggle").equals("true")) {
			System.out.println("\n("+(end-start)/1000.00+" seconds for completion)");
		}
	}
	
}
