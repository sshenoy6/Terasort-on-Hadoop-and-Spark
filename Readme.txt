Shared Memory:

There are totally 4 source files related to Shared Memory namely – 
SortingExternal.java 
This file contains the logic to sort, merge the data on a single thread. 
Firstly we read the input file line by line and put it into fixed size array. This array is one block of the input. This is sorted using mergesort and the sorted output is then written into a file chunk.

Next we generate 10 such parts of equal size and these parts are then merged using external merge sort. The corresponding entries in all 10 parts are simultaneously compared and written into a common file which is the final sorted output.

MergeSortThread.java
MergeSortFourThreads.java
MergeSortEightThreads.java

These files contain the implementation with threads. We divide the input file into chunks and for sorting we use multi-threading on each block of data. It calls the mergeSort method of the SortingExternal.java file.

To run the program:
1) In the Source Code folder go to Shared Memory and then go to scripts folder.
2) Copy the makefile and shared_memory_sort_run.sh to the Shared Memory folder and run make command to compile the java files.
3) Then run the shared_memory_sort_run.sh to execute the java files.

It is required that the dataset generated have the path /mnt/raid/dataset_new. 
So the dataset needs to be generated after creating raid on the instance. 
To create raid, run the script raid_script.sh
The command to create the dataset is:
./gensort -a 100000000 /mnt/raid/dataset_new

The final sorted output is in the file /mnt/raid/dataset_output

HADOOP:

The files written are:

Sorter.java : Contains code to run the map-reduce on the given input file. Here the input, output format, name of class containing the job, the mapper, combiner and reducer class name etc. are configured.
SorterReducer.java : Contains the reduce method. For sorting the reduce has no function except to accept the map output and write it to the output stream.
SortMapper.java: Contains the map method. Here we create keys of length 10 bytes and pass it to the mapper to do the sorting.
build.xml : The ANT config script to create the jar file.

To run the program:
1) Go to AWS console and create a spot instance. Download and copy the hadoop 2.7.2 tar file to it. Untar it and go to the hadoop-2.7.2/etc/hadoop folder.
Here we need to enter the public DNS of the instance into core-site.xml,slaves,masters,mapred-site.xml,yarn-site.xml. The other values in these files are to be as in the files in config files folder. Consider this to be the master node. The slaves file must have the public DNS of master in the first line and the public DNS of the slaves subsequently.
2) Create a slave node and do the same changes except for slave file. Here slave file must contain the public DNS of the slave alone. Create an AMI of this slave and replicate this 15 more times.
3) Go to the master node and format it.
For this go to the hadoop-2.7.2/bin folder and run the command:
./hdfs namenode -format

4)next create password less ssh to the slaves from master node by running the following command:
 eval `ssh-agent`
 chmod 777 pem_file 
 ssh-add pem_file
5)go to hadoop-2.7.2/sbin folder and run the command:
./start-dfs.sh
Then the datanodes are started.
Next start the resource manager.
./start-yarn.sh
6) Run the jps command to check if all the nodes have started.
7) Generate the file using gensort:
   ./gensort -a 1000000000 /mnt/raid/hgb_dataset
8) Upload the dataset file into hdfs using command:
    hadoop fs -put /mnt/raid/hgb_dataset hdfs:/
9) Run the map-reduce job:
    hadoop jar jar-file hdfs:///hgb_dataset hdfs:///hgb_output
10) After the execution get the file from hdfs to local file system
    hadoop fs -get hdfs:///hgb_output /mnt/raid
11) Run valsort on the parts which are in output folder to check that it is sorted.

SPARK:
1) To create the spark cluster go to ec2 folder and run the script in the folder

	Spark/spark_run_script_single.sh  for a single master slave cluster
	Spark/spark_run_script_cluster.sh for a multi node cluster

2) The spark code needs to be run on the scala interpreter prompt. 
3) To go to the shell, Download Spark 1.6 pre-built for Hadoop 2.6.
4) Go to the bin folder and run the command ./spark-shell
5) In the shell type the code given in sparkcode.txt file in Spark folder. 
6) We create a substring of length 10 byte and then call sortByKey function. We can pass number of parts here. Then we provide the path on hdfs where the output will be written.

Ensure that the data is present in hdfs in the path specified in the code.

The output parts are created on hdfs and need to be copied to the local file system so that it can be validated.
