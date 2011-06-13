package Primary;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class DiffParser {
	
	private int del, ins;

	public DiffParser(String[] query) {
		del = 0;
		ins = 0;
	}
	
	public void diffData(Process exec) throws IOException{
		BufferedReader  stdInput=  new  BufferedReader(new
	              InputStreamReader(exec.getInputStream()));
		
		String s;
		
		while  ((s=  stdInput.readLine())  !=  null)  {
			if (s.startsWith("+") && !s.startsWith("++")) {
				ins++;
			}
			else if (s.startsWith("-") && !s.startsWith("--")) {
				del++;
			}

		}
	}
	
	public void diffOut(Process exec) throws IOException {
		diffData(exec);
			System.out.print("Deleted: " + del + "\t Added: " + ins + " \t| ");
				del = 0; 
		ins = 0;
	}
}
