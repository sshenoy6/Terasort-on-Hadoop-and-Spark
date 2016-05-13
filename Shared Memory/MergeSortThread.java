

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class MergeSortThread implements Runnable {
	
	
	public static final int MAX_PARTS = 10;
	public static List<String> fileChunks = new ArrayList<String>();
	public static long fileSize = 0;
	public String[] rows;
	public long duration = 0;
	
	public MergeSortThread(){
		
	}
	public MergeSortThread(String[] rows){
		this.rows = rows;
	}
	public void run(){
		SortingExternal.mergeSort(rows);
	}
	public void divideFileAndSort(String fileName,int numberOfThreads) throws IOException, InterruptedException {
		File fileToBeDivided = new File(fileName);
		RandomAccessFile randomAccessFD = null;
		BufferedWriter bw1 = null;
		
		try {
			randomAccessFD = new RandomAccessFile(fileName, "rw"); // open the file (dataset) for reading the data to be sorted
																	
			fileSize = fileToBeDivided.length(); // calculate the number of bytes in the file
			int chunkSize = (int) fileSize / MAX_PARTS; // calculate each chunk size
			
			int currentChunkSize = 0;
			
			int currentChunkBeingRead = 0;
			while (currentChunkBeingRead < MAX_PARTS) { // repeatedly read chunks of data of fixed size
				currentChunkBeingRead++;
				currentChunkSize = 0;//individual chunk file size which cannot exceed 1000000 bytes or 1 MB
				rows = new String[chunkSize/100];
				
				int i = 0;
				bw1 = new BufferedWriter(new FileWriter("/mnt/raid/sorted_chunk"
								+ currentChunkBeingRead+".txt")); //open buffered writer for temporary files
				fileChunks.add("/mnt/raid/sorted_chunk"+ currentChunkBeingRead+".txt"); // create a list of the temporary files to merge sorted temporary files
				
				while (currentChunkSize < chunkSize && i<(chunkSize/100)) { // read up to chunkSize
					
					rows[i] = randomAccessFD.readLine();// read each line in the file
					
					currentChunkSize += rows[i].length() + 2;// calculate the number of bytes read so far -> explicitly add 2 characters to account for newline character
					
					i++;
				}
				//Implement multi threading for the sorting of chunks
				long start = System.currentTimeMillis();
				
				for (int j=0;j<numberOfThreads;j++){
					Thread t1 = new Thread(new MergeSortThread(rows));
					t1.start(); //in the run method - apply merge sort on the chunk of strings read into array from the dataset generated using gensort. 
					t1.join();
				}
				
				duration+=System.currentTimeMillis()-start;
				for (i=0;i<rows.length;i++) {
					
					bw1.write(rows[i]+"\n");//write the sorted chunks to temporary files
					
				}
				
				bw1.close();//close the buffered writer
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				randomAccessFD.close(); //close the file handle
				bw1.close();//close the buffered writer
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	public static void main(String []args) throws IOException, InterruptedException{
		MergeSortThread obj = new MergeSortThread();
		SortingExternal obj2 = new SortingExternal();
		fileSize = new File("/mnt/raid/dataset_new").length();//find the length of the dataset file
		obj.divideFileAndSort("/mnt/raid/dataset_new",2);//call the function to divide the dataset into chunks and sort them

		System.out.println("Sorting total time in ms-->"+obj.duration);
		obj2.mergeDividedSortedFiles(fileChunks,fileSize);//merge the sorted, divided chunks of files
		
		
		//delete the temporary file chunks when the program exits
		for(int i=0;i<fileChunks.size();i++){
			new File(fileChunks.get(i)).deleteOnExit();
		}
	}
}
