import java.io.*;
import java.util.*;

//Source of IO reading code: stackoverflow.com

public class HistoryParser {
	
	private ResourceBundle bundle = ResourceBundle.getBundle("config");
	
	private String[] args;
	LinkedList<Double> ratingList = new LinkedList<Double>();
	LinkedList<String> revisionList = new LinkedList<String>();
	LinkedList<String> caseList = new LinkedList<String>();
	LinkedList<String> dateList = new LinkedList<String>();
	LinkedList<String> specialList = new LinkedList<String>();

	
	public HistoryParser(String[] arg) {
		args = arg;
	}
	
	public LinkedList<String>[] collectTargetData(Process exec) throws IOException{
		BufferedReader  stdInput=  new  BufferedReader(new
	              InputStreamReader(exec.getInputStream()));
		
		@SuppressWarnings("unchecked")
		LinkedList<String>[] data = new LinkedList[3];
		String s;
		String[] ss;
		LinkedList<String> rev = new LinkedList<String>();
		LinkedList<String> date = new LinkedList<String>();
		LinkedList<String> nChanged = new LinkedList<String>();
		int count = 0;
		
		while  ((s=  stdInput.readLine())  !=  null)  {
			if (s.startsWith("r")){
				if (count != 0){
					nChanged.addLast(Integer.toString(count));
					count = 0;
				}
				ss = s.split(" ");
				rev.addLast(ss[0].substring(1));
				date.addLast(ss[4]+" "+ss[5]);
			}
			else if (s.startsWith("   M") || s.startsWith("   A") || s.startsWith("   D") || s.startsWith("   R")){
				count++;
			}
		}
		
		nChanged.addLast(Integer.toString(count));
				
		data[0] = rev;
		data[1] = date;
		data[2] = nChanged;
		return data;
	}
	
	@SuppressWarnings("rawtypes")
	public LinkedList<ListNode> getHistoricalRelevancy(LinkedList<LinkedList<String>> RevisionList, LinkedList<LinkedList<String>> DateList, LinkedList<LinkedList<String>> fileList){
		int i, j;
		int presentAtRevision;
		LinkedList<ListNode> results = new LinkedList<ListNode>();
		LinkedList<String> revision = new LinkedList<String>();
		LinkedList<Integer> relevantN = new LinkedList<Integer>();
		ListNode node;
		String tar;
		String tarDate;
		String com;
		String tarChange;
		for (i = 0; i < RevisionList.size(); i++) {
			presentAtRevision = 1;
			LinkedList<String> target = RevisionList.get(i);
			LinkedList<String> targetDate = DateList.get(i);
			LinkedList<String> targetFiles = fileList.get(i);
			Iterator<String> targetIterator = target.iterator();		
			Iterator<String> dateIterator = targetDate.iterator();
			Iterator<String> changeIterator = targetFiles.iterator();
			while (targetIterator.hasNext()) {
				tar = targetIterator.next();
				tarDate = dateIterator.next();
				tarChange = changeIterator.next();
				node = new ListNode(tar, args.length);
				node.setDate(tarDate);
				node.newRelevantFile(args[i]);
				node.setTotalChanges(Integer.parseInt(tarChange));
				if (!revision.contains(tar)) {
					revision.addLast(tar);
					for (j = 0; j<RevisionList.size(); j++) {
						if (j == i) {
							continue;
						}
						LinkedList<String> toCompare = RevisionList.get(j);
						Iterator<String> comparing = toCompare.iterator();
						while (comparing.hasNext()) {
							com = comparing.next();
							if (tar.equals(com)) {
								presentAtRevision++;
								node.newRelevantFile(args[j]);
								break;
							}
						}
					}
					relevantN.addLast(presentAtRevision);
					presentAtRevision = 1;
					results.addLast(node);
				}
			}
		}
		LinkedList<LinkedList> resultList = new LinkedList<LinkedList>();
		resultList.addLast(revision);
		resultList.addLast(relevantN);
		return results;
	}
	
	public LinkedList<ListNode> listSort(LinkedList<ListNode> list){
		LinkedList<ListNode> newList = new LinkedList<ListNode>();
		ListNode current;
		ListNodeComparator LNC = new ListNodeComparator();
		int i, j;
		for (i = 0; i < list.size(); i++){
			current = list.get(i);
			if (newList.peek() == null){
				newList.addFirst(current);
			}
			else {
				j = 0;
				while (j < newList.size()){
					if (LNC.compare(newList.get(j), current) > 0) {
						j++;
					}
					else {
						break;
					}
				}
				newList.add(j, current);
			}
		}
		return newList;
	}
	
	public void printHistoryInformation() throws IOException{
		
		int i;
		LinkedList<LinkedList<String>> dataRepoRevision = new LinkedList<LinkedList<String>>();
		LinkedList<LinkedList<String>> dataRepoDate = new LinkedList<LinkedList<String>>();
		LinkedList<LinkedList<String>> dataRepoFiles = new LinkedList<LinkedList<String>>();


		System.out.println("\nQueried Files:");
		String p = bundle.getString("repo");
		
		for (i = 0; i < args.length; i++){
			System.out.println("\n"+args[i]);
			String n = p+args[i];
			Process exec = Runtime.getRuntime().exec("svn log -v "+n+" -q");
			LinkedList<String>[] info = collectTargetData(exec);
			dataRepoRevision.addLast(info[0]);
			dataRepoDate.addLast(info[1]);
			dataRepoFiles.addLast(info[2]);
		}
		int j;

		LinkedList<ListNode> history = getHistoricalRelevancy(dataRepoRevision, dataRepoDate, dataRepoFiles);
		history = listSort(history);
		System.out.println();
		for (i = 0; i < history.size(); i++){
			String files = "";
			ListNode current = history.get(i);
			Iterator<String> listIt = current.getRelevantFiles().iterator();
			while(listIt.hasNext()){
				files += listIt.next()+"\t";
			}
			String rat = Double.toString(current.getRating());
			if (rat.length() <= 8){
				rat = rat+"\t\t";
			}
			System.out.println(current.getRevision()+"\t"+current.getDate()+"\t"+current.getNumberOfRelevants()+"/"+current.getTotalQuery()+" relevant files\t"+current.getTotalChanges()+"\t"+rat+"\t"+current.getRatingComment()+"\t"+files);
			for (j = 0; j < 50; j++) {
				System.out.print("----------");
			}
			System.out.print("\n");
		}
	}

}
