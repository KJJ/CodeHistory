package primary;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class DiffParser {
	
	private int del, ins; //counts the number of deleted and inserted lines, respectively

	/**
	 * Constructor, initializes the counters
	 */
	public DiffParser() {
		del = 0; //initialize the field
		ins = 0; //initialize the field
	}
	
	/**
	 * takes the diffs and captures their data
	 * @param exec the diff command for a certain file at a certain revision
	 * @throws IOException input exception from data coming from the console
	 */
	public void diffData(Process exec) throws IOException{
		//reads the data coming in, refer to previous comments about sources 
		BufferedReader  stdInput=  new  BufferedReader(new
	              InputStreamReader(exec.getInputStream()));
		
		String s; //input line
		
		while  ((s =  stdInput.readLine())  !=  null)  {
			if (s.startsWith("+") && !s.startsWith("++")) { //desired lines only start with 1 '+' sign
				ins++; //count the addition
			}
			else if (s.startsWith("-") && !s.startsWith("--")) { //desired lines only start with 1 '+' sign
				del++; //count the deletion
			}

		}
	}
	
	/**
	 * takes a svn diff command, gathers impact data and outputs it to the screen
	 * @param exec the diff command
	 * @throws IOException if the input or output is incorrect
	 */
	public void diffOut(Process exec) throws IOException {
		diffData(exec); //gets the data
			System.out.print("Deleted: " + del + "\t Added: " + ins + " \t| "); //prints that data out
				del = 0; //reset field
		ins = 0; //reset field
	}
}
