

import java.io.File;
import java.io.IOException;

public class MergeSortFourThreads {

	/**
	 * @param args
	 */
	public static final int MAX_PARTS = 10;
	//public static List<String> fileChunks = new ArrayList<String>();
	public static long fileSize = 0;
	public String[] rows;
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		MergeSortThread obj = new MergeSortThread();
		SortingExternal obj2 = new SortingExternal();
		fileSize = new File("/mnt/raid/dataset_new").length();//find the length of the dataset file
		obj.divideFileAndSort("/mnt/raid/dataset_new",4);//call the function to divide the dataset into chunks and sort them
		System.out.println("Sorting total time in ms-->"+obj.duration);
		
		obj2.mergeDividedSortedFiles(MergeSortThread.fileChunks,fileSize);//merge the sorted, divided chunks of files
		
		
		//delete the temporary file chunks when the program exits
		for(int i=0;i<MergeSortThread.fileChunks.size();i++){
			new File(MergeSortThread.fileChunks.get(i)).deleteOnExit();
		}

	}

}
