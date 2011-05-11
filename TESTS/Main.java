public class Main {
	/**
	 * main method of the history system
	 * @param args command line arguments supplied by the user
	 */
	public static void main(String[] args){
		try {
			int i,j; //loops counters
			/*
			 * these two for loops look to see whether the user accidently used the same file twice
			 * when specifying their parameters. If this did occur, an exception is thrown and the user
			 * notified about what happened.
			 */
			for (i=0; i < args.length; i++){
				for(j = i+1; j < args.length; j++){
					if (args[i].equals(args[j])){
						throw new Exception("2 queried files are the same file");
					}
				}
			}
			//information is parsed and printed here using a HistoryParser instance
		    HistoryParser history = new HistoryParser(args);
		    history.printHistoryInformation();
	
		}
		//catches any exception thrown
		catch (Exception e) {
			e.printStackTrace(); //prints out the issue caught
		}
	}

}
