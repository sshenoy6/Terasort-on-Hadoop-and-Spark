

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class SortingExternal{

	public static final int MAX_PARTS = 10;
	public static List<String> fileChunks = new ArrayList<String>();
	public static long fileSize = 0;
	/**
	 * Method to divide the dataset into multiple chunks and sort each file
	 * @param fileName
	 * @throws IOException
	 */
	public void divideFileAndSort(String fileName) throws IOException, InterruptedException {
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
				String[] currentRow = new String[chunkSize/100];
				
				int i = 0;
				bw1 = new BufferedWriter(new FileWriter("/mnt/raid/sorted_chunk"
								+ currentChunkBeingRead+".txt")); //open buffered writer for temporary files
				fileChunks.add("/mnt/raid/sorted_chunk"+ currentChunkBeingRead+".txt"); // create a list of the temporary files to merge sorted temporary files
				
				while (currentChunkSize < chunkSize && i<(chunkSize/100)) { // read up to chunkSize
					
					currentRow[i] = randomAccessFD.readLine();// read each line in the file
					
					currentChunkSize += currentRow[i].length() + 2;// calculate the number of bytes read so far -> explicitly add 2 characters to account for newline character
					
					i++;
				}
				
				mergeSort(currentRow); 
				
				for (i=0;i<currentRow.length;i++) {
					bw1.write(currentRow[i]+"\n");//write the sorted chunks to temporary files
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

	/**
	 * Implementation of merge sort to sort the chunks of files
	 * @param keys
	 */
	public static void mergeSort(String[] keys) {
		if (keys.length >= 2) {
			
			String[] left = new String[keys.length / 2]; //divide the array into 2 parts

			String[] right = new String[keys.length - keys.length / 2];

			for (int i = 0; i < left.length; i++) { // copy data into left array
				left[i] = keys[i];
			}

			for (int i = 0; i < right.length; i++) { //copy data into right array
				right[i] = keys[i + keys.length / 2];
			}
			
			mergeSort(left); // sort the left array
			mergeSort(right);//sort the right array
			merge(keys, left, right); //merge the sorted arrays
		}
	}

	/**
	 * Merge the partially sorted data within the files
	 * @param keys
	 * @param left
	 * @param right
	 */
	public static void merge(String[] keys, String[] left, String[] right) {
		int a = 0;
		int b = 0;
		for (int i = 0; i <keys.length; i++) {
			if (b >= right.length
					|| (a < left.length && left[a].substring(0, 10)
							.compareTo(right[b].substring(0, 10)) < 0)) { //compare the first 10 bytes within each entry of left and right subdivisions
				keys[i] = left[a]; //copy the smaller element as per ASCII value into merged array
				a++;
			} else {
				keys[i] = right[b];//copy the smaller element as per ASCII value into merged array
				b++;
			}
		}
	}
	/**
	 * Merge the sorted chunks of files into a single file
	 * @param files
	 * @param fileSize
	 * @throws IOException
	 */
	public void mergeDividedSortedFiles(List<String> files,long fileSize) throws IOException{
		System.out.println("Merging divisions The file size is "+files.size());
		BufferedReader[] reader = new BufferedReader[files.size()];
		String[] data = new String[files.size()];
		for(int i=0;i<=files.size()-1;i++){ //associate readers to each of the temporary files
			reader[i] = new BufferedReader(new FileReader(
					"/mnt/raid/sorted_chunk"
							+(i+1)+".txt"));
		}
		System.out.println("Length of readers is "+reader.length);
		for(int i=0;i<files.size();i++){ // copy the data from each sorted chunk into an array
			data[i] = reader[i].readLine();
		}
		System.out.println("The size of data array is "+data.length);
		BufferedWriter writer = new BufferedWriter(new FileWriter("/mnt/raid/dataset_output",true));// associate writer to output file
		for (int j=0;j<fileSize/100;j++){
			String init = data[0];
			int minFile = 0;
			for(int k=0;k<files.size();k++){
				if(data[k]!=null && init!=null && init.substring(0, 10).compareTo(data[k].substring(0, 10)) > 0){ //find the least value among all the chunks and write it to the output file
					init = data[k];
					minFile = k;
				}
			}
			if(init!=null)
				writer.write(init+"\n"); //include newline character explicitly while writing
			data[minFile] = reader[minFile].readLine();
		}
		for(int l=0;l<files.size();l++){
			reader[l].close(); //close all the readers associated with the file chunks
		}
		writer.close();
	}
	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		SortingExternal obj = new SortingExternal();
		long start = System.currentTimeMillis();
		obj.divideFileAndSort("/mnt/raid/dataset_new");//call the function to divide the dataset into chunks and sort them
		long fileSize = new File("/mnt/raid/dataset_new").length();//find the length of the dataset file
		obj.mergeDividedSortedFiles(fileChunks,fileSize);//merge the sorted, divided chunks of files
		long duration = System.currentTimeMillis() - start; //calculate time taken for the entire sort to run
		System.out.println("The time taken is "+duration+" ms");
		//delete the temporary file chunks when the program exits
		for(int i=0;i<fileChunks.size();i++){
			new File(fileChunks.get(i)).deleteOnExit();
		}
	}
}
